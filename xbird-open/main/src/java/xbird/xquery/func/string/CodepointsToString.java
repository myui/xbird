/*
 * @(#)$Id: CodepointsToString.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.Iterator;

import xbird.util.xml.XMLUtils;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.StringType;

/**
 * fn:codepoints-to-string($arg as xs:integer*) as xs:string.
 * <DIV lang="en">
 * Creates an xs:string from a sequence of Unicode code points.
 * Returns the zero-length string if $arg is the empty sequence. 
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-codepoints-to-string
 */
public final class CodepointsToString extends BuiltInFunction {
    private static final long serialVersionUID = -8033267999983702537L;
    
    public static final String SYMBOL = "fn:codepoints-to-string";

    public CodepointsToString() {
        super(SYMBOL, StringType.STRING);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:integer*") });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null && argv.size() == 1);
        Item firstItem = argv.getItem(0);
        if(firstItem.isEmpty()) {
            // Returns the zero-length string if $arg is the empty sequence.
            return ValueSequence.EMPTY_SEQUENCE;
        }
        // If any of the code points in $arg is not a legal XML character, 
        // an error is raised [err:FOCH0001].
        Iterator<? extends Item> argItor = firstItem.iterator();
        assert (argItor.hasNext());
        final StringBuilder buf = new StringBuilder(64);
        while(argItor.hasNext()) {
            Item it = argItor.next();
            assert (it instanceof XInteger);
            final int cp = (int) ((XInteger) it).getValue();
            if(!XMLUtils.isValid(cp)) {
                throw new DynamicError("err:FOCH0001", "Invalid XML char was detected: `" + cp
                        + "`!");
            }
            final char[] chars = Character.toChars(cp);
            buf.append(chars);
        }
        final String ret = buf.toString();
        return XString.valueOf(ret);
    }
}
