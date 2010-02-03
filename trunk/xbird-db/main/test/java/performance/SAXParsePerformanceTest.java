/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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
package performance;

import java.io.*;

import javax.xml.parsers.*;

import junit.framework.TestCase;

import org.xml.sax.*;

import xbird.util.datetime.StopWatch;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class SAXParsePerformanceTest extends TestCase {

    private static final String XML_FILE = "/tmp/xpathmark5.out";

    public void testSaxParse() throws SAXException, FileNotFoundException, IOException,
            ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setContentHandler(new NopHandler());
        StopWatch sw = new StopWatch("Sax parse time");
        reader.parse(new InputSource(new FileInputStream(XML_FILE)));
        System.err.println(sw);
    }

    private static final class NopHandler implements ContentHandler {

        public void characters(char[] ch, int start, int length) throws SAXException {}

        public void endDocument() throws SAXException {}

        public void endElement(String uri, String localName, String qName) throws SAXException {}

        public void endPrefixMapping(String prefix) throws SAXException {}

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}

        public void processingInstruction(String target, String data) throws SAXException {}

        public void setDocumentLocator(Locator locator) {}

        public void skippedEntity(String name) throws SAXException {}

        public void startDocument() throws SAXException {}

        public void startElement(String uri, String localName, String qName, Attributes atts)
                throws SAXException {}

        public void startPrefixMapping(String prefix, String uri) throws SAXException {}

    }
}
