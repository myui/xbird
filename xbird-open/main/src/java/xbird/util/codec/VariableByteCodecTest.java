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

import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;
import xbird.util.io.FastByteArrayOutputStream;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class VariableByteCodecTest extends TestCase {

    public void testEncodeDecodeInt() throws IOException {
        verify(0);
        verify(1);
        verify(7);
        verify(21);
        verify(127);
        verify(128);
        verify(433);
        verify(23111);
        verify(4343511);
        verify(Integer.MAX_VALUE - 1);
        verify(Integer.MAX_VALUE);
    }

    private static void verify(int i) throws IOException {
        FastByteArrayOutputStream os = new FastByteArrayOutputStream(256);
        VariableByteCodec.encodeInt(i, os);
        byte[] b1 = os.toByteArray();
        Assert.assertEquals(i, VariableByteCodec.decodeInt(b1));
    }

}
