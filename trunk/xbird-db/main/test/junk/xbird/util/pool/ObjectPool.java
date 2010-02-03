/*
 * @(#)$Id: ObjectPool.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.pool;

import java.util.LinkedList;
import java.util.Queue;


/**
 * The base class for Object Pooling.
 * <DIV lang="en">
 * The recycling strategy is FIFO.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public abstract class ObjectPool<T extends Poolable> {

    public static final int INFINITE_CAPACITY = -1;

    protected final int maxPoolSize;

    protected final Queue<T> pool;

    private transient int allocated = 0, recycled = 0;

    public ObjectPool() {
        this(new LinkedList<T>(), INFINITE_CAPACITY);
    }

    public ObjectPool(Queue<T> pool) {
        this(pool, INFINITE_CAPACITY);
    }

    public ObjectPool(Queue<T> pool, int maxPoolSize) {
        this.pool = pool;
        this.maxPoolSize = maxPoolSize;
    }

    protected abstract T createObject();

    public T getObject() {
        T res = pool.poll();
        if (res == null) {
            res = createObject();
            res.setPool(this);
            ++allocated;
        } else {
            ++recycled;
        }
        return res;
    }

    public synchronized void putObject(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Pooling null object.");
        }
        if (maxPoolSize == INFINITE_CAPACITY || pool.size() < maxPoolSize) {
            pool.add(obj);
        }
    }

}