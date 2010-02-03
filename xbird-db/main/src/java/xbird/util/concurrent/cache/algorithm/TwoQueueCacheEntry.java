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

import static xbird.util.concurrent.cache.algorithm.TwoQueueCachePolicy.IN;
import static xbird.util.concurrent.cache.algorithm.TwoQueueCachePolicy.MAIN;
import static xbird.util.concurrent.cache.algorithm.TwoQueueCachePolicy.NIL;
import static xbird.util.concurrent.cache.algorithm.TwoQueueCachePolicy.OUT;
import xbird.util.cache.CacheEntry;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class TwoQueueCacheEntry<K, V> extends CacheEntry<K, V> {
    private static final long serialVersionUID = -391560721525495873L;

    protected TwoQueueCacheEntry<K, V> next, prev;
    protected int type = NIL;

    public TwoQueueCacheEntry(K key, V value) {
        super(key, value);
    }

    protected void recordAccess(TwoQueueCachePolicy<K, V> lm) {
        switch(type) {
            case MAIN: // move tail entry to head of Am
                remove();
                TwoQueueCachePolicy.addToFront(lm._headMain, this);
                break;
            case OUT: // move from A1out to Am
                if(lm._sizeMain >= lm._maxMain) {// workaround for a bug in full-2Q algorithm
                    // move the coldest entry in Am to the tail of A1out (likely to be removed) 
                    TwoQueueCacheEntry<K, V> tailMain = lm._headMain.next;
                    assert (tailMain != lm._headMain);
                    tailMain.remove();
                    lm._sizeMain--;
                    tailMain.type = OUT;
                    assert (lm._headOut != lm._headOut.next);
                    lm.addToFront(lm._headOut.next, tailMain);
                    lm._sizeOut++;
                }
                remove();
                lm._sizeOut--;
                this.type = MAIN;
                TwoQueueCachePolicy.addToFront(lm._headMain, this);
                lm._sizeMain++;
                break;
            case IN: // do nothing                    
                break;
            default:
                throw new IllegalStateException();
        }
    }

    protected void recordRemoval() {
        remove();
    }

    /**
     * Removes this entry from the linked list.
     */
    protected void remove() {
        if(prev != null) {
            prev.next = next;
        }
        if(next != null) {
            next.prev = prev;
        }
        prev = null;
        next = null;
    }

    /**
     * Inserts this entry before the specified existing entry in the list.
     * <pre>existingEntry.PREV <-> this <-> existingEntry - existingEntry.NEXT</pre>
     */
    protected void addBefore(TwoQueueCacheEntry<K, V> existingEntry) {
        next = existingEntry;
        prev = existingEntry.prev;
        prev.next = this;
        next.prev = this;
    }

}
