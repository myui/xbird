/*
 * @(#)$Id: Translate.java 3619 2008-03-26 07:23:03Z yui $
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
 * fn:translate($arg as xs:string?, $mapString as xs:string, $transString as xs:string) as xs:string.
 * <DIV lang="en">
 * Returns the value of $arg modified so that every character in the value of $arg 
 * that occurs at some position N in the value of $mapString has been replaced by 
 * the character that occurs at position N in the value of $transString.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-translate
 */
public final class Translate extends BuiltInFunction {
    private static final long serialVersionUID = -4608970269678484243L;
    public static final String SYMBOL = "fn:translate";

    /** high surrogate (0xD800-0xDBFF) */
    private static final char[] HIGH_SURROGATE_RANGE = { 0xD800, 0xDBFF };

    /** low surrogate (0xDC00-0xDFFF) */
    private static final char[] IND_LOW_SURROGATE_RANGE = { 0xDC00, 0xDFFF };

    public Translate() {
        super(SYMBOL, StringType.STRING);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?"),
                StringType.STRING, StringType.STRING });
        return s;
    }

    public XString eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        Item firstItem = argv.getItem(0);
        if(firstItem.isEmpty()) {
            // If the value of $arg is the empty sequence, the zero-length string is returned.
            return XString.valueOf("");
        }
        Item secItem = argv.getItem(1);
        Item thirdItem = argv.getItem(2);
        String src = firstItem.stringValue();
        String map = secItem.stringValue();
        String trans = thirdItem.stringValue();
        String translated = translate(src, map, trans);
        return XString.valueOf(translated);
    }

    private static String translate(String src, String map, String trans) {
        final int srclen = src.length();
        final StringBuilder buf = new StringBuilder(srclen);
        final int translen = trans.length();
        for(int i = 0; i < srclen; i++) {
            final char srcc = src.charAt(i);
            final int cindex = map.indexOf(srcc);
            if(cindex < translen) {
                final char newchar = cindex < 0 ? srcc : trans.charAt(cindex);
                if(srcc >= HIGH_SURROGATE_RANGE[0] && srcc <= HIGH_SURROGATE_RANGE[1]) {
                    assert (srclen >= i + 1);
                    final char lowChar = src.charAt(i + 1);
                    assert (lowChar >= HIGH_SURROGATE_RANGE[0] && lowChar <= HIGH_SURROGATE_RANGE[1]);
                    final int entity = (srclen - HIGH_SURROGATE_RANGE[0]) * 0x400
                            + (lowChar - IND_LOW_SURROGATE_RANGE[0]) + 0x10000 /* BMP */;
                    buf.append(entity);
                } else {
                    buf.append(newchar);
                }
            }
        }
        return buf.toString();
    }

}
