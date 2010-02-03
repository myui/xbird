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
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import xbird.server.ServiceException;
import xbird.server.services.PerfmonService;
import xbird.storage.io.FixedSegments;
import xbird.storage.io.NioFixedSegment;
import xbird.storage.io.Segments;
import xbird.util.cache.ICacheEntry;
import xbird.util.concurrent.cache.helpers.ReplacementAlgorithm;
import xbird.util.concurrent.cache.helpers.ReplacementPolicySelector;
import xbird.util.concurrent.collections.ConcurrentCollectionProvider;
import xbird.util.concurrent.lock.AtomicBackoffLock;
import xbird.util.concurrent.lock.ILock;
import xbird.util.concurrent.lock.NoopLock;
import xbird.util.datetime.DateTimeFormatter;
import xbird.util.datetime.StopWatch;
import xbird.util.io.FileUtils;
import xbird.util.io.IOUtils;

import com.sun.forte.st.collector.CollectorAPI;

/**
 * 
 * <DIV lang="en"></DIV> <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class CacheBenchmark6a {

    private static final int TIME_TO_EST = 60000;
    private static final boolean enableStat = false;
    private static final boolean showstdout = false;
    private static final boolean volatilesync;
    private static final boolean useNIO;
    private static final boolean emuratePageIn;
    private static final boolean enableDprofile;
    private static final byte[] dummy = new byte[8192];
    private static final long[] distribution;

    static {
        System.setProperty("xbird.bufman.trace_cachemiss", "false");
        useNIO = System.getProperty("xbird.disable_nio") == null;
        emuratePageIn = System.getProperty("xbird.disable_enum_pagein") == null;
        enableDprofile = System.getProperty("xbird.enable_dprofile") != null;
        volatilesync = System.getProperty("xbird.enable_volatilesync") != null;
        try {
            distribution = readDistribution("/experiment/zipfgen/zipf-S4000000-L1-R4000000-P10.dat");
            //distribution = readDistribution("/experiment/zipfgen/1000k_zipf0.5_N50k.dat");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public CacheBenchmark6a() {}

    static volatile boolean _start, _stop;

    public static void main(String[] args) {
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] { CacheBenchmark6a.class });
        TestListenerAdapter tla = new TestListenerAdapter();
        testng.addListener(tla);
        if(enableDprofile) {
            CollectorAPI.sample("bench5.1.er");
            testng.run();
            // CollectorAPI.terminate();
        } else {
            testng.run();
        }
    }

    @DataProvider(name = "zipf")
    public Object[][] zipfDist1() {
        // #1 hit rate test
        //return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 64, 4096, 15625, 20d, 100, 8 } };
        //return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 64, 8192, 15625, 20d, 100, 8 } };
        //return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 64, 16384, 15625, 20d, 100, 8 } };
        //return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 64, 32768, 15625, 20d, 100, 8 } };
        // #2 vary distribution
        //return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.5_N50k.dat", 64, 16384, 15625, 20d, 100, 8 } };
        //return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.5_N50k.dat", 64, 32768, 15625, 20d, 100, 8 } };
        // #3 CPU scalability
        //return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 8, 16384, 15625, 20d, 100, 8 } };
        //return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 16, 16384, 15625, 20d, 100, 8 } };
        //return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 32, 16384, 15625, 20d, 100, 8 } };
        return new Object[][] { { "zipf-S4000000-L1-R4000000-P10.dat", 64,
                131072 /* 1024 * 1024 / 8 */, 15625 /* 1000000 / 64 */, 20d, 100, 8 } };
    }

    private static long[] readDistribution(String filename) throws IOException {
        final long[] dist = new long[8000000];
        int i = 0;
        for(int f = 1; f <= 8; f++) {
            final File file = new File(filename + "." + f);
            if(!file.exists()) {
                throw new IllegalStateException("File not found: " + filename);
            }
            final BufferedReader reader = new BufferedReader(new FileReader(file));
            for(int j = 0; j < 1000000; j++) {
                String line = reader.readLine();
                dist[i++] = Long.parseLong(line.trim());
            }
            IOUtils.closeQuietly(reader);
        }
        return dist;
    }

    @Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchGClock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();

        // GClock
        runBenchmarkWithZipfDistribution1(new NoopLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.NbGClock, pageSize, distribution);
    }

    //@Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchGClockK(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();

        // GClock
        runBenchmarkWithZipfDistribution1(new NoopLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.NbGClockK, pageSize, distribution);
    }

    //@Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchGClockSpinlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();
        runBenchmarkWithZipfDistribution1(false, new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.NbGClock, pageSize, distribution);
    }

    @Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchNormalGClock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();
        runBenchmarkWithZipfDistribution1(false, new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.GClock, pageSize, distribution);
    }

    // @Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchNormalGClockSync(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();
        runBenchmarkWithZipfDistribution1(false, null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.GClock, pageSize, distribution);
    }

    //@Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchNormalGClockRWLock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();
        runBenchmarkWithZipfDistribution1(true, null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.GClock, pageSize, distribution);
    }

    @Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchLRUSpinlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        // LRU + spinlock
        System.gc();
        runBenchmarkWithZipfDistribution1(new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, distribution);
    }

    // @Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchLRUSync(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        // LRU + sync
        System.gc();
        runBenchmarkWithZipfDistribution1(null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, distribution);
    }

    //@Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchLRU_RWlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();
        runBenchmarkWithZipfDistribution1(true, null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, distribution);
    }

    @Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbench2QSpinlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        // 2Q + spinlock
        System.gc();
        runBenchmarkWithZipfDistribution1(new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, distribution);
    }

    // @Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbench2QSync(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);
        // 2Q + sync
        System.gc();
        runBenchmarkWithZipfDistribution1(null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, distribution);
    }

    //@Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbench2Q_RWlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();
        runBenchmarkWithZipfDistribution1(true, null, filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, distribution);
    }

    private static void runBenchmarkWithZipfDistribution1(final ILock lock, final String filename, final int threads, final int capacity, final int round, final double scanPercentage, final int scanLength, final ReplacementAlgorithm algo, final int pageSize, final long[] dist)
            throws IOException, InterruptedException, ServiceException {
        runBenchmarkWithZipfDistribution1(false, lock, filename, threads, capacity, round, scanPercentage, scanLength, algo, pageSize, dist);
    }

    private static void runBenchmarkWithZipfDistribution1(final boolean useRWlock, final ILock lock, final String filename, final int threads, final int capacity, final int round, final double scanPercentage, final int scanLength, final ReplacementAlgorithm algo, final int pageSize, final long[] dist)
            throws IOException, InterruptedException, ServiceException {
        final ConcurrentPluggableCache<Long, byte[]> cache = new ConcurrentPluggableCache<Long, byte[]>(capacity, ConcurrentCollectionProvider.<ICacheEntry<Long, byte[]>> createConcurrentMapLong(capacity));
        cache.setReplacementPolicy(ReplacementPolicySelector.<Long, byte[]> provide(capacity, algo));

        OperatingSystemMXBean mx = ManagementFactory.getOperatingSystemMXBean();
        final com.sun.management.OperatingSystemMXBean sunmx = (com.sun.management.OperatingSystemMXBean) mx;
        long startCpuTime = sunmx.getProcessCpuTime();

        final double percent = scanPercentage / 100d;
        StopWatch watchdog = new StopWatch("nthreads: " + threads + ", capacity: " + capacity
                + ", scanPercentage: " + percent + ", scanLength: " + scanLength + ", pageSize: "
                + pageSize);

        final Segments pager = makeTempSegments(pageSize);
        final Status[] stats = new Status[threads];
        final ReadWriteLock rwlock = new ReentrantReadWriteLock();

        final boolean isGCLOCK = (algo == ReplacementAlgorithm.GClock);

        _start = _stop = false;
        final Thread[] thrs = new Thread[threads];
        for(int i = 0; i < threads; i++) {
            final Status stat = new Status();
            final int thrdnum = i;
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
                                if(isGCLOCK) {
                                    runBenchmarkWithZipfDistributionReadWriteLockForGCLOCK(rwlock, thrdnum, stat, pager, cache, capacity, round, percent, scanLength, dist);
                                } else {
                                    runBenchmarkWithZipfDistributionReadWriteLock(rwlock, thrdnum, stat, pager, cache, capacity, round, percent, scanLength, dist);
                                }
                            } else {
                                runBenchmarkWithZipfDistributionSync(thrdnum, stat, pager, cache, capacity, round, percent, scanLength, dist);
                            }
                        } else {
                            if(isGCLOCK) {
                                runBenchmarkWithZipfDistributionLockForGCLOCK(thrdnum, stat, lock, pager, cache, capacity, round, percent, scanLength, dist);
                            } else {
                                runBenchmarkWithZipfDistributionLock(thrdnum, stat, lock, pager, cache, capacity, round, percent, scanLength, dist);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        PerfmonService perfmon = new PerfmonService(3000);
        if(enableStat) {
            perfmon.start();
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
        if(enableStat) {
            perfmon.stop();
        }
        long elapsedCpuTime = sunmx.getProcessCpuTime() - startCpuTime;

        long numOps = 0L, cacheMisses = 0L, elapsed = 0L;
        int ioContention = 0;
        for(int i = 0; i < threads; i++) {
            numOps += stats[i].ops;
            cacheMisses += stats[i].miss;
            elapsed += stats[i].mills;
            ioContention += stats[i].iocontention;
        }
        long avgElapsedInSec = (elapsed / threads) / 1000L;
        long opsPerSec = numOps / avgElapsedInSec;

        long hit = numOps - cacheMisses;
        double hitRatio = ((double) hit) / numOps;
        double missRatio = ((double) cacheMisses) / numOps;
        System.err.println("["
                + algo
                + ((algo == ReplacementAlgorithm.NbGClock || algo == ReplacementAlgorithm.NbGClockK) ? ", counter type: "
                        + System.getProperty("xbird.counter", "striped")
                        : "")
                + " - "
                + filename
                + "] synchronization: "
                + (useRWlock ? "rwlock" : (lock == null ? "sync"
                        : ((lock instanceof NoopLock) ? "non-blocking" : "spinlock"))) + ", read: "
                + numOps + ", miss: " + cacheMisses + ", hit ratio: " + hitRatio + ", miss ratio: "
                + missRatio + ", io contention: " + ioContention);
        System.err.println(watchdog.toString());
        System.err.println("number of ops: " + numOps + ", average number of ops per second: "
                + opsPerSec);
        System.err.println("Elapsed cpu time: " + DateTimeFormatter.formatNanoTime(elapsedCpuTime));
        System.err.flush();

        pager.close();
    }

    private static Segments makeTempSegments(int pageSize) throws IOException {
        File tmpFile = new File("/experiment/data/xmark32G.xml");
        if(!tmpFile.exists()) {
            throw new IllegalStateException("File not found: " + tmpFile.getAbsolutePath());
        }
        if(useNIO) {
            return new NioFixedSegment(tmpFile, pageSize * 1024, true);
        } else {
            return new FixedSegments(tmpFile, pageSize * 1024);
        }
    }

    private static void runBenchmarkWithZipfDistributionLock(int thrdnum, Status stat, ILock lock, Segments pager, ConcurrentPluggableCache<Long, byte[]> cache, int capacity, int round, double percent, int scanLength, long[] dist) {
        boolean scanning = false;
        long scanCount = 0L;

        final Random rand = new Random(129);

        int scanned = 0, zipf = 0;
        long base = 0L;

        long mills1 = System.currentTimeMillis();
        long ops = 0L;
        long miss = 0L;
        int iocontention = 0;

        final int start = thrdnum * round;
        final int limit = start + round - 1;
        for(int nth = start, i = 0; !_stop; nth = (nth < limit) ? nth + 1 : start, i++) {
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
            final ICacheEntry<Long, byte[]> entry = cache.fixEntry(key);
            lock.unlock();

            if(volatilesync) {
                if(entry.volatileGetValue() == null) {
                    synchronized(entry) {
                        if(entry.getValue() == null) {
                            byte[] b = emurateReadInPage(pager, capacity, key);
                            entry.setValue(b);
                            miss++;
                        }
                    }
                }
            } else {
                if(entry.getValue() == null) {
                    byte[] b = emurateReadInPage(pager, capacity, key);
                    if(!entry.compareAndSetValue(null, b)) {
                        iocontention++;
                    }
                    miss++;
                }
            }
            entry.unpin();
            ops++;
        }

        long mills2 = System.currentTimeMillis();
        stat.mills = mills2 - mills1;
        stat.ops = ops;
        stat.miss = miss;
        stat.iocontention = iocontention;

        if(showstdout) {
            System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
                    + ", zipf: " + zipf);
        }
    }

    private static void runBenchmarkWithZipfDistributionLockForGCLOCK(int thrdnum, Status stat, ILock lock, Segments pager, ConcurrentPluggableCache<Long, byte[]> cache, int capacity, int round, double percent, int scanLength, long[] dist) {
        boolean scanning = false;
        long scanCount = 0L;

        final Random rand = new Random(129);

        int scanned = 0, zipf = 0;
        long base = 0L;

        long mills1 = System.currentTimeMillis();
        long ops = 0L;
        long miss = 0L;
        int iocontention = 0;

        final int start = thrdnum * round;
        final int limit = start + round - 1;
        for(int nth = start, i = 0; !_stop; nth = (nth < limit) ? nth + 1 : start, i++) {
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
            final ICacheEntry<Long, byte[]> entry = cache.allocateEntryForClock(key, lock);

            if(volatilesync) {
                if(entry.volatileGetValue() == null) {
                    synchronized(entry) {
                        if(entry.getValue() == null) {
                            byte[] b = emurateReadInPage(pager, capacity, key);
                            entry.setValue(b);
                            miss++;
                        }
                    }
                }
            } else {
                if(entry.getValue() == null) {
                    byte[] b = emurateReadInPage(pager, capacity, key);
                    if(!entry.compareAndSetValue(null, b)) {
                        iocontention++;
                    }
                    miss++;
                }
            }
            entry.unpin();
            ops++;
        }

        long mills2 = System.currentTimeMillis();
        stat.mills = mills2 - mills1;
        stat.ops = ops;
        stat.miss = miss;
        stat.iocontention = iocontention;

        if(showstdout) {
            System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
                    + ", zipf: " + zipf);
        }
    }

    private static void runBenchmarkWithZipfDistributionSync(int thrdnum, Status stat, Segments pager, ConcurrentPluggableCache<Long, byte[]> cache, int capacity, int round, double percent, int scanLength, long[] dist) {
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
            final int distnth = nth * thrdnum;
            if(scanning || scanLength != 0 && (i == scanLength && rand.nextDouble() < percent)) {
                if(++scanCount <= scanLength) {
                    if(!scanning) {
                        base = dist[distnth];
                    }
                    key = base + scanCount;
                    scanning = true;
                    scanned++;
                } else {
                    key = dist[distnth];
                    scanCount = 0;
                    scanning = false;
                    i = 0;
                    zipf++;
                }
            } else {
                key = dist[distnth];
                zipf++;
            }
            final ICacheEntry<Long, byte[]> entry;
            synchronized(cache) {
                entry = cache.fixEntry(key);
                if(entry.getValue() == null) {
                    byte[] b = emurateReadInPage(pager, capacity, key);
                    entry.setValue(b);
                    miss++;
                }
                entry.unpin();
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

    private static void runBenchmarkWithZipfDistributionReadWriteLock(ReadWriteLock rwlock, int thrdnum, Status stat, Segments pager, ConcurrentPluggableCache<Long, byte[]> cache, int capacity, int round, double percent, int scanLength, long[] dist) {
        boolean scanning = false;
        long scanCount = 0L;

        final Random rand = new Random(129);

        int scanned = 0, zipf = 0;
        long base = 0L;

        long mills1 = System.currentTimeMillis();
        long ops = 0L;
        long miss = 0L;
        int iocontention = 0;

        final Lock readLock = rwlock.readLock();
        final Lock writeLock = rwlock.writeLock();

        final int start = thrdnum * round;
        final int limit = start + round - 1;
        for(int nth = start, i = 0; !_stop; nth = (nth < limit) ? nth + 1 : start, i++) {
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
            if(volatilesync) {
                if(entry.volatileGetValue() == null) {
                    synchronized(entry) {
                        if(entry.getValue() == null) {
                            byte[] b = emurateReadInPage(pager, capacity, key);
                            entry.setValue(b);
                            miss++;
                        }
                    }
                }
            } else {
                if(entry.getValue() == null) {
                    final byte[] b = emurateReadInPage(pager, capacity, key);
                    if(!entry.compareAndSetValue(null, b)) {
                        iocontention++;
                    }
                    miss++;
                }
            }
            entry.unpin();
            ops++;
        }

        long mills2 = System.currentTimeMillis();
        stat.mills = mills2 - mills1;
        stat.ops = ops;
        stat.miss = miss;
        stat.iocontention = iocontention;

        if(showstdout) {
            System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
                    + ", zipf: " + zipf);
        }
    }

    private static void runBenchmarkWithZipfDistributionReadWriteLockForGCLOCK(ReadWriteLock rwlock, int thrdnum, Status stat, Segments pager, ConcurrentPluggableCache<Long, byte[]> cache, int capacity, int round, double percent, int scanLength, long[] dist) {
        boolean scanning = false;
        long scanCount = 0L;

        final Random rand = new Random(129);

        int scanned = 0, zipf = 0;
        long base = 0L;

        long mills1 = System.currentTimeMillis();
        long ops = 0L;
        long miss = 0L;
        int iocontention = 0;

        final Lock readLock = rwlock.readLock();
        final Lock writeLock = rwlock.writeLock();

        final int start = thrdnum * round;
        final int limit = start + round - 1;
        for(int nth = start, i = 0; !_stop; nth = (nth < limit) ? nth + 1 : start, i++) {
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
            final ICacheEntry<Long, byte[]> entry = cache.allocateEntryForClock(key, readLock, writeLock);
            if(volatilesync) {
                if(entry.volatileGetValue() == null) {
                    synchronized(entry) {
                        if(entry.getValue() == null) {
                            byte[] b = emurateReadInPage(pager, capacity, key);
                            entry.setValue(b);
                            miss++;
                        }
                    }
                }
            } else {
                if(entry.getValue() == null) {
                    final byte[] b = emurateReadInPage(pager, capacity, key);
                    if(!entry.compareAndSetValue(null, b)) {
                        iocontention++;
                    }
                    miss++;
                }
            }
            entry.unpin();
            ops++;
        }

        long mills2 = System.currentTimeMillis();
        stat.mills = mills2 - mills1;
        stat.ops = ops;
        stat.miss = miss;
        stat.iocontention = iocontention;

        if(showstdout) {
            System.out.println("[" + Thread.currentThread().getName() + "] scanned: " + scanned
                    + ", zipf: " + zipf);
        }
    }

    private static byte[] emurateReadInPage(final Segments pager, final int capacity, final long key) {
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
        int iocontention;
    }
}
