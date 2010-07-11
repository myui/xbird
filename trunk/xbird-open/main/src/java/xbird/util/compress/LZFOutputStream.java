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
import java.io.OutputStream;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class LZFOutputStream extends OutputStream {

    public static final int MAGIC = ('L' << 24) | ('Z' << 16) | ('F' << 8) | '0';
    public static final int DEFAULT_BUF_SIZE = 4096;

    private final LZFCodec _codec = new LZFCodec();

    private final OutputStream _out;
    private final int _buflen;
    private final byte[] _buffer;
    private int _pos = 0;

    public LZFOutputStream(OutputStream out) {
        this(out, DEFAULT_BUF_SIZE);
    }

    public LZFOutputStream(OutputStream out, int buflen) {
        this(out, buflen, false);
    }

    public LZFOutputStream(OutputStream out, int buflen, boolean noMagic) {
        this._out = out;
        this._buflen = buflen;
        this._buffer = new byte[buflen];
        if(!noMagic) {
            try {
                writeInt(MAGIC, out);
            } catch (IOException e) {
                throw new IllegalStateException("failed to write a magic field", e);
            }
        }
    }

    @Override
    public void write(int b) throws IOException {
        if(_pos >= _buflen) {
            compressAndWrite(_buffer, _pos);
            _pos = 0;
        }
        _buffer[_pos++] = (byte) b;
    }

    @Override
    public void flush() throws IOException {
        compressAndWrite(_buffer, _pos);
        _pos = 0;
        _out.flush();
    }

    @Override
    public void close() throws IOException {
        flush();
        _out.close();
    }

    private void compressAndWrite(final byte[] buff, final int len) throws IOException {
        assert (len <= DEFAULT_BUF_SIZE) : buff;
        if(len > 0) {
            final byte[] compressed = _codec.compress(buff, len);
            final int clen = compressed.length;
            if(clen >= len) { // no compress
                writeInt(-len, _out);
                _out.write(buff, 0, len);
            } else {// compress
                writeInt(clen, _out);
                _out.write(compressed, 0, clen);
            }
        }
    }

    private void writeInt(final int x, final OutputStream out) throws IOException {
        out.write((byte) (x >>> 24));
        out.write((byte) (x >>> 16));
        out.write((byte) (x >>> 8));
        out.write((byte) (x >>> 0));
    }

}
