/*
 * @(#)$Id: ElementConstructor.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.constructor;

import java.util.*;

import xbird.util.xml.NamespaceBinder;
import xbird.xquery.TypeError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.instance.XQueryDataModel;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.node.*;
import xbird.xquery.dm.value.sequence.CloneNodesSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.node.ElementTest;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#doc-xquery-CompElemConstructor
 * @link http://www.w3.org/TR/xquery-semantics/#sec_comp_elem_constructor
 * @link http://www.w3.org/TR/xquery/#id-attributes
 * @link http://www.w3.org/TR/xquery/#id-content
 */
public final class ElementConstructor extends NameExprConstructor {
    private static final long serialVersionUID = 1533995309945289717L;

    private final QualifiedName name; // avoid null
    private int _attslen;
    private final List<AttributeConstructorBase> attributes;
    private final ArrayList<XQExpression> contents = new ArrayList<XQExpression>(8); // intentional use of ArrayList

    public ElementConstructor(QualifiedName name) {
        this(name, new ArrayList<AttributeConstructorBase>(4));
    }

    public ElementConstructor(QualifiedName name, List<AttributeConstructorBase> atts) {
        this.name = name;
        this._attslen = atts.size();
        this.attributes = atts;
        this._type = name == null ? ElementTest.ANY_ELEMENT : new ElementTest(name);
    }

