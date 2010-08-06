/*
 * @(#)$Id:BasicStringChunk.java 2335 2007-07-17 04:14:15Z yui $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.storage.DbCollection;
import xbird.storage.io.Segments;
import xbird.storage.io.VarSegments;
import xbird.storage.io.VarSegments.DescriptorType;
import xbird.util.collections.ints.Int2IntHash.Int2IntLRUMap;
import xbird.util.compress.CompressionCodec;
import xbird.util.compress.CompressorFactory;
import xbird.util.hashes.HashUtils;
import xbird.util.lang.ArrayUtils;
import xbird.util.primitive.Primitives;
import xbird.util.resource.PropertyMap;
import xbird.util.string.StringUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405 AT gmail.com)
 */
public final class BasicStringChunk implements IStringChunk {
    private static final long serialVersionUID = -2802997860361606643L;
    private static final Log LOG = LogFactory.getLog(StringChunk.class);

    private static final int DEFAULT_PAGES = 8;
    private static final float ENLARGE_PAGES_FACTOR = 1.4f;

    private static final int BLOCK_SHIFT = 12;
    private static final int BLOCK_SIZE = 1 << BLOCK_SHIFT; // 2^12 = 4096 = 4KB
    private static final long BLOCK_MASK = BLOCK_SIZE - 1L;

    //Caution: CHUNKED_THRESHOLD must be less than Character.MAX_VALUE (2^16=65536).
    private static final int CHUNKED_THRESHOLD = BLOCK_SIZE / 8; // 512 byte
    private static final int MAX_CHUNK_POINTER = (1 << 31) - 1;
    private static final int DEFUALT_BIG_STRINGS_SIZE = (int) (DEFAULT_PAGES * ENLARGE_PAGES_FACTOR * 4);
    private static final long BIG_STRING_MASK = 1L;

    //  --------------------------------------------
    // transient stuff

    private final char[] tmpBuf = new char[CHUNKED_THRESHOLD];
    /** map for duplicate value management. key: chunk hash value, value: address */
    private transient final Int2IntLRUMap _hashv2addr = new Int2IntLRUMap(1024);

    //--------------------------------------------
    // persistent stuff

    private List<String> _strPool;
    private char[][] _cchunks;
    private long _cpointer = 0;

    private final CompressionCodec compressor;

    //--------------------------------------------    

    public BasicStringChunk() {
        this._strPool = new ArrayList<String>(DEFUALT_BIG_STRINGS_SIZE);
        this._cchunks = new char[DEFAULT_PAGES][];
        this._cchunks[0] = new char[BLOCK_SIZE];
        this.compressor = CompressorFactory.createCodec();
    }

    //--------------------------------------------    

    public void get(final long addr, final StringBuilder sb) {
        final long ptr = indexOf(addr);
        if((addr & BIG_STRING_MASK) != 0) { // is big string
            assert (ptr <= 0x7fffffffL) : ptr;
            final String s = _strPool.get((int) ptr);
            sb.append(s);
        } else {
            final int page = (int) ptr >> BLOCK_SHIFT;
            final int block = (int) (ptr & BLOCK_MASK);
            final char[] cc = _cchunks[page];
            final int length = cc[block]; // the first char is the length of the string
            sb.append(cc, block + 1, length);
        }
    }

    public String getString(final long addr) {
        final long ptr = indexOf(addr);
        final String ret;
        if((addr & BIG_STRING_MASK) != 0L) { // is big string
            ret = _strPool.get((int) ptr);
        } else {
            final int page = (int) ptr >> BLOCK_SHIFT;
            final int block = (int) (ptr & BLOCK_MASK);
            final char[] cp = _cchunks[page];
            final int length = cp[block]; // the first char is the length of the string
            ret = new String(cp, block + 1, length);
        }
        return ret;
    }

    public long getBufferAddress(long addr) {
        throw new UnsupportedOperationException();
    }

    public long store(final char[] ch, final int start, final int length) {
        final int raddr;
        if(length >= CHUNKED_THRESHOLD) {
            raddr = allocateStringChunk(new String(ch, start, length));
        } else {
            raddr = storeCharChunk(ch, start, length);
        }
        return raddr;
    }

    public long store(final String s) {
        assert (s != null);
        final int strlen = s.length();
        if(strlen < CHUNKED_THRESHOLD) {
            s.getChars(0, strlen, tmpBuf, 0);
            return store(tmpBuf, 0, strlen);
        }
        return allocateStringChunk(s);
    }

    private int allocateStringChunk(final String s) {
        final int index = _strPool.size();
        _strPool.add(s);
        return stringKey(index);
    }

    private static int stringKey(final int index) {
        return (index << 1) + 1;
    }

    private int storeCharChunk(final char[] ch, final int start, final int length) {
        final int hcode = HashUtils.hashCode(ch, start, length);
        final int haddr = _hashv2addr.get(hcode);
        if(haddr != -1) {
            final char[] strInAddr = getStoredChars(haddr);
            assert (strInAddr != null);
            if(ArrayUtils.equals(strInAddr, ch, start, length)) {
                return haddr;
            } else {
                _hashv2addr.remove(hcode);
            }
        }
        final int raddr = allocateCharChunk(ch, start, length);
        _hashv2addr.put(hcode, raddr);
        return raddr;
    }

