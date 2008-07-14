/*
 * @(#)$Id: Remove.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.sequence.ProxySequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.IntegerType;

/**
 * fn:remove($target as item()*, $position as xs:integer) as item()*.
 * <DIV lang="en">
 * Returns a new sequence constructed from the value of $target with the item 
 * at the position specified by the value of $position removed.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-remove
 */
public final class Remove extends BuiltInFunction {
    private static final long serialVersionUID = -5504658491131019824L;
    public static final String SYMBOL = "fn:remove";

    public Remove() {
        super(SYMBOL, SequenceType.ANY_ITEMS);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS,
                IntegerType.INTEGER });
        return s;
    }

    @Override
    public Type getReturnType(List<XQExpression> params) {
        XQExpression target = params.get(0);
        Type targetType = target.getType();
        return targetType;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        assert (argv != null && argv.size() == 2);
        final Item target = argv.getItem(0);
        if(target.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        Item secondItem = argv.getItem(1);
        assert (secondItem instanceof XInteger);
        final int pos = (int) ((XInteger) secondItem).getValue();
        return pos > 0 ? new RemoveEmuration(target, pos, dynEnv) : target;
    }

    private static final class RemoveEmuration extends ProxySequence {
        private static final long serialVersionUID = -737597888736351350L;

        private final int _removePos;

        public RemoveEmuration(final Sequence src, final int removePos, final DynamicContext dynEnv) {
            super(src, dynEnv);
            assert (removePos > 0);
            this._removePos = removePos;
        }

        public boolean next(IFocus focus) throws XQueryException {
            final int cpos = focus.getContextPosition();
            Iterator<? extends Item> itor = focus.getBaseFocus();
            boolean removed = false;
            while(itor.hasNext()) {
                Item next = itor.next();
                if(!removed && cpos == _removePos - 1) {
                    removed = true;
                    continue;
                }
                focus.setContextItem(next);
                return true;
            }
            return false;
        }

    }

}
