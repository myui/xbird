/*
 * @(#)$Id: AdditiveExpr.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.math;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.Evaluable;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.Variable.PreEvaluatedVariable;
import xbird.xquery.meta.*;
import xbird.xquery.operator.OverloadedFunction;
import xbird.xquery.operator.math.MinusOp;
import xbird.xquery.operator.math.PlusOp;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#id-arithmetic
 * @link http://www.w3.org/TR/xquery-semantics/#sec_arithmetic
 */
public final class AdditiveExpr extends ArithmeticExpr {
    private static final long serialVersionUID = 819347526645410348L;

    private final boolean isPlusOp;
    private OverloadedFunction _op = null;

    public AdditiveExpr(boolean isPlusOp, XQExpression leftOperand, XQExpression rightOperand) {
        super(leftOperand, rightOperand);
        this.isPlusOp = isPlusOp;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public boolean isPlus() {
        return this.isPlusOp;
    }

    @Override
    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        super.staticAnalysis(statEnv);
        final OverloadedFunction op = resolveOp();
        op.staticAnalysis(statEnv, _leftOperand, _rightOperand);
        this._op = op;
        if(_leftOperand instanceof Evaluable && _rightOperand instanceof Evaluable) {
            // apply eagar evaluation
            final Sequence<? extends Item> evaluated = eval(null, DynamicContext.DUMMY);
            return new PreEvaluatedVariable(evaluated);
        }
        this._type = op.getReturnType();
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Item v1 = _leftOperand.evalAsItem(contextSeq, dynEnv, true);
        if(v1 == null) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        final Item v2 = _rightOperand.evalAsItem(contextSeq, dynEnv, true);
        if(v2 == null) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        Sequence res = _op.eval(dynEnv, v1, v2);
        return res;
    }

    private OverloadedFunction resolveOp() {
        if(isPlusOp) {
            return new PlusOp();
        } else {
            return new MinusOp();
        }
    }

}
