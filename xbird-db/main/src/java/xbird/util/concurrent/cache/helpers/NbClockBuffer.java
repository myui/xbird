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
import java.util.concurrent.atomic.AtomicInteger;

import xbird.util.concurrent.cache.algorithm.NbGClockCacheEntry;
import xbird.util.concurrent.counter.CounterProvider;
import xbird.util.concurrent.counter.ICounter;
import xbird.util.concurrent.lang.VolatileArray;
import xbird.util.hashes.HashUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NbClockBuffer<K, V> implements Serializable {
    private static final long serialVersionUID = -6547438635108653192L;

    private final int _capacity;
    private final int _capacityMask;
    private final VolatileArray<NbGClockCacheEntry<K, V>> _array;

    private final AtomicInteger _free;
    private final ICounter _clockHand;

    public NbClockBuffer(int capacity) {
        if(capacity < 1) {
            throw new IllegalArgumentException("Illegal capacity: " + capacity);
        }
        int actualCapacity = HashUtils.nextPowerOfTwo(capacity);
        this._capacity = actualCapacity;
        this._capacityMask = actualCapacity - 1;
        this._array = new VolatileArray<NbGClockCacheEntry<K, V>>(actualCapacity);
        this._free = new AtomicInteger(actualCapacity);
        this._clockHand = CounterProvider.createIntCounter();
    }

    public NbGClockCacheEntry<K, V> add(final NbGClockCacheEntry<K, V> entry) {
        assert (entry != null);
        for(;;) {
            final int free = _free.get();
            if(free == 0) {
                return swap(entry);
            }
            if(_free.compareAndSet(free, free - 1)) {
                break; // ensured that there are free spaces
            }
        }
        // While more than one thread might comes here, it does not cause a problem
        // since there are actually free (null) spaces.
        int idx = _clockHand.get();
        while(!_array.compareAndSet(idx & _capacityMask, null, entry)) {
            idx++;
        }
        _clockHand.add(1);
        return null;
    }

    private NbGClockCacheEntry<K, V> swap(final NbGClockCacheEntry<K, V> entry) {
        final int start = _clockHand.get();
        int pinned = 0;
        for(int i = start & _capacityMask;; i = (i + 1) & _capacityMask) {
            final NbGClockCacheEntry<K, V> e = _array.get(i);
            if(e == null) {
                // was not yet full. Rarely fall thru.
                // yield this slot to other threads.
                continue;
            }
            final int pincount = e.pinCount();
            if(pincount == -1) {//evicted
                if(_array.compareAndSet(i, e, entry)) {
                    moveClockHand(_clockHand, _capacity, i, start);
                    return e;
                }
                continue;
            }
            if(pincount > 0) {//pinned
                if(++pinned >= _capacity) {// all buffers are pinned 
                    Thread.yield();
                    pinned = 1;
                }
                continue;
            }
            if(e.decrReferenceCount() <= 0) {
                // sweep evicted pages and low-referenced pages first.
                if(e.tryEvict() && _array.compareAndSet(i, e, entry)) {
                    moveClockHand(_clockHand, _capacity, i, start);
                    return e;
                }
                continue;
            }
        }
    }

    private static void moveClockHand(final ICounter clockHand, final int capacity, final int curr, final int start) {
        final int delta = (curr < start) ? (curr + capacity - start + 1) : (curr - start + 1);
        clockHand.add(delta);
    }

}
