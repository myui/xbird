/*
 * @(#)$Id: Matches.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.func.*;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.BooleanType;
import xbird.xquery.type.xs.StringType;

/**
 * fn:matches.
 * <DIV lang="en">
 * The function returns true if $input matches the regular expression supplied as 
 * $pattern as influenced by the value of $flags, if present; otherwise, it returns false.
 * <ul>
 * <li>fn:matches($input as xs:string?, $pattern as xs:string) as xs:boolean</li>
 * <li>fn:matches($input as xs:string?, $pattern as xs:string, $flags as xs:string) as xs:boolean</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-matches
 * @link http://www.w3.org/TR/xquery-operators/#flags
 */
public final class Matches extends BuiltInFunction {
    private static final long serialVersionUID = 226707058370060178L;
    public static final String SYMBOL = "fn:matches";

    public Matches() {
        super(SYMBOL, BooleanType.BOOLEAN);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        Type strq = TypeRegistry.safeGet("xs:string?");
        s[0] = new FunctionSignature(getName(), new Type[] { strq, StringType.STRING });
        s[1] = new FunctionSignature(getName(), new Type[] { strq, StringType.STRING,
                StringType.STRING });
        return s;
    }

    @Override
    public Function staticAnalysis(StaticContext context, List<? extends XQExpression> params)
            throws XQueryException {
        // TODO performance pre-compile
        return super.staticAnalysis(context, params);
    }

    public BooleanValue eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        final int arglen = argv.size();
        Item firstItem = argv.getItem(0);
        // If $input is the empty sequence, it is interpreted as the zero-length string.
        String input = firstItem.isEmpty() ? "" : firstItem.stringValue();
        Item secondItem = argv.getItem(1);
        String pattern = secondItem.stringValue();
        int flags = Pattern.UNIX_LINES;
        if(arglen == 3) {
            Item thirdItem = argv.getItem(2);
            String flagsStr = thirdItem.stringValue();
            flags = convertFlags(flagsStr);
        }
        final Pattern compiled;
        try {
            compiled = Pattern.compile(pattern, flags);
        } catch (PatternSyntaxException pse) {
            throw new DynamicError("err:FORX0002", "Invalid pattern: " + pattern);
        }
        Matcher matcher = compiled.matcher(input);
        final boolean match = matcher.matches();
        return match ? BooleanValue.TRUE : BooleanValue.FALSE;
    }

    public static int convertFlags(final String flags) throws DynamicError {
        assert (flags != null);
        int ret = Pattern.UNIX_LINES;
        for(int i = 0; i < flags.length(); i++) {
            char c = flags.charAt(i);
            switch(c) {
                case 's':
                    ret |= Pattern.DOTALL;
                    break;
                case 'm':
                    ret |= Pattern.MULTILINE;
                    break;
                case 'i':
                    ret |= (Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                    break;
                case 'x':
                    ret |= Pattern.COMMENTS;
                    break;
                default:
                    throw new DynamicError("err:FORX0001", "Unsupported flag: `" + c + '`');
            }
        }
        return ret;
    }

}
