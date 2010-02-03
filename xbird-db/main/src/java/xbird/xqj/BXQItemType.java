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
package xbird.xqj;

import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQItemType;

import xbird.xquery.dm.NodeKind;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.*;
import xbird.xquery.type.node.*;
import xbird.xquery.type.schema.SchemaType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#datatypes
 */
public final class BXQItemType implements XQItemType {

    private final Type type_; // TODO REVIEWME should be Type instead of ItemType?

    public BXQItemType(Type type) {
        if(type == null) {
            throw new IllegalArgumentException();
        }
        this.type_ = type;
    }
    
    public Type getInternalType() {
        return type_;
    }

    public int getBaseType() throws XQException {
        return type_.getXQJBaseType();
    }

    public int getItemKind() {
        final Type type = type_;
        if(type instanceof AtomicType) {
            return XQITEMKIND_ATOMIC;
        } else if(type instanceof NodeType) {
            NodeType nodetype = (NodeType) type;
            final byte nodekind = nodetype.getNodeKind();
            switch(nodekind) {
                case NodeKind.DOCUMENT:
                    DocumentTest doctest = (DocumentTest) nodetype;
                    final ItemType docNodeType = doctest.getNodeType();
                    if(docNodeType == null) {
                        return XQITEMKIND_DOCUMENT;
                    } else if(docNodeType instanceof SchemaType) {
                        return XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT;
                    } else {
                        return XQITEMKIND_DOCUMENT_ELEMENT;
                    }
                case NodeKind.ELEMENT:
                    return (nodetype instanceof SchemaType) ? XQITEMKIND_SCHEMA_ELEMENT
                            : XQITEMKIND_ELEMENT;
                case NodeKind.ATTRIBUTE:
                case NodeKind.NAMESPACE:
                    return (nodetype instanceof SchemaType) ? XQITEMKIND_SCHEMA_ATTRIBUTE
                            : XQITEMKIND_ATTRIBUTE;
                case NodeKind.TEXT:
                case NodeKind.CDATA:
                    return XQITEMKIND_TEXT;
                case NodeKind.COMMENT:
                    return XQITEMKIND_COMMENT;
                case NodeKind.PROCESSING_INSTRUCTION:
                    return XQITEMKIND_PI;
                case NodeKind.ANY:
                default:
                    return XQITEMKIND_NODE;
            }
        }
        return XQITEMKIND_ITEM;
    }

    public int getItemOccurrence() {
        return OCC_EXACTLY_ONE;
    }

    public QName getNodeName() throws XQException {
        Type type = type_;
        if(type instanceof DocumentTest) {
            type = ((DocumentTest) type).getNodeType();
        }
        if(type instanceof NodeType) {
            NodeType nodetype = (NodeType) type;
            final byte nodekind = nodetype.getNodeKind();
            switch(nodekind) {
                case NodeKind.ELEMENT:
                case NodeKind.ATTRIBUTE:
                case NodeKind.DOCUMENT:
                    QualifiedName qname = nodetype.getNodeName();
                    return QualifiedName.toJavaxQName(qname);
                default:
                    break;
            }
        }
        throw new XQException("This method should not be called with the ItemType: " + type_, "err:XQJxxxx");
    }

    public String getPIName() throws XQException {
        final Type type = type_;
        if(type instanceof PITest) {
            PITest pi = (PITest) type;
            return pi.getName();
        }
        throw new XQException("This method should not be called with the ItemType: " + type_, "err:XQJxxxx");
    }

    public URI getSchemaURI() {
        return null; // always returns null
    }

