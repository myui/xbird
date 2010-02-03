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

import java.util.concurrent.ConcurrentMap;

import xbird.util.cache.Cleaner;
import xbird.util.cache.ICacheEntry;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public interface ReplacementPolicy<K, V, CE extends ICacheEntry<K, V>> {

    public void setCleaner(Cleaner<K, V> cleaner);

    public int getMaxCapacity();

    public void recordAccess(CE entry);

    public void recordRemoval(CE entry);

    public V addEntry(ConcurrentMap<K, CE> map, K key, V value, boolean replace);

    public ICacheEntry<K, V> allocateEntry(ConcurrentMap<K, CE> map, K key, V value);

    public int prefetchEntries(ICacheEntry<K, V> entry);

}
