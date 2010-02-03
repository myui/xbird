/*
 * @(#)$Id: SAXEventWriterTest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm;

import java.io.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import xbird.xquery.dm.dtm.*;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class SAXEventWriterTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SAXEventWriterTest.class);
    }

    public SAXEventWriterTest(String name) {
        super(name);
    }

    public void testParse() throws Exception {
        try {
            System.gc();
            long before_usedm = Runtime.getRuntime().totalMemory()
                    - Runtime.getRuntime().freeMemory();
            long start = System.currentTimeMillis();
            //parse(getClass().getResourceAsStream("test/test01.xml"));
            //parse(new FileInputStream("D:/Software/xmark/auction.xml"));
            //System.gc();
            parse(new FileInputStream("C:/Software/xmark/xmark1.xml"));
            //System.gc();
            //parse(new FileInputStream("C:/Software/xmark/xmark10.xml"));
            System.out.println("elasped time: " + (System.currentTimeMillis() - start) + " ms");
            long after_usedm = Runtime.getRuntime().totalMemory()
                    - Runtime.getRuntime().freeMemory();
            System.out.println("memory used: " + (after_usedm - before_usedm) + " bytes");
            System.gc();
            after_usedm = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.out.println("memory used(after GC): " + (after_usedm - before_usedm) + " bytes");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void parse(InputStream is) throws Exception {
        //DocumentTable store = new ProfiledDocumentTable(File.createTempFile("dtm_profile", "csv"));
        IDocumentTable store = new DocumentTable();
        DocumentTableBuilder handler = new DocumentTableBuilder(store);
        XMLReader reader = getXMLReader(handler);
        reader.parse(new InputSource(new BufferedInputStream(is)));
    }

    protected static final XMLReader getXMLReader(DocumentTableBuilder handler) throws Exception {
        final XMLReader myReader;

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser parser = factory.newSAXParser();
        myReader = parser.getXMLReader();

        // setup handlers (requires saxHandler)
        myReader.setContentHandler(handler);
        myReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
        myReader.setFeature("http://xml.org/sax/features/validation", true);
        myReader.setFeature("http://apache.org/xml/features/validation/dynamic", true);
        myReader.setFeature("http://apache.org/xml/features/validation/schema", true);

        return myReader;
    }

}
