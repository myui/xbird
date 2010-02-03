/*
 * @(#)$Id: ExtOpenHash.java 3619 2008-03-26 07:23:03Z yui $
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
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xbird.storage.DbException;
import xbird.util.ArrayUtils;
import xbird.util.ObjectUtils;
import xbird.util.collections.SoftHashMap;

/**
 * An variant of Extendable Hash.
 * <DIV lang="en">
 * Hashing is done by combination of Open-Addressing method and Chained-Hash method.
 * Open-addressing is used only at the occuation of first collision. 
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class ExtOpenHash extends Paged {

    private static final Log LOG = LogFactory.getLog(ExtOpenHash.class);

    private static final byte USED_BUCKET = 1;

    private final HashFileHeader _fileHeader;

    private final Map<Long, HashBucket> _cache = new SoftHashMap<Long, HashBucket>(128);

    private final int _bucketsSize;

    public ExtOpenHash(File file, int buckets) {
        super(file, buckets);
        this._fileHeader = (HashFileHeader) getFileHeader();
        if(buckets < 1) {
            throw new IllegalArgumentException("At least one bucket, but was : " + buckets);
        }
        this._bucketsSize = buckets;
    }

    @Override
    public FileHeader createFileHeader() {
        throw new IllegalStateException();
    }

    @Override
    public FileHeader createFileHeader(long pageCount) {
        return new HashFileHeader(pageCount);
    }

    @Override
    public PageHeader createPageHeader() {
        return new HashPageHeader();
    }

    public Value findValue(Key key) throws DbException {
        if(key == null || key.getLength() == 0) {
            return null;
        }
        try {
            ensureResourceOpen();
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
            ensureResourceOpen();
            HashBucket bucket = seekInsertionBucket(key);
            return bucket.addValue(key, value);
        } catch (IOException e) {
            throw new DbException(e);
        }
    }

    private HashBucket seekInsertionBucket(Key key) throws IOException {
        long pageNum = keyHash(key);
        HashBucket bucket;
        synchronized(_cache) {
            bucket = _cache.get(pageNum);
            if(bucket == null) {
                Page p = getPage(pageNum);
                bucket = new HashBucket(p);
                PageHeader ph = p.getPageHeader();
                if(ph.getStatus() == USED_BUCKET) {
                    bucket.read();
                } else {
                    ph.setStatus(USED_BUCKET);
                }
                _cache.put(pageNum, bucket);
            }
        }
        return bucket;
    }

    private final long keyHash(Key key) {
        int keyhash = key.hashCode();
        long pageNum = Math.abs(keyhash) % _bucketsSize;
        return pageNum;
    }

    private synchronized HashBucket loadRecordedBucket(long pageNum) throws IOException {
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
            ensureResourceOpen();
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

    private final class KeyIterator implements Iterator<Key> {

        private int curBucketNum = 0;

        private HashBucket curBucket = null;

        private int index = 0;

        public KeyIterator() {
            super();
        }

        public boolean hasNext() {
            return false;
        }

        public Key next() {
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private final class HashBucket {

        private final Page page;

        private final HashPageHeader ph;

        private boolean loaded = false;

        private Key[] keys = Key.EMPTY_KEYS;

        private Value[] values = Value.EMPTY_VALUES;

        public HashBucket(Page page) {
            this.page = page;
            this.ph = (HashPageHeader) page.getPageHeader();
        }

        public synchronized Value addValue(Key key, Value value) throws IOException {
            return this.addValue(key, value, 1);
        }

        private Value addValue(Key key, Value value, int nth) throws IOException {
            int idx = Arrays.binarySearch(keys, key);
            if(idx >= 0) {//found
                Value old = values[idx];
                values[idx] = value;
                write();
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
                    return bucket.addValue(key, value, nth + 1);
                }
                boolean split = needSplit(key, value);
                if(split) {
                    BitSet openBuckets = _fileHeader._openBuckets;
                    OPEN_ADDR: if(nextCollision == NO_PAGE && !openBuckets.isEmpty()) {
                        long openAddr = ph.getNextOpenAddress();
                        boolean newOpen = false;
                        if(openAddr == NO_PAGE) {
                            newOpen = true;
                            openAddr = openBuckets.nextClearBit(0);
                        } else {
                            boolean isOpen = openBuckets.get((int) openAddr);
                            if(!isOpen) {
                                break OPEN_ADDR;
                            }
                        }

                        if(LOG.isDebugEnabled()) {
                            LOG.debug("Use an open bucket(" + openAddr
                                    + ") from the overflow bucket(" + page.getPageNum() + ')');
                        }

                        Page openPage = getPage(openAddr);
                        HashBucket bucket = _cache.get(openPage);
                        if(bucket == null) {
                            bucket = new HashBucket(openPage);
                            if(bucket.ph.getStatus() == USED_BUCKET) {
                                bucket.read();
                            }
                        }
                        if(bucket.enoughSpace(key, value)) {
                            if(newOpen) {
                                ph.setNextOpenAddress(openAddr);
                                write();
                            }

                            int j = Arrays.binarySearch(bucket.keys, key);
                            if(j < 0) {
                                j = -(j + 1);
                            }
                            bucket.set(ArrayUtils.<Key> insert(bucket.keys, j, key), ArrayUtils.<Value> insert(bucket.values, j, value));
                            bucket.ph.setStatus(USED_BUCKET);
                            assert (bucket.ph.nextCollision == NO_PAGE) : bucket.ph.nextCollision;

                            _cache.put(openAddr, bucket);
                            bucket.write();
                            return null;
                        }
                        openBuckets.set((int) openAddr, false); // set bucket in use, fall through
                    }
                    openBuckets.set((int) page.getPageNum(), false); // set bucket in use, fall through
                    _fileHeader.setDirty(true);
                    set(ArrayUtils.<Key> insert(keys, idx, key), ArrayUtils.<Value> insert(values, idx, value));
                    split();
                } else {
                    set(ArrayUtils.<Key> insert(keys, idx, key), ArrayUtils.<Value> insert(values, idx, value));
                    write();
                }
                return null;
            }
        }

        private synchronized void split() throws IOException {
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

            Page newPage = getFreePage();
            long np = newPage.getPageNum();
            long origNp = ph.getNextCollision();
            ph.setNextCollision(np);
            write();

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

            rBucket.ph.setStatus(USED_BUCKET);
            rBucket.ph.setNextCollision(origNp);

            _cache.put(np, rBucket);
            rBucket.write();
        }

        private boolean needSplit(Key key, Value value) {
            int datalen = ph.getDataLength() + key.getLength() + value.getLength() + 4;
            int worksize = _fileHeader.getWorkSize();
            return datalen > worksize;
        }

        /** Is less than bucket 1/3 is full */
        private boolean enoughSpace(Key key, Value value) {
            int datalen = ph.getDataLength() + key.getLength() + value.getLength() + 4;
            int worksize = _fileHeader.getWorkSize();
            return datalen < (worksize / 3);
        }

        public synchronized Value findValue(Key key) throws IOException {
            return this.findValue(key, 1);
        }

        private Value findValue(Key key, int nth) throws IOException {
            int idx = Arrays.binarySearch(keys, key);
            if(idx >= 0) {//found
                Value found = values[idx];
                if(LOG.isDebugEnabled() && nth > 1) {
                    LOG.debug("found a value in " + nth + "th chain");
                }
                return found;
            } else {// try to look up next collision page                
                idx = -(idx + 1);
                assert (idx <= keys.length) : "idx: " + idx + ", keys: " + keys.length;
                if(nth == 1) {
                    long openAddr = ph.getNextOpenAddress();
                    if(openAddr != NO_PAGE) {
                        HashBucket bucket = loadRecordedBucket(openAddr);
                        if(bucket == null) {
                            throw new IllegalStateException("Required page is not in use: "
                                    + openAddr);
                        }
                        int j = Arrays.binarySearch(bucket.keys, key);
                        if(j >= 0) {
                            Value found = bucket.values[j];
                            assert (found != null);
                            if(LOG.isDebugEnabled()) {
                                LOG.debug("found in open bucket bucket(" + openAddr
                                        + ") of the overflow bucket(" + page.getPageNum() + ')');
                            }
                            return found;
                        }//fall through if not found                        
                    }
                }
                long np = ph.getNextCollision();
                if(np != NO_PAGE && idx == keys.length) {
                    HashBucket bucket = loadRecordedBucket(np);
                    if(bucket == null) {
                        throw new IllegalStateException("Required page is not in use: " + np);
                    }
                    if(LOG.isDebugEnabled() && nth == 1) {
                        LOG.debug("found chain for bucket (" + page.getPageNum() + ')');
                    }
                    return bucket.findValue(key, nth + 1);
                }
                if(LOG.isDebugEnabled() && nth > 1) {
                    LOG.debug("key not found, while tried to lookup " + nth + " chains");
                }
                return null;// not found
            }
        }

        public synchronized Value removeValue(Key key) throws IOException {
            return this.removeValue(key, 1);
        }

        private Value removeValue(Key key, int nth) throws IOException {
            int idx = Arrays.binarySearch(keys, key);
            if(idx >= 0) {//found
                Value old = values[idx];
                set(ArrayUtils.remove(keys, idx), ArrayUtils.remove(values, idx));
                write();
                return old;
            } else {// try to look up next collision page                
                idx = -(idx + 1);
                assert (idx <= keys.length) : "idx: " + idx + ", keys: " + keys.length;
                if(nth == 1) {
                    long openAddr = ph.getNextOpenAddress();
                    if(openAddr != NO_PAGE) {
                        HashBucket bucket = loadRecordedBucket(openAddr);
                        if(bucket == null) {
                            throw new IllegalStateException("Required page is not in use: "
                                    + openAddr);
                        }
                        int j = Arrays.binarySearch(bucket.keys, key);
                        if(j >= 0) {
                            Value found = bucket.values[j];
                            assert (found != null);
                            set(ArrayUtils.remove(bucket.keys, j), ArrayUtils.remove(bucket.values, j));
                            bucket.write();
                            return found;
                        }//fall through if not found
                    }
                }
                long np = ph.getNextCollision();
                if(np != NO_PAGE && idx == keys.length) {
                    HashBucket bucket = loadRecordedBucket(np);
                    if(bucket == null) {
                        throw new IllegalStateException("Required page is not in use: " + np);
                    }
                    return bucket.removeValue(key, nth + 1);
                }
                return null;// not found
            }
        }

        private void set(Key[] keys, Value[] values) {
            this.keys = keys;
            this.values = values;
            this.ph.setEntryCount(keys.length);
        }

        /**
         * Reads node only if it is not loaded yet
         */
        public synchronized void read() throws IOException {
            if(!this.loaded) {
                Value v = readValue(page);
                DataInputStream is = new DataInputStream(v.getInputStream());
                // Read in the keys
                int entries = ph.getEntryCount();
                assert (entries > 0);
                keys = new Key[entries];
                for(int i = 0; i < entries; i++) {
                    int keySize = is.readInt();
                    byte[] b = new byte[keySize];
                    is.read(b);
                    keys[i] = new Key(b);
                }
                // Read in the pointers
                values = new Value[entries];
                for(int i = 0; i < entries; i++) {
                    int valSize = is.readInt();
                    byte[] b = new byte[valSize];
                    is.read(b);
                    values[i] = new Value(b);
                }
                this.loaded = true;
            }
        }

        public synchronized void write() throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(_fileHeader.getWorkSize());
            DataOutputStream os = new DataOutputStream(bos);
            // Write out the Values
            for(int i = 0; i < keys.length; i++) {
                os.writeInt(keys[i].getLength());
                keys[i].writeTo(os);
            }
            // Write out the Values
            for(int i = 0; i < values.length; i++) {
                os.writeInt(values[i].getLength());
                values[i].writeTo(os);
            }
            writeValue(page, new Value(bos.toByteArray()));
        }

    }

    private final class HashFileHeader extends FileHeader {

        private BitSet _openBuckets;

        public HashFileHeader(long pageCount) {
            super(pageCount);
            if(pageCount > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Illegal pageCount: " + pageCount);
            }
            int buckets = (int) pageCount;
            BitSet open = new BitSet(buckets);
            open.flip(0, buckets);
            this._openBuckets = open;
        }

        @Override
        protected synchronized void write(RandomAccessFile raf) throws IOException {
            super.write(raf);
            byte[] b = ObjectUtils.toBytes(_openBuckets);
            raf.writeInt(b.length);
            raf.write(b);
        }

        @Override
        protected synchronized void read(RandomAccessFile raf) throws IOException {
            super.read(raf);
            int len = raf.readInt();
            byte[] b = new byte[len];
            raf.read(b);
            this._openBuckets = ObjectUtils.readObject(new ByteArrayInputStream(b));
        }
    }

    private final class HashPageHeader extends PageHeader {

        private int entries = 0;

        private long nextOpen = NO_PAGE;

        private long nextCollision = NO_PAGE;

        public HashPageHeader() {}

        @Override
        public synchronized void read(ByteBuffer buf) throws IOException {
            super.read(buf);
            if(getStatus() == UNUSED) {
                return;
            }
            this.entries = buf.getInt();
            this.nextOpen = buf.getLong();
            this.nextCollision = buf.getLong();
        }

        @Override
        public synchronized void write(ByteBuffer buf) throws IOException {
            super.write(buf);
            buf.putInt(entries);
            buf.putLong(nextOpen);
            buf.putLong(nextCollision);
        }

        public void setNextOpenAddress(long nextOpen) {
            this.nextOpen = nextOpen;
        }

        public long getNextOpenAddress() {
            return nextOpen;
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
