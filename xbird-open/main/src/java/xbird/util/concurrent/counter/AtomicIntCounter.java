/*
 * @(#)$$Id$$
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

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class AtomicIntCounter implements ICounter {
    private static final long serialVersionUID = 3639749122540382687L;

    private final AtomicInteger value;

    public AtomicIntCounter() {
        this(0);
    }
    
    public AtomicIntCounter(int initValue) {
        this.value = new AtomicInteger(initValue);
    }
    
    public int getAndIncrement() {
        return value.getAndIncrement();
    }

    public int getAndDecrement() {
        return value.getAndDecrement();
    }

    public int decrementAndGet() {
        return value.decrementAndGet();
    }

    public int incrementAndGet() {
        return value.incrementAndGet();
    }

    public int get() {
        return value.get();
    }

    public int estimateGet() {
        return value.get();
    }

    public int getAndAdd(final int x) {
        return value.getAndAdd(x);
    }

    public int addAndGet(final int x) {
        return value.addAndGet(x);
    }

    public void add(final int x) {
        value.addAndGet(x);
    }

}
