/*
 * @(#)$Id: OneOrMore.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.func.*;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.type.*;
import xbird.xquery.type.Type.Occurrence;

/**
 * fn:one-or-more($arg as item()*) as item()+.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-one-or-more
 */
public final class OneOrMore extends BuiltInFunction {
    private static final long serialVersionUID = -857693933671581928L;
    public static final String SYMBOL = "fn:one-or-more";

    public OneOrMore() {
        super(SYMBOL, new SequenceType(ItemType.ANY_ITEM, Occurrence.OCC_ONE_OR_MORE));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS });
        return s;
    }

    @Override
    public Function staticAnalysis(StaticContext context, List<? extends XQExpression> params)
            throws XQueryException {
        assert (params != null);
        // infer return type from parameter types
        assert (params.size() == 1);
        XQExpression p = params.get(0);
        Type prime = p.getType().prime();
        assert (!(prime instanceof SequenceType));
        assert (prime instanceof ItemType);
        this._returnType = new SequenceType((ItemType) prime, Type.Occurrence.OCC_ONE_OR_MORE);
        assert (Type.Occurrence.OCC_ONE_OR_MORE.accepts(_returnType.quantifier().getAlignment()));
        return this;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null && argv.size() == 1);
        Item arg = argv.getItem(0);
        final Type.Occurrence quantifier = arg.getType().quantifier();
        if(!Type.Occurrence.OCC_ONE_OR_MORE.accepts(quantifier.getAlignment())) {
            if(arg.isEmpty()) {
                // Returns $arg if it contains one or more items. Otherwise, raises an error [err:FORG0004].
                throw new DynamicError("err:FORG0004", "Invalid result sequence type was detected: "
                        + arg.getType());
            }
        }
        return arg;
    }

}
