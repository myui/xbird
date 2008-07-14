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
package xbird.util.compress;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class LZFInputStream extends InputStream {

    private final LZFCodec _codec = new LZFCodec();

    private final InputStream _in;
    private byte[] _buffer;
    private int _pos = 0;

    public LZFInputStream(InputStream in) throws IOException {
        this(in, LZFOutputStream.DEFAULT_BUF_SIZE);
    }

    public LZFInputStream(InputStream in, int buflen) throws IOException {
        if(readInt(in) != LZFOutputStream.MAGIC) {
            throw new IOException("Not a LZF Stream");
        }
        this._in = in;
    }

    @Override
    public int read() throws IOException {
        fillBuffer();
        final byte[] buf = _buffer;
        if(_pos >= buf.length) {
            return -1;
        }
        return buf[_pos++] & 255;
    }

    private void fillBuffer() throws IOException {
        final byte[] buffer = _buffer;
        if(buffer != null && _pos < buffer.length) {
            return;
        }
        final InputStream in = _in;
        int len = readInt(in);
        assert (len <= LZFOutputStream.DEFAULT_BUF_SIZE) : len;
        if(len < 0) { // not compressed
            len = -len;
            byte[] dest = ensureBuffer(buffer, len);
            readFully(in, len, dest);
            this._buffer = dest;
        } else {
            byte[] dest = ensureBuffer(buffer, len);
            readFully(in, len, dest);
            byte[] uncompressed = _codec.decompress(dest);
            this._buffer = uncompressed;
        }
        this._pos = 0;
    }

    private static int readInt(final InputStream in) throws IOException {
        int x = ((in.read() & 0xFF) << 24);
        x += ((in.read() & 0xFF) << 16);
        x += ((in.read() & 0xFF) << 8);
        x += (in.read() & 0xFF);
        return x;
    }

    private static byte[] ensureBuffer(final byte[] buf, final int required) {
        return (buf == null || buf.length < required) ? new byte[required] : buf;
    }

    private static void readFully(final InputStream in, final int len, final byte[] dest)
            throws IOException {
        in.read(dest, 0, len);
    }

    @Override
    public void close() throws IOException {
        _in.close();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

}
