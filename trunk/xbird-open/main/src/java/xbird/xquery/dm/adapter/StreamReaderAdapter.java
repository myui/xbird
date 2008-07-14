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
package xbird.xquery.dm.adapter;

import java.util.NoSuchElementException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import xbird.util.xml.*;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.instance.DocumentTableModel.DTMAttribute;
import xbird.xquery.dm.instance.DocumentTableModel.DTMElement;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.node.*;
import xbird.xquery.dm.value.sequence.AttrNodeSequence;
import xbird.xquery.meta.IFocus;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class StreamReaderAdapter extends XMLStreamReaderBase {

    private/* final */Item item_;
    private/* final */EventState currentEvent_;

    private boolean reachedEnd_ = false;

    private final NamespaceBinder namespaces_ = new NamespaceBinder();

    public StreamReaderAdapter(Item item) {
        final EventState ev = new EventState();
        this.currentEvent_ = ev;
        if(item instanceof XQNode) {
            final XQNode node = (XQNode) item;
            final byte kind = node.nodeKind();
            final int type = resolveType(kind, false);
            ev.setEvent(type, node);
            this.item_ = null;
        } else {
            this.item_ = item;
        }
    }

    public void close() throws XMLStreamException {
        this.reachedEnd_ = true;
        // clean up
        this.item_ = null;
        this.currentEvent_ = null;
        namespaces_.clean();
    }

    public int getAttributeCount() {
        checkConditionForAttribute();
        final XQNode node = currentEvent_.getNode();
        if(node instanceof DTMElement) {
            return ((DTMElement) node).getAttributesCount();
        } else if(node instanceof DMElement) {
            return ((DMElement) node).attribute().size();
        }
        throw new IllegalStateException("Unexpected node class: " + node.getClass().getName());
    }

    public QName getAttributeName(int index) {
        checkConditionForAttribute();
        final XQNode node = currentEvent_.getNode();
        final QualifiedName attname;
        if(node instanceof DTMElement) {
            final DTMAttribute att = ((DTMElement) node).attribute(index);
            if(att == null) {
                throw new IndexOutOfBoundsException(); // REVIEWME what's needed by the spec ?
            }
            attname = att.nodeName();
        } else if(node instanceof DMElement) {
            AttrNodeSequence<DMAttribute> attributes = ((DMElement) node).attribute();
            final int attrSize = attributes.size();
            if(index >= attrSize) {
                throw new IndexOutOfBoundsException(); // REVIEWME what's needed by the spec ?
            }
            DMAttribute att = attributes.getItem(index);
            attname = att.nodeName();
        } else {
            throw new IllegalStateException("Unexpected node class: " + node.getClass().getName());
        }
        assert (attname != null);
        return QualifiedName.toJavaxQName(attname);
    }

    public String getAttributeValue(int index) {
        checkConditionForAttribute();
        final XQNode node = currentEvent_.getNode();
        if(node instanceof DTMElement) {
            final DTMAttribute att = ((DTMElement) node).attribute(index);
            if(att == null) {
                throw new IndexOutOfBoundsException(); // REVIEWME what's needed by the spec ?
            }
            return att.getContent();
        } else if(node instanceof DMElement) {
            AttrNodeSequence<DMAttribute> attributes = ((DMElement) node).attribute();
            final int attrSize = attributes.size();
            if(index >= attrSize) {
                throw new IndexOutOfBoundsException(); // REVIEWME what's needed by the spec ?
            }
            DMAttribute att = attributes.getItem(index);
            return att.getContent();
        } else {
            throw new IllegalStateException("Unexpected node class: " + node.getClass().getName());
        }
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        if(localName == null) {
            throw new IllegalArgumentException();
        }
        checkConditionForAttribute();
        final XQNode node = currentEvent_.getNode();
        final DMNode proxy = node.asDMNode();
        for(DMAttribute att : proxy.attribute()) {
            final QualifiedName attname = att.nodeName();
            assert (attname != null);
            if(localName.equals(attname.getLocalPart())) {
                if(namespaceURI == null || namespaceURI.equals(attname.getNamespaceURI())) {
                    return att.getContent();
                }
            }
        }
        return null;
    }

    @Override
    protected final void checkConditionForAttribute() {
        final int type = getEventType();
        if(type != START_ELEMENT) {
            throw new IllegalStreamStateException("Unexpected event: "
                    + XMLStreamUtils.resolveTypeName(type), getLocation());
        }
    }

    public int getEventType() {
        return currentEvent_.getEventType();
    }

    public String getLocalName() {
        checkConditionForElement();
        return currentEvent_.getLocalName();
    }

    public QName getName() {
        checkConditionForElement();
        return currentEvent_.getName();
    }

    public NamespaceContext getNamespaceContext() {
        checkConditionForNamespace();
        return namespaces_;
    }

    public int getNamespaceCount() {
        checkConditionForNamespace();
        return namespaces_.getNamespaceCount();
    }

    public String getNamespacePrefix(int index) {
        checkConditionForNamespace();
        return namespaces_.getPrefix(index);
    }

    public String getNamespaceURI(int index) {
        checkConditionForNamespace();
        return namespaces_.getNamespaceURI(index);
    }

    public String getPIData() {
        final int type = getEventType();
        if(type != PROCESSING_INSTRUCTION) {
            throw new IllegalStreamStateException("Unexpected event: "
                    + XMLStreamUtils.resolveTypeName(type), getLocation());
        }
        return currentEvent_.getValue();
    }

    public String getPITarget() {
        final int type = getEventType();
        if(type != PROCESSING_INSTRUCTION) {
            throw new IllegalStreamStateException("Unexpected event: "
                    + XMLStreamUtils.resolveTypeName(type), getLocation());
        }
        return currentEvent_.getLocalName();
    }

    public String getText() {
        checkConditionForText();
        return currentEvent_.getValue();
    }

    public boolean hasNext() throws XMLStreamException {
        return !reachedEnd_;
    }

    public int next() throws XMLStreamException {
        if(reachedEnd_) {
            throw new NoSuchElementException("Already reached to the END of the document");
        }
        return handleNext();
    }

    private int handleNext() throws XMLStreamException {
        assert (!reachedEnd_);
        final EventState currentEvent = currentEvent_;
        final XQNode curnode = currentEvent.getNode();
        if(curnode == null) {
            final IFocus<Item> itor = item_.iterator();
            try {
                if(itor.hasNext()) {
                    final Item it = itor.next();
                    if(it instanceof AtomicValue) {
                        final String text = it.stringValue();
                        currentEvent.setEvent(CHARACTERS, new DMText(text));
                        this.reachedEnd_ = true;
                        return CHARACTERS;
                    } else if(it instanceof XQNode) {
                        final XQNode node = (XQNode) it;
                        final int ev = resolveType(node.nodeKind(), false);
                        currentEvent.setEvent(ev, node);
                        return traverse(currentEvent, node);
                    }
                }
            } finally {
                itor.closeQuietly();
                this.item_ = null;
            }
            throw new IllegalStateException();
        } else {
            return traverse(currentEvent, curnode);
        }
    }

    private int traverse(final EventState currentEvent, final XQNode node) {
        assert (!reachedEnd_);
        final TraverseAction action = currentEvent.getNextAction();
        if(action == TraverseAction.SELF) {
            final int ev = handleEvent(currentEvent, node, TraverseAction.FIRST_CHILD, false);
            if(currentEvent.isTraversalDone()) {
                this.reachedEnd_ = true;
            }
            return ev;
        }
        assert (!currentEvent.isTraversalDone());
        if(action == TraverseAction.FIRST_CHILD) {
            if(currentEvent.isClosed()) {
                currentEvent.setNextAction(TraverseAction.NEXT_SIBLING);
                // to nextsib
            } else {
                final XQNode firstChild = node.firstChild();
                if(firstChild == null) {
                    currentEvent.setNextAction(TraverseAction.NEXT_SIBLING);
                    // to nextsib
                } else {
                    currentEvent.incrDepth();
                    return handleEvent(currentEvent, firstChild, TraverseAction.SELF, false);
                }
            }
        }
        if(action == TraverseAction.NEXT_SIBLING) {
            final XQNode nextsib = node.nextSibling();
            if(nextsib == null) {
                currentEvent.setNextAction(TraverseAction.PARENT);
                // to parent
            } else {
                return handleEvent(currentEvent, nextsib, TraverseAction.SELF, false);
            }
        }
        if(action == TraverseAction.PARENT) {
            final XQNode parent = node.parent();
            if(parent == null) {
                throw new IllegalStateException();
            } else {
                currentEvent.decrDepth();
                final int ev = handleEvent(currentEvent, parent, TraverseAction.NEXT_SIBLING, true);
                if(currentEvent.isTraversalDone()) {
                    this.reachedEnd_ = true;
                }
                return ev;
            }
        }
        throw new IllegalStateException();
    }

    private int handleEvent(final EventState currentEvent, final XQNode node, final TraverseAction nextAction, final boolean ascending) {
        final byte kind = node.nodeKind();
        final int ev = resolveType(kind, ascending);
        if(ev == START_ELEMENT) {
            final NamespaceBinder namespaces = namespaces_;
            namespaces.pushContext();
            final DMNode elem = node.asDMNode();
            for(DMNamespace ns : elem.namespaceNodes()) {
                final QualifiedName qname = ns.nodeName();
                final String prefix;
                if(qname != null) {
                    prefix = qname.getLocalPart();
                } else {
                    prefix = "";
                }
                final String uri = ns.getContent();
                namespaces.declarePrefix(prefix, uri);
            }
        } else if(ev == END_ELEMENT) {
            namespaces_.popContext();
        }
        currentEvent.setEvent(ev, node);
        currentEvent.setNextAction(nextAction);
        return ev;
    }

    /**
     * Resolves the StAX event type with the internal node type.
     */
    private static int resolveType(final byte type, final boolean closeTag) {
        switch(type) {
            case NodeKind.DOCUMENT:
                return closeTag ? END_DOCUMENT : START_DOCUMENT;
            case NodeKind.ELEMENT:
                return closeTag ? END_ELEMENT : START_ELEMENT;
            case NodeKind.ATTRIBUTE:
                return ATTRIBUTE;
            case NodeKind.NAMESPACE:
                return NAMESPACE;
            case NodeKind.TEXT:
                return CHARACTERS;
            case NodeKind.CDATA:
                return CDATA;
            case NodeKind.COMMENT:
                return COMMENT;
            case NodeKind.PROCESSING_INSTRUCTION:
                return PROCESSING_INSTRUCTION;
            default:
                throw new IllegalArgumentException("Invalid node type was specified: "
                        + NodeKind.resolveName(type));
        }
    }

    private enum TraverseAction {
        SELF, FIRST_CHILD, NEXT_SIBLING, PARENT
    }

    private static final class EventState {

        private int depth = 0;
        private int event;
        private XQNode node;
        private TraverseAction nextAction = TraverseAction.SELF;

        EventState() {}

        void setEvent(int event, XQNode node) {
            this.event = event;
            this.node = node;
        }

        void incrDepth() {
            ++depth;
        }

        void decrDepth() {
            --depth;
        }

        int getDepth() {
            return depth;
        }

        int getEventType() {
            return event;
        }

        XQNode getNode() {
            return node;
        }

        boolean isTraversalDone() {
            return depth == 0 && isClosed();
        }

        boolean isClosed() {
            return (event != START_ELEMENT) && (event != START_DOCUMENT);
        }

        QName getName() {
            final QualifiedName name = node.nodeName();
            return (name == null) ? null : QualifiedName.toJavaxQName(name);
        }

        String getLocalName() {
            final QualifiedName name = node.nodeName();
            return (name == null) ? null : name.getLocalPart();
        }

        String getValue() {
            return node.getContent();
        }

        TraverseAction getNextAction() {
            return nextAction;
        }

        void setNextAction(TraverseAction nextAction) {
            this.nextAction = nextAction;
        }
    }
}
