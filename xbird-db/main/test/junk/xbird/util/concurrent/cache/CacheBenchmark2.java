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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import xbird.util.StopWatch;
import xbird.util.cache.ICacheEntry;
import xbird.util.concurrent.cache.helpers.ReplacementPolicySelector;
import xbird.util.concurrent.cache.helpers.ReplacementPolicySelector.ReplacementAlgorithm;
import xbird.util.io.FileUtils;
import xbird.util.io.IOUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class CacheBenchmark2 {

    static {
        System.setProperty("xbird.bufman.trace_cachemiss", "true");
    }

    public CacheBenchmark2() {}

    @DataProvider(name = "zipf")
    public Object[][] zipfDist1() {
        return new Object[][] { { "/tmp/zipfgen/100k_zipf0.86.dat", 1000, 100000, 20d, 100 } };
    }

    @Test(dataProvider = "zipf")
    public void testRunbench(String inputFile, Integer capacity, Integer round, Double scanPercentage, Integer scanLength)
            throws FileNotFoundException {
        File file = new File(inputFile);
        if(!file.exists()) {
            Assert.fail("file not found: " + inputFile);
        }
        String filename = FileUtils.basename(inputFile);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        runBenchmarkWithZipfDistribution(filename, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.GClockK, reader);
        IOUtils.closeQuietly(reader);
        reader = new BufferedReader(new FileReader(file));
        runBenchmarkWithZipfDistribution(filename, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.GClock, reader);
        IOUtils.closeQuietly(reader);
        reader = new BufferedReader(new FileReader(file));
        runBenchmarkWithZipfDistribution(filename, capacity, round, scanPercentage, scanLength, ReplacementAlgorithm.LRU, reader);
        IOUtils.closeQuietly(reader);
    }

    private static void runBenchmarkWithZipfDistribution(String filename, int capacity, int round, double scanPercentage, int scanLength, ReplacementAlgorithm algo, BufferedReader reader) {
        ConcurrentPluggableCache<Long, Long> cache = new ConcurrentPluggableCache<Long, Long>(capacity);
        cache.setReplacementPolicy(ReplacementPolicySelector.<Long, Long> provide(capacity, algo));

        double percent = scanPercentage / 100d;
        boolean scanning = false;
        long scanCount = 0L;

        int scanned = 0, zipf = 0;

        StopWatch watchdog = new StopWatch("capacity: " + capacity + ", round: " + round
                + ", scanPercentage: " + percent + ", scanLength: " + scanLength);
        for(int nth = 0; nth < round; nth++) {
            final long key;
            if(scanning || scanLength != 0 && (nth % scanLength == 0 && Math.random() < percent)) {
                if(++scanCount <= scanLength) {
                    key = scanCount;
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
            ICacheEntry<Long, Long> entry = cache.allocateEntry(key);
            entry.setValue(key);
            entry.unpin();
        }
        long miss = cache.getCacheMiss();
        long hit = round - miss;
        double hitRatio = ((double) hit) / round;
        double missRatio = ((double) miss) / round;
        System.err.println("[" + algo + " - " + filename + "] read: " + round + ", miss: " + miss
                + ", hit ratio: " + hitRatio + ", miss ratio: " + missRatio);
        System.err.println(watchdog.toString());
        System.err.println("scanned: " + scanned + ", zipf: " + zipf);
        System.err.flush();
    }

    private static long readNextLong(BufferedReader reader) {
        try {
            String line = reader.readLine();
            assert (line != null);
            return Long.parseLong(line.trim());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
