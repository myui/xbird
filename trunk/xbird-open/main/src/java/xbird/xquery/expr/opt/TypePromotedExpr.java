/*
 * @(#)$Id: TypePromotedExpr.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.expr.func.DirectFunctionCall;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;

/**
 * This expression used for parameters that function inlining is applied.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class TypePromotedExpr extends AbstractXQExpression {
    private static final long serialVersionUID = 3629056761669022924L;

    private XQExpression promotedExpr;
    private final Type promoteTo;

    public TypePromotedExpr(XQExpression promotedExpr, Type promoteTo) {
        this(promotedExpr, promoteTo, false);
    }

    public TypePromotedExpr(XQExpression promotedExpr, Type promoteTo, boolean analyzed) {
        this.promotedExpr = promotedExpr;
        this.promoteTo = promoteTo;
        this._type = promoteTo;
        this._analyzed = true;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression getPromotedExpr() {
        return promotedExpr;
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            XQExpression analyzed = promotedExpr.staticAnalysis(statEnv);
            //Type inferredType = analyzed.getType();
            //if(!TypeUtil.isPromotable(inferredType, promoteTo)) {
            //    reportError("err:XPTY0004", "Declared type '" + _type
            //            + "' does not accept inferred type '" + inferredType + '\'');
            //}
            this.promotedExpr = analyzed;
        }
        return this;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence res = promotedExpr.eval(contextSeq, dynEnv);
        final Sequence promoted = DirectFunctionCall.mapFunctionArgument(res, promoteTo, dynEnv);
        return promoted;
    }

}
