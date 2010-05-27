/*
 * @(#)$Id: IndexedSet.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.collections;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class IndexedSet<E> extends AbstractSet<E> implements Externalizable {
    private static final long serialVersionUID = 8775694634056054599L;

    private Map<E, Integer> _map;
    private SparseArrayList<E> _list;

    public IndexedSet() {
        this(256);
    }

    public IndexedSet(final int size) {
        this._map = new HashMap<E, Integer>(size);
        this._list = new SparseArrayList<E>();
    }

    public IndexedSet(Map<E, Integer> delegate) {
        this._map = delegate;
        this._list = new SparseArrayList<E>();
        for(Map.Entry<E, Integer> e : delegate.entrySet()) {
            int i = e.getValue();
            E v = e.getKey();
            _list.add(i, v);
        }
    }

    public int indexOf(E e) {
        Integer v = _map.get(e);
        return v == null ? -1 : v;
    }

    public int addIndexOf(E e) {
        if(_map.containsKey(e)) {
            return _map.get(e);
        } else {
            final int i = _map.size();
            _map.put(e, i);
            _list.add(i, e);
            return i;
        }
    }

    @Override
    public boolean add(E e) {
        if(_map.containsKey(e)) {
            return true;
        } else {
            int i = _map.size();
            _map.put(e, i);
            _list.add(i, e);
            return false;
        }
    }

    public E get(int index) {
        return _list.get(index);
    }

    public Iterator<E> iterator() {
        return _map.keySet().iterator();
    }

    public int size() {
        return _map.size();
    }

    //--------------------------------------------------
    // Externalizable

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final int size = in.readInt();
        final Map<E, Integer> map = new HashMap<E, Integer>(size);
        final SparseArrayList<E> list = new SparseArrayList<E>();
        for(int i = 0; i < size; i++) {
            E e = (E) in.readObject();
            map.put(e, i);
            list.add(i, e);
        }
        this._map = map;
        this._list = list;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_map.size());
        for(E e : _map.keySet()) {
            out.writeObject(e);
        }
    }
}
