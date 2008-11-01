/*
 * @(#)$Id: Int2IntHash.java 3619 2008-03-26 07:23:03Z yui $
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
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import xbird.util.math.Primes;

/**
 * ChainedHash implementation for int to int hash.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class Int2IntHash implements Serializable, Iterable<Int2IntHash.BucketEntry>, Closeable {
    private static final long serialVersionUID = 1L;

    private static final float DEFAULT_LOAD_FACTOR = 0.7f;

    protected BucketEntry[] _buckets;
    private final float _loadFactor;
    protected int _threshold;
    protected int _size = 0;

    public Int2IntHash(int size) {
        this(size, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Create a hash table that can comfortably hold the specified number of entries.
     * The actual table created to be is the smallest prime greater than size * 2.
     */
    public Int2IntHash(int size, float loadFactor) {
        assert (size > 0) : size;
        final int bucketSize = Primes.findLeastPrimeNumber(size * 2);
        this._buckets = new BucketEntry[bucketSize];
        this._loadFactor = loadFactor;
        this._threshold = (int) (size * loadFactor);
    }

    public boolean contains(int key) {
        final int bucket = indexFor(key, _buckets.length);
        for(BucketEntry e = _buckets[bucket]; e != null; e = e.next) {
            if(key == e.key) {
                return true;
            }
        }
        return false;
    }

    public int get(int key) {
        final int bucket = indexFor(key, _buckets.length);
        for(BucketEntry e = _buckets[bucket]; e != null; e = e.next) {
            if(key == e.key) {
                e.recordAccess(this);
                return e.value;
            }
        }
        return -1;
    }

    /**
     * Put an entry for the given key number.
     * If one already exists, old value is replaced.
     * 
     * @return old value for the given key. if not found, return -1.
     */
    public int put(int key, int value) {
        assert (value != -1);
        final int bucket = indexFor(key, _buckets.length);
        // find an entry
        BucketEntry e;
        for(e = _buckets[bucket]; e != null; e = e.next) {
            if(key == e.key) {
                final int replaced = e.value;
                e.value = value;
                e.recordAccess(this);
                return replaced; // found
            }
        }
        // if not found, create a new entry.
        addEntry(bucket, key, value, _buckets[bucket]);
        return -1;
    }

    protected void addEntry(int bucket, int key, int value, BucketEntry next) {
        final BucketEntry entry = new BucketEntry(key, value, next);
        this._buckets[bucket] = entry;
        if(++_size > _threshold) {
            resize(2 * _buckets.length);
        }
    }

    public int remove(int key) {
        final int bucket = indexFor(key, _buckets.length);
        // find an entry
        BucketEntry e, prev = null;
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
        return -1;
    }

    public int size() {
        return _size;
    }

    protected void resize(int newCapacity) {
        final BucketEntry[] newTable = new BucketEntry[newCapacity];
        rehash(newTable);
        this._buckets = newTable;
        this._threshold = (int) (newCapacity * _loadFactor);
    }

    private void rehash(BucketEntry[] newTable) {
        final int oldsize = _buckets.length;
        final int newsize = newTable.length;
        for(int i = 0; i < oldsize; i++) {
            BucketEntry oldEntry = _buckets[i];
            while(oldEntry != null) {
                BucketEntry e = oldEntry;
                oldEntry = oldEntry.next;
                final int bucket = indexFor(e.key, newsize);
                e.next = newTable[bucket];
                newTable[bucket] = e;
            }
        }
    }

    private static int indexFor(final int key, final int length) {
        return (key & 0x7fffffff) % (length - 1);
    }

    public static class BucketEntry implements Externalizable {
        private static final long serialVersionUID = 1L;

        int key;

        BucketEntry next;

        int value;

        public BucketEntry() {}

        BucketEntry(int key, int value, BucketEntry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        private BucketEntry(int key, int value) {
            this(key, value, null);
        }

        public int getKey() {
            return key;
        }

        public int getValue() {
            return value;
        }

        public BucketEntry getNext() {
            return next;
        }

        @Override
        public String toString() {
            return new StringBuilder(48).append(key).append('/').append(value).toString();
        }

        protected void recordAccess(Int2IntHash m) {}

        protected void recordRemoval(Int2IntHash m) {}

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this.key = in.readInt();
            this.value = in.readInt();
            boolean hasNext = in.readBoolean();
            BucketEntry cur = this;
            while(hasNext) {
                final int k = in.readInt();
                final int v = in.readInt();
                BucketEntry n = new BucketEntry(k, v);
                cur.next = n;
                cur = n;
                hasNext = in.readBoolean();
            }
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            BucketEntry cur = this;
            while(true) {
                out.writeInt(cur.key);
                out.writeInt(cur.value);
                if(cur.next != null) {// hasNext
                    out.writeBoolean(true);
                    cur = cur.next;
                } else {
                    out.writeBoolean(false);
                    break;
                }
            }
        }
    }

    public Iterator<BucketEntry> iterator() {
        return new IntIterator();
    }

    public String toString() {
        final int len = size() * 10 + 2;
        final StringBuilder buf = new StringBuilder(len);
        buf.append('{');
        final Iterator<BucketEntry> itor = iterator();
        while(itor.hasNext()) {
            BucketEntry e = itor.next();
            buf.append(e.key);
            buf.append('=');
            buf.append(e.value);
            if(itor.hasNext()) {
                buf.append(',');
            }
        }
        buf.append('}');
        return buf.toString();
    }

    private final class IntIterator implements Iterator<BucketEntry> {

        private int cursor = 0;

        private int curBucketIndex = 0;

        private BucketEntry curBucket = null;

        IntIterator() {}

        public boolean hasNext() {
            return _size > cursor;
        }

        public BucketEntry next() {
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
                BucketEntry e = curBucket;
                this.curBucket = e.next;
                return e;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static final class Int2IntLRUMap extends Int2IntHash {
        private static final long serialVersionUID = 1L;

        private final int maxCapacity;

        private final transient ChainedEntry entryChainHeader;

        public Int2IntLRUMap(int limit) {
            super(limit, 1.0f);
            this.maxCapacity = limit;
            this.entryChainHeader = initEntryChain();
        }

        private ChainedEntry initEntryChain() {
            ChainedEntry header = new ChainedEntry(-1, -1, null);
            header.prev = header.next = header;
            return header;
        }

        @Override
        protected void addEntry(final int bucket, final int key, final int value, final BucketEntry next) {
            final ChainedEntry newEntry = new ChainedEntry(key, value, next);
            this._buckets[bucket] = newEntry;
            newEntry.addBefore(entryChainHeader);
            ++_size;
            ChainedEntry eldest = entryChainHeader.next;
            if(removeEldestEntry(eldest)) {
                remove(eldest.key);
            } else {
                if(_size > _threshold) {
                    resize(2 * _buckets.length);
                }
            }
        }

        private final boolean removeEldestEntry(BucketEntry eldest) {
            return size() > maxCapacity;
        }

        private static final class ChainedEntry extends BucketEntry {
            private static final long serialVersionUID = -8782644116023761439L;

            private ChainedEntry prev, next;

            ChainedEntry(int key, int value, BucketEntry next) {
                super(key, value, next);
            }

            @Override
            protected void recordAccess(Int2IntHash m) {
                remove();
                Int2IntLRUMap lm = (Int2IntLRUMap) m;
                addBefore(lm.entryChainHeader);
            }

            @Override
            protected void recordRemoval(Int2IntHash m) {
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
            private void addBefore(ChainedEntry existingEntry) {
                next = existingEntry;
                prev = existingEntry.prev;
                prev.next = this;
                next.prev = this;
            }
        }

        @Override
        public Iterator<BucketEntry> iterator() {
            return new OrderIterator(entryChainHeader);
        }

        private final class OrderIterator implements Iterator<BucketEntry> {

            private ChainedEntry entry;

            public OrderIterator(final ChainedEntry e) {
                assert (e != null);
                this.entry = e;
            }

            public boolean hasNext() {
                return entry.next != entryChainHeader;
            }

            public BucketEntry next() {
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

    public void close() throws IOException {
        this._buckets = null;
    }

}
