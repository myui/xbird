/*
 * @(#)$Id: CodepointEqual.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;

/**
 * fn:codepoint-equal($comparand1 as xs:string?, $comparand2 as xs:string?) as xs:boolean?.
 * <DIV lang="en">
 * Returns true or false depending on whether the value of $comparand1 is equal 
 * to the value of $comparand2, according to the Unicode code point collation
 * (http://www.w3.org/2005/xpath-functions/collation/codepoint).
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CodepointEqual extends BuiltInFunction {
    private static final long serialVersionUID = 6044505376395565347L;
    
    public static final String SYMBOL = "fn:codepoint-equal";

    public CodepointEqual() {
        super(SYMBOL, TypeRegistry.safeGet("xs:boolean?"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        Type str = TypeRegistry.safeGet("xs:string?");
        s[0] = new FunctionSignature(getName(), new Type[] { str, str });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null && argv.size() == 2);
        // If either argument is the empty sequence, the result is the empty sequence.
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
        final boolean b = comparand1.equals(comparand2);
        return b ? BooleanValue.TRUE : BooleanValue.FALSE;
    }

}
