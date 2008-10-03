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
package xbird.util.cache;

import xbird.util.concurrent.counter.UnsafeIntCounter;
import xbird.util.concurrent.lock.AtomicBackoffLock;
import xbird.util.concurrent.lock.ILock;
import xbird.util.lang.PrivilegedAccessor;
import xbird.util.lang.UnsafeUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class CacheEntry<K, V> implements ICacheEntry<K, V> {
    private static final long serialVersionUID = 962426580970946876L;

    //TODO should use AtomicFieldReferenceUpdater?
    private static final sun.misc.Unsafe unsafe = UnsafeUtils.getUnsafe();
    private static final long valueOffset = unsafe.objectFieldOffset(PrivilegedAccessor.getField(CacheEntry.class, "_value"));

    protected int _index = -1;
    protected final K _key;
    protected V _value;
    private final int _hash;

    private final UnsafeIntCounter _pinning;
    private final ILock _lock = new AtomicBackoffLock();

    /** used for barrier */
    private volatile int _macguffin;

    public CacheEntry(K key, V value) {
        this._key = key;
        this._value = value;
        this._hash = (key == null) ? -1 : _key.hashCode();
        this._pinning = new UnsafeIntCounter(1);
        this._macguffin = 0;
    }

    public void setIndex(int index) {
        this._index = index;
    }

    public final int getIndex() {
        return _index;
    }

    public final K getKey() {
        return _key;
    }

    public final V getValue() {
        return _value;
    }

    public final V volatileGetValue() {
        int lfence = _macguffin;
        return _value;
        //return <V> unsafe.getObjectVolatile(this, valueOffset);
    }

    public void setValue(final V newValue) {
        this._value = newValue;
    }

    public final void volatileSetValue(final V newValue) {
        this._value = newValue;
        this._macguffin = 0; // sfence
    }

    public final boolean compareAndSetValue(final V expect, final V update) {
        return unsafe.compareAndSwapObject(this, valueOffset, expect, update);
    }

    @Override
    public final int hashCode() {
        return _hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj == null || obj.hashCode() != _hash) {
            return false;
        }
        if(obj instanceof CacheEntry) {
            final CacheEntry e = (CacheEntry) obj;
            return e.getValue() == _value && e.getKey() == _key;
        }
        return false;
    }

    public final boolean pin() {
        final UnsafeIntCounter pinning = _pinning;
        int capa;
        do {
            capa = pinning.get();
            if(capa == -1) {// does not pin when the entry is evicted
                return false;
            }
        } while(!pinning.compareAndSet(capa, capa + 1));
        return true;
    }

    public final void unpin() {
        _pinning.decrementAndGet();
    }

    public final boolean isPinned() {
        return _pinning.get() > 0;
    }

    public final int pinCount() {
        return _pinning.get();
    }

    @Deprecated
    public final void setEvicted() {
        _pinning.set(-1);
    }

    public final void tryEvict(int expect, int update) {
        _pinning.compareAndSet(expect, update);
    }

    public final boolean tryEvict() {
        return _pinning.compareAndSet(0, -1);
    }

    public final boolean isEvicted() {
        return _pinning.get() == -1;
    }

    public final ILock lock() {
        return _lock;
    }

}
