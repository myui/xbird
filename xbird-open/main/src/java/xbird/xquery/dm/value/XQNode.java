/*
 * @(#)$Id: XQNode.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import xbird.util.xml.SAXWriter;
import xbird.xquery.XQRTException;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.AbstractNode;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.instance.DocumentTableModel;
import xbird.xquery.dm.instance.XQueryDataModel;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.dm.value.node.DMNode;
import xbird.xquery.dm.value.sequence.IRandomAccessSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.UntypedAtomicValue;
import xbird.xquery.expr.path.NodeTest;
import xbird.xquery.meta.*;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.node.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class XQNode extends AbstractNode
        implements Cloneable, Item, IRandomAccessSequence<Item> {
    private static final long serialVersionUID = 1L;

    private transient int _hashcode = -1;
    protected transient int _cloned = 0;

    public XQNode(final long id) {
        super(id);
    }

    public long getDocumentOrder() {
        return getDocumentId() + getPosition();
    }

    public abstract int getDocumentId();

    public long getSerialNumber() {
        return -1L;
    }

    public abstract String getNamespaceURI();

    public abstract XQNode firstChild();

    @Override
    public abstract XQNode lastChild();

    public abstract XQNode getDocumentNode();

    public abstract XQNode parent();

    public abstract XQNode nextSibling();

    public abstract XQNode nextSibling(NodeTest filter);

    public abstract XQNode previousSibling();

    public XQNode getRoot() {
        if(isRoot()) {
            return this;
        } else {
            XQNode root = getDocumentNode();
            if(root == null) { // document node may be null.
                XQNode p = this;
                while(true) {
                    XQNode tmp = p.parent();
                    if(tmp != null) {
                        p = tmp;
                    } else {
                        break;
                    }
                }
                assert (p != null);
                return p;
            } else {
                return root;
            }
        }
    }

    @Override
    public XQNode following(boolean firstcall) {
        return (XQNode) super.following(firstcall);
    }

    @Override
    public XQNode preceding(long parentId) {
        return (XQNode) super.preceding(parentId);
    }

    @Override
    public XQNode nextNode() {
        return (XQNode) super.nextNode();
    }

    public NodeType getType() {
        byte kind = nodeKind();
        final NodeType type;
        switch(kind) {
            case NodeKind.DOCUMENT:
                type = DocumentTest.ANY_DOCUMENT;
                break;
            case NodeKind.ELEMENT:
                type = ElementTest.ANY_ELEMENT;
                break;
            case NodeKind.ATTRIBUTE:
                type = AttributeTest.ANY_ATTRIBUTE;
                break;
            case NodeKind.NAMESPACE:
                type = NamespaceTest.ANY_NAMESPACE;
                break;
            case NodeKind.PROCESSING_INSTRUCTION:
                type = PITest.ANY_PI;
                break;
            case NodeKind.COMMENT:
                type = NodeType.COMMENT;
                break;
            case NodeKind.TEXT:
            case NodeKind.CDATA:
                type = NodeType.TEXT;
                break;
            case NodeKind.ANY:
                type = NodeType.ANYNODE;
                break;
            default:
                throw new IllegalStateException("Illegal node kind was detected: " + kind);
        }
        return type;
    }

    /**
     * @link http://www.w3.org/TR/xquery-operators/#func-data
     */
    public Sequence<? extends Item> atomize(DynamicContext dynEnv) {
        return typedValue(); // TODO typed-value handling
    }

    /**
     * The dm:typed-value accessor returns the typed-value of the node
     * as a sequence of zero or more atomic values.
     * 
     * If the node does not have a typed value an error is raised [err:FOTY0012].
     * 
     * @link http://www.w3.org/TR/xpath-datamodel/#dm-typed-value
     */
    public AtomicValue typedValue() {
        return new UntypedAtomicValue(stringValue());
    }

    public abstract QualifiedName nodeName();

    public abstract int getNameCode();

    //--------------------------------------------
    // Sequence Interface

    public Item getContextItem(IFocus<Item> focus) {
        return (focus.getContextPosition() == 1) ? this : null;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean next(IFocus focus) throws XQueryException {
        int curPos = focus.getContextPosition();
        if(curPos == 0) {
            focus.setContextItem(this);
            return true;
        }
        return false;
    }

    public Focus<Item> iterator() {
        return new Focus<Item>(this, DynamicContext.DUMMY);
    }

    @SuppressWarnings("unchecked")
    public <T extends XQNode> T castAs() {
        return (T) this;
    }

    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        SAXWriter saxwriter = new SAXWriter(sw);
        saxwriter.setXMLDeclaration(false);
        SAXSerializer ser = new SAXSerializer(saxwriter, sw);
        try {
            ser.emit(this);
        } catch (XQueryException e) {
            return "failed at " + e.getStackTrace()[1] + ".\n" + e.getMessage();
        }
        return sw.toString();
    }

    //--------------------------------------------
    // Implementation of Comparable interface

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!(obj instanceof XQNode)) {
            return false;
        }
        XQNode n = (XQNode) obj;
        if(n instanceof DMNode) {
            XQNode mydoc = getDocumentNode();
            XQNode trgdoc = n.getDocumentNode();
            if(mydoc != trgdoc) { // document root is not same
                XQNode myroot = getRoot();
                XQNode trgroot = n.getRoot();
                assert (myroot != null && trgroot != null);
                if(myroot != trgroot) { // root is not same
                    return System.identityHashCode(myroot) == System.identityHashCode(trgroot);
                } else {
                    long trgpos = n.getPosition();
                    return _id == trgpos;
                }
            } else {
                long trgpos = n.getPosition();
                return _id == trgpos;
            }
        } else {
            assert (n.getDataModel() instanceof DocumentTableModel);
            long mynodeid = getDocumentOrder();
            long trgnodeid = n.getDocumentOrder();
            return mynodeid == trgnodeid;
        }
    }

    public int compareTo(Item node) {
        if(!(node instanceof XQNode)) {
            throw new XQRTException("err:XPTY0004", "XQNode#compareTo(Object) accepts only XQNode instance as an argument, but was.. "
                    + node.getClass().getCanonicalName());
        }
        if(this == node) {
            return 0;
        }
        XQNode n = (XQNode) node;
        if(n instanceof DMNode) {
            XQNode mydoc = getDocumentNode();
            XQNode trgdoc = n.getDocumentNode();
            if(mydoc != trgdoc) { // document root is not same
                XQNode myroot = getRoot();
                XQNode trgroot = n.getRoot();
                if(myroot != trgroot) { // root is not same
                    return System.identityHashCode(myroot) > System.identityHashCode(trgroot) ? 1
                            : -1;
                } else {
                    long trgpos = n.getPosition();
                    if(_id == trgpos) {
                        return 0;
                    }
                    return _id > trgpos ? 1 : -1;
                }
            } else {
                long trgpos = n.getPosition();
                if(_id == trgpos) {
                    long mySerial = getSerialNumber();
                    long trgSerial = n.getSerialNumber();
                    return mySerial > trgSerial ? 1 : -1;
                }
                return _id > trgpos ? 1 : -1;
            }
        } else {
            assert (n.getDataModel() instanceof DocumentTableModel);
            long mynodeid = getDocumentOrder();
            long trgnodeid = n.getDocumentOrder();
            return mynodeid == trgnodeid ? 0 : (mynodeid > trgnodeid ? 1 : -1);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends DMNode> T asDMNode() {
        if(this instanceof DMNode) {
            return (T) this;
        }
        final T proxy = XQueryDataModel.<T> createProxy(this);
        return proxy;
    }

    @Override
    public int hashCode() {
        if(_hashcode == -1) {
            this._hashcode = Double.valueOf(getDocumentOrder()).hashCode();
        }
        return _hashcode;
    }

    //--------------------------------------------
    // Data Model accessors

    /**
     * Returns the base URI of a node as a sequence containing zero or one URI reference.
     */
    public abstract String baseUri();

    //--------------------------------------------
    // IRandomAccessSequence interface implimentation

    public Item getItem(int index) {
        if(index == 0) {
            return this;
        } else {
            throw new IllegalArgumentException("Index out of range.. " + index);
        }
    }

    public List<Item> getItems() {
        List<Item> ret = new ArrayList<Item>(1);
        ret.add(this);
        return ret;
    }

    public int size() {
        return 1;
    }

    public Sequence subSequence(int fromIndex, int toIndex) {
        if(fromIndex == 0 && toIndex < 1) {
            throw new IllegalArgumentException("Index out of range.");
        }
        return (toIndex == 0) ? ValueSequence.EMPTY_SEQUENCE : this;
    }

    public List<Item> materialize() {
        return getItems();
    }

    @Override
    public XQNode clone() {
        if(_cloned++ == 0) {
            return this;
        }
        try {
            return (XQNode) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
    }

    protected final XQNode cloneWithoutIncr() {
        try {
            return (XQNode) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
    }

}
