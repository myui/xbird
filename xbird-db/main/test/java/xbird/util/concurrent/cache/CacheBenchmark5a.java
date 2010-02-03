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
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import org.testng.Assert;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import xbird.server.ServiceException;
import xbird.storage.io.FixedSegments;
import xbird.util.cache.ICacheEntry;
import xbird.util.collections.longs.Long2QCache;
import xbird.util.collections.longs.LongHash;
import xbird.util.concurrent.cache.helpers.ReplacementAlgorithm;
import xbird.util.concurrent.cache.helpers.ReplacementPolicySelector;
import xbird.util.concurrent.collections.ConcurrentCollectionProvider;
import xbird.util.concurrent.lock.AtomicBackoffLock;
import xbird.util.concurrent.lock.FairReadWriteLock;
import xbird.util.concurrent.lock.ILock;
import xbird.util.concurrent.lock.NoopLock;
import xbird.util.datetime.StopWatch;
import xbird.util.io.FileUtils;
import xbird.util.io.IOUtils;

import com.sun.forte.st.collector.CollectorAPI;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class CacheBenchmark5a {

    private static final int TIME_TO_EST = 60000;
    private static final boolean showstdout = false;
    private static final boolean emuratePageIn;
    private static final boolean enableDprofile;
    private static final byte[] dummy = new byte[8192];

    static {
        emuratePageIn = System.getProperty("xbird.disable_enum_pagein") == null;
        enableDprofile = System.getProperty("xbird.enable_dprofile") != null;
    }

    public CacheBenchmark5a() {}

    static volatile boolean _start, _stop;

    public static void main(String[] args) {
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] { CacheBenchmark5a.class });
        TestListenerAdapter tla = new TestListenerAdapter();
        testng.addListener(tla);
        if(enableDprofile) {
            CollectorAPI.sample("bench5.1.er");
            testng.run();
            //CollectorAPI.terminate();
        } else {
            testng.run();
        }
    }

    @DataProvider(name = "zipf")
    public Object[][] zipfDist1() {
        //return new Object[][] { { "/tmp/zipfgen/640k_zipf0.8.dat", 64, 1000, 10000, 20d, 100 } };
        //return new Object[][] { { "/tmp/zipfgen/640k_zipf0.8.dat", 10, 1000, 64000, 20d, 100 } };
        //return new Object[][] { { "/tmp/zipfgen/100k_zipf0.8_N10k.dat", 10, 1000, 10000, 20d, 100 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.8_N10k.dat", 64, 1000, 10000, 20d, 100, 8 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 128, 16384, 7812, 20d, 100, 8 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 64, 4096, 15625, 20d, 100, 64 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 64, 4096, 15625, 20d, 100, 8 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.7_N30k.dat", 8, 8192, 31250, 20d, 100, 8 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 8, 4096, 15625, 20d, 100, 8 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 64, 16384, 15625, 20d, 100, 64 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 64, 32768, 15625, 20d, 100, 64 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 32, 16384, 31250, 20d, 100, 8 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 16, 16384, 62500, 20d, 100, 8 } };
        //return new Object[][] { { "/tmp/zipfgen/1000k_zipf0.86_N50k.dat", 8, 16384, 125000, 20d, 100, 8 } };
        return new Object[][] { { "/experiment/zipfgen/zipf-S4000000-L1-R4000000-P10.dat", 64,
                131072 /* 1024 * 1024 / 8 */, 15625 /* 1000000 / 64 */, 20d, 100, 8 } };
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

    @Test(dataProvider = "zipf")
    //, invocationCount = 3)
    public void testRunbenchGClock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        System.gc();

        // GClock        
        runBenchmarkWithZipfDistribution1(false, new NoopLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.NbGClock, pageSize, dist);
    }

    //@Test(dataProvider = "zipf")
    //, invocationCount = 3)
    public void testRunbenchGClockK(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        System.gc();

        // GClock        
        runBenchmarkWithZipfDistribution1(false, new NoopLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.NbGClockK, pageSize, dist);
    }

    @Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchNormalGClock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        System.gc();
        runBenchmarkWithZipfDistribution1(false, new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.GClock, pageSize, dist);
    }

    //@Test(dataProvider = "zipf", timeOut=70000)
    //, invocationCount = 3)
    public void testRunbenchLRURWlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        System.gc();
        runBenchmarkWithZipfDistribution1(true, null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, dist);
    }

    @Test(dataProvider = "zipf")
    //, invocationCount = 3)
    public void testRunbenchLRUSpinlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        // LRU + spinlock        
        System.gc();
        runBenchmarkWithZipfDistribution1(false, new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, dist);

        //System.gc();
        //runBenchmarkWithZipfDistribution2(new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, dist);
    }

    //@Test(dataProvider = "zipf")
    //, invocationCount = 3)
    public void testRunbenchLRUSync(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        // LRU + sync
        System.gc();
        runBenchmarkWithZipfDistribution1(false, null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, dist);

        // System.gc();
        // runBenchmarkWithZipfDistribution2(null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, dist);
    }

    //@Test(dataProvider = "zipf")
    //, invocationCount = 3)
    public void testRunbench2QRWlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        System.gc();
        runBenchmarkWithZipfDistribution1(true, null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, dist);
    }

    @Test(dataProvider = "zipf")
    //, invocationCount = 3)
    public void testRunbench2QSpinlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        // 2Q + spinlock
        System.gc();
        runBenchmarkWithZipfDistribution1(false, new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, dist);

        //System.gc();
        //runBenchmarkWithZipfDistribution2(new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, dist);
    }

    //@Test(dataProvider = "zipf")
    //, invocationCount = 3)
    public void testRunbench2QSync(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }

        String filename = FileUtils.basename(inputFile);
        long[][] dist = readDistribution(file, threads, round);

        // 2Q + sync
        System.gc();
        runBenchmarkWithZipfDistribution1(false, null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, dist);

        //System.gc();
        //runBenchmarkWithZipfDistribution2(null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, dist);
    }

    private static void runBenchmarkWithZipfDistribution1(final boolean useRWlock, final ILock lock, final String filename, final int threads, final int capacity, final int round, final double scanPercentage, final int scanLength, final ReplacementAlgorithm algo, final int pageSize, final long[][] dist)
            throws IOException, InterruptedException {
        final ConcurrentPluggableCache<Long, byte[]> cache = new ConcurrentPluggableCache<Long, byte[]>(capacity, ConcurrentCollectionProvider.<ICacheEntry<Long, byte[]>> createConcurrentMapLong(capacity));
        cache.setReplacementPolicy(ReplacementPolicySelector.<Long, byte[]> provide(capacity, algo));

        final double percent = scanPercentage / 100d;
        StopWatch watchdog = new StopWatch("nthreads: " + threads + ", capacity: " + capacity
                + ", scanPercentage: " + percent + ", scanLength: " + scanLength + ", pageSize: "
                + pageSize);

        final FixedSegments pager = makeTempSegments(pageSize);
        final Status[] stats = new Status[threads];

        final ReadWriteLock rwlock = new FairReadWriteLock();//new ReentrantReadWriteLock();

        _start = _stop = false;
        final Thread[] thrs = new Thread[threads];
        for(int i = 0; i < threads; i++) {
            final int threadId = i;
            final Status stat = new Status();
            stats[i] = stat;
            thrs[i] = new Thread() {
                public void run() {
                    while(!_start) {
                        // Spin till Time To Go
                        try {
                            Thread.sleep(1);
                        } catch (Exception e) {
                        }
                    }
                    try {
                        if(lock == null) {
                            if(useRWlock) {
                                runBenchmarkWithZipfDistributionReadWriteLock(rwlock, stat, pager, cache, capacity, round, percent, scanLength, dist[threadId]);
                            } else {
                                runBenchmarkWithZipfDistributionSync(stat, pager, cache, capacity, round, percent, scanLength, dist[threadId]);
                            }
                        } else {
                            runBenchmarkWithZipfDistributionLock(stat, lock, pager, cache, capacity, round, percent, scanLength, dist[threadId]);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        // Run threads
        for(int i = 0; i < threads; i++) {
            thrs[i].start();
        }
        _start = true;
        try {
            Thread.sleep(TIME_TO_EST);
        } catch (InterruptedException e) {
        }
        _stop = true;
        for(int i = 0; i < threads; i++) {
            thrs[i].join();
        }

        long numOps = 0L, cacheMisses = 0L, elapsed = 0L, execInterval = 0L, durationHold = 0L;
        for(int i = 0; i < threads; i++) {
            numOps += stats[i].ops;
            cacheMisses += stats[i].miss;
            elapsed += stats[i].mills;
            execInterval += stats[i].execInterval;
            durationHold += stats[i].durationHold;
        }
        long avgElapsedInSec = (elapsed / threads) / 1000L;
        long opsPerSec = numOps / avgElapsedInSec;

        long hit = numOps - cacheMisses;
        double hitRatio = ((double) hit) / numOps;
        double missRatio = ((double) cacheMisses) / numOps;
        System.err.println("["
                + algo
                + " - "
                + filename
                + "] synchronization: "
                + (lock == null ? (useRWlock ? "readWrite" : "sync")
                        : ((lock instanceof NoopLock) ? "non-blocking" : "spinlock")) + ", read: "
                + numOps + ", miss: " + cacheMisses + ", hit ratio: " + hitRatio + ", miss ratio: "
                + missRatio);
        System.err.println(watchdog.toString());
        System.err.println("number of ops: " + numOps + ", average number of ops per second: "
                + opsPerSec + ", total execution interval(msec): " + execInterval
                + ", total duration hold(msec): " + durationHold);
        System.err.flush();

        pager.close();
    }

    private static void runBenchmarkWithZipfDistribution2(final ILock lock, final String filename, final int threads, final int capacity, final int round, final double scanPercentage, final int scanLength, final ReplacementAlgorithm algo, final int pageSize, final long[][] dist)
            throws IOException, InterruptedException {
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

        final double percent = scanPercentage / 100d;
        StopWatch watchdog = new StopWatch("nthreads: " + threads + ", capacity: " + capacity
                + ", scanPercentage: " + percent + ", scanLength: " + scanLength + ", pageSize: "
                + pageSize);

        final FixedSegments pager = makeTempSegments(pageSize);
        final Status[] stats = new Status[threads];

        _start = _stop = false;
        final Thread[] thrs = new Thread[threads];
        for(int i = 0; i < threads; i++) {
            final int threadId = i;
            final Status stat = new Status();
            stats[i] = stat;
            thrs[i] = new Thread() {
                public void run() {
                    while(!_start) {
                        // Spin till Time To Go
                        try {
                            Thread.sleep(1);
                        } catch (Exception e) {
                        }
                    }
                    try {
                        if(lock == null) {
                            runBenchmarkWithZipfDistributionLongHashSync(stat, pager, hash, capacity, round, percent, scanLength, dist[threadId]);
                        } else {
                            runBenchmarkWithZipfDistributionLongHashLock(stat, lock, pager, hash, capacity, round, percent, scanLength, dist[threadId]);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        // Run threads
        for(int i = 0; i < threads; i++) {
            thrs[i].start();
        }
        _start = true;
        try {
            Thread.sleep(TIME_TO_EST);
        } catch (InterruptedException e) {
        }
        _stop = true;
        for(int i = 0; i < threads; i++) {
            thrs[i].join();
        }

        long numOps = 0L, cacheMisses = 0L, elapsed = 0L;
        for(int i = 0; i < threads; i++) {
            numOps += stats[i].ops;
            cacheMisses += stats[i].miss;
            elapsed += stats[i].mills;
        }
        long avgElapsedInSec = (elapsed / threads) / 1000;
        long opsPerSec = numOps / avgElapsedInSec;

        long hit = numOps - cacheMisses;
        double hitRatio = ((double) hit) / numOps;
        double missRatio = ((double) cacheMisses) / numOps;
        System.err.println("["
                + algo
                + " longhash - "
                + filename
                + "] synchronization: "
                + (lock == null ? "sync" : ((lock instanceof NoopLock) ? "non-blocking"
                        : "spinlock")) + ", read: " + numOps + ", miss: " + cacheMisses
                + ", hit ratio: " + hitRatio + ", miss ratio: " + missRatio);
        System.err.println(watchdog.toString());
        System.err.println("number of ops: " + numOps + ", average number of ops per second: "
                + opsPerSec);
        System.err.flush();

        pager.close();
    }

    private static FixedSegments makeTempSegments(int pageSize) throws IOException {
        File tmpFile = new File("/experiment/data/xmark32G.xml");
        if(!tmpFile.exists()) {
            throw new IllegalStateException("File not found: " + tmpFile.getAbsolutePath());
        }
        FixedSegments segment = new FixedSegments(tmpFile, pageSize * 1024);
        return segment;
    }

    private static void runBenchmarkWithZipfDistributionLock(Status stat, ILock lock, FixedSegments pager, ConcurrentPluggableCache<Long, byte[]> cache, int capacity, int round, double percent, int scanLength, long[] dist) {
        boolean scanning = false;
        long scanCount = 0L;

        final Random rand = new Random();

        int scanned = 0, zipf = 0;
        long base = 0L;

        long mills1 = System.currentTimeMillis();
        long ops = 0L;
        long miss = 0L;

        long totalExecInterval = 0L, totalDurationHold = 0L;
        long prelock = System.currentTimeMillis();

        final int limit = round - 1;
        for(int nth = 0, i = 0; !_stop; nth = (nth >= limit) ? 0 : nth + 1, i++) {
            final long key;
            if(scanning || scanLength != 0 && (i == scanLength && rand.nextDouble() < percent)) {
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
                    i = 0;
                    zipf++;
                }
            } else {
                key = dist[nth];
                zipf++;
            }
            long curr = System.currentTimeMillis();
            totalExecInterval += (curr - prelock);
            prelock = curr;
            lock.lock();
            long postlock = System.currentTimeMillis();
            final ICacheEntry<Long, byte[]> entry = cache.fixEntry(key);
            lock.unlock();
            long postunlock = System.currentTimeMillis();
            totalDurationHold += (postunlock - postlock);
            if(entry.getValue() == null) {
                byte[] b = emurateReadInPage(pager, capacity, key);
                entry.setValue(b);
                miss++;
            }
            entry.unpin();
            ops++;
        }

        long mills2 = System.currentTimeMillis();
        stat.mills = mills2 - mills1;
        stat.ops = ops;
        stat.miss = miss;
        stat.execInterval = totalExecInterval;
        stat.durationHold = totalDurationHold;

        if(showstdout) {
            System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
                    + ", zipf: " + zipf);
            System.out.flush();
        }
    }

    private static void runBenchmarkWithZipfDistributionReadWriteLock(ReadWriteLock rwlock, Status stat, FixedSegments pager, ConcurrentPluggableCache<Long, byte[]> cache, int capacity, int round, double percent, int scanLength, long[] dist) {
        boolean scanning = false;
        long scanCount = 0L;

        final Random rand = new Random();

        int scanned = 0, zipf = 0;
        long base = 0L;

        long mills1 = System.currentTimeMillis();
        long ops = 0L;
        long miss = 0L;

        final Lock readLock = rwlock.readLock();
        final Lock writeLock = rwlock.writeLock();

        final int limit = round - 1;
        for(int nth = 0, i = 0; !_stop; nth = (nth >= limit) ? 0 : nth + 1, i++) {
            final long key;
            if(scanning || scanLength != 0 && (i == scanLength && rand.nextDouble() < percent)) {
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
                    i = 0;
                    zipf++;
                }
            } else {
                key = dist[nth];
                zipf++;
            }
            final ICacheEntry<Long, byte[]> entry = cache.allocateEntry(key, readLock, writeLock);
            if(entry.getValue() == null) {
                byte[] b = emurateReadInPage(pager, capacity, key);
                entry.setValue(b);
                miss++;
            }
            entry.unpin();
            ops++;
        }

        long mills2 = System.currentTimeMillis();
        stat.mills = mills2 - mills1;
        stat.ops = ops;
        stat.miss = miss;

        if(showstdout) {
            System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
                    + ", zipf: " + zipf);
            System.out.flush();
        }
    }

    private static void runBenchmarkWithZipfDistributionSync(Status stat, FixedSegments pager, ConcurrentPluggableCache<Long, byte[]> cache, int capacity, int round, double percent, int scanLength, long[] dist) {
        boolean scanning = false;
        long scanCount = 0L;

        final Random rand = new Random();

        int scanned = 0, zipf = 0;
        long base = 0L;

        long mills1 = System.currentTimeMillis();
        long ops = 0L;
        long miss = 0L;

        final int limit = round - 1;
        for(int nth = 0, i = 0; !_stop; nth = (nth >= limit) ? 0 : nth + 1, i++) {
            final long key;
            if(scanning || scanLength != 0 && (i == scanLength && rand.nextDouble() < percent)) {
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
                    i = 0;
                    zipf++;
                }
            } else {
                key = dist[nth];
                zipf++;
            }
            final ICacheEntry<Long, byte[]> entry;
            synchronized(cache) {
                entry = cache.fixEntry(key);
            }
            if(entry.getValue() == null) {
                byte[] b = emurateReadInPage(pager, capacity, key);
                entry.setValue(b);
                miss++;
            }
            entry.unpin();
            ops++;
        }

        long mills2 = System.currentTimeMillis();
        stat.mills = mills2 - mills1;
        stat.ops = ops;
        stat.miss = miss;

        if(showstdout) {
            System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
                    + ", zipf: " + zipf);
            System.out.flush();
        }
    }

    private static void runBenchmarkWithZipfDistributionLongHashLock(Status stat, ILock lock, FixedSegments pager, LongHash<byte[]> hash, int capacity, int round, double percent, int scanLength, long[] dist) {
        boolean scanning = false;
        long scanCount = 0L;

        final Random rand = new Random();

        int scanned = 0, zipf = 0;
        long base = 0L;

        long mills1 = System.currentTimeMillis();
        long ops = 0L;
        long miss = 0L;

        final int limit = round - 1;
        for(int nth = 0, i = 0; !_stop; nth = (nth >= limit) ? 0 : nth + 1, i++) {
            final long key;
            if(scanning || scanLength != 0 && (i == scanLength && rand.nextDouble() < percent)) {
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
                    i = 0;
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
                miss++;
            }
            lock.unlock();
            ops++;
        }

        long mills2 = System.currentTimeMillis();
        stat.mills = mills2 - mills1;
        stat.ops = ops;
        stat.miss = miss;

        if(showstdout) {
            System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
                    + ", zipf: " + zipf);
            System.out.flush();
        }
    }

    private static void runBenchmarkWithZipfDistributionLongHashSync(Status stat, FixedSegments pager, LongHash<byte[]> hash, int capacity, int round, double percent, int scanLength, long[] dist) {
        boolean scanning = false;
        long scanCount = 0L;

        final Random rand = new Random();

        int scanned = 0, zipf = 0;
        long base = 0L;

        long mills1 = System.currentTimeMillis();
        long ops = 0L;
        long miss = 0L;

        final int limit = round - 1;
        for(int nth = 0, i = 0; !_stop; nth = (nth >= limit) ? 0 : nth + 1, i++) {
            final long key;
            if(scanning || scanLength != 0 && (i == scanLength && rand.nextDouble() < percent)) {
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
                    i = 0;
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
                    miss++;
                }
            }
            ops++;
        }

        long mills2 = System.currentTimeMillis();
        stat.mills = mills2 - mills1;
        stat.ops = ops;
        stat.miss = miss;

        if(showstdout) {
            System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
                    + ", zipf: " + zipf);
            System.out.flush();
        }
    }

    private static byte[] emurateReadInPage(final FixedSegments pager, final int capacity, final long key) {
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

    private static class Status {
        long ops;
        long miss;
        long mills;
        long execInterval;
        long durationHold;
    }
}
