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
package xbird.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class Memoizer<A, V> implements Computable<A, V> {

    private final ConcurrentMap<A, Future<V>> cache;
    private final Computable<A, V> c;

    public Memoizer(Computable<A, V> c) {
        this.cache = new ConcurrentHashMap<A, Future<V>>();
        this.c = c;
    }

    public V compute(final A arg) throws InterruptedException {
        while(true) {
            Future<V> f = cache.get(arg);
            if(f == null) {
                final Callable<V> eval = new Callable<V>() {
                    public V call() throws Exception {
                        return c.compute(arg);
                    }
                };
                final FutureTask<V> ft = new FutureTask<V>(eval);
                f = cache.putIfAbsent(arg, ft);
                if(f == null) {
                    f = ft;
                    ft.run();
                }
            }
            try {
                return f.get();
            } catch (CancellationException ce) {
                cache.remove(arg, f);
            } catch (ExecutionException ee) {
                throw launderThrowable(ee.getCause());
            }
        }
    }

    private static RuntimeException launderThrowable(final Throwable t) {
        if(t instanceof RuntimeException) {
            return (RuntimeException) t;
        } else if(t instanceof Error) {
            throw (Error) t;
        } else {
            throw new IllegalStateException("Not unchecked", t);
        }
    }

}
