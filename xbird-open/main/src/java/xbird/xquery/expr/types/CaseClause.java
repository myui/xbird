/*
 * @(#)$Id: CaseClause.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.BindingVariable.CaseVariable;
import xbird.xquery.meta.*;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CaseClause extends AbstractXQExpression {
    private static final long serialVersionUID = -1278988831116465658L;

    private final CaseVariable bindingVar;
    private XQExpression retExpr;

    public CaseClause(CaseVariable var, XQExpression retExpr) {
        this.bindingVar = var;
        this.retExpr = retExpr;
    }

    public CaseVariable getVariable() {
        return this.bindingVar;
    }

    @Deprecated
    public Type getTypeTest() {
        return bindingVar.getType();
    }

    public XQExpression getReturnExpr() {
        return this.retExpr;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            XQExpression analyzed = retExpr.staticAnalysis(statEnv);
            this.retExpr = analyzed;
            this._type = analyzed.getType();
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        throw new IllegalStateException("CaseClause should not be evaluated.");
    }

}
