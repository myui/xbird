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
package xbird.util.nio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ByteBufferInputStream extends InputStream {

    private final NioSelectorPool selectorPool;
    private final ByteBuffer byteBuffer;
    
    private SelectionKey key;
    private int readTimeout = 30000;

    public ByteBufferInputStream(@CheckForNull final ByteBuffer byteBuffer, @CheckForNull final SelectionKey key, @Nonnull NioSelectorPool pool) {
        if(byteBuffer == null) {
            throw new IllegalArgumentException();
        }
        if(key == null) {
            throw new IllegalArgumentException();
        }
        this.selectorPool = pool;
        this.byteBuffer = byteBuffer;
        this.key = key;
    }
    
    public void setSelectionKey(SelectionKey key) {
        this.key = key;
    }

    public void setReadTimeout(int mills) {
        this.readTimeout = mills;
    }

    @Override
    public int available() throws IOException {
        return byteBuffer.remaining();
    }

    @Override
    public void close() throws IOException {}

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int read() throws IOException {
        if(!byteBuffer.hasRemaining()) {
            byteBuffer.clear();
            int eof = doRead();
            if(eof <= 0) {
                return -1;
            }
        }
        return byteBuffer.hasRemaining() ? (byteBuffer.get() & 0xff) : -1;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if(!byteBuffer.hasRemaining()) {
            byteBuffer.clear();
            int eof = doRead();
            if(eof <= 0) {
                return -1;
            }
        }
        int remaining = byteBuffer.remaining();
        if(len > remaining) {
            len = remaining;
        }
        byteBuffer.get(b, off, len);
        return len;
    }

    private int doRead() throws IOException {
        int bytesRead = NIOUtils.readWithTemporarySelector(key.channel(), byteBuffer, readTimeout, selectorPool);
        byteBuffer.flip();
        return bytesRead;
    }

}
