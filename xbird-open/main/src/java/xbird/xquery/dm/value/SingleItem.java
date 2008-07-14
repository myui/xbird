/*
 * @(#)$Id: SingleItem.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value;

import java.util.*;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.sequence.IRandomAccessSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.UntypedAtomicType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class SingleItem extends AbstractSequence<Item>
        implements Item, IRandomAccessSequence<Item> {
    private static final long serialVersionUID = 1L;

    public SingleItem() {
        super(DynamicContext.DUMMY);
    }

    //--------------------------------------------
    // IRandomAccessSequence interface implimentation

    public Collection<Item> getItems() {
        final List<Item> ret = new ArrayList<Item>(1);
        ret.add(this);
        return ret;
    }

    public Item getItem(int index) {
        if(index == 0) {
            return this;
        } else {
            throw new IllegalArgumentException("Index out of range.. " + index);
        }
    }

    public Sequence subSequence(int fromIndex, int toIndex) {
        if(fromIndex == 0 && toIndex < 1) {
            throw new IllegalArgumentException("Index out of range.");
        }
        return (toIndex == 0) ? ValueSequence.EMPTY_SEQUENCE : this;
    }

    public int size() {
        return 1;
    }

    //--------------------------------------------
    // Sequence interface implimentation

    public boolean next(IFocus focus) throws XQueryException {
        final int pos = focus.getContextPosition();
        if(pos == 0) {
            focus.setContextItem(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof SingleItem) {
            final int cmp = compareTo((SingleItem) o);
            return cmp == 0;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public static final class DummySingleItem extends SingleItem {
        private static final long serialVersionUID = 6994111398394373691L;

        public static final DummySingleItem INSTANCE = new DummySingleItem();

        private DummySingleItem() {}

        public String stringValue() {
            return "";
        }

        public Type getType() {
            return UntypedAtomicType.UNTYPED_ATOMIC;
        }

        public int compareTo(Item o) {
            throw new IllegalStateException("DummySingleItem is uncomparable");
        }

        @Override
        public String toString() {
            return "";
        }

    }

}
