/*
 * @(#)$Id: IntLRUMapTest.java 3619 2008-03-26 07:23:03Z yui $
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

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import xbird.util.collections.ints.IntHash.BucketEntry;
import xbird.util.collections.ints.IntHash.IntLRUMap;

public class IntLRUMapTest extends TestCase {

    public IntLRUMapTest() {
        super(IntLRUMapTest.class.getName());
    }

    @Test
    public void testPut() {
        final int limit = 4;
        IntLRUMap<Integer> lru = new IntLRUMap<Integer>(4);
        lru.put(0, 1);
        lru.put(1, 2);
        lru.put(2, 3);
        lru.put(3, 4);
        Assert.assertEquals(4, lru.put(3, 5));
        Assert.assertEquals(null, lru.put(4, 5));
        Assert.assertEquals(limit, lru.size());
    }

    @Test
    public void testGetPromotion() {
        IntLRUMap<Integer> map = new IntLRUMap<Integer>(3);
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        // eviction order is now 1,2,3       
        map.get(1);
        // eviction order is now 2,3,1
        map.put(4, 4);
        // 2 should be evicted (then 3,1,4)
        int[] keys = new int[3];
        int i = 0;
        for(BucketEntry<Integer> e : map) {
            keys[i++] = e.key;
        }
        Assert.assertEquals(keys[0], 3);
        Assert.assertEquals(keys[1], 1);
        Assert.assertEquals(keys[2], 4);
    }

    @Test
    public void testMultiplePuts() {
        IntLRUMap<String> map2 = new IntLRUMap<String>(2);
        map2.put(1, "foo");
        map2.put(2, "bar");
        map2.put(3, "foo");
        map2.put(4, "bar");
        Assert.assertEquals(map2.get(4), "bar");
        Assert.assertEquals(map2.get(1), null);
    }
}
