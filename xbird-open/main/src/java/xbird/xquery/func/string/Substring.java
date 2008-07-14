/*
 * @(#)$Id: Substring.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.literal.XNumber;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.*;

/**
 * fn:substring.
 * <DIV lang="en">
 * Returns the portion of the value of $sourceString beginning at the position 
 * indicated by the value of $startingLoc and continuing for the number of characters 
 * indicated by the value of $length.<br/>
 * Note that the first character of a string is located at position 1, not position 0.
 * <ul>
 * <li>fn:substring($sourceString as xs:string?, $startingLoc as xs:double) as xs:string</li>
 * <li>fn:substring($sourceString as xs:string?, $startingLoc as xs:double, $length as xs:double) as xs:string</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-substring
 */
public final class Substring extends BuiltInFunction {
    private static final long serialVersionUID = -7070216833028372984L;
    public static final String SYMBOL = "fn:substring";

    public Substring() {
        super(SYMBOL, StringType.STRING);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[4];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?"),
                DoubleType.DOUBLE });
        s[1] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?"),
                DoubleType.DOUBLE, DoubleType.DOUBLE });
        // workaround
        s[2] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?"),
                NumericType.getInstance() });
        s[3] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?"),
                NumericType.getInstance(), NumericType.getInstance() });
        return s;
    }

    public XString eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        final Item firstItem = argv.getItem(0);
        if(firstItem.isEmpty()) {
            // If the value of $sourceString is the empty sequence, the zero-length string is returned.
            return XString.valueOf("");
        }
        final String sourceString = firstItem.stringValue();
        final int maxSourceLen = sourceString.length();
        final Item secItem = argv.getItem(1);
        final XNumber secNum = (XNumber) secItem;
        // first character of a string is located at position 1, not position 0.
        final int startingLoc = Math.max(0, (int) Math.round(secNum.asDouble() - 1));
        final int endingLoc;
        final int arglen = argv.size();
        if(arglen == 3) {
            final Item thirdItem = argv.getItem(2);
            final XNumber thirdNum = (XNumber) thirdItem;
            final int length = (int) Math.round(thirdNum.asDouble());
            endingLoc = Math.max(0, Math.min(maxSourceLen, length));
        } else {
            endingLoc = maxSourceLen;
        }
        if(endingLoc <= startingLoc) {
            return XString.valueOf("");
        }
        final String ret = sourceString.substring(startingLoc, endingLoc);
        return XString.valueOf(ret);
    }

}
