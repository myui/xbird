/*
 * @(#)$Id: MemorySentitiveObjectPool.java 3619 2008-03-26 07:23:03Z yui $
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

import java.lang.ref.SoftReference;
import java.util.LinkedList;


/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class MemorySentitiveObjectPool extends ObjectPool {

    public MemorySentitiveObjectPool() {
        super();
    }

    public MemorySentitiveObjectPool(int maxPoolSize) {
        super(new LinkedList<Poolable>(), maxPoolSize);
    }

    public Poolable getObject() {
        PoolableSoftReference ref = (PoolableSoftReference) super.getObject();
        final Poolable picked = ref.get();
        if (picked == null) { // may be garbase collected
            final Poolable res = createObject();
            putObject(res);
            return res;
        } else {
            return picked;
        }
    }

    public void putObject(Poolable obj) {
        super.putObject(new PoolableSoftReference(obj));
    }

    private static final class PoolableSoftReference extends SoftReference<Poolable>
            implements Poolable {

        public PoolableSoftReference(Poolable poolObj) {
            super(poolObj);
        }

        public void reclaim() {}

        public void setPool(ObjectPool pool) {}

    }
}