    @Deprecated
    private char[] getStoredChars(final int addr) {
        final long ptr = indexOf(addr);
        final long lp = ptr >> BLOCK_SHIFT;
        if(lp > 0x7fffffffL) {
            throw new IllegalStateException("Illegal page number: " + lp);
        }
        final int page = (int) lp;
        final char[] cc = _cchunks[page];
        final int block = (int) (ptr & BLOCK_MASK);
        final int length = cc[block]; // the first char is the length of the string
        final int from = block + 1;
        return ArrayUtils.copyOfRange(cc, from, from + length);
    }

    private int allocateCharChunk(final char[] src, final int start, final int length) {
        assert (length < CHUNKED_THRESHOLD) : length;
        final int reqlen = length + 1; // actual length is store in first char. 
        final long lpage;
        if(((_cpointer & BLOCK_MASK) + reqlen) > BLOCK_SIZE) {
            // spanning pages is not allowed, allocate in next chunk.
            lpage = (_cpointer >> BLOCK_SHIFT) + 1;
            if(lpage > Integer.MAX_VALUE) {
                throw new IllegalStateException("Assained page exceeds system's limit: " + lpage);
            }
            this._cpointer = lpage * BLOCK_SIZE;
        } else {
            lpage = _cpointer >> BLOCK_SHIFT;
        }
        final int page = (int) lpage;
        if(page >= _cchunks.length) {
            enlarge((int) (_cchunks.length * ENLARGE_PAGES_FACTOR));
        }
        if(_cchunks[page] == null) {
            _cchunks[page] = new char[BLOCK_SIZE];
        }
        final long lblock = _cpointer & BLOCK_MASK;
        if(lblock > Integer.MAX_VALUE) {
            throw new IllegalStateException("Assained block exceeds system's limit: " + lblock);
        }
        final int block = (int) lblock;
        assert (length < Character.MAX_VALUE) : length;
        _cchunks[page][block] = (char) length;
        System.arraycopy(src, start, _cchunks[page], block + 1, length);
        final long index = _cpointer;
        _cpointer += reqlen; // move ahead pointer
        return chunkKey(index);
    }

    private static int chunkKey(final long index) {
        if(index > MAX_CHUNK_POINTER) {
            throw new IllegalStateException("Assained key '" + index + "' exceeds system's limit '"
                    + MAX_CHUNK_POINTER + '\'');
        }
        return (int) index << 1;
    }

    private static long indexOf(final long addr) {
        return addr >>> 1;
    }

    private void enlarge(final int pages) {
        final char[][] newchunks = new char[pages][];
        System.arraycopy(_cchunks, 0, newchunks, 0, _cchunks.length);
        this._cchunks = newchunks;
    }

    public void flush(final DbCollection coll, final String docName, final PropertyMap docProps)
            throws IOException {
        final File chunkFile = getChunkFile(coll, docName);
        assert (!chunkFile.exists()) : "file already exists: " + chunkFile.getAbsolutePath();
        final int splen = _strPool.size();
        final Segments paged = new VarSegments(chunkFile, DescriptorType.hash);
        for(int i = 0; i < splen; i++) {// big string
            final String s = _strPool.get(i);
            final byte[] b = compressor.compress(StringUtils.getBytes(s));
            final int addr = stringKey(i);
            paged.write(addr, b);
        }
        _strPool.clear();
        final long lcclen = _cpointer >> BLOCK_SHIFT;
        assert (lcclen <= Integer.MAX_VALUE) : lcclen;
        final int cclen = Math.min((int) lcclen, _cchunks.length - 1);
        for(int i = 0; i <= cclen; i++) {
            final char[] c = _cchunks[i];
            assert (c != null);
            final byte[] b = compress(compressor, c);
            final int addr = chunkKey(i * BLOCK_SIZE);
            paged.write(addr, b);
            _cchunks[i] = null;
        }
        docProps.setProperty(KEY_STRPOOL_WRITTEN, String.valueOf(splen));
        docProps.setProperty(KEY_CHUNK_WRITTEN, String.valueOf(cclen));
        paged.flush(false);
        close();
        LOG.info("write string chunk file:" + chunkFile.getAbsolutePath());
    }

    private static File getChunkFile(final DbCollection coll, final String docName) {
        final File baseDir = new File(coll.getAbsolutePath());
        assert (baseDir.exists() && baseDir.isDirectory());
        final File chunkFile = new File(baseDir, docName + STRING_CHUNK_FILE_SUFFIX);
        return chunkFile;
    }

    private final byte[] compress(final CompressionCodec compressor, final char[] c) {
        final byte[] b = Primitives.toBytes(c);
        return compressor.compress(b);
    }

    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this._strPool = (List<String>) in.readObject();
        this._cchunks = (char[][]) in.readObject();
        this._cpointer = in.readLong();
    }

    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(_strPool);
        out.writeObject(_cchunks);
        out.writeLong(_cpointer);
    }

    public void close() throws IOException {
        this._strPool = null;
        this._cchunks = null;
        _hashv2addr.close();
    }

    public int getAndIncrementReferenceCount() {
        return 1; // dummy
    }

}
