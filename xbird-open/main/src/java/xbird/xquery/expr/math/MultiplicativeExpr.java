/*
 * @(#)$Id: MultiplicativeExpr.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.operator.OverloadedFunction;
import xbird.xquery.operator.math.*;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class MultiplicativeExpr extends ArithmeticExpr {
    private static final long serialVersionUID = -2862919029919882029L;

    public static final String MULTIPLY = "*";
    public static final String DIV = "div";
    public static final String IDIV = "idiv";
    public static final String MOD = "mod";

    private final String operator;
    private OverloadedFunction _op = null;

    public MultiplicativeExpr(String operator, XQExpression leftOperand, XQExpression rightOperand) {
        super(leftOperand, rightOperand);
        this.operator = operator;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        super.staticAnalysis(statEnv);
        OverloadedFunction op = resolveOp();
        op.staticAnalysis(statEnv, _leftOperand, _rightOperand);
        this._op = op;
        this._type = op.getReturnType();
        return this;
    }

    private OverloadedFunction resolveOp() {
        if(MULTIPLY.equals(operator)) {
            return new TimesOp();
        } else if(DIV.equals(operator)) {
            return new DivOp();
        } else if(IDIV.equals(operator)) {
            return new IdivOp();
        } else if(MOD.equals(operator)) {
            return new ModOp();
        } else {
            throw new IllegalStateException("Invalid operator: " + operator);
        }
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        if(!_analyzed) {
            _analyzed = false;
        }
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

}
