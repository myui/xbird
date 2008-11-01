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
package xbird.util.collections.longs;

/**
 * 
 * <DIV lang="en">
 * Get detail on the following paper.
 * Theodore Johnson, Dennis Shasha: 2Q: A Low Overhead High Performance Buffer Management Replacement Algorithm.
 * In Proc. VLDB, pp.439-450, 1994.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ObservableLong2QCache<V> extends Long2QCache<V> {
    private static final long serialVersionUID = 1672606884853339665L;

    private final int purgeUnits;
    private final Cleaner<V> cleaner;

    public ObservableLong2QCache(int limit, Cleaner<V> cleaner) {
        this(limit, 1, cleaner);
    }

    public ObservableLong2QCache(int limit, int purgeUnits, Cleaner<V> cleaner) {
        super(limit);
        if(limit < purgeUnits) {
            throw new IllegalArgumentException("limit '" + limit + "' < pergeUnits '" + purgeUnits
                    + '\'');
        }
        if(purgeUnits < 1) {
            throw new IllegalArgumentException("Illegal purgeUnits: " + purgeUnits);
        }
        this.purgeUnits = Math.max(purgeUnits, maxOut);
        this.cleaner = cleaner;
    }

    @Override
    protected void addEntry(int bucket, long key, V value, BucketEntry<V> next) {
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
                    purge(tailOut);
                    sizeOut--;
                }
            }
        } else if(isFull()) { // remove coldest
            ChainedEntry<V> tailMain = headMain.next;
            if(tailMain != headMain) {
                purge(tailMain);
                sizeMain--;
            }
        }
        newEntry.type = IN;
        addToFront(headIn, newEntry);
        sizeIn++;
        _size++;
    }

    private final void purge(ChainedEntry<V> e) {
        V removed = remove(e.key);
        if(removed != null) {
            cleaner.cleanup(e.key, removed);
        }
        for(int i = 1; i < purgeUnits; i++) {
            final ChainedEntry outEldest = headOut.next;
            final V outRemoved = remove(outEldest.key);
            if(outRemoved != null) {
                cleaner.cleanup(outEldest.key, outRemoved);
            }
        }
    }

    public void purgeAll() {
        for(BucketEntry<V> e : this) {
            cleaner.cleanup(e.key, e.value);
        }
        clear();
    }
}
