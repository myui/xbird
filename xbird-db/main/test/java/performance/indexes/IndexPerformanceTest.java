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
package performance.indexes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Assert;

import xbird.storage.DbException;
import xbird.storage.index.BIndexFile;
import xbird.storage.index.BTree;
import xbird.storage.index.Key;
import xbird.storage.index.SortedStaticHash;
import xbird.storage.index.Value;
import xbird.util.datetime.StopWatch;
import xbird.util.math.Primes;
import xbird.util.primitive.Primitives;
import xbird.util.string.StringUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class IndexPerformanceTest extends TestCase {
    private static final int REPEAT = 100000;

    public IndexPerformanceTest(String name) {
        super(name);
    }

    public void testBplusTreeUniq() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        tmpFile.deleteOnExit();
        BTree btree = new BTree(tmpFile, false);
        btree.create(false);

        List<byte[]> list = new ArrayList<byte[]>(REPEAT);
        Random random = new Random(54552542345L);
        StopWatch sw1 = new StopWatch("[BplusTreeUniq] Index Construction of " + REPEAT);
        for(int i = 0; i < REPEAT; i++) {
            long v = random.nextLong();
            byte[] b = Primitives.toBytes(v);
            Value k = new Value(b);
            Assert.assertTrue(btree.addValue(k, v) == -1);
            list.add(b);
        }
        System.err.println(sw1);

        btree.flush(true, true);

        StopWatch sw2 = new StopWatch("[BplusTreeUniq] Index Search of " + (REPEAT / 2));
        for(int i = REPEAT - 1; i >= 0; i -= 2) {
            byte[] b = list.get(i);
            Assert.assertEquals("#" + i, Primitives.getLong(b), btree.findValue(new Key(b)));
        }
        System.err.println(sw2);
        System.err.println("[BplusTreeUniq] " + tmpFile.getAbsolutePath() + " - size: "
                + tmpFile.length());
        btree.drop();
    }

    public void xtestExtHash_Large3001Buckets() throws IOException, DbException {
        File tmpFile = File.createTempFile("exthash", ".tmp");
        tmpFile.deleteOnExit();
        int buckets = Primes.findLeastPrimeNumber(3000);
        SortedStaticHash hash = new SortedStaticHash(tmpFile, buckets);
        hash.create(false);

        List<byte[]> list = new ArrayList<byte[]>(REPEAT);
        Random random = new Random(54552542345L);
        StopWatch sw1 = new StopWatch("[SortedStaticHash_with_" + buckets
                + "_buckets] Index Construction of " + REPEAT);
        for(int i = 0; i < REPEAT; i++) {
            String d = Double.toString(random.nextLong());
            byte[] b = StringUtils.getBytes(d);
            Key k = new Key(b);
            Value v = new Value(b);
            Assert.assertNull(hash.addValue(k, v));
            list.add(b);
        }
        System.err.println(sw1);

        hash.flush(true, true);

        StopWatch sw2 = new StopWatch("[SortedStaticHash_with_" + buckets
                + "_buckets] Index Search of " + (REPEAT / 2));
        for(int i = REPEAT - 1; i >= 0; i -= 2) {
            byte[] b = list.get(i);
            Assert.assertEquals("#" + i, new Value(b), hash.findValue(new Key(b)));
        }
        System.err.println(sw2);
        hash.drop();
    }

    public void xtestExtHash_Large3001Buckets_2KPage() throws IOException, DbException {
        File tmpFile = File.createTempFile("exthash", ".tmp");
        tmpFile.deleteOnExit();
        int buckets = Primes.findLeastPrimeNumber(3000);
        SortedStaticHash hash = new SortedStaticHash(tmpFile, 1024 * 2, buckets);
        hash.create(false);

        List<byte[]> list = new ArrayList<byte[]>(REPEAT);
        Random random = new Random(54552542345L);
        StopWatch sw1 = new StopWatch("[SortedStaticHash_with_" + buckets
                + "_buckets_2kPage] Index Construction of " + REPEAT);
        for(int i = 0; i < REPEAT; i++) {
            String d = Double.toString(random.nextLong());
            byte[] b = StringUtils.getBytes(d);
            Key k = new Key(b);
            Value v = new Value(b);
            Assert.assertNull(hash.addValue(k, v));
            list.add(b);
        }
        System.err.println(sw1);

        hash.flush(true, true);

        StopWatch sw2 = new StopWatch("[SortedStaticHash_with_" + buckets
                + "_buckets_2kPage] Index Search of " + (REPEAT / 2));
        for(int i = REPEAT - 1; i >= 0; i -= 2) {
            byte[] b = list.get(i);
            Assert.assertEquals("#" + i, new Value(b), hash.findValue(new Key(b)));
        }
        System.err.println(sw2);
        hash.drop();
    }

    public void xtestExtHash_Default1021Buckets() throws IOException, DbException {
        File tmpFile = File.createTempFile("exthash", ".tmp");
        tmpFile.deleteOnExit();
        int buckets = Primes.findLeastPrimeNumber(REPEAT / 100);
        SortedStaticHash hash = new SortedStaticHash(tmpFile, buckets);
        hash.create(false);

        List<byte[]> list = new ArrayList<byte[]>(REPEAT);
        Random random = new Random(54552542345L);
        StopWatch sw1 = new StopWatch("[SortedStaticHash_with_" + buckets
                + "_buckets] Index Construction of " + REPEAT);
        for(int i = 0; i < REPEAT; i++) {
            String d = Double.toString(random.nextLong());
            byte[] b = StringUtils.getBytes(d);
            Key k = new Key(b);
            Value v = new Value(b);
            Assert.assertNull(hash.addValue(k, v));
            list.add(b);
        }
        System.err.println(sw1);

        hash.flush(true, true);

        StopWatch sw2 = new StopWatch("[SortedStaticHash_with_" + buckets
                + "_buckets] Index Search of " + (REPEAT / 2));
        for(int i = REPEAT - 1; i >= 0; i -= 2) {
            byte[] b = list.get(i);
            Assert.assertEquals("#" + i, new Value(b), hash.findValue(new Key(b)));
        }
        System.err.println(sw2);
        hash.drop();
    }

    public void testBFile() throws IOException, DbException {
        File tmpFile = File.createTempFile("bfile", ".tmp");
        tmpFile.deleteOnExit();
        BIndexFile bfile = new BIndexFile(tmpFile, false);
        bfile.create(false);

        List<byte[]> list = new ArrayList<byte[]>(REPEAT);
        Random random = new Random(54552542345L);
        StopWatch sw1 = new StopWatch("[BFileUniq] Index Construction of " + REPEAT);
        for(int i = 0; i < REPEAT; i++) {
            long v = random.nextLong();
            byte[] b = Primitives.toBytes(v);
            Value k = new Value(b);
            bfile.addValue(k, b);
            list.add(b);
        }
        System.err.println(sw1);

        bfile.flush(true, true);

        StopWatch sw2 = new StopWatch("[BFileUniq] Index Search of " + (REPEAT / 2));
        for(int i = REPEAT - 1; i >= 0; i -= 2) {
            byte[] b = list.get(i);
            Assert.assertEquals("#" + i, new Value(b), bfile.getValue(new Value(b)));
        }
        System.err.println(sw2);
        System.err.println("[BFileUniq] " + tmpFile.getAbsolutePath() + " - size: "
                + tmpFile.length());
        bfile.drop();
    }

}
