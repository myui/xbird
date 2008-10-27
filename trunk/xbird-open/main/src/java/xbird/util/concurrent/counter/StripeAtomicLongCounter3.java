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

import sun.misc.Unsafe;
import xbird.util.lang.HashUtils;
import xbird.util.lang.UnsafeUtils;
import xbird.util.system.SystemUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class StripeAtomicLongCounter3 implements LCounter {
    private static final long serialVersionUID = -6828495151317672372L;

    private final static int SHIFT_PADDING = SystemUtils.is64BitVM() ? 3 : 2;

    private static final Unsafe _unsafe = UnsafeUtils.getUnsafe();
    private static final int _base = _unsafe.arrayBaseOffset(long[].class);
    private static final int _scale = _unsafe.arrayIndexScale(long[].class);
    private final long[] _array;
    private volatile int _macguffin;

    private final int _size;
    private final int _mask;

    public StripeAtomicLongCounter3(int nstripe) {
        assert (nstripe > 0) : nstripe;
        int power2 = HashUtils.nextPowerOfTwo(nstripe);
        this._size = power2;
        this._mask = power2 - 1;

        this._array = new long[power2 << SHIFT_PADDING];
        // must perform at least one volatile write to conform to JMM
        this._macguffin = 0;
    }

    public long get() {
        long sum = getVolatile(0); // force volatile read
        final long[] ary = _array;
        final int pad = 1 << SHIFT_PADDING;
        for(int i = pad; i < ary.length; i += pad) {
            sum += ary[i];
        }
        return sum;
    }

    public void add(final long x) {
        int idx = HashUtils.hash(Thread.currentThread(), _size - 1);
        addAndGet(idx, x);
    }

    public void add2(final long x) {
        int hash = HashUtils.hash(Thread.currentThread());
        int idx = hash & _mask;
        addAndGet(idx, x);
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
        return addAndGet(1L);
    }

    public long decrementAndGet() {
        return addAndGet(-1L);
    }

    public long getAndIncrement() {
        return getAndAdd(1L);
    }

    public long getAndDecrement() {
        return getAndAdd(-1L);
    }

    private long addAndGet(final int i, final long delta) {
        final int ix = i << SHIFT_PADDING;
        while(true) {
            long current = getVolatile(ix);
            long next = current + delta;
            if(compareAndSet(ix, current, next)) {
                return next;
            }
        }
    }

    private long getVolatile(final int i) {
        return _array[i + _macguffin];
    }

    private boolean compareAndSet(final int i, final long expect, final long update) {
        return _unsafe.compareAndSwapLong(_array, rawIndex(i), expect, update);
    }

    private long rawIndex(final int i) {
        assert (i >= 0 && i < _array.length) : "Illegal index: " + i;
        return _base + i * _scale;
    }

}
