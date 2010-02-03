/*
 * @(#)$Id: Int2IntLRUMapTest.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.util.collections.ints.Int2IntHash;
import xbird.util.collections.ints.Int2IntHash.Int2IntLRUMap;

public class Int2IntLRUMapTest extends TestCase {

    public Int2IntLRUMapTest() {
        super(Int2IntLRUMapTest.class.getName());
    }

    @Test
    public void testPut() {
        final int limit = 4;
        Int2IntLRUMap lru = new Int2IntLRUMap(4);
        lru.put(0, 1);
        lru.put(1, 2);
        lru.put(2, 3);
        lru.put(3, 4);
        Assert.assertEquals(4, lru.put(3, 5));
        Assert.assertEquals(-1, lru.put(4, 5));
        Assert.assertEquals(limit, lru.size());
    }

    @Test
    public void testGetPromotion() {
        Int2IntLRUMap map = new Int2IntLRUMap(3);
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
        for(Int2IntHash.BucketEntry e : map) {
            keys[i++] = e.key;
        }
        Assert.assertEquals(keys[0], 3);
        Assert.assertEquals(keys[1], 1);
        Assert.assertEquals(keys[2], 4);
    }
}
