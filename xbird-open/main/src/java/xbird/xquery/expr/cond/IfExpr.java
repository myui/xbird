/*
 * @(#)$Id: IfExpr.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.cond;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.*;
import xbird.xquery.expr.seq.SequenceExpression;
import xbird.xquery.func.seq.FnBoolean;
import xbird.xquery.meta.*;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.ChoiceType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#sec_conditionals
 */
public class IfExpr extends AbstractXQExpression {
    private static final long serialVersionUID = 5533795454418649122L;

    private XQExpression condExpr;
    private XQExpression thenExpr, elseExpr;

    public IfExpr(XQExpression condExpr, XQExpression thenExpr) {
        this(condExpr, thenExpr, SequenceExpression.EMPTY_SEQUENCE);
    }

    public IfExpr(XQExpression condExpr, XQExpression thenExpr, XQExpression elseExpr) {
        this.condExpr = condExpr;
        this.thenExpr = thenExpr;
        this.elseExpr = elseExpr;
    }

    public XQExpression getCondExpr() {
        return this.condExpr;
    }

    public XQExpression getElseExpr() {
        return this.elseExpr;
    }

    public XQExpression getThenExpr() {
        return this.thenExpr;
    }

    public void setElseExpr(XQExpression elseExpr) {
        this.elseExpr = elseExpr;
    }

    public void setThenExpr(XQExpression thenExpr) {
        this.thenExpr = thenExpr;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            this.condExpr = condExpr.staticAnalysis(statEnv);
            this.thenExpr = thenExpr.staticAnalysis(statEnv);
            this.elseExpr = elseExpr.staticAnalysis(statEnv);
            // optimize if cond statically returns true/false.
            if(condExpr instanceof Evaluable) {
                Sequence<? extends Item> evaluated = condExpr.eval(null, DynamicContext.DUMMY);
                boolean cond = FnBoolean.effectiveBooleanValue(evaluated);
                return cond ? thenExpr : elseExpr;
            }
            this._type = new ChoiceType(thenExpr.getType(), elseExpr.getType());
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence condSeq = condExpr.eval(contextSeq, dynEnv);
        if(FnBoolean.effectiveBooleanValue(condSeq)) {
            return thenExpr.eval(contextSeq, dynEnv);
        } else {
            return elseExpr.eval(contextSeq, dynEnv);
        }
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence condSeq = condExpr.eval(contextSeq, dynEnv);
        if(FnBoolean.effectiveBooleanValue(condSeq)) {
            thenExpr.evalAsEvents(handler, contextSeq, dynEnv);
        } else {
            elseExpr.evalAsEvents(handler, contextSeq, dynEnv);
        }
    }

}
