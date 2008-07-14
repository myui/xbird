/*
 * @(#)$Id: CastExpr.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.NOTATIONType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#doc-xquery-CastExpr
 * @link http://www.w3.org/TR/xquery-semantics/#sec_cast
 */
public final class CastExpr extends CastableExpr {
    private static final long serialVersionUID = 1654964871858996739L;

    public CastExpr(XQExpression expr, Type type) {
        super(expr, type);
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
            final Type targetType = _targetType;
            final Type primeType = targetType.prime();
            if(primeType == NOTATIONType.NOTATION || primeType == AtomicType.ANY_ATOMIC_TYPE) {
                throw new StaticError("err:XPST0080", "Illegal target type: " + _targetType);
            }
            this._inputExpr = _inputExpr.staticAnalysis(statEnv);
            final Type inputType = _inputExpr.getType();
            if(inputType == targetType) {
                return _inputExpr;
            }
            this._type = targetType;
        }
        return this;
    }

    @Override
    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence<? extends Item> input = _inputExpr.eval(contextSeq, dynEnv);
        final Sequence<? extends Item> atomized = input.atomize(dynEnv);
        final IFocus<? extends Item> itor = atomized.iterator();
        if(itor.hasNext()) {
            final Item it = itor.next();
            if(itor.hasNext()) {
                itor.closeQuietly();
                reportError("err:FORG0001", "Result of atomization is a sequence of more than one items");
            }
            itor.closeQuietly();
            if(it instanceof AtomicValue) {
                final AtomicType tt = (AtomicType) _targetType.prime();
                final AtomicValue retv = ((AtomicValue) it).castAs(tt, dynEnv);
                //retv.restrictType(tt);
                return retv;
            } else {
                reportError("err:XPTY0004", "Result item of atomization was not AtomicValue");
            }
        } else {
            itor.closeQuietly();
            if(TypeUtil.isOptional(_targetType)) {
                return ValueSequence.EMPTY_SEQUENCE;
            } else {
                reportError("err:XPTY0004", "Failed casting empty sequence to " + _targetType);
            }
        }
        throw new IllegalStateException();
    }

}
