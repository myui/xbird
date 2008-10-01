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
package xbird.util.nio;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.Selector;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import xbird.util.concurrent.AtomicUtils;
import xbird.util.concurrent.collections.NonBlockingStack;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NioSelectorPool implements Closeable {

    private final NonBlockingStack<Selector> pool = new NonBlockingStack<Selector>();
    private final AtomicInteger active = new AtomicInteger(0);
    private final AtomicInteger spare = new AtomicInteger(0);
    private final AtomicInteger waiter = new AtomicInteger(0);

    private int maxSelectors = Integer.MAX_VALUE;
    private int timeout = 0; // no timeout
    private int maxAttempt = Integer.MAX_VALUE;

    public NioSelectorPool() {}

    public void setMaxSelectors(int size) {
        this.maxSelectors = size;
    }

    public void setTimeout(int mills) {
        this.timeout = mills;
    }

    public void setMaxAttempt(int count) {
        this.maxAttempt = count;
    }

    @Nullable
    public Selector getSelector() throws IOException {
        Selector selector = pool.pop();
        if(selector == null) {
            if(AtomicUtils.tryIncrementIfLessThan(active, maxSelectors)) {
                selector = Selector.open();
            } else {
                waiter.incrementAndGet();
                for(int i = 0; i < maxAttempt; i++) {
                    try {
                        pool.wait(timeout);
                    } catch (InterruptedException e) {
                        return null;
                    }
                    selector = pool.pop();
                    if(selector != null) {
                        spare.decrementAndGet();
                        break;
                    }
                }
                waiter.decrementAndGet();
            }
        } else {
            spare.decrementAndGet();
        }
        return selector;
    }

    public void returnSelector(@Nonnull final Selector s) {
        if(AtomicUtils.tryIncrementIfLessThan(spare, maxSelectors)) {
            pool.push(s);
            if(waiter.get() > 0) {
                pool.notify();
            }
        } else {
            NIOUtils.close(s);
            active.decrementAndGet();
        }
    }

    public void selectNowAndReturnSelector(@Nonnull final Selector s) {
        try {
            s.selectNow();
        } catch (IOException e) {
            NIOUtils.close(s);
            active.decrementAndGet();
            return;
        }
        returnSelector(s);
    }

    public void close() throws IOException {
        IOException firstException = null;
        Selector s;
        while((s = pool.pop()) != null) {
            try {
                s.close();
            } catch (IOException e) {
                if(firstException == null) {
                    firstException = e;
                }
            }
        }
        if(firstException != null) {
            throw firstException;
        }
    }

}
