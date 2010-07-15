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
package xbird.util.pool;

import xbird.util.concurrent.collections.ConcurrentBoundedQueue;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class BoundedObjectPool<V> implements ObjectPool<V> {

    protected final ConcurrentBoundedQueue<V> queue;

    public BoundedObjectPool(int initEntries) {
        this(initEntries, false);
    }

    public BoundedObjectPool(int initEntries, boolean makeObjects) {
        this.queue = new ConcurrentBoundedQueue<V>(initEntries);
        if(makeObjects) {
            for(int i = 0; i < initEntries; i++) {
                queue.offer(createObject());
            }
        }
    }

    protected abstract V createObject();

    public V borrowObject() {
        final V pooled = queue.poll();
        if(pooled != null) {
            return pooled;
        }
        V created = createObject();
        return created;
    }

    public boolean returnObject(V value) {
        return queue.offer(value);
    }

    public void clear() {
        queue.clear();
    }

}
