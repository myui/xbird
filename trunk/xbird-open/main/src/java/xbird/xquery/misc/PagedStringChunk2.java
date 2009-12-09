/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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
package xbird.xquery.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.server.services.RemotePagingService;
import xbird.storage.DbCollection;
import xbird.storage.io.RemoteVarSegments;
import xbird.storage.io.Segments;
import xbird.storage.io.VarSegments;
import xbird.util.cache.ILongCache;
import xbird.util.collections.LRUMap;
import xbird.util.collections.PairList;
import xbird.util.compress.CompressionCodec;
import xbird.util.compress.CompressorFactory;
import xbird.util.concurrent.cache.ConcurrentLongCache;
import xbird.util.concurrent.reference.FinalizableSoftValueReferenceMap;
import xbird.util.concurrent.reference.ReferenceMap;
import xbird.util.concurrent.reference.ReferentFinalizer;
import xbird.util.io.FastBufferedInputStream;
import xbird.util.io.FastBufferedOutputStream;
import xbird.util.lang.ObjectUtils;
import xbird.util.primitives.MutableString;
import xbird.util.primitives.Primitives;
import xbird.util.resource.PropertyMap;
import xbird.util.string.StringUtils;
import xbird.xquery.dm.coder.SerializationContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class PagedStringChunk2 implements IStringChunk {
    private static final long serialVersionUID = 7336041844435810833L;
    private static final Log LOG = LogFactory.getLog(PagedStringChunk2.class);
    private static final String CACHE_FILE_SUFFIX = ".cache";
    private static final String KEY_PENDING_CCPTR = "pending_ccptr";

    private static final int BLOCK_SHIFT = 12;
    private static final int BLOCK_SIZE = 1 << BLOCK_SHIFT;
    private static final long BLOCK_MASK = BLOCK_SIZE - 1L;
    private static final int CHUNKED_THRESHOLD = 512;
    private static final int FLUSH_THRESHOLD = 1024;

    //--------------------------------------------
    // shared stuff

    private static final ReferenceMap<String, Map<MutableString, Long>> _constructMapCache;
    static {
        ReferentFinalizer<String, Map<MutableString, Long>> finalizer = new ReferentFinalizer<String, Map<MutableString, Long>>() {
            public void finalize(String fileName, Map<MutableString, Long> map) {
                try {
                    makeCache(fileName, map);
                } catch (IOException e) {
                    LOG.warn(e);
                }
            }
        };
        _constructMapCache = new FinalizableSoftValueReferenceMap<String, Map<MutableString, Long>>(finalizer);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                for(Map.Entry<String, Map<MutableString, Long>> e : _constructMapCache.entrySet()) {
                    try {
                        makeCache(e.getKey(), e.getValue());
                    } catch (IOException ioe) {
                        LOG.debug(ioe);
                    }
                }
            }
        });
    }

    //--------------------------------------------
    // temporary stuff

    private final char[] _tmpBuf = new char[CHUNKED_THRESHOLD];

    private/* final */Map<MutableString, Long> _constructionMap;
    private final PairList<Long, byte[]> _pendingFlushPages = new PairList<Long, byte[]>(FLUSH_THRESHOLD);

    private/* final */ILongCache<char[]> _referenceMap = new ConcurrentLongCache<char[]>(4096); // 8k * 4k = 32MB

    //--------------------------------------------
    // persistent stuff

    private Segments paged_;
    private transient int strBlockPtr_;
    private transient long ccPointer_;

    private int _pendingCharsPtr = 0;
    private final char[] _pendingChars = new char[BLOCK_SIZE];

    //--------------------------------------------
    // control stuff

    private final AtomicInteger _refcount = new AtomicInteger(1);
    private final CompressionCodec _compressor = CompressorFactory.createCodec();

    //--------------------------------------------
    // transfer stuff

    private SerializationContext _serContext = null;
    private final boolean _tranfered;

    // -------------------------------------------------

    /** should not called otherwise serialization */
    public PagedStringChunk2() {
        this._tranfered = true;
        this.paged_ = null; // dummy
        this._constructionMap = null;
        this.strBlockPtr_ = -1;
        this.ccPointer_ = 0L;
    }

    public PagedStringChunk2(VarSegments paged, PropertyMap properties) {
        if(paged == null) {
            throw new IllegalArgumentException();
        }
        this._tranfered = false;
        this.paged_ = paged;

        String sp = properties.getProperty(KEY_STRPOOL_WRITTEN, "-1");
        this.strBlockPtr_ = Integer.parseInt(sp);
        String cp = properties.getProperty(KEY_CHUNK_POINTER, "0");
        this.ccPointer_ = Long.parseLong(cp);
        String pc = properties.getProperty(KEY_PENDING_CCPTR, "0");
        this._pendingCharsPtr = Integer.parseInt(pc);

        if(_pendingCharsPtr > 0) {
            long page = (ccPointer_ - 1) >> BLOCK_SHIFT;
            byte[] b = read(page);
            assert (b != null) : "ccPointer:" + ccPointer_ + ",  page:" + page;
            byte[] decompressed = _compressor.decompress(b);
            Primitives.getChars(decompressed, _pendingChars);
        }

        this._constructionMap = reconstruct(paged);
    }

    private Map<MutableString, Long> reconstruct(VarSegments paged) {
        String fileName = paged.getFile().getAbsolutePath() + CACHE_FILE_SUFFIX;
        final Map<MutableString, Long> map = _constructMapCache.get(fileName);
        if(map != null) {
            return map;
        }
        File cacheFile = new File(fileName);
        if(!cacheFile.exists()) {
            return new LRUMap<MutableString, Long>(Integer.getInteger("xbird.strchk.cmapsize", 32768));
        }
        final FileInputStream fis;
        try {
            fis = new FileInputStream(cacheFile);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
        final FastBufferedInputStream bis = new FastBufferedInputStream(fis);
        Object obj = ObjectUtils.readObjectQuietly(bis);
        return (Map<MutableString, Long>) obj;
    }

    public int getAndIncrementReferenceCount() {
        return _refcount.getAndIncrement();
    }

    public void setSerializationContext(SerializationContext serContext) {
        this._serContext = serContext;
    }

    public long getBufferAddress(long addr) {
        if(addr < 0) {
            return addr;
        } else {
            long page = addr >> BLOCK_SHIFT;
            return page;
        }
    }

    public void get(long addr, StringBuilder sb) {
        if(addr < 0) {
            char[] c = getStringInternal(addr);
            sb.append(c);
        } else {
            char[] c = getCharChunkInternal(addr);
            int block = (int) (addr & BLOCK_MASK);
            int length = c[block]; // the first char is the length of the string
            sb.append(c, block + 1, length);
        }
    }

    public String getString(long addr) {
        if(addr < 0) {
            char[] c = getStringInternal(addr);
            return new String(c);
        } else {
            char[] c = getCharChunkInternal(addr);
            int block = (int) (addr & BLOCK_MASK);
            int length = c[block]; // the first char is the length of the string
            return new String(c, block + 1, length);
        }
    }

    private char[] getStringInternal(final long addr) {
        char[] c = _referenceMap.get(addr);
        if(c != null) {
            return c;
        }
        final byte[] b;
        if(_tranfered) {
            b = readv(addr, _serContext);
        } else {
            b = read(addr);
        }
        c = _compressor.decompressAsChars(b);
        _referenceMap.put(addr, c);
        return c;
    }

    private char[] getCharChunkInternal(final long addr) {
        final long page = addr >> BLOCK_SHIFT;
        char[] c = _referenceMap.get(page);
        if(c != null) {
            return c;
        }
        final byte[] b;
        if(_tranfered) {
            b = readv(page, _serContext);
        } else {
            b = read(page);
        }
        c = _compressor.decompressAsChars(b);
        _referenceMap.put(page, c);
        return c;
    }

    private byte[] read(final long addr) {
        try {// read from disk
            return paged_.read(addr);
        } catch (IOException e) {
            throw new IllegalStateException("read from disk failed", e);
        }
    }

    private byte[] readv(final long addr, final SerializationContext serContext) {
        SortedSet<Long> tails = serContext.textBufferAddresses().tailSet(addr);
        int nPages = Math.min(tails.size(), 16); // minimum 16 MB 
        final long[] addrs;
        if(nPages == 0) {
            addrs = new long[] { addr };
        } else {
            addrs = new long[nPages];
            int n = 0;
            for(long l : tails) {
                addrs[n++] = l;
                if(n == nPages) {
                    break;
                }
            }
        }
        final byte[][] tmp;
        try {// read from disk
            tmp = paged_.readv(addrs);
        } catch (IOException e) {
            throw new IllegalStateException("read from disk failed", e);
        }
        for(int i = 1; i < nPages; i++) {
            char[] cc = _compressor.decompressAsChars(tmp[i]);
            long tmpAddr = addrs[i];
            _referenceMap.put(tmpAddr, cc);
        }
        return tmp[0];
    }

    public synchronized long store(char[] ch, int start, int length) {
        if(length < CHUNKED_THRESHOLD) {
            return storeCharChunk(ch, start, length);
        } else {
            byte[] b = Primitives.toBytes(ch, start, length);
            return storeStringChunk(b);
        }
    }

    public synchronized long store(String s) {
        final int strlen = s.length();
        if(strlen < CHUNKED_THRESHOLD) {
            s.getChars(0, strlen, _tmpBuf, 0);
            return storeCharChunk(_tmpBuf, 0, strlen);
        } else {
            byte[] b = StringUtils.getBytes(s);
            return storeStringChunk(b);
        }
    }

    private int storeStringChunk(final byte[] s) {
        if(_pendingFlushPages.size() >= FLUSH_THRESHOLD) {
            flushPendingPages();
        }
        final byte[] b = _compressor.compress(s);
        _pendingFlushPages.add(new Long(strBlockPtr_), b);
        return strBlockPtr_--;
    }

    private long storeCharChunk(final char[] ch, final int start, final int length) {
        MutableString probe = new MutableString(ch, start, length);
        Long addr = _constructionMap.get(probe);
        if(addr != null) {
            return addr.longValue();
        }

        int deltaToPlus = length + 1;
        int nextPtrProbe = _pendingCharsPtr + deltaToPlus;
        if(nextPtrProbe >= BLOCK_SIZE) {
            if(_pendingFlushPages.size() >= FLUSH_THRESHOLD) {
                flushPendingPages();
            }
            final byte[] b = compress(_compressor, _pendingChars);
            long page = (ccPointer_ - 1) >> BLOCK_SHIFT;
            _pendingFlushPages.add(page, b);
            ccPointer_ = (page + 1) << BLOCK_SHIFT; // set counter to next page
            _pendingCharsPtr = 0;
        }

        _pendingChars[_pendingCharsPtr] = (char) length;
        System.arraycopy(ch, start, _pendingChars, _pendingCharsPtr + 1, length);
        _pendingCharsPtr += deltaToPlus;

        long curPointer = ccPointer_;
        _constructionMap.put(probe, curPointer);
        ccPointer_ += deltaToPlus;
        return curPointer;
    }

    private static byte[] compress(final CompressionCodec compressor, final char[] c) {
        final byte[] b = Primitives.toBytes(c);
        return compressor.compress(b);
    }

    private void flushPendingPages() {
        final PairList<Long, byte[]> pendings = _pendingFlushPages;
        final int size = pendings.size();
        for(int i = 0; i < size; i++) {
            try {
                paged_.write(pendings.getKey(i), pendings.getValue(i));
            } catch (IOException ioe) {
                throw new IllegalStateException(ioe);
            }
        }
        pendings.clear();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        RemoteVarSegments paged = RemoteVarSegments.read(in);
        this.paged_ = paged;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if(paged_ instanceof RemoteVarSegments) {
            ((RemoteVarSegments) paged_).writeExternal(out);
        } else {
            File dataFile = paged_.getFile();
            String filePath = dataFile.getAbsolutePath();
            RemoteVarSegments remoteSeg = new RemoteVarSegments(RemotePagingService.PORT, filePath, false);
            remoteSeg.writeExternal(out);
        }
    }

    public synchronized void flush(DbCollection coll, String docName, PropertyMap properties)
            throws IOException {
        if(_pendingCharsPtr > 0) {
            final byte[] b = compress(_compressor, _pendingChars);
            long page = (ccPointer_ - 1) >> BLOCK_SHIFT;
            _pendingFlushPages.add(page, b);
            assert (_pendingCharsPtr < BLOCK_SIZE) : _pendingCharsPtr;
        }
        if(!_pendingFlushPages.isEmpty()) {
            flushPendingPages();
        }
        properties.setProperty(KEY_STRPOOL_WRITTEN, Integer.toString(strBlockPtr_));
        properties.setProperty(KEY_CHUNK_POINTER, Long.toString(ccPointer_));
        properties.setProperty(KEY_PENDING_CCPTR, Integer.toString(_pendingCharsPtr));

        paged_.flush(false);
        close();
    }

    public void close() throws IOException {
        if(_refcount.decrementAndGet() == 0) {
            String fileName = paged_.getFile().getAbsolutePath() + CACHE_FILE_SUFFIX;
            _constructMapCache.put(fileName, _constructionMap);
            forseClose();
        }
    }

    private void forseClose() throws IOException {
        this._constructionMap = null;
        _referenceMap.clear(); // TODO REVIEWME
        if(paged_ != null) {
            paged_.flush(true);
            //this.paged_ = null;
        }
    }

    private static void makeCache(final String fileName, final Map<MutableString, Long> map)
            throws IOException {
        File cacheFile = new File(fileName);
        FileOutputStream fos = new FileOutputStream(cacheFile, false);
        FastBufferedOutputStream bos = new FastBufferedOutputStream(fos);
        ObjectUtils.toStream(map, bos);
        bos.flush();
        bos.close();
    }
}
