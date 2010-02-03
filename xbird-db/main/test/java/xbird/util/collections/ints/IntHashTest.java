/*
 * @(#)$Id: IntHashTest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.collections.ints;

import java.util.Random;

import junit.framework.TestCase;

import xbird.util.collections.ints.IntArrayList;
import xbird.util.collections.ints.IntHash;
import xbird.util.lang.ObjectUtils;

public class IntHashTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(IntHashTest.class);
    }

    public IntHashTest(String arg0) {
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
        IntArrayList keys = new IntArrayList(100000);
        IntHash<Long> hash = new IntHash<Long>();
        Random random = new Random(System.currentTimeMillis());
        for(int i = 0; i < 100000; i++) {
            int key = random.nextInt();
            keys.add(key);
            long value = random.nextLong();
            hash.put(key, value);
        }
        IntHash<Long> copyed = ObjectUtils.deepCopy(hash);
        assert (hash.size() == copyed.size());
        for(int i = 0; i < 100000; i++) {
            int key = keys.get(i);
            assertEquals(hash.get(key), copyed.get(key));
        }
    }

}
