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
package xbird.xquery.dm.dtm;

import java.io.Externalizable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.util.Set;

import xbird.config.Settings;
import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.storage.io.Segments;
import xbird.util.cache.ICacheEntry;
import xbird.util.cache.ILongCache;
import xbird.util.collections.LongQueue;
import xbird.util.concurrent.cache.ConcurrentLongCache;
import xbird.util.io.IOUtils;
import xbird.util.lang.PrivilegedAccessor;
import xbird.util.nio.CloseableMappedByteBuffer;
import xbird.util.nio.IMemoryMappedFile;
import xbird.util.nio.MemoryMappedFile;
import xbird.util.nio.RemoteMemoryMappedFile;
import xbird.util.resource.PropertyMap;
import xbird.util.struct.LongRangeSet;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.coder.SerializationContext;
import xbird.xquery.dm.instance.DocumentTableModel;
import xbird.xquery.misc.IStringChunk;
import xbird.xquery.misc.PagedStringChunk2;
import xbird.xquery.misc.QNameTable;

/**
 * 
 * <DIV lang="en"></DIV> <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class MemoryMappedDocumentTable extends AbstractDocumentTable
        implements Externalizable {
    private static final long serialVersionUID = 3874393492511022282L;

    public static final String MMDTM_CLASS = MemoryMappedDocumentTable.class.getName();
    private static final String PROP_NATIVE_BYTEORDER = "xbird.database.mmap.native_byteorder";
    private static final String KEY_NATIVE_BYTEORDER = "nativeByteOrder";

    // 64k mapping
    private static final int PAGE_SHIFT = Integer.parseInt(Settings.get("xbird.database.mmap.pageshift", "16"));
    private static final int PAGE_SIZE = 1 << PAGE_SHIFT;
    private static final int LOGICAL_PAGE_LENGTH = 1 << (PAGE_SHIFT - 2);
    private static final int LOGICAL_PAGE_MASK = LOGICAL_PAGE_LENGTH - 1;

    // 32MB pool
    public static final int CACHED_PAGES = Integer.parseInt(Settings.get("xbird.database.mmap.cachedpages", "512"));

    private final boolean _readOnly;
    private final IMemoryMappedFile _mmfile;
    private/* final */boolean _nativeByteOrder;

    /**
     * @see DocumentTableModel#readExternal(ObjectInput)
     */
    private transient/* final */ILongCache<int[]> _pool;

    private final boolean _transfered;

    public MemoryMappedDocumentTable() {//only for Externalizable
        super();
        this._readOnly = true;
        this._transfered = true;
        this._mmfile = null; //dummy
    }

    public MemoryMappedDocumentTable(final DbCollection coll, final String docName, final PropertyMap docProps, final boolean readOnly) {
        super(coll, docName, docProps);
        this._readOnly = readOnly;
        this._transfered = false;
        final boolean nativeByteOrder;
        if(!readOnly) {
            nativeByteOrder = Boolean.parseBoolean(Settings.get(PROP_NATIVE_BYTEORDER, "false"));
        } else {
            if(docProps == null) {
                throw new IllegalStateException();
            }
            nativeByteOrder = Boolean.parseBoolean(docProps.getProperty(KEY_NATIVE_BYTEORDER
                    + docName));
        }
        this._nativeByteOrder = nativeByteOrder;
        final File segFile = new File(coll.getDirectory(), docName + DTM_SEGMENT_FILE_SUFFIX);
        try {
            this._mmfile = new MemoryMappedFile(segFile, PAGE_SHIFT, readOnly, nativeByteOrder);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("file not found: " + segFile.getAbsolutePath(), e);
        }
        this._pool = readOnly ? new ConcurrentLongCache<int[]>(CACHED_PAGES) : null;
    }

    @Override
    public void close() throws IOException {
        if(_refcount.getAndDecrement() == 0) {
            if(!_readOnly) {
                _pool.clear();
            }
            if(_pool != null) {
                IOUtils.closeQuietly(_pool);
                this._pool = null;
            }
            _close();
            _mmfile.close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if(_pool != null) {
            if(!_readOnly) {
                _pool.clear();
            }
            this._pool = null;
        }
        _close();
        if(_mmfile != null) {
            _mmfile.close();
        }
    }

    public ILongCache<int[]> getBufferPool() {
        return _pool;
    }

    public void setBufferPool(final ILongCache<int[]> pool) {
        this._pool = pool;
    }

    public long dataAt(final long at) {
        if(at > _blockPtr) {// not initialized
            return 0;
        }
        return _readOnly ? dataAt_RO(at) : dataAt_RW(at);
    }

    private long dataAt_RO(final long at) {
        final int offset = (int) (at & LOGICAL_PAGE_MASK);
        final long pageId = toPageId(at);

        final ICacheEntry<Long, int[]> cacheSlot = _pool.fixEntry(pageId);
        final int[] cachedPage = cacheSlot.getValue();
        if(cachedPage != null) {
            cacheSlot.unpin();
            return cachedPage[offset];
        }

        final long pageOffset = toPageOffset(pageId);
        final int[] newPage;
        if(_transfered) {
            RemoteMemoryMappedFile remoteMM = (RemoteMemoryMappedFile) _mmfile;
            newPage = remoteMM.transferBuffers(pageOffset, LOGICAL_PAGE_LENGTH, _pool);
        } else {
            newPage = _mmfile.transferBuffer(pageOffset, LOGICAL_PAGE_LENGTH);
        }
        cacheSlot.setValue(newPage);
        cacheSlot.unpin();
        return newPage[offset];
    }

    /*
    private long dataAt_RO_sync(final long at) {
        final int offset = (int) (at & LOGICAL_PAGE_MASK);
        final long pageId = toPageId(at);

        final ICacheEntry<Long, int[]> cacheSlot;
        int[] cachedPage;
        synchronized(_pool) {
            cacheSlot = _pool.allocateEntry(pageId);
            cachedPage = cacheSlot.getValue();
            if(cachedPage != null) {
                final long pageOffset = toPageOffset(pageId);
                final int[] newPage;
                if(_transfered) {
                    RemoteMemoryMappedFile remoteMM = (RemoteMemoryMappedFile) _mmfile;
                    newPage = remoteMM.transferBuffers(pageOffset, LOGICAL_PAGE_LENGTH, _pool);
                } else {
                    newPage = _mmfile.transferBuffer(pageOffset, LOGICAL_PAGE_LENGTH);
                }
                cacheSlot.setValue(newPage);
                cachedPage = newPage;
            }
        }
        cacheSlot.unpin();
        return cachedPage[offset];
    }
    */
    
    /*
    private final xbird.util.concurrent.lock.SpinLock _lock = new xbird.util.concurrent.lock.AtomicBackoffLock();

    private long dataAt_RO_spinlock(final long at) {
        final int offset = (int) (at & LOGICAL_PAGE_MASK);
        final long pageId = toPageId(at);

        final ICacheEntry<Long, int[]> cacheSlot;
        int[] cachedPage;
        _lock.lock();
        try {
            cacheSlot = _pool.allocateEntry(pageId);
            cachedPage = cacheSlot.getValue();
            if(cachedPage != null) {
                final long pageOffset = toPageOffset(pageId);
                final int[] newPage;
                if(_transfered) {
                    RemoteMemoryMappedFile remoteMM = (RemoteMemoryMappedFile) _mmfile;
                    newPage = remoteMM.transferBuffers(pageOffset, LOGICAL_PAGE_LENGTH, _pool);
                } else {
                    newPage = _mmfile.transferBuffer(pageOffset, LOGICAL_PAGE_LENGTH);
                }
                cacheSlot.setValue(newPage);
                cachedPage = newPage;
            }
        } finally {
            _lock.unlock();
        }
        cacheSlot.unpin();
        return cachedPage[offset];
    }
    */

    private long dataAt_RW(final long at) {
        final long pageOffset = directToPageOffset(at);
        final CloseableMappedByteBuffer buf = _mmfile.allocateBuffer(pageOffset);
        final int offset = ((int) (at & LOGICAL_PAGE_MASK)) << 2;
        final long res = buf.getBuffer().getInt(offset); // TODO getInt should be refined
        //IOUtils.closeQuietly(buf);
        return res;
    }

    protected void setData(final long at, final long value) {
        assert (!_readOnly) : "update is not allowed for readOnly mode";
        final long pageOffset = directToPageOffset(at);
        final CloseableMappedByteBuffer buf = _mmfile.allocateBuffer(pageOffset);
        final int offset = ((int) (at & LOGICAL_PAGE_MASK)) << 2;
        buf.getBuffer().putInt(offset, (int) value);
        //IOUtils.closeQuietly(buf);
    }

    protected void setFlag(final long at, final int flag) {
        assert (!_readOnly) : "update is not allowed for readOnly mode";
        final long pageOffset = directToPageOffset(at);
        final CloseableMappedByteBuffer buf = _mmfile.allocateBuffer(pageOffset);
        final int offset = ((int) (at & LOGICAL_PAGE_MASK)) << 2;
        final ByteBuffer bufimpl = buf.getBuffer();
        final int old = bufimpl.getInt(offset);
        bufimpl.putInt(offset, old | flag);
        //IOUtils.closeQuietly(buf);
    }

    public void flush(final DbCollection coll, final String docName) throws IOException,
            DbException {
        // #1 write properties
        PropertyMap properties = coll.getCollectionProperties();
        writeProperties(docName, properties);

        // #2 write QName table
        coll.flushSymbols();

        // #3 write str chunk
        _strChunk.flush(coll, docName, properties);

        properties.save();

        _mmfile.close();
    }

    private void writeProperties(final String docName, final PropertyMap properties)
            throws IOException {
        properties.setProperty(KEY_BLOCK_PTR + docName, String.valueOf(_blockPtr));
        properties.setProperty(KEY_NATIVE_BYTEORDER + docName, String.valueOf(_nativeByteOrder));
        long usedPages = _blockPtr / LOGICAL_PAGE_LENGTH;
        if(_blockPtr % LOGICAL_PAGE_LENGTH != 0) {
            usedPages += 1;
        }
        properties.setProperty(KEY_USED_PAGES + docName, String.valueOf(usedPages));
    }

    public Segments getPaged(final DbCollection coll, final String docName) {
        throw new IllegalStateException("should not be called");
    }

    public PagingProfile getPagingProfile() {
        return null; // TODO
    }

    private static long toPageId(final long logicalAddr) {
        return (logicalAddr << 2) >>> PAGE_SHIFT;
    }

    private static long toPageOffset(final long pageId) {
        return pageId << PAGE_SHIFT;
    }

    private static long directToPageOffset(final long logicalAddr) {
        return ((logicalAddr << 2) >>> PAGE_SHIFT) << PAGE_SHIFT;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // super members
        this._blockPtr = in.readLong();
        this._nameTable = QNameTable.read(in);
        this._strChunk = (IStringChunk) in.readObject();
        // this members
        RemoteMemoryMappedFile mmfile = RemoteMemoryMappedFile.read(in);
        PrivilegedAccessor.unsafeSetField(this, MemoryMappedDocumentTable.class, "_mmfile", mmfile);

        this._nativeByteOrder = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // super members
        out.writeLong(_blockPtr);
        _nameTable.writeExternal(out);
        out.writeObject(_strChunk);
        // this members
        RemoteMemoryMappedFile remoteMMFile = _mmfile.externalize();
        remoteMMFile.writeExternal(out);
        out.writeBoolean(_nativeByteOrder);
    }

    public long[] referredTextBlocks(final long curr, final long last, final SerializationContext serContext) {
        final LongQueue textBlocks = new LongQueue();
        final Set<Long> textBufferAddrs = serContext.textBufferAddresses();
        for(long i = curr; i <= last; i += BLOCKS_PER_NODE) {
            final byte kind = getNodeKindAt(i);
            if(kind == NodeKind.TEXT || kind == NodeKind.ATTRIBUTE || kind == NodeKind.NAMESPACE
                    || kind == NodeKind.COMMENT) {
                long cid = dataAt(i + CONTENT_OFFSET);
                long addr = _strChunk.getBufferAddress(cid);
                if(textBufferAddrs.add(addr)) {
                    textBlocks.add(addr);
                }
            }
        }
        return textBlocks.toArray();
    }

    public void markReferredBlocks(final long cur, final long last, final long[] textBlocks, final SerializationContext serContext) {
        long firstPage = directToPageOffset(cur);
        long lastPage = directToPageOffset(last);
        LongRangeSet ranges = serContext.ranges();
        if(ranges.isEmpty()) {
            bindSerializationContext(serContext);
        }
        ranges.addRange(firstPage, lastPage + PAGE_SIZE);
        final Set<Long> textBufferAddrs = serContext.textBufferAddresses();
        for(long tb : textBlocks) {
            textBufferAddrs.add(tb);
        }
    }

    private void bindSerializationContext(final SerializationContext serContext) {
        ((RemoteMemoryMappedFile) _mmfile).setSerializationContext(serContext);
        ((PagedStringChunk2) _strChunk).setSerializationContext(serContext);
    }

    public String getDocumentIdentifer() {
        if(!_transfered) {
            return null;
        }
        RemoteMemoryMappedFile remoteMmFile = (RemoteMemoryMappedFile) _mmfile;
        return remoteMmFile.getFileIdentifier();
    }

}
