/*
 * @(#)$Id: DecoratedMap.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class DecoratedMap<K, V> implements Map<K, V> {

    private final Map<K, V> _entries;
    private final Map<K, V> _decorated;

    public DecoratedMap(Map<K, V> decorated) {
        this(decorated, new HashMap<K, V>());
    }

    public DecoratedMap(Map<K, V> decorated, Map<K, V> entries) {
        if (decorated == null) {
            throw new IllegalArgumentException("argument MUST not be null.");
        }
        this._entries = entries;
        this._decorated = decorated;
    }

    public int size() {
        return _entries.size() + _decorated.size();
    }

    public boolean isEmpty() {
        return _entries.isEmpty() && _decorated.isEmpty();
    }

    public boolean containsKey(Object key) {
        return _entries.containsKey(key) || _decorated.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return _entries.containsValue(value) || _decorated.containsValue(value);
    }

    public V get(Object key) {
        V v = _decorated.get(key);
        if (v == null) {
            return _entries.get(key);
        } else {
            return v;
        }
    }

    public V put(K key, V value) {
        if (_decorated.containsKey(key)) {
            return _decorated.put(key, value);
        } else {
            return _entries.put(key, value);
        }
    }

    public V remove(Object key) {
        V v = _decorated.remove(key);
        if (v == null) {
            return _entries.get(key);
        } else {
            return v;
        }
    }

    public void putAll(Map<? extends K, ? extends V> t) {
        for(Map.Entry<? extends K, ? extends V> e : t.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    public void clear() {
        _entries.clear();
        _decorated.clear();
    }

    public Set<K> keySet() {
        Set<K> s = new HashSet<K>();
        s.addAll(_entries.keySet());
        s.addAll(_decorated.keySet());
        return s;
    }

    public Collection<V> values() {
        List<V> v = new LinkedList<V>();
        v.addAll(_entries.values());
        v.addAll(_decorated.values());
        return v;
    }

    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> s = new HashSet<Map.Entry<K, V>>();
        s.addAll(_entries.entrySet());
        s.addAll(_decorated.entrySet());
        return s;
    }

}
