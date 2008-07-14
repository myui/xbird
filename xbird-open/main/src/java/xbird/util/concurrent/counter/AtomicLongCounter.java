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

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class AtomicLongCounter implements LCounter {
    private static final long serialVersionUID = -2222654229548961671L;
    
    private final AtomicLong value = new AtomicLong(0);

    public AtomicLongCounter() {}

    public long getAndIncrement() {
        return value.getAndIncrement();
    }

    public long getAndDecrement() {
        return value.getAndDecrement();
    }

    public long decrementAndGet() {
        return value.decrementAndGet();
    }

    public long incrementAndGet() {
        return value.incrementAndGet();
    }

    public long get() {
        return value.get();
    }

    public long getAndAdd(final long x) {
        return value.getAndAdd(x);
    }

    public long addAndGet(final long x) {
        return value.addAndGet(x);
    }

    public void add(final long x) {
        value.addAndGet(x);
    }

}
