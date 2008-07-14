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

import org.cliffc.high_scale_lib.ConcurrentAutoTable;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class HighScalableIntCounter implements ICounter {
    private static final long serialVersionUID = 80493494124020203L;

    private final ConcurrentAutoTable _counter;

    public HighScalableIntCounter() {
        this._counter = new ConcurrentAutoTable();
    }

    public void add(int x) {
        _counter.add(x);
    }

    public int get() {
        return (int) _counter.get();
    }

    public int estimateGet() {
        return (int) _counter.estimate_get();
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
