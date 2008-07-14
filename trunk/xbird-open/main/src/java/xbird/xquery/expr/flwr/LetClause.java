/*
 * @(#)$Id: LetClause.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.BindingVariable.LetVariable;
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
public class LetClause extends Binding {
    private static final long serialVersionUID = 7693803045289928607L;

    public LetClause(LetVariable variable) {
        super(variable);
    }

    @Override
    public LetVariable getVariable() {
        return (LetVariable) _variable;
    }

    public int getExpressionType() {
        return LET_CLAUSE;
    }

    public LetClause visit(XQueryParserVisitor visitor, XQueryContext ctxt) throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    @Override
    public String toString() {
        return "let $" + _variable.getName() + " := " + _variable.getValue().toString();
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        LetVariable lv = (LetVariable) _variable;
        XQExpression letExpr = lv.getValue();
        Sequence<? extends Item> res = letExpr.eval(contextSeq, dynEnv);
        if(lv.attemptEagarEvaluation() && lv.getReferenceCount() > 1) {
            List<? extends Item> items = res.materialize(); // TODO REVIEWME weak memory utilization
            lv.allocateResult(new ValueSequence(items, dynEnv), dynEnv);
        } else {
            lv.allocateResult(res, dynEnv);
        }
        return res;
    }
}