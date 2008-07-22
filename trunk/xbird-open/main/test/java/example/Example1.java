/*
 * @(#)$Id: codetemplate_xbird.xml 3792 2008-04-21 21:39:23Z yui $
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
package example;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URL;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import xbird.util.io.NoopWriter;
import xbird.util.net.NetUtils;
import xbird.util.xml.SAXWriter;
import xbird.xquery.XQueryException;
import xbird.xquery.XQueryModule;
import xbird.xquery.XQueryProcessor;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.dm.ser.Serializer;
import xbird.xquery.dm.ser.StAXSerializer;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.meta.IFocus;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.type.Type;
import xbird.xquery.type.node.NodeType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class Example1 {

    public Example1() {}

    public static void main(String[] args) {
        try {
            new Example1().doMain(args);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private void doMain(String[] args) throws IOException, XQueryException, XMLStreamException {
        URL queryFileUrl = Example1.class.getResource("bib_relative.xq");
        InputStream input1 = queryFileUrl.openStream();
        URI baseUri = NetUtils.toURI(queryFileUrl); // This baseUri effects to fn:doc(...)
        invokeQueryPullMode(input1, baseUri);
        input1.close();

        System.out.println("--------------------------------");

        InputStream input2 = Example1.class.getResourceAsStream("bib_absolute.xq");
        invokeQueryPushMode(input2, null);
    }

    private static void invokeQueryPullMode(InputStream input, URI baseUri) throws XQueryException {
        XQueryProcessor proc = new XQueryProcessor();
        // #1 parse a query
        XQueryModule module = proc.parse(input, baseUri);
        // #2 execute the compiled expression using ``pull'' mode
        Sequence<? extends Item> result = proc.execute(module);

        // prepare SAX result handler
        Writer writer = new NoopWriter();
        SAXWriter saxHandler = new SAXWriter(writer, "UTF-8"); // SAXWriter implements ContentHandler
        saxHandler.setPrettyPrint(true); // enabled formatting
        saxHandler.setXMLDeclaration(true); // insert XML declaration to output
        Serializer ser = new SAXSerializer(saxHandler);
        ser.emit(result); // emit SAX events to SAX handler

        for(Item item : result) {// Note that every Sequence instances implement Iterable. 
            // get string value
            String stringValue = item.stringValue();
            // get type
            Type itemType = item.getType();
            if(TypeUtil.subtypeOf(itemType, NodeType.ELEMENT)) {
                // the Item is element                
            } else if(TypeUtil.instanceOf(item, NodeType.TEXT)) {
                // another variant
            }

            // print string value
            System.out.println(item); // item.toString() invokes item.stringValue()

            // Item is subclass of Sequence, thus it can also be converted to SAX events.
            ser.emit(item);
        }

        // The above 'for each' is equivalent to the following expression. 
        IFocus focus = result.iterator(); // IFocus extends Iterable
        while(result.next(focus)) {
            Item item = focus.getContextItem();
            // ..
        }
    }

    private static void invokeQueryPushMode(InputStream input, URI baseUri) throws XQueryException,
            XMLStreamException {
        XQueryProcessor proc = new XQueryProcessor();
        // #1 parse a query
        XQueryModule module = proc.parse(input);

        // prepare a result handler (StAX)
        Writer writer = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter streamWriter = factory.createXMLStreamWriter(writer);
        XQEventReceiver handler = new StAXSerializer(streamWriter);

        // #2 execute the compiled expression using ``push'' mode
        //   In push mode, the result is directed to the events.
        proc.execute(module, handler);

        streamWriter.flush(); // flushing is required
        System.out.println(writer.toString());
    }

}
