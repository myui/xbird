/*
 * @(#)$Id: XDMTreeBuilder.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.coder;

import javax.xml.XMLConstants;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.ser.Serializer;
import xbird.xquery.dm.value.node.*;
import xbird.xquery.dm.value.sequence.NodeSequence;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class XDMTreeBuilder extends Serializer {

    private DMNode _root = null;
    private DMNode _current = null;

    public XDMTreeBuilder() {}

    public DMNode harvest() {
        return _root;
    }

    public void reset() {
        this._root = null;
        this._current = null;
    }

    private void addChild(DMNode n) {
        if(_root == null) {
            this._root = n;
        }
        if(_current != null) {
            if(_current instanceof DMElement) {
                ((DMElement) _current).addChild(n);
            } else if(_current instanceof DMDocument) {
                NodeSequence<DMNode> nodes = new NodeSequence<DMNode>(DynamicContext.DUMMY);
                nodes.addItem(n);
                ((DMDocument) _current).setChildren(nodes);
            } else {
                throw new IllegalStateException("Illegal node type: "
                        + _current.getClass().getName());
            }
            if(n instanceof DMDocument || n instanceof DMElement) {
                this._current = n;
            }
        } else {
            this._current = n;
        }
    }

    public void addAttribute(DMAttribute att) {
        if(_root == null) {
            this._root = att;
        }
        if(_current != null) {
            if(_current instanceof DMElement) {
                ((DMElement) _current).addAttribute(att);
            } else {
                throw new IllegalStateException("Illegal node type: " + att.getType());
            }
        }
    }

    public void evStartDocument() throws XQueryException {
        if(_root != null) {
            throw new IllegalStateException("document root already exists");
        }
        DMDocument doc = new DMDocument();
        _root = _current = doc;
    }

    public void evEndDocument() throws XQueryException {
        _current = null;
    }

    public void evStartElement(String prefix, String localName, String namespaceURI)
            throws XQueryException {
        QualifiedName qname = QNameTable.instantiate(namespaceURI, localName, prefix);
        DMElement elem = new DMElement(qname);
        addChild(elem);
    }

    public void evEndElement(String prefix, String localName, String namespaceURI)
            throws XQueryException {
        _current = _current.parent();
    }

    public void evAttribute(QualifiedName qname, String value) throws XQueryException {
        DMAttribute att = new DMAttribute(qname, value);
        addAttribute(att);
    }

    public void evNamespace(String prefix, String uri) throws XQueryException {
        QualifiedName attName = QNameTable.instantiate(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE, prefix);
        DMNamespace ns = new DMNamespace(attName, uri);
        addAttribute(ns);
    }

    public void evText(char[] ch, int start, int length) throws XQueryException {
        DMText text = new DMText(String.valueOf(ch, start, length));
        addChild(text);
    }

    public void evCData(char[] ch, int start, int length) throws XQueryException {
        this.evText(ch, start, length);//TODO REVIEWME
    }

    public void evComment(char[] ch, int start, int length) throws XQueryException {
        DMComment comment = new DMComment(String.valueOf(ch, start, length));
        addChild(comment);
    }

    public void evProcessingInstruction(String target, String data) throws XQueryException {
        DMProcessingInstruction pi = new DMProcessingInstruction(target, data);
        addChild(pi);
    }

    @Override
    protected void flushElement() throws XQueryException {}

    public void endItem(boolean last) throws XQueryException {}
}
