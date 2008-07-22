/*
 * @(#)$Id: XMarkTest.java 1491 2006-11-17 18:09:21Z yui $
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
package benchmark.xmark;

import java.io.*;

import javax.xml.parsers.*;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sf.saxon.trans.XPathException;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;
import xbird.util.StopWatch;
import xbird.util.io.FastMultiByteArrayOutputStream;
import xbird.util.io.IOUtils;
import xbird.util.xml.SAXWriter;
import xbird.xquery.*;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.dm.ser.Serializer;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMarkCollectionValidateTest extends TestCase {

    private static final String TARGET_FILE = System.getProperty("target_file", "xmark1.xml");
    private static final String TARGET_COLLECTION = System.getProperty("target_collection", "/xmark1/xmark1.xml");
    private static final String XMARK_HOME = System.getProperty("xmark_home", "C:/Software/xmark");

    public static void main(String[] args) {
        junit.textui.TestRunner.run(XMarkTest.class);
    }

    private static String executeQueryWithXbird_file(String queryFile) throws XQueryException,
            IOException {
        String queryFilePath = XMARK_HOME + '/' + queryFile;
        System.gc();

        String queryStr = IOUtils.toString(new FileInputStream(queryFilePath));
        queryStr = queryStr.replace("fn:doc(\"auction.xml\")", "fn:doc('" + TARGET_FILE + "')");

        final StopWatch sw = new StopWatch("[Xbird_file] " + queryFilePath);
        final XQueryProcessor processor = new XQueryProcessor();
        // parse
        XQueryModule mod = processor.parse(queryStr, new File(queryFilePath).toURI());
        // execute               
        FastMultiByteArrayOutputStream out = new FastMultiByteArrayOutputStream();
        SAXWriter writer = new SAXWriter(out, "UTF-8");
        final Serializer ser = new SAXSerializer(writer);
        Sequence<? extends Item> result = processor.execute(mod);
        ser.emit(result);
        writer.flush();

        System.err.println(sw);
        return out.toString();
    }

    private static String executeQueryWithXbird_collection(String queryFile)
            throws XQueryException, IOException {
        String queryFilePath = XMARK_HOME + '/' + queryFile;
        System.gc();

        String queryStr = IOUtils.toString(new FileInputStream(queryFilePath));
        queryStr = queryStr.replace("fn:doc(\"auction.xml\")", "fn:collection('"
                + TARGET_COLLECTION + "')");

        final StopWatch sw = new StopWatch("[Xbird_collection] " + queryFilePath);
        final XQueryProcessor processor = new XQueryProcessor();
        // parse
        XQueryModule mod = processor.parse(queryStr, new File(queryFilePath).toURI());
        // execute
        FastMultiByteArrayOutputStream out = new FastMultiByteArrayOutputStream();
        SAXWriter writer = new SAXWriter(out, "UTF-8");
        final Serializer ser = new SAXSerializer(writer);
        Sequence<? extends Item> result = processor.execute(mod);
        ser.emit(result);
        //processor.execute(mod, ser);
        writer.flush();

        System.err.println(sw);
        return out.toString();
    }

    private void assertEqual(String expected, String actual) throws UnsupportedEncodingException {
        if(actual.length() == 0) {
            Assert.assertEquals(expected, actual);
        }
        actual = "<doc>" + actual + "</doc>";
        expected = "<doc>" + expected + "</doc>";
        Document actualDoc = buildDocument(new ByteArrayInputStream(actual.getBytes("UTF-8")));
        Document expectedDoc = buildDocument(new ByteArrayInputStream(expected.getBytes("UTF-8")));
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

    @Test
    public void testRun1() throws FileNotFoundException, XPathException, SAXException, IOException,
            ParserConfigurationException, XQueryException {
        System.out.println("Running q1.xq ..");
        assertEqual(executeQueryWithXbird_file("q1.xq"), executeQueryWithXbird_collection("q1.xq"));
    }

    @Test
    public void testRun2() throws FileNotFoundException, XPathException, SAXException, IOException,
            ParserConfigurationException, XQueryException {
        System.out.println("Running q2.xq ..");
        assertEqual(executeQueryWithXbird_file("q2.xq"), executeQueryWithXbird_collection("q2.xq"));
    }

    @Test
    public void testRun3() throws FileNotFoundException, XPathException, SAXException, IOException,
            ParserConfigurationException, XQueryException {
        System.out.println("Running q3.xq ..");
        assertEqual(executeQueryWithXbird_file("q3.xq"), executeQueryWithXbird_collection("q3.xq"));
    }

    @Test
    public void testRun4() throws FileNotFoundException, XPathException, SAXException, IOException,
            ParserConfigurationException, XQueryException {
        System.out.println("Running q4.xq ..");
        assertEqual(executeQueryWithXbird_file("q4.xq"), executeQueryWithXbird_collection("q4.xq"));
    }

    @Test
    public void testRun5() throws FileNotFoundException, XPathException, SAXException, IOException,
            ParserConfigurationException, XQueryException {
        System.out.println("Running q5.xq ..");
        assertEqual(executeQueryWithXbird_file("q5.xq"), executeQueryWithXbird_collection("q5.xq"));
    }

    @Test
    public void testRun6() throws FileNotFoundException, XPathException, SAXException, IOException,
            ParserConfigurationException, XQueryException {
        System.out.println("Running q6.xq ..");
        assertEqual(executeQueryWithXbird_file("q6.xq"), executeQueryWithXbird_collection("q6.xq"));
    }

    @Test
    public void testRun7() throws FileNotFoundException, XPathException, SAXException, IOException,
            ParserConfigurationException, XQueryException {
        System.out.println("Running q7.xq ..");
        assertEqual(executeQueryWithXbird_file("q7.xq"), executeQueryWithXbird_collection("q7.xq"));
    }

    @Test
    public void testRun8() throws FileNotFoundException, XPathException, SAXException, IOException,
            ParserConfigurationException, XQueryException {
        System.out.println("Running q8.xq ..");
        assertEqual(executeQueryWithXbird_file("q8.xq"), executeQueryWithXbird_collection("q8.xq"));
    }

    @Test
    public void testRun9() throws FileNotFoundException, XPathException, SAXException, IOException,
            ParserConfigurationException, XQueryException {
        System.out.println("Running q9.xq ..");
        assertEqual(executeQueryWithXbird_file("q9.xq"), executeQueryWithXbird_collection("q9.xq"));
    }

    @Test
    public void testRun10() throws FileNotFoundException, XPathException, SAXException,
            IOException, ParserConfigurationException, XQueryException {
        System.out.println("Running q10.xq ..");
        assertEqual(executeQueryWithXbird_file("q10.xq"), executeQueryWithXbird_collection("q10.xq"));
    }

    @Test
    public void testRun11() throws FileNotFoundException, XPathException, SAXException,
            IOException, ParserConfigurationException, XQueryException {
        System.out.println("Running q11.xq ..");
        assertEqual(executeQueryWithXbird_file("q11.xq"), executeQueryWithXbird_collection("q11.xq"));
    }

    @Test
    public void testRun12() throws FileNotFoundException, XPathException, SAXException,
            IOException, ParserConfigurationException, XQueryException {
        System.out.println("Running q12.xq ..");
        assertEqual(executeQueryWithXbird_file("q12.xq"), executeQueryWithXbird_collection("q12.xq"));
    }

    @Test
    public void testRun13() throws FileNotFoundException, XPathException, SAXException,
            IOException, ParserConfigurationException, XQueryException {
        System.out.println("Running q13.xq ..");
        assertEqual(executeQueryWithXbird_file("q13.xq"), executeQueryWithXbird_collection("q13.xq"));
    }

    @Test
    public void testRun14() throws FileNotFoundException, XPathException, SAXException,
            IOException, ParserConfigurationException, XQueryException {
        System.out.println("Running q14.xq ..");
        assertEqual(executeQueryWithXbird_file("q14.xq"), executeQueryWithXbird_collection("q14.xq"));
    }

    @Test
    public void testRun15() throws FileNotFoundException, XPathException, SAXException,
            IOException, ParserConfigurationException, XQueryException {
        System.out.println("Running q15.xq ..");
        assertEqual(executeQueryWithXbird_file("q15.xq"), executeQueryWithXbird_collection("q15.xq"));
    }

    @Test
    public void testRun16() throws FileNotFoundException, XPathException, SAXException,
            IOException, ParserConfigurationException, XQueryException {
        System.out.println("Running q16.xq ..");
        assertEqual(executeQueryWithXbird_file("q16.xq"), executeQueryWithXbird_collection("q16.xq"));
    }

    @Test
    public void testRun17() throws FileNotFoundException, XPathException, SAXException,
            IOException, ParserConfigurationException, XQueryException {
        System.out.println("Running q17.xq ..");
        assertEqual(executeQueryWithXbird_file("q17.xq"), executeQueryWithXbird_collection("q17.xq"));
    }

    @Test
    public void testRun18() throws FileNotFoundException, XPathException, SAXException,
            IOException, ParserConfigurationException, XQueryException {
        System.out.println("Running q18.xq ..");
        assertEqual(executeQueryWithXbird_file("q18.xq"), executeQueryWithXbird_collection("q18.xq"));
    }

    @Test
    public void testRun19() throws FileNotFoundException, XPathException, SAXException,
            IOException, ParserConfigurationException, XQueryException {
        System.out.println("Running q19.xq ..");
        assertEqual(executeQueryWithXbird_file("q19.xq"), executeQueryWithXbird_collection("q19.xq"));
    }

    @Test
    public void testRun20() throws FileNotFoundException, XPathException, SAXException,
            IOException, ParserConfigurationException, XQueryException {
        System.out.println("Running q20.xq ..");
        assertEqual(executeQueryWithXbird_file("q20.xq"), executeQueryWithXbird_collection("q20.xq"));
    }
}
