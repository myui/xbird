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
package xbird.util.concurrent.counter;

import java.util.concurrent.atomic.AtomicInteger;

import xbird.util.lang.HashUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class StripeAtomicIntCounter implements ICounter {
    private static final long serialVersionUID = -1886936728047307997L;

    private final AtomicInteger[] _cnts;
    private final int _size;
    private final int _mask;

    private volatile int _fuzzy_sum_cache;
    private volatile long _fuzzy_time;

    public StripeAtomicIntCounter(int nstripe) {
        assert (nstripe > 0) : nstripe;
        final int power2 = HashUtils.nextPowerOfTwo(nstripe);
        this._size = power2;
        this._mask = power2 - 1;

        this._cnts = new AtomicInteger[power2];
        for(int i = 0; i < power2; i++) {
            _cnts[i] = new AtomicInteger();
        }
    }
    
    public StripeAtomicIntCounter(int nstripe, int initValue) {
        assert (nstripe > 0) : nstripe;
        final int power2 = HashUtils.nextPowerOfTwo(nstripe);
        this._size = power2;
        this._mask = power2 - 1;

        this._cnts = new AtomicInteger[power2];
        _cnts[0] = new AtomicInteger(initValue);
        for(int i = 1; i < power2; i++) {
            _cnts[i] = new AtomicInteger();
        }
    }

    public int get() {
        final AtomicInteger[] cnts = _cnts;
        int sum = 0;
        for(int i = 0; i < _size; i++) {
            sum += cnts[i].get();
        }
        return sum;
    }

    public int estimateGet() {
        if(_size < 64) {
            return get();
        }
        final long millis = System.currentTimeMillis();
        if(_fuzzy_time != millis) {
            _fuzzy_sum_cache = get();
            _fuzzy_time = millis; // Indicate freshness of cached value
        }
        return _fuzzy_sum_cache;
    }

    public void add(final int x) {
        int idx = HashUtils.hash(Thread.currentThread(), _size - 1);
        _cnts[idx].addAndGet(x);
    }

    public void add2(final int x) {
        int hash = HashUtils.hash(Thread.currentThread());
        int idx = hash & _mask;
        _cnts[idx].addAndGet(x);
    }

    public int getAndAdd(final int x) {
        int l = get();
        add(x);
        return l;
    }

    public int addAndGet(final int x) {
        add(x);
        return get();
    }

    public int incrementAndGet() {
        return addAndGet(1);
    }

    public int decrementAndGet() {
        return addAndGet(-1);
    }

    public int getAndIncrement() {
        return getAndAdd(1);
    }

    public int getAndDecrement() {
        return getAndAdd(-1);
    }

}
