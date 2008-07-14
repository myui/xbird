/*
 * @(#)$Id: DTMTreeBuilder.java 3619 2008-03-26 07:23:03Z yui $
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

import static xbird.xquery.dm.NodeKind.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.dtm.DocumentTable;
import xbird.xquery.dm.dtm.IDocumentTable;
import xbird.xquery.dm.instance.DocumentTableModel;
import xbird.xquery.dm.ser.Serializer;
import xbird.xquery.dm.value.XQNode;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @deprecated
 */
public final class DTMTreeBuilder extends Serializer {

    private final IDocumentTable _table;
    private final DocumentTableModel _model;

    private long _rootNodeId = -1L;
    private byte _rootEvent = -1;

    private boolean _hasPendingElem = false;
    /** #1: prefix, #2: localName, #3: namespaceURI. */
    private final String[] _pendingElem = new String[3];
    private final List<Attribute> _pendingAtts = new ArrayList<Attribute>(8);
    private final List<Namespace> _pendingNSDecls = new ArrayList<Namespace>(4);

    public DTMTreeBuilder() {
        this._table = new DocumentTable(); // TODO not efficient at memory-usage, there may be lots of nodes in result sequnece.
        this._model = new DocumentTableModel(_table, true);
    }

    public XQNode harvest() {
        if(_rootEvent == -1) {
            assert (_rootNodeId == -1);
            return null;
        }
        assert (_rootNodeId != -1);
        return _model.createNode(_rootEvent, _rootNodeId);
    }

    public void reset() {
        this._rootNodeId = -1L;
        this._rootEvent = -1;
        this._hasPendingElem = false;
        _pendingAtts.clear();
        _pendingNSDecls.clear();
    }

    private void setRootIfNotAlreadySet(byte ev, long nodeId) {
        if(_rootEvent == -1) {
            this._rootEvent = ev;
            this._rootNodeId = nodeId;
        }
    }

    public void evStartDocument() throws XQueryException {
        setRootIfNotAlreadySet(DOCUMENT, _table.openNode(DOCUMENT));
    }

    public void evEndDocument() throws XQueryException {
        _table.closeNode();
    }

    public void evStartElement(String prefix, String localName, String namespaceURI)
            throws XQueryException {
        if(_hasPendingElem) {
            flushElement();
        }
        _pendingElem[0] = prefix;
        _pendingElem[1] = localName;
        _pendingElem[2] = namespaceURI;
        this._hasPendingElem = true;
    }

    public void evEndElement(String prefix, String localName, String namespaceURI)
            throws XQueryException {
        if(_hasPendingElem) {
            flushElement();
        }
        _table.closeNode();
    }

    public void evAttribute(QualifiedName qname, String value) throws XQueryException {
        _pendingAtts.add(new Attribute(qname, value));
    }

    private static final class Attribute {
        final QualifiedName qname;
        final String value;

        public Attribute(QualifiedName qname, String value) {
            this.qname = qname;
            this.value = value;
        }
    }

    public void evNamespace(String prefix, String uri) throws XQueryException {
        _pendingNSDecls.add(new Namespace(prefix, uri));
    }

    private static final class Namespace {
        final String prefix;
        final String uri;

        public Namespace(String prefix, String uri) {
            this.prefix = prefix;
            this.uri = uri;
        }
    }

    public void evText(char[] ch, int start, int length) throws XQueryException {
        if(_hasPendingElem) {
            flushElement();
        }
        long nid = _table.openNode(TEXT);
        setRootIfNotAlreadySet(TEXT, nid);
        _table.setTextAt(nid, ch, start, length);
        _table.closeNode();
    }

    public void evCData(char[] ch, int start, int length) throws XQueryException {
        if(_hasPendingElem) {
            flushElement();
        }
        long nid = _table.openNode(CDATA);
        setRootIfNotAlreadySet(CDATA, nid);
        _table.setTextAt(nid, ch, start, length);
        _table.closeNode();
    }

    public void evComment(char[] ch, int start, int length) throws XQueryException {
        if(_hasPendingElem) {
            flushElement();
        }
        long nid = _table.openNode(COMMENT);
        setRootIfNotAlreadySet(COMMENT, nid);
        _table.setTextAt(nid, ch, start, length);
        _table.closeNode();
    }

    public void evProcessingInstruction(String target, String data) throws XQueryException {
        if(_hasPendingElem) {
            flushElement();
        }
        long nid = _table.openNode(PROCESSING_INSTRUCTION);
        setRootIfNotAlreadySet(PROCESSING_INSTRUCTION, nid);
        _table.setName(nid, data, target);
        _table.closeNode();
    }

    //TODO REVIEWME
    public void endItem(boolean last) throws XQueryException {}

    protected void flushElement() throws XQueryException {
        if(!_hasPendingElem) {
            throw new IllegalStateException();
        }
        long elemid = _table.openNode(ELEMENT);
        setRootIfNotAlreadySet(ELEMENT, elemid);
        String prefix = _pendingElem[0];
        String lname = _pendingElem[1];
        assert (lname != null && lname.length() > 0) : lname;
        String nsuri = _pendingElem[2];
        _table.setName(elemid, nsuri, lname, prefix);

        flushDependants(elemid);

        this._hasPendingElem = false;
    }

    private void flushDependants(long parent) throws XQueryException {
        if(!_pendingNSDecls.isEmpty()) {
            int nslen = _pendingNSDecls.size();
            for(int i = 0; i < nslen; i++) {
                Namespace ns = _pendingNSDecls.get(i);
                long nsid = _table.putAttribute(NAMESPACE, parent, i, nslen);
                _table.setAttributeName(nsid, XMLConstants.XMLNS_ATTRIBUTE_NS_URI, ns.prefix);
                _table.setTextAt(nsid, ns.uri);
            }
            _pendingNSDecls.clear();
        }
        if(!_pendingAtts.isEmpty()) {
            int attlen = _pendingAtts.size();
            for(int i = 0; i < attlen; i++) {
                Attribute att = _pendingAtts.get(i);
                long attid = _table.putAttribute(ATTRIBUTE, parent, i, attlen);
                QualifiedName qname = att.qname;
                _table.setAttributeName(attid, qname.getNamespaceURI(), qname.getLocalPart(), qname.getPrefix());
                _table.setTextAt(attid, att.value);
            }
            _pendingAtts.clear();
        }
    }

}
