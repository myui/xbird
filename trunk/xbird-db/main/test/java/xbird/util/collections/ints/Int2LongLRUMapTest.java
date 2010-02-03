/*
 * @(#)$Id: Int2LongLRUMapTest.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.util.collections.ints.Int2LongHash;
import xbird.util.collections.ints.Int2LongHash.Int2LongLRUMap;

public class Int2LongLRUMapTest extends TestCase {

    public Int2LongLRUMapTest() {
        super(Int2LongLRUMapTest.class.getName());
    }

    @Test
    public void testPut() {
        final int limit = 4;
        Int2LongLRUMap lru = new Int2LongLRUMap(4);
        lru.put(0, 1L);
        lru.put(1, 2L);
        lru.put(2, 3L);
        lru.put(3, 4L);
        Assert.assertEquals(4L, lru.put(3, 5));
        Assert.assertEquals(-1L, lru.put(4, 5));
        Assert.assertEquals(limit, lru.size());
    }

    @Test
    public void testGetPromotion() {
        Int2LongLRUMap map = new Int2LongLRUMap(3);
        map.put(1, 1L);
        map.put(2, 2L);
        map.put(3, 3L);
        // eviction order is now 1,2,3       
        map.get(1);
        // eviction order is now 2,3,1
        map.put(4, 4L);
        // 2 should be evicted (then 3,1,4)
        int[] keys = new int[3];
        int i = 0;
        for(Int2LongHash.BucketEntry e : map) {
            keys[i++] = e.key;
        }
        Assert.assertEquals(keys[0], 3);
        Assert.assertEquals(keys[1], 1);
        Assert.assertEquals(keys[2], 4);
    }
}
