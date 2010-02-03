/*
 * @(#)$Id: LRUPerformanceTest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.collections;

import java.util.Random;

import junit.framework.TestCase;

import org.junit.Test;
import xbird.util.collections.ints.Int2QCache;
import xbird.util.collections.ints.Int2IntHash.Int2IntLRUMap;
import xbird.util.collections.ints.IntHash.IntLRUMap;
import xbird.util.collections.longs.Long2QCache;
import xbird.util.collections.longs.LongHash.LongLRUMap;
import xbird.util.datetime.StopWatch;
import xbird.util.system.SystemUtils;

public class LRUPerformanceTest extends TestCase {

    private static final int LOOP = 1024 * 512;
    private static final int LIMIT = 1024 * 16;

    public LRUPerformanceTest() {
        super(LRUPerformanceTest.class.getName());
    }

    @Test
    public void testInt2IntLruMap() {
        System.out.print("testInt2IntLruMap - ");
        System.gc();
        long free = SystemUtils.getHeapFreeMemory();
        System.out.print("free(init): " + free);
        Int2IntLRUMap ilru = new Int2IntLRUMap(LIMIT);
        for(int i = 0; i < LOOP; i++) {
            ilru.put(i, i);
        }
        Random random = new Random();
        for(int i = 0; i < LOOP / 2; i++) {
            int r = random.nextInt(LOOP - 1);
            ilru.put(r, r);
        }
        long used = free - SystemUtils.getHeapFreeMemory();
        System.out.print(", used(before GC): " + used);
        System.gc();
        used = free - SystemUtils.getHeapFreeMemory();
        System.out.print(", used(after GC): " + used);
        System.out.println(", total gc count: " + SystemUtils.countGC());
    }

    @Test
    public void testIntLruMap() {
        StringBuilder buf = new StringBuilder(1024);
        System.gc();
        long free = SystemUtils.getHeapFreeMemory();
        buf.append(" free(init): " + free);
        StopWatch sw = new StopWatch("testIntLruMap");
        IntLRUMap<Integer> lru = new IntLRUMap<Integer>(LIMIT);
        for(int i = 0; i < LOOP; i++) {
            lru.put(i, i);
        }
        Random random = new Random();
        for(int i = 0; i < LOOP / 2; i++) {
            int r = random.nextInt(LOOP - 1);
            lru.put(r, r);
        }
        buf.insert(0, sw.toString());
        long used = free - SystemUtils.getHeapFreeMemory();
        buf.append(", used(before GC): " + used);
        System.gc();
        used = free - SystemUtils.getHeapFreeMemory();
        buf.append(", used(after GC): " + used);
        buf.append(", total gc count: " + SystemUtils.countGC());
        System.out.println(buf);
    }

    @Test
    public void testInt2QMap() {
        StringBuilder buf = new StringBuilder(1024);
        System.gc();
        long free = SystemUtils.getHeapFreeMemory();
        buf.append(" free(init): " + free);
        StopWatch sw = new StopWatch("testInt2QMap");
        Int2QCache<Integer> lru = new Int2QCache<Integer>(LIMIT);
        for(int i = 0; i < LOOP; i++) {
            lru.put(i, i);
        }
        Random random = new Random();
        for(int i = 0; i < LOOP / 2; i++) {
            int r = random.nextInt(LOOP - 1);
            lru.put(r, r);
        }
        buf.insert(0, sw.toString());
        long used = free - SystemUtils.getHeapFreeMemory();
        buf.append(", used(before GC): " + used);
        System.gc();
        used = free - SystemUtils.getHeapFreeMemory();
        buf.append(", used(after GC): " + used);
        buf.append(", total gc count: " + SystemUtils.countGC());
        System.out.println(buf);
    }

    @Test
    public void testJDKLruMap() {
        System.out.print("testJDKLruMap - ");
        System.gc();
        long free = SystemUtils.getHeapFreeMemory();
        System.out.print("free(init): " + free);
        LRUMap<Integer, Integer> ilru = new LRUMap<Integer, Integer>(LIMIT);
        for(int i = 0; i < LOOP; i++) {
            ilru.put(i, i);
        }
        Random random = new Random();
        for(int i = 0; i < LOOP / 2; i++) {
            int r = random.nextInt(LOOP - 1);
            ilru.put(r, r);
        }
        long used = free - SystemUtils.getHeapFreeMemory();
        System.out.print(", used(before GC): " + used);
        System.gc();
        used = free - SystemUtils.getHeapFreeMemory();
        System.out.print(", used(after GC): " + used);
        System.out.println(", total gc count: " + SystemUtils.countGC());
    }

    @Test
    public void testLongLruMap() {
        StringBuilder buf = new StringBuilder(1024);
        System.gc();
        long free = SystemUtils.getHeapFreeMemory();
        buf.append(" free(init): " + free);
        StopWatch sw = new StopWatch("testLongLruMap");
        LongLRUMap<Integer> lru = new LongLRUMap<Integer>(LIMIT);
        for(int i = 0; i < LOOP; i++) {
            lru.put(i, i);
        }
        buf.insert(0, sw.toString());
        Random random = new Random();
        for(int i = 0; i < LOOP / 2; i++) {
            int r = random.nextInt(LOOP - 1);
            lru.put(r, r);
        }
        long used = free - SystemUtils.getHeapFreeMemory();
        buf.append(", used(before GC): " + used);
        System.gc();
        used = free - SystemUtils.getHeapFreeMemory();
        buf.append(", used(after GC): " + used);
        buf.append(", total gc count: " + SystemUtils.countGC());
        System.out.println(buf);
    }

    @Test
    public void testLong2QMap() {
        StringBuilder buf = new StringBuilder(1024);
        System.gc();
        long free = SystemUtils.getHeapFreeMemory();
        buf.append(" free(init): " + free);
        StopWatch sw = new StopWatch("testLong2QMap");
        Long2QCache<Integer> lru = new Long2QCache<Integer>(LIMIT);
        for(int i = 0; i < LOOP; i++) {
            lru.put(i, i);
        }
        Random random = new Random();
        for(int i = 0; i < LOOP / 2; i++) {
            int r = random.nextInt(LOOP - 1);
            lru.put(r, r);
        }
        buf.insert(0, sw.toString());
        long used = free - SystemUtils.getHeapFreeMemory();
        buf.append(", used(before GC): " + used);
        System.gc();
        used = free - SystemUtils.getHeapFreeMemory();
        buf.append(", used(after GC): " + used);
        buf.append(", total gc count: " + SystemUtils.countGC());
        System.out.println(buf);
    }
}
