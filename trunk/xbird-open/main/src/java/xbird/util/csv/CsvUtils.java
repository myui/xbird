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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CsvUtils {

    public static final char DEFAULT_FIELD_SEPARATOR = '\t';
    public static final char DEFAULT_QUOTE_CHARACTER = '"';
    public static final char DEFAULT_ESCAPE_CHARACTER = '\\';
    private static final int EOF = -1;

    private CsvUtils() {}

    public static String readLine(final PushbackReader r) throws IOException {
        return readLine(r, DEFAULT_QUOTE_CHARACTER, new StringBuilder(128));
    }

    public static String readLine(final PushbackReader r, final char quoteChar) throws IOException {
        return readLine(r, quoteChar, new StringBuilder(128));
    }

    /**
    * Reads the next logical line of the CSV file. Returns the next line as a
    * {@link java.lang.String} without the trailing newline, or
    * <code>null</code> if there is no more data to read before the end of the file.
    *
    * <p>
    * The last line of a file is returned if it contains any characters, but
    * is ignored if it is empty. The contract for this method is intended to
    * approximate the contract of {@link java.io.BufferedReader#readLine}.
    * </p>
    */
    @Nullable
    public static String readLine(final PushbackReader r, final char quoteChar, final StringBuilder lineBuf)
            throws IOException {
        boolean inQuote = false;
        int ch;
        while((ch = r.read()) != EOF) {
            if(ch == quoteChar) {
                inQuote = !inQuote;
            } else if(inQuote == false) {
                if(ch == '\n') {
                    break;
                } else if(ch == '\r') {
                    final int ch2 = r.read();
                    if(ch2 == '\n') { // See if this is a CRLF pair.
                        break;
                    } else if(ch2 != EOF) {
                        r.unread(ch2);
                    }
                }
            }
            lineBuf.append((char) ch);
        }
        if((lineBuf.length() == 0) && (ch == EOF)) {
            return null;
        } else {
            String line = lineBuf.toString();
            lineBuf.setLength(0); // clear buffer
            return line;
        }
    }

    public static String[] parseLine(final String line) {
        return parseLine(line, DEFAULT_FIELD_SEPARATOR, DEFAULT_QUOTE_CHARACTER);
    }

    /**
     * Parses a logical line of CSV content into fields. 
     * 
     * An attempt is made to reconstruct CSV data in a generally compatible way 
     * across import sources.
     */
    public static String[] parseLine(final String line, final char filedSeparator, final char quoteChar) {
        final List<String> fields = new ArrayList<String>();
        parseLine(line, fields, filedSeparator, quoteChar);
        return (String[]) fields.toArray(new String[fields.size()]);
    }

    private static void parseLine(final String line, final List<String> fields, final char filedSeparator, final char quoteChar) {
        final StringBuilder fieldBuf = new StringBuilder(32);
        final int lineLength = line.length();
        for(int pos = 0; pos < lineLength; pos++) {
            final char ch = line.charAt(pos);
            if(ch == filedSeparator) {// Case #1 - empty
                fields.add("");
                if(pos == (lineLength - 1)) {// trick for the case ",,"
                    fields.add("");
                    break;
                }
            } else if(ch == quoteChar) {// case #2 - has quote
                pos++; /* skip the quote */
                fieldBuf.setLength(0); // clear                
                while(true) {
                    final int next = nextOccurrence(pos, line, quoteChar, lineLength);
                    fieldBuf.append(line.substring(pos, next));
                    pos = next;
                    if(next >= lineLength) {
                        break; // Unterminated quote. Nevertheless, we'll take it.
                    } else if(next == lineLength - 1) {
                        pos++; /* skip the closing quote */
                        break; // Quote is at the end of the line. It is, therefore, not doubled.
                    } else if(line.charAt(next + 1) != quoteChar) {
                        /* skip the closing quote and skip to the next comma */
                        pos = nextOccurrence(pos + 1, line, filedSeparator, lineLength);
                        break;
                    } else {// Quote is doubled. It should be considered part of the content, and does not end the field.
                        fieldBuf.append(quoteChar);
                        pos += 2; /* skip both doubled quotes */
                    }
                }
                fields.add(fieldBuf.toString());
            } else { // case #3 normal entry
                int next = nextOccurrence(pos, line, filedSeparator, lineLength);
                String field = line.substring(pos, next);
                fields.add(field);
                pos = next;
            }
        }
    }

    public static void retrieveFields(@Nonnull final String line, @Nonnull final int[] fieldIndicies, @Nonnull final List<String> fields) {
        retrieveFields(line, fieldIndicies, fields, DEFAULT_FIELD_SEPARATOR, DEFAULT_QUOTE_CHARACTER);
    }

    public static void retrieveFields(@Nonnull final String line, @Nonnull final int[] fieldIndicies, @Nonnull final List<String> fields, final char filedSeparator, final char quoteChar) {
        final StringBuilder fieldBuf = new StringBuilder(32);
        final int lineLength = line.length();
        final int numRetrieveFields = fieldIndicies.length;
        for(int i = 0, fi = 0, pos = 0; (fi < numRetrieveFields) && (pos <= lineLength); i++, pos++) {
            if(i == fieldIndicies[fi]) {
                if(pos == lineLength) {// trick for the case ",,"
                    retrieveField(line, lineLength, pos - 1, fields, filedSeparator, quoteChar, fieldBuf);
                    break;
                } else {
                    pos = retrieveField(line, lineLength, pos, fields, filedSeparator, quoteChar, fieldBuf);
                    fieldBuf.setLength(0); // clear                    
                    fi++;
                }
            } else {
                pos = skip(line, lineLength, pos, filedSeparator, quoteChar);
            }
        }
    }

    private static int retrieveField(final String line, final int lineLength, int pos, final List<String> fields, final char filedSeparator, final char quoteChar, final StringBuilder fieldBuf) {
        final char ch = line.charAt(pos);
        if(ch == filedSeparator) {// Case #1 - empty
            fields.add("");
            return pos;
        } else if(ch == quoteChar) {// case #2 - has quote
            pos++; /* skip the quote */
            fieldBuf.setLength(0); // clear                
            while(true) {
                final int next = nextOccurrence(pos, line, quoteChar, lineLength);
                fieldBuf.append(line.substring(pos, next));
                pos = next;
                if(next >= lineLength) {
                    break; // Unterminated quote. Nevertheless, we'll take it.
                } else if(next == lineLength - 1) {
                    pos++; /* skip the closing quote */
                    break; // Quote is at the end of the line. It is, therefore, not doubled.
                } else if(line.charAt(next + 1) != quoteChar) {
                    /* skip the closing quote and skip to the next comma */
                    pos = nextOccurrence(pos + 1, line, filedSeparator, lineLength);
                    break;
                } else {// Quote is doubled. It should be considered part of the content, and does not end the field.
                    fieldBuf.append(quoteChar);
                    pos += 2; /* skip both doubled quotes */
                }
            }
            fields.add(fieldBuf.toString());
            return pos;
        } else { // case #3 normal entry
            int next = nextOccurrence(pos, line, filedSeparator, lineLength);
            String field = line.substring(pos, next);
            fields.add(field);
            return next;
        }
    }

    private static int skip(final String line, final int lineLength, int pos, final char filedSeparator, final char quoteChar) {
        final char ch = line.charAt(pos);
        if(ch == filedSeparator) {// Case #1 - empty            
            return pos;
        } else if(ch == quoteChar) {// case #2 - has quote
            pos++; /* skip the quote */
            while(true) {
                final int next = nextOccurrence(pos, line, quoteChar, lineLength);
                pos = next;
                if(next >= lineLength) {
                    break; // Unterminated quote. Nevertheless, we'll take it.
                } else if(next == lineLength - 1) {
                    pos++; /* skip the closing quote */
                    break; // Quote is at the end of the line. It is, therefore, not doubled.
                } else if(line.charAt(next + 1) != quoteChar) {
                    /* skip the closing quote and skip to the next comma */
                    pos = nextOccurrence(pos + 1, line, filedSeparator, lineLength);
                    break;
                } else {// Quote is doubled. It should be considered part of the content, and does not end the field.
                    pos += 2; /* skip both doubled quotes */
                }
            }
            return pos;
        } else { // case #3 normal entry
            int next = nextOccurrence(pos, line, filedSeparator, lineLength);
            return next;
        }
    }

    /**
     * Returns the next character index in <code>line</code> that is not
     * whitespace, beginning at <code>pos</code>. Returns the next character
     * index in <code>line</code> that contains the specified character,
     * <code>ch</code>, beginning at <code>pos</code>. If there is no such
     * index, the method returns <code>line.length()</code>.
     */
    private static int nextOccurrence(int pos, final String line, final char endChar, final int lineLength) {
        while((pos < lineLength) && (line.charAt(pos) != endChar)) {
            pos++;
        }
        return pos;
    }
}
