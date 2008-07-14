/*
 * @(#)$Id: QNameUtil.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.misc;

import javax.xml.XMLConstants;

import xbird.util.xml.NamespaceBinder;
import xbird.xquery.XQueryException;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class QNameUtil {

    private QNameUtil() {}

    public static QualifiedName parse(String qname, NamespaceBinder namespaceContext, String defaultNamespace)
            throws XQueryException {
        assert (qname != null) : "Given QName is null.";
        assert (namespaceContext != null) : "Given namespaceContext is null.";
        final String localPart;
        final String prefix;
        final String namespaceURI;
        final int pos = qname.indexOf(':');
        if(pos == -1) { // has no prefix
            localPart = qname;
            prefix = "";
            namespaceURI = (defaultNamespace == null) ? namespaceContext.getNamespaceURI(prefix)
                    : defaultNamespace;
        } else { // has prefix
            prefix = qname.substring(0, pos);
            localPart = qname.substring(pos + 1);
            if(prefix.length() == 0) {
                throw new IllegalArgumentException("Invalid QName expression: " + qname);
            }
            namespaceURI = namespaceContext.getNamespaceURI(prefix);
            if(namespaceURI == null) {
                throw new XQueryException("err:XPST0081", "invalid namespace prefix: " + prefix);
            }
        }
        return QNameTable.instantiate(namespaceURI, localPart, prefix);
    }

    public static QualifiedName parse(String qname, NamespaceBinder namespaces) {
        try {
            return parse(qname, namespaces, null);
        } catch (XQueryException e) {
            throw new IllegalArgumentException("Unable to resolve QName: " + qname);
        }
    }

    public static QualifiedName parse(String qname, String nsuri) {
        assert (qname != null);
        final int pos = qname.indexOf(':');
        if(pos == -1) {
            return QNameTable.instantiate(nsuri, qname);
        } else {
            final String prefix = qname.substring(0, pos);
            final String lpart = qname.substring(pos + 1);
            return QNameTable.instantiate(nsuri, lpart, prefix);
        }
    }

    public static QualifiedName resolveNSAttrName(String prefix, NamespaceBinder namespaceContext, String defaultNamespace)
            throws XQueryException {
        assert (namespaceContext != null) : "Given namespaceContext is null.";
        if(prefix == null || prefix.length() == 0) {
            return QNameTable.instantiate(defaultNamespace == null ? XMLConstants.DEFAULT_NS_PREFIX
                    : defaultNamespace, XMLConstants.XMLNS_ATTRIBUTE);
        } else {
            return QNameTable.instantiate(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix, XMLConstants.XMLNS_ATTRIBUTE);
        }
    }

    public static String toNamespaceDecl(QualifiedName name) {
        final String prefix = name.getPrefix();
        final String lpart = name.getLocalPart();
        assert (lpart != null);
        // xmlns:xxx || xmlns
        if((prefix == null || prefix.length() == 0) && lpart.equals("xmlns")) {
            return "xmlns";
        } else if(prefix.equals("xmlns")) {
            return "xmlns:" + lpart;
        } else {
            throw new IllegalStateException("Invalid QName as namespace name: " + name);
        }
    }

    public static String toLexicalForm(QualifiedName name) {
        assert (name != null);
        final String prefix = name.getPrefix();
        final String lpart = name.getLocalPart();
        if(prefix == null || prefix.length() == 0) {
            return lpart;
        } else {
            return prefix + ':' + lpart;
        }
    }

    public static boolean isSame(QualifiedName qname, String nsuri, String name) {
        assert (qname != null && name != null);
        if(nsuri == null) {
            nsuri = XMLConstants.NULL_NS_URI;
        }
        if(nsuri.equals(qname.getNamespaceURI()) && name.equals(qname.getLocalPart())) {
            return true;
        }
        return false;
    }

}