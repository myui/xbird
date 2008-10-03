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
package xbird.util.lang;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;

import junit.framework.TestCase;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class ArrayUtilsTest extends TestCase {

    /**
     * Test method for {@link xbird.util.lang.ArrayUtils#toArray(java.util.Collection, java.lang.Class)}.
     */
    public void testToArray() {
        String[] orig = new String[] {"aaa", "bbb", "ccc"};
        List<String> origList = Arrays.asList(orig);
        String[] dest = ArrayUtils.toArray(origList, String[].class);
        Assert.assertEquals(orig, dest);
    }

    public void testGetArrayClass() {
        Class<String[]> clazz = ArrayUtils.getArrayClass(String.class);
        Assert.assertEquals(String[].class, clazz);
    }
}
