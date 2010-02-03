/*
 * @(#)$Id: DbCollectionTest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.storage;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;

import xbird.storage.tx.Transaction;
import xbird.util.datetime.StopWatch;
import xbird.util.io.FileUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.dtm.BigDocumentTable;
import xbird.xquery.dm.dtm.DocumentTable;
import xbird.xquery.dm.dtm.IDocumentTable;
import xbird.xquery.dm.instance.DocumentTableModel;

public class DbCollectionTest extends TestCase {

    private static final String COL_NAME = "xmark1";
    private static final String TEST_FILE = "C:/Software/xmark/xmark1.xml";

    public DbCollectionTest() {
        super(DbCollectionTest.class.getName());
    }

    @Test
    @Ignore
    public void xtestCreateCollection() throws DbException {
        DbCollection coll = DbCollection.getRootCollection();
        DbCollection xmark = coll.createCollection(COL_NAME);
        assert (xmark.getDirectory().exists());
        File collDir = coll.getDirectory();
        File[] matchFiles = collDir.listFiles(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() && COL_NAME.equals(f.getName());
            }
        });
        assert (matchFiles.length > 0);
        assert (coll.removeCollection(COL_NAME));
    }

    @Test
    @Ignore
    public void xtestPutDocument() throws XQueryException, DbException, FileNotFoundException {
        StopWatch sw = new StopWatch("testPutDocument");
        DbCollection xmark = DbCollection.getRootCollection().createCollection(COL_NAME);
        assert (xmark.getDirectory().exists());
        final DocumentTableModel dtm = new DocumentTableModel(false);
        File file = new File(TEST_FILE);
        dtm.loadDocument(new FileInputStream(file));
        IDocumentTable doc = dtm.getDocumentTable();
        //DbCollection coll = new DbCollection(true);
        xmark.putDocument(new Transaction(), file.getName(), doc);
        System.err.println(sw);
    }

    @Test
    public void xtestPutDocumentPageOut() throws XQueryException, DbException,
            FileNotFoundException {
        DbCollection xmark = DbCollection.getRootCollection().createCollection(COL_NAME);
        assert (xmark.getDirectory().exists());
        File file = new File(TEST_FILE);
        IDocumentTable doc = new DocumentTable.PersistentDocumentTable(xmark, FileUtils.getFileName(file));
        final DocumentTableModel dtm = new DocumentTableModel(doc);
        dtm.loadDocument(new FileInputStream(file));
        xmark.putDocument(new Transaction(), file.getName(), doc);
    }

    public void xtestPutDocumentBig() throws XQueryException, DbException, FileNotFoundException {
        DbCollection xmark = DbCollection.getRootCollection().createCollection(COL_NAME);
        assert (xmark.getDirectory().exists());
        File file = new File(TEST_FILE);
        IDocumentTable doc = new BigDocumentTable();
        final DocumentTableModel dtm = new DocumentTableModel(doc);
        dtm.loadDocument(new FileInputStream(file));
        xmark.putDocument(new Transaction(), file.getName(), doc);
    }

    @Test
    public void testPutDocumentPageOutBig() throws XQueryException, DbException,
            FileNotFoundException {
        StopWatch sw = new StopWatch("testPutDocumentPageOutBig");
        DbCollection xmark = DbCollection.getRootCollection().createCollection(COL_NAME);
        assert (xmark.getDirectory().exists());
        File file = new File(TEST_FILE);
        IDocumentTable doc = new BigDocumentTable.PersistentBigDocumentTable(xmark, FileUtils.getFileName(file));
        final DocumentTableModel dtm = new DocumentTableModel(doc);
        dtm.loadDocument(new FileInputStream(file));
        xmark.putDocument(new Transaction(), file.getName(), doc);
        System.err.println(sw);
    }

    @Test
    @Ignore
    public void xtestFlushSymbols() throws DbException {
        DbCollection coll = DbCollection.getRootCollection().createCollection(COL_NAME);
        coll.getSymbols().setDirty(true);
        coll.flushSymbols();
    }

}
