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
package scenario.multithread;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.Configuration;
import net.sf.saxon.event.SaxonOutputKeys;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.trans.XPathException;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import xbird.util.xml.SAXWriter;
import xbird.xquery.XQueryException;
import xbird.xquery.XQueryModule;
import xbird.xquery.XQueryProcessor;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.dm.ser.Serializer;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class MultithreadedInvocationTest {

    static {
        DocumentBuilderFactory ctrlDbf = XMLUnit.getControlDocumentBuilderFactory();
        ctrlDbf.setIgnoringElementContentWhitespace(true);
        DocumentBuilderFactory testDbf = XMLUnit.getTestDocumentBuilderFactory();
        testDbf.setIgnoringElementContentWhitespace(true);
        XMLUnit.setIgnoreWhitespace(true);
    }

    public MultithreadedInvocationTest() {}

    public static void main(String[] args) {
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] { MultithreadedInvocationTest.class });
        TestListenerAdapter tla = new TestListenerAdapter();
        testng.addListener(tla);
        testng.run();
    }

    @Test(invocationCount = 100, threadPoolSize = 20)
    public void invokeQueryPushMode() throws XQueryException, XMLStreamException, XPathException,
            SAXException, IOException {
        String query = "declare namespace hoge=\"http://www.hoge.jp/dtd\"; <records xmlns:hoge=\"http://www.hoge.jp/dtd\"> {for $t in doc(\"./main/test/java/scenario/multithread/data-s.xml\")/records/record return <record>{$t/@*}{$t/author/text()}{ fn:concat(\"《\", $t/title/text() , \"》\") }</record>} </records>";
        String xbirdOut = invokeQueryPushModeUsingSAX(query);
        String saxonOut = invokeQueryBySaxon(query);
        Diff diff = new Diff(saxonOut, xbirdOut);
        if(!diff.identical()) {
            if(!diff.similar()) {
                System.err.println(diff.toString());
            }
        }
    }

    private static String invokeQueryPushModeUsingSAX(String query) throws XQueryException,
            XMLStreamException, UnsupportedEncodingException {
        XQueryProcessor proc = new XQueryProcessor();
        InputStream input = new ByteArrayInputStream(query.getBytes("UTF-8"));
        XQueryModule module = proc.parse(input);
        StringWriter writer = new StringWriter(8192);
        SAXWriter saxwr = new SAXWriter(writer);
        Serializer ser = new SAXSerializer(saxwr, writer);
        //ser.setInterveBlanks(false);
        proc.execute(module, ser);
        return writer.toString();
    }

    private static String invokeQueryBySaxon(String query) throws XPathException {
        Configuration config = new Configuration();
        config.setHostLanguage(Configuration.XQUERY);
        StaticQueryContext staticContext = new StaticQueryContext(config);
        XQueryExpression exp = staticContext.compileQuery(query);
        DynamicQueryContext dynamicContext = new DynamicQueryContext(config);
        StringWriter res_sw = new StringWriter();
        Properties props = new Properties();
        props.setProperty(SaxonOutputKeys.WRAP, "no");
        props.setProperty(OutputKeys.INDENT, "no");
        props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        exp.run(dynamicContext, new StreamResult(res_sw), props);
        return res_sw.toString();
    }

}
