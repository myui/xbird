/*
 * @(#)$Id: IntersectOp.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.seq;

import xbird.xquery.TypeError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.INodeSequence;
import xbird.xquery.dm.value.sequence.ProxyNodeSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;

/**
 * Constructs a sequence containing every node that occurs in the values of
 * both $parameter1 and $parameter2, eliminating duplicate nodes.
 * Nodes are returned in document order.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-intersect
 */
public final class IntersectOp extends SequenceOp {
    private static final long serialVersionUID = 2274830235210807853L;

    public IntersectOp(XQExpression leftOperand, XQExpression rightOperand) {
        super(leftOperand, rightOperand, TypeRegistry.safeGet("node()*"));
    }

    public String getOperator() {
        return "intersect";
    }

    @Override
    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    @Override
    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            this._leftOperand = _leftOperand.staticAnalysis(statEnv);
            this._rightOperand = _rightOperand.staticAnalysis(statEnv);
            final Type ltype = _leftOperand.getType();
            if(!TypeUtil.subtypeOf(ltype, _type)) {
                throw new TypeError("Inferred type of left operand is invalid: "
                        + _leftOperand.getType());
            }
            if(!TypeUtil.subtypeOf(_rightOperand.getType(), _type)) {
                throw new TypeError("Inferred type of left operand is invalid: "
                        + _rightOperand.getType());
            }
            this._type = ltype;
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        // assume returned sequences are distinct ordered node sequence.
        INodeSequence<XQNode> left = ProxyNodeSequence.wrap(_leftOperand.eval(contextSeq, dynEnv), dynEnv);
        INodeSequence<XQNode> right = ProxyNodeSequence.wrap(_rightOperand.eval(contextSeq, dynEnv), dynEnv);
        final IntersectEmuration res = new IntersectEmuration(left, right, dynEnv, _type);
        return res;
    }

    private static final class IntersectEmuration extends AbstractSequence<XQNode> {
        private static final long serialVersionUID = 1885734381391523775L;
        private final Type type;
        private final Sequence<XQNode> left, right;

        private IFocus<XQNode> itor1, itor2;
        private XQNode nextNode1, nextNode2;

        public IntersectEmuration(Sequence<XQNode> left, Sequence<XQNode> right, DynamicContext dynEnv, Type type) {
            super(dynEnv);
            this.left = left;
            this.right = right;
            this.type = type;
        }

        @Override
        public IFocus<XQNode> iterator() {
            this.itor1 = left.iterator();
            this.itor2 = right.iterator();
            this.nextNode1 = itor1.hasNext() ? itor1.next() : null;
            this.nextNode2 = itor2.hasNext() ? itor2.next() : null;
            return new Focus<XQNode>(this, _dynEnv);
        }

        public boolean next(IFocus<XQNode> focus) throws XQueryException {
            while(true) {
                if(nextNode1 == null || nextNode2 == null) {
                    itor1.closeQuietly();
                    itor2.closeQuietly();
                    return false;
                } else {
                    int cmp = nextNode1.compareTo(nextNode2);
                    if(cmp == 0) {
                        focus.setContextItem(nextNode1);
                        this.nextNode1 = itor1.hasNext() ? itor1.next() : null;
                        this.nextNode2 = itor2.hasNext() ? itor2.next() : null;
                        return true;
                    } else if(cmp < 0) {// skip left item
                        this.nextNode1 = itor1.hasNext() ? itor1.next() : null;
                    } else {// skip right item
                        this.nextNode2 = itor2.hasNext() ? itor2.next() : null;
                    }
                }
            }
        }

        public Type getType() {
            return type;
        }
    }
}
