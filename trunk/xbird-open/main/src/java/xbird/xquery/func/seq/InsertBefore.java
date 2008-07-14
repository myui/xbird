/*
 * @(#)$Id: InsertBefore.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.sequence.ProxySequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.IntegerType;

/**
 * fn:insert-before($target as item()*, $position as xs:integer, $inserts as item()*) as item()*.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-insert-before
 */
public final class InsertBefore extends BuiltInFunction {
    private static final long serialVersionUID = 2696152358059415990L;
    public static final String SYMBOL = "fn:insert-before";

    public InsertBefore() {
        super(SYMBOL, SequenceType.ANY_ITEMS);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS,
                IntegerType.INTEGER, SequenceType.ANY_ITEMS });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null && argv.size() == 3);
        final Item target = argv.getItem(0);
        if(target.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        Item secondItem = argv.getItem(1);
        assert (secondItem instanceof XInteger);
        int pos = (int) ((XInteger) secondItem).getValue();
        // If $position is less than one (1), the first position, the effective value of $position is one (1).
        if(pos < 1) {
            pos = 1;
        }
        Item inserts = argv.getItem(2);
        if(target instanceof ValueSequence) {
            ValueSequence vs = (ValueSequence) target;
            final int last = vs.size();
            if(pos > last) {
                pos = last;
            }
            vs.addSequence(pos - 1, inserts);
            return vs;
        } else {
            return new InsertEmuration(target, inserts, pos, dynEnv);
        }
    }

    private static final class InsertEmuration extends ProxySequence {
        private static final long serialVersionUID = 4748828736062203534L;
        
        private final Sequence<Item> _inserts;
        private final int _insertPos;
        private IFocus<Item> _insertItor;
        private boolean _reachedEnd = false;

        private InsertEmuration(Sequence src, Sequence<Item> inserts, int insertPos, DynamicContext dynEnv) {
            super(src, dynEnv);
            assert (insertPos >= 1);
            this._inserts = inserts;
            this._insertPos = insertPos;
        }

        @Override
        public IFocus iterator() {
            final IFocus focus = super.iterator();
            this._insertItor = _inserts.iterator();
            this._reachedEnd = false;
            return focus;
        }

        public boolean next(IFocus focus) throws XQueryException {
            if(_reachedEnd) {
                return false;
            }
            final int cpos = focus.getContextPosition();
            Iterator<Item> itor = focus.getBaseFocus();
            if(itor.hasNext()) {
                if(cpos >= (_insertPos - 1)) {
                    if(_insertItor.hasNext()) {
                        Item next = _insertItor.next();
                        focus.setContextItem(next);
                        return true;
                    }
                }
                Item next = itor.next();
                focus.setContextItem(next);
                return true;
            } else {
                if(_insertItor.hasNext()) {
                    Item next = _insertItor.next();
                    focus.setContextItem(next);
                    return true;
                } else {
                    this._reachedEnd = false;
                    return false;
                }
            }
        }

    }
}
