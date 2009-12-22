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
package xbird.util.csv;

import java.io.IOException;
import java.io.PushbackReader;

import javax.annotation.Nonnull;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class SimpleCvsReader implements CvsReader {

    private final PushbackReader reader;
    protected final char filedSeparator;
    protected final char quoteChar;
    private final StringBuilder lineBuf;

    public SimpleCvsReader(@Nonnull PushbackReader reader, char filedSeparator, char quoteChar) {
        this.reader = reader;
        this.filedSeparator = filedSeparator;
        this.quoteChar = quoteChar;
        this.lineBuf = new StringBuilder(128);
    }

    public final String getNextLine() throws IOException {
        lineBuf.setLength(0);
        return CsvUtils.readLine(reader, quoteChar, lineBuf);
    }

    public final String[] readNext() throws IOException {
        String line = getNextLine();
        if(line == null) {
            return null;
        }
        return CsvUtils.parseLine(line, filedSeparator, quoteChar);
    }

    public final String[] parseLine(final String line) throws IOException {
        return CsvUtils.parseLine(line, filedSeparator, quoteChar);
    }

    public void close() throws IOException {
        reader.close();
        lineBuf.setLength(0);
    }

}
