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
package xbird.util.string;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class StringUtilsTest {

    /**
     * Test method for {@link xbird.util.string.StringUtils#deleteLastChar(java.lang.StringBuilder)}.
     */
    @Test
    public void testDeleteLastChar() {
        assertEquals("", StringUtils.deleteLastChar(new StringBuilder(",")).toString());
        assertEquals("a", StringUtils.deleteLastChar(new StringBuilder("ab")).toString());
    }

    /**
     * Test method for {@link xbird.util.string.StringUtils#replaceLastChar(java.lang.StringBuilder, char)}.
     */
    @Test
    public void testReplaceLastChar() {
        assertEquals("\n", StringUtils.replaceLastChar(new StringBuilder(","), '\n').toString());
        assertEquals("abb", StringUtils.replaceLastChar(new StringBuilder("abc"), 'b').toString());
    }

}
