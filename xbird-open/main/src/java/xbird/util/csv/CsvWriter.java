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
package xbird.util.csv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.logging.LogFactory;

import xbird.util.io.FastBufferedWriter;
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
    private static final String JAVA_STRING_CLASS_NAME = "java.lang.String";

    private char separator = ',';
    private String lineSeparator = System.getProperty("line.separator");
    private final Writer _writer;

    /**
     * Write in UTF-8 encoding.
     */
    public CsvWriter(@CheckForNull File file) {
        this(file, "UTF-8", false);
    }

    public CsvWriter(@CheckForNull File file, @CheckForNull String encoding, boolean append) {
        if(file == null) {
            throw new IllegalArgumentException();
        }
        if(encoding == null) {
            throw new IllegalArgumentException();
        }
        try {
            FileOutputStream out = new FileOutputStream(file, append);
            OutputStreamWriter osw = new OutputStreamWriter(out, encoding);
            this._writer = new FastBufferedWriter(osw, 16384);
        } catch (IOException e) {
            throw new IllegalStateException("failed to writer to file: " + file.getAbsolutePath(), e);
        }
    }

    public CsvWriter(@CheckForNull Writer writer) {
        if(writer == null) {
            throw new IllegalArgumentException();
        }
        this._writer = writer;
    }

    public void setFieldSeparator(char chr) {
        this.separator = chr;
    }

    public void setLineSeparator(String str) {
        this.lineSeparator = str;
    }

    public void writeRow(final String... cols) {
        assert (cols != null);
        final int collen = cols.length;
        for(int i = 0; i < collen; i++) {
            if(i > 0) {
                write(separator);
            }
            final String data = cols[i];
            if(data != null) {
                final boolean doQuote = needQuotes(data);
                if(doQuote) {
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

    public int writeAll(@Nonnull final ResultSet rs, @Nonnull final String nullStr, final boolean includeHeaders)
            throws SQLException {
        final ResultSetMetaData meta = rs.getMetaData();
        if(includeHeaders) {
            writeColumnNames(meta);
        }
        final int numColumns = meta.getColumnCount();
        final String[] columnClasses = new String[numColumns + 1];
        for(int i = 1; i <= numColumns; i++) {
            String className = meta.getColumnClassName(i);
            columnClasses[i] = JAVA_STRING_CLASS_NAME.equals(className) ? JAVA_STRING_CLASS_NAME
                    : className;
        }
        int numRows = 0;
        while(rs.next()) {
            for(int i = 1; i <= numColumns; i++) {
                if(i != 1) {
                    write(separator);
                }
                final String column = rs.getString(i);
                if(column == null) {
                    write(nullStr);
                } else if(JAVA_STRING_CLASS_NAME == columnClasses[i]) { // for speed optimization
                    write(QUOTE);
                    write(quoteData(column));
                    write(QUOTE);
                } else {
                    write(column);
                }
            }
            write(lineSeparator);
            numRows++;
        }
        flush();
        return numRows;
    }

    private void writeColumnNames(final ResultSetMetaData metadata) throws SQLException {
        final int numColumns = metadata.getColumnCount();
        for(int i = 1; i <= numColumns; i++) {
            if(i != 1) {
                write(separator);
            }
            String columnName = metadata.getColumnName(i);
            write(columnName);
        }
        write(lineSeparator);
    }

    public void flush() {
        try {
            _writer.flush();
        } catch (IOException e) {
            LogFactory.getLog(CsvWriter.class).warn("Failed to flush", e);
        }
    }

    public void close() {
        flush();
        try {
            _writer.close();
        } catch (IOException e) {
            // fall through within error message
            LogFactory.getLog(CsvWriter.class).debug(PrintUtils.prettyPrintStackTrace(e));
        }
    }

    private void write(final String s) {
        try {
            _writer.write(s);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void write(final char c) {
        try {
            _writer.write(c);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private boolean needQuotes(final String data) {
        assert (data != null);
        return data.indexOf(separator) != -1 || data.indexOf(lineSeparator) != -1;
    }

    private static String quoteData(final String data) {
        final StringBuilder buf = new StringBuilder((int) (data.length() * 1.2));
        final int strlen = data.length();
        for(int i = 0; i < strlen; i++) {
            final char c = data.charAt(i);
            buf.append(c);
            switch(c) {
                case QUOTE:
                    buf.append(QUOTE);
                    break;
                default:
                    break;
            }
        }
        return buf.toString();
    }

}
