/*
 * @(#)$Id: StringJoin.java 3619 2008-03-26 07:23:03Z yui $
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
 * fn:string-join($arg1 as xs:string*, $arg2 as xs:string) as xs:string.
 * <DIV lang="en">
 * Returns a xs:string created by concatenating the members of
 * the $arg1 sequence using $arg2 as a separator.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-string-join
 */
public final class StringJoin extends BuiltInFunction {
    private static final long serialVersionUID = -5504148115942668416L;
    public static final String SYMBOL = "fn:string-join";

    public StringJoin() {
        super(SYMBOL, StringType.STRING);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string*"),
                StringType.STRING });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        assert (argv != null && argv.size() == 2);
        final Item arg = argv.getItem(0);
        if(arg.isEmpty()) {
            // If the value of $arg1 is the empty sequence, the zero-length string is returned.
            return XString.valueOf("");
        }
        final Item sep = argv.getItem(1);
        final String separator = sep.stringValue();
        final StringBuilder buf = new StringBuilder(256);
        boolean first = true;
        for(Item it : arg) {
            assert (it instanceof XString);
            buf.append(it.stringValue());
            if(first) {
                first = false;
            } else {
                buf.append(separator);
            }
        }
        final String ret = buf.toString();
        return XString.valueOf(ret);
    }

}
