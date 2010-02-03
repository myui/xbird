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
package xbird.util.collections.longs;

import java.util.*;

import junit.framework.TestCase;

import xbird.util.collections.longs.Long2LongOpenHash;
import xbird.util.datetime.StopWatch;
import xbird.util.lang.ObjectUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class Long2LongOpenHashTest extends TestCase {

    public Long2LongOpenHashTest(String name) {
        super(name);
    }

    public void testPut100000() {
        put(100000);
    }
    
    public void testPut1000000() {
        put(1000000);
    }
    
    public void testPut10000000() {
        put(10000000);
    }

    public void put(int times) {
        StopWatch sw = new StopWatch("[Long2LongOpenHashTest] testPut" + times);
        List<Long> keys = new ArrayList<Long>(times);
        Long2LongOpenHash hash = new Long2LongOpenHash(times);
        Random random = new Random(System.currentTimeMillis());
        for(int i = 0; i < times; i++) {
            long key = random.nextLong();
            keys.add(key);
            long value = random.nextLong();
            hash.put(key, value);
            assertEquals(value, hash.get(key));
        }
        Long2LongOpenHash copyed = ObjectUtils.deepCopy(hash);
        assertEquals(hash.size(), copyed.size());
        for(int i = 0; i < times; i++) {
            long key = keys.get(i).longValue();
            assertEquals("Round#" + i, hash.get(key), copyed.get(key));
        }
        System.err.println(sw);
    }
}
