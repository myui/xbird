/*
 * @(#)$Id: TreatExpr.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.types;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;

/**
 * The expression "Expr treat as SequenceType", can be used to modify the static type of 
 * its operand without changing its value.
 * <DIV lang="en">
 * Unlike cast, however, treat does not change the dynamic type or value of its operand.
 * Instead, the purpose of treat is to ensure that an expression has an expected dynamic type
 * at evaluation time.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#id-treat
 * @link http://www.w3.org/TR/xquery-semantics/#sec_treat
 */
public class TreatExpr extends CastableExpr {
    private static final long serialVersionUID = 5829295024501998470L;

    public TreatExpr(XQExpression expr, Type seqType) {
        super(expr, seqType);
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
            this._inputExpr = _inputExpr.staticAnalysis(statEnv);
            final Type exprType = _inputExpr.getType();
            if(TypeUtil.subtypeOf(exprType, _targetType)) {
                return _inputExpr;
            }
            this._type = _targetType;
        }
        return this;
    }

    @Override
    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence<? extends Item> v = _inputExpr.eval(contextSeq, dynEnv);
        if(!TypeUtil.instanceOf(v, _targetType)) {
            reportError("err:XPDY0050", "Expected type `" + _targetType
                    + "` does not accepts the actual value type: " + v.getType());
        }
        return v;
    }

}
