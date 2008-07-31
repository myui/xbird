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

import xbird.util.concurrent.collections.ConcurrentCyclicBuffer;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class BoundedObjectPool<V> implements ObjectPool<V> {

    private final ConcurrentCyclicBuffer<V> queue;

    public BoundedObjectPool(int initEntries) {
        this.queue = new ConcurrentCyclicBuffer<V>(initEntries);
        for(int i = 0; i < initEntries; i++) {
            queue.offer(createObject());
        }
    }

    protected abstract V createObject();

    public V borrowObject() {
        final V pooled = queue.poll();
        if(pooled != null) {
            return pooled;
        }
        final V created = createObject();
        queue.offer(created);
        return created;
    }

    public void returnObject(V value) {
        queue.offer(value);
    }

    public void clear() {
        queue.clear();
    }

}