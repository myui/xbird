/*
 * @(#)$Id: StringToCodepoint.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;

/**
 * fn:string-to-codepoints($arg as xs:string?) as xs:integer*.
 * <DIV lang="en">
 * Returns the sequence of Unicode code points that constitute an xs:string.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-string-to-codepoints
 */
public final class StringToCodepoint extends BuiltInFunction {
    private static final long serialVersionUID = -6260437736207861399L;
    public static final String SYMBOL = "fn:string-to-codepoints";

    public StringToCodepoint() {
        super(SYMBOL, TypeRegistry.safeGet("xs:integer*"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?") });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null && argv.size() == 1);
        Item firstItem = argv.getItem(0);
        if(firstItem.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        assert (firstItem instanceof XString);
        final String arg = firstItem.stringValue();
        return new CodepointEmuration(arg, dynEnv);
    }

    private static class CodepointEmuration extends AbstractSequence<XInteger> {

        private final String _src;

        CodepointEmuration(String src, DynamicContext dynEnv) {
            super(dynEnv);
            assert (src != null);
            this._src = src;
        }

        public boolean next(IFocus focus) throws XQueryException {
            int pos = focus.getContextPosition();
            final int srclen = _src.length();
            if(pos < srclen) {
                int cp = _src.codePointAt(pos);
                focus.setContextItem(XInteger.valueOf(cp));
                return true;
            } else {
                return false;
            }
        }

        public Type getType() {
            return TypeRegistry.safeGet("xs:integer*");
        }

        @Override
        public boolean isEmpty() {
            return _src.length() > 0;
        }

    }

}
