/*
 * @(#)$Id: SubstringMatch.java 3619 2008-03-26 07:23:03Z yui $
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
import java.text.RuleBasedCollator;

import xbird.util.resource.CollationUtils;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.BooleanType;
import xbird.xquery.type.xs.StringType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#substring.functions
 */
public abstract class SubstringMatch extends BuiltInFunction {

    protected SubstringMatch(String funcName, Type retType) {
        super(funcName, retType);
    }

    public final Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        final int arglen = argv.size();
        final Item firstItem = argv.getItem(0);
        final Item secondItem = argv.getItem(1);
        final String src = firstItem.stringValue();
        if(src.length() == 0) {
            return BooleanValue.FALSE;
        }
        final String pattern = secondItem.stringValue();
        if(pattern.length() == 0) {
            return BooleanValue.TRUE;
        }
        final RuleBasedCollator collator;
        if(arglen == 3) {
            Item thirdItem = argv.getItem(2);
            String collation = thirdItem.stringValue();
            Collator coll = CollationUtils.resolve(collation, dynEnv.getStaticContext());
            if(!(coll instanceof RuleBasedCollator)) {
                // If the specified collation does not support collation units an error
                // may be raised [err:FOCH0004].
                throw new DynamicError("err:FOCH0004", "Collator for the collation `" + collation
                        + "` is expected to be rule based collator, but was " + coll);
            }
            collator = (RuleBasedCollator) coll;
        } else {
            collator = null;
        }
        final AtomicValue res = match(src, pattern, collator);
        return res;
    }

    protected abstract AtomicValue match(String src, String pattern, RuleBasedCollator collator);

    /**
     * fn:contains.
     * <DIV lang="en">
     * Returns an xs:boolean indicating whether or not the value of $arg1 contains 
     * at least one sequence of collation units that provides a minimal match to 
     * the collation units in the value of $arg2, according to the collation that is used.
     * <ul>
     * <li>fn:contains($arg1 as xs:string?, $arg2 as xs:string?) as xs:boolean</li>
     * <li>fn:contains($arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:boolean</li>
     * </ul>
     * </DIV>
     * <DIV lang="ja"></DIV>
     * 
     * @link http://www.w3.org/TR/xquery-operators/#func-contains
     */
    public static final class Contains extends SubstringMatch {
        private static final long serialVersionUID = -3761170192277907129L;
        public static final String SYMBOL = "fn:contains";

        public Contains() {
            super(SYMBOL, BooleanType.BOOLEAN);
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[2];
            Type strq = TypeRegistry.safeGet("xs:string?");
            s[0] = new FunctionSignature(getName(), new Type[] { strq, strq });
            s[1] = new FunctionSignature(getName(), new Type[] { strq, strq, StringType.STRING });
            return s;
        }

        protected BooleanValue match(final String src, final String pattern, final RuleBasedCollator collator) {
            final int index = CollationUtils.indexOf(src, pattern, collator);
            return index >= 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
        }
    }

    /**
     * fn:starts-with.
     * <DIV lang="en">
     * Returns an xs:boolean indicating whether or not the value of $arg1 starts
     * with a sequence of collation units that provides a minimal match to the collation units
     * of $arg2 according to the collation that is used.
     * <ul>
     * <li>fn:starts-with($arg1 as xs:string?, $arg2 as xs:string?) as xs:boolean</li>
     * <li>fn:starts-with($arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:boolean</li>
     * </ul>
     * </DIV>
     * <DIV lang="ja"></DIV>
     * 
     * @link http://www.w3.org/TR/xquery-operators/#func-starts-with
     */
    public static final class StartWith extends SubstringMatch {
        private static final long serialVersionUID = 1728674981701400271L;
        public static final String SYMBOL = "fn:starts-with";

        public StartWith() {
            super(SYMBOL, BooleanType.BOOLEAN);
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[2];
            Type strq = TypeRegistry.safeGet("xs:string?");
            s[0] = new FunctionSignature(getName(), new Type[] { strq, strq });
            s[1] = new FunctionSignature(getName(), new Type[] { strq, strq, StringType.STRING });
            return s;
        }

        protected BooleanValue match(String src, String pattern, RuleBasedCollator collator) {
            final int index = CollationUtils.indexOf(src, pattern, collator);
            return index == 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
        }
    }

    /**
     * fn:ends-with.
     * <DIV lang="en">
     * <ul>
     * <li>fn:ends-with($arg1 as xs:string?, $arg2 as xs:string?) as xs:boolean</li>
     * <li>fn:ends-with($arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:boolean</li>
     * </ul>
     * </DIV>
     * <DIV lang="ja"></DIV>
     * 
     * @link http://www.w3.org/TR/xquery-operators/#func-ends-with
     */
    public static final class EndWith extends SubstringMatch {
        private static final long serialVersionUID = -4696743940774765581L;
        public static final String SYMBOL = "fn:ends-with";

        public EndWith() {
            super(SYMBOL, BooleanType.BOOLEAN);
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[2];
            Type strq = TypeRegistry.safeGet("xs:string?");
            s[0] = new FunctionSignature(getName(), new Type[] { strq, strq });
            s[1] = new FunctionSignature(getName(), new Type[] { strq, strq, StringType.STRING });
            return s;
        }

        protected BooleanValue match(String src, String pattern, RuleBasedCollator collator) {
            final boolean b = CollationUtils.endsWith(src, pattern, collator);
            return b ? BooleanValue.TRUE : BooleanValue.FALSE;
        }
    }

    /**
     * fn:substring-before.
     * <DIV lang="en">
     * <ul>
     * <li>fn:substring-before($arg1 as xs:string?, $arg2 as xs:string?) as xs:string</li>
     * <li>fn:substring-before($arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:string</li>
     * </ul>
     * </DIV>
     * <DIV lang="ja"></DIV>
     * 
     * @link http://www.w3.org/TR/xquery-operators/#func-substring-before
     */
    public static final class SubstringBefore extends SubstringMatch {
        private static final long serialVersionUID = -4205864900054486015L;
        public static final String SYMBOL = "fn:substring-before";

        public SubstringBefore() {
            super(SYMBOL, StringType.STRING);
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[2];
            Type strq = TypeRegistry.safeGet("xs:string?");
            s[0] = new FunctionSignature(getName(), new Type[] { strq, strq });
            s[1] = new FunctionSignature(getName(), new Type[] { strq, strq, StringType.STRING });
            return s;
        }

        protected XString match(String src, String pattern, RuleBasedCollator collator) {
            final int index = CollationUtils.indexOf(src, pattern, collator);
            return index <= 0 ? XString.valueOf("") : XString.valueOf(src.substring(0, index));
        }
    }

    /**
     * fn:substring-after.
     * <DIV lang="en">
     * <ul>
     * <li>fn:substring-after($arg1 as xs:string?, $arg2 as xs:string?) as xs:string</li>
     * <li>fn:substring-after($arg1 as xs:string?, $arg2 as xs:string?, $collation as xs:string) as xs:string</li>
     * </ul>
     * </DIV>
     * <DIV lang="ja"></DIV>
     * 
     * @link http://www.w3.org/TR/xquery-operators/#func-substring-after
     */
    public static final class SubstringAfter extends SubstringMatch {
        private static final long serialVersionUID = 1090442728849712460L;
        public static final String SYMBOL = "fn:substring-after";

        public SubstringAfter() {
            super(SYMBOL, StringType.STRING);
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[2];
            Type strq = TypeRegistry.safeGet("xs:string?");
            s[0] = new FunctionSignature(getName(), new Type[] { strq, strq });
            s[1] = new FunctionSignature(getName(), new Type[] { strq, strq, StringType.STRING });
            return s;
        }

        protected XString match(String src, String pattern, RuleBasedCollator collator) {
            final int index = CollationUtils.indexOf(src, pattern, collator);
            if(index == -1) {
                return new XString("");
            } else {
                final String res = src.substring(index + pattern.length());
                return new XString(res);
            }
        }

    }
}
