/*
 * @(#)$Id$
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
package xbird.xquery.expr.opt;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @deprecated
 */
public final class TypeCheckingExpr extends AbstractXQExpression {
    private static final long serialVersionUID = 922309388158514919L;

    private XQExpression _expr;

    public TypeCheckingExpr(XQExpression expr, Type checkedType) {
        this._expr = expr;
        this._type = checkedType;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(_expr, ctxt);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            XQExpression analyzed = _expr.staticAnalysis(statEnv);
            Type inferredType = analyzed.getType();
            if(!TypeUtil.subtypeOf(inferredType, _type)) {
                reportError("err:XPTY0004", "Declared type '" + _type
                        + "' does not accept inferred type '" + inferredType + '\'');
            }
            this._expr = analyzed;
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        return _expr.eval(contextSeq, dynEnv);
    }

}
