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
package xbird.util.codec;

import junit.framework.TestCase;

import org.junit.Test;

import xbird.util.string.StringUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class VariableByteCodecTest extends TestCase {
    @Test
    public void testCoding() {
        test(0);
        test(1);
        test(2);
        test(10);
        test(Integer.MAX_VALUE);
        test((long) Integer.MAX_VALUE + 1L);
        test(Long.MAX_VALUE - 1);
        test(Long.MAX_VALUE);
        test(0x80L - 1);
        test(0x80L);
        test(0x80L + 1);
        test(0x10000L - 1);
        test(0x10000L);
        test(0x10000L + 1);
        test(0x1000000L - 1);
        test(0x1000000L);
        test(0x1000000L + 1);
        test(0x100000000L - 1);
        test(0x100000000L);
        test(0x100000000L + 1);
        test(0x10000000000L - 1);
        test(0x10000000000L);
        test(0x10000000000L + 1);
    }

    private void test(long l) {
        byte[] encoded = VariableByteCodec.encodeLong(l);
        long decoded = VariableByteCodec.decodeLong(encoded);
        System.out.println("original: " + l + ", requiredBytes: "
                + VariableByteCodec.requiredBytes(l) + ", encoded: "
                + StringUtils.toBitString(encoded) + ", encoded byte length: " + encoded.length
                + ", decoded: " + decoded);
    }
}
