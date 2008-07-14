/*
 * @(#)$Id: Binding.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.flwr;

import xbird.xquery.XQueryException;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class Binding extends AbstractXQExpression {
    private static final long serialVersionUID = 1L;

    public static final int FOR_CLAUSE = 1;
    public static final int LET_CLAUSE = 2;

    protected final BindingVariable _variable;

    public Binding(BindingVariable variable) {
        this._variable = variable;
    }

    public abstract Binding visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException;

    public BindingVariable getVariable() {
        return _variable;
    }

    public Type getVariableType() {
        return _variable.getType();
    }

    public XQExpression getExpression() {
        return _variable.getValue();
    }

    public void setExpression(XQExpression expr) {
        _variable.setValue(expr);
    }

    public abstract int getExpressionType();

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            _analyzed = true;
            _variable.staticAnalysis(statEnv);
        }
        return this;
    }

}