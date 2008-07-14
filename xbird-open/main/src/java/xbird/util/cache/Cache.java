/*
 * @(#)$Id: Cache.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.cache;

/**
 * Defines interface for caching.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public interface Cache<K, V> {

    /**
     * Get an item from the cache.
     */
    public V get(K key) throws CacheException;

    /**
     * Add an item to the cache.
     */
    public void put(K key, V value) throws CacheException;

    /**
     * Remove an item from the cache.
     */
    public void remove(K key) throws CacheException;

    /**
     * Clear all items in the cache.
     */
    public void clear() throws CacheException;

    /**
     * Destroy the cache.
     */
    public void destroy() throws CacheException;

}
