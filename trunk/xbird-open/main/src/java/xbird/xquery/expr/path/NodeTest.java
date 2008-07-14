/*
 * @(#)$Id: NodeTest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.path;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.path.axis.AxisStep;
import xbird.xquery.meta.*;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.node.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#node-tests
 */
public final class NodeTest extends AbstractXQExpression {
    private static final long serialVersionUID = -368827245366853165L;

    public static final String ANY = "*";
    public static final NodeTest ANYNODE = new NodeTest(NodeType.ANYNODE);

    private final NodeType _kindTest;
    private QualifiedName _nodeName = null; // null if "*"

    /**
     * Represents principal node kind `*`.
     * <ul>
     * <li>For the attribute axis, the principal node kind is attribute.</li>
     * <li>For all other axes, the principal node kind is element.</li>
     * </ul>
     */
    public NodeTest(int axisKind) {
        this(axisKind == AxisStep.ATTR ? NodeType.ATTRIBUTE : NodeType.ELEMENT);
    }

    public NodeTest(int axisKind, QualifiedName nodeName) {
        final NodeType kindTest = (axisKind == AxisStep.ATTR) ? new AttributeTest(nodeName)
                : new ElementTest(nodeName);
        this._nodeName = nodeName;
        this._kindTest = kindTest;
        this._type = kindTest;
    }

    public NodeTest(NodeType kindTest) {
        if(kindTest == null) {
            throw new IllegalArgumentException();
        }
        this._nodeName = kindTest.getNodeName();
        this._kindTest = kindTest;
        this._type = kindTest;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public NodeType getKindTest() {
        return this._kindTest;
    }

    public QualifiedName getNodeName() {
        return this._nodeName;
    }

    @Override
    public String toString() {
        if(_nodeName != null) {
            final StringBuilder buf = new StringBuilder(64);
            final String pref = _nodeName.getPrefix();
            if(pref != null && pref.length() > 0) {
                buf.append(pref);
                buf.append(':');
            }
            buf.append(_nodeName.getLocalPart());
            return buf.toString();
        } else {
            return _kindTest.toString();
        }
    }

    public int[] toQuery(RewriteInfo info, boolean isAttrStep) {
        return _kindTest.toQuery(info, isAttrStep);
    }

    public boolean accepts(final XQNode node) {
        if(node == null) {
            return false;
        }
        if(this == ANYNODE) {
            return true; // node()
        }
        final byte kind = node.nodeKind();
        if(!_kindTest.acccept(kind)) {
            return false;
        }
        final QualifiedName nodeName = _nodeName;
        if(nodeName != null) {
            final int myNameCode = nodeName.identity();
            final int nameCode = node.getNameCode();
            if(myNameCode != -1 && nameCode != -1) {
                return QNameTable.nameEquals(myNameCode, nameCode);
            } else {
                final QualifiedName name = node.nodeName();
                final String lpart = name.getLocalPart();
                final String mylpart = nodeName.getLocalPart();
                if(mylpart != lpart && ANY != mylpart) {
                    return false;
                }
                final String myprefix = nodeName.getPrefix();
                final String uri = name.getNamespaceURI();
                final String myuri = nodeName.getNamespaceURI();
                if(myuri != uri && ANY != myprefix) {
                    return false;
                }
            }
        }
        return true;
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        throw new IllegalStateException("NodeTest should not be evaluated.");
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof NodeTest)) {
            return false;
        }
        NodeTest casted = (NodeTest) obj;
        if(_kindTest != null) {
            if(!_kindTest.equals(casted.getKindTest())) {
                return false;
            }
        } else {
            if(casted.getKindTest() != null) {
                return false;
            }
        }
        if(_nodeName != null) {
            if(!_nodeName.equals(casted.getNodeName())) {
                return false;
            }
        } else {
            if(casted.getNodeName() != null) {
                return false;
            }
        }
        return true;
    }

}
