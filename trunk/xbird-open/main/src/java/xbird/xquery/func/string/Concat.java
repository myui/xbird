/*
 * @(#)$Id: Concat.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.string;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.StringType;

/**
 * fn:concat($arg1 as xdt:anyAtomicType?, $arg2 as xdt:anyAtomicType?, ...) as xs:string.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-concat
 */
public final class Concat extends BuiltInFunction {
    private static final long serialVersionUID = -5481034539434462332L;
    
    public static final String SYMBOL = "fn:concat";

    public Concat() {
        super(SYMBOL, StringType.STRING);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[4];
        Type anyatom = TypeRegistry.safeGet("xs:anyAtomicType?");
        s[0] = new FunctionSignature(getName(), new Type[] { anyatom, anyatom });
        s[1] = new FunctionSignature(getName(), new Type[] { anyatom, anyatom, anyatom });
        s[2] = new FunctionSignature(getName(), new Type[] { anyatom, anyatom, anyatom, anyatom });
        s[3] = new FunctionSignature(getName(), new Type[] { anyatom, anyatom, anyatom, anyatom,
                anyatom });
        // TODO varargs
        return s;
    }

    public XString eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        if(argv == null) {
            return XString.valueOf("");
        }
        final StringBuilder buf = new StringBuilder(256);
        final int arglen = argv.size();
        for(int i = 0; i < arglen; i++) {
            Item arg = argv.getItem(i);
            String argStr = arg.stringValue();
            if(argStr.length() > 0) {
                buf.append(argStr);
            }
        }
        final String ret = buf.toString();
        return XString.valueOf(ret);
    }
}
