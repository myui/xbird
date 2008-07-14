/*
 * @(#)$Id: Reverse.java 3619 2008-03-26 07:23:03Z yui $
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
import java.util.Stack;

import xbird.util.iterator.ReverseIterator;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.*;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;

/**
 * fn:reverse($arg as item()*) as item()*.
 * <DIV lang="en">
 * Reverses the order of items in a sequence. If $arg is the empty sequence, 
 * the empty sequence is returned.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-reverse
 */
public final class Reverse extends BuiltInFunction {
    private static final long serialVersionUID = 2154542136553587421L;
    public static final String SYMBOL = "fn:reverse";

    public Reverse() {
        super(SYMBOL, SequenceType.ANY_ITEMS);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null && argv.size() == 1);
        Item arg = argv.getItem(0);
        return new Reverser(arg, dynEnv);
    }

    private static final class Reverser extends AbstractSequence<Item> {
        private static final long serialVersionUID = -7288851327130322738L;
        
        private Stack<Item> _itemStack = null;
        private final Sequence<Item> _src;

        Reverser(Sequence<Item> src, DynamicContext dynEnv) {
            super(dynEnv);
            assert (src != null);
            this._src = src;
        }

        public boolean next(IFocus focus) throws XQueryException {
            final Iterator<Item> baseItor = focus.getBaseFocus();
            if(baseItor.hasNext()) {
                final Item next = baseItor.next();
                focus.setContextItem(next);
                return true;
            }
            return false;
        }

        public Focus<Item> iterator() {
            if(_itemStack == null) {
                final Stack<Item> st = new Stack<Item>();
                final IFocus<Item> srcItor = _src.iterator();
                for(Item it : srcItor) {
                    st.push(it);
                }
                srcItor.closeQuietly();
                this._itemStack = st;
            }
            final Iterator<Item> baseItor = new ReverseIterator<Item>(_itemStack);
            return new Focus<Item>(this, baseItor, _dynEnv);
        }

        public Type getType() {
            return _src.getType();
        }

        @Override
        public boolean isEmpty() {
            return _src.isEmpty();
        }

    }

}
