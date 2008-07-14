/*
 * @(#)$Id: CastableExpr.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.StaticError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.BooleanType;
import xbird.xquery.type.xs.NOTATIONType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#doc-xquery-CastableExpr
 * @link http://www.w3.org/TR/xquery/#doc-xquery-SingleType
 */
public class CastableExpr extends AbstractXQExpression {
    private static final long serialVersionUID = 5090688169583974420L;

    protected XQExpression _inputExpr;
    protected final Type _targetType; /* restricted to SingleType */

    public CastableExpr(XQExpression expr, Type type) {
        this._inputExpr = expr;
        this._targetType = type;
    }

    public XQExpression getExpression() {
        return this._inputExpr;
    }

    public Type getTargetType() {
        return this._targetType;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            final Type primeType = _targetType.prime();
            if(primeType == NOTATIONType.NOTATION || primeType == AtomicType.ANY_ATOMIC_TYPE) {
                throw new StaticError("err:XPST0080", "Illegal target type: " + _targetType);
            }
            this._inputExpr = _inputExpr.staticAnalysis(statEnv);
            this._type = BooleanType.BOOLEAN;
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence<? extends Item> input = _inputExpr.eval(contextSeq, dynEnv).atomize(dynEnv);
        final IFocus<? extends Item> itor = input.iterator();
        if(itor.hasNext()) {
            final Item it = itor.next();
            if(itor.hasNext()) {
                itor.closeQuietly();
                return BooleanValue.FALSE;
            }
            itor.closeQuietly();
            if(it instanceof AtomicValue) {
                try {
                    final AtomicType tt = (AtomicType) _targetType.prime();
                    final AtomicValue casteed = ((AtomicValue) it).castAs(tt, dynEnv);
                } catch (XQueryException e) {
                    return BooleanValue.FALSE;
                }
                return BooleanValue.TRUE;
            } else {
                return BooleanValue.FALSE;
            }
        } else {
            itor.closeQuietly();
            return TypeUtil.isOptional(_targetType) ? BooleanValue.TRUE : BooleanValue.FALSE;
        }
    }

}
