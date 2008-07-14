/*
 * @(#)$Id: ExpressionProxy.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @deprecated
 */
public final class ExpressionProxy implements XQExpression {
    private static final long serialVersionUID = 1754483203141006063L;

    private final XQExpression orig;
    private XQExpression replaced = null;

    public ExpressionProxy(XQExpression orig) {
        this.orig = orig;
    }

    public XQExpression getOriginalExpr() {
        return orig;
    }

    public void setReplacedExpr(XQExpression e) {
        this.replaced = e;
    }

    public XQExpression getReplacedExpr() {
        return replaced;
    }

    public XQExpression getAppropriateExpr() {
        if(replaced != null) {
            return replaced;
        } else {
            return orig;
        }
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public int getBeginColumn() {
        return orig.getBeginColumn();
    }

    public int getBeginLine() {
        return orig.getBeginLine();
    }

    public void setLocation(int beginLine, int beginColumn) {
        getAppropriateExpr().setLocation(beginLine, beginColumn);
    }

    public Type getType() {
        return getAppropriateExpr().getType();
    }

    public boolean isLoopInvariant() {
        return getAppropriateExpr().isLoopInvariant();
    }

    public void setLoopInvariant(boolean invariant) {
        getAppropriateExpr().setLoopInvariant(invariant);
    }

    public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info) {
        return false;
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        return getAppropriateExpr();
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        return getAppropriateExpr().eval(contextSeq, dynEnv);
    }

    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        Sequence<? extends Item> result = eval(contextSeq, dynEnv);
        handler.emit(result);
    }

    public Item evalAsItem(Sequence<? extends Item> contextSeq, DynamicContext dynEnv, boolean opt)
            throws XQueryException {
        return getAppropriateExpr().evalAsItem(contextSeq, dynEnv, opt);
    }

    @Override
    public String toString() {
        return getAppropriateExpr().toString();
    }

}
