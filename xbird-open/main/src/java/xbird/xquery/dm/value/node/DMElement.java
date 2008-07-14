/*
 * @(#)$Id: DMElement.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value.node;

import javax.xml.XMLConstants;

import xbird.xquery.TypeError;
import xbird.xquery.XQRTException;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.instance.XQueryDataModel;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.*;
import xbird.xquery.dm.value.xsi.UntypedAtomicValue;
import xbird.xquery.expr.path.NodeTest;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.schema.ComplexType;
import xbird.xquery.type.schema.ComplexType.ContentType;
import xbird.xquery.type.xs.Untyped;
import xbird.xquery.type.xs.UntypedAtomicType;

/**
 * Element Nodes encapsulate XML elements.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xpath-datamodel/#ElementNode
 */
public class DMElement extends DMNode {
    private static final long serialVersionUID = 529515965770768972L;

    private final QualifiedName _name;
    private final NodeSequence<DMNode> _children = new LinkedNodeSequence<DMNode>(DynamicContext.DUMMY);
    private final AttrNodeSequence<DMAttribute> _attributes = new AttrNodeSequence<DMAttribute>(DynamicContext.DUMMY);
    private final AttrNodeSequence<DMNamespace> _namespaces = new AttrNodeSequence<DMNamespace>(DynamicContext.DUMMY);
    private transient AtomicValue _typedValue = null;

    public DMElement() {//for serialization
        super();
        this._name = null;
    }

    public DMElement(QualifiedName name) {
        super();
        this._name = name;
    }

    protected DMElement(int id, QualifiedName name) {
        super(id);
        this._name = name;
    }

    public void addAttribute(DMAttribute... atts) {
        for(DMAttribute att : atts) {
            _attributes.addItem(att);
        }
    }

    public void addNamespace(DMNamespace... namespaces) {
        for(DMNamespace ns : namespaces) {
            _namespaces.addItem(ns);
        }
    }

    public void addChild(DMNode... nodes) {
        for(DMNode node : nodes) {
            node.setParent(this);
            _children.addItem(node);
        }
        if(_children.size() > 1) {
            this._typedValue = null;
        }
    }

    public void addContents(Sequence<? extends Item> contents, DynamicContext dynEnv)
            throws TypeError {
        if(contents == null) {
            return;
        }
        boolean prevAtom = false, prevAttr = true;
        DMNode prevTxt = null;
        int i = 0;
        final IFocus<? extends Item> itor = contents.iterator();
        for(Item content : itor) {
            i++;
            if(content instanceof XQNode) {
                DMNode dmnode = ((XQNode) content).asDMNode();
                byte nodekind = dmnode.nodeKind();
                if(nodekind == NodeKind.ATTRIBUTE) {
                    if(!prevAttr) {
                        itor.closeQuietly();
                        // If the content sequence contains an attribute node
                        // following a node that is not an attribute node, a type error is raised [err:XQTY0024].
                        throw new TypeError("err:XQTY0024", "An attribute node in the content sequence is not allowed: "
                                + content.getType());
                    }
                    DMAttribute attr = new DMAttribute(dmnode.getPosition(), dmnode.nodeName(), dmnode.getContent());
                    addAttribute(attr);
                    prevTxt = null;
                } else {
                    prevAttr = false;
                    if(nodekind == NodeKind.TEXT) {
                        String curText = dmnode.getContent();
                        assert (curText != null) : dmnode;
                        if(curText.length() == 0) {
                            continue;
                        }
                        if(prevTxt != null) {
                            String newc = prevTxt.getContent() + curText;
                            prevTxt.setContent(newc);
                        } else {
                            addChild(dmnode);
                            prevTxt = dmnode;
                        }
                    } else {
                        if(nodekind == NodeKind.DOCUMENT) {
                            // If the content sequence contains a document node, the document node 
                            // is replaced in the content sequence by its children.
                            final INodeSequence<? extends DMNode> childs = ((DMNode) dmnode).children();
                            for(DMNode n : childs) {
                                addChild(n);
                            }
                            prevTxt = null;
                        } else {
                            addChild(dmnode);
                        }
                        prevTxt = null;
                    }
                }
                prevAtom = false;
            } else {
                if(prevTxt != null) {
                    final String newc;
                    if(prevAtom) {
                        newc = prevTxt.getContent() + ' ' + content.stringValue();
                    } else {
                        newc = prevTxt.getContent() + content.stringValue();
                    }
                    prevTxt.setContent(newc);
                } else {
                    String sv = content.stringValue();
                    if(sv.length() == 0) {
                        continue;
                    }
                    DMText txt = XQueryDataModel.createText(sv);
                    addChild(txt);
                    prevTxt = txt;
                }
                prevAtom = true;
                prevAttr = false;
            }
        }
        itor.closeQuietly();
    }

