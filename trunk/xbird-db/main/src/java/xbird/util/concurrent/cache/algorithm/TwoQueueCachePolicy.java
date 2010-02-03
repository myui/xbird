/*
 * @(#)$Id$
 *
 * Copyright 2008 Makoto YUI
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

import java.util.concurrent.ConcurrentMap;

import xbird.util.cache.Cleaner;
import xbird.util.cache.ICacheEntry;
import xbird.util.concurrent.cache.ReplacementPolicy;
import xbird.util.concurrent.cache.helpers.NopCleaner;

/**
 * 
 * <DIV lang="en"></DIV> <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class TwoQueueCachePolicy<K, V>
        implements ReplacementPolicy<K, V, TwoQueueCacheEntry<K, V>> {
    public static final int MAIN = 1, IN = 2, OUT = 3, NIL = 0;

    private final int _maxCapacity;
    private float _percentMain = 0.5f, _percentIn = 0.2f;

    protected final TwoQueueCacheEntry<K, V> _headIn; // fifo
    protected final TwoQueueCacheEntry<K, V> _headOut; // 2nd chance (as hot queue)
    protected final TwoQueueCacheEntry<K, V> _headMain; // hot (LRU)

    protected int _numEntries = 0;
    protected int _sizeMain, _sizeIn, _sizeOut;
    protected int _maxMain, _maxIn, _maxOut;

    private Cleaner<K, V> _cleaner = NopCleaner.getInstance();

    public TwoQueueCachePolicy(int capacity) {
        this._maxCapacity = capacity;
        this._headIn = new TwoQueueCacheEntry<K, V>(null, null);
        this._headOut = new TwoQueueCacheEntry<K, V>(null, null);
        this._headMain = new TwoQueueCacheEntry<K, V>(null, null);
        initEntryChain();
    }

    private void initEntryChain() {
        _headIn.next = _headIn.prev = _headIn;
        _headOut.next = _headOut.prev = _headOut;
        _headMain.next = _headMain.prev = _headMain;
        _sizeIn = _sizeOut = _sizeMain = 0;
        recalcMax();
    }

    public int getMaxCapacity() {
        return _maxCapacity;
    }

    public void setCleaner(Cleaner<K, V> cleaner) {
        this._cleaner = cleaner;
    }

    public void setPercentIn(float percent) {
        this._percentIn = percent;
        recalcMax();
    }

    public void setPercentMain(float percent) {
        this._percentMain = percent;
        recalcMax();
    }

    private void recalcMax() {
        _maxMain = (int) (_maxCapacity * _percentMain);
        _maxIn = (int) (_maxCapacity * _percentIn);
        _maxOut = _maxCapacity - _maxMain - _maxIn;
    }

    public void recordAccess(TwoQueueCacheEntry<K, V> entry) {
        entry.recordAccess(this);
    }

    public void recordRemoval(TwoQueueCacheEntry<K, V> entry) {
        entry.recordRemoval();
    }

    public V addEntry(ConcurrentMap<K, TwoQueueCacheEntry<K, V>> map, K key, V value, boolean replace) {
        final TwoQueueCacheEntry<K, V> newEntry = new TwoQueueCacheEntry<K, V>(key, value);

        map.put(key, newEntry);

        if(isInQueueFull()) {
            TwoQueueCacheEntry<K, V> tailIn = _headIn.next;
            if(tailIn != _headIn) {
                tailIn.remove();
                _sizeIn--;
                tailIn.type = OUT;
                addToFront(_headOut, tailIn);
                _sizeOut++;
            }
            if(_sizeOut > _maxOut) {
                TwoQueueCacheEntry<K, V> tailOut = _headOut.next;
                if(tailOut != _headOut) {
                    tailOut.setEvicted();
                    K remotedKey = tailOut.getKey();
                    _cleaner.cleanup(remotedKey, tailOut.getValue());
                    map.remove(remotedKey, tailOut);
                    _numEntries--;
                    tailOut.recordRemoval();
                    _sizeOut--;
                }
            }
        } else if(isFull()) { // remove coldest
            TwoQueueCacheEntry<K, V> tailMain = _headMain.next;
            if(tailMain != _headMain) {
                tailMain.setEvicted();
                K remotedKey = tailMain.getKey();
                _cleaner.cleanup(remotedKey, tailMain.getValue());
                map.remove(remotedKey, tailMain);
                _numEntries--;
                tailMain.recordRemoval();
                _sizeMain--;
            }
        }
        newEntry.type = IN;
        addToFront(_headIn, newEntry);
        _sizeIn++;
        _numEntries++;

        return null;
    }

    public TwoQueueCacheEntry<K, V> allocateEntry(ConcurrentMap<K, TwoQueueCacheEntry<K, V>> map, K key, V value) {
        final TwoQueueCacheEntry<K, V> newEntry = new TwoQueueCacheEntry<K, V>(key, value);

        map.put(key, newEntry);

        if(isInQueueFull()) {
            TwoQueueCacheEntry<K, V> tailIn = _headIn.next;
            if(tailIn != _headIn) {
                tailIn.remove();
                _sizeIn--;
                tailIn.type = OUT;
                addToFront(_headOut, tailIn);
                _sizeOut++;
            }
            if(_sizeOut > _maxOut) {
                TwoQueueCacheEntry<K, V> tailOut = _headOut.next;
                if(tailOut != _headOut) {
                    while(!tailOut.tryEvict()) {
                        tailOut = tailOut.next;
                        assert (tailOut != _headOut);
                    }
                    K remotedKey = tailOut.getKey();
                    _cleaner.cleanup(remotedKey, tailOut.getValue());
                    map.remove(remotedKey, tailOut);
                    _numEntries--;
                    tailOut.recordRemoval();
                    _sizeOut--;
                }
            }
        } else if(isFull()) { // remove coldest
            TwoQueueCacheEntry<K, V> tailMain = _headMain.next;
            if(tailMain != _headMain) {
                while(!tailMain.tryEvict()) {
                    tailMain = tailMain.next;
                    assert (tailMain != _headMain);
                }
                K remotedKey = tailMain.getKey();
                _cleaner.cleanup(remotedKey, tailMain.getValue());
                map.remove(remotedKey, tailMain);
                _numEntries--;
                tailMain.recordRemoval();
                _sizeMain--;
            }
        }
        newEntry.type = IN;
        addToFront(_headIn, newEntry);
        _sizeIn++;
        _numEntries++;

        return newEntry;
    }

    protected static <K, V> void addToFront(TwoQueueCacheEntry<K, V> head, TwoQueueCacheEntry<K, V> e) {
        e.addBefore(head);
    }

    private boolean isFull() {
        return _numEntries >= _maxCapacity;
    }

    private boolean isInQueueFull() {
        return _sizeIn >= _maxIn;
    }

    public int prefetchEntries(final ICacheEntry<K, V> e) {
        final TwoQueueCacheEntry<K, V> entry = (TwoQueueCacheEntry<K, V>) e;
        int sum = 0;
        final TwoQueueCacheEntry<K, V> prev = entry.prev;
        if(prev != null) {
            if(prev.next != null) {
                sum++;
            }
        }
        final TwoQueueCacheEntry<K, V> next = entry.next;
        if(next != null) {
            if(next.prev != null) {
                sum++;
            }
        }
        /*
        {
            final TwoQueueCacheEntry<K, V> headMain = _headMain;
            final TwoQueueCacheEntry<K, V> hprev = headMain.prev;
            if(hprev != null) {
                if(hprev.next != null) {
                    sum++;
                }
            }
            final TwoQueueCacheEntry<K, V> hnext = headMain.next;
            if(hnext != null) {
                if(hnext.prev != null) {
                    sum++;
                }
                if(hnext.next != null) {
                    sum++;
                }
            }
        }
        */
        return sum;
    }
}
