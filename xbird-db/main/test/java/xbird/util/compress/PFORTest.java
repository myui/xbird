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
import xbird.util.datetime.StopWatch;
import xbird.util.io.FastMultiByteArrayOutputStream;
import xbird.util.primitive.Primitives;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class PFORTest extends TestCase {

    public PFORTest(String name) {
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

    public void testRandChars2000() throws IOException {
        FastMultiByteArrayOutputStream out = new FastMultiByteArrayOutputStream(4096);
        DataOutputStream data = new DataOutputStream(out);
        Random rand = new Random(22222);
        for(int times = 0; times < 10; times++) {
            for(int i = 0; i < 2048; i++) {
                data.writeChar(rand.nextInt());
            }
            probe(out.toByteArray_clear());
        }
    }

    public void testRandChars8K() throws IOException {
        FastMultiByteArrayOutputStream out = new FastMultiByteArrayOutputStream(4096);
        DataOutputStream data = new DataOutputStream(out);
        Random rand = new Random(22222);
        for(int times = 0; times < 10; times++) {
            for(int i = 0; i < 4096; i++) {
                data.writeChar(rand.nextInt());
            }
            probe(out.toByteArray_clear());
        }
    }

    public void probe(byte[] input) {
        char[] inputc = Primitives.toChars(input);

        System.gc();
        runPFOR(inputc);

        System.gc();
        runLzf(input);
    }

    private static void runPFOR(char[] input) {
        StopWatch sw1 = new StopWatch("PFOR [on demand] compress: ");
        byte[] compressed = PFOR.compress(input);
        System.out.println(sw1.toString());
        StopWatch sw2 = new StopWatch("PFOR decompress: ");
        char[] decompressed = PFOR.decompressAsChars(compressed);
        System.out.println(sw2);
        ArrayAssert.assertEquals(input, decompressed);
        float ratio = (float) compressed.length / (float) (input.length << 1);
        System.out.println("Original: " + input.length + ", Compressed: " + compressed.length
                + ", Compression ratio: " + Float.toString(ratio * 100.0f));
    }

    private static void runLzf(byte[] input) {
        LZFCodec codec = new LZFCodec();
        StopWatch sw1 = new StopWatch("LZF compress: ");
        byte[] compressed = codec.compress(input);
        System.out.println(sw1.toString());
        StopWatch sw2 = new StopWatch("LZF decompress: ");
        byte[] decompressed = codec.decompress(compressed);
        System.out.println(sw2);
        ArrayAssert.assertEquals(input, decompressed);
        float ratio = (float) compressed.length / (float) input.length;
        System.out.println("Original: " + input.length + ", Compressed: " + compressed.length
                + ", Compression ratio: " + Float.toString(ratio * 100.0f));
    }

}
