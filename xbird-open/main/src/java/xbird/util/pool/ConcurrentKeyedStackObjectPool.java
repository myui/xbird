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

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import xbird.util.concurrent.collections.ConcurrentCollectionProvider;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ConcurrentKeyedStackObjectPool<K, V extends Closeable> implements Closeable {

    private final PoolableObjectFactory<K, V> _factory;
    private final ConcurrentMap<K, Queue<PooledObject<V>>> _poolMap;

    private final Timer _sweepTimer;

    public ConcurrentKeyedStackObjectPool(String sweepThreadName, PoolableObjectFactory<K, V> factory) {
        this._factory = factory;
        this._poolMap = ConcurrentCollectionProvider.createConcurrentMap(8);
        this._sweepTimer = new Timer("ObjPoolExpirer#" + sweepThreadName, true);
        _sweepTimer.scheduleAtFixedRate(new Expirer(factory.geTimeToLive()), 3000, factory.getSweepInterval());
    }

    public V borrowObject(K key) {
        Queue<PooledObject<V>> queue = _poolMap.get(key);
        if(queue == null) {
            return _factory.makeObject(key);
        }
        PooledObject<V> popedObj = queue.poll();
        if(popedObj != null) {
            V value = popedObj.getValue();
            if(_factory.validateObject(value)) {
                return popedObj.getValue();
            }
        }
        return _factory.makeObject(key);
    }

    public void returnObject(K key, V value) {
        if(!_factory.validateObject(value)) {
            return;
        }
        PooledObject<V> newPoolObj = asPooledObject(value);
        Queue<PooledObject<V>> queue = _poolMap.get(key);
        if(queue == null) {
            Queue<PooledObject<V>> newQueue = new ConcurrentLinkedQueue<PooledObject<V>>();
            Queue<PooledObject<V>> existingQueue = _poolMap.putIfAbsent(key, newQueue);
            if(existingQueue != null) {
                existingQueue.offer(newPoolObj);
            } else {
                newQueue.offer(newPoolObj);
            }
        } else {
            queue.offer(newPoolObj);
        }
    }

    private static <V> PooledObject<V> asPooledObject(V value) {
        return new PooledObject<V>(value);
    }

    private static final class PooledObject<V> {
        final V value;
        volatile long lastAccessTime;

        public PooledObject(V value) {
            this.value = value;
            this.lastAccessTime = System.currentTimeMillis();
        }

        public V getValue() {
            return value;
        }

        public long getLastAccessTime() {
            return lastAccessTime;
        }

        void setLastAccessTime(long lastAccessTime) {
            this.lastAccessTime = lastAccessTime;
        }
    }

    private final class Expirer extends TimerTask {

        final int timeToLive;

        public Expirer(int timeToLive) {
            this.timeToLive = timeToLive;
        }

        @Override
        public void run() {
            final long timeNow = System.currentTimeMillis();
            final Iterator<Queue<PooledObject<V>>> outerItor = _poolMap.values().iterator();
            outer: while(outerItor.hasNext()) {
                Queue<PooledObject<V>> pool = outerItor.next();
                final Iterator<PooledObject<V>> innerItor = pool.iterator();
                while(innerItor.hasNext()) {
                    PooledObject<V> pooledObj = innerItor.next();
                    V value = pooledObj.getValue();
                    long timeIdle = timeNow - pooledObj.getLastAccessTime();
                    if(_factory.validateObject(value) && timeIdle < timeToLive) {
                        continue outer; // make it live in the pool
                    }
                    innerItor.remove();
                    try {
                        value.close();
                    } catch (IOException e) {
                        ;
                    }
                }
                if(pool.isEmpty()) {
                    outerItor.remove();
                }
            }
        }

        @Override
        public boolean cancel() {
            final Iterator<Queue<PooledObject<V>>> poolItor = _poolMap.values().iterator();
            while(poolItor.hasNext()) {
                final Queue<PooledObject<V>> pool = poolItor.next();
                PooledObject<V> pooledObj;
                while((pooledObj = pool.poll()) != null) {
                    try {
                        pooledObj.getValue().close();
                    } catch (IOException e) {
                        ;
                    }
                }
                poolItor.remove();
            }
            return super.cancel();
        }

    }

    public void close() throws IOException {
        _sweepTimer.cancel();
    }

}
