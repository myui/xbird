/*
 * @(#)$Id: SortedStaticHash.java 2262 2007-07-10 21:07:50Z yui $
 *
 * Copyright 2006-2008 Makoto YUI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Makoto YUI - initial implementation
 */
package xbird.storage.index;

import java.io.*;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.storage.DbException;
import xbird.util.collections.longs.LongHash;
import xbird.util.collections.longs.ObservableLongLRUMap;
import xbird.util.collections.longs.LongHash.BucketEntry;
import xbird.util.collections.longs.LongHash.Cleaner;
import xbird.util.io.FastMultiByteArrayOutputStream;
import xbird.util.lang.ArrayUtils;
import xbird.util.math.Primes;

/**
 * A variant of Static Hash.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class SortedStaticHash extends Paged {
    private static final Log LOG = LogFactory.getLog(SortedStaticHash.class);

    public static final int DEFAULT_BUCKETS = 1024;
    /** If page size is 4k, 2m (4k * 512) cache */
    private static final int DEFAULT_IN_MEMORY_BUCKETS = 512;

    private static final int VAR_KEY_LENGTH = -1;
    private static final byte USED_BUCKET = 1;
    private static final Value[] EMPTY_VALUES = new Value[0];

    @Deprecated
    private static final int INSERTIONSORT_THRESHOLD = 7;

    private final HashFileHeader _fileHeader;
    private final LongHash<HashBucket> _cache;

    public SortedStaticHash(File file) {
        this(file, DEFAULT_PAGESIZE, DEFAULT_BUCKETS, VAR_KEY_LENGTH, DEFAULT_IN_MEMORY_BUCKETS);
    }

    public SortedStaticHash(File file, int buckets) {
        this(file, DEFAULT_PAGESIZE, buckets, VAR_KEY_LENGTH, DEFAULT_IN_MEMORY_BUCKETS);
    }

    public SortedStaticHash(File file, int pageSize, int buckets) {
        this(file, pageSize, buckets, VAR_KEY_LENGTH, DEFAULT_IN_MEMORY_BUCKETS);
    }

    public SortedStaticHash(File file, int pageSize, int buckets, int keylen, int caches) {
        super(file, pageSize);
        final HashFileHeader fh = (HashFileHeader) getFileHeader();
        fh.setBucketSize(Primes.findLeastPrimeNumber(buckets));
        fh.keyLength = keylen;
        this._fileHeader = fh;
        final Synchronizer sync = new Synchronizer();
        final int purgeSize = Math.max(caches >>> 5, 16); // perge 1/32 pages or 16 pages at a time
        this._cache = new ObservableLongLRUMap<HashBucket>(caches, purgeSize, sync);
    }

    private static final class Synchronizer implements Cleaner<HashBucket> {

        Synchronizer() {}

        public void cleanup(long bucketPageNum, HashBucket bucket) {
            try {
                bucket.write();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            } catch (DbException e) {
                throw new IllegalStateException(e);
            }
        }

    }

    @Override
    public boolean create(boolean close) throws DbException {
        //if(exists())
        _fileHeader.setTotalPageCount(_fileHeader.bucketSize);
        return super.create(close);
    }

    protected FileHeader createFileHeader(int pageSize) {
        return new HashFileHeader(pageSize);
    }

    protected PageHeader createPageHeader() {
        return new HashPageHeader();
    }

    public Value findValue(Key key) throws DbException {
        if(key == null || key.getLength() == 0) {
            return null;
        }
        try {
            checkOpened();
            long pageNum = keyHash(key);
            HashBucket bucket = loadRecordedBucket(pageNum);
            if(bucket != null) {
                return bucket.findValue(key);
            }
        } catch (IOException e) {
            throw new DbException(e);
        }
        return null;
    }

    public Value addValue(Key key, Value value) throws DbException {
        if(key == null) {
            throw new IllegalArgumentException("Null key is not accepted");
        }
        int keylen = key.getLength(), pagesize = _fileHeader.getPageSize();
        if(keylen > pagesize) {
            throw new IllegalArgumentException("key size '" + keylen + "' exceeds page size '"
                    + pagesize + '\'');
        }
        if(value == null) {
            throw new IllegalArgumentException("Null value is not accepted");
        }
        try {
            checkOpened();
            HashBucket bucket = seekInsertionBucket(key);
            return bucket.addValue(key, value);
        } catch (IOException e) {
            throw new DbException(e);
        }
    }

    private HashBucket seekInsertionBucket(Key key) throws DbException {
        long pageNum = keyHash(key);
        HashBucket bucket;
        synchronized(_cache) {
            bucket = _cache.get(pageNum);
            if(bucket == null) {
                Page p = getPage(pageNum);
                bucket = new HashBucket(p);
                PageHeader ph = p.getPageHeader();
                if(ph.getStatus() == USED_BUCKET) {
                    try {
                        bucket.read();
                    } catch (IOException e) {
                        throw new DbException(e);
                    }
                } else {
                    ph.setStatus(USED_BUCKET);
                }
                _cache.put(pageNum, bucket);
            }
        }
        return bucket;
    }

    private final long keyHash(Key key) {
        final int keyhash = key.hashCode();
        final long pageNum = Math.abs(keyhash) % _fileHeader.getBucketSize();
        return pageNum;
    }

    private synchronized HashBucket loadRecordedBucket(long pageNum) throws IOException,
            DbException {
        HashBucket bucket;
        synchronized(_cache) {
            bucket = _cache.get(pageNum);
            if(bucket == null) {
                Page p = getPage(pageNum);
                PageHeader ph = p.getPageHeader();
                if(ph.getStatus() == UNUSED) {
                    return null;
                }
                bucket = new HashBucket(p);
                bucket.read();
                _cache.put(pageNum, bucket);
            }
        }
        return bucket;
    }

    public Value removeValue(Key key) throws DbException {
        if(key == null || key.getLength() == 0) {
            return null;
        }
        try {
            checkOpened();
            long pageNum = keyHash(key);
            HashBucket bucket = loadRecordedBucket(pageNum);
            if(bucket != null) {
                return bucket.removeValue(key);
            }
        } catch (IOException e) {
            throw new DbException(e);
        }
        return null;
    }

    public void flush(boolean purge, boolean clear) throws DbException {
        if(purge) {
            for(BucketEntry<HashBucket> e : _cache) {
                HashBucket node = e.getValue();
                try {
                    node.write();
                } catch (IOException ioe) {
                    throw new DbException(ioe);
                }
            }
        }
        if(clear) {
            _cache.clear();
        }
        super.flush();
    }

    private final class HashBucket {

        private final Page page;
        private final HashPageHeader ph;

        private Key[] keys = Key.EMPTY_KEYS;
        private Value[] values = EMPTY_VALUES;

        private boolean loaded = false;
        private boolean dirty = false;
        private int currentDataLen = -1;

        @Deprecated
        private boolean sorted = false; // TODO not used

        public HashBucket(Page page) {
            this.page = page;
            this.ph = (HashPageHeader) page.getPageHeader();
        }

        private synchronized Value addValue(Key key, Value value) throws IOException, DbException {
            int idx = searchKey(keys, key);
            if(idx >= 0) {//found
                Value old = values[idx];
                values[idx] = value;
                return old;
            } else {
                idx = -(idx + 1);
                long nextCollision = ph.getNextCollision();
                if(nextCollision != NO_PAGE && idx == keys.length) {
                    HashBucket bucket = loadRecordedBucket(nextCollision);
                    if(bucket == null) {
                        throw new IllegalStateException("Required page is not in use: "
                                + nextCollision);
                    }
                    return bucket.addValue(key, value);
                }

                set(ArrayUtils.<Key> insert(keys, idx, key), ArrayUtils.<Value> insert(values, idx, value));
                incrDataLength(key, value);

                if(needSplit()) {
                    split();
                }
                return null;
            }
        }

        private boolean needSplit() {
            final int datalen = calculateDataLength();
            final int worksize = _fileHeader.getWorkSize();
            return datalen > worksize;
        }

        private synchronized void split() throws IOException, DbException {
            // chain hash bucket
            int pivot = keys.length / 2;

            Key[] leftkeys = new Key[pivot];
            Value[] leftValues = new Value[pivot];
            System.arraycopy(keys, 0, leftkeys, 0, leftkeys.length);
            System.arraycopy(values, 0, leftValues, 0, leftValues.length);

            Key[] rightKeys = new Key[keys.length - pivot];
            Value[] rightValues = new Value[keys.length - pivot];
            System.arraycopy(keys, leftkeys.length, rightKeys, 0, rightKeys.length);
            System.arraycopy(values, leftValues.length, rightValues, 0, rightValues.length);

            set(leftkeys, leftValues);
            calculateDataLength();

            Page newPage = getFreePage();
            long np = newPage.getPageNum();
            if(np < _fileHeader.bucketSize) {
                throw new IllegalStateException("Illegal overflow page number: " + np);
            }
            long origNp = ph.getNextCollision();
            ph.setNextCollision(np);

            if(LOG.isDebugEnabled()) {
                if(origNp == NO_PAGE) {
                    LOG.debug("Bucket overflow. Chain bucket page " + page.getPageNum() + " -> "
                            + np);
                } else {
                    LOG.debug("Bucket overflow. Chain bucket page " + page.getPageNum() + " -> "
                            + np + " -> " + origNp);
                }
            }

            HashBucket rBucket = new HashBucket(newPage);
            rBucket.set(rightKeys, rightValues);
            rBucket.calculateDataLength();

            rBucket.ph.setStatus(USED_BUCKET);
            rBucket.ph.setNextCollision(origNp);

            _cache.put(np, rBucket);
        }

        public synchronized Value findValue(Key key) throws IOException, DbException {
            return this.findValue(key, 1);
        }

        private Value findValue(Key key, int depth) throws IOException, DbException {
            int idx = searchKey(keys, key);
            if(idx >= 0) {//found
                Value found = values[idx];
                if(LOG.isDebugEnabled() && depth > 1) {
                    LOG.debug("found a value in " + depth + "th chain");
                }
                return found;
            } else {// try to look up next collision page                
                idx = -(idx + 1);
                long np = ph.getNextCollision();
                if(np != NO_PAGE && idx == keys.length) {
                    HashBucket bucket = loadRecordedBucket(np);
                    if(bucket == null) {
                        throw new IllegalStateException("Required page is not in use: " + np);
                    }
                    if(LOG.isDebugEnabled() && depth == 1) {
                        LOG.debug("found chain for bucket (" + page.getPageNum() + ')');
                    }
                    return bucket.findValue(key, depth + 1);
                }
                if(LOG.isDebugEnabled() && depth > 1) {
                    LOG.debug("key not found, while tried to lookup " + depth + " chains");
                }
                return null;// not found
            }
        }

        public synchronized Value removeValue(Key key) throws IOException, DbException {
            return this.removeValue(key, 1);
        }

        private Value removeValue(Key key, int depth) throws IOException, DbException {
            int idx = searchKey(keys, key);
            if(idx >= 0) {//found
                Value old = values[idx];
                set(ArrayUtils.remove(keys, idx), ArrayUtils.remove(values, idx));
                decrDataLength(key, old);
                return old;
            } else {// try to look up next collision page                
                idx = -(idx + 1);
                long np = ph.getNextCollision();
                if(np != NO_PAGE && idx == keys.length) {
                    HashBucket bucket = loadRecordedBucket(np);
                    if(bucket == null) {
                        throw new IllegalStateException("Required page is not in use: " + np);
                    }
                    return bucket.removeValue(key, depth + 1);
                }
                return null;// not found
            }
        }

        private void set(Key[] keys, Value[] values) {
            this.keys = keys;
            this.values = values;
            this.ph.setEntryCount(keys.length);
            this.dirty = true;
        }

        /**
         * Reads node only if it is not loaded yet
         * @throws DbException 
         */
        protected synchronized void read() throws IOException, DbException {
            if(!this.loaded) {
                Value v = readValue(page);
                DataInputStream is = new DataInputStream(v.getInputStream());

                int entries = ph.getEntryCount();
                int keySize = _fileHeader.keyLength;
                boolean varKey = (keySize == VAR_KEY_LENGTH);

                keys = new Key[entries];
                values = new Value[entries];
                for(int i = 0; i < entries; i++) {
                    // Read in the keys
                    if(varKey) {
                        keySize = is.readInt();
                    }
                    byte[] kb = new byte[keySize];
                    is.read(kb);
                    keys[i] = new Key(kb);
                    // Read in the values
                    int valSize = is.readInt();
                    byte[] vb = new byte[valSize];
                    is.read(vb);
                    values[i] = new Value(vb);
                }

                this.loaded = true;
            }
        }

        protected synchronized void write() throws IOException, DbException {
            if(!dirty) {
                return;
            }
            FastMultiByteArrayOutputStream bos = new FastMultiByteArrayOutputStream(_fileHeader.getWorkSize());
            DataOutputStream os = new DataOutputStream(bos);
            boolean varKey = !isFixedKey();
            for(int i = 0; i < keys.length; i++) {
                // Write out the keys
                if(varKey) {
                    os.writeInt(keys[i].getLength());
                }
                keys[i].writeTo(os);
                // Write out the Values
                os.writeInt(values[i].getLength());
                values[i].writeTo(os);
            }
            writeValue(page, new Value(bos.toByteArray()));
            this.dirty = false;
        }

        private boolean isFixedKey() {
            return _fileHeader.keyLength != VAR_KEY_LENGTH;
        }

        private void incrDataLength(Key key, Value value) {
            int datalen = currentDataLen;
            if(datalen == -1) {
                datalen = calculateDataLength();
            }
            if(!isFixedKey()) {
                datalen += 4;
            }
            datalen += key.getLength();
            datalen += (value.getLength() + 4);
            this.currentDataLen = datalen;
        }

        private void decrDataLength(Key key, Value value) {
            int datalen = currentDataLen;
            if(!isFixedKey()) {
                datalen -= 4;
            }
            datalen -= key.getLength();
            datalen -= (value.getLength() + 4);
            this.currentDataLen = datalen;
        }

        private int calculateDataLength() {
            if(currentDataLen > 0) {
                return currentDataLen;
            }
            final int keyslen = keys.length;
            int datalen = 4 * keyslen; // for each length of values            
            if(!isFixedKey()) {
                datalen += (4 * keyslen);
            }
            for(int i = 0; i < keyslen; i++) {
                datalen += keys[i].getLength();
                datalen += values[i].getLength();
            }
            this.currentDataLen = datalen;
            return datalen;
        }

        @Deprecated
        private void sort() {
            assert (!sorted);
            Key[] destKeys = keys.clone();
            Value[] destValues = values.clone();
            mergeSort(keys, destKeys, values, destValues, 0, destKeys.length);
        }

    }

    private static int searchKey(final Key[] keys, final Key key) {
        final int keylen = keys.length;
        if(keylen == 0) {
            return -1;
        }
        final int lastIdx = keylen - 1;
        final Key rightmost = keys[lastIdx];
        final int cmp = key.compareTo(rightmost);
        if(cmp > 0) {//tiny optimization
            return -(keylen + 1);
        } else if(cmp == 0) {
            return lastIdx;
        } else {
            return ArrayUtils.binarySearch(keys, 0, lastIdx, key);
        }
    }

    @Deprecated
    private static final void mergeSort(final Key[] srcKeys, final Key[] destKeys, final Value[] srcValues, final Value[] destValues, final int low, final int high) {
        final int length = high;

        // Insertion sort on smallest arrays
        if(length < INSERTIONSORT_THRESHOLD) {
            for(int i = low; i < high; i++) {
                for(int j = i; j > low && destKeys[j - 1].compareTo(destKeys[j]) > 0; j--) {
                    final int k = j - 1;
                    swap(destKeys, j, k);
                    swap(destValues, j, k);
                }
            }
            return;
        }

        // Recursively sort halves of dest into src
        final int destLow = low;
        final int destHigh = high;
        final int mid = (low + high) >>> 1;
        mergeSort(destKeys, srcKeys, destValues, srcValues, low, mid);
        mergeSort(destKeys, srcKeys, destValues, srcValues, mid, high);

        // If list is already sorted, just copy from src to dest.  This is an
        // optimization that results in faster sorts for nearly ordered lists.
        if(srcKeys[mid - 1].compareTo(srcKeys[mid]) <= 0) {
            System.arraycopy(srcKeys, low, destKeys, destLow, length);
            System.arraycopy(srcValues, low, destValues, destLow, length);
            return;
        }

        // Merge sorted halves (now in src) into dest
        for(int i = destLow, p = low, q = mid; i < destHigh; i++) {
            if(q >= high || p < mid && srcKeys[p].compareTo(srcKeys[q]) <= 0) {
                destKeys[i] = srcKeys[p];
                destValues[i] = srcValues[p];
                ++p;
            } else {
                destKeys[i] = srcKeys[q];
                destValues[i] = srcValues[q];
                ++q;
            }
        }
    }

    /**
     * Swaps x[a] with x[b].
     */
    @Deprecated
    private static final void swap(final Value[] x, final int a, final int b) {
        Value t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    private final class HashFileHeader extends FileHeader {

        private int bucketSize;
        private int keyLength = VAR_KEY_LENGTH;

        public HashFileHeader(int pageSize) {
            super(pageSize);
        }

        @Override
        protected synchronized void read(RandomAccessFile raf) throws IOException {
            super.read(raf);
            this.bucketSize = raf.readInt();
            this.keyLength = raf.readInt();
        }

        @Override
        protected synchronized void write(RandomAccessFile raf) throws IOException {
            super.write(raf);
            raf.writeInt(bucketSize);
            raf.writeInt(keyLength);
        }

        public void setBucketSize(int bucketSize) {
            this.bucketSize = bucketSize;
        }

        public int getBucketSize() {
            return bucketSize;
        }

        public int getKeyLength() {
            return keyLength;
        }

        public void setKeyLength(int keyLength) {
            this.keyLength = keyLength;
        }
    }

    private final class HashPageHeader extends PageHeader {

        private int entries = 0;
        private long nextCollision = NO_PAGE;

        public HashPageHeader() {}

        @Override
        public synchronized void read(ByteBuffer buf) {
            super.read(buf);
            if(getStatus() == UNUSED) {
                return;
            }
            this.entries = buf.getInt();
            this.nextCollision = buf.getLong();
        }

        @Override
        public synchronized void write(ByteBuffer buf) {
            super.write(buf);
            buf.putInt(entries);
            buf.putLong(nextCollision);
        }

        /** The next page for a Record collision (if any) */
        public void setNextCollision(long nextCollision) {
            this.nextCollision = nextCollision;
        }

        /** The next page for a Record collision (if any) */
        public long getNextCollision() {
            return nextCollision;
        }

        public void setEntryCount(int entries) {
            this.entries = entries;
        }

        public int getEntryCount() {
            return entries;
        }
    }
}
