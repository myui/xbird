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
public final class FixedStripeAtomicIntCounter implements ICounter {
    private static final long serialVersionUID = -1886936728047307997L;

    private final AtomicInteger _cnts1, _cnts2, _cnts3, _cnts4;

    private volatile int _fuzzy_sum_cache;
    private volatile long _fuzzy_time;

    public FixedStripeAtomicIntCounter() {
        this._cnts1 = new AtomicInteger();
        this._cnts2 = new AtomicInteger();
        this._cnts3 = new AtomicInteger();
        this._cnts4 = new AtomicInteger();
    }

    public int get() {
        return _cnts1.get() + _cnts2.get() + _cnts3.get() + _cnts4.get();
    }

    public int estimateGet() {
        final long millis = System.currentTimeMillis();
        if(_fuzzy_time != millis) {
            _fuzzy_sum_cache = get();
            _fuzzy_time = millis; // Indicate freshness of cached value
        }
        return _fuzzy_sum_cache;
    }

    public void add(final int x) {
        final int idx = HashUtils.hash(Thread.currentThread(), 3);
        switch(idx) {
            case 0:
                _cnts1.addAndGet(x);
                break;
            case 1:
                _cnts2.addAndGet(x);
                break;
            case 2:
                _cnts3.addAndGet(x);
                break;
            case 3:
                _cnts4.addAndGet(x);
                break;
        }
    }

    public void add2(final int x) {
        int hash = HashUtils.hash(Thread.currentThread());
        final int idx = hash & 3;
        switch(idx) {
            case 0:
                _cnts1.addAndGet(x);
                break;
            case 1:
                _cnts2.addAndGet(x);
                break;
            case 2:
                _cnts3.addAndGet(x);
                break;
            case 3:
                _cnts4.addAndGet(x);
                break;
        }
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
