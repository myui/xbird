/*
 * @(#)$Id: ExtHashTest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.storage.index;

import java.io.File;
import java.io.IOException;
import java.util.*;

import junit.framework.TestCase;

import org.junit.Assert;
import xbird.storage.DbException;
import xbird.util.string.StringUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class ExtHashTest extends TestCase {

    public ExtHashTest(String name) {
        super(name);
    }

    public void testFindValue() throws IOException, DbException {
        File tmpFile = File.createTempFile("exthash", ".tmp");
        tmpFile.deleteOnExit();
        SortedStaticHash hash = new SortedStaticHash(tmpFile, 8);
        hash.create(false);
        hash.addValue(new Key("a"), new Value("aa"));
        hash.addValue(new Key("b"), new Value("bb"));
        hash.addValue(new Key("c"), new Value("cc"));
        hash.flush(true, true);
        Assert.assertEquals(new Value("bb"), hash.findValue(new Key("b")));
        hash.drop();
    }

    public void testFindValue_Cold() throws IOException, DbException {
        File tmpFile = File.createTempFile("exthash", ".tmp");
        tmpFile.deleteOnExit();
        SortedStaticHash hash = new SortedStaticHash(tmpFile, 8);
        hash.create(false);
        hash.addValue(new Key("a"), new Value("aa"));
        hash.addValue(new Key("b"), new Value("bb"));
        hash.addValue(new Key("c"), new Value("cc"));
        hash.flush(true, true);
        Assert.assertEquals(new Value("bb"), hash.findValue(new Key("b")));
        hash.close();

        Assert.assertTrue(tmpFile.exists());
        hash = new SortedStaticHash(tmpFile, 8);
        Assert.assertTrue(hash.open());

        Assert.assertEquals(new Value("bb"), hash.findValue(new Key("b")));
        hash.drop();
    }

    private static final int REPEAT = 100000;

    public void xtestAddValue_SmallBucket8() throws IOException, DbException {
        File tmpFile = File.createTempFile("SmallBucket8", ".tmp");
        tmpFile.deleteOnExit();
        SortedStaticHash hash = new SortedStaticHash(tmpFile, 8);
        List<byte[]> list = new ArrayList<byte[]>(REPEAT);
        Random random = new Random(432542542L);
        for(int i = 0; i < REPEAT; i++) {
            String d = Double.toString(random.nextDouble());
            byte[] b = StringUtils.getBytes(d);
            Assert.assertNull(hash.addValue(new Key(b), new Value(b)));
            list.add(b);
        }
        hash.flush(true, true);
        for(int i = REPEAT - 1; i >= 0; i -= 2) {
            byte[] b = list.get(i);
            Assert.assertEquals("#" + i, new Value(b), hash.findValue(new Key(b)));
        }
        hash.drop();
    }

    public void testAddValue_BigBucket100() throws IOException, DbException {
        File tmpFile = File.createTempFile("BigBucket100", ".tmp");
        tmpFile.deleteOnExit();
        SortedStaticHash hash = new SortedStaticHash(tmpFile, REPEAT / 100);
        hash.create(false);
        List<byte[]> list = new ArrayList<byte[]>(REPEAT);
        Random random = new Random(54552542345L);
        for(int i = 0; i < REPEAT; i++) {
            String d = Double.toString(random.nextDouble());
            byte[] b = StringUtils.getBytes(d);
            Key k = new Key(b);
            Value v = new Value(b);
            Assert.assertNull(hash.addValue(k, v));
            Assert.assertEquals("#" + i, v, hash.findValue(k));
            list.add(b);
        }
        hash.flush(true, true);
        for(int i = REPEAT - 1; i >= 0; i -= 2) {
            byte[] b = list.get(i);
            Assert.assertEquals("#" + i, new Value(b), hash.findValue(new Key(b)));
        }
        hash.drop();
    }

    public void testRemoveValue() throws IOException, DbException {
        File tmpFile = File.createTempFile("RemoveValue", ".tmp");
        tmpFile.deleteOnExit();
        SortedStaticHash hash = new SortedStaticHash(tmpFile, 8);
        hash.create(false);
        hash.addValue(new Key("a"), new Value("aa"));
        hash.addValue(new Key("b"), new Value("bb"));
        hash.addValue(new Key("c"), new Value("cc"));
        Assert.assertEquals(new Value("bb"), hash.findValue(new Key("b")));
        hash.removeValue(new Key("b"));
        hash.flush(true, true);
        Assert.assertNull(hash.findValue(new Key("b")));
        hash.drop();
    }

}
