/*
 * @(#)$Id: BindingVariable.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.func.FunctionCall.FunctionCallContext;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.StringType;
import xbird.xquery.type.xs.UnsignedIntType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class BindingVariable extends Variable {
    private static final long serialVersionUID = 1L;
    private static final Log LOG = LogFactory.getLog(BindingVariable.class);
    public static final int SLOT_UNSET = -1;

    private transient int bindedSlot = SLOT_UNSET;
    private transient boolean _insideRemoteExpr = false;

    public BindingVariable(QualifiedName varName) {
        super(varName);
    }

    public BindingVariable() {//for Externalizable
        super();
    }

    @Override
    public boolean isImmutable() {
        return false;
    }

    public void setInsideRemoteExpr(boolean inside) {
        this._insideRemoteExpr = inside;
    }

    @Override
    public boolean isInsideRemoteExpr() {
        return _insideRemoteExpr;
    }

    public void setSlot(int slot) {
        this.bindedSlot = slot;
    }

    public int getSlot() {
        return bindedSlot;
    }

    @Override
    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    @Override
    public void setResult(Sequence res) {
        throw new IllegalStateException("allocateResult(Sequence, DynamicContext) method should be used");
    }

    public void allocateResult(Sequence bindingValue, DynamicContext dynEnv) {
        assert (dynEnv != null);
        assert (bindingValue != null);
        if(!(dynEnv instanceof FunctionCallContext)) {
            this._result = bindingValue;
            return;
        }
        FunctionCallContext fctxt = (FunctionCallContext) dynEnv;
        fctxt.storeLocal(this, bindingValue);
    }

    public Sequence loadResult(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        if(!(dynEnv instanceof FunctionCallContext)) {
            if(_result == null) {
                return _value.eval(contextSeq, dynEnv);
            }
            return _result;
        }
        FunctionCallContext fctxt = (FunctionCallContext) dynEnv;
        int slotSize = fctxt.getSlotSize();
        final Sequence res;
        if(bindedSlot >= slotSize) {
            res = _value.eval(contextSeq, dynEnv);
            fctxt.storeLocal(this, res);
        } else if(bindedSlot == SLOT_UNSET) {
            res = _value.eval(contextSeq, dynEnv);
            fctxt.storeLocal(this, res);
        } else {
            res = fctxt.loadLocal(this, bindedSlot);
        }
        return res;
    }

    @Override
    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence res = loadResult(contextSeq, dynEnv);
        if(res == null) {
            throw new IllegalStateException("The value of '$" + getName() + "' is not binded");
        }
        return res;
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence res = loadResult(contextSeq, dynEnv);
        if(res == null) {
            throw new IllegalStateException("The value of '$" + getName() + "' is not binded");
        }
        handler.emit(res);
    }

    //--------------------------------------------
    // subclasses

    public static final class ForVariable extends BindingVariable {
        private static final long serialVersionUID = -8304070464774579520L;

        public ForVariable(QualifiedName varName) {
            super(varName);
        }

        @Override
        public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info) {
            if(_value == null) {
                throw new IllegalStateException("value is not set.. bug?");
            }
            if(info.isLookaheadRequired()) {
                return false;
            }
            boolean accessable = _value.isPathIndexAccessable(statEnv, info);
            if(accessable) {
                info.requestLookahead(this);
                return true;
            } else {
                return false;
            }
        }

    }

    public static class LetVariable extends BindingVariable {
        private static final long serialVersionUID = -5649071142501903158L;

        private boolean eagarEval = false;

        public LetVariable(QualifiedName varName) {
            super(varName);
        }

        public void setEagarEvaluated(boolean eager) {
            this.eagarEval = eager;
        }

        public boolean attemptEagarEvaluation() {
            return eagarEval;
        }

        @Override
        public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
            super.staticAnalysis(statEnv);
            if(_referenceCount == 1) {
                return _value;
            }
            return this;
        }

        @Override
        public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info) {
            if(_value == null) {
                throw new IllegalStateException("value is not set.. bug?");
            }
            return _value.isPathIndexAccessable(statEnv, info);
        }
    }

    public static final class AnonymousLetVariable extends LetVariable {
        private static final long serialVersionUID = -1940001402036505359L;

        public AnonymousLetVariable(XQExpression expr) {
            super(null);
            this._value = expr;
            this._type = expr.getType();
        }

        @Override
        public String getName() {
            return Integer.toString(System.identityHashCode(this));
        }
    }

    public static final class LoopInvariantLetVariable extends LetVariable {
        private static final long serialVersionUID = 3284084573759768499L;

        public LoopInvariantLetVariable(XQExpression expr) {
            super(null);
            this._value = expr;
            this._type = expr.getType();
        }

        @Override
        public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
            this._analyzed = true;
            return this;
        }

        @Override
        public Sequence<Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
                throws XQueryException {
            if(_result != null) {
                if(LOG.isTraceEnabled()) {
                    LOG.trace("reuse materialized result $" + getName());
                }
                return _result;
            }
            if(LOG.isDebugEnabled()) {
                LOG.debug("materialized $" + getName() + "=> \n" + _value);
            }
            Sequence<? extends Item> result = _value.eval(contextSeq, dynEnv);
            List<? extends Item> materialized = result.materialize();
            ValueSequence vs = new ValueSequence(materialized, dynEnv);
            this._result = vs;
            return vs;
        }

        @Override
        public String getName() {
            return Integer.toString(System.identityHashCode(this));
        }
    }

    public static final class PositionalVariable extends BindingVariable {
        private static final long serialVersionUID = 831187130699648659L;

        public PositionalVariable(QualifiedName varName) {
            super(varName);
            this._type = UnsignedIntType.UNSIGNED_INT;
        }

        @Override
        public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
            return this;
        }
    }

    public static final class QuantifiedVariable extends BindingVariable {
        private static final long serialVersionUID = 4005880607260478495L;

        public QuantifiedVariable(QualifiedName varName) {
            super(varName);
        }
    }

    public static final class CaseVariable extends BindingVariable {
        private static final long serialVersionUID = -7853855368064456651L;

        public CaseVariable(QualifiedName varName) {
            super(varName);
            this._type = Type.ANY;
        }

        @Override
        public CaseVariable staticAnalysis(StaticContext statEnv) throws XQueryException {
            return this;
        }
    }

    public static final class ParametricVariable extends BindingVariable {
        private static final long serialVersionUID = 4734930446193789472L;

        public ParametricVariable(QualifiedName varName) {
            super(varName);
        }

        @Override
        public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
                throws XQueryException {
            return visitor.visit(this, ctxt);
        }
    }

    public static final class ExecHostVariable extends BindingVariable implements Externalizable {
        private static final long serialVersionUID = -970934824142356358L;
        private Sequence _xferedResult = null;

        public ExecHostVariable(QualifiedName varName) {
            super(varName);
            this._type = StringType.STRING;
        }

        public ExecHostVariable() {
            super();
            this._type = StringType.STRING;
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this._varName = QualifiedName.readFrom(in);
            this._xferedResult = (Sequence) in.readObject();
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            _varName.writeExternal(out);
            out.writeObject(_xferedResult);
        }

        @Override
        public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
                throws XQueryException {
            return super.visit(visitor, ctxt);
        }

        @Override
        public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
            return this;
        }

        @Override
        public boolean isInsideRemoteExpr() {
            return true;
        }

        @Override
        public void allocateResult(Sequence bindingValue, DynamicContext dynEnv) {
            this._xferedResult = bindingValue;
        }

        @Override
        public Sequence loadResult(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
                throws XQueryException {
            return _xferedResult;
        }
    }

}
