/*
 * @(#)$Id: DistinctSortExpr.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.*;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class DistinctSortExpr extends AbstractXQExpression {
    private static final long serialVersionUID = -2142546948798525618L;

    private XQExpression _step;
    private final boolean _reverse;

    public DistinctSortExpr(XQExpression step, boolean reverse) {
        if(step == null) {
            throw new IllegalArgumentException();
        }
        this._step = step;
        this._reverse = reverse;
        this._type = step.getType();
    }

    public XQExpression getStepExpr() {
        return _step;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            this._step = _step.staticAnalysis(statEnv);
            this._type = _step.getType();
        }
        return this;
    }

    @Override
    public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info) {
        return true;
    }

    public DistinctDocOrderSequence eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence src = _step.eval(contextSeq, dynEnv);
        final INodeSequence<XQNode> wrapped = ProxyNodeSequence.wrap(src, dynEnv);
        return new DistinctDocOrderSequence(wrapped, dynEnv, _type, _reverse); // TODO repair not to use PriorityQueue
    }
}
