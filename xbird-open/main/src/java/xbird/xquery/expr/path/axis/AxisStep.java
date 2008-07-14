/*
 * @(#)$Id: AxisStep.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.path.axis;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AbstractSequence;
import xbird.xquery.dm.value.XQNode;
import xbird.xquery.dm.value.sequence.INodeSequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.path.NodeTest;
import xbird.xquery.expr.path.StepExpr;
import xbird.xquery.meta.*;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;
import xbird.xquery.type.node.NodeType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class AxisStep extends AbstractXQExpression implements StepExpr {
    private static final long serialVersionUID = 1L;

    // forward step
    public static final int CHILD = 1;
    public static final int DESC = 2;
    public static final int ATTR = 3;
    public static final int SELF = 4;
    public static final int DESC_OR_SELF = 5;
    public static final int FOLLOWING_SIBLING = 6;
    public static final int FOLLOWING = 7;
    // reverse step
    public static final int PARENT = 8;
    public static final int ANCESTOR = 9;
    public static final int PRECEDING_SIBLING = 10;
    public static final int PRECEDING = 11;
    public static final int ANCESTOR_OR_SELF = 12;

    public static final String CHILD_EXPR = "child::";
    public static final String DESC_EXPR = "descendant::";
    public static final String ATTR_EXPR = "@";
    public static final String SELF_EXPR = "self::";
    public static final String DESC_OR_SELF_EXPR = "descendant-ot-self::";
    public static final String FOLLOWING_SIBLING_EXPR = "following-sibling::";
    public static final String FOLLOWING_EXPR = "following::";
    public static final String PARENT_EXPR = "parent::";
    public static final String ANCESTOR_EXPR = "ancestor::";
    public static final String PRECEDING_SIBLING_EXPR = "preceding-sibling::";
    public static final String PRECEDING_EXPR = "preceding::";
    public static final String ANCESTOR_OR_SELF_EXPR = "ancestor-or-self::";
    public static final String ROOT_DESC_EXPR = "descendant-or-self::node()";

    private final int axisKind;
    protected final NodeTest _nodeTest;

    public AxisStep(int kind, NodeTest test) {
        assert (test != null);
        this.axisKind = kind;
        this._nodeTest = test;
        this._type = NodeType.ANYNODE;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public boolean isNonDownwardAxis() {
        return false;
    }
    
    public int getAxisKind() {
        return this.axisKind;
    }

    public NodeTest getNodeTest() {
        return this._nodeTest; // FIXME may return null
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(64);
        switch(axisKind) {
            case CHILD:
                buf.append(CHILD_EXPR);
                break;
            case DESC:
                buf.append(DESC_EXPR);
                break;
            case ATTR:
                buf.append(ATTR_EXPR);
                break;
            case SELF:
                buf.append(SELF_EXPR);
                break;
            case DESC_OR_SELF:
                buf.append(DESC_OR_SELF_EXPR);
                break;
            case FOLLOWING_SIBLING:
                buf.append(FOLLOWING_SIBLING_EXPR);
                break;
            case FOLLOWING:
                buf.append(FOLLOWING_EXPR);
                break;
            case PARENT:
                buf.append(PARENT_EXPR);
                break;
            case ANCESTOR:
                buf.append(ANCESTOR_EXPR);
                break;
            case PRECEDING_SIBLING:
                buf.append(PRECEDING_SIBLING_EXPR);
                break;
            case PRECEDING:
                buf.append(PRECEDING_EXPR);
                break;
            case ANCESTOR_OR_SELF:
                buf.append(ANCESTOR_OR_SELF_EXPR);
                break;
            default:
                throw new IllegalStateException();
        }
        buf.append(_nodeTest);
        return buf.toString();
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            _nodeTest.staticAnalysis(statEnv);
            this._type = _nodeTest.getType();
        }
        return this;
    }

    protected abstract static class AxisEmurationSequence extends AbstractSequence<XQNode>
            implements INodeSequence<XQNode> {
        private static final long serialVersionUID = 1L;

        private final XQNode _initNode;

        public AxisEmurationSequence(XQNode initNode, DynamicContext dynEnv) {
            super(dynEnv);
            this._initNode = initNode;
        }

        public Type getType() {
            return NodeType.ANYNODE;
        }

        @Override
        public Focus<XQNode> iterator() {
            final Focus<XQNode> focus = new Focus<XQNode>(this, _dynEnv);
            if(_initNode != null) {
                focus.setContextItem(_initNode);
            }
            return focus;
        }

    }

}
