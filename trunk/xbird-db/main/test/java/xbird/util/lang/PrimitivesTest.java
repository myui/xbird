/*
 * @(#)$Id: PrimitivesTest.java 3619 2008-03-26 07:23:03Z yui $
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

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;
import xbird.util.primitive.Primitives;

public class PrimitivesTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PrimitivesTest.class);
    }

    public PrimitivesTest() {
        super(PrimitivesTest.class.getName());
    }

    public void testToBytesCharArray() {
        char[] c = { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
        byte[] b = Primitives.toBytes(c);
        char[] c2 = Primitives.toChars(b);
        ArrayAssert.assertEquals(c2, c);
    }

    public void testToBytesCharArrayIntInt() {
        char[] c = { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
        byte[] b = Primitives.toBytes(c, 2, 4);
        assert (b != null);
        char[] c2 = Primitives.toChars(b);
        char[] c3 = ArrayUtils.copyOfRange(c, 2, 6);
        ArrayAssert.assertEquals(c2, c3);
    }

    public void testToBytesIntArray() {
        int[] i1 = { 1, 2, 3, 4, 5 };
        byte[] b1 = Primitives.toBytes(i1);
        int[] i2 = Primitives.toInts(b1);
        ArrayAssert.assertEquals(i1, i2);
    }

    public void testToBytesIntArrayIntInt() {
        int[] i1 = { 1, 2, 3, 4, 5 };
        byte[] b1 = Primitives.toBytes(i1, 2, 3);
        int[] i2 = Primitives.toInts(b1);
        int[] i3 = ArrayUtils.copyOfRange(i1, 2, 5);
        ArrayAssert.assertEquals(i2, i3);
    }

    public void testToCharsByteArray() {
        char[] cs = { 'f', 'e', 'w', 'f', 's', 'f', 'v', 'i' };
        byte[] b = Primitives.toBytes(cs);
        char[] c = Primitives.toChars(b);
        ArrayAssert.assertEquals(cs, c);
    }

    public void testToCharsByteArrayIntInt() {
        byte[] b = "fewfsfv".getBytes();
        char[] c = Primitives.toChars(b, 2, 4);
        byte[] b2 = Primitives.toBytes(c);
        byte[] b3 = ArrayUtils.copyOfRange(b, 2, 6);
        ArrayAssert.assertEquals(b2, b3);
    }

}
