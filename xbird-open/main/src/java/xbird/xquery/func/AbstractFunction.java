/*
 * @(#)$Id: AbstractFunction.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func;

import java.io.*;
import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class AbstractFunction implements Function, Externalizable {

    protected/* final */QualifiedName _funcName;
    protected Type _returnType; // may be null with void function.
    protected EvaluationPolicy _evalPocily = EvaluationPolicy.dynamic;

    public AbstractFunction(QualifiedName funcName, Type retType) {
        if(funcName == null) {
            throw new IllegalArgumentException();
        }
        this._funcName = funcName;
        this._returnType = retType;
    }

    public AbstractFunction() {}// for Externalizable

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._funcName = QualifiedName.readFrom(in);
        final boolean hasReturnType = in.readBoolean();
        if(hasReturnType) {
            this._returnType = (Type) in.readObject();
        }
        this._evalPocily = (EvaluationPolicy) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        _funcName.writeExternal(out);
        final Type returnType = _returnType;
        if(returnType == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(returnType);
        }
        out.writeObject(_evalPocily);
    }

    public QualifiedName getName() {
        return _funcName;
    }

    public Type getReturnType() {
        return _returnType;
    }

    public Type getReturnType(List<XQExpression> params) {
        return _returnType;
    }

    public FunctionSignature getFunctionSignature(int arity) throws XQueryException {
        final FunctionSignature[] signs = getFunctionSignatures();
        for(FunctionSignature sig : signs) {
            if(sig.getArity() == arity) {
                return sig;
            }
        }
        throw new XQueryException("Function " + _funcName.getLocalPart() + " of arity " + arity
                + " not found");
    }

    public Function staticAnalysis(StaticContext context, List<? extends XQExpression> params)
            throws XQueryException {
        return this;
    }

    public EvaluationPolicy getEvaluationPolicy() {
        return _evalPocily;
    }

    public XQExpression rewrite(List<? extends XQExpression> params, StaticContext context)
            throws XQueryException {
        assert (_evalPocily == EvaluationPolicy.rewritten);
        throw new IllegalStateException("AbstractFunction#rewrite should be overrided.");
    }

}
