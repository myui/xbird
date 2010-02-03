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
package scenario.storage;

import java.io.*;
import java.net.*;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sf.saxon.Configuration;
import net.sf.saxon.event.SaxonOutputKeys;
import net.sf.saxon.query.*;
import net.sf.saxon.trans.XPathException;

import org.custommonkey.xmlunit.XMLAssert;
import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.storage.tx.Transaction;
import xbird.util.io.FileUtils;
import xbird.util.io.IOUtils;
import xbird.util.xml.SAXWriter;
import xbird.xquery.*;
import xbird.xquery.dm.dtm.*;
import xbird.xquery.dm.instance.DocumentTableModel;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.dm.ser.Serializer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DocumentTableTest extends TestCase {
    private static final boolean DEBUG_LIGHT = true;
    private static final String TEST_FILE = "D:/workspace/xbird/main/test/resources/scenario/storage/auction.xml";

    public DocumentTableTest() throws DbException {
        super(DocumentTableTest.class.getName());
        DbCollection rootCol = DbCollection.getRootCollection();
        rootCol.removeCollection("/test");
        DbCollection testCol = rootCol.createCollection("test");
        testCol.createCollection("xmark");
    }

    public void xtestPutDocument() throws XQueryException, DbException, FileNotFoundException {
        DbCollection xmarkCol = DbCollection.getCollection("/test/xmark");
        DbCollection xmark = xmarkCol.createCollection("dtm");
        assert (xmark.getDirectory().exists());
        DocumentTableModel dtm = new DocumentTableModel(false);
        File file = new File(TEST_FILE);
        dtm.loadDocument(new FileInputStream(file));
        IDocumentTable doc = dtm.getDocumentTable();
        xmark.putDocument(new Transaction(), file.getName(), doc);
    }

    public void xtestPutDocumentPageOut() throws XQueryException, DbException,
            FileNotFoundException {
        DbCollection xmarkCol = DbCollection.getCollection("/test/xmark");
        DbCollection xmark = xmarkCol.createCollection("dtms");
        assert (xmark.getDirectory().exists());
        File file = new File(TEST_FILE);
        IDocumentTable doc = new DocumentTable.PersistentDocumentTable(xmark, FileUtils.getFileName(file));
        DocumentTableModel dtm = new DocumentTableModel(doc);
        dtm.loadDocument(new FileInputStream(file));
        xmark.putDocument(new Transaction(), file.getName(), doc);
    }

    public void xtestPutDocumentBig() throws XQueryException, DbException, FileNotFoundException {
        DbCollection xmarkCol = DbCollection.getCollection("/test/xmark");
        DbCollection xmark = xmarkCol.createCollection("big_dtm");
        assert (xmark.getDirectory().exists());
        File file = new File(TEST_FILE);
        IDocumentTable doc = new BigDocumentTable();
        DocumentTableModel dtm = new DocumentTableModel(doc);
        dtm.loadDocument(new FileInputStream(file));
        xmark.putDocument(new Transaction(), file.getName(), doc);
    }

    public void xtestPutDocumentPageOutBig() throws XQueryException, DbException,
            FileNotFoundException {
        DbCollection xmarkCol = DbCollection.getCollection("/test/xmark");
        DbCollection xmark = xmarkCol.createCollection("big_dtms");
        assert (xmark.getDirectory().exists());
        File file = new File(TEST_FILE);
        IDocumentTable doc = new BigDocumentTable.PersistentBigDocumentTable(xmark, FileUtils.getFileName(file));
        DocumentTableModel dtm = new DocumentTableModel(doc);
        dtm.loadDocument(new FileInputStream(file));
        xmark.putDocument(new Transaction(), file.getName(), doc);
    }

    public void testRunNormalDTM_Q1() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q1.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q1.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q1.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q2() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q2.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q2.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q2.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q3() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q3.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q3.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q3.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q4() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q4.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q4.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q4.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q5() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q5.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q5.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q5.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q6() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q6.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q6.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q6.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q7() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q7.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q7.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q7.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q8() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q8.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q8.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q8.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q9() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q9.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q9.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q9.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q10() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q10.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q10.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q10.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q11() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q11.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q11.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q11.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q12() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q12.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q12.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q12.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q13() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q13.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q13.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q13.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q14() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q14.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q14.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q14.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q15() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q15.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q15.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q15.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q16() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q16.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q16.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q16.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q17() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q17.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q17.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        String s1 = executeQueryWithSaxon("q17.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q18() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q18.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q18.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q18.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q19() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q19.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q19.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q19.xq");
        assertEqual(s1, o1);
    }

    public void testRunNormalDTM_Q20() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q20.xq", "fn:collection('/test/xmark/dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q20.xq", "fn:collection('/test/xmark/dtms/auction.xml')");
        assertNotNull(o1);
        assertNotNull(o1);
        String s1 = executeQueryWithSaxon("q20.xq");
        assertEqual(s1, o1);
        assertEquals(o1, o2);
        o2 = null;
    }

    public void testRunBigDTM_Q1() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q1.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q1.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q1.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q2() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q2.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q2.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q2.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q3() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q3.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q3.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q3.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q4() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q4.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q4.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q4.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q5() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q5.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q5.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q5.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q6() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q6.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q6.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q6.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q7() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q7.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q7.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q7.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q8() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q8.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q8.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q8.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q9() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q9.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q9.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q9.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q10() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q10.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q10.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q10.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q11() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q11.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q11.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q11.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q12() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q12.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q12.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q12.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q13() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q13.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q13.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q13.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q14() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q14.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q14.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q14.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q15() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q15.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q15.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q15.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q16() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q16.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q16.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q16.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q17() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q17.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q17.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q17.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q18() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q18.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q18.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q18.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q19() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q19.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q19.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q19.xq");
        assertEqual(s1, o1);
    }

    public void testRunBigDTM_Q20() throws FileNotFoundException, XPathException, SAXException,
            IOException, XQueryException, URISyntaxException {
        System.gc();
        String o1 = executeQueryWithXbird("q20.xq", "fn:collection('/test/xmark/big_dtm/auction.xml')");
        String o2 = executeQueryWithXbird("q20.xq", "fn:collection('/test/xmark/big_dtms/auction.xml')");
        assertNotNull(o1);
        assertNotNull(o1);
        assertEquals(o1, o2);
        o2 = null;
        String s1 = executeQueryWithSaxon("q20.xq");
        assertEqual(s1, o1);
    }

    private static String executeQueryWithXbird(String queryFile, String replace)
            throws XQueryException, IOException, URISyntaxException {
        URL url = DocumentTableTest.class.getResource(queryFile);
        URI uri = url.toURI();
        String query = IOUtils.toString(url.openStream());

        XQueryProcessor processor = new XQueryProcessor();
        query = query.replace("fn:doc(\"auction.xml\")", replace);
        if(DEBUG_LIGHT) {
            System.err.println(query);
        }
        XQueryModule mod = processor.parse(query, uri);
        StringWriter res_sw = new StringWriter();
        Serializer ser = new SAXSerializer(new SAXWriter(res_sw), res_sw);
        processor.execute(mod, ser);
        //Sequence<? extends Item> reseq = processor.execute(mod);
        //ser.emit(reseq);
        String result = res_sw.toString();
        return result;
    }

    private static String executeQueryWithSaxon(String queryFile) throws XPathException,
            FileNotFoundException, IOException, URISyntaxException {
        URL url = DocumentTableTest.class.getResource(queryFile);
        URI uri = url.toURI();
        String query = IOUtils.toString(url.openStream());

        Configuration config = new Configuration();
        config.setHostLanguage(Configuration.XQUERY);

        StaticQueryContext staticContext = new StaticQueryContext(config);
        staticContext.setBaseURI(uri.toString());
        XQueryExpression exp = staticContext.compileQuery(query);

        Properties props = new Properties();
        props.setProperty(SaxonOutputKeys.WRAP, "no");
        props.setProperty(OutputKeys.INDENT, "no");
        props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter res_sw = new StringWriter();
        DynamicQueryContext dynamicContext = new DynamicQueryContext(config);
        exp.run(dynamicContext, new StreamResult(res_sw), props);

        return res_sw.toString();
    }

    private void assertEqual(String expected, String actual) throws UnsupportedEncodingException {
        if(actual.length() == 0) {
            Assert.assertEquals(actual, expected);
        }
        actual = "<doc>" + actual + "</doc>";
        expected = "<doc>" + expected + "</doc>";
        Document actualDoc = buildDocument(new ByteArrayInputStream(actual.getBytes("UTF-8")));
        Document expectedDoc = buildDocument(new ByteArrayInputStream(expected.getBytes("UTF-8")));
        actual = null;
        expected = null;
        XMLAssert.assertXMLEqual(expectedDoc, actualDoc);
    }

    private static Document buildDocument(InputStream is) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final Document doc;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(is);
        } catch (Exception e) {
            throw new IllegalStateException("buildDocument failed!", e);
        }
        return doc;
    }

}
