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
package xbird.util.concurrent.cache.helpers;

import java.io.Serializable;

import xbird.util.concurrent.cache.algorithm.GClockCacheEntry;
import xbird.util.hashes.HashUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ClockBuffer<K, V> implements Serializable {
    private static final long serialVersionUID = -6547438635108653192L;

    private final int _capacityMask;
    private final GClockCacheEntry<K, V>[] _array;

    private int _free;
    private int _clockHand;

    @SuppressWarnings("unchecked")
    public ClockBuffer(int capacity) {
        if(capacity < 1) {
            throw new IllegalArgumentException("Illegal capacity: " + capacity);
        }
        int actualCapacity = HashUtils.nextPowerOfTwo(capacity);
        this._capacityMask = actualCapacity - 1;
        this._array = new GClockCacheEntry[actualCapacity];
        this._free = actualCapacity;
        this._clockHand = 0;
    }

    public GClockCacheEntry<K, V> add(final GClockCacheEntry<K, V> entry) {
        assert (entry != null);
        if(_free == 0) {
            return swap(entry);
        }
        --_free;
        _array[_clockHand++] = entry;
        return null;
    }

    private GClockCacheEntry<K, V> swap(final GClockCacheEntry<K, V> entry) {
        for(;;) {
            final int i = _clockHand & _capacityMask;
            ++_clockHand;

            final GClockCacheEntry<K, V> e = _array[i];
            assert (e != null);
            if(e.decrReferenceCount() <= 0) {
                if(e.tryEvict()) {
                    _array[i] = entry;
                    return e;
                }
            }
        }
    }

}
