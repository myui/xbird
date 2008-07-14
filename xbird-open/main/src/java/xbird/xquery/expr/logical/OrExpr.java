/*
 * @(#)$Id: OrExpr.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.logical;

import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.expr.BooleanOp;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.func.seq.FnBoolean;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class OrExpr extends LogicalExpr implements BooleanOp {
    private static final long serialVersionUID = -1330815246394416449L;

    public OrExpr(List<? extends XQExpression> exprs) {
        super(exprs);
    }

    public OrExpr(XQExpression expr) {
        super(expr);
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        for(XQExpression e : _exprs) {
            Sequence s = e.eval(contextSeq, dynEnv);
            if(FnBoolean.effectiveBooleanValue(s)) {
                return BooleanValue.TRUE;
            }
        }
        return BooleanValue.FALSE;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(256);
        for(int i = 0; i < _exprs.size(); i++) {
            if(i != 0) {
                buf.append(" or ");
            }
            XQExpression e = _exprs.get(i);
            buf.append(e.toString());
        }
        return buf.toString();
    }

}
