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
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import xbird.util.collections.FixedArrayList;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class CsvUtilsTest {

    @Test
    public void simpleTest() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("a,b,c").append("\n"); // standard case
        sb.append("a,\"b,b,b\",c").append("\n"); // quoted elements
        sb.append(",,").append("\n"); // empty elements
        sb.append("a,\"PO Box 123,\nKippax,ACT. 2615.\nAustralia\",d.\n");
        sb.append("\"Glen \"\"The Man\"\" Smith\",Athlete,Developer\n"); // Test
        sb.append("\"\"\"\"\"\",\"test\"\n"); // """""","test" representing:
        sb.append("\"a\nb\",b,\"\nd\",e\n");
        PushbackReader reader = new PushbackReader(new StringReader(sb.toString()));

        // test normal case                
        String line = CsvUtils.readLine(reader, CsvUtils.DEFAULT_QUOTE_CHARACTER);
        String[] fields = CsvUtils.parseLine(line, ',', CsvUtils.DEFAULT_QUOTE_CHARACTER);
        org.junit.Assert.assertEquals("a", fields[0]);
        org.junit.Assert.assertEquals("b", fields[1]);
        org.junit.Assert.assertEquals("c", fields[2]);

        // test quoted commas
        line = CsvUtils.readLine(reader, CsvUtils.DEFAULT_QUOTE_CHARACTER);
        fields = CsvUtils.parseLine(line, ',', CsvUtils.DEFAULT_QUOTE_CHARACTER);
        org.junit.Assert.assertEquals("a", fields[0]);
        org.junit.Assert.assertEquals("b,b,b", fields[1]);
        org.junit.Assert.assertEquals("c", fields[2]);

        String[] ary3 = new String[3];
        FixedArrayList<String> list3 = new FixedArrayList<String>(ary3);
        CsvUtils.retrieveFields(line, new int[] { 0, 2 }, list3, ',', CsvUtils.DEFAULT_QUOTE_CHARACTER);
        org.junit.Assert.assertEquals("a", ary3[0]);
        org.junit.Assert.assertEquals("c", ary3[1]);

        // test empty elements
        line = CsvUtils.readLine(reader, CsvUtils.DEFAULT_QUOTE_CHARACTER);
        fields = CsvUtils.parseLine(line, ',', CsvUtils.DEFAULT_QUOTE_CHARACTER);
        org.junit.Assert.assertEquals(3, fields.length);

        // test multiline quoted
        line = CsvUtils.readLine(reader, CsvUtils.DEFAULT_QUOTE_CHARACTER);
        fields = CsvUtils.parseLine(line, ',', CsvUtils.DEFAULT_QUOTE_CHARACTER);
        org.junit.Assert.assertEquals(3, fields.length);

        // test quoted quote chars
        line = CsvUtils.readLine(reader, CsvUtils.DEFAULT_QUOTE_CHARACTER);
        fields = CsvUtils.parseLine(line, ',', CsvUtils.DEFAULT_QUOTE_CHARACTER);
        org.junit.Assert.assertEquals("Glen \"The Man\" Smith", fields[0]);

        line = CsvUtils.readLine(reader, CsvUtils.DEFAULT_QUOTE_CHARACTER);
        fields = CsvUtils.parseLine(line, ',', CsvUtils.DEFAULT_QUOTE_CHARACTER);
        org.junit.Assert.assertTrue(fields[0].equals("\"\"")); // check the tricky situation
        Assert.assertTrue(fields[1].equals("test")); // make sure we didn't ruin the next field..

        list3.trimToZero();
        CsvUtils.retrieveFields(line, new int[] { 0 }, list3, ',', CsvUtils.DEFAULT_QUOTE_CHARACTER);
        Assert.assertTrue(ary3[0].equals("\"\""));

        line = CsvUtils.readLine(reader, CsvUtils.DEFAULT_QUOTE_CHARACTER);
        fields = CsvUtils.parseLine(line, ',', CsvUtils.DEFAULT_QUOTE_CHARACTER);
        Assert.assertEquals(4, fields.length);

        // test end of stream
        line = CsvUtils.readLine(reader, CsvUtils.DEFAULT_QUOTE_CHARACTER);
        Assert.assertNull(line);
    }

}
