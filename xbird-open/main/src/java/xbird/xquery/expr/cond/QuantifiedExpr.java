/*
 * @(#)$Id: QuantifiedExpr.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.expr.*;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.expr.var.BindingVariable.QuantifiedVariable;
import xbird.xquery.expr.var.Variable.PreEvaluatedVariable;
import xbird.xquery.func.seq.FnBoolean;
import xbird.xquery.meta.*;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.xs.BooleanType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author yui (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#id-quantified-expressions
 * @link http://www.w3.org/TR/xquery-semantics/#id-quantified-expressions
 */
public class QuantifiedExpr extends AbstractXQExpression implements BooleanOp {
    private static final long serialVersionUID = 3130103441451724447L;

    private final boolean every;
    private final QuantifiedVariable bindingVar;
    private XQExpression condExpr = null;

    public QuantifiedExpr(boolean every, QuantifiedVariable bindingVar) {
        assert (bindingVar != null);
        this.every = every;
        this.bindingVar = bindingVar;
        this._type = BooleanType.BOOLEAN;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public boolean isEvery() {
        return this.every;
    }

    public BindingVariable getBinding() {
        return bindingVar;
    }

    public void setCondExpr(XQExpression cond) {
        this.condExpr = cond;
    }

    public XQExpression getCondExpr() {
        return this.condExpr;
    }

    //--------------------------------------------
    // static analysis/dynamic evaluation

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            final XQExpression analyzed = bindingVar.staticAnalysis(statEnv);
            if(analyzed instanceof Evaluable) {
                final Sequence<? extends Item> src = analyzed.eval(null, DynamicContext.DUMMY);
                final Sequence<? extends Item> evaluated = effectiveBooleanValue(src, null, DynamicContext.DUMMY);
                return new PreEvaluatedVariable(evaluated);
            } else if(TypeUtil.subtypeOf(SequenceType.EMPTY, bindingVar.getType())) {
                final boolean isEvery = every;
                return new LiteralExpr(isEvery ? BooleanValue.TRUE : BooleanValue.FALSE);
            }
            this.condExpr = condExpr.staticAnalysis(statEnv);
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final XQExpression inExpr = bindingVar.getValue();
        final Sequence<? extends Item> src = inExpr.eval(contextSeq, dynEnv);
        return effectiveBooleanValue(src, contextSeq, dynEnv);
    }

    public Sequence<? extends Item> effectiveBooleanValue(Sequence<? extends Item> src, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final IFocus<? extends Item> srcItor = src.iterator();
        if(every) {
            while(true) {
                if(!srcItor.hasNext()) {
                    srcItor.closeQuietly();
                    return BooleanValue.TRUE;
                } else {
                    final Item curitem = srcItor.next();
                    bindingVar.allocateResult(curitem, dynEnv);
                    final Sequence v = condExpr.eval(contextSeq, dynEnv);
                    if(!FnBoolean.effectiveBooleanValue(v)) {
                        srcItor.closeQuietly();
                        return BooleanValue.FALSE;
                    }
                }
            }
        } else {//some
            while(true) {
                if(!srcItor.hasNext()) {
                    srcItor.closeQuietly();
                    return BooleanValue.FALSE;
                } else {
                    final Item curitem = srcItor.next();
                    bindingVar.allocateResult(curitem, dynEnv);
                    final Sequence v = condExpr.eval(contextSeq, dynEnv);
                    if(FnBoolean.effectiveBooleanValue(v)) {
                        srcItor.closeQuietly();
                        return BooleanValue.TRUE;
                    }
                }
            }
        }
    }

}
