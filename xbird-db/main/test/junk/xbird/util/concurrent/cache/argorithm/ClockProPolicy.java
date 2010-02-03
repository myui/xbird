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
import java.util.concurrent.atomic.AtomicReferenceArray;

import xbird.util.concurrent.cache.ReplacementPolicy;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ClockProPolicy<K, V>
        implements ReplacementPolicy<K, V, ClockProCacheEntry<K, V>> {

    private final int _maxCapacity;
    private final AtomicReferenceArray<ClockProCacheEntry<K, V>> _ringBuffer;

    private float _percentHot = 0.3f, _percentCold = 0.7f;
    private int _maxHot, _maxCold;

    private int hotClockHand, testClockHand, coldClockHand;

    public ClockProPolicy(int capacity) {
        this._maxCapacity = capacity;
        this._ringBuffer = new AtomicReferenceArray<ClockProCacheEntry<K, V>>(capacity);
        adjustBuffer();
    }

    private void adjustBuffer() {
        this._maxHot = (int) (_maxCapacity * _percentHot);
        this._maxCold = (int) (_maxCapacity * _percentCold);
    }

    public int getMaxCapacity() {
        return _maxCapacity;
    }

    public void recordAccess(ClockProCacheEntry<K, V> entry) {
        entry.setReferenced(true);
    }

    public void recordRemoval(ClockProCacheEntry<K, V> entry) {
        entry.setResident(false);
    }

    public V addEntry(ConcurrentMap<K, ClockProCacheEntry<K, V>> map, K key, V value) {
        if(isFull(map)) {
            // sweep a page from the buffer
            // TODO
        }
        return null;
    }

    private boolean isFull(final ConcurrentMap<K, ClockProCacheEntry<K, V>> map) {
        return map.size() >= _maxCapacity;
    }

}
