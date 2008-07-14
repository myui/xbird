/*
 * @(#)$Id: CountingOutputStream.java 3619 2008-03-26 07:23:03Z yui $
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
import java.io.OutputStream;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405 AT gmail.com)
 */
public class CountingOutputStream extends OutputStream {

    private final OutputStream proxy;
    private long count = 0;

    public CountingOutputStream(final OutputStream out) {
        assert (out != null);
        this.proxy = out;
    }

    public void reset() {
        this.count = 0;
    }

    public long getCount() {
        if(count >= 0 && count <= Long.MAX_VALUE) {
            return count;
        }
        throw new IllegalStateException("out bytes exceeds Long.MAX_VALUE: " + count);
    }

    @Override
    public void write(int b) throws IOException {
        ++count; // just 1 byte is wrote
        proxy.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        count += len;
        proxy.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        count += b.length;
        proxy.write(b);
    }

    @Override
    public void close() throws IOException {
        proxy.close();
    }

    @Override
    public void flush() throws IOException {
        proxy.flush();
    }

}
