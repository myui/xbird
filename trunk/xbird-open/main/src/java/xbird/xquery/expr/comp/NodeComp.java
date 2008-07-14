/*
 * @(#)$Id: NodeComp.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.comp;

import xbird.xquery.TypeError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.INodeSequence;
import xbird.xquery.dm.value.sequence.ProxyNodeSequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.node.NodeType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NodeComp extends ComparisonOp {
    private static final long serialVersionUID = 4312032383333203541L;

    public enum Operator {
        IS("is", 0), PRECEDES("<<", -1), FOLLOWS(">>", 1);

        public final String sgn;
        private final int align;

        Operator(String sgn, int align) {
            this.sgn = sgn;
            this.align = align;
        }

        public boolean acceptSgn(int cmp) {
            final int cmpsgn = (cmp == 0) ? 0 : (cmp > 0) ? 1 : -1;
            return align == cmpsgn;
        }

        @Override
        public String toString() {
            return sgn;
        }
    }

    private Operator operator;

    public NodeComp(Operator operator, XQExpression leftOperand) {
        super(leftOperand);
        this.operator = operator;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public String getOperator() {
        return operator.toString();
    }

    @Override
    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            XQExpression analyzed = super.staticAnalysis(statEnv);
            if(analyzed != this) {
                return analyzed;
            }
            this._analyzed = true;
            // TODO REVIEWME is corrent to compare with prime type?
            if(!TypeUtil.subtypeOf(_leftOperand.getType().prime(), NodeType.ANYNODE)) {
                reportError(TypeError.TYPE_ERROR_CODE, "left operand expected to be "
                        + NodeType.ANYNODE + ", but was " + _leftOperand.getType());
            }
            if(!TypeUtil.subtypeOf(_rightOperand.getType().prime(), NodeType.ANYNODE)) {
                reportError(TypeError.TYPE_ERROR_CODE, "right operand expected to be "
                        + NodeType.ANYNODE + ", but was " + _rightOperand.getType());
            }
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final INodeSequence<XQNode> v1 = ProxyNodeSequence.wrap(_leftOperand.eval(contextSeq, dynEnv), dynEnv);
        final IFocus<XQNode> v1itor = v1.iterator();
        if(!v1itor.hasNext()) {
            v1itor.closeQuietly();
            return BooleanValue.FALSE;
        }
        final INodeSequence<XQNode> v2 = ProxyNodeSequence.wrap(_rightOperand.eval(contextSeq, dynEnv), dynEnv);
        final IFocus<XQNode> v2itor = v2.iterator();
        if(!v2itor.hasNext()) {
            v1itor.closeQuietly();
            v2itor.closeQuietly();
            return BooleanValue.FALSE;
        }
        final XQNode n1 = v1itor.next();
        final XQNode n2 = v2itor.next();
        if(v1itor.hasNext()) {
            v1itor.closeQuietly();
            v2itor.closeQuietly();
            return BooleanValue.FALSE;
        }
        if(v2itor.hasNext()) {
            v1itor.closeQuietly();
            v2itor.closeQuietly();
            return BooleanValue.FALSE;
        }
        v1itor.closeQuietly();
        v2itor.closeQuietly();
        final int cmp = n1.compareTo(n2);
        final boolean res = operator.acceptSgn(cmp);
        return res ? BooleanValue.TRUE : BooleanValue.FALSE;
    }

    //--------------------------------------------
    // helper

    public void switchOperand() {
        final Operator op;
        switch(operator) {
            case PRECEDES:
                op = Operator.FOLLOWS;
                break;
            case FOLLOWS:
                op = Operator.PRECEDES;
                break;
            default:
                op = operator;
        }
        final XQExpression tmp = _leftOperand;
        this._leftOperand = _rightOperand;
        this._rightOperand = tmp;
        this.operator = op;
    }

}
