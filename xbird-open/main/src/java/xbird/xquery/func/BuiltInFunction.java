/*
 * @(#)$Id: BuiltInFunction.java 3619 2008-03-26 07:23:03Z yui $
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
import java.util.List;

import xbird.xquery.TypeError;
import xbird.xquery.XQueryConstants;
import xbird.xquery.XQueryException;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class BuiltInFunction extends AbstractFunction {
    public static final String EXT_NSPREFIX = "ext";
    public static final String EXT_NAMESPACE_URI = "http://code.google.com/p/xbird/";

    protected/* final */FunctionSignature[] _signs;

    public BuiltInFunction(String funcName, Type retType) {
        this(resolve(funcName), retType);
    }

    protected BuiltInFunction(QualifiedName funcName, Type retType) {
        super(funcName, retType);
        this._signs = signatures();
    }

    public BuiltInFunction() {
        super();
    }// for Externalizable

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

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

        final FunctionSignature[] signs = _signs;
        final int numSigns = signs.length;
        out.writeInt(numSigns);
        for(int i = 0; i < numSigns; i++) {
            signs[i].writeExternal(out);
        }
    }

    public void writeTo(ObjectOutput out) throws IOException {
        final QualifiedName name = getName();
        if(name == null) {
            throw new IllegalStateException();
        }
        name.writeExternal(out);
    }

    public static BuiltInFunction readFrom(ObjectInput in) throws IOException {
        final QualifiedName name = QualifiedName.readFrom(in);
        final BuiltInFunction builtIn = PredefinedFunctions.lookup(name);
        if(builtIn == null) {
            throw new IllegalStateException("Function not found: " + name);
        }
        return builtIn;
    }

    protected Function strictCheckArguments(final StaticContext context, final List<? extends XQExpression> params)
            throws XQueryException {
        final int arity = params.size();
        final FunctionSignature sig = getFunctionSignature(arity);
        final Type[] expectedTypes = sig.getArgumentTypes();
        assert (expectedTypes.length == arity);
        for(int i = 0; i < arity; i++) {
            Type expected = expectedTypes[i];
            XQExpression expr = params.get(i);
            Type actual = expr.getType();
            if(!TypeUtil.subtypeOf(actual, expected)) {// REVIEWME ok
                throw new TypeError("err:XPTY0004", i + "th parameter type '" + actual
                        + "' does not match to the expected argument type '" + expected + '\'');
            }
        }
        return this;
    }

    public boolean isReusable() {
        return true;
    }

    public void visit(XQueryParserVisitor visitor, XQueryContext ctxt) throws XQueryException {
        visitor.visit(this, ctxt);
    }

    protected static final QualifiedName resolve(String fname) {
        final String s[] = fname.split(":");
        if(s.length != 2) {
            throw new IllegalArgumentException("Invalid type: " + fname);
        }
        final String uri;
        if(XQueryConstants.FN.equals(s[0])) {
            uri = XQueryConstants.FN_URI;
        } else if(XQueryConstants.XS.equals(s[0])) {
            uri = XQueryConstants.XS_URI;
        } else if(XQueryConstants.XDT.equals(s[0])) {
            uri = XQueryConstants.XDT_URI;
        } else if(EXT_NSPREFIX.equals(s[0])) {
            uri = EXT_NAMESPACE_URI;
        } else {
            throw new IllegalArgumentException("Invalid type: " + fname);
        }
        return QNameTable.instantiate(uri, s[1], s[0]);
    }

    public FunctionSignature[] getFunctionSignatures() {
        return _signs;
    }

    protected abstract FunctionSignature[] signatures();

}
