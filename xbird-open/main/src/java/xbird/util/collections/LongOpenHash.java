/*
 * @(#)$Id: Int2LongOpenHash.java 1429 2006-11-08 15:36:28Z yui $
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import xbird.util.lang.HashUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.eece.unm.edu/faculty/heileman/hash/hash.html
 */
public class LongOpenHash<V> implements Externalizable {
    private static final long serialVersionUID = -8162355845665353513L;

    public static final float DEFAULT_LOAD_FACTOR = 0.7f;
    public static final float DEFAULT_GROW_FACTOR = 2.0f;

    protected static final byte FREE = 0;
    protected static final byte FULL = 1;
    protected static final byte REMOVED = 2;

    protected final transient float _loadFactor;
    protected final transient float _growFactor;

    protected int _used = 0;
    protected int _threshold;

    protected long[] _keys;
    protected V[] _values;
    protected byte[] _states;

    private int keyMask;

    public LongOpenHash(int size) {
        this(size, DEFAULT_LOAD_FACTOR, DEFAULT_GROW_FACTOR);
    }

    public LongOpenHash(int size, float loadFactor, float growFactor) {
        if(size < 1) {
            throw new IllegalArgumentException();
        }
        this._loadFactor = loadFactor;
        this._growFactor = growFactor;
        int actualSize = HashUtils.nextPowerOfTwo(4, size);
        this.keyMask = actualSize - 1;
        this._keys = new long[actualSize];
        this._values = (V[]) new Object[actualSize];
        this._states = new byte[actualSize];
        this._threshold = Math.round(actualSize * _loadFactor);
    }

    public LongOpenHash() {// required for serialization
        this._loadFactor = DEFAULT_LOAD_FACTOR;
        this._growFactor = DEFAULT_GROW_FACTOR;
    }

    public boolean containsKey(long key) {
        return findKey(key) >= 0;
    }

    /**
     * @return -1L if not found
     */
    public V get(long key) {
        int i = findKey(key);
        if(i < 0) {
            return null;
        }
        recordAccess(i);
        return _values[i];
    }

    public V put(long key, V value) {
        int hash = keyHash(key);
        int keyIdx = hash & keyMask;

        boolean expanded = preAddEntry(keyIdx);
        if(expanded) {
            keyIdx = hash & keyMask;
        }

        long[] keys = _keys;
        V[] values = _values;
        byte[] states = _states;

        if(states[keyIdx] == FULL) {
            if(keys[keyIdx] == key) {
                V old = values[keyIdx];
                values[keyIdx] = value;
                recordAccess(keyIdx);
                return old;
            }
            // try second hash
            final int keyLength = _keys.length;
            int decr = 1 + (hash & (keyLength - 3));
            for(;;) {
                keyIdx -= decr;
                if(keyIdx < 0) {
                    keyIdx += keyLength;
                }
                if(isFree(keyIdx, key)) {
                    break;
                }
                if(states[keyIdx] == FULL && keys[keyIdx] == key) {
                    V old = values[keyIdx];
                    values[keyIdx] = value;
                    recordAccess(keyIdx);
                    return old;
                }
            }
        }
        keys[keyIdx] = key;
        values[keyIdx] = value;
        states[keyIdx] = FULL;
        postAddEntry(keyIdx);
        ++_used;
        return null;
    }

    /** Return weather the required slot is free for new entry */
    protected boolean isFree(int index, long key) {
        byte stat = _states[index];
        if(stat == FREE) {
            return true;
        }
        if(stat == REMOVED && _keys[index] == key) {
            return true;
        }
        return false;
    }

    /** @return expanded or not */
    protected boolean preAddEntry(int index) {
        if(_used >= _threshold) {// too filled
            int newCapacity = Math.round(_keys.length * _growFactor);
            ensureCapacity(newCapacity);
            return true;
        }
        return false;
    }

    protected void postAddEntry(int index) {}

    protected int findKey(long key) {
        long[] keys = _keys;
        byte[] states = _states;

        int hash = keyHash(key);
        int keyIdx = hash & keyMask;
        if(states[keyIdx] != FREE) {
            if(states[keyIdx] == FULL && keys[keyIdx] == key) {
                return keyIdx;
            }
            // try second hash
            final int keyLength = keys.length;
            int decr = 1 + (hash & (keyLength - 3));
            for(;;) {
                keyIdx -= decr;
                if(keyIdx < 0) {
                    keyIdx += keyLength;
                }
                if(isFree(keyIdx, key)) {
                    return -1;
                }
                if(states[keyIdx] == FULL && keys[keyIdx] == key) {
                    return keyIdx;
                }
            }
        }
        return -1;
    }

    public V remove(long key) {
        long[] keys = _keys;
        V[] values = _values;
        byte[] states = _states;

        int hash = keyHash(key);
        int keyIdx = hash & keyMask;
        if(states[keyIdx] != FREE) {
            if(states[keyIdx] == FULL && keys[keyIdx] == key) {
                V old = values[keyIdx];
                states[keyIdx] = REMOVED;
                --_used;
                recordRemoval(keyIdx);
                return old;
            }
            //  second hash
            final int keyLength = keys.length;
            int decr = 1 + (hash & (keyLength - 3));
            for(;;) {
                keyIdx -= decr;
                if(keyIdx < 0) {
                    keyIdx += keyLength;
                }
                if(states[keyIdx] == FREE) {
                    return null;
                }
                if(states[keyIdx] == FULL && keys[keyIdx] == key) {
                    V old = values[keyIdx];
                    states[keyIdx] = REMOVED;
                    --_used;
                    recordRemoval(keyIdx);
                    return old;
                }
            }
        }
        return null;
    }