    //--------------------------------------------
    // Data Model accessors

    @Override
    public AttrNodeSequence<DMAttribute> attribute() {
        return _attributes;
    }

    public final AttrNodeSequence<DMAttribute> attribute(NodeTest filter, DynamicContext dynEnv) {
        if(filter == null) {
            return _attributes;
        }
        final AttrNodeSequence<DMAttribute> atts = new AttrNodeSequence<DMAttribute>(dynEnv);
        for(DMAttribute att : _attributes) {
            if(filter.accepts(att)) {
                atts.addItem(att);
            }
        }
        return atts;
    }

    public DMAttribute getAttribute(String nsuri, String name) {
        assert (nsuri != null && name != null);
        for(DMAttribute att : _attributes) {
            if(QNameUtil.isSame(att.nodeName(), nsuri, name)) {
                return att;
            }
        }
        return null;
    }

    @Override
    public INodeSequence<DMNode> children() {
        return _children;
    }

    @Override
    public INodeSequence<DMNamespace> namespaceNodes() {
        return _namespaces;
    }

    public byte nodeKind() {
        return NodeKind.ELEMENT;
    }

    @Override
    public String getContent() {
        throw new IllegalStateException(getClass().getSimpleName()
                + "#getContent() should not be called.");
    }

    @Override
    public final QualifiedName nodeName() {
        return _name;
    }

    @Override
    public QualifiedName typeName() {
        return UntypedAtomicType.UNTYPED_ATOMIC.getTypeName();
    }

    @Override
    public AtomicValue typedValue() {
        if(_typedValue != null) {
            return _typedValue;
        }
        Type t = TypeRegistry.safeGet(typeName());
        assert (t != null);
        Type mixed = new ComplexType(ContentType.MixedContent);
        if(TypeUtil.subtypeOf(t, Untyped.UNTYPED) || TypeUtil.subtypeOf(t, mixed)) {
            // - If the element is of type xdt:untyped, 
            //  its typed-value is its dm:string-value as an xdt:untypedAtomic.
            // - If the element has a complex type with mixed content (including xs:anyType), 
            //  its typed-value is its dm:string-value as an xdt:untypedAtomic.
            return new UntypedAtomicValue(stringValue());
        } else if(t instanceof ComplexType) {
            ContentType ct = ((ComplexType) t).getContentType();
            if(ct.isType(ContentType.EmptyContent)) {
                // - If the element has a complex type with empty content, its typed-value 
                //  is the empty sequence.           
                return ValueSequence.<AtomicValue> emptySequence();
            } else if(ct.isType(ContentType.SimpleContent)) {
                // - If the element has a simple type or a complex type with simple content, 
                //  the result is a sequence of zero or more atomic values. 
                return new UntypedAtomicValue(stringValue().toString());
            }
        }
        //  The typed-value of such an element is undefined. 
        //  Attempting to access this property with the dm:typed-value accessor always raises 
        //  an error.
        throw new XQRTException("The typed-value of such an element is undefined: " + t);
    }

    @Override
    public String getNamespaceURI() {
        return _name.getNamespaceURI();
    }

    @Override
    public String baseUri() {
        String baseuri = super.baseUri();
        if(baseuri != null) {
            return baseuri;
        }
        for(DMAttribute att : _attributes) {
            QualifiedName attname = att.nodeName();
            if(XMLConstants.XML_NS_URI.equals(attname.getNamespaceURI())
                    && "base".equals(attname.getLocalPart())) {
                String uri = att.getContent();
                setBaseUri(uri);
                return uri;
            }
        }
        for(XQNode p = parent(); p != null; p = p.parent()) {
            String uri = p.baseUri();
            if(uri != null) {
                setBaseUri(uri);
                return uri;
            }
        }
        return null;
    }
}