    public ElementConstructor(XQExpression nameExpr) {
        this(null, Collections.<AttributeConstructorBase> emptyList());
        this._nameExpr = nameExpr;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    //--------------------------------------------
    // getter / setter

    public QualifiedName getName() {
        return this.name;
    }

    public String getElemName() {
        StringBuilder buf = new StringBuilder(32);
        String prefix = name.getPrefix();
        if(prefix != null && prefix.length() > 0) {
            buf.append(prefix);
            buf.append(':');
        }
        buf.append(name.getLocalPart());
        return buf.toString();
    }

    public List<XQExpression> getContents() {
        return this.contents;
    }

    public List<AttributeConstructorBase> getAttributes() {
        return this.attributes;
    }

    public void addContent(XQExpression expr) throws TypeError {
        if(expr == null) {
            throw new IllegalArgumentException();
        }
        if(expr instanceof AttributeConstructorBase) {
            if(!contents.isEmpty()) {
                throw new TypeError("err:XQTY0024", "an attribute node following a node that is not an attribute node");
            }
            AttributeConstructorBase att = (AttributeConstructorBase) expr;
            if(att instanceof NamespaceConstructor) {
                attributes.add(0, att);
                ++_attslen;
            } else if(att instanceof AttributeConstructor) {
                attributes.add(att);
                ++_attslen;
            } else {
                throw new IllegalStateException("Illegal attribute class: "
                        + att.getClass().getName());
            }
        } else {
            contents.add(expr);
        }
    }

    @Deprecated
    public void addContent(TextContent expr) {
        assert (expr != null);
        if(expr == TextContent.TrimedText.INSTANCE) {
            return;
        }
        if(contents != null && !contents.isEmpty()) { // tiny optimization.
            int lastidx = contents.size() - 1;
            XQExpression lastEntry = contents.get(lastidx);
            if(lastEntry instanceof TextContent) {
                TextContent tc = (TextContent) lastEntry;
                if(tc.isCData() && expr.isCData()) {
                    tc.appendText(expr.getValue().toString());
                    return;
                }
            }
        }
        contents.add(expr);
    }

    //--------------------------------------------
    // static analysis / dynamic evaluation

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            if(name == null) {
                if(_nameExpr == null) {
                    reportError("err:XPTY0004", "name expression was null");
                }
                this._nameExpr = _nameExpr.staticAnalysis(statEnv);
            }
            for(AttributeConstructorBase att : attributes) {
                att.staticAnalysis(statEnv);
            }
            boolean stripSpace = statEnv.isStripBoundarySpace();
            int size = contents.size();
            if(size > 0) {
                int from = 0;
                {// trim head
                    XQExpression content = contents.get(0);
                    if(stripSpace && content instanceof TextContent) {
                        TextContent tc = (TextContent) content;
                        TextContent striped = stripBoundaryText(tc);
                        if(striped == null) {
                            contents.remove(0);
                            --size;
                        } else if(tc != striped) {
                            contents.set(0, striped);
                            ++from;
                        }
                    }
                }
                for(int i = from; i < size; i++) {
                    XQExpression c = contents.get(i);
                    if(stripSpace && i == (size - 1) && c instanceof TextContent) {// trim end
                        TextContent tc = (TextContent) c;
                        TextContent striped = stripBoundaryText(tc);
                        if(striped == null) {
                            contents.remove(i);
                        } else if(tc != striped) {
                            contents.set(i, striped);
                        }
                        break;
                    }
                    contents.set(i, c.staticAnalysis(statEnv));
                }
            }
        }
        return this;
    }

    private static TextContent stripBoundaryText(TextContent tc) {
        String c = tc.getValue().stringValue();
        String trimed = c.trim();
        int trimedLen = trimed.length();
        if(trimedLen == 0) {
            return null;
        }
        if(trimedLen != c.length()) {
            return new TextContent(trimed);
        }
        return tc;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        QualifiedName instName = name;
        if(instName == null) {
            instName = processNameExpr(contextSeq, dynEnv);
        }
        DMElement dmelem = XQueryDataModel.createElement(instName);
        boolean hasNamespaceDecl = false;
        NamespaceBinder knownNamespaces = dynEnv.getStaticContext().getStaticalyKnownNamespaces();
        // process attribute/namespace
        for(AttributeConstructorBase att : attributes) {
            if(att instanceof NamespaceConstructor) {
                if(!hasNamespaceDecl) {
                    knownNamespaces.pushContext(); // TODO REVIEWME performance
                    hasNamespaceDecl = true;
                }
                NamespaceConstructor nc = ((NamespaceConstructor) att);
                DMNamespace dmns = nc.eval(contextSeq, dynEnv);
                dmelem.addNamespace(dmns);
                String nsprefix = nc.getPrefix();
                String nsuri = nc.getValue();
                knownNamespaces.declarePrefix(nsprefix, nsuri);
            } else if(att instanceof AttributeConstructor) {
                AttributeConstructor ac = (AttributeConstructor) att;
                DMAttribute dmatt = ac.eval(contextSeq, dynEnv);
                dmelem.addAttribute(dmatt);
            } else {
                throw new IllegalStateException("Expected to be NamespaceConstructor or AttributeConstructor instance, but was.. "
                        + att.getClass().getCanonicalName());
            }
        }
        // process children
        for(XQExpression content : contents) {
            Sequence childs = content.eval(contextSeq, dynEnv);
            if(childs != ValueSequence.EMPTY_SEQUENCE) {
                CloneNodesSequence clonedseq = new CloneNodesSequence(childs, dynEnv);            
                dmelem.addContents(clonedseq, dynEnv);
            }
        }
        if(hasNamespaceDecl) {
            knownNamespaces.popContext();
        }
        return dmelem;
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        QualifiedName instName = name;
        if(instName == null) {
            instName = processNameExpr(contextSeq, dynEnv);
        }
        handler.evStartElement(instName);
        boolean hasNamespaceDecl = false;
        NamespaceBinder knownNamespaces = dynEnv.getStaticContext().getStaticalyKnownNamespaces();

        final int attlen = _attslen;
        for(int i = 0; i < attlen; i++) {
            AttributeConstructorBase att = attributes.get(i);
            if(att instanceof NamespaceConstructor) {
                if(!hasNamespaceDecl) {
                    knownNamespaces.pushContext(); // TODO REVIEWME performance
                    hasNamespaceDecl = true;
                }
                NamespaceConstructor nc = ((NamespaceConstructor) att);
                String nsprefix = nc.getPrefix();
                String nsuri = nc.getValue();
                knownNamespaces.declarePrefix(nsprefix, nsuri);
                handler.evNamespace(nsprefix, nsuri);
            } else {
                att.evalAsEvents(handler, contextSeq, dynEnv);
            }
        }
        final boolean old = handler.setBlankRequired(false);
        final int contentlen = contents.size();
        for(int i = 0; i < contentlen; i++) {
            XQExpression content = contents.get(i);
            content.evalAsEvents(handler, contextSeq, dynEnv);
            handler.setBlankRequired(true);
        }
        handler.setBlankRequired(old);

        if(hasNamespaceDecl) {
            knownNamespaces.popContext();
        }
        handler.evEndElement(instName);
    }

}
