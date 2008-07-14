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
package xbird.util.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implementation of a cache with the full two-queue page replacement policy.
 * <DIV lang="en">
 * Get detail on the following paper.
 * Theodore Johnson, Dennis Shasha: 2Q: A Low Overhead High Performance Buffer Management Replacement Algorithm.
 * In Proc. VLDB, pp.439-450, 1994.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class Int2QCache<V> extends IntHash<V> {
    private static final long serialVersionUID = 7811241071141908561L;

    public static final int MAIN = 1, IN = 2, OUT = 3, NIL = 0;

    private final int maxCapacity;
    private float percentMain = 0.5f, percentIn = 0.2f;

    protected final ChainedEntry<V> headIn; // fifo
    protected final ChainedEntry<V> headOut; // 2nd chance (as hot queue)
    protected final ChainedEntry<V> headMain; // hot (LRU)

    protected int sizeMain, sizeIn, sizeOut;
    protected int maxMain, maxIn, maxOut;

    public Int2QCache(int limit) {
        super(limit, 1.0f);
        this.maxCapacity = limit;
        this.headIn = new ChainedEntry<V>(-1, null, null);
        this.headOut = new ChainedEntry<V>(-1, null, null);
        this.headMain = new ChainedEntry<V>(-1, null, null);
        initEntryChain();
    }

    private void initEntryChain() {
        headIn.next = headIn.prev = headIn;
        headOut.next = headOut.prev = headOut;
        headMain.next = headMain.prev = headMain;
        sizeIn = sizeOut = sizeMain = 0;
        recalcMax();
    }

    public void setPercentIn(float percent) {
        this.percentIn = percent;
        recalcMax();
    }

    public void setPercentMain(float percent) {
        this.percentMain = percent;
        recalcMax();
    }

    private void recalcMax() {
        maxMain = (int) (maxCapacity * percentMain);
        maxIn = (int) (maxCapacity * percentIn);
        maxOut = maxCapacity - maxMain - maxIn;
    }

    @Override
    protected void addEntry(int bucket, int key, V value, BucketEntry<V> next) {
        final ChainedEntry<V> newEntry = new ChainedEntry<V>(key, value, next);
        this._buckets[bucket] = newEntry;

        if(isInQueueFull()) {
            ChainedEntry<V> tailIn = headIn.next;
            if(tailIn != headIn) {
                tailIn.remove();
                sizeIn--;
                tailIn.type = OUT;
                addToFront(headOut, tailIn);
                sizeOut++;
            }
            if(sizeOut > maxOut) {
                ChainedEntry<V> tailOut = headOut.next;
                if(tailOut != headOut) {
                    purge(tailOut.key);
                    sizeOut--;
                }
            }
        } else if(isFull()) {
            // remove coldest
            ChainedEntry<V> tailMain = headMain.next;
            if(tailMain != headMain) {
                purge(tailMain.key);
                sizeMain--;
            }
        }
        newEntry.type = IN;
        addToFront(headIn, newEntry);
        sizeIn++;
        _size++;
    }

    private static final <V> void addToFront(final ChainedEntry<V> head, final ChainedEntry<V> e) {
        e.addBefore(head);
    }

    protected final boolean isFull() {
        return size() >= maxCapacity;
    }

    protected final boolean isInQueueFull() {
        return sizeIn > maxIn;
    }

    protected final boolean isMainQueueFull() {
        return sizeMain > maxMain;
    }

    @Override
    public synchronized void clear() {
        initEntryChain();
        super.clear();
    }

    @Override
    public final EntriesIterator iterator() {
        return new EntriesIterator();
    }

    private static final class ChainedEntry<V> extends BucketEntry<V> {
        private static final long serialVersionUID = -2234150049934401324L;

        private ChainedEntry<V> prev, next;
        private int type = NIL;

        ChainedEntry(int key, V value, BucketEntry<V> next) {
            super(key, value, next);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void recordAccess(final IntHash m) {
            final Int2QCache lm = (Int2QCache) m;
            switch(type) {
                case MAIN: // move tail entry to head of Am
                    remove();
                    addToFront(lm.headMain, this);
                    break;
                case OUT: // move from A1out to Am
                    if(lm.sizeMain >= lm.maxMain) {// workaround for a bug in full-2Q algorithm
                        // move the coldest entry in Am to the tail of A1out (likely to be removed) 
                        ChainedEntry<V> tailMain = lm.headMain.next;
                        assert (tailMain != lm.headMain);
                        tailMain.remove();
                        lm.sizeMain--;
                        tailMain.type = OUT;
                        assert (lm.headOut != lm.headOut.next);
                        addToFront(lm.headOut.next, tailMain);
                        lm.sizeOut++;
                    }
                    remove();
                    lm.sizeOut--;
                    this.type = MAIN;
                    addToFront(lm.headMain, this);
                    lm.sizeMain++;
                    break;
                case IN: // do nothing                    
                    break;
                default:
                    throw new IllegalStateException("Unexpected type: " + type);
            }
        }

        @Override
        protected void recordRemoval(final IntHash m) {
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
         * <pre>existingEntry.PREV <-> this <-> existingEntry - existingEntry.NEXT</pre>
         */
        private void addBefore(ChainedEntry<V> existingEntry) {
            next = existingEntry;
            prev = existingEntry.prev;
            prev.next = this;
            next.prev = this;
        }
    }

    private final class EntriesIterator implements Iterator<BucketEntry<V>> {

        private ChainedEntry<V> entry;
        private int curQueue = IN;

        public EntriesIterator() {
            this.entry = headIn;
        }

        public boolean hasNext() {
            switch(curQueue) {
                case IN:
                    if(entry.next != headIn) {
                        return true;
                    }
                case MAIN:
                    if(entry.next != headMain) {
                        return true;
                    }
                case OUT:
                    if(entry.next != headOut) {
                        return true;
                    }
                default:
                    return false;
            }
        }

        public BucketEntry<V> next() {
            entry = entry.next;
            switch(curQueue) {
                case IN:
                    if(entry != headIn) {
                        break;
                    }
                    this.curQueue = MAIN;
                    this.entry = headMain;
                case MAIN:
                    if(entry != headMain) {
                        break;
                    }
                    this.curQueue = OUT;
                    this.entry = headOut;
                case OUT:
                    if(entry != headOut) {
                        break;
                    }
                    this.curQueue = NIL;
                    this.entry = null;
                default:
                    throw new NoSuchElementException();
            }
            return entry;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
