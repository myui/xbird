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

import xbird.util.collections.longs.LongHash.LongLRUMap;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ObservableLongLRUMap<V> extends LongLRUMap<V> {
    private static final long serialVersionUID = 2481614187542943334L;

    private final int purgeUnits;
    private final Cleaner<V> cleaner;

    public ObservableLongLRUMap(int limit, Cleaner<V> cleaner) {
        this(limit, 1, cleaner);
    }

    public ObservableLongLRUMap(int limit, int purgeUnits, Cleaner<V> cleaner) {
        super(limit);
        if(limit < purgeUnits) {
            throw new IllegalArgumentException("limit '" + limit + "' < pergeUnits '" + purgeUnits
                    + '\'');
        }
        if(purgeUnits < 1) {
            throw new IllegalArgumentException("Illegal purgeUnits: " + purgeUnits);
        }
        this.purgeUnits = purgeUnits;
        this.cleaner = cleaner;
    }

    @Override
    protected void addEntry(int bucket, long key, V value, xbird.util.collections.longs.LongHash.BucketEntry<V> next) {
        final ChainedEntry<V> newEntry = new ChainedEntry<V>(key, value, next);
        this._buckets[bucket] = newEntry;
        newEntry.addBefore(entryChainHeader);
        ++_size;
        ChainedEntry eldest = entryChainHeader.next;
        if(removeEldestEntry()) {
            V removed = remove(eldest.key);
            if(removed != null) {
                cleaner.cleanup(eldest.key, removed);
            }
            for(int i = 1; i < purgeUnits; i++) {
                final ChainedEntry iEldest = entryChainHeader.next;
                final V iRemoved = remove(iEldest.key);
                if(iRemoved != null) {
                    cleaner.cleanup(iEldest.key, iRemoved);
                }
            }
        } else {
            if(_size > _threshold) {
                resize(2 * _buckets.length);
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
