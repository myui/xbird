/*
 * @(#)$Id: NormalizeUnicode.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.DynamicError;
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

import com.ibm.icu.text.Normalizer;

/**
 * fn:normalize-unicode.
 * <DIV lang="en">
 * Returns the value of $arg normalized according to the normalization criteria 
 * for a normalization form identified by the value of $normalizationForm.
 * <ul>
 * <li>fn:normalize-unicode($arg as xs:string?) as xs:string</li>
 * <li>fn:normalize-unicode($arg as xs:string?, $normalizationForm as xs:string) as xs:string</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-normalize-unicode
 */
public final class NormalizeUnicode extends BuiltInFunction {
    private static final long serialVersionUID = 2651492084383157264L;
    public static final String SYMBOL = "fn:normalize-unicode";

    public NormalizeUnicode() {
        super(SYMBOL, StringType.STRING);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?") });
        s[1] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?"),
                StringType.STRING });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null);
        final int arglen = argv.size();
        assert (arglen == 1 || arglen == 2);
        Item firstItem = argv.getItem(0);
        // If the value of $arg is the empty sequence, returns the zero-length string.
        if(firstItem.isEmpty()) {
            return XString.valueOf("");
        }
        String arg = firstItem.stringValue();
        // If the $normalizationForm is absent, as in the first format above, 
        // it shall be assumed to be "NFC"
        String nform = "NFC";
        if(arglen == 2) {
            Item secondItem = argv.getItem(1);
            nform = secondItem.stringValue();
        }
        final String normed;
        if("NFC".equalsIgnoreCase(nform)) {
            normed = Normalizer.normalize(arg, Normalizer.NFC);
        } else if("NFD".equalsIgnoreCase(nform)) {
            normed = Normalizer.normalize(arg, Normalizer.NFD);
        } else if("NFKC".equalsIgnoreCase(nform)) {
            normed = Normalizer.normalize(arg, Normalizer.NFKC);
        } else if("NFKD".equalsIgnoreCase(nform) || "FULLY-NORMALIZED".equalsIgnoreCase(nform)) {
            normed = Normalizer.normalize(arg, Normalizer.NFKD);
        } else {
            // TODO "FULLY-NORMALIZED"
            throw new DynamicError("err:FOCH0003", "Unsupported normalizationForm: " + nform);
        }
        return XString.valueOf(normed);
    }

}
