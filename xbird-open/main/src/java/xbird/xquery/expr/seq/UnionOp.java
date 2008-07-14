/*
 * @(#)$Id: UnionOp.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import xbird.util.iterator.ChainedIterator;
import xbird.xquery.TypeError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AbstractSequence;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.XQNode;
import xbird.xquery.dm.value.sequence.INodeSequence;
import xbird.xquery.dm.value.sequence.ProxyNodeSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.Terminatable;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.Focus;
import xbird.xquery.meta.IFocus;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;

/**
 * Constructs a sequence containing every node that occurs in the values
 * of either $parameter1 or $parameter2, eliminating duplicate nodes.
 * Nodes are returned in document order.
 * <DIV lang="en">
 * op:union($parameter1 as node()*, $parameter2 as node()*) as node()*
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-union
 */
public final class UnionOp extends SequenceOp {
    private static final long serialVersionUID = 1666780272282536765L;

    public UnionOp(XQExpression leftOperand, XQExpression rightOperand) {
        super(leftOperand, rightOperand, TypeRegistry.safeGet("node()*"));
    }

    public String getOperator() {
        return "|";
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
            final Type rtype = _rightOperand.getType();
            if(!TypeUtil.subtypeOf(rtype, _type)) {
                throw new TypeError("Inferred type of left operand is invalid: "
                        + _rightOperand.getType());
            }
            this._type = TypeUtil.union(ltype, rtype);
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        // assume returned sequences are distinct ordered node sequence.
        INodeSequence<XQNode> left = ProxyNodeSequence.wrap(_leftOperand.eval(contextSeq, dynEnv), dynEnv);
        INodeSequence<XQNode> right = ProxyNodeSequence.wrap(_rightOperand.eval(contextSeq, dynEnv), dynEnv);
        // If either operand is the empty sequence, a sequence is returned 
        // containing the nodes in the other operand in document order 
        // after eliminating duplicates.
        final UnionEmuration res = new UnionEmuration(left, right, dynEnv, _type);
        return res;
    }

    @Deprecated
    private static Sequence emurateUnion(Sequence<XQNode> left, Sequence<XQNode> right, DynamicContext dynEnv) {
        final Iterator<XQNode> baseItor = new ChainedIterator<XQNode>(left.iterator(), right.iterator());
        final SortedSet<XQNode> merge = new TreeSet<XQNode>();
        while(baseItor.hasNext()) {
            XQNode n = baseItor.next();
            if(!merge.contains(n)) {
                merge.add(n);
            }
        }
        return merge.isEmpty() ? ValueSequence.emptySequence()
                : new ValueSequence(new ArrayList<XQNode>(merge), dynEnv);
    }

    private static final class UnionEmuration extends AbstractSequence<XQNode>
            implements Terminatable {
        private static final long serialVersionUID = 1885734381391523775L;

        private final Type type;
        private final Sequence<XQNode> left, right;

        private IFocus<XQNode> itor1, itor2;
        private XQNode nextNode1, nextNode2;

        public UnionEmuration(Sequence<XQNode> left, Sequence<XQNode> right, DynamicContext dynEnv, Type type) {
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
            final Focus<XQNode> focus = new Focus<XQNode>(this, _dynEnv);
            focus.addTerminationHook(this);
            return focus;
        }

        public boolean next(IFocus<XQNode> focus) throws XQueryException {
            if(focus.reachedEnd()) {
                return false;
            }
            if(nextNode1 == null) {
                if(nextNode2 == null) {
                    focus.setReachedEnd(true);
                    itor1.closeQuietly();
                    itor2.closeQuietly();
                    return false;
                } else {
                    focus.setContextItem(nextNode2);
                    this.nextNode2 = itor2.hasNext() ? itor2.next() : null;
                    return true;
                }
            } else {
                if(nextNode2 == null) {
                    focus.setContextItem(nextNode1);
                    this.nextNode1 = itor1.hasNext() ? itor1.next() : null;
                    return true;
                } else {// both items exists
                    final int cmp = nextNode1.compareTo(nextNode2);
                    if(cmp < 0) {
                        focus.setContextItem(nextNode1);
                        this.nextNode1 = itor1.hasNext() ? itor1.next() : null;
                        return true;
                    } else if(cmp == 0) {
                        focus.setContextItem(nextNode2);
                        this.nextNode1 = itor1.hasNext() ? itor1.next() : null;
                        this.nextNode2 = itor2.hasNext() ? itor2.next() : null;
                        return true;
                    } else {
                        focus.setContextItem(nextNode2);
                        this.nextNode2 = itor2.hasNext() ? itor2.next() : null;
                        return true;
                    }
                }
            }
        }

        public Type getType() {
            return type;
        }

        public void close() {
            itor1.closeQuietly();
            itor2.closeQuietly();
        }
    }

}