    public int size() {
        return _used;
    }

    public void clear() {
        Arrays.fill(_states, FREE);
        this._used = 0;
    }

    public IMapIterator<V> entries() {
        return new MapIterator();
    }

    @Override
    public String toString() {
        int len = size() * 10 + 2;
        StringBuilder buf = new StringBuilder(len);
        buf.append('{');
        IMapIterator i = entries();
        while(i.next() != -1) {
            buf.append(i.getKey());
            buf.append('=');
            buf.append(i.getValue());
            if(i.hasNext()) {
                buf.append(',');
            }
        }
        buf.append('}');
        return buf.toString();
    }

    protected void ensureCapacity(int newCapacity) {
        if(_used < _threshold) {
            throw new IllegalStateException("used: " + _used + ", threshold: " + _threshold);
        }
        int capa = HashUtils.nextPowerOfTwo(newCapacity);
        rehash(capa);
        this.keyMask = capa - 1;
        this._threshold = Math.round(capa * _loadFactor);
    }

    private void rehash(int newCapacity) {
        int oldCapacity = _keys.length;
        if(newCapacity <= oldCapacity) {
            throw new IllegalArgumentException("new: " + newCapacity + ", old: " + oldCapacity);
        }
        long[] newkeys = new long[newCapacity];
        V[] newValues = (V[]) new Object[newCapacity];
        byte[] newStates = new byte[newCapacity];
        int used = 0;
        for(int i = 0; i < oldCapacity; i++) {
            if(_states[i] == FULL) {
                used++;
                long k = _keys[i];
                V v = _values[i];
                int hash = keyHash(k);
                int keyIdx = hash & keyMask;
                if(newStates[keyIdx] == FULL) {// second hashing
                    int decr = 1 + (hash & (newCapacity - 3));
                    while(newStates[keyIdx] != FREE) {
                        keyIdx -= decr;
                        if(keyIdx < 0) {
                            keyIdx += newCapacity;
                        }
                    }
                }
                newStates[keyIdx] = FULL;
                newkeys[keyIdx] = k;
                newValues[keyIdx] = v;
            }
        }
        this._keys = newkeys;
        this._values = newValues;
        this._states = newStates;
        this._used = used;
    }

    private static int keyHash(long key) {
        return ((int) (key ^ (key >>> 32))) & 0x7fffffff;
    }

    protected void recordAccess(int idx) {};

    protected void recordRemoval(int idx) {};

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_threshold);
        out.writeInt(_used);

        out.writeInt(_keys.length);
        final IMapIterator itor = entries();
        int i = 0;
        for(; itor.next() != -1; i++) {
            out.writeLong(itor.getKey());
            out.writeObject(itor.getValue());
        }
        if(i != _used) {
            throw new IllegalStateException("used: " + _used + ", stream out: " + i);
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._threshold = in.readInt();
        final int used = in.readInt();
        this._used = used;

        final int keylen = in.readInt();
        final long[] keys = new long[keylen];
        final V[] values = (V[]) new Object[keylen];
        final byte[] states = new byte[keylen];
        for(int i = 0; i < used; i++) {
            long k = in.readLong();
            V v = (V) in.readObject();
            int hash = keyHash(k);
            int keyIdx = hash & keyMask;
            if(states[keyIdx] != FREE) {// second hash
                int decr = 1 + (hash & (keylen - 3));
                for(;;) {
                    keyIdx -= decr;
                    if(keyIdx < 0) {
                        keyIdx += keylen;
                    }
                    if(states[keyIdx] == FREE) {
                        break;
                    }
                }
            }
            states[keyIdx] = FULL;
            keys[keyIdx] = k;
            values[keyIdx] = v;
        }
        this._keys = keys;
        this._values = values;
        this._states = states;
    }

    public interface IMapIterator<T> {

        public boolean hasNext();

        /**
         * @return -1 if not found
         */
        public int next();

        public long getKey();

        public T getValue();

    }

    private final class MapIterator implements IMapIterator<V> {

        int nextEntry;
        int lastEntry = -1;

        MapIterator() {
            this.nextEntry = nextEntry(0);
        }

        /** find the index of next full entry */
        int nextEntry(int index) {
            while(index < _keys.length && _states[index] != FULL) {
                index++;
            }
            return index;
        }

        public boolean hasNext() {
            return nextEntry < _keys.length;
        }

        public int next() {
            if(!hasNext()) {
                return -1;
            }
            int curEntry = nextEntry;
            this.lastEntry = nextEntry;
            this.nextEntry = nextEntry(nextEntry + 1);
            return curEntry;
        }

        public long getKey() {
            if(lastEntry == -1) {
                throw new IllegalStateException();
            }
            return _keys[lastEntry];
        }

        public V getValue() {
            if(lastEntry == -1) {
                throw new IllegalStateException();
            }
            return _values[lastEntry];
        }
    }
}
