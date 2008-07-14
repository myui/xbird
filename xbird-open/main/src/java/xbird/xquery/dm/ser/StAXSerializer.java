/*
 * @(#)$Id: StAXSerializer.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.ser;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import xbird.xquery.*;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @see javax.xml.stream.XMLStreamWriter
 */
public class StAXSerializer extends Serializer {

    private final XMLStreamWriter writer;

    public StAXSerializer(XMLStreamWriter writer) {
        super();
        this.writer = writer;
    }

    public void evStartDocument() throws XQueryException {
        try {
            writer.writeStartDocument();
        } catch (XMLStreamException e) {
            wrapStaxException(e);
        }
    }

    public void evEndDocument() throws XQueryException {
        try {
            writer.writeEndDocument();
        } catch (XMLStreamException e) {
            wrapStaxException(e);
        }
    }

    public void evStartElement(String prefix, String localName, String namespaceURI)
            throws XQueryException {
        try {
            writer.writeStartElement(prefix, localName, namespaceURI);
        } catch (XMLStreamException e) {
            wrapStaxException(e);
        }
    }

    public void evEndElement(String prefix, String localName, String namespaceURI)
            throws XQueryException {
        try {
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            wrapStaxException(e);
        }
    }

    public void evAttribute(QualifiedName qname, String value) throws XQueryException {
        try {
            writer.writeAttribute(qname.getPrefix(), qname.getNamespaceURI(), qname.getLocalPart(), value);
        } catch (XMLStreamException e) {
            wrapStaxException(e);
        }
    }

    public void evNamespace(String prefix, String uri) throws XQueryException {
        try {
            writer.writeNamespace(prefix, uri);
        } catch (XMLStreamException e) {
            wrapStaxException(e);
        }
    }

    public void evProcessingInstruction(String target, String data) throws XQueryException {
        try {
            writer.writeProcessingInstruction(target, data);
        } catch (XMLStreamException e) {
            wrapStaxException(e);
        }
    }

    public void evText(char[] ch, int start, int length) throws XQueryException {
        try {
            writer.writeCharacters(ch, start, length);
        } catch (XMLStreamException e) {
            wrapStaxException(e);
        }
    }

    public void evCData(char[] ch, int start, int length) throws XQueryException {
        try {
            writer.writeCData(String.valueOf(ch, start, length));
        } catch (XMLStreamException e) {
            wrapStaxException(e);
        }
    }

    public void evComment(char[] ch, int start, int length) throws XQueryException {
        try {
            writer.writeComment(String.valueOf(ch, start, length));
        } catch (XMLStreamException e) {
            wrapStaxException(e);
        }
    }

    private static XQRTException wrapStaxException(XMLStreamException ex) throws DynamicError {
        throw new DynamicError("Writing SAX event failed", ex);
    }

    protected void flushElement() throws XQueryException {
        throw new IllegalStateException(getClass().getSimpleName()
                + "#flushElement() should not be called");
    }

    public void endItem(boolean last) throws XQueryException {}

}
