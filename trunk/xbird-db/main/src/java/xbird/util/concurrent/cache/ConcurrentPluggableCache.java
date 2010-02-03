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
package xbird.util.concurrent.cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;

import xbird.storage.io.Segments;
import xbird.util.cache.CacheEntry;
import xbird.util.cache.Cleaner;
import xbird.util.cache.ICacheEntry;
import xbird.util.collections.ArrayQueue;
import xbird.util.concurrent.cache.helpers.ReplacementPolicySelector;
import xbird.util.concurrent.collections.ConcurrentCollectionProvider;
import xbird.util.concurrent.lock.AtomicBackoffLock;
import xbird.util.concurrent.lock.ILock;
import xbird.util.primitive.MutableLong;

/**
 * 
 * 
 * <DIV lang="en"></DIV> <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ConcurrentPluggableCache<K, V> extends AbstractMap<K, V>
        implements ConcurrentMap<K, V>, Serializable {
    private static final long serialVersionUID = -1375386751224945824L;

    private final ConcurrentMap<K, ICacheEntry<K, V>> _delegate;
    private transient ReplacementPolicy<K, V, ICacheEntry<K, V>> _policy;

    public ConcurrentPluggableCache(ConcurrentMap<K, ICacheEntry<K, V>> map, ReplacementPolicy<K, V, ICacheEntry<K, V>> policy) {
        super();
        this._delegate = map;
        this._policy = policy;
    }

    public ConcurrentPluggableCache(int capacity) {
        super();
        this._delegate = ConcurrentCollectionProvider.createConcurrentMap(capacity);
        this._policy = ReplacementPolicySelector.provide(capacity);
    }

    public ConcurrentPluggableCache(int capacity, ConcurrentMap<K, ICacheEntry<K, V>> map) {
        super();
        this._delegate = map;
        this._policy = ReplacementPolicySelector.provide(capacity);
    }

    public void setReplacementPolicy(ReplacementPolicy<K, V, ICacheEntry<K, V>> policy) {
        this._policy = policy;
    }

    public void setCleaner(Cleaner<K, V> cleaner) {
        _policy.setCleaner(cleaner);
    }

    @Override
    public V get(Object key) {
        ensureNotNull(key);
        final ICacheEntry<K, V> entry = _delegate.get(key);
        if(entry != null) {
            _policy.recordAccess(entry);
            return dereferenceValue(entry);
        }
        return null;
    }

    public ICacheEntry<K, V> fixEntry(final K key) {
        ensureNotNull(key);
        ICacheEntry<K, V> entry = _delegate.get(key);
        if(entry != null && entry.pin()) {
            _policy.recordAccess(entry);
        } else {
            entry = _policy.allocateEntry(_delegate, key, null);
        }
        return entry;
    }

    private static final int MAX_QUEUE_SIZE = 64;
    //private static final int NUM_PROCS = SystemUtils.availableProcessors();
    private static final int BATCH_THRESHOLD = 32;
    private final ILock lock = new AtomicBackoffLock();
    private final ThreadLocal<ArrayQueue<ICacheEntry<K, V>>> pendingQueues = new ThreadLocal<ArrayQueue<ICacheEntry<K, V>>>() {
        protected ArrayQueue<ICacheEntry<K, V>> initialValue() {
            return new ArrayQueue<ICacheEntry<K, V>>(new ICacheEntry[MAX_QUEUE_SIZE]);
        }
    };

    /*
    private final ArrayQueue<ICacheEntry<K, V>>[] pendingQueues;
    {
        pendingQueues = new ArrayQueue[NUM_PROCS];
        for(int i = 0; i < NUM_PROCS; i++) {
            pendingQueues[i] = new ArrayQueue<ICacheEntry<K, V>>(new ICacheEntry[MAX_QUEUE_SIZE]);
        }
    }
    */

    public ICacheEntry<K, V> fixEntryLazySync(final K key) {
        ensureNotNull(key);
        ICacheEntry<K, V> entry = _delegate.get(key);
        if(entry != null && entry.pin()) {
            replacementForPageHit(entry);
        } else {
            entry = replacementForPageMiss(key);
        }
        return entry;
    }

    private void replacementForPageHit(final ICacheEntry<K, V> entry) {
        final ArrayQueue<ICacheEntry<K, V>> queue = pendingQueues.get();
        queue.offer(entry);
        final int tail = queue.size();
        final boolean locked;
        if(tail >= BATCH_THRESHOLD) {
            // prefetch pages
            _policy.prefetchEntries(entry);
            locked = lock.tryLock();
        } else {
            return;
        }
        if(!locked) {
            if(tail < MAX_QUEUE_SIZE) {
                return;
            } else {
                // prefetch pages
                _policy.prefetchEntries(entry);
                lock.lock();
            }
        }
        final int size = queue.size();
        for(int i = 0; i < size; i++) {
            ICacheEntry<K, V> e = queue.get(i);
            _policy.recordAccess(e);
        }
        lock.unlock();
        queue.clear();
    }

    private ICacheEntry<K, V> replacementForPageMiss(final K key) {
        final ArrayQueue<ICacheEntry<K, V>> queue = pendingQueues.get();
        // prefetch pages
        lock.lock();
        final int size = queue.size();
        for(int i = 0; i < size; i++) {
            ICacheEntry<K, V> e = queue.get(i);
            _policy.recordAccess(e);
        }
        ICacheEntry<K, V> entry = _policy.allocateEntry(_delegate, key, null);
        lock.unlock();
        queue.clear();
        return entry;
    }

    public ICacheEntry<K, V> fixEntryLazySync2(final Segments pager, final K key, final long lKey, final MutableLong miss) {
        ensureNotNull(key);
        ICacheEntry<K, V> entry = _delegate.get(key);
        if(entry != null && entry.pin()) {
            replacementForPageHit(entry);
        } else {
            entry = replacementForPageMiss2(pager, key, lKey);
            miss.increment();
        }
        return entry;
    }

    @SuppressWarnings("unchecked")
    private ICacheEntry<K, V> replacementForPageMiss2(final Segments pager, final K key, final long lKey) {
        final ArrayQueue<ICacheEntry<K, V>> queue = pendingQueues.get();
        // prefetch pages
        lock.lock();
        final int size = queue.size();
        for(int i = 0; i < size; i++) {
            ICacheEntry<K, V> e = queue.get(i);
            _policy.recordAccess(e);
        }
        final ICacheEntry<K, V> entry = _policy.allocateEntry(_delegate, key, null);
        final byte[] b;
        try {
            b = pager.read(lKey * 80);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        entry.setValue((V) b);
        lock.unlock();
        queue.clear();
        return entry;
    }

    @Deprecated
    public ICacheEntry<K, V> allocateEntry(final K key, final Lock readLock, final Lock writeLock) {
        ensureNotNull(key);

        readLock.lock();
        ICacheEntry<K, V> entry = _delegate.get(key);
        readLock.unlock();

        writeLock.lock();
        if(entry != null && entry.pin()) {
            _policy.recordAccess(entry);
        } else {
            entry = _policy.allocateEntry(_delegate, key, null);
        }
        writeLock.unlock();
        return entry;
    }

    @Deprecated
    public ICacheEntry<K, V> allocateEntryForClock(final K key, final Lock readLock, final Lock writeLock) {
        ensureNotNull(key);

        readLock.lock();
        ICacheEntry<K, V> entry = _delegate.get(key);
        readLock.unlock();

        if(entry != null && entry.pin()) {
            _policy.recordAccess(entry);
        } else {
            writeLock.lock();
            entry = _policy.allocateEntry(_delegate, key, null);
            writeLock.unlock();
        }
        return entry;
    }

    public ICacheEntry<K, V> allocateEntryForClock(final K key, final ILock lock) {
        ensureNotNull(key);
        ICacheEntry<K, V> entry = _delegate.get(key);
        if(entry != null && entry.pin()) {
            _policy.recordAccess(entry);
        } else {
            lock.lock();
            entry = _policy.allocateEntry(_delegate, key, null);
            lock.unlock();
        }
        return entry;
    }

    @Override
    public V put(K key, V value) {
        ensureNotNull(key, value);
        final ICacheEntry<K, V> oldEntry = _delegate.get(key);
        if(oldEntry != null) {
            V oldValue = dereferenceValue(oldEntry);
            oldEntry.setValue(value); // REVIEWME
            _policy.recordAccess(oldEntry);
            return oldValue;
        } else {
            return _policy.addEntry(_delegate, key, value, true);
        }
    }

    public V putIfAbsent(K key, V value) {
        ensureNotNull(key, value);
        final ICacheEntry<K, V> oldEntry = _delegate.get(key);
        if(oldEntry != null) {
            V oldValue = dereferenceValue(oldEntry);
            oldEntry.setValue(value); // REVIEWME
            _policy.recordAccess(oldEntry);
            return oldValue;
        } else {
            return _policy.addEntry(_delegate, key, value, false);
        }
    }

    @Override
    public V remove(Object key) {
        ensureNotNull(key);
        final ICacheEntry<K, V> entry = _delegate.remove(key);
        if(entry != null) {
            _policy.recordRemoval(entry);
            return dereferenceValue(entry);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public boolean remove(Object key, Object value) {
        ensureNotNull(key, value);
        CacheEntry probe = new CacheEntry(key, value);
        return _delegate.remove(key, probe);
    }

    private transient volatile Set<Map.Entry<K, V>> _entrySet = null;

    /**
     * Unsupported.
     * @throws UnsupportedOperationException
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entrySet = _entrySet;
        if(entrySet == null) {
            entrySet = new EntrySet();
            _entrySet = entrySet;
        }
        return entrySet;
    }

    @Override
    public void clear() {
        _delegate.clear();
        this._policy = ReplacementPolicySelector.provide(_policy.getMaxCapacity());
    }

    @Override
    public boolean containsKey(Object key) {
        ensureNotNull(key);
        return _delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return _delegate.containsValue(value);
    }

    @Override
    public boolean isEmpty() {
        return _delegate.isEmpty();
    }

    @Override
    public int size() {
        return _delegate.size();
    }

    private static void ensureNotNull(final Object obj) {
        if(obj == null) {
            throw new NullPointerException();
        }
    }

    private static <V> void ensureNotNull(final Object key, final V value) {
        if(key == null) {
            throw new NullPointerException();
        }
        if(value == null) {
            throw new NullPointerException();
        }
    }

    private static <K, V> V dereferenceValue(final ICacheEntry<K, V> value) {
        if(value == null) {
            return null;
        }
        return value.getValue();
    }

    private Entry dereferenceEntry(final Map.Entry<K, ICacheEntry<K, V>> entry) {
        final V value = dereferenceValue(entry.getValue());
        return (value == null) ? null : new Entry(entry.getKey(), value);
    }

    private final class Entry implements Map.Entry<K, V> {
        final K key;
        V value;

        public Entry(K key, V value) {
            assert (key != null);
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V newValue) {
            this.value = newValue;
            return put(key, newValue);
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof ConcurrentPluggableCache.Entry)) {
                return false;
            }
            ConcurrentPluggableCache.Entry entry = (ConcurrentPluggableCache.Entry) obj;
            return key.equals(entry.key) && value.equals(entry.value);
        }

        @Override
        public int hashCode() {
            return key.hashCode() * 31 + value.hashCode();
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }

    }

    private final class EntryIterator implements Iterator<Map.Entry<K, V>> {

        private final Iterator<Map.Entry<K, ICacheEntry<K, V>>> itor;

        private Map.Entry<K, V> _next, _lastReturned;

        public EntryIterator() {
            this.itor = _delegate.entrySet().iterator();
        }

        private void advance() {
            while(itor.hasNext()) {
                Map.Entry<K, V> entry = dereferenceEntry(itor.next());
                if(entry != null) {
                    _next = entry;
                    return;
                }
            }
            _next = null;
        }

        public boolean hasNext() {
            return _next != null;
        }

        public Map.Entry<K, V> next() {
            if(_next == null) {
                throw new NoSuchElementException();
            }
            _lastReturned = _next;
            advance();
            return _lastReturned;
        }

        public void remove() {
            ConcurrentPluggableCache.this.remove(_lastReturned.getKey());
        }

    }

    private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public int size() {
            return _delegate.size();
        }

        @Override
        public void clear() {
            _delegate.clear();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean contains(Object o) {
            if(!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<K, V> e = (Map.Entry<K, V>) o;
            V v = ConcurrentPluggableCache.this.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean remove(Object o) {
            if(!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<K, V> e = (Map.Entry<K, V>) o;
            return ConcurrentPluggableCache.this.remove(e.getKey(), e.getValue());
        }

    }

    public V replace(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public boolean replace(K key, V oldValue, V newValue) {
        throw new UnsupportedOperationException();
    }

}
