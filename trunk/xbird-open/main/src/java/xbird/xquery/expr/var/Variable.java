/*
 * @(#)$Id: Variable.java 3749 2008-04-14 23:15:19Z yui $
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import xbird.util.concurrent.counter.ThreadLocalCounter;
import xbird.util.primitive.MutableLong;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.XQueryModule;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.Evaluable;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.func.DirectFunctionCall;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#prod-core-VarDecl
 */
public abstract class Variable extends AbstractXQExpression {
    private static final long serialVersionUID = 1L;
    private static final ThreadLocalCounter _counter = new ThreadLocalCounter(MutableLong.INT_MIN_VALUE);

    protected QualifiedName _varName;
    protected XQExpression _value;
    protected Sequence _result = null;

    protected int _referenceCount = 0;
    private/* final */int _birthid;

    protected Variable(QualifiedName varName) {
        this(varName, null);
    }

    protected Variable(QualifiedName varName, XQExpression value) {
        this._varName = varName;
        this._value = value;
        this._birthid = (int) _counter.getAndIncrement();
    }

    protected Variable() {}// Externalizable

    protected void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._type = (Type) in.readObject();
        final boolean hasVarName = in.readBoolean();
        if(hasVarName) {
            this._varName = QualifiedName.readFrom(in);
        }
        final boolean hasValue = in.readBoolean();
        if(hasValue) {
            this._value = (XQExpression) in.readObject();
        }
        final boolean hasResult = in.readBoolean();
        if(hasResult) {
            this._result = (Sequence) in.readObject();
        }
        this._referenceCount = in.readInt();
        this._birthid = in.readInt();
    }

    protected void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_type);
        final QualifiedName varName = _varName;
        if(varName == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            varName.writeExternal(out);
        }
        final XQExpression value = _value;
        if(value == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(value);
        }
        final Sequence result = _result;
        if(result == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(_result);
        }
        out.writeInt(_referenceCount);
        out.writeInt(_birthid);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readExternal(in);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeExternal(out);
    }

    public int incrementReferenceCount() {
        return ++_referenceCount;
    }

    public int getReferenceCount() {
        return _referenceCount;
    }

    public int getBirthId() {
        return _birthid;
    }

    public boolean isImmutable() {
        return true; // TODO REVIEWME
    }

    public boolean isInsideRemoteExpr() {
        return false;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    //--------------------------------------------
    // getter/setter

    public QualifiedName getVarName() {
        return _varName;
    }

    public String getName() {
        if(_varName == null) {
            return Integer.toString(System.identityHashCode(this));
        }
        return QNameUtil.toLexicalForm(_varName);
    }

    public void setValue(XQExpression value) {
        this._value = value;
    }

    /**
     * This value may null if result already set.
     */
    public XQExpression getValue() {
        return _value;
    }

    public void setResult(Sequence res) {
        this._result = res;
    }

    public Sequence getResult() {
        return this._result;
    }

    public void setType(Type type) {
        this._type = type;
    }

    public boolean equals(Variable trgVar) {
        return (_varName == null) ? trgVar == null : _varName.equals(trgVar.getVarName());
    }

    @Override
    public String toString() {
        return '$' + getName();
    }

    //--------------------------------------------
    // static analysis/dynamic evaluation

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            if(_result == null && _value == null) {
                throw new IllegalStateException("Neither value nor result are binded of $"
                        + getName());
            }
            if(_result != null) {
                this._type = _result.getType();
            } else {
                // infer type
                XQExpression analysed = _value.staticAnalysis(statEnv);
                XQExpression promoted = TypeUtil.promote(analysed, _type);
                this._type = promoted.getType();
                this._value = promoted;
            }
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        if(_result != null) {
            return _result;
        }
        if(_value == null) {
            throw new DynamicError("The value of variable '$" + getName() + "' is not defined");
        }
        final Sequence<? extends Item> result = _value.eval(contextSeq, dynEnv);
        this._result = result;
        return result;
    }

    public static class GlobalVariable extends Variable {
        private static final long serialVersionUID = 4518495428271329049L;

        public GlobalVariable(QualifiedName varName, XQExpression value) {
            super(varName, value);
        }

        public GlobalVariable(QualifiedName varName) {
            super(varName, null);
        }

        @Override
        public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info) {
            return true;
        }

        @Override
        public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
            super.staticAnalysis(statEnv);
            if(_referenceCount == 1 && _value != null) {
                return _value;
            }
            return this;
        }
    }

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#sec_variable-declarations
     */
    public static final class ExternalVariable extends GlobalVariable {
        private static final long serialVersionUID = -1619955854072232237L;

        private boolean checkType = false;

        public ExternalVariable(QualifiedName varName, XQueryModule currentModule) {
            super(varName, null);
            this._type = SequenceType.ANY_ITEMS;
        }

        @Override
        public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
                throws XQueryException {
            return visitor.visit(this, ctxt);
        }

        @Override
        public String toString() {
            return super.toString() + " external";
        }

        @Override
        public void setType(Type type) {
            this._type = type;
            this.checkType = true;
        }

        @Override
        public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
            return this;
        }

        @Override
        public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
                throws XQueryException {
            Sequence result = _result;
            if(result == null) {
                throw new DynamicError("External variable '$" + getName() + "' is not set");
            }
            if(checkType) {
                final Type resultType = result.getType();
                if(!TypeUtil.subtypeOf(resultType, _type)) {
                    result = DirectFunctionCall.mapFunctionArgument(_result, _type, dynEnv);
                }
            }
            return result;
        }
    }

    public static final class PreEvaluatedVariable extends Variable implements Evaluable {
        private static final long serialVersionUID = 555376125369760898L;

        public PreEvaluatedVariable(Sequence result, Type type) {
            super(null);
            if(result == null) {
                throw new IllegalArgumentException();
            }
            if(type == null) {
                throw new IllegalArgumentException();
            }
            this._result = result;
            this._type = type;
            this._analyzed = true;
        }

        public PreEvaluatedVariable(Sequence<? extends Item> eagerEvaluated) {
            this(eagerEvaluated, eagerEvaluated.getType());
        }

        @Override
        public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
                throws XQueryException {
            return visitor.visit(this, ctxt);
        }

        @Override
        public String toString() {
            return _result.toString();
        }
    }

}