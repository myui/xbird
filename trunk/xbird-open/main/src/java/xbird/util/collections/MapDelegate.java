/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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
public abstract class MapDelegate<K, V> implements Map<K, V> {

    protected final Map<K, V> _delegate;

    public MapDelegate(Map<K, V> map) {
        this._delegate = map;
    }

    public void clear() {
        _delegate.clear();
    }

    public boolean containsKey(Object key) {
        return _delegate.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return _delegate.containsValue(value);
    }

    public Set<Entry<K, V>> entrySet() {
        return _delegate.entrySet();
    }

    public V get(Object key) {
        return _delegate.get(key);
    }

    public boolean isEmpty() {
        return _delegate.isEmpty();
    }

    public Set<K> keySet() {
        return _delegate.keySet();
    }

    public V put(K key, V value) {
        return _delegate.put(key, value);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        _delegate.putAll(m);
    }

    public V remove(Object key) {
        return _delegate.remove(key);
    }

    public int size() {
        return _delegate.size();
    }

    public Collection<V> values() {
        return _delegate.values();
    }

    @Override
    public boolean equals(Object o) {
        return _delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return _delegate.hashCode();
    }

}
