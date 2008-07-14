/*
 * @(#)$Id: VarRef.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.var;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * Used as a "Variable Reference" in expressions or a "Parameter" of function.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#id-variables
 */
public final class VarRef extends AbstractXQExpression {
    private static final long serialVersionUID = 2089453881615342109L;

    private final QualifiedName _varName;
    private Variable _value;

    public VarRef(Variable var) {
        if(var == null) {
            throw new IllegalArgumentException();
        }
        this._varName = var.getVarName();
        this._value = var;
        this._type = var.getType();
        var.incrementReferenceCount();
    }

    public QualifiedName getName() {
        return _varName;
    }

    public Variable getValue() {
        return _value;
    }

    public void setValue(Variable var) {
        this._value = var;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    @Override
    public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info) {
        return _value.isPathIndexAccessable(statEnv, info);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            XQExpression analysed = _value.staticAnalysis(statEnv);
            if(!(analysed instanceof Variable)) {
                return analysed;
            }
            this._value = (Variable) analysed;
            this._type = analysed.getType();
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence<? extends Item> res = _value.eval(contextSeq, dynEnv); // [Note] should not shortcut        
        return res;
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        _value.evalAsEvents(handler, contextSeq, dynEnv);
    }

    @Override
    public String toString() {
        return '$' + _value.getName();
    }

}
