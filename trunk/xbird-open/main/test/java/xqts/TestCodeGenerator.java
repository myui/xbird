/*
 * @(#)$Id: TestCodeGenerator.java 1223 2006-10-30 04:07:22Z yui $
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import xbird.util.io.IOUtils;
import xbird.util.xml.NamespaceBinder;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class TestCodeGenerator {

    private static final String ADDR_TEST_GROUPS = "//ns:test-group[count(child::ns:test-group)=0]";
    private static final String DEFAULT_TEST_DEST_DIR = "main/test/java/xqts";
    private static final String DEFAULT_TEST_RESOURCE_DIR = "main/test/resources/xqts";
    private static final String TEST_DEST_DIR = XQTSTestBase.XQTS_PROP.getProperty("test_dest_dir", DEFAULT_TEST_DEST_DIR);
    private static final String TEST_RESOURCE_DIR = XQTSTestBase.XQTS_PROP.getProperty("test_resource_dir", DEFAULT_TEST_RESOURCE_DIR);

    private TestCodeGenerator() {}

    public static void main(String[] args) {
        try {
            prepare();
            generate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static void prepare() throws IOException {
        File testResourceDir = new File(TEST_RESOURCE_DIR);
        assert (testResourceDir.exists()) : testResourceDir.getAbsolutePath();
        File testCaseList = new File(testResourceDir, "TestCase.list");
        if(testCaseList.exists()) {
            boolean status = testCaseList.delete();
            assert (status) : "delete file failed: " + testCaseList.getAbsolutePath();
        }
        testCaseList = new File(testResourceDir, "TestCase.list");
        assert (!testCaseList.exists());
        boolean created = testCaseList.createNewFile();
        assert (created) : "File creation failed: " + testCaseList.getAbsolutePath();
    }

    private static void generate() throws Exception {
        final Document catalog = XQTSTestBase.catalogPool.borrowObject();
        final XPath xpath = XPathFactory.newInstance().newXPath();
        NamespaceBinder resolver = new NamespaceBinder();
        resolver.declarePrefix(XQTSTestBase.CATALONG_URI_PREFIX, XQTSTestBase.CATALONG_URI);
        xpath.setNamespaceContext(resolver);
        int tcCount = 0, tcGenCount = 0;
        File testDestDir = new File(TEST_RESOURCE_DIR);
        assert (testDestDir.exists()) : testDestDir.getAbsolutePath();
        File destFile = new File(testDestDir, "TestCase.list");
        PrintWriter pw = new PrintWriter(destFile);
        NodeList rs = (NodeList) xpath.evaluate(ADDR_TEST_GROUPS, catalog, XPathConstants.NODESET);
        final int rslen = rs.getLength();
        for(int i = 0; i < rslen; i++) {
            final String ADDR_TEST_GROUP = '(' + ADDR_TEST_GROUPS + ")[" + (i + 1) + ']';
            Node testGroup = rs.item(i);
            assert (testGroup != null);
            assert (testGroup.hasAttributes());
            String filepath = (String) xpath.evaluate("ns:test-case[1]/@FilePath", testGroup, XPathConstants.STRING);
            if(filepath == null || filepath.length() == 0) {
                // test-group might have no test-case
                continue;
            }
            assert (filepath.endsWith("/")) : filepath;
            String testDir = filepath.replace('-', '_').toLowerCase().substring(0, filepath.lastIndexOf('/'));
            String packageName = testDir.replace('/', '.');
            String[] dirs = testDir.split("/");
            assert (dirs.length > 0);
            File parentDir = new File(TEST_DEST_DIR);
            assert (parentDir.isDirectory());
            for(String dir : dirs) {
                assert (parentDir.exists());
                File curDir = new File(parentDir, dir);
                if(!curDir.exists()) {
                    boolean mkdirSucc = curDir.mkdir();
                    assert (mkdirSucc);
                    System.err.println("Created directory.. " + curDir.getAbsolutePath());
                }
                parentDir = curDir;
            }
            assert (parentDir.exists());
            final String CLASS_NAME = toClassName(testGroup.getAttributes().getNamedItem("name").getNodeValue())
                    + "Test";
            final String CLASS_SRC_FILE = CLASS_NAME + ".java";
            File classSrcFile = new File(parentDir, CLASS_SRC_FILE);
            if(!classSrcFile.exists()) {
                final String tmpl = IOUtils.toString(TestCodeGenerator.class.getResourceAsStream("XQTSTest.template"));
                final String PACKAGE_NAME = ("xqts." + testDir.replace('/', '.'));
                assert (CLASS_NAME != null);
                final String TEST_PATH = '(' + ADDR_TEST_GROUP + "//ns:test-case)";
                final String code = tmpl.replace("$XQTS_VERSION", XQTSTestBase.xqtsVersion).replace("$PACKAGE", PACKAGE_NAME).replace("$TEST_PATH", TEST_PATH).replace("$CLASSNAME", CLASS_NAME);
                final StringBuilder codeBuf = new StringBuilder(512);
                assert (code.length() > 0);
                codeBuf.append(code, 0, code.lastIndexOf('}') - 1);
                final int count = countTests(TEST_PATH, catalog);
                assert (count >= 1) : count;
                String methodTmpl = IOUtils.toString(TestCodeGenerator.class.getResourceAsStream("TestMethod.template"));
                methodTmpl = methodTmpl.replace("$TIMEOUT", XQTSTestBase.XQTS_PROP.getProperty("test.timeout")).replace("$TESTPATH", TEST_PATH);
                NodeList methods = (NodeList) xpath.evaluate(TEST_PATH + "/@name", catalog, XPathConstants.NODESET);
                assert (count == methods.getLength());
                for(int j = 0; j < count; j++) {
                    String methodName = methods.item(j).getTextContent();
                    assert (methodName != null);
                    methodName = toClassName(methodName);
                    final String m = methodTmpl.replace("$METHOD_NAME", methodName).replace("$i", String.valueOf(j + 1));
                    codeBuf.append(m);
                }
                codeBuf.append('}');
                final OutputStream fos = new FileOutputStream(classSrcFile);
                fos.write(codeBuf.toString().getBytes());
                fos.flush();
                System.err.println("Created source file.. " + classSrcFile.getAbsolutePath());
                ++tcGenCount;
                fos.close();
                addToTestSuite(pw, packageName, CLASS_NAME);
            } else {
                addToTestSuite(pw, packageName, CLASS_NAME);
            }
            ++tcCount;
        }
        pw.flush();
        pw.close();
        System.out.println("Generation successfully done.");
        System.out.println("Total testCases: " + tcCount + ", generated in this session: "
                + tcGenCount);
        //XQTSTestBase.catalogPool.returnObject(catalog);
    }

    private static void addToTestSuite(PrintWriter pw, String packageName, String className)
            throws IOException {
        assert (packageName != null);
        assert (className != null);
        String line = "xqts." + packageName + '.' + className + '\n';
        pw.write(line);
    }

    protected static int countTests(String testPath, Document catalog)
            throws XPathExpressionException {
        final XPath xpath = XPathFactory.newInstance().newXPath();
        NamespaceBinder resolver = new NamespaceBinder();
        resolver.declarePrefix(XQTSTestBase.CATALONG_URI_PREFIX, XQTSTestBase.CATALONG_URI);
        xpath.setNamespaceContext(resolver);
        final String count = "count(" + testPath + ")";
        XPathExpression expr = xpath.compile(count);
        final Double d = (Double) expr.evaluate(catalog, XPathConstants.NUMBER);
        return d.intValue();
    }

    private static String toClassName(String name) {
        final StringBuilder buf = new StringBuilder(64);
        String nameBlocks[] = name.split("-");
        for(String n : nameBlocks) {
            char c = n.charAt(0);
            if(Character.isDigit(c)) {
                if(Character.isDigit(buf.charAt(buf.length() - 1))) {
                    buf.append('_');
                }
                buf.append(c);
            } else {
                buf.append(Character.toUpperCase(c));
            }
            if(n.length() > 1) {
                buf.append(n.substring(1));
            }
        }
        String className = buf.toString();
        int tail = className.indexOf('.');
        if(tail == -1) {
            return className;
        }
        return className.substring(0, tail);
    }

}
