/*
 * @(#)$Id: EncodeForUri.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.util.xml.XMLUtils;
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
 * fn:encode-for-uri($uri-part as xs:string?) as xs:string.
 * <DIV lang="en">
 * This function should be used to process an xs:string to be used as a path segment in a URI.
 * It is invertible but not idempotent.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-encode-for-uri
 */
public final class EncodeForUri extends BuiltInFunction {
    private static final long serialVersionUID = -8525587551429352739L;
    
    public static final String SYMBOL = "fn:encode-for-uri";

    public EncodeForUri() {
        super(SYMBOL, StringType.STRING);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?") });
        return s;
    }

    public XString eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        // If $uri-part is the empty sequence, returns the zero-length string.
        Item firstItem = argv.getItem(0);
        if(firstItem.isEmpty()) {
            return XString.valueOf("");
        }
        assert (firstItem instanceof XString);
        String uripart = firstItem.stringValue();
        String escaped = XMLUtils.escapeUri(uripart, true);
        return XString.valueOf(escaped);
    }

}
