/*
 * @(#)$Id: CsvWriter.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.*;

import xbird.util.lang.PrintUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class CsvWriter {

    private static final char QUOTE = '\"';

    private char separator = ',';
    private String lineSeparator = System.getProperty("line.separator");
    private final Writer _writer;

    public CsvWriter(File file) {
        this(file, System.getProperty("file.encoding"));
    }

    public CsvWriter(File file, String encoding) {
        try {
            this._writer = new FileWriter(file);
        } catch (IOException e) {
            throw new IllegalStateException("failed to writer to file: " + file.getAbsolutePath(), e);
        }
    }

    public CsvWriter(Writer writer) {
        this._writer = writer;
    }

    public void setSeparator(char chr) {
        this.separator = chr;
    }

    public void writeRow(String... cols) {
        assert (cols != null);
        final int collen = cols.length;
        for(int i = 0; i < collen; i++) {
            if (i > 0) {
                write(separator);
            }
            final String data = cols[i];
            if (data != null) {
                final boolean doQuote = needQuotes(data);
                if (doQuote) {
                    write(QUOTE);
                    write(quoteData(data));
                    write(QUOTE);
                } else {
                    write(data);
                }
            }
        }
        write(lineSeparator);
    }

    public void close() {
        try {
            _writer.close();
        } catch (IOException e) {
            // fall through within error message
            System.err.print(PrintUtils.prettyPrintStackTrace(e));
        }
    }

    private void write(String s) {
        try {
            _writer.write(s);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void write(char c) {
        try {
            _writer.write(c);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String quoteData(String data) {
        final StringBuilder buf = new StringBuilder((int) (data.length() * 1.2));
        final int strlen = data.length();
        for(int i = 0; i < strlen; i++) {
            final char c = data.charAt(i);
            buf.append(c);
            switch (c) {
                case QUOTE:
                    buf.append(QUOTE);
                    break;
                default:
                    break;
            }
        }
        return buf.toString();
    }

    private boolean needQuotes(String data) {
        assert (data != null);
        return data.indexOf(separator) != -1 || data.indexOf(lineSeparator) != -1;
    }

}
