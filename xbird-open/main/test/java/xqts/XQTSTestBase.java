/*
 * @(#)$$Id: XQTSTestBase.java 1322 2006-11-02 11:59:58Z yui $$
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
package xqts;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import xbird.util.collections.SoftHashMap;
import xbird.util.io.IOUtils;
import xbird.util.lang.PrintUtils;
import xbird.util.pool.ObjectPool;
import xbird.util.pool.StackObjectPool;
import xbird.util.xml.NamespaceBinder;
import xbird.util.xml.SAXWriter;
import xbird.util.xml.XMLUtils;
import xbird.xquery.XQRTException;
import xbird.xquery.XQueryException;
import xbird.xquery.XQueryModule;
import xbird.xquery.XQueryProcessor;
import xbird.xquery.dm.instance.DocumentTableModel;
import xbird.xquery.dm.instance.DocumentTableModel.DTMDocument;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.dm.ser.Serializer;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.NodeSequence;
import xbird.xquery.dm.value.xsi.DateTimeValue;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.misc.ModuleManager;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.SimpleModuleResolver;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.xs.DateType;

/**
 *
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 *
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://java.sun.com/developer/technicalArticles/xml/jaxp1-3/#Reuse_Parser_Instance
 * @link http://java.sun.com/j2se/1.5.0/docs/api/javax/xml/xpath/XPath.html
 */
public class XQTSTestBase {

    private static final boolean generateReport = System.getProperty("xqts.report_on") != null;

    private static Map<String, DTMDocument> _docCache = Collections.synchronizedMap(new SoftHashMap<String, DTMDocument>());
    private static Map<String, Document> _expectedDocumentCache = Collections.synchronizedMap(new SoftHashMap<String, Document>());
    private static Map<String, String> _expectedResultStrCache = Collections.synchronizedMap(new WeakHashMap<String, String>());

    public static final String CATALONG_URI_PREFIX = "ns";
    public static final String CATALONG_URI = "http://www.w3.org/2005/02/query-test-XQTSCatalog";
    private static final String SAX_PARSER_FACTORY = "javax.xml.parsers.SAXParserFactory";

    public static final Properties XQTS_PROP = new Properties();
    public static final ObjectPool<Document> catalogPool;
    public static final String xqtsVersion;

    private static final String xqtsDir;
    private static final String xqtsReportFile;
    private static final File xqtsQueryPath;
    private static final File xqtsResultPath;

    private static final boolean ispect;
    private static final boolean doPrint;

    static {// load XQTS test properties
        try {
            XQTS_PROP.load(XQTSTestBase.class.getResourceAsStream("xqts.properties"));
        } catch (Exception e) {
            throw new IllegalStateException("Couldn't load xqts.properties.", e);
        }
        System.setProperty(SAX_PARSER_FACTORY, XQTS_PROP.getProperty(SAX_PARSER_FACTORY)); // workaround for a xerces bug
        xqtsDir = XQTS_PROP.getProperty("xqts_dir");
        xqtsReportFile = XQTS_PROP.getProperty("xqts.report.destfile");

        catalogPool = new StackObjectPool<Document>() {
            @Override
            protected Document createObject() {
                return buildDocument(xqtsDir + "/XQTSCatalog.xml");
            }
        };

        final Document catalog = buildDocument(xqtsDir + "/XQTSCatalog.xml");
        // show XQTS version
        final XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            xqtsVersion = xpath.evaluate("/*[local-name()='test-suite']/@version", catalog);
            String queryRelPath = xpath.evaluate("/*[local-name()='test-suite']/@XQueryQueryOffsetPath", catalog);
            xqtsQueryPath = new File(xqtsDir, queryRelPath);
            String resRelPath = xpath.evaluate("/*[local-name()='test-suite']/@ResultOffsetPath", catalog);
            xqtsResultPath = new File(xqtsDir, resRelPath);
        } catch (XPathExpressionException e) {
            throw new IllegalStateException("Couldn't get XQTS version.");
        }
        ispect = Boolean.parseBoolean(XQTS_PROP.getProperty("xqts.enable_inspect"));
        doPrint = (System.getProperty("xqts.disable_printing") != null) ? false
                : !Boolean.parseBoolean(XQTS_PROP.getProperty("xqts.disable_printing"));

