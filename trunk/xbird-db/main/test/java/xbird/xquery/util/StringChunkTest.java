/*
 * @(#)$Id: StringChunkTest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.util;

import junit.framework.TestCase;

import xbird.util.string.StringUtils;
import xbird.xquery.misc.*;

public class StringChunkTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(StringChunkTest.class);
    }

    public StringChunkTest(String testName) {
        super(testName);
    }

    public void testStoreCharArrayIntInt() {
        IStringChunk chunk = new StringChunk();
        char[] ch = new char[] { 'a', 'b', 'c', 'd' };
        long addr = chunk.store(ch, 2, 2);
        String stored = chunk.getString(addr);
        assertNotNull(stored);
        assertTrue(StringUtils.equals(stored, ch, 2, 2));
    }

    public void testStoreString() {
        IStringChunk chunk = new StringChunk();
        String s = "abcdefg";
        long addr = chunk.store(s);
        assertEquals(s, chunk.getString(addr));
    }

    public void testStoreCharArrayIntInt2() {
        IStringChunk chunk = new BasicStringChunk();
        char[] ch = new char[] { 'a', 'b', 'c', 'd' };
        long addr = chunk.store(ch, 2, 2);
        String stored = chunk.getString(addr);
        assertNotNull(stored);
        assertTrue(StringUtils.equals(stored, ch, 2, 2));
    }

    public void testStoreString2() {
        IStringChunk chunk = new BasicStringChunk();
        String s = "abcdefg";
        long addr = chunk.store(s);
        assertEquals(s, chunk.getString(addr));
    }

}
