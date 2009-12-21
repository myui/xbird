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
package xbird.util.concurrent.counter;

import xbird.util.primitive.MutableLong;

/**
 * ThreadLocal counter.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ThreadLocalCounter implements LCounter {
    private static final long serialVersionUID = -536435244025591411L;
    
    private final ThreadLocal<MutableLong> counter;

    public ThreadLocalCounter() {
        this(0L);
    }

    public ThreadLocalCounter(final long initVal) {
        this.counter = new ThreadLocal<MutableLong>() {
            protected MutableLong initialValue() {
                return new MutableLong(initVal);
            }
        };
    }

    public long get() {
        return counter.get().longValue();
    }

    public long getAndIncrement() {
        return counter.get().getAndIncrement();
    }

    public long getAndDecrement() {
        return counter.get().getAndDecrement();
    }

    public long incrementAndGet() {
        return counter.get().incrementAndGet();
    }

    public long decrementAndGet() {
        return counter.get().decrementAndGet();
    }

    public long addAndGet(long x) {
        return counter.get().addAndGet(x);
    }

    public long getAndAdd(long x) {
        return counter.get().getAndAdd(x);
    }

    public void add(final long x) {
        counter.get().add(x);
    }

}
