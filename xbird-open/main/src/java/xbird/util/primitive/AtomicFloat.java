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
package xbird.util.primitive;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class AtomicFloat extends Number {
    private static final long serialVersionUID = 4992180751166673712L;

    private final AtomicInteger value;

    public AtomicFloat() {
        this(0.0f);
    }

    public AtomicFloat(float value) {
        this.value = new AtomicInteger(i(value));
    }

    public final float get() {
        return f(value.get());
    }

    public final void set(float newValue) {
        value.set(i(newValue));
    }

    public final void lazySet(float newValue) {
        value.lazySet(i(newValue));
    }

    public final float getAndSet(float newValue) {
        return f(value.getAndSet(i(newValue)));
    }

    public final boolean compareAndSet(float expect, float update) {
        return value.compareAndSet(i(expect), i(update));
    }

    public final boolean weakCompareAndSet(float expect, float update) {
        return value.weakCompareAndSet(i(expect), i(update));
    }

    public final float getAndIncrement() {
        return getAndAdd(1.0f);
    }

    public final float getAndDecrement() {
        return getAndAdd(-1.0f);
    }

    public final float getAndAdd(float delta) {
        for(;;) {
            int icurrent = value.get();
            float current = f(icurrent);
            float next = current + delta;
            int inext = i(next);
            if(value.compareAndSet(icurrent, inext)) {
                return current;
            }
        }
    }

    public final float incrementAndGet() {
        return addAndGet(1.0f);
    }

    public final float decrementAndGet() {
        return addAndGet(-1.0f);
    }

    public final float addAndGet(float delta) {
        for(;;) {
            int icurrent = value.get();
            float current = f(icurrent);
            float next = current + delta;
            int inext = i(next);
            if(value.compareAndSet(icurrent, inext)) {
                return next;
            }
        }
    }

    public String toString() {
        return Float.toString(get());
    }

    @Override
    public int intValue() {
        return (int) get();
    }

    @Override
    public long longValue() {
        return (long) get();
    }

    @Override
    public float floatValue() {
        return get();
    }

    @Override
    public double doubleValue() {
        return (double) get();
    }

    private static final int i(final float f) {
        return Float.floatToIntBits(f);
    }

    private static final float f(final int i) {
        return Float.intBitsToFloat(i);
    }

}
