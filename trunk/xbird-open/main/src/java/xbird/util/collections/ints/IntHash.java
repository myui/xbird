/*
 * @(#)$Id: IntHash.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.collections.ints;

import java.io.Closeable;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.NoSuchElementException;

import xbird.util.hashes.HashUtils;
import xbird.xquery.dm.dtm.PagingProfile;

/**
 * ChainedHash implementation for int to Object hash.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class IntHash<V> implements Externalizable, Iterable<IntHash.BucketEntry<V>>, Closeable {
    private static final long serialVersionUID = 1L;
    private static final float DEFAULT_LOAD_FACTOR = 0.7f;

    private transient float _loadFactor = DEFAULT_LOAD_FACTOR;

    protected BucketEntry<V>[] _buckets;
    protected int _mask;
    protected int _threshold;
    protected int _size = 0;

    private transient PagingProfile _profile = null;

    /**
     * Create a hash table that can comfortably hold the specified number of entries.
     * The actual table created to be is the smallest prime greater than size * 2.
     */
    public IntHash(int size, float loadFactor) {
        int bucketSize = HashUtils.nextPowerOfTwo(size);
        this._buckets = new BucketEntry[bucketSize];
        this._mask = bucketSize - 1;
        this._loadFactor = loadFactor;
        this._threshold = (int) (size * loadFactor);
    }

    public IntHash(int size) {
        this(size, DEFAULT_LOAD_FACTOR);
    }

    public IntHash() {}//for Object Serialization

    public int size() {
        return _size;
    }

    /**
     * Put an entry for the given key number.
     * If one already exists, old value is replaced.
     * 
     * @return old value for the given key. if not found, return -1.
     */
    public V put(int key, V value) {
        final int bucket = indexFor(key, _mask);
        // find an entry
        final BucketEntry<V> first = _buckets[bucket];
        for(BucketEntry<V> e = first; e != null; e = e.next) {
            if(key == e.key) {
                V replaced = e.value;
                e.value = value;
                e.recordAccess(this);
                return replaced; // found
            }
        }
        // if not found, create a new entry.
        addEntry(bucket, key, value, first);
        return null;
    }

    public V putIfAbsent(int key, V value) {
        final int bucket = indexFor(key, _mask);
        // find an entry
        final BucketEntry<V> first = _buckets[bucket];
        for(BucketEntry<V> e = first; e != null; e = e.next) {
            if(key == e.key) {
                return e.value;
            }
        }
        // if not found, create a new entry.
        addEntry(bucket, key, value, first);
        return null;
    }

    public boolean contains(int key) {
        final int bucket = indexFor(key, _mask);
        for(BucketEntry e = _buckets[bucket]; e != null; e = e.next) {
            if(key == e.key) {
                return true;
            }
        }
        return false;
    }

    public final V get(final int key) {
        final BucketEntry<V>[] entries = _buckets;
        final int bucket = indexFor(key, _mask);
        for(BucketEntry<V> e = entries[bucket]; e != null; e = e.next) {
            if(key == e.key) {
                e.recordAccess(this);
                return e.value;
            }
        }
        return null;
    }

    public synchronized void clear() {
        BucketEntry tab[] = _buckets;
        for(int i = tab.length; --i >= 0;) {
            tab[i] = null;
        }
        this._size = 0;
    }

    protected final V purge(final int key) {
        if(_profile != null) {
            _profile.recordPurgation(key);
        }
        return this.remove(key);
    }

    public V remove(final int key) {
        final int bucket = indexFor(key, _mask);
        // find an entry
        BucketEntry<V> e, prev = null;
        for(e = _buckets[bucket]; e != null; prev = e, e = e.next) {
            if(key == e.key) {
                if(prev != null) {
                    prev.next = e.next;
                } else {
                    _buckets[bucket] = e.next;
                }
                --_size;
                e.recordRemoval(this);
                return e.value;
            }
        }
        return null;
    }

    public static class BucketEntry<V> implements Externalizable {
        private static final long serialVersionUID = 1L;

        int key;
        V value;
        BucketEntry<V> next;

        BucketEntry(int key, V value, BucketEntry<V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        private BucketEntry(int key, V value) {
            this(key, value, null);
        }

        public BucketEntry() {}// for serialization

        public int getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this.key = in.readInt();
            this.value = (V) in.readObject();
            boolean hasNext = in.readBoolean();
            BucketEntry<V> cur = this;
            while(hasNext) {
                final int k = in.readInt();
                final V v = (V) in.readObject();
                BucketEntry<V> n = new BucketEntry<V>(k, v);
                cur.next = n;
                cur = n;
                hasNext = in.readBoolean();
            }
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            assert (value != null);
            BucketEntry<V> cur = this;
            while(true) {
                out.writeInt(cur.key);
                out.writeObject(cur.value);
                if(cur.next != null) {// hasNext
                    out.writeBoolean(true);
                    cur = cur.next;
                } else {
                    out.writeBoolean(false);
                    break;
                }
            }
        }

        @Override
        public String toString() {
            return new StringBuilder(64).append(key).append('/').append(value).toString();
        }

        protected void recordAccess(IntHash m) {}

        protected void recordRemoval(IntHash m) {}

    }

    protected void addEntry(int bucket, int key, V value, BucketEntry<V> next) {
        final BucketEntry<V> entry = new BucketEntry<V>(key, value, next);
        this._buckets[bucket] = entry;
        if(++_size > _threshold) {
            resize(_buckets.length << 1);
        }
    }

    protected void resize(int newCapacity) {
        int adequateSize = HashUtils.nextPowerOfTwo(newCapacity);
        BucketEntry<V>[] newTable = new BucketEntry[adequateSize];
        this._mask = newCapacity - 1;
        rehash(newTable, newCapacity);
        this._buckets = newTable;
        this._threshold = (int) (newCapacity * _loadFactor);
    }

    private void rehash(final BucketEntry<V>[] newTable, final int newsize) {
        final int oldsize = _buckets.length;
        for(int i = 0; i < oldsize; i++) {
            BucketEntry<V> oldEntry = _buckets[i];
            while(oldEntry != null) {
                BucketEntry<V> e = oldEntry;
                oldEntry = oldEntry.next;
                int bucket = indexFor(e.key, _mask);
                e.next = newTable[bucket];
                newTable[bucket] = e;
            }
        }
    }

    private static final int indexFor(final int key, final int mask) {
        return (key & 0x7fffffff) & mask;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._threshold = in.readInt();
        this._size = in.readInt();
        final int blen = in.readInt();
        this._buckets = new BucketEntry[blen];
        for(int i = 0; i < blen; i++) {
            _buckets[i] = (BucketEntry<V>) in.readObject();
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_threshold);
        out.writeInt(_size);
        out.writeInt(_buckets.length);
        for(int i = 0; i < _buckets.length; i++) {
            out.writeObject(_buckets[i]);
        }
    }

    public Iterator<BucketEntry<V>> iterator() {
        return new IntIterator<V>();
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(512);
        for(BucketEntry e : this) {
            if(buf.length() > 0) {
                buf.append(", ");
            }
            buf.append('{');
            buf.append(e.key);
            buf.append('/');
            buf.append(e.value);
            buf.append('}');
        }
        return buf.toString();
    }

    private final class IntIterator<V> implements Iterator<BucketEntry<V>> {

        private int cursor = 0;
        private int curBucketIndex = 0;
        private BucketEntry<V> curBucket = null;

        IntIterator() {}

        public boolean hasNext() {
            return _size > cursor;
        }

        public BucketEntry<V> next() {
            ++cursor;
            if(curBucket == null) {
                for(int i = curBucketIndex; i < _buckets.length; i++) {
                    BucketEntry e = _buckets[i];
                    if(e != null) {
                        this.curBucket = e;
                        this.curBucketIndex = i + 1;
                        break;
                    }
                }
            }
            if(curBucket != null) {
                BucketEntry<V> e = curBucket;
                this.curBucket = e.next;
                return e;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static final class IntLRUMap<V> extends IntHash<V> {
        private static final long serialVersionUID = -9193071605707035207L;

        private final int maxCapacity;

        private final transient ChainedEntry<V> entryChainHeader;

        public IntLRUMap(final int limit) {
            super(limit, 1.0f);
            this.maxCapacity = limit;
            this.entryChainHeader = initEntryChain();
        }

        private ChainedEntry<V> initEntryChain() {
            ChainedEntry<V> header = new ChainedEntry<V>(-1, null, null);
            header.prev = header.next = header;
            return header;
        }

        @Override
        protected void addEntry(int bucket, int key, V value, BucketEntry<V> next) {
            final ChainedEntry<V> newEntry = new ChainedEntry<V>(key, value, next);
            this._buckets[bucket] = newEntry;
            newEntry.addBefore(entryChainHeader);
            ++_size;
            ChainedEntry eldest = entryChainHeader.next;
            if(removeEldestEntry(eldest)) {
                purge(eldest.key);
            } else {
                if(_size > _threshold) {
                    resize(2 * _buckets.length);
                }
            }
        }

        private final boolean removeEldestEntry(BucketEntry eldest) {
            return size() > maxCapacity;
        }

        private static final class ChainedEntry<V> extends BucketEntry<V> {
            private static final long serialVersionUID = 1L;

            private ChainedEntry<V> prev, next;

            ChainedEntry(int key, V value, BucketEntry<V> next) {
                super(key, value, next);
            }

            @Override
            protected void recordAccess(IntHash m) {
                remove();
                IntLRUMap lm = (IntLRUMap) m;
                addBefore(lm.entryChainHeader);
            }

            @Override
            protected void recordRemoval(IntHash m) {
                remove();
            }

            /**
             * Removes this entry from the linked list.
             */
            private void remove() {
                prev.next = next;
                next.prev = prev;
                prev = null;
                next = null;
            }

            /**
             * Inserts this entry before the specified existing entry in the list.
             */
            private void addBefore(ChainedEntry<V> existingEntry) {
                next = existingEntry;
                prev = existingEntry.prev;
                prev.next = this;
                next.prev = this;
            }
        }

        public Iterator<BucketEntry<V>> iterator() {
            return new OrderIterator<V>(entryChainHeader);
        }

        private class OrderIterator<V> implements Iterator<BucketEntry<V>> {

            private ChainedEntry<V> entry;

            public OrderIterator(ChainedEntry<V> e) {
                if(e == null) {
                    throw new IllegalArgumentException();
                }
                this.entry = e;
            }

            public boolean hasNext() {
                return entry.next != entryChainHeader;
            }

            public BucketEntry<V> next() {
                entry = entry.next;
                if(entry == entryChainHeader) {
                    throw new NoSuchElementException();
                }
                return entry;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }

    public void setPagingProfile(final PagingProfile profile) {
        this._profile = profile;
    }

    public void close() throws IOException {
        this._buckets = null;
    }

}
