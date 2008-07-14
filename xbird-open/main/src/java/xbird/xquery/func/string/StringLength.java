/*
 * @(#)$Id: StringLength.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.IntegerType;

/**
 * fn:string-length.
 * <DIV lang="en">
 * Returns an xs:integer equal to the length in characters of the value of $arg.
 * <ul>
 * <li>fn:string-length() as xs:integer</li>
 * <li>fn:string-length($arg as xs:string?) as xs:integer</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-string-length
 */
public final class StringLength extends BuiltInFunction {
    private static final long serialVersionUID = -9075137656188960355L;
    public static final String SYMBOL = "fn:string-length";

    public StringLength() {
        super(SYMBOL, IntegerType.INTEGER);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        s[0] = new FunctionSignature(getName());
        s[1] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?") });
        return s;
    }

    public XInteger eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        if(argv == null) {
            // If no argument is supplied, $arg defaults to the string value of the context item (.).
            // If no argument is supplied and the context item is undefined an error is raised: [err:XPDY0002].
            Item contextItem = dynEnv.contextItem();
            if(contextItem == null) {
                throw new DynamicError("err:XPDY0002", "ContextItem is not set");
            }
            String sv = contextItem.stringValue();
            // If the value of $arg is the empty sequence, the xs:integer 0 is returned.
            return XInteger.valueOf(sv.length());
        }
        Item first = argv.getItem(0);
        final String argstr = first.stringValue();
        return XInteger.valueOf(argstr.length());
    }

}
