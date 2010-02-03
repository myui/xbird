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
import xbird.util.concurrent.lock.LockDelegate;
import xbird.util.concurrent.lock.NoopLock;
import xbird.util.datetime.DateTimeFormatter;
import xbird.util.datetime.StopWatch;
import xbird.util.io.FileUtils;
import xbird.util.io.IOUtils;
import xbird.util.primitive.MutableLong;

/**
 * 
 * <DIV lang="en"></DIV> <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class CacheBenchmark6_32G {

    private static final int TIME_TO_EST = 60000;
    private static final boolean enableStat = false;
    private static final boolean showstdout = false;
    private static final boolean volatilesync;
    private static final boolean useNIO;
    private static final boolean emuratePageIn;
    private static final byte[] dummy = new byte[8192];
    private static final long[] distribution;
    private static final int ENV_DATASET_NUM;

    static {
        System.setProperty("xbird.bufman.trace_cachemiss", "false");
        useNIO = System.getProperty("xbird.disable_nio") == null;
        emuratePageIn = System.getProperty("xbird.disable_enum_pagein") == null;
        volatilesync = System.getProperty("xbird.enable_volatilesync") != null;
        ENV_DATASET_NUM = Integer.parseInt(System.getProperty("xbird.datasetnum", "3"));
        try {
            System.out.println("xbird.datasetnum = " + ENV_DATASET_NUM);
            switch(ENV_DATASET_NUM) {
                case 5:
                case 6:
                    distribution = readDistribution("/experiment/zipfgen/1000k_zipf0.5_N50k.dat");
                    break;
                default:
                    distribution = readDistribution("/experiment/zipfgen/1000k_zipf0.86_N50k.dat");
                    break;
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public CacheBenchmark6_32G() {}

    static volatile boolean _start, _stop;

    public static void main(String[] args) {
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] { CacheBenchmark6_32G.class });
        TestListenerAdapter tla = new TestListenerAdapter();
        testng.addListener(tla);
        testng.run();
    }

    @DataProvider(name = "zipf")
    public Object[][] zipfDist1() {
        switch(ENV_DATASET_NUM) {
            // #1 hit rate test
            case 1:
                return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 64, 4096,
                        15625, 20d, 100, 8 } };
            case 2:
                return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 64, 8192,
                        15625, 20d, 100, 8 } };
            case 3:// default
                return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 64, 16384,
                        15625, 20d, 100, 8 } };
            case 4:
                return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 64, 32768,
                        15625, 20d, 100, 8 } };
                // #2 vary distribution
            case 5:
                return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.5_N50k.dat", 64, 16384,
                        15625, 20d, 100, 8 } };
            case 6:
                return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.5_N50k.dat", 64, 32768,
                        15625, 20d, 100, 8 } };
                // #3 CPU scalability
            case 7:
                return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 8, 16384,
                        15625, 20d, 100, 8 } };
            case 8:
                return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 16, 16384,
                        15625, 20d, 100, 8 } };
            case 9:
                return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 32, 16384,
                        15625, 20d, 100, 8 } };
            case 10:
                return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 64, 16384,
                        15625, 20d, 100, 8 } };
            case 11:
                return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 8, 8192,
                        15625, 20d, 100, 8 } };
            case 12:
                return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 16, 8192,
                        15625, 20d, 100, 8 } };
            case 13:
                return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 32, 8192,
                        15625, 20d, 100, 8 } };
            case 14:
                return new Object[][] { { "/experiment/zipfgen/1000k_zipf0.86_N50k.dat", 64, 8192,
                        15625, 20d, 100, 8 } };
            default:
                throw new IllegalStateException("Unsupported test dataset number: "
                        + ENV_DATASET_NUM);
        }
    }

    private static long[] readDistribution(String filename) throws IOException {
        final long[] dist = new long[8000000];
        int i = 0;
        for(int f = 1; f <= 8; f++) {
            final File file = new File(filename + '.' + f);
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
    public void testRunbenchNbGClock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();

        runBenchmarkWithZipfDistribution1(new NoopLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.NbGClock, pageSize, distribution);
    }

    @Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchNormalGClock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();

        runBenchmarkWithZipfDistribution1(new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.GClock, pageSize, distribution);
    }

    @Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchLRUSpinlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();

        runBenchmarkWithZipfDistribution1(new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, distribution);
    }

    //@Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchLRUSleeplock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();

        runBenchmarkWithZipfDistribution1(new LockDelegate(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, pageSize, distribution);
    }

    @Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchBpLRU(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();

        runBenchmarkWithZipfDistribution1(new NoopLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.BpLRU, pageSize, distribution);
    }

    @Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbench2QSpinlock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();

        runBenchmarkWithZipfDistribution1(new AtomicBackoffLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, distribution);
    }

    //@Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbench2QSleeplock(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();

        runBenchmarkWithZipfDistribution1(new LockDelegate(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.FullTwoQueue, pageSize, distribution);
    }

    @Test(dataProvider = "zipf")
    // , invocationCount = 3)
    public void testRunbenchBp2Q(String inputFile, Integer threads, Integer capacity, Integer round, Double scanPercentage, Integer scanLength, int pageSize)
            throws IOException, InterruptedException, ServiceException {
        String filename = FileUtils.basename(inputFile);

        System.gc();

        runBenchmarkWithZipfDistribution1(new NoopLock(), filename, threads, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.BpFullTwoQueue, pageSize, distribution);
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
                            throw new IllegalStateException();
                        } else {
                            if(isGCLOCK) {
                                runBenchmarkWithZipfDistributionLockForGCLOCK(thrdnum, stat, lock, pager, cache, capacity, round, percent, scanLength, dist);
                            } else {
                                switch(algo) {
                                    case BpFullTwoQueue:
                                    case BpLRU:
                                        //runBenchmarkWithZipfDistributionLazySync(thrdnum, stat, lock, pager, cache, capacity, round, percent, scanLength, dist);
                                        runBenchmarkWithZipfDistributionLazySync2(thrdnum, stat, lock, pager, cache, capacity, round, percent, scanLength, dist);
                                        break;
                                    default:
                                        runBenchmarkWithZipfDistributionLock(thrdnum, stat, lock, pager, cache, capacity, round, percent, scanLength, dist);
                                        break;
                                }
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
                        : ((lock instanceof NoopLock) ? "non-blocking"
                                : lock.getClass().getSimpleName()))) + ", read: " + numOps
                + ", miss: " + cacheMisses + ", hit ratio: " + hitRatio + ", miss ratio: "
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

    private static void runBenchmarkWithZipfDistributionLazySync(int thrdnum, Status stat, ILock lock, Segments pager, ConcurrentPluggableCache<Long, byte[]> cache, int capacity, int round, double percent, int scanLength, long[] dist) {
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
            final ICacheEntry<Long, byte[]> entry = cache.fixEntryLazySync(key);

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

    private static void runBenchmarkWithZipfDistributionLazySync2(int thrdnum, Status stat, ILock lock, Segments pager, ConcurrentPluggableCache<Long, byte[]> cache, int capacity, int round, double percent, int scanLength, long[] dist) {
        boolean scanning = false;
        long scanCount = 0L;

        final Random rand = new Random(129);

        int scanned = 0, zipf = 0;
        long base = 0L;

        long mills1 = System.currentTimeMillis();
        long ops = 0L;
        final MutableLong miss = new MutableLong(0L);
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
            ICacheEntry<Long, byte[]> entry = cache.fixEntryLazySync2(pager, key, key, miss);
            // application logic here.
            entry.unpin();
            ops++;
        }

        long mills2 = System.currentTimeMillis();
        stat.mills = mills2 - mills1;
        stat.ops = ops;
        stat.miss = miss.getValue();
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

    private static byte[] emurateReadInPage(final Segments pager, final int capacity, final long key) {
        if(!emuratePageIn) {
            return dummy;
        }
        try {
            return pager.read(key * 80);
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
