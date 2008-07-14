/*
 * @(#)$Id: ValueSequence.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value.sequence;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AbstractSequence;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ValueSequence extends AbstractSequence<Item>
        implements Item, IRandomAccessSequence<Item>, Externalizable {
    private static final long serialVersionUID = -584285721169278056L;

    public static final int DEFAULT_SEQ_SIZE = 32;
    public static final ValueSequence EMPTY_SEQUENCE = new ValueSequence(Collections.<Item> emptyList(), DynamicContext.DUMMY);

    private final List<Item> _items;

    public ValueSequence(DynamicContext dynEnv) {
        this(new ArrayList<Item>(DEFAULT_SEQ_SIZE), dynEnv);
    }

    public ValueSequence(List<? extends Item> items, DynamicContext dynEnv) {
        super(dynEnv);
        this._items = (List<Item>) items;
    }

    public ValueSequence() { // for Externalizable
        super(DynamicContext.DUMMY);
        this._items = new ArrayList<Item>(512);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final int size = in.readInt();
        ArrayList<Item> items = new ArrayList<Item>(size);
        for(int i = 0; i < size; i++) {
            Item e = (Item) in.readObject();
            items.add(e);
        }
        _items.addAll(items);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        final List<Item> items = _items;
        final int size = items.size();
        out.writeInt(size);
        for(int i = 0; i < size; i++) {
            Item it = items.get(i);
            out.writeObject(it);
        }
    }

    //--------------------------------------------
    // IRandomAccessSequence interface implimentation

    public Item getItem(int index) {
        return _items.get(index);
    }

    public Collection<Item> getItems() {
        return _items;
    }

    public int size() {
        return _items.size();
    }

    public Sequence subSequence(int fromIndex, int toIndex) {
        return new ValueSequence(_items.subList(fromIndex, toIndex), _dynEnv);
    }

    //--------------------------------------------
    // IModifiableSequence interface implimentation

    public boolean addItem(Item i) {
        return _items.add(i);
    }

    public boolean addItems(Collection<? extends Item> c) {
        return _items.addAll(c);
    }

    public Item setItem(int index, Item i) {
        return _items.set(index, i);
    }

    public void addSequence(int index, Sequence<? extends Item> seq) {
        final IFocus<? extends Item> itor = seq.iterator();
        for(Item e : itor) {
            _items.add(index++, e);
        }
        itor.closeQuietly();
    }

    public Item remove(int index) {
        return _items.remove(index);
    }

    //--------------------------------------------
    // Sequence interface implimentation

    public Sequence<? extends Item> atomize(DynamicContext dynEnv) {
        if(_items.size() == 1) {
            return _items.get(0);
        } else {
            return this;
        }
    }

    public Type getType() {
        if(isEmpty()) {
            return SequenceType.EMPTY;
        } else if(size() == 1) {
            return getItem(0).getType();
        } else {
            return SequenceType.ANY_ITEMS;
        }
    }

    public String stringValue() {
        final StringBuilder buf = new StringBuilder();
        for(int i = 0; i < _items.size(); i++) {
            if(i != 0) {
                buf.append(' ');
            }
            Item it = _items.get(i);
            buf.append(it.stringValue());
        }
        return buf.toString(); // TODO cache
    }

    public boolean next(IFocus focus) throws XQueryException {
        final int pos = focus.getContextPosition();
        if(pos < _items.size()) {
            Item it = _items.get(pos);
            focus.setContextItem(it);
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return _items.isEmpty();
    }

    public int compareTo(Item trg) {
        if(size() > 0) {
            Item it = _items.get(0);
            return it.compareTo(trg);
        }
        return -1; // TODO empty last?
    }

    public static final <T extends Sequence> T emptySequence() {
        return (T) EMPTY_SEQUENCE;
    }

    @Override
    public List<Item> materialize() {
        return _items;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Item) {
            final int cmp = compareTo((ValueSequence) obj);
            return cmp == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return _items.hashCode();
    }

}
