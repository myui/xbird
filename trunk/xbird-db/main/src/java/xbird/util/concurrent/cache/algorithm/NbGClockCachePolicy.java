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
package xbird.util.concurrent.cache.algorithm;

import java.util.concurrent.ConcurrentMap;

import xbird.util.cache.Cleaner;
import xbird.util.cache.ICacheEntry;
import xbird.util.concurrent.cache.ReplacementPolicy;
import xbird.util.concurrent.cache.helpers.NbClockBuffer;
import xbird.util.concurrent.cache.helpers.NopCleaner;

/**
 * In GCLOCK, a counter is associated with each page, whose initial value is assigned 
 * when the page is brought into the buffer.
 * <DIV lang="en">
 * Victor F. Nicola, Asit Dan, Daniel M. Dias: 
 * Analysis of the Generalized Clock Buffer Replacement Scheme for Database Transaction Processing.
 * SIGMETRICS pp. 35-46, 1992.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class NbGClockCachePolicy<K, V> implements ReplacementPolicy<K, V, NbGClockCacheEntry<K, V>> {

    private final int _maxCapacity;
    private final NbClockBuffer<K, V> _buf;

    private Cleaner<K, V> _cleaner = NopCleaner.getInstance();

    public NbGClockCachePolicy(int capacity) {
        this._maxCapacity = capacity;
        this._buf = new NbClockBuffer<K, V>(capacity);
    }

    public final void setCleaner(Cleaner<K, V> cleaner) {
        this._cleaner = cleaner;
    }

    public final int getMaxCapacity() {
        return _maxCapacity;
    }

    public final void recordAccess(final NbGClockCacheEntry<K, V> entry) {
        entry.incrReferenceCount();
    }

    public final void recordRemoval(final NbGClockCacheEntry<K, V> entry) {
        entry.setReferenceCount(Integer.MIN_VALUE);
    }

    public final V addEntry(final ConcurrentMap<K, NbGClockCacheEntry<K, V>> map, final K key, final V value, final boolean replace) {
        final NbGClockCacheEntry<K, V> newEntry = createCacheEntry(key, value, false);
        final NbGClockCacheEntry<K, V> removed = _buf.add(newEntry);
        if(removed != null) {
            K remotedKey = removed.getKey();
            _cleaner.cleanup(remotedKey, removed.getValue());
            map.remove(remotedKey, removed);
        }
        final NbGClockCacheEntry<K, V> prevEntry = map.putIfAbsent(key, newEntry);
        if(prevEntry != null) {
            newEntry.setEvicted();
            V oldValue = prevEntry.getValue();
            prevEntry.setValue(value); // REVIEWME non-atomic operation is permittable?
            prevEntry.incrReferenceCount();
            return oldValue;
        }
        return null;
    }

    public final ICacheEntry<K, V> allocateEntry(final ConcurrentMap<K, NbGClockCacheEntry<K, V>> map, final K key, final V value) {
        for(;;) {
            final NbGClockCacheEntry<K, V> newEntry = createCacheEntry(key, value, true);
            final NbGClockCacheEntry<K, V> removed = _buf.add(newEntry);
            if(removed != null) {
                K remotedKey = removed.getKey();
                if(map.remove(remotedKey, removed)) {
                    _cleaner.cleanup(remotedKey, removed.getValue());
                }
            }
            final NbGClockCacheEntry<K, V> prevEntry = map.putIfAbsent(key, newEntry);
            if(prevEntry != null) {
                if(!prevEntry.pin()) {
                    if(map.replace(key, prevEntry, newEntry)) {
                        newEntry.setValue(prevEntry.getValue());
                        return newEntry;
                    }
                    newEntry.tryEvict(1, -1);
                    continue;
                }
                newEntry.tryEvict(1, -1);
                prevEntry.incrReferenceCount();
                return prevEntry;
            }
            return newEntry;
        }
    }

    protected NbGClockCacheEntry<K, V> createCacheEntry(final K key, final V value, final boolean pin) {
        return new NbGClockCacheEntry<K, V>(key, value, pin);
    }

    public int prefetchEntries(ICacheEntry<K, V> entry) {
        return 0;
    }

}
