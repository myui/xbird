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
public final class FastBufferedInputStream extends InputStream {

    private static final int DEFAULT_BUFSIZE = 1024 * 8;

    private final int _bufsize;
    private InputStream _in;
    private byte[] _buffer;

    /** The current position in the buffer. */
    private int _pos = 0;

    /** The number of available bytes starting from {@link #_pos}. */
    private int _avail = 0;

    public FastBufferedInputStream(final InputStream in) {
        this(in, DEFAULT_BUFSIZE);
    }

    public FastBufferedInputStream(final InputStream in, final int bufsize) {
        this._bufsize = bufsize;
        this._in = in;
        this._buffer = new byte[bufsize];
    }

    @Override
    public int read() throws IOException {
        if(_avail == 0) {
            final int avail = read(_buffer);
            if(avail <= 0) {
                this._avail = -1;
                return -1;
            }
            this._avail = avail;
            this._pos = 0;
        }
        _avail--;
        return _buffer[_pos++] & 0xFF;
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return readInternal(b, 0, b.length);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if(off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        return readInternal(b, off, len);
    }

    private int readInternal(final byte[] b, final int off, final int len) throws IOException {
        if(len == 0) {
            return 0;
        }
        if(len <= _avail) {
            System.arraycopy(_buffer, _pos, b, off, len);
            _pos += len;
            _avail -= len;
            return len;
        } else {
            if(_avail == -1) {
                return -1;
            }
            int offset = off;
            int remaining = len;
            int avail = _avail;
            if(avail > 0) {
                System.arraycopy(_buffer, _pos, b, offset, avail);
                offset += avail;
                remaining -= avail;
                this._avail = 0;
            }
            final int filled = fillBuffer();
            if(filled <= 0) {
                return avail;
            } else {
                return avail + readInternal(b, offset, remaining);
            }
        }
    }

    private int fillBuffer() throws IOException {
        if(_in == null) {
            throw new NullPointerException("Underlying stream is already closed");
        }
        assert (_avail == 0) : _avail;
        this._pos = 0;
        final int avail = _in.available();
        if(avail <= 0) {
            this._avail = -1;
            return -1;
        }
        final int tryread = Math.min(avail, _bufsize);
        final int actread = _in.read(_buffer, 0, tryread);
        this._avail = actread;
        return actread;
    }

    @Override
    public long skip(final long n) throws IOException {
        if(_avail == -1) {
            return 0;
        }
        if(n <= _avail) {
            int i = (int) n;
            _pos += i;
            _avail -= i;
            return n;
        }
        int count = 0;
        for(; count < n && read() != -1; count++) {
        }
        return count;
    }

    @Override
    public int available() throws IOException {
        if(_in == null) {
            return 0;
        }
        if(_avail == -1) {
            return 0;
        }
        return _in.available() + _avail;
    }

    @Override
    public void close() throws IOException {
        //if(_in == null) return;
        if(_in != System.in) {
            _in.close();
        }
        this._in = null;
        this._buffer = null;
    }

    @Override
    public void reset() throws IOException {
        _avail = _pos = 0;
    }

}
