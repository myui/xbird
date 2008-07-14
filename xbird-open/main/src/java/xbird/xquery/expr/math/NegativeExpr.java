/*
 * @(#)$Id: NegativeExpr.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.operator.OverloadedFunction;
import xbird.xquery.operator.math.UnaryMinus;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NegativeExpr extends AbstractXQExpression {
    private static final long serialVersionUID = -2769785866970377773L;

    private XQExpression _expr;
    private OverloadedFunction _op = null;

    public NegativeExpr(XQExpression expr) {
        this._expr = expr;
    }

    public XQExpression getExpression() {
        return _expr;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            this._expr = _expr.staticAnalysis(statEnv);
            final UnaryMinus op = new UnaryMinus();
            op.staticAnalysis(statEnv, _expr);
            this._op = op;
            this._type = op.getReturnType();
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Item v1 = _expr.evalAsItem(contextSeq, dynEnv, true);
        if(v1 == null) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        Sequence res = _op.eval(dynEnv, v1);
        return res;
    }

}
