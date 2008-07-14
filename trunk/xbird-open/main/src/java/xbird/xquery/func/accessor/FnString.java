/*
 * @(#)$Id: FnString.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.accessor;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.StringType;

/**
 * fn:string.
 * <DIV lang="en">
 * Returns the value of $arg represented as a xs:string.
 * <ul>
 *  <li>fn:string() as xs:string</li>
 *  <li>fn:string($arg as item()?) as xs:string</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-string
 */
public final class FnString extends BuiltInFunction {
    private static final long serialVersionUID = 3288544957431628280L;
    public static final String SYMBOL = "fn:string";

    public FnString() {
        super(SYMBOL, StringType.STRING);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        s[0] = new FunctionSignature(getName());
        s[1] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("item()?") });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        final Item contextItem = dynEnv.contextItem();
        if(argv == null) {
            if(contextItem == null) {
                throw new DynamicError("err:XPDY0002", "ContextItem is not set");
            } else {
                // If no argument is supplied, this function returns the string value 
                // of the context item (.).
                final String sv = contextItem.stringValue();
                return XString.valueOf(sv);
            }
        }
        // If $arg is the empty sequence, the zero-length string is returned.
        if(argv.isEmpty()) {
            return XString.valueOf("");
        }
        final Item arg = argv.getItem(0);
        final IFocus<? extends Item> argItor = arg.iterator();
        if(argItor.hasNext()) {
            final Item firstItem = argItor.next();
            final Sequence ret;
            if(firstItem instanceof XQNode) {
                // If $arg is a node, the function returns the string-value of the node.
                final String sv = ((XQNode) firstItem).stringValue();
                ret = XString.valueOf(sv);
            } else if(firstItem instanceof AtomicValue) {
                // If $arg is an atomic value, then the function returns the same string as 
                // is returned by the expression "$arg cast as xs:string".
                final XString sv = ((AtomicValue) firstItem).<XString> castAs(StringType.STRING, dynEnv);
                ret = sv;
            } else {
                argItor.closeQuietly();
                throw new IllegalStateException("Illegal argument type: "
                        + arg.getClass().getName());
            }
            if(argItor.hasNext()) {
                argItor.closeQuietly();
                throw new DynamicError("argument should have zero or one element.");
            }
            argItor.closeQuietly();
            return ret;
        } else {
            argItor.closeQuietly();
            // If $arg is the empty sequence, the zero-length string is returned.
            return XString.valueOf("");
        }
    }

}
