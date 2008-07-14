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
public final class HighScalableLongCounter extends ConcurrentAutoTable implements LCounter {
    private static final long serialVersionUID = -7385044619688994480L;

    public HighScalableLongCounter() {
        super();
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
