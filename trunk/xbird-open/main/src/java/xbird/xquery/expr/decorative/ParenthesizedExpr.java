/*
 * @(#)$Id: ParenthesizedExpr.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.decorative;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.seq.SequenceExpression;
import xbird.xquery.meta.*;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#id-paren-expressions
 */
public final class ParenthesizedExpr extends AbstractXQExpression implements DecorativeExpression {
    private static final long serialVersionUID = 3613352065392436233L;

    private final XQExpression expr;

    public ParenthesizedExpr(XQExpression expr) {
        this.expr = expr;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression getExpr() {
        return this.expr;
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        assert (!_analyzed);
        if(expr == null) {
            return SequenceExpression.EMPTY_SEQUENCE;
        } else {
            return expr.staticAnalysis(statEnv);
        }
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        throw new IllegalStateException("ParenthesizedExpr should be pruned.");
    }

    public static XQExpression normalize(XQExpression expr) {
        if(expr instanceof ParenthesizedExpr) {
            return normalize(((ParenthesizedExpr) expr).expr); // remove nesting
        } else {
            return expr;
        }
    }

}