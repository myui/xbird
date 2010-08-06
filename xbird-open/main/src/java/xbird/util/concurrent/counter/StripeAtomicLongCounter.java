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

import java.util.concurrent.atomic.AtomicLong;

import xbird.util.hashes.HashUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class StripeAtomicLongCounter implements LCounter {
    private static final long serialVersionUID = -3233754920587135376L;
    
    private final AtomicLong[] _cnts;
    private final int _size;
    private final int _mask;

    public StripeAtomicLongCounter(int nstripe) {
        int power2 = HashUtils.nextPowerOfTwo(nstripe);
        this._cnts = new AtomicLong[power2];
        this._size = power2;
        this._mask = power2 - 1;
        for(int i = 0; i < nstripe; i++) {
            _cnts[i] = new AtomicLong();
        }
    }

    public long get() {
        final AtomicLong[] cnts = _cnts;
        long sum = 0;
        for(int i = 0; i < _size; i++) {
            sum += cnts[i].get();
        }
        return sum;
    }

    public void add(final long x) {
        int idx = HashUtils.hash(Thread.currentThread(), _size - 1);
        _cnts[idx].addAndGet(x);
    }

    public void add2(final long x) {
        int hash = HashUtils.hash(Thread.currentThread());
        int idx = hash & _mask;
        _cnts[idx].addAndGet(x);
    }

    public long getAndAdd(final long x) {
        long l = get();
        add(x);
        return l;
    }

    public long addAndGet(final long x) {
        add(x);
        return get();
    }

    public long incrementAndGet() {
        return addAndGet(1);
    }

    public long decrementAndGet() {
        return addAndGet(-1);
    }

    public long getAndIncrement() {
        return getAndAdd(1);
    }

    public long getAndDecrement() {
        return getAndAdd(-1);
    }

}
