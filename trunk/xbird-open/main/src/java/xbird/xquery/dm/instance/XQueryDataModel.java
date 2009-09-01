/*
 * @(#)$Id: XQueryDataModel.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.instance;

import java.util.Iterator;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.instance.DocumentTableModel.DTMDocument;
import xbird.xquery.dm.instance.DocumentTableModel.DTMElement;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.XQNode;
import xbird.xquery.dm.value.node.*;
import xbird.xquery.dm.value.sequence.INodeSequence;
import xbird.xquery.dm.value.sequence.ProxyNodeSequence;
import xbird.xquery.expr.path.NodeTest;
import xbird.xquery.expr.path.axis.AttributeStep;
import xbird.xquery.expr.path.axis.ChildStep;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.xs.UntypedAtomicType;

/**
 * An utility class for XQuery Data Model.
 * <DIV lang="en">
 * This class is state-less.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class XQueryDataModel extends DataModel {
    private static final long serialVersionUID = -2299908044994456382L;

    public static final XQueryDataModel INSTANCE = new XQueryDataModel();

    private XQueryDataModel() {}

    public static DMDocument createDocument() {
        return new DMDocument();
    }

    public static DMDocument createDocument(String baseUri, Sequence contents)
            throws XQueryException {
        return new DMDocument(baseUri, contents);
    }

    public static DMElement createElement(QualifiedName name) {
        return new DMElement(name);
    }

    public static DMAttribute createAttribute(QualifiedName name, String value) {
        return new DMAttribute(name, value);
    }

    public static DMNamespace createNamespace(QualifiedName name, String content) {
        return new DMNamespace(name, content);
    }

    public static DMText createText(String content) {
        return new DMText(content);
    }

    public static DMComment createComment(String content) {
        return new DMComment(content);
    }

    public static DMProcessingInstruction createProcessingInstruction(String target, String content) {
        return new DMProcessingInstruction(target, content);
    }

    public static <T extends DMNode> T createProxy(XQNode n) {
        assert (n != null);
        if(n instanceof DMNode) {
            return (T) n;
        }
        final DMNode proxy;
        switch(n.nodeKind()) {
            case NodeKind.DOCUMENT:
                proxy = new DMDocumentProxy(n);
                break;
            case NodeKind.ELEMENT:
                proxy = new DMElementProxy(n);
                break;
            case NodeKind.TEXT:
            case NodeKind.CDATA:
                proxy = new DMTextProxy(n);
                break;
            case NodeKind.ATTRIBUTE:
            case NodeKind.NAMESPACE:
            case NodeKind.PROCESSING_INSTRUCTION:
            case NodeKind.COMMENT:
                proxy = new DMNodeProxy(n);
                break;
            default:
                throw new IllegalStateException("Illegal node kind: "
                        + NodeKind.resolveName(n.nodeKind()));
        }
        return (T) proxy;
    }

    /**
     * Traverses tree in depth first order and reports events.
     * 
     * @link http://www.w3.org/TR/xquery/#doc-xquery-CompDocConstructor
     * @link http://www.w3.org/TR/xquery/#id-content
     */
    public void export(XQNode rawNode, XQEventReceiver receiver) throws XQueryException {
        assert (rawNode instanceof DMNode);
        DMNode node = (DMNode) rawNode;
        if(node instanceof DMNodeProxy) {
            DMNodeProxy proxy = (DMNodeProxy) node;
            XQNode delegated = proxy.getDelegated();
            DataModel dm = delegated.getDataModel();
            dm.export(delegated, receiver);
            return;
        }
        switch(node.nodeKind()) {
            case NodeKind.DOCUMENT:
                receiver.evStartDocument();
                final INodeSequence<? extends DMNode> children = node.children();
                final IFocus<? extends DMNode> childItor = children.iterator();
                while(childItor.hasNext()) {
                    DMNode child = childItor.next();
                    export(child, receiver);
                    // [Note] document node constructor may have more than 1 children. permitted by the spec.                    
                }
                childItor.closeQuietly();
                receiver.evEndDocument();
                break;
            case NodeKind.ELEMENT:
                final DMElement e = node.<DMElement> castAs();
                final QualifiedName elemName = e.nodeName();
                receiver.evStartElement(elemName);
                for(DMNamespace ns : e.namespaceNodes()) {
                    QualifiedName qname = ns.nodeName();
                    final String localName;
                    if(qname != null) {
                        localName = qname.getLocalPart();
                    } else {
                        localName = "";
                    }
                    String value = ns.getContent();
                    receiver.evNamespace(localName, value);
                }
                for(DMAttribute att : e.attribute()) {
                    receiver.evAttribute(att.nodeName(), att.getContent());
                }
                final INodeSequence<DMNode> elemChilds = e.children();
                for(DMNode child : elemChilds) {
                    export(child, receiver);
                }
                receiver.evEndElement(elemName);
                break;
            case NodeKind.ATTRIBUTE:
                final DMAttribute att = node.<DMAttribute> castAs();
                receiver.evAttribute(att.nodeName(), att.getContent());
                break;
            case NodeKind.NAMESPACE:
                final DMNamespace ns = node.<DMNamespace> castAs();
                receiver.evNamespace(ns.nodeName().getPrefix(), ns.getNamespaceURI());
                break;
            case NodeKind.TEXT:
                final DMText txt = node.<DMText> castAs();
                receiver.evText(txt.getContent());
                break;
            case NodeKind.COMMENT:
                final DMComment comment = node.<DMComment> castAs();
                receiver.evComment(comment.getContent());
                break;
            case NodeKind.PROCESSING_INSTRUCTION:
                final DMProcessingInstruction pi = node.<DMProcessingInstruction> castAs();
                receiver.evProcessingInstruction(pi.getTarget(), pi.getContent());
                break;
            default:
                throw new IllegalStateException("Illegal node type: " + node.nodeKind());
        }
    }

    public static class DMNodeProxy extends DMNode {
        private static final long serialVersionUID = -8985101380986863717L;

        protected final XQNode _delegate;

        DMNodeProxy(XQNode n) {
            super(n.getPosition());
            if(n instanceof DMNode) {
                throw new IllegalStateException("Unexpected class: " + n.getClass());
            }
            this._delegate = n;
            if(n.nodeKind() != nodeKind()) {
                throw new IllegalArgumentException("Illegal node kind as document node: "
                        + NodeKind.resolveName(nodeKind()));
            }
        }

        public XQNode getDelegated() {
            return _delegate;
        }

        @Override
        public int getDocumentId() {
            return super.getDocumentId();
        }

        @Override
        public DMNode getDocumentNode() {
            XQNode doc = _delegate.getDocumentNode();
            return createProxy(doc);
        }

        @Override
        public DMNode parent() {
            DMNode dmParent = super.parent();
            if(dmParent == null) {
                XQNode p = _delegate.parent();
                if(p == null) {
                    return null;
                }
                dmParent = createProxy(p);
                setParent(dmParent);
            }
            return dmParent;
        }

        @Override
        public DMNode nextSibling() {
            DMNode dmsib = super.nextSibling();
            if(dmsib == null) {
                XQNode sib = _delegate.nextSibling();
                if(sib == null) {
                    return null;
                }
                dmsib = createProxy(sib);
                setSibling(dmsib);
            }
            return dmsib;
        }

        @Override
        public DMNode nextSibling(NodeTest filter) {
            DMNode dmsib = super.nextSibling(filter);
            if(dmsib == null) {
                XQNode sib = _delegate.nextSibling(filter);
                if(sib == null) {
                    return null;
                }
                dmsib = createProxy(sib);
                setSibling(dmsib);
            }
            return dmsib;
        }

        @Override
        public String getContent() {
            String content = super.getContent();
            if(content == null) {
                content = _delegate.getContent();
                setContent(content);
            }
            return content;
        }

        @Override
        public String baseUri() {
            String dmbaseuri = super.baseUri();
            if(dmbaseuri == null) {
                dmbaseuri = _delegate.baseUri();
                setBaseUri(dmbaseuri);
            }
            return dmbaseuri;
        }

        @Override
        public QualifiedName typeName() {
            return super.typeName(); // TODO schema
        }

        public byte nodeKind() {
            return _delegate.nodeKind();
        }

        @Override
        public String getNamespaceURI() {
            QualifiedName nodename = nodeName();
            if(nodename != null) {
                return nodename.getNamespaceURI();
            }
            return super.getNamespaceURI();
        }

        @Override
        public QualifiedName nodeName() {
            return _delegate.nodeName();
        }

        @Override
        public String stringValue() {
            return _delegate.stringValue();
        }

    }

    private static class DMElementProxy extends DMNodeProxy {
        private static final long serialVersionUID = 2361216232536259834L;

        DMElementProxy(XQNode n) {
            super(n);
        }

        @Override
        public INodeSequence<DMNode> children() {
            final INodeSequence children = new ChildStep.ChildEmurationSequence(_delegate, NodeTest.ANYNODE, DynamicContext.DUMMY);
            return new DMNodeSequenceEmuration<DMNode>(children);
        }

        @Override
        public QualifiedName nodeName() {
            return _delegate.nodeName();
        }

        @Override
        public INodeSequence<DMAttribute> attribute() {
            DTMElement e = (DTMElement) _delegate;
            final INodeSequence attributes = new AttributeStep.AttributeEmurationSequence(e, NodeTest.ANYNODE, DynamicContext.DUMMY);
            return new DMNodeSequenceEmuration<DMAttribute>(attributes);
        }

        @Override
        public INodeSequence<DMNamespace> namespaceNodes() {
            DTMElement e = (DTMElement) _delegate;
            final INodeSequence namespaces = new AttributeStep.NamespaceEmurationSequence(e, NodeTest.ANYNODE, DynamicContext.DUMMY);
            return new DMNodeSequenceEmuration<DMNamespace>(namespaces);
        }

    }

    private static final class DMDocumentProxy extends DMElementProxy {
        private static final long serialVersionUID = -8252314705717558769L;

        DMDocumentProxy(XQNode n) {
            super(n);
        }

        @Override
        public String documentUri() {
            DTMDocument doc = (DTMDocument) _delegate.getDocumentNode();
            return doc == null ? null : doc.documentUri();
        }
    }

    private static final class DMTextProxy extends DMNodeProxy {
        private static final long serialVersionUID = 3958438047231353016L;

        DMTextProxy(XQNode n) {
            super(n);
        }

        @Override
        public QualifiedName typeName() {
            return UntypedAtomicType.UNTYPED_ATOMIC.getTypeName();
        }
    }

    private static final class DMNodeSequenceEmuration<T extends DMNode> extends
            ProxyNodeSequence<T> {
        private static final long serialVersionUID = 6851807234943577040L;

        public DMNodeSequenceEmuration(INodeSequence<? extends XQNode> _delegate) {
            super(_delegate, DynamicContext.DUMMY);
        }

        public boolean next(IFocus focus) throws XQueryException {
            Iterator<? extends XQNode> delItor = focus.getBaseFocus();
            if(delItor.hasNext()) {
                XQNode delItem = delItor.next();
                DMNode dmnode = delItem.asDMNode();
                focus.setContextItem(dmnode);
                return true;
            }
            return false;
        }

    }

}
