/*
 * @(#)$Id: ValidateOp.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.dyna;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#id-validate
 * @link http://www.w3.org/TR/xquery-semantics/#sec_validate_expr
 */
public final class ValidateOp extends AbstractXQExpression {
    private static final long serialVersionUID = 3230093526066669323L;
    
    private final boolean isStrict;
    private final XQExpression expr;

    public ValidateOp(XQExpression expr) {
        this(expr, true);
    }

    public ValidateOp(XQExpression expr, boolean strict) {
        this.expr = expr;
        this.isStrict = strict;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt) throws XQueryException {
        return visitor.visit(this, ctxt);
    }
    
    public XQExpression getExpression() {
        return this.expr;
    }

    public boolean isStrict() {
        return this.isStrict;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("validate ");
        if (isStrict == false) {
            buf.append("lax ");
        }
        buf.append('{');
        buf.append(expr.toString());
        buf.append('}');
        return buf.toString();
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        assert (!_analyzed);
        return expr.staticAnalysis(statEnv);
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv) throws XQueryException {
        throw new UnsupportedOperationException("ValidationExpr is not supported.");
    }

}
