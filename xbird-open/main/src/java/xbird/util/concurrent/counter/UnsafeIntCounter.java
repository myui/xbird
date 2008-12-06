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

import xbird.util.lang.PrivilegedAccessor;
import xbird.util.lang.UnsafeUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class UnsafeIntCounter implements ICounter {
    private static final long serialVersionUID = 1L;

    private static final sun.misc.Unsafe unsafe = UnsafeUtils.getUnsafe();
    private static final long valueOffset = unsafe.objectFieldOffset(PrivilegedAccessor.getField(UnsafeIntCounter.class, "_cnt"));

    private/* volatile */int _cnt;
    //private volatile int _macguffin;

    public UnsafeIntCounter() {
        this(0);
    }

    public UnsafeIntCounter(int initvalue) {
        this._cnt = initvalue;
    }

    // ------------------------------------------------

    public int get() {
        return _cnt;
    }

    public int estimateGet() {
        return _cnt;
    }

    public void add(final int x) {
        int current;
        do {
            current = _cnt;
        } while(!unsafe.compareAndSwapInt(this, valueOffset, current, current + x));
    }

    public boolean compareAndSet(final int expect, final int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
    }

    public boolean set(final int expect, final int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
    }

    public void set(final int x) {
        //unsafe.putOrderedInt(this, valueOffset, x);
        this._cnt = x;
    }

    // ------------------------------------------------

    public int getAndAdd(final int x) {
        int current;
        for(;;) {
            current = _cnt;
            if(unsafe.compareAndSwapInt(this, valueOffset, current, current + x)) {
                return current;
            }
        }
    }

    public int addAndGet(final int x) {
        int current;
        for(;;) {
            current = _cnt;
            final int next = current + x;
            if(unsafe.compareAndSwapInt(this, valueOffset, current, next)) {
                return next;
            }
        }
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
