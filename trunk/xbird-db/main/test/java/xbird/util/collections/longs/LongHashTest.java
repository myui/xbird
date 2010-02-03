/*
 * @(#)$Id: LongHashTest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.collections.longs;

import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Assert;

import xbird.util.collections.longs.LongHash;
import xbird.util.collections.longs.LongHash.BucketEntry;
import xbird.util.datetime.StopWatch;
import xbird.util.lang.ObjectUtils;

public class LongHashTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LongHashTest.class);
    }

    public LongHashTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'xbird.util.IntHash.put(int, V)'
     */
    public void testPut() {
        StopWatch sw = new StopWatch();
        long[] keys = new long[100000];
        LongHash<Long> hash = new LongHash<Long>();
        Random random = new Random(System.currentTimeMillis());
        for(int i = 0; i < 100000; i++) {
            int key = random.nextInt();
            keys[i] = key;
            long value = random.nextLong();
            hash.put(key, value);
        }
        LongHash<Long> copyed = ObjectUtils.deepCopy(hash);
        assert (hash.size() == copyed.size());
        for(int i = 0; i < 100000; i++) {
            long key = keys[i];
            assertEquals(hash.get(key), copyed.get(key));
        }
        System.out.println(sw);
    }

    public void testIterator() {
        long[] values = new long[100000];
        LongHash<Long> hash = new LongHash<Long>();
        Random random = new Random(System.currentTimeMillis());
        for(int i = 0; i < 100000; i++) {
            long v = random.nextLong();
            values[i] = v;
            hash.put(i, v);
        }

        long[] nvalues = new long[100000];
        for(BucketEntry<Long> e : hash) {
            int k = (int) e.getKey();
            long v = e.getValue();
            nvalues[k] = v;
        }
        Assert.assertTrue(Arrays.equals(values, nvalues));
    }

}
