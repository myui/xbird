/*
 * @(#)$Id: Subsequence.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.seq;

import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XNumber;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.path.FilterExpr;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.DoubleType;
import xbird.xquery.type.xs.NumericType;

/**
 * fn:subsequence.
 * <DIV lang="en">
 * <ul>
 * <li>fn:subsequence($sourceSeq as item()*, $startingLoc as xs:double) as item()*</li>
 * <li>fn:subsequence($sourceSeq as item()*, $startingLoc as xs:double, $length as xs:double) as item()*</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class Subsequence extends BuiltInFunction {
    private static final long serialVersionUID = -1941283897300455851L;
    public static final String SYMBOL = "fn:subsequence";

    public Subsequence() {
        super(SYMBOL, SequenceType.ANY_ITEMS);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[4];
        s[0] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS,
                DoubleType.DOUBLE });
        s[1] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS,
                DoubleType.DOUBLE, DoubleType.DOUBLE });
        // workaround
        s[2] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS,
                NumericType.getInstance() });
        s[3] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS,
                NumericType.getInstance(), NumericType.getInstance() });
        return s;
    }

    @Override
    public Type getReturnType(List<XQExpression> params) {
        if(params == null) {
            throw new IllegalArgumentException();
        }
        this._returnType = params.get(0).getType();
        return _returnType;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        Item src = argv.getItem(0);
        Item fromIt = argv.getItem(1);
        final int from = ((XNumber) fromIt).getNumber().intValue();
        final int arglen = argv.size();
        if(arglen == 2) {
            return new FilterExpr.Sliced(src, from, Integer.MAX_VALUE, dynEnv, getReturnType());
        }
        Item lengthIt = argv.getItem(2);
        final int length = ((XNumber) lengthIt).getNumber().intValue(); //TODO exceeds Integer.MAX
        if(length < 1) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        return new FilterExpr.Sliced(src, from, from + length - 1, dynEnv, getReturnType());
    }

}
