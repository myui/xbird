/*
 * @(#)$Id: Count.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.agg;

import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.sequence.*;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.opt.PathIndexAccessExpr.IndexMatchedSequence;
import xbird.xquery.func.*;
import xbird.xquery.func.opt.EagerEvaluated;
import xbird.xquery.meta.*;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.IntegerType;

/**
 * fn:count($arg as item()*) as xs:integer.
 * <DIV lang="en">
 * Returns the number of items in the value of $arg.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-count
 */
public final class Count extends BuiltInFunction {
    private static final long serialVersionUID = 8271004501052232949L;

    public static final String SYMBOL = "fn:count";

    public Count() {
        super(SYMBOL, IntegerType.INTEGER);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS });
        return s;
    }

    @Override
    public Function staticAnalysis(StaticContext context, List<? extends XQExpression> params)
            throws XQueryException {
        if(params.isEmpty()) {
            return new EagerEvaluated(this, XInteger.valueOf(0));
        }
        return this;
    }

    public Sequence eval(final Sequence<? extends Item> contextSeq, final ValueSequence argv, final DynamicContext dynEnv)
            throws XQueryException {
        int count = 0;
        if(!argv.isEmpty()) {
            final Item arg = argv.getItem(0);
            if(arg instanceof IRandomAccessSequence) {
                count = ((IRandomAccessSequence) arg).size();
            } else {
                if(arg instanceof SingleCollection) {
                    final Sequence source = ((SingleCollection) arg).getSource();
                    if(source instanceof IRandomAccessSequence) {
                        count = ((IRandomAccessSequence) source).size();
                        return XInteger.valueOf(count);
                    } else if(source instanceof IndexMatchedSequence) {
                        count = ((IndexMatchedSequence) source).totalCount();
                        return XInteger.valueOf(count);
                    }
                }
                final IFocus<Item> argItor = arg.iterator();
                for(Item it : argItor) {
                    ++count;
                }
                argItor.closeQuietly();
            }
        }
        return XInteger.valueOf(count);
    }

}
