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

import java.util.Arrays;

import xbird.util.concurrent.lang.AtomicSimpleRandom;
import xbird.util.concurrent.lang.VolatileIntArray;
import xbird.util.lang.BitUtils;
import xbird.util.lang.HashUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NestedIntCounter implements ICounter {
    private static final long serialVersionUID = -430693237059414592L;

    private final ICounter[] _edges;
    private final VolatileIntArray _sums;
    private final int _nbits;

    private final AtomicSimpleRandom _rand = new AtomicSimpleRandom();

    public NestedIntCounter(int nedges) {
        this(nedges, Math.min(2, nedges >>> 1));
    }

    public NestedIntCounter(int nedges, int nstripe) {
        assert (nedges > 0) : nedges;
        assert (nstripe > 0) : nstripe;
        int shifts = HashUtils.shiftsForNextPowerOfTwo(nedges);
        this._nbits = shifts;
        int power2 = 1 << shifts;

        this._edges = new ICounter[power2];
        for(int i = 0; i < power2; i++) {
            _edges[i] = new StripeAtomicIntCounter2(nstripe);
        }
        final int[] ary = new int[power2];
        Arrays.fill(ary, -1);
        this._sums = new VolatileIntArray(power2);
    }

    public NestedIntCounter(ICounter[] edges) {
        int length = edges.length;
        if(!HashUtils.isPowerOfTwo(length)) {
            throw new IllegalArgumentException("number of edges must be power of two, but was "
                    + length);
        }
        this._nbits = BitUtils.mostSignificantBit(length);
        this._edges = edges;
        final int[] ary = new int[length];
        Arrays.fill(ary, -1);
        this._sums = new VolatileIntArray(length);
    }

    public int get() {
        final ICounter[] cnts = _edges;
        int sum = 0;
        for(int i = 0; i < _edges.length; i++) {
            int cached = _sums.get(i);
            if(cached == -1) {
                sum += cnts[i].get();
            } else {
                sum += cached;
            }
        }
        return sum;
    }

    public void add(final int x) {
        int idx = HashUtils.hash(Thread.currentThread(), _edges.length - 1);
        _sums.set(idx, -1);
        int sum = _edges[idx].randAdd(x);
        _sums.set(idx, sum);
    }

    public int randAdd(final int x) {
        int idx = _rand.next(_nbits);
        _sums.set(idx, -1);
        int sum = _edges[idx].randAdd(x);
        _sums.set(idx, sum);
        return sum;
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

    public int estimateGet() {
        throw new UnsupportedOperationException();
    }
}
