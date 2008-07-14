/*
 * @(#)$Id: DMNode.java 3749 2008-04-14 23:15:19Z yui $
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

import java.util.*;

import xbird.util.concurrent.counter.ThreadLocalCounter;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.instance.XQueryDataModel;
import xbird.xquery.dm.value.XQNode;
import xbird.xquery.dm.value.sequence.INodeSequence;
import xbird.xquery.dm.value.sequence.NodeSequence;
import xbird.xquery.expr.path.NodeTest;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xpath-datamodel/#accessors
 */
public abstract class DMNode extends XQNode {
    private static final long serialVersionUID = 1L;
    private static final ThreadLocalCounter globalUidCounter = new ThreadLocalCounter(Integer.MIN_VALUE);

    private long _serialNum;

    private int _docid = -1;
    private DMNode _document = null;
    private DMNode _parent = null;
    private DMNode _sibling = null;
    private String _content = null;
    private String _baseUri = null;
    private QualifiedName _typeName = null;

    public DMNode() {//for serialization
        this(-1L);
    }

    protected DMNode(long id) {
        super(id);
        this._serialNum = globalUidCounter.getAndIncrement();
    }

    public int getDocumentId() {
        if(nodeKind() == NodeKind.DOCUMENT) {
            assert (_docid != -1);
            return _docid;
        } else {
            if(_docid != -1) {
                return _docid;
            }
            DMNode doc = getDocumentNode();
            if(doc != null) {
                int docid = doc.getDocumentId();
                this._docid = docid;
                return docid;
            } else {
                return -1;
            }
        }
    }

    public final int getNameCode() {
        QualifiedName name = nodeName();
        if(name == null) {
            return -1;
        }
        return name.identity();
    }

    public String getNamespaceURI() {
        throw new IllegalStateException();
    }

    public void setDocumentId(int docid) {
        this._docid = docid;
    }

    public void setParent(DMNode n) {
        this._parent = n;
    }

    public void setSibling(DMNode n) {
        this._sibling = n;
    }

    public void setContent(String content) {
        this._content = content;
    }

    public void setBaseUri(String uri) {
        _baseUri = uri;
    }

    public String getText() {
        throw new IllegalStateException(getClass().getSimpleName()
                + "#getText() should not be called!");
    }

    public String getContent() {
        return _content;
    }

    public DMNode getDocumentNode() {
        return _document;
    }

    public DMNode firstChild() {
        Iterator<? extends DMNode> citor = children().iterator();
        if(citor.hasNext()) {
            return citor.next();
        } else {
            return null;
        }
    }

    public DMNode lastChild() {
        Iterator<? extends DMNode> citor = children().iterator();
        DMNode last = null;
        if(citor.hasNext()) {
            last = citor.next();
        }
        return last;
    }

    public DMNode nextSibling() {
        return _sibling;
    }

    public DMNode nextSibling(final NodeTest filter) {
        assert (filter != null);
        final DMNode sibling = _sibling;
        if(sibling == null) {
            return null;
        }
        if(filter.accepts(sibling)) {
            return sibling;
        } else {
            return sibling.nextSibling(filter);
        }
    }

    public DMNode previousSibling() {
        return null;
    }

    @Override
    public DMNode following(boolean firstcall) {
        return (DMNode) super.following(firstcall);
    }

    public final XQueryDataModel getDataModel() {
        return XQueryDataModel.INSTANCE;
    }

    //--------------------------------------------
    // Data Model accessors

    /**
     * Returns the attributes of a node as a sequence containing zero or more
     * Attribute Nodes.
     */
    public INodeSequence<DMAttribute> attribute() {
        return NodeSequence.<DMAttribute> emptySequence();
    }

    /**
     * Returns the base URI of a node as a sequence containing zero or one URI reference.
     */
    public String baseUri() {
        if(_baseUri == null) {
            final DMNode p = parent();
            return p == null ? null : p.baseUri();
        } else {
            return _baseUri;
        }
    }

    /**
     * Returns the children of a node as a sequence containing zero or more nodes.
     */
    public INodeSequence<? extends DMNode> children() {
        return NodeSequence.<DMNode> emptySequence();
    }

    /**
     * Returns the absolute URI of the resource from which the Document Node was constructed,
     * if the absolute URI is available. 
     */
    public String documentUri() {
        return null;
    }

    /**
     * Returns true if the node is an XML ID. 
     */
    public boolean isId() {
        return false;
    }

    /**
     * Returns true if the node is an XML IDREF or IDREFS. 
     */
    public boolean isIdrefs() {
        return false;
    }

    /**
     * Returns the dynamic, in-scope namespaces associated with a node as a set of 
     * prefix/URI pairs.
     */
    public String[] namespaceBindings() {
        final INodeSequence<DMNamespace> namespaces = namespaceNodes();
        if(namespaces == null) {
            return new String[0];
        }
        final List<String> res = new ArrayList<String>(4);
        for(DMNamespace ns : namespaceNodes()) {
            res.add(ns.nodeName().getPrefix()); //prefix
            res.add(ns.getContent()); // uri
        }
        return res.toArray(new String[res.size()]);
    }

    /**
     * Returns the dynamic, in-scope namespaces associated with a node as a sequence 
     * containing zero or more Namespace Nodes.
     */
    public INodeSequence<DMNamespace> namespaceNodes() {
        return NodeSequence.<DMNamespace> emptySequence();
    }

    /**
     * Returns true if the node is "nilled".
     */
    public boolean nilled() {
        return false;
    }

    /**
     * Returns the name of the node as a sequence of zero or one xs:QNames.
     */
    public QualifiedName nodeName() {
        return null;
    }

    /**
     * Returns the parent of a node as a sequence containing zero or one nodes.
     */
    public DMNode parent() {
        return _parent;
    }

    public String stringValue() {
        if(_content == null) {
            final StringBuilder buf = new StringBuilder(256);
            for(DMNode n : children()) {
                recStringValue(n, buf);
            }
            final String ret = buf.toString();
            this._content = ret;
            return ret;
        } else {
            return _content;
        }
    }

    /**
     * Returns the name of the schema type of a node as a sequence of zero or one xs:QNames.
     */
    public QualifiedName typeName() {
        return _typeName;
    }

    /**
     * Returns the public identifier of an unparsed external entity declared 
     * in the specified document.
     */
    public String unparsedEntityPublicId(String s) {
        return null;
    }

    /**
     * Returns the system identifier of an unparsed external entity declared 
     * in the specified document.
     */
    public String unparsedEntitySystemId(String s) {
        return null;
    }

    private static final void recStringValue(DMNode node, StringBuilder buf) {
        final byte kind = node.nodeKind();
        if(kind == NodeKind.ELEMENT) {
            for(DMNode n : node.children()) {
                recStringValue(n, buf);
            }
        } else if(kind == NodeKind.TEXT) {
            buf.append(node.stringValue());
        }
    }

    @Override
    public DMNode clone() {
        DMNode n = (DMNode) super.cloneWithoutIncr();
        // Copied element node has new node identity
        n._serialNum = globalUidCounter.getAndIncrement();
        return n;
    }

    @Override
    public long getSerialNumber() {
        return _serialNum;
    }

}
