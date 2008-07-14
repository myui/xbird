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
package xbird.util.cache;

/**
 * 
 * <DIV lang="en"></DIV> <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SynchronizedLongCache<V> implements ILongCache<V> {
    private static final long serialVersionUID = -7311798726317917321L;

    private final ILongCache<V> _delegate;

    public SynchronizedLongCache(ILongCache<V> delegate) {
        this._delegate = delegate;
    }

    public static <V> ILongCache<V> synchronizedCache(ILongCache<V> cache) {
        return new SynchronizedLongCache<V>(cache);
    }

    public synchronized void clear() {
        _delegate.clear();
    }

    public synchronized V get(long key) {
        return _delegate.get(key);
    }

    public synchronized V put(long key, V value) {
        return _delegate.put(key, value);
    }

    public synchronized V putIfAbsent(long key, V value) {
        return _delegate.putIfAbsent(key, value);
    }

    public synchronized ICacheEntry<Long, V> fixEntry(long key) {
        return _delegate.fixEntry(key);
    }

}
