/*
 * @(#)$Id: Replace.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.List;
import java.util.regex.*;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.func.*;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.StringType;

/**
 * The function returns the xs:string that is obtained by replacing each non-overlapping substring of $input
 * that matches the given $pattern with an occurrence of the $replacement string.
 * <DIV lang="en">
 * <ul>
 * <li>fn:replace($input as xs:string?, $pattern as xs:string, $replacement as xs:string) as xs:string</li>
 * <li>fn:replace($input as xs:string?, $pattern as xs:string, $replacement as xs:string, $flags as xs:string) as xs:string</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-replace
 */
public final class Replace extends BuiltInFunction {
    private static final long serialVersionUID = -1194625445370293412L;
    public static final String SYMBOL = "fn:replace";

    public Replace() {
        super(SYMBOL, StringType.STRING);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        Type strq = TypeRegistry.safeGet("xs:string?");
        s[0] = new FunctionSignature(getName(), new Type[] { strq, StringType.STRING,
                StringType.STRING });
        s[1] = new FunctionSignature(getName(), new Type[] { strq, StringType.STRING,
                StringType.STRING, StringType.STRING });
        return s;
    }

    @Override
    public Function staticAnalysis(StaticContext context, List<? extends XQExpression> params)
            throws XQueryException {
        // TODO performance pre-compile
        return super.staticAnalysis(context, params);
    }

    public XString eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        final int arglen = argv.size();
        Item firstItem = argv.getItem(0);
        // If $input is the empty sequence, it is interpreted as the zero-length string.
        String input = firstItem.isEmpty() ? "" : firstItem.stringValue();
        Item secondItem = argv.getItem(1);
        String pattern = secondItem.stringValue();
        Item thirdItem = argv.getItem(2);
        String replacement = thirdItem.stringValue();
        checkReplacement(replacement);
        int flags = Pattern.UNIX_LINES;
        if(arglen == 4) {
            Item forthItem = argv.getItem(3);
            String flagsStr = forthItem.stringValue();
            flags = Matches.convertFlags(flagsStr);
        }
        final Pattern compiled;
        try {
            compiled = Pattern.compile(pattern, flags);
        } catch (PatternSyntaxException pse) {
            throw new DynamicError("err:FORX0002", "Invalid pattern: " + pattern);
        }
        if(compiled.matcher("").matches()) {
            // An error is raised [err:FORX0003] if the pattern matches a zero-length string, 
            // that is, if the expression fn:matches("", $pattern, $flags) returns true. 
            // It is not an error, however, if a captured substring is zero-length.
            throw new DynamicError("err:FORX0003", "Specified pattern is illegally matched to zero-length string: "
                    + pattern);
        }
        Matcher matcher = compiled.matcher(input);
        final String replaced = matcher.replaceAll(replacement);
        return XString.valueOf(replaced);
    }

    private static void checkReplacement(String replacement) throws DynamicError {
        assert (replacement != null);
        final int len = replacement.length();
        for(int i = 0; i < len; i++) {
            char c = replacement.charAt(i);
            if(c == '$') {
                // An error is raised [err:FORX0004] if the value of $replacement contains 
                // a "$" character that is not immediately followed by a digit 0-9 
                // and not immediately preceded by a "\".
                if(len >= i + 1) {
                    char next = replacement.charAt(i + 1);
                    if(Character.isDigit(next)) {
                        continue;
                    }
                }
                throw new DynamicError("err:FORX0004", "Invalid replacement expr: " + replacement);
            } else if(c == '\\') {
                if(len >= i + 1) {
                    char next = replacement.charAt(i + 1);
                    // An error is raised [err:FORX0004] if the value of $replacement contains 
                    // a "\" character that is not part of a "\\" pair, unless it is immediately
                    // followed by a "$" character.
                    if(next == '\\' || next == '$') {
                        continue;
                    }
                }
                throw new DynamicError("err:FORX0004", "Invalid replacement expr: " + replacement);
            }
        }
    }
}
