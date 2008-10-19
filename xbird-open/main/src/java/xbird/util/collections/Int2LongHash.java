/*
 * @(#)$Id: Int2LongHash.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.collections;

import java.io.*;
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
public class Int2LongHash implements Serializable, Iterable<Int2LongHash.BucketEntry> {
    private static final long serialVersionUID = 1L;

    private static final float DEFAULT_LOAD_FACTOR = 0.7f;

    protected BucketEntry[] _buckets;
    private final float _loadFactor;
    protected int _threshold;
    protected int _size = 0;

    public Int2LongHash(int size) {
        this(size, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Create a hash table that can comfortably hold the specified number of entries.
     * The actual table created to be is the smallest prime greater than size * 2.
     */
    public Int2LongHash(int size, float loadFactor) {
        assert (size > 0) : size;
        int bucketSize = Primes.findLeastPrimeNumber(size * 2);
        this._buckets = new BucketEntry[bucketSize];
        this._loadFactor = loadFactor;
        this._threshold = (int) (size * loadFactor);
    }

    public boolean contains(int key) {
        int bucket = indexFor(key, _buckets.length);
        for(BucketEntry e = _buckets[bucket]; e != null; e = e.next) {
            if(key == e.key) {
                return true;
            }
        }
        return false;
    }

    public long get(int key) {
        int bucket = indexFor(key, _buckets.length);
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
    public long put(int key, long value) {
        assert (value != -1L);
        int bucket = indexFor(key, _buckets.length);
        // find an entry
        BucketEntry e;
        for(e = _buckets[bucket]; e != null; e = e.next) {
            if(key == e.key) {
                long replaced = e.value;
                e.value = value;
                e.recordAccess(this);
                return replaced; // found
            }
        }
        // if not found, create a new entry.
        addEntry(bucket, key, value, _buckets[bucket]);
        return -1L;
    }

    protected void addEntry(int bucket, int key, long value, BucketEntry next) {
        BucketEntry entry = new BucketEntry(key, value, next);
        this._buckets[bucket] = entry;
        if(++_size > _threshold) {
            resize(2 * _buckets.length);
        }
    }

    public long remove(int key) {
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
        BucketEntry[] newTable = new BucketEntry[newCapacity];
        rehash(newTable);
        this._buckets = newTable;
        this._threshold = (int) (newCapacity * _loadFactor);
    }

    private void rehash(BucketEntry[] newTable) {
        int oldsize = _buckets.length;
        int newsize = newTable.length;
        for(int i = 0; i < oldsize; i++) {
            BucketEntry oldEntry = _buckets[i];
            while(oldEntry != null) {
                BucketEntry e = oldEntry;
                oldEntry = oldEntry.next;
                int bucket = indexFor(e.key, newsize);
                e.next = newTable[bucket];
                newTable[bucket] = e;
            }
        }
    }

    private static int indexFor(int key, int length) {
        return (key & 0x7fffffff) % (length - 1);
    }

    public static class BucketEntry implements Externalizable {

        int key;
        long value;
        BucketEntry next;

        BucketEntry() {}
        
        BucketEntry(int key, long value, BucketEntry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        BucketEntry(int key, long value) {
            this(key, value, null);
        }

        public final int getKey() {
            return key;
        }

        public final long getValue() {
            return value;
        }

        public BucketEntry getNext() {
            return next;
        }

        @Override
        public String toString() {
            return new StringBuilder(48).append(key).append('/').append(value).toString();
        }

        void recordAccess(Int2LongHash m) {}

        void recordRemoval(Int2LongHash m) {}

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this.key = in.readInt();
            this.value = in.readLong();
            boolean hasNext = in.readBoolean();
            BucketEntry cur = this;
            while(hasNext) {
                int k = in.readInt();
                long v = in.readLong();
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
                out.writeLong(cur.value);
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

    @Override
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

    public static final class Int2LongLRUMap extends Int2LongHash {
        private static final long serialVersionUID = 1L;

        private final int maxCapacity;
        private final transient ChainedEntry entryChainHeader;

        public Int2LongLRUMap(int limit) {
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
        protected void addEntry(int bucket, int key, long value, BucketEntry next) {
            ChainedEntry newEntry = new ChainedEntry(key, value, next);
            this._buckets[bucket] = newEntry;
            newEntry.addBefore(entryChainHeader);
            ++_size;            
            if(removeEldestEntry()) {
                ChainedEntry eldest = entryChainHeader.next;
                remove(eldest.key);
            } else {
                if(_size > _threshold) {
                    resize(2 * _buckets.length);
                }
            }
        }

        private boolean removeEldestEntry() {
            return size() > maxCapacity;
        }

        @Override
        public Iterator<BucketEntry> iterator() {
            return new OrderIterator(entryChainHeader);
        }

        private static final class ChainedEntry extends BucketEntry {
            private static final long serialVersionUID = -4864620734808326464L;
            
            private ChainedEntry prev, next;

            ChainedEntry(int key, long value, BucketEntry next) {
                super(key, value, next);
            }

            @Override
            void recordAccess(Int2LongHash m) {
                this.remove();
                Int2LongLRUMap lm = (Int2LongLRUMap) m;
                addBefore(lm.entryChainHeader);
            }

            @Override
            void recordRemoval(Int2LongHash m) {
                this.remove();
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

        private final class OrderIterator implements Iterator<BucketEntry> {

            private ChainedEntry entry;

            public OrderIterator(ChainedEntry e) {
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

}
