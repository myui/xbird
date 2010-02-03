/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
 *
 * Copyright (c) 2005-2006 Makoto YUI and Project XBird
 * All rights reserved.
 * 
 * This file is part of XBird and is distributed under the terms of
 * the Common Public License v1.0.
 * 
 * Contributors:
 *     Makoto YUI - initial implementation
 */
package xbird.util.concurrent.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import xbird.storage.io.FixedSegments;
import xbird.util.StopWatch;
import xbird.util.cache.ICacheEntry;
import xbird.util.collections.Long2QCache;
import xbird.util.collections.LongHash;
import xbird.util.concurrent.cache.helpers.ReplacementPolicySelector;
import xbird.util.concurrent.cache.helpers.ReplacementPolicySelector.ReplacementAlgorithm;
import xbird.util.concurrent.collections.ConcurrentCollectionProvider;
import xbird.util.concurrent.lock.AtomicBackoffLock;
import xbird.util.concurrent.lock.NoopLock;
import xbird.util.concurrent.lock.SpinLock;
import xbird.util.io.FileUtils;
import xbird.util.io.IOUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class CacheBenchmark4 {

    private static final boolean showstdout = true;
    private static final boolean emuratePageIn;
    private static final byte[] dummy = new byte[8192];

    static {
        System.setProperty("xbird.bufman.trace_cachemiss", "true");
        emuratePageIn = System.getProperty("xbird.disable_enum_pagein") == null;
    }

    public CacheBenchmark4() {}

    public static void main(String[] args) {
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] { CacheBenchmark4.class });
        TestListenerAdapter tla = new TestListenerAdapter();
        testng.addListener(tla);
        testng.run();
    }

    @DataProvider(name = "zipf")
    public Object[][] zipfDist1() {
        //return new Object[][] { { "/tmp/zipfgen/640k_zipf0.8.dat", 64, 1000, 10000, 20d, 100 } };
        //return new Object[][] { { "/tmp/zipfgen/640k_zipf0.8.dat", 10, 1000, 64000, 20d, 100 } };
        //return new Object[][] { { "/tmp/zipfgen/100k_zipf0.8_N10k.dat", 10, 1000, 10000, 20d, 100 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.8_N10k.dat", 64, 1000, 10000, 20d, 100, 8 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 128, 16384, 7812, 20d, 100, 8 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 64, 4096, 15625, 20d, 100, 64 } };
        return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 64, 8192, 15625, 20d,
                100, 64 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 64, 16384, 15625, 20d, 100, 64 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 64, 32768, 15625, 20d, 100, 64 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 32, 16384, 31250, 20d, 100, 8 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 16, 16384, 62500, 20d, 100, 8 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 8, 16384, 125000, 20d, 100, 8 } };
    }

    private static long[][] readDistribution(File file, int nthreads, int round) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        long[][] dist = new long[nthreads][];
        for(int th = 0; th < nthreads; th++) {
            long[] curdist = new long[round];
            dist[th] = curdist;
            for(int i = 0; i < round; i++) {
                String line = reader.readLine();
                curdist[i] = Long.parseLong(line.trim());
            }
        }
        IOUtils.closeQuietly(reader);
        return dist;
    }

    @Test(dataProvider = "zipf", invocationCount = 3)
    public void testRunbenchGClock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        System.gc();

        // GClock        
        runBenchmarkWithZipfDistribution1(new NoopLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.GClock, pageSize, dist);
    }

    @Test(dataProvider = "zipf", invocationCount = 3)
    public void testRunbenchGClockK(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        System.gc();

        // GClock        
        runBenchmarkWithZipfDistribution1(new NoopLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.GClockK, pageSize, dist);
    }

    @Test(dataProvider = "zipf", invocationCount = 3)
    public void testRunbenchLRUSpinlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        // LRU + spinlock        
        //System.gc();
        //runBenchmarkWithZipfDistribution1(new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, dist);

        System.gc();
        runBenchmarkWithZipfDistribution2(new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, dist);
    }

    @Test(dataProvider = "zipf", invocationCount = 3)
    public void testRunbenchLRUSync(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        // LRU + sync
        //System.gc();
        //runBenchmarkWithZipfDistribution1(null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, dist);

        System.gc();
        runBenchmarkWithZipfDistribution2(null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, dist);
    }

    @Test(dataProvider = "zipf", invocationCount = 3)
    public void testRunbench2QSpinlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        // 2Q + spinlock
        //System.gc();
        //runBenchmarkWithZipfDistribution1(new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, dist);

        System.gc();
        runBenchmarkWithZipfDistribution2(new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, dist);
    }

    @Test(dataProvider = "zipf", invocationCount = 3)
    public void testRunbench2QSync(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        // 2Q + sync
        //System.gc();
        //runBenchmarkWithZipfDistribution1(null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, dist);

        System.gc();
        runBenchmarkWithZipfDistribution2(null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, dist);
    }

    private static void runBenchmarkWithZipfDistribution1(final SpinLock lock, final String filename, final int threads, final int capacity, final int round, final double scanPercentage, final int scanLength, final ReplacementAlgorithm algo, final int pageSize, final long[][] dist)
            throws IOException {
        final ConcurrentPluggableCache<Long, byte[]> cache = new ConcurrentPluggableCache<Long, byte[]>(capacity, ConcurrentCollectionProvider.<ICacheEntry<Long, byte[]>> createConcurrentMapLong(capacity));
        cache.setReplacementPolicy(ReplacementPolicySelector.<Long, byte[]> provide(capacity, algo));

        final int total = round * threads;
        final double percent = scanPercentage / 100d;
        StopWatch watchdog = new StopWatch("nthreads: " + threads + ", capacity: " + capacity
                + ", invocations: " + total + ", scanPercentage: " + percent + ", scanLength: "
                + scanLength + ", pageSize: " + pageSize);

        final FixedSegments pager = makeTempSegments(pageSize);

        final ExecutorService exec = Executors.newFixedThreadPool(threads);
        try {
            for(int i = 0; i < threads; i++) {
                final int threadId = i;
                exec.submit(new Runnable() {
                    public void run() {
                        try {
                            if(lock == null) {
                                runBenchmarkWithZipfDistributionSync(pager, cache, capacity, round, percent, scanLength, dist[threadId]);
                            } else {
                                runBenchmarkWithZipfDistributionLock(lock, pager, cache, capacity, round, percent, scanLength, dist[threadId]);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } finally {
            exec.shutdown();
            try {
                exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                ;
            }
        }

        long miss = cache.getCacheMiss();
        long hit = total - miss;
        double hitRatio = ((double) hit) / total;
        double missRatio = ((double) miss) / total;
        System.err.println("["
                + algo
                + " - "
                + filename
                + "] synchronization: "
                + (lock == null ? "sync" : ((lock instanceof NoopLock) ? "non-blocking"
                        : "spinlock")) + ", read: " + total + ", miss: " + miss + ", hit ratio: "
                + hitRatio + ", miss ratio: " + missRatio);
        System.err.println(watchdog.toString());
        System.err.flush();

        pager.close();
    }

    private static void runBenchmarkWithZipfDistribution2(final SpinLock lock, final String filename, final int threads, final int capacity, final int round, final double scanPercentage, final int scanLength, final ReplacementAlgorithm algo, final int pageSize, final long[][] dist)
            throws IOException {
        final LongHash<byte[]> hash;
        switch(algo) {
            case LRU:
                hash = new LongHash.LongLRUMap<byte[]>(capacity);
                break;
            case FullTwoQueue:
                hash = new Long2QCache<byte[]>(capacity);
                break;
            default:
                throw new IllegalStateException("unexpected algorithm for here: " + algo);
        }

        final int total = round * threads;
        final double percent = scanPercentage / 100d;
        StopWatch watchdog = new StopWatch("nthreads: " + threads + ", capacity: " + capacity
                + ", invocations: " + total + ", scanPercentage: " + percent + ", scanLength: "
                + scanLength + ", pageSize: " + pageSize);

        final FixedSegments pager = makeTempSegments(pageSize);

        final ExecutorService exec = Executors.newFixedThreadPool(threads);
        try {
            for(int i = 0; i < threads; i++) {
                final int threadId = i;
                exec.submit(new Runnable() {
                    public void run() {
                        try {
                            if(lock == null) {
                                runBenchmarkWithZipfDistributionLongHashSync(pager, hash, capacity, round, percent, scanLength, dist[threadId]);
                            } else {
                                runBenchmarkWithZipfDistributionLongHashLock(lock, pager, hash, capacity, round, percent, scanLength, dist[threadId]);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } finally {
            exec.shutdown();
            try {
                exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                ;
            }
        }

        long miss = hash.getCacheMiss();
        long hit = total - miss;
        double hitRatio = ((double) hit) / total;
        double missRatio = ((double) miss) / total;
        System.err.println("["
                + algo
                + " longhash - "
                + filename
                + "] synchronization: "
                + (lock == null ? "sync" : ((lock instanceof NoopLock) ? "non-blocking"
                        : "spinlock")) + ", read: " + total + ", miss: " + miss + ", hit ratio: "
                + hitRatio + ", miss ratio: " + missRatio);
        System.err.println(watchdog.toString());
        System.err.flush();

        pager.close();
    }

    private static FixedSegments makeTempSegments(int pageSize) throws IOException {
        File tmpFile = new File("/experiment/data/vldb/xmark10.xml.dtms");
        if(!tmpFile.exists()) {
            throw new IllegalStateException("File not found: " + tmpFile.getAbsolutePath());
        }
        FixedSegments segment = new FixedSegments(tmpFile, pageSize * 1024);
        return segment;
    }

    private static void runBenchmarkWithZipfDistributionLock(SpinLock lock, FixedSegments pager, ConcurrentPluggableCache<Long, byte[]> cache, int capacity, int round, double percent, int scanLength, long[] dist) {
        boolean scanning = false;
        long scanCount = 0L;

        int scanned = 0, zipf = 0;
        long base = 0L;

        for(int nth = 0; nth < round; nth++) {
            final long key;
            if(scanning || scanLength != 0 && (nth % scanLength == 0 && Math.random() < percent)) {
                if(++scanCount <= scanLength) {
                    if(!scanning) {
                        base = dist[nth];
                    }
                    key = base + scanCount;
                    scanning = true;
                    scanned++;
                } else {
                    key = dist[nth];
                    scanCount = 0;
                    scanning = false;
                    zipf++;
                }
            } else {
                key = dist[nth];
                zipf++;
            }
            lock.lock();
            ICacheEntry<Long, byte[]> entry = cache.allocateEntry(key);
            if(entry.getValue() == null) {
                byte[] b = emurateReadInPage(pager, capacity, key);
                entry.setValue(b);
            }
            entry.unpin();
            lock.unlock();
        }
        if(showstdout) {
            System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
                    + ", zipf: " + zipf);
            System.out.flush();
        }
    }

    private static void runBenchmarkWithZipfDistributionSync(FixedSegments pager, ConcurrentPluggableCache<Long, byte[]> cache, int capacity, int round, double percent, int scanLength, long[] dist) {
        boolean scanning = false;
        long scanCount = 0L;

        int scanned = 0, zipf = 0;
        long base = 0L;

        for(int nth = 0; nth < round; nth++) {
            final long key;
            if(scanning || scanLength != 0 && (nth % scanLength == 0 && Math.random() < percent)) {
                if(++scanCount <= scanLength) {
                    if(!scanning) {
                        base = dist[nth];
                    }
                    key = base + scanCount;
                    scanning = true;
                    scanned++;
                } else {
                    key = dist[nth];
                    scanCount = 0;
                    scanning = false;
                    zipf++;
                }
            } else {
                key = dist[nth];
                zipf++;
            }
            final ICacheEntry<Long, byte[]> entry;
            synchronized(cache) {
                entry = cache.allocateEntry(key);
                if(entry.getValue() == null) {
                    byte[] b = emurateReadInPage(pager, capacity, key);
                    entry.setValue(b);
                }
                entry.unpin();
            }
        }
        if(showstdout) {
            System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
                    + ", zipf: " + zipf);
            System.out.flush();
        }
    }

    private static void runBenchmarkWithZipfDistributionLongHashLock(SpinLock lock, FixedSegments pager, LongHash<byte[]> hash, int capacity, int round, double percent, int scanLength, long[] dist) {
        boolean scanning = false;
        long scanCount = 0L;

        int scanned = 0, zipf = 0;
        long base = 0L;

        for(int nth = 0; nth < round; nth++) {
            final long key;
            if(scanning || scanLength != 0 && (nth % scanLength == 0 && Math.random() < percent)) {
                if(++scanCount <= scanLength) {
                    if(!scanning) {
                        base = dist[nth];
                    }
                    key = base + scanCount;
                    scanning = true;
                    scanned++;
                } else {
                    key = dist[nth];
                    scanCount = 0;
                    scanning = false;
                    zipf++;
                }
            } else {
                key = dist[nth];
                zipf++;
            }
            lock.lock();
            byte[] entry = hash.get(key);
            if(entry == null) {
                entry = emurateReadInPage(pager, capacity, key);
                hash.put(key, entry);
            }
            lock.unlock();
        }
        if(showstdout) {
            System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
                    + ", zipf: " + zipf);
            System.out.flush();
        }
    }

    private static void runBenchmarkWithZipfDistributionLongHashSync(FixedSegments pager, LongHash<byte[]> hash, int capacity, int round, double percent, int scanLength, long[] dist) {
        boolean scanning = false;
        long scanCount = 0L;

        int scanned = 0, zipf = 0;
        long base = 0L;

        for(int nth = 0; nth < round; nth++) {
            final long key;
            if(scanning || scanLength != 0 && (nth % scanLength == 0 && Math.random() < percent)) {
                if(++scanCount <= scanLength) {
                    if(!scanning) {
                        base = dist[nth];
                    }
                    key = base + scanCount;
                    scanning = true;
                    scanned++;
                } else {
                    key = dist[nth];
                    scanCount = 0;
                    scanning = false;
                    zipf++;
                }
            } else {
                key = dist[nth];
                zipf++;
            }
            synchronized(hash) {
                byte[] entry = hash.get(key);
                if(entry == null) {
                    entry = emurateReadInPage(pager, capacity, key);
                    hash.put(key, entry);
                }
            }
        }
        if(showstdout) {
            System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
                    + ", zipf: " + zipf);
            System.out.flush();
        }
    }

    private static byte[] emurateReadInPage(FixedSegments pager, int capacity, long key) {
        if(!emuratePageIn) {
            return dummy;
        }
        try {
            return pager.read(key % capacity);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }
}
