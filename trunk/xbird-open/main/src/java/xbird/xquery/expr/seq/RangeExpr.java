/*
 * @(#)$Id: RangeExpr.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;

/**
 * 
 * <DIV lang="en">
 * fs:to($firstval as xs:integer?, $lastval as xs:integer?) as xs:integer*.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#sec_fs_to
 */
public final class RangeExpr extends SequenceOp {
    private static final long serialVersionUID = 8826678173081694777L;

    public RangeExpr(XQExpression leftOperand, XQExpression rightOperand) {
        super(leftOperand, rightOperand, TypeRegistry.safeGet("xs:integer*"));
    }

    public String getOperator() {
        return "to";
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
            final Type integerType = TypeRegistry.safeGet("xs:integer?");
            if(!TypeUtil.subtypeOf(_leftOperand.getType(), integerType)) {
                throw new TypeError("Inferred type of left operand is invalid: "
                        + _leftOperand.getType());
            }
            if(!TypeUtil.subtypeOf(_rightOperand.getType(), integerType)) {
                throw new TypeError("Inferred type of left operand is invalid: "
                        + _rightOperand.getType());
            }
        }
        return this;
    }

    public ValueSequence eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence<? extends Item> first = _leftOperand.eval(contextSeq, dynEnv);
        final Sequence<? extends Item> second = _rightOperand.eval(contextSeq, dynEnv);
        final IFocus<? extends Item> firstItor = first.iterator();
        final IFocus<? extends Item> secondItor = second.iterator();
        if(!firstItor.hasNext() || !secondItor.hasNext()) {
            firstItor.closeQuietly();
            secondItor.closeQuietly();
            return ValueSequence.EMPTY_SEQUENCE;
        }
        final Item firstItem = firstItor.next();
        final Item secondItem = secondItor.next();
        assert (!firstItor.hasNext());
        assert (!secondItor.hasNext());
        firstItor.closeQuietly();
        secondItor.closeQuietly();
        final ValueSequence newSeq = new ValueSequence(dynEnv);
        final int from = ((XInteger) firstItem).getNumber().intValue();
        final int to = ((XInteger) secondItem).getNumber().intValue();
        for(int i = from; i <= to; i++) {
            newSeq.addItem(new XInteger(i));
        }
        return newSeq;
    }

}
