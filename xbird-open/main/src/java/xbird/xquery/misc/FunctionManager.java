/*
 * @(#)$Id:FunctionManager.java 2335 2007-07-17 04:14:15Z yui $
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
package xbird.xquery.misc;

import java.util.*;

import xbird.xquery.XQueryConstants;
import xbird.xquery.XQueryException;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.func.*;
import xbird.xquery.func.ext.JavaFunction;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class FunctionManager {

    private final Map<FunctionSignature, UserFunction> functions = new HashMap<FunctionSignature, UserFunction>();

    public FunctionManager() {}

    public void declareLocalFunction(UserFunction func) throws XQueryException {
        FunctionSignature[] sigs = func.getFunctionSignatures();
        if(sigs.length == 0) {
            throw new IllegalStateException("Function does not have signatures.");
        }
        for(int i = 0; i < sigs.length; i++) {
            if(functions.containsKey(sigs[i])) {
                throw new XQueryException("err:XQST0034", "Function already defined: "
                        + func.getName());
            }
            functions.put(sigs[i], func);
        }
    }

    public Collection<UserFunction> getLocalFunctions() {
        return functions.values();
    }

    public Function lookupFunction(QualifiedName funcName, List<? extends XQExpression> params) {
        // step1-1. bind java function.
        if(funcName.getNamespaceURI().startsWith(JavaFunction.PROTOCOL)) {
            JavaFunction func = new JavaFunction(funcName);
            return func;
        }
        // step1-2. pre-defined functions lookup.
        final int arity = params.size();
        if(!XQueryConstants.LOCAL_URI.equals(funcName.getNamespaceURI())) {
            BuiltInFunction func = PredefinedFunctions.lookup(funcName);
            if(func != null) {
                FunctionSignature[] sigs = func.getFunctionSignatures();
                for(int i = 0; i < sigs.length; i++) {
                    if(sigs[i].getArity() == arity) {
                        return func;
                    }
                }
            }
        }
        // step2. user-defined functions lookup
        final FunctionSignature sig = new FunctionSignature(funcName, arity);
        return functions.get(sig);
    }

    @Override
    public String toString() {
        return functions.toString();
    }
}