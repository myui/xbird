/*
 * @(#)$Id: ExactlyOne.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.Iterator;
import java.util.List;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.Function;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.type.ItemType;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;

/**
 * fn:exactly-one($arg as item()*) as item().
 * <DIV lang="en">
 * Returns the input sequence if it contains exactly one item.
 * Raises an error otherwise.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-exactly-one
 */
public final class ExactlyOne extends BuiltInFunction {
    private static final long serialVersionUID = -2106728989802741825L;

    public static final String SYMBOL = "fn:exactly-one";

    public ExactlyOne() {
        super(SYMBOL, ItemType.ANY_ITEM);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS });
        return s;
    }

    @Override
    public Function staticAnalysis(StaticContext context, List<? extends XQExpression> params)
            throws XQueryException {
        // infer return type from parameter types
        XQExpression p = params.get(0);
        this._returnType = p.getType().prime();
        return this;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        Item arg = argv.getItem(0);
        final Type.Occurrence quantifier = arg.getType().quantifier();
        if(!Type.Occurrence.OCC_EXACTLY_ONE.accepts(quantifier.getAlignment())) {
            Iterator<? extends Item> argItor = arg.iterator();
            if(!argItor.hasNext()) {
                throw new DynamicError("err:FORG0005", "Item not found");
            }
            Item first = argItor.next();
            if(argItor.hasNext()) {
                // Returns $arg if it contains one or more items. Otherwise, raises an error [err:FORG0004].
                throw new DynamicError("err:FORG0005", "Sequence has more than one items");
            }
            return first;
        }
        return arg;
    }
}
