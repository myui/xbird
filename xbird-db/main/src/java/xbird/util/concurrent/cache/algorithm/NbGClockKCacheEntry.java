/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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
package xbird.util.concurrent.cache.algorithm;

import xbird.util.concurrent.AtomicUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NbGClockKCacheEntry<K, V> extends NbGClockCacheEntry<K, V> {
    private static final long serialVersionUID = 966328017663012869L;

    private final int _maxCount;

    public NbGClockKCacheEntry(int maxCount, K key, V value) {
        this(maxCount, key, value, true);
    }

    public NbGClockKCacheEntry(int maxCount, K key, V value, boolean pin) {
        super(key, value, pin);
        this._maxCount = maxCount;
    }

    @Override
    public void incrReferenceCount() {
        AtomicUtils.tryIncrementAndGetIfLessThan(_refCount, _maxCount);
    }

    @Override
    public int decrReferenceCount() {
        return AtomicUtils.tryDecrementAndGetIfGreaterThan(_refCount, 0);
    }

}
