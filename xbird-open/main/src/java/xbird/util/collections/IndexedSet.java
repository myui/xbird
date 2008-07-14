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

import java.io.*;
import java.util.*;

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

    public IndexedSet() {
        this(256);
    }

    public IndexedSet(final int size) {
        this._map = new HashMap<E, Integer>(size);
    }
    
    public IndexedSet(Map<E, Integer> delegate) {
        this._map = delegate;
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
            return i;
        }
    }

    @Override
    public boolean add(E e) {
        if(_map.containsKey(e)) {
            return true;
        } else {
            _map.put(e, _map.size());
            return false;
        }
    }

    public Iterator<E> iterator() {
        return _map.keySet().iterator();
    }

    public int size() {
        return _map.size();
    }

    //--------------------------------------------------
    // Externalizable

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final int size = in.readInt();
        Map<E, Integer> map = new HashMap<E, Integer>(size);
        for(int i = 0; i < size; i++) {
            E e = (E) in.readObject();
            map.put(e, i);
        }
        this._map = map;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_map.size());
        for(E e : _map.keySet()) {
            out.writeObject(e);
        }
    }
}
