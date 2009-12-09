/*
 * @(#)$Id:StringChunk.java 2335 2007-07-17 04:14:15Z yui $
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
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.server.services.RemotePagingService;
import xbird.storage.DbCollection;
import xbird.storage.io.RemoteVarSegments;
import xbird.storage.io.Segments;
import xbird.storage.io.VarSegments;
import xbird.storage.io.VarSegments.DescriptorType;
import xbird.util.cache.ILongCache;
import xbird.util.collections.ints.Int2LongOpenHash.Int2LongOpenLRUMap;
import xbird.util.compress.CompressionCodec;
import xbird.util.compress.CompressorFactory;
import xbird.util.concurrent.cache.ConcurrentLongCache;
import xbird.util.lang.ArrayUtils;
import xbird.util.lang.HashUtils;
import xbird.util.lang.PrivilegedAccessor;
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
public class StringChunk implements IStringChunk {
    private static final long serialVersionUID = 5218513633893247581L;
    private static final Log LOG = LogFactory.getLog(StringChunk.class);

    private static final int DEFAULT_PAGES = 8;
    private static final float ENLARGE_PAGES_FACTOR = 1.4f;

    private static final int BLOCK_SHIFT = 12;
    private static final int DEFAULT_BLOCK_SIZE = 1 << BLOCK_SHIFT; // 2^12 = 4096 = 4KB
    private static final long DEFAULT_BLOCK_SIZE_L = DEFAULT_BLOCK_SIZE;
    private static final long BLOCK_MASK = DEFAULT_BLOCK_SIZE_L - 1L;

    //Caution: CHUNKED_THRESHOLD must be less than Character.MAX_VALUE (2^16=65536).
    private static final int CHUNKED_THRESHOLD = DEFAULT_BLOCK_SIZE / 8; // 512 byte
    private static final int DEFUALT_BIG_STRINGS_SIZE = (int) (DEFAULT_PAGES * ENLARGE_PAGES_FACTOR * 4);
    private static final long BIG_STRING_MASK = 1L;

    //--------------------------------------------
    // transient stuff

    private/* transient */final char[] tmpBuf = new char[CHUNKED_THRESHOLD];
    /** map for duplicate value management. key: chunk hash value, value: address */
    private/* transient */final Int2LongOpenLRUMap _hashv2addr = new Int2LongOpenLRUMap(1024);

    //--------------------------------------------
    // persistent stuff

    protected List<byte[]> _strPool;
    protected char[][] _cchunks;
    protected long _cpointer = 0;

    protected transient final CompressionCodec compressor;

    //--------------------------------------------

    public StringChunk() {
        this._strPool = new ArrayList<byte[]>(DEFUALT_BIG_STRINGS_SIZE);
        this._cchunks = new char[DEFAULT_PAGES][];
        this.compressor = CompressorFactory.createCodec();
    }

    public void close() throws IOException {
        this._strPool = null;
        this._cchunks = null;
    }

    public int getAndIncrementReferenceCount() {
        return 1; // dummy
    }

    public final long store(final char[] ch, final int start, final int length) {
        final long raddr;
        if(length >= CHUNKED_THRESHOLD) {
            final byte[] b = Primitives.toBytes(ch, start, length);
            raddr = storeStringChunk(b);
        } else {
            raddr = storeCharChunk(ch, start, length);
        }
        return raddr;
    }

    /**
     * Store a String and return the stored address.
     */
    public final long store(final String s) {
        final int strlen = s.length();
        if(strlen < CHUNKED_THRESHOLD) {
            s.getChars(0, strlen, tmpBuf, 0);
            return storeCharChunk(tmpBuf, 0, strlen);
        }
        final byte[] b = StringUtils.getBytes(s);
        return storeStringChunk(b);
    }

    protected final int storeStringChunk(final byte[] s) {
        final byte[] b = compressor.compress(s);
        return allocateStringChunk(b);
    }

    protected int allocateStringChunk(final byte[] s) {
        final int index = _strPool.size();
        _strPool.add(s);
        return stringKey(index);
    }

    private static final int stringKey(final int index) {
        assert (index >= 0) : index;
        return (index << 1) + 1;
    }

    protected final long storeCharChunk(final char[] ch, final int start, final int length) {
        final int hcode = HashUtils.hashCode(ch, start, length);
        final long haddr = _hashv2addr.get(hcode);
        if(haddr != -1L) {
            final char[] strInAddr = getChars(haddr);
            assert (strInAddr != null);
            if(ArrayUtils.equals(strInAddr, ch, start, length)) {
                return haddr;
            } else {
                _hashv2addr.remove(hcode);
            }
        }
        final long raddr = allocateCharChunk(ch, start, length);
        _hashv2addr.put(hcode, raddr);
        return raddr;
    }

    protected long allocateCharChunk(final char[] src, final int start, final int length) {
        assert (length < CHUNKED_THRESHOLD) : length;
        final int reqlen = length + 1; // actual length is store in first char. 
        final long lpage;
        if(((_cpointer & BLOCK_MASK) + reqlen) > DEFAULT_BLOCK_SIZE_L) {
            // spanning pages is not allowed, allocate in next chunk.
            lpage = (_cpointer >> BLOCK_SHIFT) + 1;
            _cpointer = lpage * DEFAULT_BLOCK_SIZE_L;
        } else {
            lpage = _cpointer >> BLOCK_SHIFT;
        }
        final int page = (int) lpage;
        if(page >= _cchunks.length) {
            enlarge((int) (_cchunks.length * ENLARGE_PAGES_FACTOR));
        }
        if(_cchunks[page] == null) {
            _cchunks[page] = new char[DEFAULT_BLOCK_SIZE];
        }
        final long lblock = _cpointer & BLOCK_MASK;
        final int block = (int) lblock;
        assert (length <= Character.MAX_VALUE) : length;
        _cchunks[page][block] = (char) length;
        System.arraycopy(src, start, _cchunks[page], block + 1, length);
        final long index = _cpointer;
        _cpointer += reqlen; // move ahead pointer
        return chunkKey(index);
    }

    private static final long chunkKey(final long index) {
        return index << 1;
    }

    private static final long indexOf(final long addr) {
        return addr >>> 1;
    }

    public void get(final long addr, final StringBuilder sb) {
        final long ptr = indexOf(addr);
        if((addr & BIG_STRING_MASK) != 0) { // is big string
            assert (ptr <= 0x7fffffffL) : ptr;
            final byte[] pooled = _strPool.get((int) ptr);
            final char[] c = decompress(compressor, pooled);
            sb.append(c);
        } else {
            assert (ptr <= _cpointer) : ptr;
            final long lp = ptr >> BLOCK_SHIFT;
            final int page = (int) lp;
            if(lp != page) {
                throw new IllegalStateException("Illegal page number: " + lp);
            }
            final int block = (int) (ptr & BLOCK_MASK);
            final char[] cc = _cchunks[page];
            final int length = cc[block]; // the first char is the length of the string
            sb.append(cc, block + 1, length);
        }
    }

    public String getString(final long addr) {
        final long ptr = indexOf(addr);
        final String ret;
        if((addr & BIG_STRING_MASK) != 0) { // is big string
            assert (ptr <= 0x7fffffffL) : ptr;
            final byte[] b = _strPool.get((int) ptr);
            final char[] c = decompress(compressor, b);
            ret = new String(c);
        } else {
            assert (ptr <= _cpointer) : ptr;
            final long lp = ptr >> BLOCK_SHIFT;
            final int page = (int) lp;
            if(lp != page) {
                throw new IllegalStateException("Illegal page number: " + lp);
            }
            final int block = (int) (ptr & BLOCK_MASK);
            final char[] cc = _cchunks[page];
            final int length = cc[block]; // the first char is the length of the string
            ret = new String(cc, block + 1, length);
        }
        return ret;
    }

    protected char[] getChars(final long addr) {
        final long ptr = indexOf(addr);
        final char[] ch;
        if((addr & BIG_STRING_MASK) != 0) { // is big string
            assert (ptr <= 0x7fffffffL) : ptr;
            final byte[] b = _strPool.get((int) ptr);
            ch = decompress(compressor, b);
        } else {
            assert (ptr <= _cpointer) : ptr;
            final long lp = ptr >> BLOCK_SHIFT;
            final int page = (int) lp;
            if(lp != page) {
                throw new IllegalStateException("Illegal page number: " + lp);
            }
            final int block = (int) (ptr & BLOCK_MASK);
            final char[] cc = _cchunks[page];
            final int length = cc[block]; // the first char is the length of the string
            final int from = block + 1;
            ch = ArrayUtils.copyOfRange(cc, from, from + length);
        }
        return ch;
    }

    public long getBufferAddress(long addr) {
        throw new UnsupportedOperationException();
    }

    protected void enlarge(final int pages) {
        final char[][] newchunks = new char[pages][];
        System.arraycopy(_cchunks, 0, newchunks, 0, _cchunks.length);
        this._cchunks = newchunks;
    }

    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(_strPool);
        out.writeObject(_cchunks);
        out.writeLong(_cpointer);
    }

    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this._strPool = (List<byte[]>) in.readObject();
        this._cchunks = (char[][]) in.readObject();
        this._cpointer = in.readLong();
    }

    public void flush(final DbCollection coll, final String docName, final PropertyMap docProps)
            throws IOException {
        final File chunkFile = getChunkFile(coll, docName);
        assert (!chunkFile.exists()) : "file already exists: " + chunkFile.getAbsolutePath();
        final int splen = _strPool.size();
        final Segments paged = new VarSegments(chunkFile, DescriptorType.hash);
        for(int i = 0; i < splen; i++) {// big string
            final byte[] b = _strPool.get(i);
            final int addr = stringKey(i);
            paged.write(addr, b);
        }
        _strPool.clear();
        final long lcclen = _cpointer >> BLOCK_SHIFT;
        assert (lcclen <= Integer.MAX_VALUE) : lcclen;
        final int cclen = Math.min((int) lcclen, _cchunks.length - 1);
        for(int i = 0; i <= cclen; i++) {
            final char[] c = _cchunks[i];
            final byte[] b = compress(compressor, c);
            final long addr = chunkKey(i * DEFAULT_BLOCK_SIZE_L);
            paged.write(addr, b);
            _cchunks[i] = null;
        }
        docProps.setProperty(KEY_STRPOOL_WRITTEN, String.valueOf(splen));
        docProps.setProperty(KEY_CHUNK_WRITTEN, String.valueOf(cclen));
        paged.flush(false);
        close();
        LOG.info("write string chunk file:" + chunkFile.getAbsolutePath());
    }

    private static final byte[] compress(final CompressionCodec compressor, final char[] c) {
        final byte[] b = Primitives.toBytes(c);
        return compressor.compress(b);
    }

    private static final char[] decompress(final CompressionCodec compressor, final byte[] v) {
        return compressor.decompressAsChars(v);
    }

    private static File getChunkFile(final DbCollection coll, final String docName) {
        final File baseDir = new File(coll.getAbsolutePath());
        assert (baseDir.exists() && baseDir.isDirectory());
        final File chunkFile = new File(baseDir, docName + STRING_CHUNK_FILE_SUFFIX);
        return chunkFile;
    }

    @Deprecated
    public static final class PagedStringChunk extends StringChunk {
        private static final long serialVersionUID = 7255938374376066703L;

        /**
         * Default cache size is 32MB.
         * <pre>
         * 512k * 8 * 2 = 8m
         * 512k * 16 * 2 = 16m
         * 512k * 32 * 2 = 32m
         * 512k * 64 * 2 = 64m
         * 512k * 128 * 2 = 128m
         * 512k * 256 * 2 = 256m
         * </pre>
         */
        private static final int STR_CHUNK_CACHES = Integer.getInteger("xbird.cchunk_caches", 32) * 2;

        private transient Segments _paged;
        private transient int spwritten, ccwritten;
        private transient int lccwritten = 0;
        private transient int paging_threshold = 20;
        private transient int enlarged = 0;
        private transient final ILongCache _cache;

        private SerializationContext _serContext = null;
        private final boolean _tranfered;
        private final AtomicInteger _refcount = new AtomicInteger(1);

        public PagedStringChunk() {//Externalizable
            super();
            this._cache = new ConcurrentLongCache(STR_CHUNK_CACHES);
            this._tranfered = true;
            this.spwritten = 0;
            this.ccwritten = 0;
        }

        public PagedStringChunk(VarSegments paged, int strPoolWritten, int charChunkWritten) {
            super();
            this._paged = paged;
            this._cache = new ConcurrentLongCache(STR_CHUNK_CACHES);
            this._tranfered = false;
            this.spwritten = strPoolWritten;
            this.ccwritten = charChunkWritten;
        }

        @Override
        public int getAndIncrementReferenceCount() {
            return _refcount.getAndIncrement();
        }

        @Override
        public void close() throws IOException {
            if(_refcount.decrementAndGet() == 0) {
                super.close();
                PrivilegedAccessor.unsafeSetField(this, PagedStringChunk.class, "_cache", null);
                _paged.close();
                this._paged = null;
            }
        }

        public void setSerializationContext(SerializationContext serContext) {
            this._serContext = serContext;
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this._cpointer = in.readLong();
            this._paged = RemoteVarSegments.read(in);
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            // super members
            out.writeLong(_cpointer);
            // this members
            if(_paged instanceof RemoteVarSegments) {
                ((RemoteVarSegments) _paged).writeExternal(out);
            } else {
                File dataFile = _paged.getFile();
                String filePath = dataFile.getAbsolutePath();
                RemoteVarSegments remoteSeg = new RemoteVarSegments(RemotePagingService.PORT, filePath, false);
                remoteSeg.writeExternal(out);
            }
        }

        @Override
        protected void enlarge(final int pages) {
            ++enlarged;
            if(enlarged >= paging_threshold) {
                try {
                    pagedOut();
                } catch (IOException e) {
                    throw new IllegalStateException("page out failed", e);
                }
                paging_threshold = Math.max(2, (int) (paging_threshold * 0.8));
                enlarged = 0;
            }
            super.enlarge(pages);
        }

        private void pagedOut() throws IOException {
            // flush big strings
            final int splen = _strPool.size();
            for(int i = 0; i < splen; i++) {// big string
                final byte[] b = _strPool.get(i);
                final int addr = stringKey(spwritten + i);
                _paged.write(addr, b);
            }
            _strPool.clear();
            this.spwritten += splen;
            // flush char chunks
            final long lcclen = _cpointer >> BLOCK_SHIFT;
            assert (lcclen <= Integer.MAX_VALUE) : lcclen;
            final int cclimit = (int) lcclen;
            for(int i = lccwritten; i < cclimit; i++) {
                final char[] c = _cchunks[i];
                assert (c != null) : "cchunk[" + i + "] does not contain element";
                final byte[] b = compress(compressor, c);
                final long addr = chunkKey((ccwritten + i) * DEFAULT_BLOCK_SIZE_L);
                _paged.write(addr, b);
                _cchunks[i] = null; // prompt gc of heap object
            }
            if(LOG.isDebugEnabled()) {
                if(splen > 0) {
                    LOG.debug("Paged out big string: " + spwritten + " - "
                            + (spwritten + splen - 1));
                }
                if(cclimit > lccwritten) {
                    LOG.debug("Paged out chunk chars: " + lccwritten + " - " + cclimit);
                }
            }
            this.lccwritten = cclimit;
            _paged.flush(false);
        }

        private int outpages() {
            final int splen = _strPool.size();
            final long cpages = _cpointer >> BLOCK_SHIFT;
            assert (cpages <= 0x7fffffffL) : cpages;
            final int cclen = (int) cpages - lccwritten;
            return splen + cclen;
        }

        @Override
        public void flush(final DbCollection coll, final String docName, final PropertyMap docProps)
                throws IOException {
            final int splen = _strPool.size();
            for(int i = 0; i < splen; i++) {// big string
                final byte[] b = _strPool.get(i);
                final int addr = stringKey(spwritten + i);
                _paged.write(addr, b);
            }
            if(LOG.isDebugEnabled()) {
                if(splen > 0) {
                    LOG.debug("Paged out big string: " + spwritten + " - "
                            + (spwritten + splen - 1));
                }
            }
            _strPool.clear();
            this.spwritten += splen;
            final long lcclen = _cpointer >> BLOCK_SHIFT;
            assert (lcclen <= Integer.MAX_VALUE) : lcclen;
            final int cclimit = Math.min((int) lcclen, _cchunks.length - 1);
            for(int i = lccwritten; i <= cclimit; i++) {
                final char[] c = _cchunks[i];
                if(c == null) {
                    continue;
                }
                final byte[] b = compress(compressor, c);
                final long addr = chunkKey((ccwritten + i) * DEFAULT_BLOCK_SIZE_L);
                _paged.write(addr, b);
                _cchunks[i] = null;
            }
            this.ccwritten += cclimit;
            if(LOG.isDebugEnabled()) {
                LOG.debug("Paged out chunk chars: " + lccwritten + " - " + cclimit
                        + ", next ccwritten: " + ccwritten);
            }

            docProps.setProperty(KEY_STRPOOL_WRITTEN, Integer.toString(spwritten));
            docProps.setProperty(KEY_CHUNK_WRITTEN, Integer.toString(ccwritten));
            _paged.flush(false);
            close();
        }

        @Override
        public void get(final long addr, final StringBuilder sb) {
            if(sb == null) {
                throw new IllegalArgumentException();
            }
            final long ptr = indexOf(addr);
            if((addr & BIG_STRING_MASK) != 0L) { // big string
                final byte[] b = getStringInternal(addr, ptr);
                final char[] cc = decompress(compressor, b);
                sb.append(cc);
            } else {
                final char[] cc = getCharChunkInternal(ptr);
                final int block = (int) (ptr & BLOCK_MASK);
                final int length = cc[block]; // the first char is the length of the string
                sb.append(cc, block + 1, length);
            }
        }

        private byte[] getStringInternal(final long addr, final long ptr) {
            final int splen = _strPool.size();
            final long spoffset = ptr - spwritten;
            final byte[] b;
            if((splen > 0) && (spoffset >= 0) && (spoffset <= splen)) {
                assert (spoffset <= 0x7fffffffL) : spoffset;
                b = _strPool.get((int) spoffset);
            } else {
                assert (addr <= 0x7fffffffL) : addr;
                b = prepareBigString((int) addr);
                assert (b != null);
            }
            return b;
        }

        @Override
        public String getString(final long addr) {
            final long ptr = indexOf(addr);
            final String ret;
            if((addr & BIG_STRING_MASK) != 0) { // is big string
                final byte[] b = getStringInternal(addr, ptr);
                final char[] cc = decompress(compressor, b);
                ret = new String(cc);
            } else {
                final char[] cc = getCharChunkInternal(ptr);
                final int block = (int) (ptr & BLOCK_MASK);
                final int length = cc[block]; // the first char is the length of the string
                ret = new String(cc, block + 1, length);
            }
            return ret;
        }

        private char[] getCharChunkInternal(final long ptr) {
            final long lp = ptr >> BLOCK_SHIFT;
            if(lp > 0x7fffffffL) {
                throw new IllegalStateException("Illegal page number: " + lp);
            }
            final int requiredPage = (int) lp;
            char[] cc;
            if(requiredPage >= _cchunks.length) {
                cc = prepareCharChunk(requiredPage);
            } else {
                cc = _cchunks[requiredPage];
                if(cc == null) {
                    cc = prepareCharChunk(requiredPage);
                }
            }
            return cc;
        }

        @Override
        protected char[] getChars(final long addr) {
            final long ptr = indexOf(addr);
            final char[] ch;
            if((addr & BIG_STRING_MASK) != 0L) { // is big string
                final byte[] b = getStringInternal(addr, ptr);
                ch = decompress(compressor, b);
            } else {
                final char[] cc = getCharChunkInternal(ptr);
                final int block = (int) (ptr & BLOCK_MASK);
                final int length = cc[block]; // the first char is the length of the string
                final int from = block + 1;
                ch = ArrayUtils.copyOfRange(cc, from, from + length);
            }
            return ch;
        }

        private byte[] prepareBigString(final int addr) {
            assert ((addr & BIG_STRING_MASK) != 0L);
            byte[] b = (byte[]) _cache.get(addr);
            if(b == null) {// read from disk
                if(_tranfered) {
                    b = readv(addr, false, _serContext);
                } else {
                    b = read(addr);
                }
                assert (b != null) : "Illegal addr: " + addr;
                _cache.putIfAbsent(addr, b);
            }
            return b;
        }

        private char[] prepareCharChunk(final int page) {
            final long addr = chunkKey(page * DEFAULT_BLOCK_SIZE_L);
            char[] cc = (char[]) _cache.get(addr);
            if(cc == null) {// read from disk
                final byte[] v;
                if(_tranfered) {
                    v = readv(addr, true, _serContext);
                } else {
                    v = read(addr);
                }
                assert (v != null) : "Illegal addr: " + addr + ", page: " + page + ", ccwritten:"
                        + ccwritten + ", lccwritten:" + lccwritten;
                cc = decompress(compressor, v);
                _cache.putIfAbsent(addr, cc);
            }
            return cc;
        }

        private byte[] read(final long addr) {
            try {// read from disk
                return _paged.read(addr);
            } catch (IOException e) {
                throw new IllegalStateException("read from disk failed", e);
            }
        }

        private byte[] readv(final long addr, boolean charChunk, SerializationContext serContext) {
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
                tmp = _paged.readv(addrs);
            } catch (IOException e) {
                throw new IllegalStateException("read from disk failed", e);
            }
            for(int i = 1; i < nPages; i++) {
                final long tmpAddr = addrs[i];
                if((tmpAddr & BIG_STRING_MASK) != 0L) {//is big string?
                    _cache.putIfAbsent(tmpAddr, tmp[i]);
                } else {
                    char[] cc = decompress(compressor, tmp[i]);
                    _cache.putIfAbsent(tmpAddr, cc);
                }
            }
            return tmp[0];
        }

        @Override
        public long getBufferAddress(long addr) {
            final long ptr = indexOf(addr);
            if((addr & BIG_STRING_MASK) != 0L) { // big string
                return ptr;
            } else {
                long pageId = ptr >> BLOCK_SHIFT;
                return chunkKey(pageId * DEFAULT_BLOCK_SIZE_L);
            }
        }

        @Override
        protected int allocateStringChunk(final byte[] s) {
            final int index = _strPool.size();
            _strPool.add(s);
            return stringKey(spwritten + index);
        }

        private int page_out_threshold = 10000;

        @Override
        protected long allocateCharChunk(final char[] src, final int start, final int length) {
            if((length >= CHUNKED_THRESHOLD) || (length > Character.MAX_VALUE)) {
                throw new IllegalArgumentException("Illegal character size: " + length);
            }
            final int reqlen = length + 1; // actual length is store in first char. 
            final int page;
            if(((_cpointer & BLOCK_MASK) + reqlen) > DEFAULT_BLOCK_SIZE_L) {
                // spanning pages is not allowed, allocate in next chunk.
                final long lp = (_cpointer >> BLOCK_SHIFT) + 1L;
                assert (lp <= 0x7fffffffL) : "Assigned page size exceeds limit: " + lp;
                page = (int) lp;
                _cpointer = lp * DEFAULT_BLOCK_SIZE_L;
            } else {
                final long lp = _cpointer >> BLOCK_SHIFT;
                assert (lp <= 0x7fffffffL) : "Assigned page size exceeds limit: " + lp;
                page = (int) lp;
            }
            assert (page >= 0) : "Illegal page '" + page + "' with cpointer '" + _cpointer + '\'';
            if(page >= _cchunks.length) {
                enlarge((int) (_cchunks.length * ENLARGE_PAGES_FACTOR));
            }
            if(_cchunks[page] == null) {
                if(page < lccwritten) {
                    _cchunks[page] = prepareCharChunk(page);
                } else {
                    final int outs = outpages();
                    if(outs > page_out_threshold) {
                        try {
                            pagedOut();
                        } catch (IOException e) {
                            throw new IllegalStateException("page out failed", e);
                        }
                        if(page_out_threshold > 5000) {
                            page_out_threshold -= 100;
                        }
                    }
                    _cchunks[page] = new char[DEFAULT_BLOCK_SIZE];
                }
            }
            final long lblock = _cpointer & BLOCK_MASK;
            assert (lblock <= Integer.MAX_VALUE) : lblock;
            final int block = (int) lblock;
            _cchunks[page][block] = (char) length;
            System.arraycopy(src, start, _cchunks[page], block + 1, length);
            final long index = _cpointer;
            _cpointer += reqlen; // move ahead pointer
            return chunkKey((ccwritten * DEFAULT_BLOCK_SIZE_L) + index);
        }
    }
}
