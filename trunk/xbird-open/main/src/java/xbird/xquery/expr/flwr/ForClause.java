/*
 * @(#)$Id: ForClause.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.Iterator;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.sequence.ProxySequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.expr.var.BindingVariable.ForVariable;
import xbird.xquery.expr.var.BindingVariable.PositionalVariable;
import xbird.xquery.meta.*;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.xs.UnsignedIntType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ForClause extends Binding {
    private static final long serialVersionUID = -8160074766452136479L;

    private PositionalVariable _posVar = null;
    private int _loopDepth = -1;

    public ForClause(ForVariable variable) {
        super(variable);
        assert (variable != null);
    }

    public ForClause visit(XQueryParserVisitor visitor, XQueryContext ctxt) throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    //--------------------------------------------
    // getter/setter

    public int getLoopDepth() {
        return _loopDepth;
    }

    @Override
    public ForVariable getVariable() {
        return (ForVariable) _variable;
    }

    public XQExpression getInExpr() {
        return _variable.getValue();
    }

    public void setInExpr(XQExpression inExpr) {
        _variable.setValue(inExpr);
    }

    public PositionalVariable getPositionVariable() {
        return _posVar;
    }

    public void setPositionVariable(PositionalVariable posVar) {
        posVar.setType(UnsignedIntType.UNSIGNED_INT);
        this._posVar = posVar;
    }

    public int getExpressionType() {
        return FOR_CLAUSE;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(256);
        buf.append("for $");
        buf.append(_variable.getName());
        if(_posVar != null) {
            buf.append(" at $");
            buf.append(_posVar.getName());
        }
        buf.append(" in ");
        XQExpression inExpr = _variable.getValue();
        assert (inExpr != null);
        buf.append(inExpr);
        return buf.toString();
    }

    //--------------------------------------------
    // static analysis/dynamic evaluation

    @Override
    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            _variable.staticAnalysis(statEnv);
            this._type = _variable.getType().prime();
            this._loopDepth = statEnv.incrLoopDepth();
        }
        return this;
    }

    public ForEmurationSequence eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        XQExpression inExpr = _variable.getValue();
        Sequence<? extends Item> src = inExpr.eval(contextSeq, dynEnv);
        /*
         if(_loopDepth > 1) {
         // TODO REVIEWME memory space            
         // cache evaluated sequence in the nested loop
         if(inExpr.isLoopInvariant()) {
         List<? extends Item> materialized = src.materialize();
         src = new ValueSequence(materialized, dynEnv);
         }
         }
         */
        final ForEmurationSequence forSeq;
        if(_posVar != null) {
            forSeq = new ForAtEmurationSequence(_variable, _posVar, src, dynEnv);
        } else {
            forSeq = new ForEmurationSequence(_variable, src, dynEnv);
        }
        return forSeq;
    }

    private static class ForEmurationSequence extends ProxySequence {
        private static final long serialVersionUID = -7238875877774044286L;
        protected final BindingVariable _bindingVar;

        ForEmurationSequence(BindingVariable var, Sequence src, DynamicContext dynEnv) {
            super(src, dynEnv);
            this._bindingVar = var;
        }

        public boolean next(IFocus focus) throws XQueryException {
            Iterator<? extends Item> delItor = focus.getBaseFocus();
            boolean hasNext = delItor.hasNext();
            if(hasNext) {
                Item delItem = delItor.next();
                focus.setContextItem(delItem);
                _bindingVar.allocateResult(delItem, _dynEnv); // note that binding var is shared.
                return true;
            }
            focus.setReachedEnd(true);
            return false;
        }
    }

    private static final class ForAtEmurationSequence extends ForEmurationSequence {
        private static final long serialVersionUID = 4480262472646639142L;

        private final XInteger _posv;

        ForAtEmurationSequence(BindingVariable bindingVar, PositionalVariable posVar, Sequence src, DynamicContext dynEnv) {
            super(bindingVar, src, dynEnv);
            if(posVar == null) {
                throw new IllegalStateException();
            }
            XInteger posv = new XInteger(0);
            posVar.allocateResult(posv, dynEnv);
            this._posv = posv;
        }

        @Override
        public IFocus iterator() {
            final IFocus focus = super.iterator();
            _posv.setValue(0);
            return focus;
        }

        @Override
        public boolean next(IFocus focus) throws XQueryException {
            final boolean hasNext = super.next(focus);
            if(hasNext) {
                final int curpos = focus.getContextPosition() + 1;
                _posv.setValue(curpos);
                return true;
            } else {
                return false;
            }
        }
    }

}