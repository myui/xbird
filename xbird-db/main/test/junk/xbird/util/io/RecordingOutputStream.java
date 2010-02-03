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
import java.io.OutputStream;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RecordingOutputStream extends OutputStream {

    private final OutputStream delegate;
    private final FastByteArrayOutputStream buffered;

    public RecordingOutputStream(OutputStream out) {
        this(out, 16384);
    }

    public RecordingOutputStream(OutputStream out, int bufsize) {
        this.delegate = out;
        this.buffered = new FastByteArrayOutputStream(bufsize);
    }

    public byte[] toByteArray() {
        return buffered.toByteArray();
    }

    @Override
    public void write(int b) throws IOException {
        buffered.write(b);
        delegate.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        buffered.write(b, off, len);
        delegate.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        buffered.write(b);
        delegate.write(b);
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
        //buffered.flush();
    }

    @Override
    public void close() throws IOException {
        buffered.reset();
        buffered.close();
        try {
            delegate.flush();
        } finally {
            delegate.close();
        }
    }

}
