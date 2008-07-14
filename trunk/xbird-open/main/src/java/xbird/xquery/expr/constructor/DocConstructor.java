/*
 * @(#)$Id: DocConstructor.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.constructor;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.instance.XQueryDataModel;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.node.DMDocument;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.node.DocumentTest;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#id-documentConstructors
 * @link http://www.w3.org/TR/xquery/#doc-xquery-CompDocConstructor
 */
public final class DocConstructor extends AbstractXQExpression implements NodeConstructor {
    private static final long serialVersionUID = 188895787718907324L;
    
    private XQExpression expr;

    public DocConstructor(XQExpression expr) {
        this.expr = expr;
        this._type = DocumentTest.ANY_DOCUMENT;
    }

    public XQExpression getExpr() {
        return this.expr;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt) throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    //--------------------------------------------
    // static analysis/dynamic eval

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            this.expr = expr.staticAnalysis(statEnv);
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        Sequence contents = expr.eval(contextSeq, dynEnv);
        DMDocument doc = XQueryDataModel.createDocument(dynEnv.getStaticContext().getBaseURIString(), contents);
        return doc;
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv) throws XQueryException {
        Sequence<? extends Item> contents = expr.eval(contextSeq, dynEnv);
        handler.evStartDocument();
        handler.emit(contents);
        handler.evEndDocument();
    }

}
