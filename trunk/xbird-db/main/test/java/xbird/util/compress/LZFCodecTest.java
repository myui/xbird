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
package xbird.util.compress;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;
import xbird.util.compress.LZFCodec;
import xbird.util.io.FastMultiByteArrayOutputStream;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class LZFCodecTest extends TestCase {

    public LZFCodecTest(String name) {
        super(name);
    }

    public void test1() {
        probe("aabbbcbbbb".getBytes());
    }

    public void test2() {
        probe("asasdfdfagfffffaerawefgsgsdffffgvsdfgsgfsgfgweffvsregsrevsdfsagghhawdfd".getBytes());
    }

    public void testRand1000() throws IOException {
        FastMultiByteArrayOutputStream out = new FastMultiByteArrayOutputStream(4096);
        DataOutputStream data = new DataOutputStream(out);
        Random rand = new Random(22222);
        for(int times = 0; times < 3; times++) {
            for(int i = 0; i < 1000; i++) {
                data.writeInt(rand.nextInt());
            }
            probe(out.toByteArray_clear());
        }
    }

    public void testRand10000() throws IOException {
        FastMultiByteArrayOutputStream out = new FastMultiByteArrayOutputStream(4096);
        DataOutputStream data = new DataOutputStream(out);
        Random rand = new Random(44444);
        for(int times = 0; times < 3; times++) {
            for(int i = 0; i < 10000; i++) {
                data.writeInt(rand.nextInt());
            }
            probe(out.toByteArray_clear());
        }
    }

    public void testRandGaussian10000() throws IOException {
        FastMultiByteArrayOutputStream out = new FastMultiByteArrayOutputStream(4096);
        DataOutputStream data = new DataOutputStream(out);
        Random rand = new Random(3333);
        for(int times = 0; times < 3; times++) {
            for(int i = 0; i < 10000; i++) {
                data.writeDouble(rand.nextGaussian());
            }
            probe(out.toByteArray_clear());
        }
    }

    public void probe(byte[] input) {
        LZFCodec codec = new LZFCodec();
        byte[] compressed = codec.compress(input);
        byte[] decompressed = codec.decompress(compressed);
        ArrayAssert.assertEquals(input, decompressed);
        float ratio = (float) compressed.length / (float) input.length;
        System.out.println("Original: " + input.length + ", Compressed: " + compressed.length
                + ", Compression ratio: " + Float.toString(ratio * 100.0f));
    }

}
