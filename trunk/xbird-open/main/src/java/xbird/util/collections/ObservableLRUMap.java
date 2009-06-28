/*
 * @(#)$Id$
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

import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ObservableLRUMap<K, V> extends LRUMap<K, V> {
    private static final long serialVersionUID = 8089714061811479021L;

    private final Cleaner<K, V> cleaner;

    public ObservableLRUMap(int limit, Cleaner<K, V> cleaner) {
        super(limit);
        this.cleaner = cleaner;
    }

    @Override
    public void clear() {
        for(Map.Entry<K, V> e : entrySet()) {
            cleaner.cleanup(e.getKey(), e.getValue());
        }
        super.clear();
    }

    public interface Cleaner<K, V> {
        public void cleanup(K key, V value);
    }

    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {
        if(size() > maxCapacity) {
            cleaner.cleanup(eldest.getKey(), eldest.getValue());
            return true;
        }
        return false;
    }

}
