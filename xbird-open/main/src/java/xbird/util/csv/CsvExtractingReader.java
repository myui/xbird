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

import java.io.PushbackReader;
import java.util.Arrays;

import javax.annotation.Nonnull;

import xbird.util.collections.FixedArrayList;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CsvExtractingReader extends SimpleCsvReader {

    private final int[] fieldIndicies;
    private final String[] fields;
    private final FixedArrayList<String> fieldsProbe;

    public CsvExtractingReader(@Nonnull PushbackReader reader, @Nonnull int[] fieldIndicies, char filedSeparator, char quoteChar) {
        super(reader, filedSeparator, quoteChar);
        final int length = fieldIndicies.length;
        if(length == 0) {
            throw new IllegalArgumentException("fieldIndicies is invalid: "
                    + Arrays.toString(fieldIndicies));
        }
        this.fieldIndicies = fieldIndicies;
        this.fields = new String[length];
        this.fieldsProbe = new FixedArrayList<String>(fields);
    }

    public String[] retrieveFields(final String line) {
        CsvUtils.retrieveFields(line, fieldIndicies, fieldsProbe, filedSeparator, quoteChar);
        fieldsProbe.clear();
        return fields;
    }

}
