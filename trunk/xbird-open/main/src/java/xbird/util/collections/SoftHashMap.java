/*
 * @(#)$Id: SoftHashMap.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.*;

/**
 * Memory-Sentitive Hash Map with HARD referenced keys and SOFT referenced values.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class SoftHashMap<K, V> implements Map<K, V>, Serializable {
    private static final long serialVersionUID = 1317950204065615051L;

    private final Map<K, SoftValue<K, V>> hash;
    private final ReferenceQueue<V> refs = new ReferenceQueue<V>();

    public SoftHashMap() {
        this(16);
    }

    public SoftHashMap(int initSize) {
        this.hash = new HashMap<K, SoftValue<K, V>>(initSize);
    }

    public SoftHashMap(Map<K, SoftValue<K, V>> map) {
        this.hash = map;
    }

    private void processQueue() {
        while(true) {
            SoftValue<K, V> v = (SoftValue<K, V>) refs.poll();
            if(v != null) { // remove garbage collected
                hash.remove(v.key);
            } else {
                return;
            }
        }
    }

    public int size() {
        processQueue();
        return hash.size();
    }

    public void clear() {
        processQueue();
        hash.clear();
    }

    public boolean isEmpty() {
        processQueue();
        return hash.isEmpty();
    }

    public boolean containsKey(Object k) {
        processQueue();
        return hash.containsKey(k);
    }

    public boolean containsValue(Object v) {
        processQueue();
        for(V value : values()) {
            if(value.equals(v)) {
                return true;
            }
        }
        return false;
    }

    public Collection<V> values() {
        processQueue();
        if(hash.isEmpty()) {
            return Collections.<V> emptyList();
        }
        final List<V> c = new LinkedList<V>();
        for(SoftValue<K, V> value : hash.values()) {
            V v = value.get();
            if(v != null) {
                c.add(v);
            }
        }
        return c;
    }

    /**
     * Not supported.
     * 
     * @throws UnsupportedOperationException
     */
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    public Set<K> keySet() {
        processQueue();
        return hash.keySet();
    }

    public V remove(Object k) {
        processQueue();
        SoftValue<K, V> sv = hash.remove(k);
        return (sv == null) ? null : sv.get();
    }

    public void putAll(Map<? extends K, ? extends V> t) {
        for(Map.Entry<? extends K, ? extends V> e : t.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    public V get(Object k) {
        V res = null;
        SoftValue<K, V> v = hash.get(k);
        if(v != null) {
            res = v.get();
            if(res == null) {
                hash.remove(k);
            }
        }
        return res;
    }

    public V put(K k, V v) {
        processQueue();
        SoftValue<K, V> sv = hash.put(k, new SoftValue<K, V>(k, v, refs));
        return (sv == null) ? null : sv.get();
    }

    public static final class SoftValue<K, V> extends SoftReference<V> {
        private final K key;

        public SoftValue(K k, V v, ReferenceQueue<V> q) {
            super(v, q);
            this.key = k;
        }
    }

    @Override
    public String toString() {
        return hash.toString();
    }

}
