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
package xbird.util.concurrent.cache;

import xbird.util.cache.Cleaner;
import xbird.util.cache.ICacheEntry;
import xbird.util.cache.ILongCache;
import xbird.util.concurrent.cache.helpers.NopCleaner;
import xbird.util.concurrent.collections.ConcurrentCollectionProvider;

/**
 * 
 * <DIV lang="en"></DIV> <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ConcurrentLongCache<V> implements ILongCache<V> {
    private static final long serialVersionUID = 131814055963317116L;

    private final ConcurrentPluggableCache<Long, V> _cache;

    public ConcurrentLongCache(int capacity) {
        this(capacity, NopCleaner.<Long, V> getInstance());
    }

    public ConcurrentLongCache(int capacity, Cleaner<Long, V> cleaner) {
        ConcurrentPluggableCache<Long, V> cache = new ConcurrentPluggableCache<Long, V>(capacity, ConcurrentCollectionProvider.<ICacheEntry<Long, V>> createConcurrentMapLong(capacity));
        cache.setCleaner(cleaner);
        this._cache = cache;
    }

    public V get(final long key) {
        return _cache.get(key);
    }

    public V put(final long key, final V value) {
        return _cache.put(key, value);
    }

    public V putIfAbsent(long key, V value) {
        return _cache.putIfAbsent(key, value);
    }

    public ICacheEntry<Long, V> fixEntry(long key) {
        return _cache.fixEntry(key);
    }

    public void clear() {
        _cache.clear();
    }
}
