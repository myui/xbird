/*
 * @(#)$Id: UserFunction.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xbird.xquery.Module;
import xbird.xquery.XQueryConstants;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.BindingVariable.ParametricVariable;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.SyntaxError;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.Untyped;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#doc-xquery-FunctionDecl
 */
public class UserFunction extends AbstractFunction {
    private static final long serialVersionUID = 2391434779427989419L;

    public transient int beginLine = -1, beginColumn = -1;

    private XQExpression _bodyExpression = null; // may be null with External function.
    private/* final */List<ParametricVariable> _parameters; // immutable, not null  
    private boolean _isExternal = false;
    private/* final */FunctionSignature[] _signs;

    private transient/* final */Module _declaredModule;

    public UserFunction(Module declaredModule, QualifiedName funcName, List<ParametricVariable> parameters, Type retType) {
        super(funcName, retType);
        assert (retType != null);
        if(parameters == null) {
            throw new IllegalArgumentException("Function parameters MUST not be null.");
        }
        this._signs = new FunctionSignature[] { new FunctionSignature(funcName, extractTypes(parameters)) };
        this._parameters = parameters;
        this._declaredModule = declaredModule;
    }

    protected UserFunction(Module declaredModule, QualifiedName funcName, XQExpression bodyExpr, List<ParametricVariable> parameters, Type retType) {
        this(declaredModule, funcName, parameters, retType);
        if(bodyExpr == null) {
            throw new IllegalArgumentException();
        }
        this._bodyExpression = bodyExpr;
    }

    protected UserFunction(Module declaredModule, QualifiedName funcName, XQExpression bodyExpr, Type retType) {
        this(declaredModule, funcName, bodyExpr, Collections.<ParametricVariable> emptyList(), retType);
    }

    public UserFunction() {
        super();
    } // for Externalizable

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        final boolean hasBodyExpr = in.readBoolean();
        if(hasBodyExpr) {
            this._bodyExpression = (XQExpression) in.readObject();
        }
        final int numParams = in.readInt();
        final ArrayList<ParametricVariable> params = new ArrayList<ParametricVariable>(numParams);
        for(int i = 0; i < numParams; i++) {
            ParametricVariable pv = (ParametricVariable) in.readObject();
            params.add(pv);
        }
        this._parameters = params;
        this._isExternal = in.readBoolean();
        final int numSigns = in.readInt();
        final FunctionSignature[] signs = new FunctionSignature[numSigns];
        for(int i = 0; i < numSigns; i++) {
            FunctionSignature sign = FunctionSignature.readFrom(in);
            signs[i] = sign;
        }
        this._signs = signs;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        final XQExpression bodyExpr = _bodyExpression;
        if(bodyExpr == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(bodyExpr);
        }
        final List<ParametricVariable> params = _parameters;
        final int paramsSize = params.size();
        out.writeInt(paramsSize);
        for(ParametricVariable v : params) {
            out.writeObject(v);
        }
        out.writeBoolean(_isExternal);
        final FunctionSignature[] signs = _signs;
        final int numSigns = signs.length;
        out.writeInt(numSigns);
        for(int i = 0; i < numSigns; i++) {
            signs[i].writeExternal(out);
        }
    }

    public void visit(XQueryParserVisitor visitor, XQueryContext ctxt) throws XQueryException {
        visitor.visit(this, ctxt);
    }

    public XQExpression getBodyExpression() {
        return _bodyExpression;
    }

    public FunctionSignature[] getFunctionSignatures() {
        return this._signs;
    }

    public List<ParametricVariable> getParameters() {
        return _parameters;
    }

    public boolean isExternal() {
        return _isExternal;
    }

    public void setBodyExpression(XQExpression body) {
        assert (body != null);
        this._bodyExpression = body;
    }

    public void setExternal(boolean isExternal) {
        this._isExternal = isExternal;
    }

    public void setLocation(int beginLine, int beginColumn) {
        this.beginLine = beginLine;
        this.beginColumn = beginColumn;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(256);
        buf.append("declare function ");
        final String prefix = getName().getPrefix();
        if(prefix != null && prefix.length() > 0) {
            buf.append(prefix);
            buf.append(':');
        }
        buf.append(getName().getLocalPart());
        buf.append(" (");
        for(int i = 0; i < _parameters.size(); i++) {
            if(i != 0) {
                buf.append(", ");
            }
            XQExpression p = _parameters.get(i);
            buf.append(p.toString());
        }
        buf.append(") ");
        Type retType = getReturnType();
        if(retType != null) {
            buf.append("as ");
            buf.append(retType.toString());
        }
        buf.append("{\n");
        buf.append(_bodyExpression.toString());
        buf.append("\n}");
        return buf.toString();
    }

    @Override
    public Function staticAnalysis(StaticContext statEnv, List<? extends XQExpression> params)
            throws XQueryException {
        if(_bodyExpression == null) {
            throw new IllegalStateException("Expression is not binded!");
        }
        XQExpression analyzed = _bodyExpression.staticAnalysis(statEnv);
        this._bodyExpression = analyzed;
        final Type inferredType = analyzed.getType();
        if(_returnType == Untyped.UNTYPED) {
            this._returnType = inferredType;
        }
        return this;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        return _bodyExpression.eval(argv, dynEnv);
    }

    public static void checkAsPredefinedFunction(String nsuri) throws XQueryException {
        if(nsuri == null || nsuri.length() == 0) {
            throw new SyntaxError("err:XQST0060");
        }
        if(XQueryConstants.XML_URI.equals(nsuri) || XQueryConstants.XS_URI.equals(nsuri)
                || XQueryConstants.XSI_URI.equals(nsuri) || XQueryConstants.FN_URI.equals(nsuri)
                || XQueryConstants.XDT_URI.equals(nsuri)) {
            throw new SyntaxError("err:XQST0045");
        }
    }

    private static Type[] extractTypes(List<ParametricVariable> params) {
        final int size = params.size();
        final Type[] t = new Type[size];
        for(int i = 0; i < size; i++) {
            t[i] = params.get(i).getType();
        }
        return t;
    }
}