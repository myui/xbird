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

import xbird.util.collections.ObservableLRUMap.Cleaner;

/**
 * Values are reclaimed by LRU semantics and/or SoftReference GC semantics.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SoftLRUMap<K, V> extends SoftHashMap<K, V> {
    private static final long serialVersionUID = -6343362050460135467L;

    public SoftLRUMap(final int size) {
        super(new LRUMap<K, SoftValue<K, V>>(size));
    }

    public SoftLRUMap(final int size, final Cleaner<K, SoftValue<K, V>> cleaner) {
        super(new ObservableLRUMap<K, SoftValue<K, V>>(size, cleaner));
    }
    
}
