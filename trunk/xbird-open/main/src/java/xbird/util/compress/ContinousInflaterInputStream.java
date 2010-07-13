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
package xbird.util.compress;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

/**
 * InflaterInputStream that deals with a continuous stream of zipped blocks.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ContinousInflaterInputStream extends InflaterInputStream {

    private boolean closed = false;
    // this flag is set to true after EOF has reached
    private boolean reachEOF = false;

    public ContinousInflaterInputStream(InputStream in) {
        super(in);
    }

    public ContinousInflaterInputStream(InputStream in, Inflater inf) {
        super(in, inf);
    }

    public ContinousInflaterInputStream(InputStream in, Inflater inf, int size) {
        super(in, inf, size);
    }

    private void ensureOpen() throws IOException {
        if(closed) {
            throw new IOException("Stream closed");
        }
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        ensureOpen();
        if(b == null) {
            throw new NullPointerException();
        } else if(off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if(len == 0) {
            return 0;
        }
        try {
            int n;
            while((n = inf.inflate(b, off, len)) == 0) {
                if(inf.needsDictionary()) {
                    this.reachEOF = true;
                    return -1;
                }
                if(inf.finished()) {
                    int remaining = inf.getRemaining();
                    if(remaining > 0) { // restart with these remaining bytes
                        inf.reset();
                        inf.setInput(buf, this.len - remaining, remaining);
                    } else {
                        int rlen;
                        if((rlen = fill2()) == -1) {
                            this.reachEOF = true;
                            return -1;
                        }
                        inf.reset();
                        inf.setInput(buf, 0, rlen);
                    }
                    continue;
                }
                if(inf.needsInput()) {
                    fill();
                }
            }
            return n;
        } catch (DataFormatException e) {
            String s = e.getMessage();
            throw new ZipException(s != null ? s : "Invalid ZLIB data format");
        }
    }

    /**
     * Fills input buffer with more data to decompress.
     */
    private int fill2() throws IOException {
        len = in.read(buf, 0, buf.length);
        if(len == -1) {
            return -1;
        }
        inf.setInput(buf, 0, len);
        return len;
    }

    /**
     * Returns 0 after EOF has been reached, otherwise always return 1.
     * <p>
     * Programs should not count on this method to return the actual number
     * of bytes that could be read without blocking.
     *
     * @return     1 before EOF and 0 after EOF.
     * @exception  IOException  if an I/O error occurs.
     * 
     */
    public int available() throws IOException {
        ensureOpen();
        if(reachEOF) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Closes this input stream and releases any system resources associated
     * with the stream.
     * @exception IOException if an I/O error has occurred
     */
    @Override
    public void close() throws IOException {
        if(!closed) {
            inf.end();
            in.close();
            this.closed = true;
        }
    }
}
