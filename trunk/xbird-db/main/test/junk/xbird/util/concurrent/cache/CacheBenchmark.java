/*
 * @(#)$Id$
 *
 * Copyright 2008 Makoto YUI
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
package xbird.util.concurrent.cache;

import java.util.Random;

import org.junit.Test;

import xbird.util.StopWatch;
import xbird.util.cache.ICacheEntry;
import xbird.util.concurrent.cache.helpers.ReplacementPolicySelector;
import xbird.util.concurrent.cache.helpers.ReplacementPolicySelector.ReplacementAlgorithm;
import xbird.util.distribution.ZipfGenerator;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class CacheBenchmark {

    static {
        System.setProperty("xbird.bufman.trace_cachemiss", "true");
    }

    public CacheBenchmark() {}

    @Test
    public void testCacheZipf100K() {
        System.out.println("testCacheZipf100K");
        runAllBenchmarksWithZipfDistribution(10, 100000);
        runAllBenchmarksWithZipfDistribution(100, 100000);
        runAllBenchmarksWithZipfDistribution(500, 100000);
        runAllBenchmarksWithZipfDistribution(1000, 100000);
        runAllBenchmarksWithZipfDistribution(5000, 100000);
        runAllBenchmarksWithZipfDistribution(7500, 100000);
        runAllBenchmarksWithZipfDistribution(10000, 100000);
    }

    @Test
    public void testCacheZipf1000_1000K() {
        System.out.println("testCacheZipf1000_1000K");
        runAllBenchmarksWithZipfDistribution(1024, 1000000);
    }

    @Test
    public void testCacheRandom1000_1000K() {
        System.out.println("testCacheRandom1000_1000K");
        runAllBenchmarksWithRandomDistribution(1024, 1000000);
    }

    //@Test
    public void testCacheZipf1000_10000K() {
        System.out.println("testCacheZipf1000_10000K");
        runAllBenchmarksWithZipfDistribution(1024, 10000000);
    }

    //@Test
    public void testCacheRandom1000_10000K() {
        System.out.println("testCacheRandom1000_10000K");
        runAllBenchmarksWithRandomDistribution(1024, 10000000);
    }

    private static void runAllBenchmarksWithZipfDistribution(int capacity, int round) {
        // skew 1.0
        ZipfGenerator zipf1 = new ZipfGenerator(2.0d);
        long[] data = new long[round];
        for(int i = 0; i < round; i++) {
            data[i] = Math.abs(zipf1.nextLong()) % round;
        }
        runBenchmarkWithZipfDistribution(capacity, round, 1.0, data, ReplacementAlgorithm.GClockK);
        runBenchmarkWithZipfDistribution(capacity, round, 1.0, data, ReplacementAlgorithm.GClock);
        runBenchmarkWithZipfDistribution(capacity, round, 1.0, data, ReplacementAlgorithm.LRU);
        // skew 0.8
        ZipfGenerator zipf2 = new ZipfGenerator(1.8d);
        for(int i = 0; i < round; i++) {
            data[i] = Math.abs(zipf2.nextLong()) % round;
        }
        runBenchmarkWithZipfDistribution(capacity, round, 0.8, data, ReplacementAlgorithm.GClockK);
        runBenchmarkWithZipfDistribution(capacity, round, 0.8, data, ReplacementAlgorithm.GClock);
        runBenchmarkWithZipfDistribution(capacity, round, 0.8, data, ReplacementAlgorithm.LRU);
        // skew 0.5
        ZipfGenerator zipf3 = new ZipfGenerator(1.5d);
        for(int i = 0; i < round; i++) {
            data[i] = Math.abs(zipf3.nextLong()) % round;
        }
        runBenchmarkWithZipfDistribution(capacity, round, 0.5, data, ReplacementAlgorithm.GClockK);
        runBenchmarkWithZipfDistribution(capacity, round, 0.5, data, ReplacementAlgorithm.GClock);
        runBenchmarkWithZipfDistribution(capacity, round, 0.5, data, ReplacementAlgorithm.LRU);
    }

    private static void runBenchmarkWithZipfDistribution(int capacity, int round, double skew, long[] data, ReplacementAlgorithm algo) {
        ConcurrentPluggableCache<Long, Long> cache = new ConcurrentPluggableCache<Long, Long>(capacity);
        cache.setReplacementPolicy(ReplacementPolicySelector.<Long, Long> provide(capacity, algo));

        StopWatch watchdog = new StopWatch("capacity: " + capacity + ", round: " + round);
        for(int i = 0; i < round; i++) {
            long key = data[i];
            ICacheEntry<Long, Long> entry = cache.allocateEntry(key);
            entry.setValue(key);
            entry.unpin();
        }
        long miss = cache.getCacheMiss();
        double ratio = ((double) miss) / round;
        System.err.println("[Zipf (" + skew + ") - " + algo + "] read: " + round + ", miss: "
                + miss + ", miss ratio: " + ratio);
        System.err.println(watchdog.toString());
    }

    private static void runAllBenchmarksWithRandomDistribution(int capacity, int round) {
        runBenchmarkWithRandomDistribution(capacity, round, ReplacementAlgorithm.GClockK);
        runBenchmarkWithRandomDistribution(capacity, round, ReplacementAlgorithm.GClock);
        runBenchmarkWithRandomDistribution(capacity, round, ReplacementAlgorithm.LRU);
    }

    private static void runBenchmarkWithRandomDistribution(int capacity, int round, ReplacementAlgorithm algo) {
        ConcurrentPluggableCache<Long, Long> cache = new ConcurrentPluggableCache<Long, Long>(capacity);
        cache.setReplacementPolicy(ReplacementPolicySelector.<Long, Long> provide(capacity, algo));
        Random rand = new Random(76675734342L);
        StopWatch watchdog = new StopWatch("capacity: " + capacity + ", round: " + round);
        for(int i = 0; i < round; i++) {
            long key = Math.abs(rand.nextLong()) % round;
            ICacheEntry<Long, Long> entry = cache.allocateEntry(key);
            entry.setValue(key);
            entry.unpin();
        }
        System.err.println(watchdog.toString());
        long miss = cache.getCacheMiss();
        double ratio = ((double) miss) / round;
        System.err.println("[Random - " + algo + "] read: " + round + ", miss: " + miss
                + ", miss ratio: " + ratio);
    }

}
