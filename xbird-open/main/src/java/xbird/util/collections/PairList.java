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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class PairList<K, V> implements Serializable {
    private static final long serialVersionUID = -3136730106628043831L;

    private final List<K> keyList;
    private final List<V> valueList;

    public PairList(int size) {
        this.keyList = new ArrayList<K>(size);
        this.valueList = new ArrayList<V>(size);
    }

    public void add(final K key, final V value) {
        keyList.add(key);
        valueList.add(value);
    }

    public void set(final int i, K key, final V value) {
        keyList.set(i, key);
        valueList.set(i, value);
    }

    public K getKey(final int i) {
        return keyList.get(i);
    }

    public V getValue(final int i) {
        return valueList.get(i);
    }

    public boolean isEmpty() {
        return keyList.isEmpty();
    }

    public int size() {
        return keyList.size();
    }

    public void clear() {
        keyList.clear();
        valueList.clear();
    }
}
