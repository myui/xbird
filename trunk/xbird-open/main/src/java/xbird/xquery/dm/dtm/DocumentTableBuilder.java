/*
 * @(#)$Id: DocumentTableBuilder.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.dtm;

import static xbird.xquery.dm.NodeKind.*;

import java.util.*;

import javax.xml.XMLConstants;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import xbird.xquery.dm.IDocument;
import xbird.xquery.misc.BaseDocumentHandler;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DocumentTableBuilder extends BaseDocumentHandler {

    private final IDocument _store;
    private boolean _dirty = false;
    private final TextHolder _textholder = new TextHolder();
    private final Stack<String> _pendingNsDecl = new Stack<String>();
    private final Map<String, String> _nsMap;

    //  ---------------------------------------

    public DocumentTableBuilder(IDocument store) {
        super();
        this._store = store;
        Map<String, String> nsmap = new HashMap<String, String>(12);
        store.setDeclaredNamespaces(nsmap);
        this._nsMap = nsmap;
    }

    public void init() {
        if(_dirty) {
            _textholder.init();
            _pendingNsDecl.clear();
            this._dirty = false;
        }
    }

    public void startDocument() throws SAXException {
        init();
        this._dirty = true;
        _store.openNode(DOCUMENT);
    }

    public void endDocument() throws SAXException {
        _store.closeNode();
        init();
    }

    public void startElement(String uri, String localName, String qName, Attributes atts)
            throws SAXException {
        assert (uri != null);
        if(_textholder.getContentLength() > 0) {
            createTextNode();
        }
        /* element */
        String prefix;
        if(qName == null) {
            prefix = _namespace.getPrefix(uri);
        } else {
            int colonPtr = qName.indexOf(':');
            if(colonPtr == -1) {
                prefix = "";
                localName = qName;
            } else {
                prefix = qName.substring(0, colonPtr);
                localName = qName.substring(colonPtr + 1);
            }
        }
        long elemidx = _store.openNode(ELEMENT);
        assert (localName != null);
        _store.setName(elemidx, uri, localName, prefix);
        /* namespace decl */
        if(!_pendingNsDecl.isEmpty()) {
            createNamespaceDecl(elemidx);
        }
        int attlen = atts.getLength();
        for(int i = 0; i < attlen; i++) {
            String att = atts.getQName(i);
            /* attribute */
            long attidx = _store.putAttribute(ATTRIBUTE, elemidx, i, attlen);
            int pos = att.indexOf(':'); // if not found, pos = -1
            String att_lname = att.substring(pos + 1);
            assert (!"xmlns".equals(att_lname));
            if(pos != -1) {
                String att_prefix = att.substring(0, pos);
                assert (!"xmlns".equals(att_prefix));
                _store.setAttributeName(attidx, atts.getURI(i), att_lname, att_prefix);
            } else {
                _store.setAttributeName(attidx, atts.getURI(i), att_lname);
            }
            _store.setTextAt(attidx, atts.getValue(i));
        }
    }

    private void createNamespaceDecl(long elemidx) {
        int size = _pendingNsDecl.size() / 2;
        for(int i = 0; !_pendingNsDecl.isEmpty(); i++) {
            String nsuri = _pendingNsDecl.pop();
            String lname = _pendingNsDecl.pop();
            long nsidx = _store.putAttribute(NAMESPACE, elemidx, i, size);
            _store.setAttributeName(nsidx, XMLConstants.XMLNS_ATTRIBUTE_NS_URI, lname);
            _store.setTextAt(nsidx, nsuri);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(_textholder.getContentLength() > 0) {
            createTextNode();
        }
        _store.closeNode();
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if(length > 0) {
            _textholder.append(ch, start, length);
        }
    }

    public void processingInstruction(String target, String data) throws SAXException {
        assert (target != null);
        if(_textholder.getContentLength() > 0) {
            createTextNode();
        }
        long idx = _store.openNode(PROCESSING_INSTRUCTION);
        _store.setName(idx, data, target); // TODO hack
        _store.closeNode();
    }

    public void startCDATA() throws SAXException {
        if(_textholder.getContentLength() > 0) {
            createTextNode();
        } else {
            _textholder.isCDATA = true;
        }
    }

    public void endCDATA() throws SAXException {
        if(_textholder.getContentLength() > 0) {
            createTextNode();
        } else {
            _textholder.isCDATA = false;
        }
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        if(_textholder.getContentLength() > 0) {
            createTextNode();
        }
        long index = _store.openNode(COMMENT);
        _store.setTextAt(index, ch, start, length);
        _store.closeNode();
    }

    private void createTextNode() throws SAXException {
        long index = _store.openNode(_textholder.isCDATA ? CDATA : TEXT);
        _store.setTextAt(index, _textholder.content, 0, _textholder.charPtr);
        _store.closeNode();
        _textholder.init();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        assert (prefix != null && uri != null);
        final String lname = prefix.length() == 0 ? XMLConstants.NULL_NS_URI : prefix;
        _pendingNsDecl.push(lname);
        _pendingNsDecl.push(uri);
        _nsMap.put(lname, uri);
        super.startPrefixMapping(prefix, uri);
    }

    private static final class TextHolder {
        private boolean isCDATA = false;
        private char[] content = new char[1024];
        private int charPtr = 0;

        private TextHolder() {}

        private void init() {
            this.isCDATA = false;
            this.charPtr = 0;
        }

        private void append(char[] str, int offset, int len) {
            final int newPtr = charPtr + len;
            if(newPtr > content.length) {
                final int newCapacity = newPtr + 2048;
                final char[] copy = new char[newCapacity];
                System.arraycopy(content, 0, copy, 0, charPtr);
                this.content = copy;
            }
            System.arraycopy(str, offset, content, charPtr, len);
            this.charPtr = newPtr;
        }

        private int getContentLength() {
            return charPtr;
        }

        public char[] getRawContent() {
            return content;
        }
    }
}
