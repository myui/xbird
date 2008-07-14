/*
 * @(#)$Id: SAXSerializer.java 3619 2008-03-26 07:23:03Z yui $
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

import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;

import java.io.Writer;
import java.util.*;

import javax.xml.XMLConstants;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

import xbird.util.xml.*;
import xbird.util.xml.XMLUtils;
import xbird.xquery.*;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class SAXSerializer extends Serializer {

    private static final String NS_STACK_BORDER = "#";
    private static final String INCR_NAMESPACE_PREFIX = System.getProperty("xbird.incr_ns_prefix", "XXX");
    private static final String INCR_NAMESPACEDEF = "xmlns:" + INCR_NAMESPACE_PREFIX;

    private final ContentHandler _contentHandle;
    private LexicalHandler _lexicalHandle = null;

    private boolean _hasPendingElem = false;
    /** #1: prefix, #2: localName, #3: namespaceURI. */
    private final String[] _pendingElem = new String[3];
    private final AttributesImpl _pendingAtts = new AttributesImpl();
    private final Stack<String> _pendingEndPrefixMapping = new Stack<String>();

    protected final NamespaceBinder _namespace = new NamespaceBinder();

    protected final Writer _writer;

    public SAXSerializer(ContentHandler chandle) {
        this(chandle, getWriter(chandle));
    }

    public SAXSerializer(ContentHandler chandle, Writer writer) {
        this._contentHandle = chandle;
        if(chandle instanceof LexicalHandler) {
            this._lexicalHandle = (LexicalHandler) chandle;
        }
        this._writer = writer;
    }

    private static Writer getWriter(ContentHandler chandle) {
        if(chandle instanceof SAXWriter) {
            return ((SAXWriter) chandle).getWriter();
        }
        return null;
    }

    public void setLexicalHandler(LexicalHandler lexicalHandler) {
        this._lexicalHandle = lexicalHandler;
    }

    public void evStartDocument() throws XQueryException {
        try {
            _contentHandle.startDocument();
        } catch (SAXException e) {
            wrapSAXException(e);
        }
    }

    public void evEndDocument() throws XQueryException {
        try {
            _contentHandle.endDocument();
        } catch (SAXException e) {
            wrapSAXException(e);
        }
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
        _pendingEndPrefixMapping.push(NS_STACK_BORDER);
    }

    public void evEndElement(String prefix, String localName, String namespaceURI)
            throws XQueryException {
        if(_hasPendingElem) {
            flushElement();
        }
        try {
            _contentHandle.endElement(namespaceURI, localName, (prefix != null && prefix.length() > 0) ? prefix
                    + ':' + localName
                    : localName);
            for(;;) {
                String mapping = _pendingEndPrefixMapping.pop();
                if(mapping == NS_STACK_BORDER) {
                    break;
                }
                _namespace.popContext();
                _contentHandle.endPrefixMapping(mapping);
            }
        } catch (SAXException e) {
            wrapSAXException(e);
        }
    }

    public void evAttribute(QualifiedName qname, String value) throws XQueryException {
        final String prefix = qname.getPrefix();
        final String nsuri = qname.getNamespaceURI();
        if(prefix != null && prefix.length() > 0 && nsuri != null && nsuri.length() > 0) {
            final String resolvedUri = _namespace.getNamespaceURI(prefix);
            if(!nsuri.equals(resolvedUri)) {
                this.evNamespace(prefix, nsuri);
            }
        }
        _pendingAtts.addAttribute(qname.getNamespaceURI(), qname.getLocalPart(), QNameUtil.toLexicalForm(qname), "CDATA", value);
    }

    /**
     * Note: Call endPrefixMapping at the end of flushElement().
     * 
     * @see #flushElement()
     * @see ContentHandler#startPrefixMapping(String, String)
     * @param prefix if `xmlns` -> ``, else if 'xmlns:foo` => `foo`
     */
    public void evNamespace(String prefix, String uri) throws XQueryException {
        assert (prefix != null && uri != null);
        _namespace.pushContext();
        _namespace.declarePrefix(prefix, uri);
        try {
            _contentHandle.startPrefixMapping(prefix, uri);
        } catch (SAXException e) {
            wrapSAXException(e);
        }
        _pendingEndPrefixMapping.push(prefix);
        final String localName;
        final String qname;
        if(prefix.length() != 0) {
            assert (prefix.indexOf(':') == -1) : "Invalid prefix: " + prefix;
            localName = prefix;
            qname = "xmlns:" + prefix;
        } else {
            localName = "xmlns";
            qname = localName;
        }
        _pendingAtts.addAttribute(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, localName, qname, "CDATA", uri);
    }

    public void evText(char[] ch, int start, int length) throws XQueryException {
        if(_hasPendingElem) {
            flushElement();
        }
        try {
            _contentHandle.characters(ch, start, length);
        } catch (SAXException e) {
            wrapSAXException(e);
        }
    }

    public void evCData(char[] ch, int start, int length) throws XQueryException {
        if(_lexicalHandle != null) {
            try {
                _lexicalHandle.startCDATA();
                this.evText(ch, start, length);
                _lexicalHandle.endCDATA();
            } catch (SAXException e) {
                wrapSAXException(e);
            }
        } else {
            this.evText("<![CDATA[");
            this.evText(ch, start, length);
            this.evText("]]>");
        }
    }

    public void evProcessingInstruction(String target, String data) throws XQueryException {
        if(_hasPendingElem) {
            flushElement();
        }
        try {
            _contentHandle.processingInstruction(target, data);
        } catch (SAXException e) {
            wrapSAXException(e);
        }
    }

    public void evComment(char[] ch, int start, int length) throws XQueryException {
        if(_hasPendingElem) {
            flushElement();
        }
        if(_lexicalHandle != null) {
            try {
                _lexicalHandle.comment(ch, start, length);
            } catch (SAXException e) {
                wrapSAXException(e);
            }
        }
    }

    private static XQRTException wrapSAXException(SAXException ex) throws DynamicError {
        throw new DynamicError("Writing SAX event failed", ex);
    }

    protected void flushElement() throws XQueryException {
        final String prefix = _pendingElem[0];
        final String lname = _pendingElem[1];
        assert (lname != null && lname.length() > 0);
        final String nsuri = _pendingElem[2];
        if(nsuri != null && nsuri.length() > 0) {
            final String resolvedUri = _namespace.getNamespaceURI(prefix);
            if(!nsuri.equals(resolvedUri)) {
                this.evNamespace(prefix, nsuri);
            }
        } else {
            String curDefaultNamespace = _namespace.getNamespaceURI(DEFAULT_NS_PREFIX);
            if(curDefaultNamespace != null && curDefaultNamespace.length() > 0) {
                this.evNamespace("", "");
            }
        }

        final AttributesImpl pendingAtts = this._pendingAtts;
        final int attlen = pendingAtts.getLength();
        if(attlen > 0) {
            fixNamespacePrefix(pendingAtts, attlen);
        }

        final String elemQName = (prefix != null && prefix.length() > 0) ? prefix + ':' + lname
                : lname;
        try {
            _contentHandle.startElement(nsuri, lname, elemQName, pendingAtts);
        } catch (SAXException e) {
            wrapSAXException(e);
        }
        pendingAtts.clear();
        this._hasPendingElem = false;
    }

    private static final void fixNamespacePrefix(final AttributesImpl pendingAtts, final int attlen) {
        // fix namespace prefix
        final HashSet<String> attset = new HashSet<String>(attlen);
        final HashMap<String, String> rewrittedMap = new HashMap<String, String>(3);
        for(int i = 0; i < attlen; i++) {
            String attQName = pendingAtts.getQName(i);
            if(attQName.startsWith("xmlns:")) {
                if(attset.contains(attQName)) {
                    String oldAttPrefix = attQName.substring(6);
                    attQName = INCR_NAMESPACEDEF;
                    String newAttPrefix = INCR_NAMESPACE_PREFIX;
                    for(int id = 1;; id++) {
                        if(!attset.contains(attQName)) {
                            break;
                        }
                        attQName = INCR_NAMESPACEDEF + id;
                        newAttPrefix = INCR_NAMESPACE_PREFIX + id;
                    }
                    pendingAtts.setQName(i, attQName);
                    rewrittedMap.put(oldAttPrefix, newAttPrefix);
                }
                attset.add(attQName);
            } else {
                int ptr = attQName.indexOf(':');
                if(ptr > 0) {
                    String oldAttPrefix = attQName.substring(0, ptr);
                    String newPrefix = rewrittedMap.get(oldAttPrefix);
                    if(newPrefix != null) {
                        String attLocal = pendingAtts.getLocalName(i);
                        attQName = newPrefix + ':' + attLocal;
                        pendingAtts.setQName(i, attQName);
                    }
                }
            }
        }
    }

    public void endItem(boolean last) throws XQueryException {
        if(_writer != null) {
            for(int i = 0; i < _pendingAtts.getLength(); i++) {
                XMLUtils.normalizeAndPrint(_writer, _pendingAtts.getValue(i), true);
            }
            _pendingAtts.clear();
        }
    }

}
