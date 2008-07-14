/*
 * @(#)$Id: LRUCache.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.cache.algorithm;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xbird.util.cache.Cache;
import xbird.util.collections.LRUMap;

/**
 * A volatile Cache implementation which provides LRU feature. 
 * <DIV lang="en">
 * The entries might automatically be removed when its key is no longer 
 * in ordinary use.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 * @see LRUMap
 * @see WeakHashMap
 */
public class LRUCache<K, V> implements Cache<K, V> {

    private static final Log LOG = LogFactory.getLog(LRUCache.class);

    private static final int DEFAULT_MAX_CAPACITY = 256;

    private static Map<String, LRUMap> cacheManager = new WeakHashMap<String, LRUMap>();

    /** The name of cache */
    private final String cacheName;
    /** holds cache entries */
    private Map<K, V> cacheEntries;

    public LRUCache(String name) {
        this(name, DEFAULT_MAX_CAPACITY);
    }

    public LRUCache(String name, int maximumSize) {
        LRUMap<K, V> cache;
        synchronized(cacheManager) {
            cache = cacheManager.get(name);
            if(cache == null) {
                cache = new LRUMap<K, V>(maximumSize);
                cacheManager.put(name, cache);
            }
        }
        this.cacheName = name;
        this.cacheEntries = cache;
    }

    public V get(K key) {
        return ((key == null) ? null : cacheEntries.get(key));
    }

    public void put(K key, V value) {
        if(key == null) {
            return;
        }
        cacheEntries.put(key, value);
    }

    public void remove(K key) {
        if(key == null) {
            return;
        }
        cacheEntries.remove(key);
    }

    public void clear() {
        cacheEntries.clear();
    }

    public void destroy() {
        if(LOG.isTraceEnabled()) {
            LOG.trace("destroy cache: " + this.cacheName);
        }
        // cache region is singleton'ed.
        cacheManager.remove(this.cacheName);
        this.cacheEntries = null;
    }

}