        System.out.println("Run XQuery-Test-Suite(XQTS) version " + xqtsVersion + "\n");

        DocumentBuilderFactory ctrlDbf = XMLUnit.getControlDocumentBuilderFactory();
        ctrlDbf.setIgnoringElementContentWhitespace(true);
        DocumentBuilderFactory testDbf = XMLUnit.getTestDocumentBuilderFactory();
        testDbf.setIgnoringElementContentWhitespace(true);
        XMLUnit.setIgnoreWhitespace(true);
    }

    public XQTSTestBase() {}

    public XQTSTestBase(final String testName, final String targetXQTSVersion) {
        assert (xqtsVersion.equals(targetXQTSVersion)) : "version mismatch! expected version: "
                + xqtsVersion + ", target version: " + targetXQTSVersion;
    }

    protected static int countTests(final String testPath) throws XPathExpressionException {
        final XPath xpath = XPathFactory.newInstance().newXPath();
        NamespaceBinder resolver = new NamespaceBinder();
        resolver.declarePrefix(CATALONG_URI_PREFIX, CATALONG_URI);
        xpath.setNamespaceContext(resolver);
        final String count = "count(" + testPath + ")";
        XPathExpression expr = xpath.compile(count);
        final Document catalog = catalogPool.borrowObject();
        final Double d = (Double) expr.evaluate(catalog, XPathConstants.NUMBER);
        catalogPool.returnObject(catalog);
        return d.intValue();
    }

    public void invokeTest(final String testPath) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        NamespaceBinder resolver = new NamespaceBinder();
        resolver.declarePrefix(CATALONG_URI_PREFIX, CATALONG_URI);
        xpath.setNamespaceContext(resolver);

        final Document catalog = catalogPool.borrowObject();
        try {
            NodeList rs = (NodeList) xpath.evaluate(testPath, catalog, XPathConstants.NODESET);
            final int rslen = rs.getLength();
            for(int i = 0; i < rslen; i++) {
                if(doPrint) {
                    println("\n------------------------------------------------");
                }
                final StaticContext statEnv = new StaticContext();
                statEnv.setConstructionModeStrip(true);

                Node testCase = rs.item(i);
                final String testName = xpath.evaluate("./@name", testCase);
                final String testFilePath = xpath.evaluate("./@FilePath", testCase);
                final String queryFileName = xpath.evaluate("./*[local-name()='query']/@name", testCase);
                File queryFile = new File(xqtsQueryPath, testFilePath + queryFileName + ".xq");
                final URI baseUri = new File(xqtsQueryPath, testFilePath).toURI();

                XQueryModule xqmod = new XQueryModule();

                {// ((//*:test-group)//*:test-case)/*:module
                    NodeList moduleNodes = (NodeList) xpath.evaluate("./*[local-name()='module']", testCase, XPathConstants.NODESET);
                    final int modcount = moduleNodes.getLength();
                    if(modcount > 0) {
                        ModuleManager moduleManager = statEnv.getModuleManager();
                        SimpleModuleResolver modResolver = new SimpleModuleResolver();
                        moduleManager.setModuleResolver(modResolver);
                        for(int j = 0; j < modcount; j++) {
                            Node moduleNode = moduleNodes.item(j);
                            String moduleId = moduleNode.getTextContent();
                            String moduleFileStr = xpath.evaluate("/*[local-name()='test-suite']/*[local-name()='sources']/*[local-name()='module']/@FileName[../@ID='"
                                    + moduleId + "']", catalog);
                            File moduleFile = new File(xqtsDir, moduleFileStr + ".xq");
                            String physical = moduleFile.toURI().toString();
                            String logical = xpath.evaluate("./@namespace", moduleNode);
                            modResolver.addMappingRule(logical, physical);
                        }
                    }
                }
                {// ((//*:test-group)//*:test-case)/*:input-file
                    NodeList vars1 = (NodeList) xpath.evaluate("./*[local-name()='input-file']/@variable", testCase, XPathConstants.NODESET);
                    loadVariables(vars1, testCase, xqmod, statEnv, xpath, catalog, false);
                }
                { // ((//*:test-group)//*:test-case)/*:input-URI
                    NodeList vars2 = (NodeList) xpath.evaluate("./*[local-name()='input-URI']/@variable", testCase, XPathConstants.NODESET);
                    loadVariables(vars2, testCase, xqmod, statEnv, xpath, catalog, true);
                }
                {// ((//*:test-group)//*:test-case)/*:defaultCollection
                    String colId = xpath.evaluate("./*[local-name()='defaultCollection']/text()", testCase);
                    if(colId != null) {
                        NodeList list = (NodeList) xpath.evaluate("/*[local-name()='test-suite']/*[local-name()='sources']/*[local-name()='collection'][@ID='"
                                + colId + "']/*[local-name()='input-document']/text()", catalog, XPathConstants.NODESET);
                        final int listlen = list.getLength();
                        if(listlen > 0) {
                            final Map<String, DTMDocument> defaultCollectionMap = new HashMap<String, DTMDocument>(listlen);
                            for(int j = 0; j < listlen; j++) {
                                String name = list.item(j).getTextContent();
                                String docName = name + ".xml";
                                DTMDocument testDataDoc = _docCache.get(name);
                                if(testDataDoc == null) {
                                    File testDataFile = new File(xqtsDir, docName);
                                    DocumentTableModel dtm = new DocumentTableModel(false);
                                    dtm.loadDocument(new FileInputStream(testDataFile));
                                    testDataDoc = dtm.documentNode();
                                    _docCache.put(name, testDataDoc);
                                }
                                defaultCollectionMap.put(docName, testDataDoc);
                                // import namespace decl
                                Map<String, String> nsmap = testDataDoc.documentTable().getDeclaredNamespaces();
                                NamespaceBinder nsResolver = statEnv.getStaticalyKnownNamespaces();
                                nsResolver.declarePrefixs(nsmap);
                            }
                            statEnv.setDefaultCollection(defaultCollectionMap);
                        }
                    }
                }
                Sequence<? extends Item> contextItem = null;
                {// ((//*:test-group)//*:test-case)/*:contextItem
                    String contextItemRef = xpath.evaluate("./*[local-name()='contextItem']/text()", testCase);
                    if(contextItemRef != null && contextItemRef.length() > 0) {
                        String contextItemFileRef = xpath.evaluate("/*[local-name()='test-suite']/*[local-name()='sources']/*[local-name()='source']/@FileName[../@ID='"
                                + contextItemRef + "']", catalog);
                        DTMDocument contextItemDoc = _docCache.get(contextItemRef);
                        if(contextItemDoc == null) {
                            File contextItemFile = new File(xqtsDir, contextItemFileRef);
                            DocumentTableModel dtm = new DocumentTableModel(false);
                            dtm.loadDocument(new FileInputStream(contextItemFile));
                            contextItemDoc = dtm.documentNode();
                            _docCache.put(contextItemRef, contextItemDoc);
                        }
                        contextItem = contextItemDoc;
                    }
                }
                {// ((//*:test-group)//*:test-case)/*:input-query
                    String inputVarName = xpath.evaluate("./*[local-name()='input-query']/@variable", testCase);
                    if(inputVarName != null && inputVarName.length() > 0) {
                        String dateData = xpath.evaluate("./*[local-name()='input-query']/@date", testCase);
                        assert (dateData != null) : dateData;
                        QualifiedName varName = QNameTable.instantiate(XMLConstants.DEFAULT_NS_PREFIX, inputVarName);
                        Variable var = new Variable.GlobalVariable(varName, null);
                        var.setResult(new DateTimeValue(dateData, DateType.DATE));
                        xqmod.putVariable(varName, var);
                    }
                }

                // -----------------------------------------------------------
                // #1 execute
                final String resString;
                {
                    final String query = IOUtils.toString(new FileInputStream(queryFile), "UTF-8");
                    if(doPrint) {
                        println("Query: ");
                        println(query);
                    }
                    final NodeList expectedErrors = (NodeList) xpath.evaluate("./*[local-name()='expected-error']", testCase, XPathConstants.NODESET);
                    {
                        XQueryProcessor proc = new XQueryProcessor(xqmod);
                        proc.setStaticContext(statEnv);
                        if(contextItem != null) {
                            proc.setContextItem(contextItem);
                        }
                        final StringWriter res_sw = new StringWriter();
                        try {
                            XQueryModule mod = proc.parse(query, baseUri);
                            Sequence result = proc.execute(mod);
                            SAXWriter saxwriter = new SAXWriter(res_sw, SAXWriter.DEFAULT_ENCODING);
                            saxwriter.setXMLDeclaration(false);
                            Serializer ser = new SAXSerializer(saxwriter, res_sw);
                            ser.emit(result);
                        } catch (Throwable ex) {
                            final int errors = expectedErrors.getLength();
                            if(errors == 0) {
                                final Node expectedOutputs = (Node) xpath.evaluate("./*[local-name()='output-file'][last()]", testCase, XPathConstants.NODE);
                                assert (expectedOutputs != null);
                                final Element output = (Element) expectedOutputs;
                                final String expFileName = output.getTextContent();
                                final File testFileDir = new File(xqtsResultPath, testFilePath);
                                String expectedResult = _expectedResultStrCache.get(expFileName);
                                if(expectedResult == null) {
                                    File expected = new File(testFileDir, expFileName);
                                    expectedResult = IOUtils.toString(new FileInputStream(expected));
                                    _expectedResultStrCache.put(expFileName, expectedResult);
                                }
                                if(doPrint) {
                                    println("Expected Result: ");
                                    println(expectedResult);
                                }
                                final String compareForm = output.getAttribute("compare");
                                final String errmsg;
                                if("Ignore".equals(compareForm)) {
                                    errmsg = "Unexpected exception: \n" + PrintUtils.getMessage(ex);
                                    String smallerrmsg = "Unexpected exception: "
                                            + PrintUtils.prettyPrintStackTrace(ex);
                                    reportTestResult(testName, "fail", smallerrmsg);
                                } else if("Inspect".equals(compareForm)) {
                                    errmsg = "Inspectection is required, got exception: \n"
                                            + PrintUtils.prettyPrintStackTrace(ex);
                                    String smallerrmsg = "Inspectection is required, got exception: "
                                            + PrintUtils.getOneLineMessage(ex);
                                    reportTestResult(testName, "not tested", smallerrmsg);
                                } else {
                                    errmsg = (expectedResult == null) ? "No result"
                                            : ('\'' + expectedResult + '\'')
                                                    + " is expected, but caused following exception: \n"
                                                    + PrintUtils.prettyPrintStackTrace(ex);
                                    String smallerrmsg = (expectedResult == null) ? "No result"
                                            : ('\'' + expectedResult + '\'')
                                                    + " is expected, but caused following exception: "
                                                    + PrintUtils.getOneLineMessage(ex);
                                    reportTestResult(testName, "fail", smallerrmsg);
                                }
                                Assert.fail(errmsg);
                            } else {
                                String errMsg = ex.getMessage();
                                if(errMsg == null) {
                                    errMsg = PrintUtils.getOneLineMessage(ex);
                                }
                                final int lastei = errors - 1;
                                for(int ei = 0; ei < errors; ei++) {
                                    final String expectedError = expectedErrors.item(ei).getTextContent();
                                    final boolean contain = (errMsg == null) ? false
                                            : errMsg.contains(expectedError);
                                    if(contain) {
                                        reportTestResult(testName, "pass", null);
                                        break;
                                    } else {
                                        final String msg = "Expected-error: " + expectedError
                                                + ", Actual Error: " + errMsg;
                                        if(!(ex instanceof XQueryException || ex instanceof XQRTException)) {
                                            reportTestResult(testName, "fail", msg);
                                            ex.printStackTrace();
                                            Assert.fail(msg);
                                        } else {
                                            if(ei == lastei) {
                                                final String passmsg = "Expected-error: "
                                                        + expectedError + ", Actual Error: "
                                                        + getErrCode(ex);
                                                reportTestResult(testName, "fail", passmsg);
                                                ex.printStackTrace();
                                                Assert.fail(msg);
                                            }
                                        }
                                    }
                                }
                                return;
                            }
                        }
                        resString = res_sw.toString();
                        if(doPrint) {
                            println("\nActual Result: ");
                            println(resString);
                        }
                    }
                    final int errors = expectedErrors.getLength();
                    if(errors > 0) {
                        final StringBuilder buf = new StringBuilder(256);
                        for(int ei = 0; ei < errors; ei++) {
                            if(ei != 0) {
                                buf.append(" or ");
                            }
                            final String expectedError = expectedErrors.item(ei).getTextContent();
                            buf.append(expectedError);
                        }
                        buf.append(" is expected, but was ..\n");
                        buf.append(resString);
                        Assert.fail(buf.toString());
                    }
                }

                // -----------------------------------------------------------
                // #2 probe
                {
                    NodeList expectedOutputs = (NodeList) xpath.evaluate("./*[local-name()='output-file']", testCase, XPathConstants.NODESET);
                    final int expectedOuts = expectedOutputs.getLength();
                    if(expectedOuts == 0) {
                        Assert.fail("Expected condition is not found in '" + testName + '\'');
                    }
                    final File testFileDir = new File(xqtsResultPath, testFilePath);
                    final int lastoi = expectedOuts - 1;
                    for(int oi = 0; oi <= lastoi; oi++) {
                        final Element output = (Element) expectedOutputs.item(oi);
                        final String expFileName = output.getTextContent();
                        String expectedStr = _expectedResultStrCache.get(expFileName);
                        if(expectedStr == null) {
                            File expected = new File(testFileDir, expFileName);
                            expectedStr = IOUtils.toString(new FileInputStream(expected), "UTF-8");
                            _expectedResultStrCache.put(expFileName, expectedStr);
                        }
                        if(doPrint) {
                            println("Expected Result (" + expFileName + "): ");
                            println(expectedStr);
                        }

                        final String compareForm = output.getAttribute("compare");
                        if("XML".equals(compareForm)) {
                            Diff diff = new Diff(expectedStr, resString);
                            if(diff.similar()) {
                                reportTestResult(testName, "pass", null);
                                break;
                            } else {
                                if(oi == lastoi) {
                                    String errmsg = diff.toString();
                                    reportTestResult(testName, "fail", errmsg);
                                    Assert.fail(errmsg);
                                }
                            }
                        } else if("Fragment".equals(compareForm)) {
                            Document expectedDoc = _expectedDocumentCache.get(expFileName);
                            if(expectedDoc == null) {
                                expectedDoc = buildFragment(expectedStr);
                                _expectedDocumentCache.put(expFileName, expectedDoc);
                            }
                            String actual = "<doc>" + resString + "</doc>";
                            Document actualDoc = buildDocument(new ByteArrayInputStream(actual.getBytes("UTF-8")));
                            Diff diff = new Diff(expectedDoc, actualDoc);
                            if(diff.similar()) {
                                reportTestResult(testName, "pass", null);
                                break;
                            } else {
                                if(oi == lastoi) {
                                    String errmsg = diff.toString();
                                    reportTestResult(testName, "fail", errmsg);
                                    Assert.fail(errmsg);
                                }
                            }
                        } else if("Text".equals(compareForm)) {
                            if(expectedStr.equals(resString)) {
                                reportTestResult(testName, "pass", null);
                                break;
                            } else {
                                Document expectedDoc = _expectedDocumentCache.get(expFileName);
                                if(expectedDoc == null) {
                                    expectedDoc = buildFragment(expectedStr);
                                    _expectedDocumentCache.put(expFileName, expectedDoc);
                                }
                                String actual = "<doc>" + XMLUtils.escapeXML(resString) + "</doc>";
                                Document actualDoc = buildDocument(new ByteArrayInputStream(actual.getBytes("UTF-8")));
                                Diff diff = new Diff(expectedDoc, actualDoc);
                                if(diff.identical()) {
                                    reportTestResult(testName, "pass", null);
                                    break;
                                } else {
                                    if(oi == lastoi) {
                                        String errmsg = new ComparisonFailure("[Text comparison]", expectedStr, resString).getMessage();
                                        reportTestResult(testName, "fail", errmsg);
                                        Assert.assertEquals("[Text comparison]", expectedStr, resString);
                                    }
                                }
                            }
                        } else if("Ignore".equals(compareForm)) {
                            // no comparison needs to be applied; the result is always true
                            // if the implementation successfully executes the test case.
                            reportTestResult(testName, "pass", null);
                            break;
                        } else if("Inspect".equals(compareForm)) {
                            System.err.println("#" + i + " Inspection is required");
                            Document expectedDoc = _expectedDocumentCache.get(expFileName);
                            if(expectedDoc == null) {
                                expectedDoc = buildFragment(expectedStr);
                                _expectedDocumentCache.put(expFileName, expectedDoc);
                            }
                            String actual = "<doc>" + resString + "</doc>";
                            Document actualDoc = buildDocument(new ByteArrayInputStream(actual.getBytes("UTF-8")));
                            Diff diff = new Diff(expectedDoc, actualDoc);
                            if(diff.similar()) {
                                reportTestResult(testName, "pass", null);
                                break;
                            } else {
                                if(oi == lastoi) {
                                    reportTestResult(testName, "not tested", "Inspectection is required");
                                }
                            }
                            if(ispect && !diff.similar()) {
                                if(oi == lastoi) {
                                    Assert.fail("Inspectection is required: \n" + diff.toString());
                                }
                            }
                        } else {
                            String errmsg = "[BUG] could'nt compare in " + compareForm;
                            reportTestResult(testName, "fail", errmsg);
                            throw new IllegalStateException(errmsg);
                        }
                    }
                }
            }
        } finally {
            catalogPool.returnObject(catalog);
        }
    }

    private static void loadVariables(final NodeList vars, final Node testCase, final XQueryModule xqmod, final StaticContext statEnv, final XPath xpath, final Document catalog, final boolean fromUri)
            throws XPathExpressionException, FileNotFoundException, XQueryException {
        final int vlen = vars.getLength();
        for(int j = 0; j < vlen; j++) {
            final String varNameStr = vars.item(j).getNodeValue();
            if(varNameStr != null) {
                final String testDataId;
                if(fromUri) {
                    testDataId = xpath.evaluate("./*[local-name()='input-URI'][" + (j + 1)
                            + "]/text()", testCase);
                } else {
                    testDataId = xpath.evaluate("./*[local-name()='input-file'][" + (j + 1)
                            + "]/text()", testCase);
                }
                String testFileRelDir = xpath.evaluate("/*[local-name()='test-suite']/*[local-name()='sources']/*[local-name()='source']/@FileName[../@ID='"
                        + testDataId + "']", catalog);
                if(testFileRelDir != null) {
                    if(fromUri) {
                        File testDataFile = new File(xqtsDir, testFileRelDir);
                        String dest = testDataFile.toURI().toString();
                        QualifiedName varName = QNameTable.instantiate(XMLConstants.DEFAULT_NS_PREFIX, varNameStr);
                        Variable var = new Variable.GlobalVariable(varName, null);
                        var.setResult(new XString(dest));
                        xqmod.putVariable(varName, var);
                    } else {
                        DTMDocument testDataDoc = _docCache.get(testDataId);
                        if(testDataDoc == null) {
                            File testDataFile = new File(xqtsDir, testFileRelDir);
                            // load test data
                            DocumentTableModel dtm = new DocumentTableModel(false);
                            dtm.loadDocument(new FileInputStream(testDataFile));
                            testDataDoc = dtm.documentNode();
                            _docCache.put(testDataId, testDataDoc);
                        }
                        // import namespace decl
                        Map<String, String> nsmap = testDataDoc.documentTable().getDeclaredNamespaces();
                        NamespaceBinder nsResolver = statEnv.getStaticalyKnownNamespaces();
                        nsResolver.declarePrefixs(nsmap);
                        // reserve variable
                        QualifiedName varName = QNameTable.instantiate(XMLConstants.DEFAULT_NS_PREFIX, varNameStr);
                        Variable var = new Variable.GlobalVariable(varName, null);
                        var.setResult(testDataDoc);
                        xqmod.putVariable(varName, var);
                    }
                } else {
                    NodeList list = (NodeList) xpath.evaluate("/*[local-name()='test-suite']/*[local-name()='sources']/*[local-name()='collection'][@ID='"
                            + testDataId + "']/*[local-name()='input-document']/text()", catalog, XPathConstants.NODESET);
                    final int listlen = list.getLength();
                    if(listlen > 0) {
                        final NodeSequence<DTMDocument> ret = new NodeSequence<DTMDocument>(new DynamicContext(statEnv));
                        for(int i = 0; i < listlen; i++) {
                            String name = list.item(i).getTextContent();
                            DTMDocument testDataDoc = _docCache.get(name);
                            if(testDataDoc == null) {
                                File testDataFile = new File(xqtsDir, name + ".xml");
                                DocumentTableModel dtm = new DocumentTableModel(false);
                                dtm.loadDocument(new FileInputStream(testDataFile));
                                testDataDoc = dtm.documentNode();
                                _docCache.put(testDataId, testDataDoc);
                            }
                            // import namespace decl
                            Map<String, String> nsmap = testDataDoc.documentTable().getDeclaredNamespaces();
                            NamespaceBinder nsResolver = statEnv.getStaticalyKnownNamespaces();
                            nsResolver.declarePrefixs(nsmap);
                            ret.addItem(testDataDoc);
                        }
                        // reserve variable
                        QualifiedName varName = QNameTable.instantiate(XMLConstants.DEFAULT_NS_PREFIX, varNameStr);
                        Variable var = new Variable.GlobalVariable(varName, null);
                        var.setResult(ret);
                        xqmod.putVariable(varName, var);
                    }
                }
            }
        }
    }

    private static final String getErrCode(final Throwable ex) {
        final String s;
        if(ex instanceof XQueryException) {
            s = ((XQueryException) ex).getErrCode();
        } else if(ex instanceof XQRTException) {
            s = ((XQRTException) ex).getErrCode();
        } else {
            s = ex.getClass().getSimpleName();
        }
        final int ptr = s.indexOf('\n');
        if(ptr > 0) {
            return s.substring(0, ptr - 1);
        }
        return s;
    }

    private void reportTestResult(final String name, final String result, final String comment)
            throws URISyntaxException, IOException {
        if(!generateReport) {
            return;
        }
        assert (name != null);
        assert (result != null);
        StringBuilder buf = new StringBuilder(256);
        buf.append("<test-case name=\"");
        buf.append(name);
        buf.append("\" result=\"");
        buf.append(result);
        if(comment != null && comment.length() > 0 && !"fail".equals(result)) {
            buf.append("\" comment=\"");
            buf.append(XMLUtils.escapeXML(comment));
        }
        buf.append("\"/>\n");
        final String line = buf.toString();
        File file = new File(xqtsReportFile);
        OutputStream os = new FileOutputStream(file, true);
        Writer writer = new OutputStreamWriter(os, Charset.forName("UTF-8"));
        writer.write(line);
        writer.flush();
        writer.close();
        os.close();
    }

    private static Document buildDocument(final String path) {
        final File file = new File(path);
        try {
            return buildDocument(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Document buildDocument(final InputStream is) {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setExpandEntityReferences(true);
        final Document doc;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(is);
        } catch (Exception e) {
            throw new IllegalStateException("buildDocument failed", e);
        }
        return doc;
    }

    private static Document buildFragment(final String frag) {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setExpandEntityReferences(true);
        String input = "<doc>" + frag + "</doc>";
        final Document doc;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new ByteArrayInputStream(input.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new IllegalStateException("buildDocument failed", e);
        }
        return doc;
    }

    private static void println(final String msg) {
        System.out.println(msg);
    }
}
