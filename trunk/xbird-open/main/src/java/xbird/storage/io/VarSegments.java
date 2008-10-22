/*
 * @(#)$Id: VarSegments.java 3640 2008-04-03 18:12:47Z yui $
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
package xbird.storage.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.storage.DbException;
import xbird.storage.index.BTree;
import xbird.storage.index.Value;
import xbird.util.collections.Long2LongOpenHash;
import xbird.util.io.FastBufferedInputStream;
import xbird.util.io.FastBufferedOutputStream;
import xbird.util.io.IOUtils;
import xbird.util.lang.ObjectUtils;

/**
 * Variable-size pager.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class VarSegments implements Segments {
    private static final Log LOG = LogFactory.getLog(VarSegments.class);
    private static final String ENV_DESCRIPTOR_TYPE = System.getProperty("xbird.descriotor.type", "hash");

    public static final int DEFAULT_BLOCK_CACHE_SIZE = 256;
    private static final int INTEGER_BYTES = Integer.SIZE / 8;

    private static final String DESCRIPTOR_APPENDIX_HASH = ".di_h";
    private static final String DESCRIPTOR_APPENDIX_RAF = ".di_f";
    private static final String DESCRIPTOR_APPENDIX_BTREE = ".di_b";

    private final File file;
    private final IDescriptor directory;

    private RandomAccessFile raf = null;
    private long recorded = 8;

    private transient boolean open = false;

    public VarSegments(File file) {
        this(file, DEFAULT_BLOCK_CACHE_SIZE);
    }

    public VarSegments(File file, int cacheSize) {
        this.file = file;
        try {
            if(file.exists() && file.length() > 0) {
                this.recorded = loadRecoreded();
            }
            this.directory = initDescriptor(file, cacheSize >>> 2);
        } catch (IOException e) {
            throw new IllegalStateException("preparing descriptor failed", e);
        }
    }

    public VarSegments(File file, DescriptorType type) {
        this(file, DEFAULT_BLOCK_CACHE_SIZE, type);
    }

    public VarSegments(File file, int cacheSize, DescriptorType type) {
        this.file = file;
        if(file.exists()) {
            try {
                this.recorded = loadRecoreded();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        final IDescriptor desc;
        switch(type) {
            case hash:
                desc = new DescriptorHash(file, cacheSize);
                break;
            case btree:
                desc = new DescriptorBTree(file, cacheSize);
                break;
            case file:
                try {
                    desc = new DescriptorRAF(file, cacheSize);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
                break;
            default:
                throw new IllegalStateException("Illegal type: " + type);
        }
        this.directory = desc;
    }

    private long loadRecoreded() throws IOException {
        ensureOpen();
        raf.seek(0);
        long recorded = raf.readLong();
        return recorded;
    }

    public static IDescriptor initDescriptor(File baseFile) throws IOException {
        return initDescriptor(baseFile, DEFAULT_BLOCK_CACHE_SIZE >>> 2);
    }

    public static IDescriptor initDescriptor(File baseFile, int cacheSize) throws IOException {
        String baseFileName = baseFile.getName();
        File probe = new File(baseFile.getParent(), baseFileName + DESCRIPTOR_APPENDIX_BTREE);
        if(probe.exists() || ENV_DESCRIPTOR_TYPE.equals("btree")) {
            return new DescriptorBTree(baseFile, cacheSize);
        }
        probe = new File(baseFile.getParent(), baseFileName + DESCRIPTOR_APPENDIX_RAF);
        if(probe.exists() || ENV_DESCRIPTOR_TYPE.equals("file")) {
            return new DescriptorRAF(baseFile, cacheSize);
        }
        return new DescriptorHash(baseFile, cacheSize);
    }

    public File getFile() {
        return file;
    }

    public long write(final long idx, final byte[] b) throws IOException {
        ensureOpen();
        final long prevAddr = directory.getRecordAddr(idx);
        if(prevAddr != -1L && prevAddr < recorded) {
            synchronized(raf) {
                raf.seek(prevAddr);
                int prevLength = raf.readInt();
                if(b.length == prevLength) {
                    raf.seek(prevAddr + 4);
                    raf.write(b);
                    return prevAddr;
                }
            }
        }
        final long ptr = recorded;
        // write to disk
        synchronized(raf) {//insert
            raf.seek(ptr);
            raf.writeInt(b.length);
            raf.write(b);
        }
        directory.addRecord(idx, ptr);
        this.recorded = ptr + INTEGER_BYTES + b.length;
        return ptr;
    }

    public long writeIfAbsent(final long idx, final byte[] b) throws IOException {
        ensureOpen();
        final long prevAddr = directory.getRecordAddr(idx);
        if(prevAddr != -1L && prevAddr < recorded) {
            return -1L;
        }
        final long ptr = recorded;
        // write to disk
        synchronized(raf) {//insert
            raf.seek(ptr);
            raf.writeInt(b.length);
            raf.write(b);
        }
        directory.addRecord(idx, ptr);
        this.recorded = ptr + INTEGER_BYTES + b.length;
        return ptr;
    }

    /**
     * reads persistent record
     */
    public byte[] read(final long idx) throws IOException {
        final long ptr = directory.getRecordAddr(idx);
        if(ptr == -1) {
            return null;
        }
        return directRead(ptr);
    }

    public byte[][] readv(long[] idx) throws IOException {
        final int len = idx.length;
        final byte[][] pages = new byte[len][];
        for(int i = 0; i < len; i++) {
            pages[i] = read(idx[i]);
        }
        return pages;
    }

    private byte[] directRead(final long addr) throws IOException {
        if(LOG.isDebugEnabled()) {
            LOG.debug("paged in document segment of physical addr #" + addr + " from disk");
        }
        ensureOpen();
        final byte[] b;
        synchronized(raf) {
            raf.seek(addr);
            int len = raf.readInt();
            b = new byte[len];
            raf.read(b);
        }
        return b;
    }

    private void ensureOpen() throws IOException {
        if(open) {
            return;
        }
        synchronized(this) {
            if(!open) {
                if(raf == null) {
                    if(!file.exists()) {
                        boolean created = file.createNewFile();
                        if(!created) {
                            throw new IllegalStateException("could'nt create file: "
                                    + file.getAbsolutePath());
                        }
                    }
                    this.raf = new RandomAccessFile(file, "rw");
                }
                this.open = true;
            }
        }
    }

    public void close() throws IOException {
        if(raf != null) {
            raf.close();
        }
        directory.close();
        this.open = false;
    }

    public void delete() throws IOException {
        close();
        if(!file.delete()) {
            LOG.warn("delete file failed: " + file.getAbsolutePath());
        }
        directory.delete();
    }

    public synchronized void flush(boolean close) throws IOException {
        if(!open) {
            throw new IllegalStateException("not opened: " + file.getAbsolutePath());
        }
        directory.flush(close);
        syncRecordLength();
        raf.getChannel().force(true);
        if(close) {
            close();
        }
    }

    private void syncRecordLength() throws IOException {
        raf.seek(0);
        raf.writeLong(recorded);
    }

    public interface IDescriptor {

        public void addRecord(final long idx, final long addr) throws IOException;

        public long getRecordAddr(final long idx) throws IOException;

        public void flush(boolean close) throws IOException;

        public void close() throws IOException;

        public void delete() throws IOException;

    }

    public enum DescriptorType {
        hash, btree, file
    }

    private static final class DescriptorHash implements IDescriptor {
        private Long2LongOpenHash recordMap;
        private final File descFile;

        public DescriptorHash(File baseFile, int cacheSize) {
            final String descName = baseFile.getName() + DESCRIPTOR_APPENDIX_HASH;
            final File file = new File(baseFile.getParent(), descName);
            if(file.exists()) {
                final FileInputStream fis;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    throw new IllegalStateException();
                }
                final FastBufferedInputStream bis = new FastBufferedInputStream(fis, 4096);
                this.recordMap = ObjectUtils.readObjectQuietly(bis);
                IOUtils.closeQuietly(bis);
            } else {
                this.recordMap = new Long2LongOpenHash(cacheSize, 0.7f, 1.9f);
            }
            this.descFile = file;
        }

        public void addRecord(final long idx, final long addr) {
            recordMap.put(idx, addr);
        }

        public long getRecordAddr(final long idx) {
            return recordMap.get(idx);
        }

        public void close() throws IOException {
            this.recordMap = null;
        }

        public void delete() throws IOException {
            close();
            if(!descFile.delete()) {
                LOG.warn("delete file failed: " + descFile.getAbsolutePath());
            }
        }

        public void flush(boolean close) throws IOException {
            FileOutputStream fos = new FileOutputStream(descFile, false);
            FastBufferedOutputStream bos = new FastBufferedOutputStream(fos, 8192);
            ObjectUtils.toStream(recordMap, bos);
            bos.flush();
            bos.close();

            if(close) {
                close();
            }
        }
    }

    private static final class DescriptorBTree implements IDescriptor {

        private final File descFile;
        private final BTree btree;

        public DescriptorBTree(File baseFile, int cacheSize) {
            String descName = baseFile.getName() + DESCRIPTOR_APPENDIX_BTREE;
            File descFile = new File(baseFile.getParent(), descName);
            this.descFile = descFile;
            BTree tree = new BTree(descFile, 2048, cacheSize, false);
            try {
                tree.init(false);
            } catch (DbException e) {
                throw new IllegalStateException("failed on initializing b+-tree: "
                        + descFile.getAbsolutePath(), e);
            }
            this.btree = tree;
        }

        public void addRecord(long idx, long addr) throws IOException {
            try {
                btree.addValue(new Value(idx), addr);
            } catch (DbException e) {
                throw new IOException("failed to add a record addr#" + addr + " to the idx#" + idx);
            }
        }

        public long getRecordAddr(long idx) throws IOException {
            try {
                return btree.findValue(new Value(idx));
            } catch (DbException e) {
                throw new IOException("failed to get a record of idx#" + idx);
            }
        }

        public void close() throws IOException {
            try {
                btree.close();
            } catch (DbException e) {
                throw new IOException("failed to close: " + btree.getFile().getAbsolutePath());
            }
        }

        public void flush(boolean close) throws IOException {
            try {
                btree.flush(true, true);
                if(close) {
                    btree.close();
                }
            } catch (DbException e) {
                throw new IOException("failed to flush: " + btree.getFile().getAbsolutePath());
            }
        }

        public void delete() throws IOException {
            close();
            if(!descFile.delete()) {
                LOG.warn("delete file failed: " + descFile.getAbsolutePath());
            }
        }
    }

    /** key-address directory */
    @Deprecated
    private static final class DescriptorRAF implements IDescriptor {

        private final File descFile;
        private final RandomAccessFile raf;
        private final int purgeUnits;

        private SortedMap<Long, Long> addrCache = new TreeMap<Long, Long>();

        public DescriptorRAF(File baseFile, int cacheSize) throws IOException {
            String descName = baseFile.getName() + DESCRIPTOR_APPENDIX_RAF;
            File descFile = new File(baseFile.getParent(), descName);
            if(!descFile.exists()) {
                boolean created = descFile.createNewFile();
                if(!created) {
                    throw new IllegalStateException("create file failed: "
                            + descFile.getAbsolutePath());
                }
            }
            this.descFile = descFile;
            this.raf = new RandomAccessFile(descFile, "rw");
            this.purgeUnits = cacheSize;
        }

        public void addRecord(final long idx, final long addr) throws IOException {
            if(idx < 0) {
                throw new IllegalArgumentException("Illegal idx: " + idx);
            }
            if(addrCache.size() >= purgeUnits) {
                purge();
            }
            addrCache.put(idx, addr);
            //TODO update handling
        }

        public long getRecordAddr(final long idx) throws IOException {
            if(idx < 0) {
                throw new IllegalArgumentException("Illegal idx: " + idx);
            }
            Long laddr = addrCache.get(idx);
            if(laddr != null) {
                return laddr.longValue();
            }
            long offset = idx * 8;
            if(offset >= raf.length()) {
                return -1;// not found
            }
            final long addr;
            synchronized(raf) {
                raf.seek(offset);
                addr = raf.readLong();
            }
            return addr;
        }

        private synchronized void purge() throws IOException {
            long prevIdx = -1;
            for(Entry<Long, Long> e : addrCache.entrySet()) {
                final long idx = e.getKey();
                final long addr = e.getValue();
                synchronized(raf) {
                    if(idx != (prevIdx + 8)) {
                        final long offset = idx * 8;
                        raf.seek(offset);
                    }
                    raf.writeLong(addr);
                }
                prevIdx = idx;
            }
            addrCache.clear();
        }

        public void flush(boolean close) throws IOException {
            if(!addrCache.isEmpty()) {
                purge();
            }
            raf.getChannel().force(true);
            if(close) {
                raf.close();
            }
        }

        public void close() throws IOException {
            addrCache = null;
            raf.close();
        }

        public void delete() throws IOException {
            close();
            if(!descFile.delete()) {
                LOG.warn("delete file failed: " + descFile.getAbsolutePath());
            }
        }
    }
}
