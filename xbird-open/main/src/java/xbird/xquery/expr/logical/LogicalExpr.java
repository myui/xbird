/*
 * @(#)$Id$$
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

import java.util.ArrayList;
import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.type.xs.BooleanType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class LogicalExpr extends AbstractXQExpression {
    private static final long serialVersionUID = 1L;

    protected/* final */List<XQExpression> _exprs;

    public LogicalExpr(List<? extends XQExpression> exprs) {
        super();
        this._exprs = (List<XQExpression>) exprs;
        this._type = BooleanType.BOOLEAN;
    }

    public LogicalExpr(XQExpression... exprs) {
        super();
        final List<XQExpression> list = new ArrayList<XQExpression>(exprs.length);
        for(XQExpression e : exprs) {
            list.add(e);
        }
        this._exprs = list;
        this._type = BooleanType.BOOLEAN;
    }

    public List<XQExpression> getExpressions() {
        return _exprs;
    }

    public void addExpression(AndExpr expr) {
        _exprs.add(expr);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            final List<XQExpression> exprs = _exprs;
            final int len = exprs.size();
            for(int i = 0; i < len; i++) {
                XQExpression e = exprs.get(i);
                exprs.set(i, e.staticAnalysis(statEnv));
            }
        }
        return this;
    }

}