    public QName getTypeName() throws XQException {
        Type type = type_;
        if(type instanceof AtomicType) {
            QualifiedName qname = ((AtomicType) type).getTypeName();
            assert (qname != null);
            return QualifiedName.toJavaxQName(qname);
        }
        if(type instanceof DocumentTest) {
            type = ((DocumentTest) type).getNodeType();
        }
        if(type instanceof NodeType) {
            NodeType nodetype = (NodeType) type;
            final byte nodekind = nodetype.getNodeKind();
            switch(nodekind) {
                case NodeKind.ELEMENT:
                    ElementTest elementTest = (ElementTest) nodetype;
                    final QualifiedName elemType = elementTest.getTypeName();
                    if(elemType == null) {
                        return new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xs");
                    } else {
                        return QualifiedName.toJavaxQName(elemType);
                    }
                case NodeKind.ATTRIBUTE:
                    AttributeTest attrTest = (AttributeTest) nodetype;
                    final QualifiedName attrType = attrTest.getTypeName();
                    if(attrType == null) {
                        return new QName("http://www.w3.org/2001/XMLSchema", "anyType", "xs");
                    } else {
                        return QualifiedName.toJavaxQName(attrType);
                    }
                default:
                    break;
            }
        }
        throw new XQException("This method should not be called with the ItemType: " + type_, "err:XQJxxxx");
    }

    public boolean isAnonymousType() {
        return false; // TODO
    }

    public boolean isElementNillable() {
        final Type type = type_;
        if(type instanceof ElementTest) {
            return ((ElementTest) type).isNillable();
        }
        return false;
    }

    public XQItemType getItemType() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof XQItemType)) {
            return false;
        }
        // getItemKind() is equal
        XQItemType objtype = (XQItemType) obj;
        int kind = getItemKind(), thatkind = objtype.getItemKind();
        if(kind != thatkind) {
            return false;
        }
        // if getBaseType() is supported for the item kind, it must be equal
        try {
            final int basetype = getBaseType(), thatBaseType = objtype.getBaseType();
            if(basetype != thatBaseType) {
                return false;
            }
        } catch (XQException e) {
        }
        // if getNodeName() is supported for the item kind, it must be equal
        try {
            final QName nodeName = getNodeName(), thatNodeName = objtype.getNodeName();
            if(nodeName == null) {
                if(thatNodeName != null) {
                    return false;
                }
            } else if(!nodeName.equals(thatNodeName)) {
                return false;
            }
        } catch (XQException e) {
        }
        // getSchemaURI() is equal
        final URI uri = getSchemaURI(), thatUri = objtype.getSchemaURI();
        if(uri == null) {
            if(thatUri != null) {
                return false;
            }
        } else if(!uri.equals(thatUri)) {
            return false;
        }
        // if getTypeName() is supported for the item kind, it must be equal
        try {
            final QName typeName = getTypeName(), thatTypeName = objtype.getTypeName();
            if(typeName == null) {
                if(thatTypeName != null) {
                    return false;
                }
            } else if(!typeName.equals(thatTypeName)) {
                return false;
            }
        } catch (XQException e) {
        }
        // isAnonymousType() is equal
        final boolean isAnon = isAnonymousType(), thatIsAnon = objtype.isAnonymousType();
        if(isAnon != thatIsAnon) {
            return false;
        }
        // isElementNillable() is equal
        final boolean isNillable = isElementNillable(), thatIsNillable = objtype.isElementNillable();
        if(isNillable != thatIsNillable) {
            return false;
        }
        // if getPIName() is supported for the item kind, it must be equal   
        try {
            final String pi = getPIName(), thatPi = objtype.getPIName();
            if(pi == null) {
                if(thatPi != null) {
                    return false;
                }
            } else if(!pi.equals(thatPi)) {
                return false;
            }
        } catch (XQException e) {
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = getItemKind();
        final URI schemaUri = getSchemaURI();
        if(schemaUri != null) {
            hashCode = 31 * hashCode + schemaUri.hashCode();
        }
        //if getBaseType() is supported for the item kind
        try {
            hashCode = 31 * hashCode + getBaseType();
        } catch (XQException e) {
        }
        //if this.getNodeName () is supported for the item kind and this.getNodeName() != null
        try {
            hashCode = 31 * hashCode + getNodeName().hashCode();
        } catch (XQException e) {
        }
        //if this.getTypeName () is supported for the item kind
        try {
            hashCode = 31 * hashCode + getTypeName().hashCode();
        } catch (XQException e) {
        }
        //if this.getPIName () is supported for the item kind and this.getPIName () != null
        try {
            hashCode = 31 * hashCode + getPIName().hashCode();
        } catch (XQException e) {
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return type_.toString();
    }

}
