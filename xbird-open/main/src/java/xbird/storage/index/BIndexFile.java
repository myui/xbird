/*
 * @(#)$Id$
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

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xbird.storage.DbException;
import xbird.storage.index.FreeList.FreeSpace;
import xbird.storage.indexer.IndexQuery;
import xbird.util.collections.LRUMap;
import xbird.util.collections.longs.ObservableLongLRUMap;
import xbird.util.collections.longs.LongHash.BucketEntry;
import xbird.util.collections.longs.LongHash.Cleaner;
import xbird.util.collections.longs.LongHash.LongLRUMap;
import xbird.util.lang.Primitives;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class BIndexFile extends BTree {

    private static final byte DATA_RECORD = 10;
    public static final int DATA_CACHE_SIZE = Integer.getInteger("bfile.cache_size", 64);
    public static final int DATA_CACHE_PURGE_UNIT = Integer.getInteger("bfile.cache_purgeunit", 8);

    private final LongLRUMap<DataPage> dataCache;
    private final Map<Value, Long> storeCache = new LRUMap<Value, Long>(64);

    public BIndexFile(File file) {
        this(file, true);
    }

    public BIndexFile(File file, boolean duplicateAllowed) {
        this(file, DEFAULT_PAGESIZE, DEFAULT_IN_MEMORY_NODES, duplicateAllowed);
    }

    public BIndexFile(File file, int pageSize, int caches, boolean duplicateAllowed) {
        super(file, pageSize, caches, duplicateAllowed);
        final Synchronizer sync = new Synchronizer();
        this.dataCache = new ObservableLongLRUMap<DataPage>(DATA_CACHE_SIZE, DATA_CACHE_PURGE_UNIT, sync);
    }

    @Override
    public BFileHeader createFileHeader(int pageSize) {
        return new BFileHeader(pageSize);
    }

    @Override
    public BFileHeader getFileHeader() {
        return (BFileHeader) super.getFileHeader();
    }

    @Override
    public BFilePageHeader createPageHeader() {
        return new BFilePageHeader();
    }

    public final byte[] getValueBytes(long key) throws DbException {
        return getValueBytes(new Value(key));
    }

    public synchronized byte[] getValueBytes(Value key) throws DbException {
        final long ptr = findValue(key);
        if(ptr == KEY_NOT_FOUND) {
            return null;
        }
        return retrieveTuple(ptr);
    }

    protected synchronized final byte[] retrieveTuple(long ptr) throws DbException {
        long pageNum = getPageNumFromPointer(ptr);
        DataPage dataPage = getDataPage(pageNum);
        int tidx = getTidFromPointer(ptr);
        return dataPage.get(tidx);
    }

    public Value getValue(Value key) throws DbException {
        final byte[] tuple = getValueBytes(key);
        return new Value(tuple);
    }

    @Override
    public final void search(IndexQuery query, BTreeCallback callback) throws DbException {
        super.search(query, getHandler(callback));
    }

    protected BTreeCallback getHandler(BTreeCallback handler) {
        return new BFileCallback(handler);
    }

    public final long putValue(long key, byte[] value) throws DbException {
        return putValue(new Value(key), new Value(value));
    }

    public final long putValue(Value key, byte[] value) throws DbException {
        return putValue(key, new Value(value));
    }

    public synchronized long putValue(Value key, Value value) throws DbException {
        long ptr = findValue(key);
        if(ptr != KEY_NOT_FOUND) {// key found
            // update the page
            if(!isDuplicateAllowed()) {
                updateValue(value, ptr);
                return ptr;
            }
        }
        // insert a new key
        ptr = storeValue(value);
        addValue(key, ptr);
        return ptr;
    }

    protected final void updateValue(Value value, long ptr) throws DbException {
        long pageNum = getPageNumFromPointer(ptr);
        DataPage dataPage = getDataPage(pageNum);
        int tidx = getTidFromPointer(ptr);
        dataPage.set(tidx, value);
    }

    protected final long storeValue(Value value) throws DbException {
        final Long cachedPtr = storeCache.get(value);
        if(cachedPtr != null) {
            return cachedPtr.longValue();
        }

        final BFileHeader fh = getFileHeader();
        final FreeList freeList = fh.getFreeList();

        final int requiredSize = value.getLength() + 4;

        final DataPage dataPage;
        FreeSpace free = freeList.retrieve(requiredSize);
        if(free == null) {
            DataPage newPage = createDataPage();
            free = new FreeSpace(newPage.getPageNum(), fh.getWorkSize());
            freeList.add(free);
            dataPage = newPage;
        } else {
            dataPage = getDataPage(free.getPage());
        }

        final long pageNum = dataPage.getPageNum();
        final int tid = dataPage.add(value);

        saveFreeList(freeList, free, dataPage);

        long ptr = createPointer(pageNum, tid);
        storeCache.put(value, ptr);
        return ptr;
    }

    private void saveFreeList(FreeList freeList, FreeSpace free, DataPage dataPage) {
        final BFileHeader fh = getFileHeader();
        final int leftFree = fh.getWorkSize() - dataPage.getTotalDataLen();
        free.setFree(leftFree);
        if(leftFree < FreeSpace.MIN_LEFT_FREE) {
            freeList.remove(free);
        }
    }

    private DataPage createDataPage() throws DbException {
        Page p = getFreePage();
        DataPage dataPage = new DataPage(p);
        dataCache.put(p.getPageNum(), dataPage);
        return dataPage;
    }

    private DataPage getDataPage(long pageNum) throws DbException {
        DataPage dataPage = dataCache.get(pageNum);
        if(dataPage == null) {
            Page p = getPage(pageNum);
            dataPage = new DataPage(p);
            try {
                dataPage.read();
            } catch (IOException e) {
                throw new DbException("failed to read page#" + pageNum, e);
            }
            dataCache.put(pageNum, dataPage);
        }
        return dataPage;
    }

    private final class DataPage {
        private final Page page;
        private final BFilePageHeader ph;

        private final List<byte[]> tuples = new ArrayList<byte[]>(12);
        private int totalDataLen = 0;

        private boolean loaded = false;
        private boolean dirty = false;

        public DataPage(Page page) {
            this.page = page;
            ph = (BFilePageHeader) page.getPageHeader();
            ph.setStatus(DATA_RECORD);
        }

        public BFilePageHeader getPageHeader() {
            return ph;
        }

        public long getPageNum() {
            return page.getPageNum();
        }

        public int getTotalDataLen() {
            return totalDataLen;
        }

        public int add(Value value) {
            final int idx = tuples.size();
            if(idx > Short.MAX_VALUE) {
                throw new IllegalStateException("blocks length exceeds limit: " + idx);
            }

            final byte[] tuple = value.getData();
            tuples.add(tuple);

            // update controls
            ph.setTupleCount(idx + 1);
            totalDataLen += (tuple.length + 4);
            //ph.setDataLength(totalDataLen);
            setDirty();

            return idx;
        }

        public void set(int tidx, Value value) {
            if(tidx > (tuples.size() - 1)) {
                throw new IllegalStateException("Illegal tid for DataPage#" + page.getPageNum()
                        + ": " + tidx);
            }
            final byte[] tuple = value.getData();
            final byte[] oldTuple = tuples.set(tidx, tuple);
            if(oldTuple != null) {
                int diff = tuple.length - oldTuple.length;
                totalDataLen += diff;
                //ph.setDataLength(totalDataLen);
            }
            setDirty();
        }

        private void setDirty() {
            this.dirty = true;
            dataCache.put(page.getPageNum(), this);
        }

        public byte[] get(int tidx) {
            if(tidx > (tuples.size() - 1)) {
                return null;
            }
            return tuples.get(tidx);
        }

        public void read() throws DbException, IOException {
            if(loaded) {
                return; // should never happens (just for debugging)
            }
            final int tupleCount = ph.getTupleCount();
            if(tupleCount == 0) {
                return;
            }
            Value v = readValue(page);
            DataInputStream in = new DataInputStream(v.getInputStream());
            for(int i = 0; i < tupleCount; i++) {
                int len = in.readInt();
                byte[] tuple = new byte[len];
                in.read(tuple);
                tuples.add(tuple);
            }
            if(in.available() > 0) {
                throw new IllegalStateException(in.available() + " bytes left");
            }
            this.totalDataLen = v.getLength();
            this.loaded = true;
        }

        public void write() throws DbException {
            if(!dirty) {
                return;
            }
            if(totalDataLen == 0) {
                return;
            }
            final byte[] dest = new byte[totalDataLen];
            int pos = 0;
            for(byte[] tuple : tuples) {
                final int len = tuple.length;
                Primitives.putInt(dest, pos, len);
                pos += 4;
                System.arraycopy(tuple, 0, dest, pos, len);
                pos += len;
            }
            if(pos != totalDataLen) {
                throw new IllegalStateException("writes = " + pos + ", but totalDataLen = "
                        + totalDataLen);
            }
            writeValue(page, new Value(dest));
            this.dirty = false;
        }
    }

    protected final class BFileHeader extends BTreeFileHeader {

        private final FreeList freeList = new FreeList(128);
        private boolean multiValue = false;

        public BFileHeader(int pageSize) {
            super(pageSize);
        }

        public FreeList getFreeList() {
            return freeList;
        }

        public void setMultiValue(boolean multi) {
            this.multiValue = multi;
        }

        @Override
        public synchronized void read(RandomAccessFile raf) throws IOException {
            super.read(raf);
            this.multiValue = raf.readBoolean();
            freeList.read(raf);
        }

        @Override
        public synchronized void write(RandomAccessFile raf) throws IOException {
            super.write(raf);
            raf.writeBoolean(multiValue);
            freeList.write(raf);
        }
    }

    private final class BFilePageHeader extends BTreePageHeader {

        private int tupleCount = 0;

        public BFilePageHeader() {
            super();
        }

        public int getTupleCount() {
            return tupleCount;
        }

        public void setTupleCount(int tupleCount) {
            this.tupleCount = tupleCount;
        }

        @Override
        public synchronized void read(ByteBuffer buf) {
            super.read(buf);
            this.tupleCount = buf.getInt();
        }

        @Override
        public synchronized void write(ByteBuffer buf) {
            super.write(buf);
            buf.putInt(tupleCount);
        }

    }

    private final class BFileCallback implements BTreeCallback {

        final BTreeCallback handler;

        public BFileCallback(BTreeCallback handler) {
            this.handler = handler;
        }

        public boolean indexInfo(Value key, long pointer) {
            final byte[] tuple;
            try {
                tuple = retrieveTuple(pointer);
            } catch (DbException e) {
                throw new IllegalStateException(e);
            }
            return handler.indexInfo(key, tuple);
        }

        public boolean indexInfo(Value key, byte[] value) {
            throw new UnsupportedOperationException();
        }
    }

    private static long createPointer(long pageNum, int tid) {
        if(pageNum > 0x7fffffffffffL) {// over 6 bytes
            throw new IllegalArgumentException("Unexpected pageNumber that exceeds system limit: "
                    + pageNum);
        }
        if(tid > 0x7fff) {// over 4 bytes
            throw new IllegalArgumentException("Illegal idx that exceeds system limit: " + tid);
        }
        return tid | ((pageNum & 0xffffffffffffL) << 16);
    }

    private static long getPageNumFromPointer(long ptr) {
        return ptr >>> 16;
    }

    private static int getTidFromPointer(long ptr) {
        return (int) (ptr & 0xffffL);
    }

    private static final class Synchronizer implements Cleaner<DataPage> {

        public Synchronizer() {}

        public void cleanup(long key, DataPage dataPage) {
            try {
                dataPage.write();
            } catch (DbException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public void flush(boolean purge, boolean clear) throws DbException {
        if(purge) {
            for(BucketEntry<DataPage> e : dataCache) {
                DataPage dataPage = e.getValue();
                dataPage.write();
            }
        }
        if(clear) {
            dataCache.clear();
        }
        super.flush(purge, clear);
    }
}
