/*
 * @(#)$Id: BTreeTest.java 3619 2008-03-26 07:23:03Z yui $
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
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import xbird.storage.DbException;
import xbird.storage.index.BTree.BTreeCorruptException;
import xbird.storage.indexer.BasicIndexQuery;
import xbird.storage.indexer.IndexQuery;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class BTreeTest extends TestCase {

    public BTreeTest(String name) {
        super(name);
    }

    @Test
    public void testInsertEQ_NoDup() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("d"), 4);
        btree.addValue(createValue("e"), 5);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.EQ, createValue("b"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        Assert.assertEquals(1, actual.size());
        Assert.assertEquals(new Long(2), actual.get(0));
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertEQ_Dup() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 6);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("b"), 7);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("d"), 4);
        btree.addValue(createValue("e"), 5);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.EQ, createValue("b"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        Assert.assertEquals(2, actual.size());
        Assert.assertEquals(new Long(2), actual.get(0));
        Assert.assertEquals(new Long(7), actual.get(1));
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertEQ_NoDup_UniqIndex() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, false /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("d"), 4);
        btree.addValue(createValue("e"), 5);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.EQ, createValue("b"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        Assert.assertEquals(1, actual.size());
        Assert.assertEquals(new Long(2), actual.get(0));
        btree.close();
        tmpFile.delete();
    }

    @Test(expected = BTreeCorruptException.class)
    public void testInsertEQ_Dup_UniqIndex() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, false /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("b"), 1);
        btree.addValue(createValue("c"), 6);
        btree.addValue(createValue("d"), 2);
        try {
            btree.addValue(createValue("b"), 7);
            Assert.fail("BTreeCorruptException is expected");
        } catch (BTreeCorruptException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testInsertNE_Dup() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 6);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("b"), 7);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("d"), 4);
        btree.addValue(createValue("e"), 5);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.NE, createValue("b"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 1, 1, 6, 3, 4, 5 };
        Assert.assertEquals(expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertGT_Dup() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("e"), 5);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("b"), 7);
        btree.addValue(createValue("a"), 6);
        btree.addValue(createValue("d"), 4);
        btree.addValue(createValue("e"), 8);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.GT, createValue("b"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 3, 4, 5, 8 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertGE_Dup() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("e"), 5);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("b"), 7);
        btree.addValue(createValue("a"), 6);
        btree.addValue(createValue("d"), 4);
        btree.addValue(createValue("e"), 8);
        btree.addValue(createValue("b"), 9);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.GE, createValue("b"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 2, 7, 9, 3, 4, 5, 8 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertLT_Dup() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("e"), 5);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("b"), 7);
        btree.addValue(createValue("a"), 6);
        btree.addValue(createValue("d"), 4);
        btree.addValue(createValue("e"), 8);
        btree.addValue(createValue("b"), 9);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.LT, createValue("b"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 1, 1, 6 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("actual: " + actual + ", at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertLE_Dup() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("e"), 5);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("b"), 7);
        btree.addValue(createValue("a"), 6);
        btree.addValue(createValue("d"), 4);
        btree.addValue(createValue("e"), 8);
        btree.addValue(createValue("b"), 9);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.LE, createValue("b"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 1, 1, 6, 2, 7, 9 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertBW_Dup() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("e"), 5);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("b"), 7);
        btree.addValue(createValue("a"), 6);
        btree.addValue(createValue("d"), 4);
        btree.addValue(createValue("e"), 8);
        btree.addValue(createValue("b"), 9);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.BW, createValue("b"), createValue("c"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 2, 7, 9, 3 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertBW_Dup_Eq() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("e"), 5);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("b"), 7);
        btree.addValue(createValue("a"), 6);
        btree.addValue(createValue("d"), 4);
        btree.addValue(createValue("e"), 8);
        btree.addValue(createValue("b"), 9);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.BW, createValue("b"), createValue("b"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 2, 7, 9 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertBW_Dup_Lack() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("b"), 7);
        btree.addValue(createValue("a"), 6);
        btree.addValue(createValue("d"), 4);
        btree.addValue(createValue("f"), 8);
        btree.addValue(createValue("b"), 9);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.BW, createValue("b"), createValue("e"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 2, 7, 9, 3, 4 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    public void testInsertBWX() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("e"), 5);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("b1"), 7);
        btree.addValue(createValue("a1"), 6);
        btree.addValue(createValue("d"), 4);
        btree.addValue(createValue("d1"), 10);
        btree.addValue(createValue("e"), 8);
        btree.addValue(createValue("b"), 9);
        btree.addValue(createValue("d"), 11);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.BWX, createValue("b"), createValue("d1"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 7, 3, 4, 11 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertIn_All() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("aaa"), 1);
        btree.addValue(createValue("bbb"), 1);
        btree.addValue(createValue("ccc"), 2);
        btree.addValue(createValue("ccc"), 3);
        btree.addValue(createValue("ddd"), 4);
        btree.addValue(createValue("eee"), 5);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.IN, createValue("ccc"), createValue("ddd"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 2, 3, 4 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertIn_Some() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("aaa"), 1);
        btree.addValue(createValue("bbb"), 1);
        btree.addValue(createValue("ccc"), 2);
        btree.addValue(createValue("ccc"), 3);
        btree.addValue(createValue("ddd"), 4);
        btree.addValue(createValue("eee"), 5);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.IN, createValue("ccc"), createValue("fff"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 2, 3 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertStartWith() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("aaa"), 1);
        btree.addValue(createValue("bbb"), 1);
        btree.addValue(createValue("ccc"), 0);
        btree.addValue(createValue("ccc1"), 1);
        btree.addValue(createValue("ccc2"), 2);
        btree.addValue(createValue("ccc21"), 3);
        btree.addValue(createValue("ccc21"), 4);
        btree.addValue(createValue("ccc3"), 5);
        btree.addValue(createValue("ccc2812"), 6);
        btree.addValue(createValue("ccc123"), 7);
        btree.addValue(createValue("ccc222"), 8);
        btree.addValue(createValue("ddd"), 4);
        btree.addValue(createValue("eee"), 5);
        IndexQuery query = new BasicIndexQuery.IndexConditionSW(createValue("ccc2"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 2, 3, 4, 8, 6 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    public void testInsertNBW() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("e"), 5);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("b1"), 7);
        btree.addValue(createValue("a1"), 6);
        btree.addValue(createValue("d"), 4);
        btree.addValue(createValue("d1"), 10);
        btree.addValue(createValue("e"), 8);
        btree.addValue(createValue("b"), 9);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.NBW, createValue("b"), createValue("d"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 1, 1, 6, 2, 4, 10, 5, 8 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    public void testInsertNBWX() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("e"), 5);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("b1"), 7);
        btree.addValue(createValue("a1"), 6);
        btree.addValue(createValue("d"), 4);
        btree.addValue(createValue("d1"), 10);
        btree.addValue(createValue("e"), 8);
        btree.addValue(createValue("b"), 9);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.NBWX, createValue("b"), createValue("d"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 1, 1, 6, 10, 5, 8 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertNotIn_All() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("aaa"), 1);
        btree.addValue(createValue("bbb"), 1);
        btree.addValue(createValue("ccc"), 2);
        btree.addValue(createValue("ccc"), 3);
        btree.addValue(createValue("ddd"), 4);
        btree.addValue(createValue("eee"), 5);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.NOT_IN, createValue("ccc"), createValue("ddd"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 1, 1, 5 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertNotIn_Some() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("aaa"), 1);
        btree.addValue(createValue("bbb"), 1);
        btree.addValue(createValue("ccc"), 2);
        btree.addValue(createValue("ccc"), 3);
        btree.addValue(createValue("ddd"), 4);
        btree.addValue(createValue("eee"), 5);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.NOT_IN, createValue("ccc"), createValue("fff"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 1, 1, 4, 5 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsertNotStartWith() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("aaa"), 1);
        btree.addValue(createValue("bbb"), 1);
        btree.addValue(createValue("ccc"), 0);
        btree.addValue(createValue("ccc1"), 1);
        btree.addValue(createValue("ccc2"), 2);
        btree.addValue(createValue("ccc21"), 3);
        btree.addValue(createValue("ccc21"), 4);
        btree.addValue(createValue("ccc3"), 5);
        btree.addValue(createValue("ccc2812"), 6);
        btree.addValue(createValue("ccc123"), 7);
        btree.addValue(createValue("ccc222"), 8);
        btree.addValue(createValue("ddd"), 4);
        btree.addValue(createValue("eee"), 5);
        IndexQuery query = new BasicIndexQuery.IndexConditionNSW(createValue("ccc2"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        long[] expected = new long[] { 1, 1, 0, 1, 7, 5, 4, 5 };
        Assert.assertEquals("actual: " + actual, expected.length, actual.size());
        for(int i = 0; i < expected.length; i++) {
            Assert.assertEquals("at " + i, new Long(expected[i]), actual.get(i));
        }
        btree.close();
        tmpFile.delete();
    }

    @Test
    public void testInsert_Cold() throws IOException, DbException {
        File tmpFile = File.createTempFile("btree", ".tmp");
        if(tmpFile.exists()) {
            assert (tmpFile.delete()) : tmpFile.getAbsolutePath();
        }
        BTree btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.create(false));
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 1);
        btree.addValue(createValue("a"), 6);
        btree.addValue(createValue("b"), 2);
        btree.addValue(createValue("c"), 3);
        btree.addValue(createValue("d"), 4);
        btree.flush(true, true);
        btree.close();

        btree = new BTree(tmpFile, true /* duplicateAllowed */);
        Assert.assertTrue(btree.open());

        btree.addValue(createValue("e"), 5);
        btree.addValue(createValue("b"), 7);
        IndexQuery query = new BasicIndexQuery(BasicIndexQuery.EQ, createValue("b"));
        Callback callback = new Callback();
        btree.search(query, callback);
        List<Long> actual = callback.getPointers();
        Assert.assertEquals(2, actual.size());
        Assert.assertEquals(new Long(2), actual.get(0));
        Assert.assertEquals(new Long(7), actual.get(1));
        btree.close();
        tmpFile.delete();
    }

    public Value createValue(String v) {
        return new Value(v);
    }

    private static class Callback implements CallbackHandler {
        final List<Long> pointers = new ArrayList<Long>(8);

        public boolean indexInfo(Value value, long pointer) {
            pointers.add(pointer);
            return true;
        }

        public List<Long> getPointers() {
            return pointers;
        }

        public boolean indexInfo(Value key, byte[] value) {
            throw new IllegalStateException();
        }
    }

}
