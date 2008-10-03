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
package xbird.storage.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import xbird.util.cache.CacheEntry;
import xbird.util.cache.Cleaner;
import xbird.util.cache.ICacheEntry;
import xbird.util.cache.ILongCache;
import xbird.util.codec.Codec;
import xbird.util.concurrent.cache.ConcurrentLongCache;

/**
 * 
 * <DIV lang="en"></DIV> <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DiskPagedLongCache<V> implements ILongCache<V>, Cleaner<Long, V>, Closeable {
    private static final long serialVersionUID = 4361712313780427720L;

    private final VarSegments paged;
    private ILongCache<V> internalCache;

    private final Codec<V> codec;

    @SuppressWarnings("unchecked")
    public DiskPagedLongCache(File file, int cacheSize, Codec<V> codec) {
        if(!file.exists()) {
            throw new IllegalStateException("File does not exists: " + file.getAbsolutePath());
        }
        this.paged = new VarSegments(file, cacheSize);
        this.internalCache = new ConcurrentLongCache(cacheSize, this);
        this.codec = codec;
    }

    public void clear() {
        internalCache.clear();
    }

    public V get(long key) {
        final V cached = internalCache.get(key);
        if(cached != null) {
            return cached;
        }
        final byte[] b;
        try {
            b = paged.read(key);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        if(b == null) {
            return null;
        }
        final V decoded = codec.decode(b);
        internalCache.put(key, decoded);
        return decoded;
    }

    public V put(long key, V value) {
        return internalCache.put(key, value);
    }

    public V putIfAbsent(long key, V value) {
        throw new UnsupportedOperationException();
    }

    public void cleanup(Long key, V value) {
        assert (value != null) : key;
        final byte[] b = codec.encode(value);
        try {
            paged.writeIfAbsent(key.longValue(), b);
        } catch (IOException e) {
            ;
        }
    }

    public void close() throws IOException {
        this.internalCache = null;
        paged.delete();
    }

    public ICacheEntry<Long, V> fixEntry(long key) {
        final V cached = get(key);
        if(cached != null) {
            return new DummyCacheEntry(key, cached);
        } else {
            return new DummyCacheEntry(key);
        }
    }

    private final class DummyCacheEntry extends CacheEntry<Long, V> {
        private static final long serialVersionUID = 1L;

        DummyCacheEntry(long key, V value) {
            super(key, value);
        }

        DummyCacheEntry(long key) {
            super(key, null);
        }

        @Override
        public void setValue(V newValue) {
            put(_key.longValue(), newValue);
            super.setValue(newValue);
        }

    }

}
