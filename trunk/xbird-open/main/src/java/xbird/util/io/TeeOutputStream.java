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
public final class TeeOutputStream extends OutputStream {

    private final OutputStream first, sec;

    public TeeOutputStream(OutputStream first, OutputStream sec) {
        this.first = first;
        this.sec = sec;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        first.write(b, off, len);
        sec.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        first.write(b);
        sec.write(b);
    }

    @Override
    public void write(int b) throws IOException {
        first.write(b);
        sec.write(b);
    }

    @Override
    public void flush() throws IOException {
        first.flush();
        sec.flush();
    }

    @Override
    public void close() throws IOException {
        try {
            first.close();
        } finally {
            sec.close();
        }
    }

}
