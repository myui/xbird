/*
 * @(#)$Id$
 *
 * Copyright 2005 Bytecode Pty Ltd.
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
 *     Makoto YUI - imported from OpenCSV
 */
package xbird.util.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A very simple CSV reader released under a commercial-friendly license.
 * 
 * @author Glen Smith
 */
public final class OpenCsvReader implements CvsReader {

    private final BufferedReader br;
    private final OpenCsvParser parser;

    private boolean hasNext = true;

    /**
     * Constructs CSVReader using a comma for the separator.
     * 
     * @param reader
     *            the reader to an underlying CSV source.
     */
    public OpenCsvReader(Reader reader) {
        this(reader, CsvUtils.DEFAULT_FIELD_SEPARATOR, CsvUtils.DEFAULT_QUOTE_CHARACTER, CsvUtils.DEFAULT_ESCAPE_CHARACTER);
    }

    /**
     * Constructs CSVReader with supplied separator.
     * 
     * @param reader
     *            the reader to an underlying CSV source.
     * @param separator
     *            the delimiter to use for separating entries.
     */
    public OpenCsvReader(Reader reader, char separator) {
        this(reader, separator, CsvUtils.DEFAULT_QUOTE_CHARACTER, CsvUtils.DEFAULT_ESCAPE_CHARACTER);
    }

    /**
     * Constructs CSVReader with supplied separator and quote char.
     * 
     * @param reader
     *            the reader to an underlying CSV source.
     * @param separator
     *            the delimiter to use for separating entries
     * @param quotechar
     *            the character to use for quoted elements
     */
    public OpenCsvReader(Reader reader, char separator, char quotechar) {
        this(reader, separator, quotechar, CsvUtils.DEFAULT_ESCAPE_CHARACTER, false);
    }

    /**
      * Constructs CSVReader with supplied separator and quote char.
      *
      * @param reader
      *            the reader to an underlying CSV source.
      * @param separator
      *            the delimiter to use for separating entries
      * @param quotechar
      *            the character to use for quoted elements
      * @param escape
      *            the character to use for escaping a separator or quote
      */
    public OpenCsvReader(Reader reader, char separator, char quotechar, char escape) {
        this(reader, separator, quotechar, escape, false);
    }

    /**
     * Constructs CSVReader with supplied separator and quote char.
     * 
     * @param reader
     *            the reader to an underlying CSV source.
     * @param separator
     *            the delimiter to use for separating entries
     * @param quotechar
     *            the character to use for quoted elements
     * @param escape
     *            the character to use for escaping a separator or quote
     * @param strictQuotes
     *            sets if characters outside the quotes are ignored
     */
    public OpenCsvReader(Reader reader, char separator, char quotechar, char escape, boolean strictQuotes) {
        this.br = new BufferedReader(reader);
        this.parser = new OpenCsvParser(separator, quotechar, escape, strictQuotes);
    }

    /**
     * Reads the entire file into a List with each element being a String[] of
     * tokens.
     * 
     * @return a List of String[], with each String[] representing a line of the
     *         file.
     * 
     * @throws IOException
     *             if bad things happen during the read
     */
    public List<String[]> readAll() throws IOException {
        final List<String[]> allElements = new ArrayList<String[]>();
        while(hasNext) {
            final String[] nextLineAsTokens = readNext();
            if(nextLineAsTokens != null) {
                allElements.add(nextLineAsTokens);
            }
        }
        return allElements;

    }

    /**
     * Reads the next line from the buffer and converts to a string array.
     * 
     * @return a string array with each comma-separated element as a separate
     *         entry.
     * 
     * @throws IOException
     *             if bad things happen during the read
     */
    public String[] readNext() throws IOException {
        String[] result = null;
        do {
            final String nextLine = getNextLine();
            if(!hasNext) {
                return result; // should throw if still pending?
            }
            final String[] r = parser.parseLineMulti(nextLine);
            if(r.length > 0) {
                if(result == null) {
                    result = r;
                } else {
                    final String[] t = new String[result.length + r.length];
                    System.arraycopy(result, 0, t, 0, result.length);
                    System.arraycopy(r, 0, t, result.length, r.length);
                    result = t;
                }
            }
        } while(parser.isPending());
        return result;
    }

    /**
     * Reads the next line from the file.
     * 
     * @return the next line from the file without trailing newline
     * @throws IOException
     *             if bad things happen during the read
     */
    public String getNextLine() throws IOException {
        final String nextLine = br.readLine();
        if(nextLine == null) {
            hasNext = false;
        }
        return hasNext ? nextLine : null;
    }

    public String[] parseLine(String line) throws IOException {
        return parser.parseLine(line);
    }

    /**
     * Closes the underlying reader.
     * 
     * @throws IOException if the close fails
     */
    public void close() throws IOException {
        br.close();
    }

}
