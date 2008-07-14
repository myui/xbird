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
package xbird.util.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class FastMultiByteArrayInputStream extends InputStream {

    private final byte[][] _buffers;
    private final int _blocksize;

    private int _pos;
    private int _count;

    private byte[] _curBlock;
    private int _curBlockIdx;
    private int _curBlockOffset;

    private boolean _cleanable = false;

    // TODO variable block size
    public FastMultiByteArrayInputStream(byte[][] buffers, int blockSize, int totalSize) {
        if(buffers == null) {
            throw new IllegalArgumentException();
        }
        this._buffers = buffers;
        this._blocksize = blockSize;
        this._pos = 0;
        this._count = totalSize;
        this._curBlock = buffers[0];
        this._curBlockIdx = 0;
        this._curBlockOffset = 0;
    }

    public void setCleanable(boolean cleanable) {
        this._cleanable = cleanable;
    }

    public int read() {
        final byte[] block = getCurBlock(_pos++);
        return (_pos < _count) ? (block[_curBlockOffset++] & 0xff) : -1;
    }

    @Override
    public int read(final byte b[], int off, int len) {
        if(b == null) {
            throw new NullPointerException();
        } else if(off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if(_pos >= _count) {
            return -1;
        }
        if(_pos + len > _count) {
            len = _count - _pos;
        }
        if(len <= 0) {
            return 0;
        }
        final int limit = _pos + len;
        for(int n = _pos; n < limit;) {
            final byte[] block = getCurBlock(n);
            final int numleft = _blocksize - _curBlockOffset;
            final int copylen = Math.min(limit - n, numleft);
            System.arraycopy(block, _curBlockOffset, b, off, copylen);
            _curBlockOffset += copylen;
            off += copylen;
            n += copylen;
        }
        _pos += len;
        return len;
    }

    private byte[] getCurBlock(final int pos) {
        if(_curBlockOffset < _blocksize) {
            return _curBlock;
        } else {
            if(_cleanable) {
                _buffers[_curBlockIdx] = null;
            }
            final int nextBlockIdx = ++_curBlockIdx;
            final byte[] block = _buffers[nextBlockIdx];
            this._curBlock = block;
            this._curBlockOffset = 0;
            return block;
        }
    }

    @Override
    public long skip(long n) {
        if(_pos + n > _count) {
            n = _count - _pos;
        }
        if(n < 0) {
            return 0;
        }
        _pos += n;
        return n;
    }

    @Override
    public int available() {
        return _count - _pos;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void mark(int readAheadLimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {}

}
