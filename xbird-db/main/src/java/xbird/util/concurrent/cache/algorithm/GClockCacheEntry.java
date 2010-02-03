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
package xbird.util.concurrent.cache.algorithm;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import xbird.util.cache.CacheEntry;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class GClockCacheEntry<K, V> extends CacheEntry<K, V>
        implements Comparable<GClockCacheEntry<K, V>> {
    private static final long serialVersionUID = 5043033980602573152L;

    private final AtomicInteger _refCount = new AtomicInteger(1);
    private final AtomicBoolean _evicted = new AtomicBoolean(false);

    public GClockCacheEntry(K key, V value) {
        super(key, value, true);
    }

    public GClockCacheEntry(K key, V value, boolean pin) {
        super(key, value, pin);
    }

    public int getRefercenceCount() {
        return _refCount.get();
    }

    public void setReferenceCount(int cnt) {
        _refCount.set(cnt);
    }

    public void incrReferenceCount() {
        _refCount.incrementAndGet();
    }

    public int decrReferenceCount() {
        return _refCount.decrementAndGet();
    }

    public int compareTo(GClockCacheEntry<K, V> o) {
        final int otherRefCount = o.getRefercenceCount();
        final int thisRefCount = _refCount.get();
        return (thisRefCount < otherRefCount) ? -1 : (thisRefCount == otherRefCount ? 0 : 1);
    }

    @Override
    public boolean isEvicted() {
        return _evicted.get();
    }

    @Override
    public void setEvicted() {
        _evicted.set(true);
    }

    @Override
    public synchronized boolean tryEvict() {
        if(pinCount() > 0) {
            return false;
        }
        return _evicted.compareAndSet(false, true);
    }

    @Override
    public void tryEvict(int expect, int update) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean pin() {
        if(_evicted.get()) {
            return false;
        }
        _pinning.add(1);
        return true;
    }

}