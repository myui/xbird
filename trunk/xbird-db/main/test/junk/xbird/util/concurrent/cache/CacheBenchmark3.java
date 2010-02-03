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
public class CacheBenchmark3 {

    private static final boolean emuratePageIn;
    private static final byte[] dummy = new byte[8192];

    static {
        System.setProperty("xbird.bufman.trace_cachemiss", "true");
        emuratePageIn = System.getProperty("xbird.disable_enum_pagein") == null;
    }

    public CacheBenchmark3() {}

    public static void main(String[] args) {
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] { CacheBenchmark3.class });
        TestListenerAdapter tla = new TestListenerAdapter();
        testng.addListener(tla);
        testng.run();
    }

    @DataProvider(name = "zipf")
    public Object[][] zipfDist1() {
        //return new Object[][] { { "/tmp/zipfgen/640k_zipf0.8.dat", 64, 1000, 10000, 20d, 100 } };
        //return new Object[][] { { "/tmp/zipfgen/640k_zipf0.8.dat", 10, 1000, 64000, 20d, 100 } };
        //return new Object[][] { { "/tmp/zipfgen/100k_zipf0.8_N10k.dat", 10, 1000, 10000, 20d, 100 } };
        // return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.8_N10k.dat", 64, 1000, 10000, 20d, 100, 8 } };
        return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.5_N50k.dat", 64, 50000, 15625, 20d,
                100, 8 } };
    }

    @Test(dataProvider = "zipf", invocationCount = 3)
    public void testRunbenchGClockK(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        System.gc();

        // GClockK
        String filename = FileUtils.basename(inputFile);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        runBenchmarkWithZipfDistribution1(new NoopLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.GClockK, pageSize, reader);
        IOUtils.closeQuietly(reader);
    }

    @Test(dataProvider = "zipf", invocationCount = 3)
    public void testRunbenchGClock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        System.gc();

        // GClock
        String filename = FileUtils.basename(inputFile);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        runBenchmarkWithZipfDistribution1(new NoopLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.GClock, pageSize, reader);
        IOUtils.closeQuietly(reader);
    }

    @Test(dataProvider = "zipf", invocationCount = 3)
    public void testRunbenchLRUSpinlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        System.gc();

        // LRU + spinlock
        String filename = FileUtils.basename(inputFile);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        runBenchmarkWithZipfDistribution1(new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, reader);
        IOUtils.closeQuietly(reader);

        System.gc();

        reader = new BufferedReader(new FileReader(file));
        runBenchmarkWithZipfDistribution2(new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, reader);
        IOUtils.closeQuietly(reader);
    }

    @Test(dataProvider = "zipf", invocationCount = 3)
    public void testRunbenchLRUSync(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        System.gc();

        // LRU + sync
        String filename = FileUtils.basename(inputFile);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        runBenchmarkWithZipfDistribution1(null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, reader);
        IOUtils.closeQuietly(reader);

        System.gc();

        reader = new BufferedReader(new FileReader(file));
        runBenchmarkWithZipfDistribution2(null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, reader);
        IOUtils.closeQuietly(reader);
    }

    @Test(dataProvider = "zipf", invocationCount = 3)
    public void testRunbench2QSpinlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        System.gc();

        // 2Q + spinlock
        String filename = FileUtils.basename(inputFile);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        runBenchmarkWithZipfDistribution2(new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, reader);
        IOUtils.closeQuietly(reader);
    }

    @Test(dataProvider = "zipf", invocationCount = 3)
    public void testRunbench2QSync(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        System.gc();

        // 2Q + sync
        String filename = FileUtils.basename(inputFile);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        runBenchmarkWithZipfDistribution2(null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, reader);
        IOUtils.closeQuietly(reader);
    }

    private static void runBenchmarkWithZipfDistribution1(final SpinLock lock, final String filename, final int threads, final int capacity, final int round, final double scanPercentage, final int scanLength, final ReplacementAlgorithm algo, final int pageSize, final BufferedReader reader)
            throws IOException {
        final ConcurrentPluggableCache<Long, byte[]> cache = new ConcurrentPluggableCache<Long, byte[]>(capacity);
        cache.setReplacementPolicy(ReplacementPolicySelector.<Long, byte[]> provide(capacity, algo));

        final int total = round * threads;
        final double percent = scanPercentage / 100d;
        StopWatch watchdog = new StopWatch("nthreads: " + threads + ", capacity: " + capacity
                + ", invocations: " + total + ", scanPercentage: " + percent + ", scanLength: "
                + scanLength);

        final FixedSegments pager = makeTempSegments(pageSize);

        final ExecutorService exec = Executors.newFixedThreadPool(threads);
        try {
            for(int i = 0; i < threads; i++) {
                exec.submit(new Runnable() {
                    public void run() {
                        try {
                            if(lock == null) {
                                runBenchmarkWithZipfDistributionSync(pager, cache, capacity, round, percent, scanLength, reader);
                            } else {
                                runBenchmarkWithZipfDistributionLock(lock, pager, cache, capacity, round, percent, scanLength, reader);
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

    private static void runBenchmarkWithZipfDistribution2(final SpinLock lock, final String filename, final int threads, final int capacity, final int round, final double scanPercentage, final int scanLength, final ReplacementAlgorithm algo, final int pageSize, final BufferedReader reader)
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
                + scanLength);

        final FixedSegments pager = makeTempSegments(pageSize);

        final ExecutorService exec = Executors.newFixedThreadPool(threads);
        try {
            for(int i = 0; i < threads; i++) {
                exec.submit(new Runnable() {
                    public void run() {
                        try {
                            if(lock == null) {
                                runBenchmarkWithZipfDistributionLongHashSync(pager, hash, capacity, round, percent, scanLength, reader);
                            } else {
                                runBenchmarkWithZipfDistributionLongHashLock(lock, pager, hash, capacity, round, percent, scanLength, reader);
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

    private static void runBenchmarkWithZipfDistributionLock(SpinLock lock, FixedSegments pager, ConcurrentPluggableCache<Long, byte[]> cache, int capacity, int round, double percent, int scanLength, BufferedReader reader) {
        boolean scanning = false;
        long scanCount = 0L;

        int scanned = 0, zipf = 0;
        long base = 0L;

        for(int nth = 0; nth < round; nth++) {
            final long key;
            if(scanning || scanLength != 0 && (nth % scanLength == 0 && Math.random() < percent)) {
                if(++scanCount <= scanLength) {
                    if(!scanning) {
                        base = readNextLong(reader);
                    }
                    key = base + scanCount;
                    scanning = true;
                    scanned++;
                } else {
                    key = readNextLong(reader);
                    scanCount = 0;
                    scanning = false;
                    zipf++;
                }
            } else {
                key = readNextLong(reader);
                zipf++;
            }
            lock.lock();
            ICacheEntry<Long, byte[]> entry = cache.allocateEntry(key);
            if(entry.getValue() == null) {
                byte[] b = emurateReadInPage(pager, capacity, key);
                entry.setValue(b);
            }
            lock.unlock();
            entry.unpin();
        }
        //System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
        //        + ", zipf: " + zipf);
        //System.out.flush();
    }

    private static void runBenchmarkWithZipfDistributionSync(FixedSegments pager, ConcurrentPluggableCache<Long, byte[]> cache, int capacity, int round, double percent, int scanLength, BufferedReader reader) {
        boolean scanning = false;
        long scanCount = 0L;

        int scanned = 0, zipf = 0;
        long base = 0L;

        for(int nth = 0; nth < round; nth++) {
            final long key;
            if(scanning || scanLength != 0 && (nth % scanLength == 0 && Math.random() < percent)) {
                if(++scanCount <= scanLength) {
                    if(!scanning) {
                        base = readNextLong(reader);
                    }
                    key = base + scanCount;
                    scanning = true;
                    scanned++;
                } else {
                    key = readNextLong(reader);
                    scanCount = 0;
                    scanning = false;
                    zipf++;
                }
            } else {
                key = readNextLong(reader);
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
        //System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
        //        + ", zipf: " + zipf);
        //System.out.flush();
    }

    private static void runBenchmarkWithZipfDistributionLongHashLock(SpinLock lock, FixedSegments pager, LongHash<byte[]> hash, int capacity, int round, double percent, int scanLength, BufferedReader reader) {
        boolean scanning = false;
        long scanCount = 0L;

        int scanned = 0, zipf = 0;
        long base = 0L;

        for(int nth = 0; nth < round; nth++) {
            final long key;
            if(scanning || scanLength != 0 && (nth % scanLength == 0 && Math.random() < percent)) {
                if(++scanCount <= scanLength) {
                    if(!scanning) {
                        base = readNextLong(reader);
                    }
                    key = base + scanCount;
                    scanning = true;
                    scanned++;
                } else {
                    key = readNextLong(reader);
                    scanCount = 0;
                    scanning = false;
                    zipf++;
                }
            } else {
                key = readNextLong(reader);
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
        //System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
        //        + ", zipf: " + zipf);
        //System.out.flush();
    }

    private static void runBenchmarkWithZipfDistributionLongHashSync(FixedSegments pager, LongHash<byte[]> hash, int capacity, int round, double percent, int scanLength, BufferedReader reader) {
        boolean scanning = false;
        long scanCount = 0L;

        int scanned = 0, zipf = 0;
        long base = 0L;

        for(int nth = 0; nth < round; nth++) {
            final long key;
            if(scanning || scanLength != 0 && (nth % scanLength == 0 && Math.random() < percent)) {
                if(++scanCount <= scanLength) {
                    if(!scanning) {
                        base = readNextLong(reader);
                    }
                    key = base + scanCount;
                    scanning = true;
                    scanned++;
                } else {
                    key = readNextLong(reader);
                    scanCount = 0;
                    scanning = false;
                    zipf++;
                }
            } else {
                key = readNextLong(reader);
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
        //System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
        //        + ", zipf: " + zipf);
        //System.out.flush();
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

    private static long readNextLong(BufferedReader reader) {
        try {
            String line = reader.readLine();
            assert (line != null);
            return Long.parseLong(line.trim());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }
}
