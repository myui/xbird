/*
 * @(#)$Id: Tokenize.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.ObjectStreamException;
import java.util.regex.*;

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
 * fn:tokenize.
 * <DIV lang="en">
 * This function breaks the $input string into a sequence of strings,
 * treating any substring that matches $pattern as a separator.
 * The separators themselves are not returned.
 * <ul>
 * <li>fn:tokenize($input as xs:string?, $pattern as xs:string) as xs:string*</li>
 * <li>fn:tokenize($input as xs:string?, $pattern as xs:string, $flags as xs:string) as xs:string*</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-tokenize
 */
public final class Tokenize extends BuiltInFunction {
    private static final long serialVersionUID = 5015360216397174624L;
    public static final String SYMBOL = "fn:tokenize";

    public Tokenize() {
        super(SYMBOL, TypeRegistry.safeGet("xs:string*"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        Type strq = TypeRegistry.safeGet("xs:string?");
        s[0] = new FunctionSignature(getName(), new Type[] { strq, StringType.STRING });
        s[1] = new FunctionSignature(getName(), new Type[] { strq, StringType.STRING,
                StringType.STRING });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null);
        final int arglen = argv.size();
        assert (arglen == 2 || arglen == 3);
        Item firstItem = argv.getItem(0);
        // If $input is the empty sequence, or if $input is the zero-length string, the result is the empty sequence.
        if(firstItem.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        assert (firstItem instanceof XString);
        String input = firstItem.stringValue();
        Item secondItem = argv.getItem(1);
        assert (secondItem instanceof XString);
        String pattern = secondItem.stringValue();
        int flags = Pattern.UNIX_LINES;
        if(arglen == 3) {
            Item thirdItem = argv.getItem(2);
            assert (thirdItem instanceof XString);
            String flagsStr = thirdItem.stringValue();
            flags = Matches.convertFlags(flagsStr);
        }
        final Pattern compiled;
        try {
            compiled = Pattern.compile(pattern, flags);
        } catch (PatternSyntaxException pse) {
            throw new DynamicError("err:FORX0002", "Invalid pattern: " + pattern);
        }
        if(compiled.matcher("").matches()) {
            // If the supplied $pattern matches a zero-length string, that is, 
            // if fn:matches("", $pattern, $flags) returns true, then an error is raised.            
            throw new DynamicError("err:FORX0003", "Specified pattern is illegally matched to zero-length string: "
                    + pattern);
        }
        return new TokenizerEmuration(input, compiled, dynEnv);
    }

    private static final class TokenizerEmuration extends AbstractSequence<XString> {

        private final String input;
        private Pattern pattern;
        private transient Matcher matcher;

        public TokenizerEmuration() {
            super(DynamicContext.DUMMY);
            this.input = null;
            this.matcher = null;
        }

        TokenizerEmuration(String input, Pattern pattern, DynamicContext dynEnv) {
            super(dynEnv);
            assert (input != null && pattern != null);
            this.input = input;
            this.pattern = pattern;
            this.matcher = pattern.matcher(input);
        }

        public boolean next(IFocus focus) throws XQueryException {
            if(matcher.find()) {
                String matched = input.substring(matcher.start(), matcher.end());
                focus.setContextItem(XString.valueOf(matched));
            }
            return false;
        }

        public Type getType() {
            return TypeRegistry.safeGet("xs:string*");
        }

        @Override
        public IFocus<XString> iterator() {
            matcher.reset();
            return super.iterator();
        }

        private Object readResolve() throws ObjectStreamException {
            assert (pattern != null);
            assert (input != null);
            this.matcher = pattern.matcher(input);
            return this;
        }

    }

}
