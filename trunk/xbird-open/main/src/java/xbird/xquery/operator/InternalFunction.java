/*
 * @(#)$Id: InternalFunction.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.operator;

import java.io.Serializable;

import xbird.util.struct.Pair;
import xbird.xquery.*;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.UntypedAtomicType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class InternalFunction implements Serializable {

    private final String funcName;
    private final Pair<AtomicType[], AtomicType>[] sigs;
    protected int matchedSig = -1; // changes only when matched signature is found.

    public InternalFunction(String funcName) {
        this.funcName = funcName;
        this.sigs = signatures();
    }

    //--------------------------------------------
    // getter/setter

    public String getName() {
        return funcName;
    }

    public Type[] getArgumentsType() throws TypeError {
        if(matchedSig > 0) {
            return sigs[matchedSig].first;
        } else {
            throw new TypeError("Operator for " + funcName + " is not resolved.");
        }
    }

    public Type setReturnType(AtomicType t) throws TypeError {
        if(matchedSig > 0) {
            this.sigs[matchedSig].second = t;
        }
        throw new TypeError("Operator for " + funcName + " is not resolved.");
    }

    public Type getReturnType() throws TypeError {
        if(matchedSig < 0) {
            throw new TypeError("Operator for " + funcName + " is not resolved.");
        }
        assert (matchedSig <= sigs.length) : "Invalid Signature Index was specified: " + matchedSig;
        return sigs[matchedSig].second;

    }

    /**
     * Gets the function signatures.
     */
    protected abstract Pair<AtomicType[], AtomicType>[] signatures();

    //--------------------------------------------
    // static analysis/dynamic evaluation

    public InternalFunction staticAnalysis(StaticContext context, XQExpression... params)
            throws XQueryException {
        if(matchedSig != -1) {
            return this; // already resolved
        }
        return resolve(extractTypes(context, params));
    }

    public abstract Sequence eval(DynamicContext ctxt, Item... args) throws XQueryException;

    //--------------------------------------------
    // helper

    protected InternalFunction resolve(Type[] argTypes) throws TypeError {
        int idx = -1;
        for(Pair<AtomicType[], AtomicType> s : sigs) {
            idx++;
            int unknowns = 0;
            AtomicType[] expected = s.first;
            final int last = expected.length - 1;
            for(int j = 0; j < expected.length; j++) {
                Type argtype = argTypes[j];
                Type extype = expected[j];
                final boolean eq = (argtype == extype);
                if(eq || TypeUtil.subtypeOf(argtype, extype)) {//REVIEWME ok
                    if(!eq && argtype instanceof AtomicType) {
                        if(argtype == UntypedAtomicType.UNTYPED_ATOMIC
                                || argtype == AtomicType.ANY_ATOMIC_TYPE) {
                            unknowns++;
                        } else if(extype == UntypedAtomicType.UNTYPED_ATOMIC
                                || extype == AtomicType.ANY_ATOMIC_TYPE) {
                            break;
                        }
                    }
                    if(j == last) { // resolved
                        if(unknowns > 0 && unknowns != expected.length) {
                            break; // either the types are not known yet, or they are wrong
                        }
                        this.matchedSig = idx;
                        return this;
                    }
                } else {
                    break; // not match
                }
            }
        }
        // reports error
        final StringBuilder b = new StringBuilder(192);
        b.append("Internal function for ").append(funcName).append('(');
        for(int k = 0; k < argTypes.length; k++) {
            if(k != 0) {
                b.append(',');
            }
            b.append(argTypes[k]);
        }
        b.append(") is not found");
        throw new TypeError(TypeError.TYPE_ERROR_CODE, b.toString());
    }

    private static Type[] extractTypes(StaticContext context, XQExpression... params)
            throws XQueryException {
        final int size = params.length;
        final Type[] t = new Type[size];
        for(int i = 0; i < size; i++) {
            params[i].staticAnalysis(context);
            final Type type = params[i].getType();
            Type prime = type.prime();
            if(prime == Type.NONE) {
                throw new DynamicError("err:FOER0000", "User-requested error");
            }
            t[i] = prime;
        }
        return t;
    }

}
