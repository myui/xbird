/*
 * @(#)$Id: Compare.java 3619 2008-03-26 07:23:03Z yui $
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

import java.text.Collator;

import xbird.util.resource.CollationUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.StringType;

/**
 * fn:compare.
 * <DIV lang="en">
 * Returns -1, 0, or 1, depending on whether the value of the $comparand1 is respectively 
 * less than, equal to, or greater than the value of $comparand2, according to the rules 
 * of the collation that is used.
 * <ul>
 * <li>fn:compare($comparand1 as xs:string?, $comparand2 as xs:string?) as xs:integer?</li>
 * <li>fn:compare($comparand1 as xs:string?, $comparand2 as xs:string?, $collation as xs:string) as xs:integer?</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-compare
 */
public final class Compare extends BuiltInFunction {
    private static final long serialVersionUID = 3703985794159908946L;

    public static final String SYMBOL = "fn:compare";

    public Compare() {
        super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] t = new FunctionSignature[2];
        Type str = TypeRegistry.safeGet("xs:string?");
        t[0] = new FunctionSignature(getName(), new Type[] { str, str });
        t[1] = new FunctionSignature(getName(), new Type[] { str, str, StringType.STRING });
        return t;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        assert (argv != null);
        Item firstItem = argv.getItem(0);
        if(firstItem.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        Item secondItem = argv.getItem(1);
        if(secondItem.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        String comparand1 = firstItem.stringValue();
        String comparand2 = secondItem.stringValue();
        final int cmp;
        final int arglen = argv.size();
        assert (arglen == 2 || arglen == 3);
        if(arglen == 3) {
            Item thirdItem = argv.getItem(2);
            String collation = thirdItem.stringValue();
            Collator collator = CollationUtils.resolve(collation, dynEnv.getStaticContext());
            cmp = collator.compare(comparand1, comparand2);
        } else {
            cmp = comparand1.compareTo(comparand2);
        }
        return cmp == 0 ? XInteger.valueOf(0) : (cmp > 0 ? XInteger.valueOf(1)
                : XInteger.valueOf(-1));
    }

}
