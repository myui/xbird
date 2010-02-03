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

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NbGClockKCachePolicy<K, V> extends NbGClockCachePolicy<K, V> {

    private final int maxCount;

    public NbGClockKCachePolicy(int capacity, int maxCount) {
        super(capacity);
        this.maxCount = maxCount;
    }

    @Override
    protected NbGClockKCacheEntry<K, V> createCacheEntry(final K key, final V value, final boolean pin) {
        return new NbGClockKCacheEntry<K, V>(maxCount, key, value, pin);
    }
}
