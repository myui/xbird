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
package xbird.util.collections.longs;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import xbird.util.io.IOUtils;
import xbird.util.math.Primes;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.eece.unm.edu/faculty/heileman/hash/hash.html
 */
public final class Bytes2LongOpenHash implements Externalizable {
    private static final long serialVersionUID = 7760544959366660164L;

    public static final float DEFAULT_LOAD_FACTOR = 0.7f;
    public static final float DEFAULT_GROW_FACTOR = 2.0f;

    protected static final byte FREE = 0;
    protected static final byte FULL = 1;
    protected static final byte REMOVED = 2;

    protected final transient float _loadFactor;
    protected final transient float _growFactor;

    protected int _used = 0;
    protected int _threshold;

    protected byte[][] _keys;
    protected long[] _values;
    protected byte[] _states;

    public Bytes2LongOpenHash(int size) {
        this(size, DEFAULT_LOAD_FACTOR, DEFAULT_GROW_FACTOR);
    }

    public Bytes2LongOpenHash(int size, float loadFactor, float growFactor) {
        if(size < 1) {
            throw new IllegalArgumentException();
        }
        this._loadFactor = loadFactor;
        this._growFactor = growFactor;
        int actualSize = Primes.findLeastPrimeNumber(size);
        this._keys = new byte[actualSize][];
        this._values = new long[actualSize];
        this._states = new byte[actualSize];
        this._threshold = Math.round(actualSize * _loadFactor);
    }

    public Bytes2LongOpenHash() {// required for serialization
        this._loadFactor = DEFAULT_LOAD_FACTOR;
        this._growFactor = DEFAULT_GROW_FACTOR;
    }

    public boolean containsKey(byte[] key) {
        return findKey(key) >= 0;
    }

    /**
     * @return -1L if not found
     */
    public long get(byte[] key) {
        int i = findKey(key);
        if(i < 0) {
            return -1L;
        }
        recordAccess(i);
        return _values[i];
    }

    public long put(byte[] key, long value) {
        int hash = keyHash(key);
        int keyLength = _keys.length;
        int keyIdx = hash % keyLength;

        boolean expanded = preAddEntry(keyIdx);
        if(expanded) {
            keyLength = _keys.length;
            keyIdx = hash % keyLength;
        }

        final byte[][] keys = _keys;
        final long[] values = _values;
        final byte[] states = _states;

        if(states[keyIdx] == FULL) {
            if(equals(keys[keyIdx], key)) {
                long old = values[keyIdx];
                values[keyIdx] = value;
                recordAccess(keyIdx);
                return old;
            }
            // try second hash
            int decr = 1 + (hash % (keyLength - 2));
            for(;;) {
                keyIdx -= decr;
                if(keyIdx < 0) {
                    keyIdx += keyLength;
                }
                if(isFree(keyIdx, key)) {
                    break;
                }
                if(states[keyIdx] == FULL && equals(keys[keyIdx], key)) {
                    long old = values[keyIdx];
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
        return -1L;
    }

    private static boolean equals(final byte[] k1, final byte[] k2) {
        return k1 == k2 || Arrays.equals(k1, k2);
    }

    /** Return weather the required slot is free for new entry */
    protected boolean isFree(int index, byte[] key) {
        byte stat = _states[index];
        if(stat == FREE) {
            return true;
        }
        if(stat == REMOVED && equals(_keys[index], key)) {
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

    protected int findKey(byte[] key) {
        final byte[][] keys = _keys;
        final byte[] states = _states;
        int keyLength = keys.length;

        int hash = keyHash(key);
        int keyIdx = hash % keyLength;
        if(states[keyIdx] != FREE) {
            if(states[keyIdx] == FULL && equals(keys[keyIdx], key)) {
                return keyIdx;
            }
            // try second hash
            int decr = 1 + (hash % (keyLength - 2));
            for(;;) {
                keyIdx -= decr;
                if(keyIdx < 0) {
                    keyIdx += keyLength;
                }
                if(isFree(keyIdx, key)) {
                    return -1;
                }
                if(states[keyIdx] == FULL && equals(keys[keyIdx], key)) {
                    return keyIdx;
                }
            }
        }
        return -1;
    }

    public long remove(byte[] key) {
        final byte[][] keys = _keys;
        final long[] values = _values;
        final byte[] states = _states;
        int keyLength = keys.length;

        int hash = keyHash(key);
        int keyIdx = hash % keyLength;
        if(states[keyIdx] != FREE) {
            if(states[keyIdx] == FULL && equals(keys[keyIdx], key)) {
                long old = values[keyIdx];
                states[keyIdx] = REMOVED;
                --_used;
                recordRemoval(keyIdx);
                return old;
            }
            //  second hash
            int decr = 1 + (hash % (keyLength - 2));
            for(;;) {
                keyIdx -= decr;
                if(keyIdx < 0) {
                    keyIdx += keyLength;
                }
                if(states[keyIdx] == FREE) {
                    return -1L;
                }
                if(states[keyIdx] == FULL && equals(keys[keyIdx], key)) {
                    long old = values[keyIdx];
                    states[keyIdx] = REMOVED;
                    --_used;
                    recordRemoval(keyIdx);
                    return old;
                }
            }
        }
        return -1L;
    }

    public int size() {
        return _used;
    }

    public void clear() {
        Arrays.fill(_states, FREE);
        this._used = 0;
    }

    public IMapIterator entries() {
        return new MapIterator();
    }

    @Override
    public String toString() {
        int len = size() * 10 + 2;
        final StringBuilder buf = new StringBuilder(len);
        buf.append('{');
        final IMapIterator i = entries();
        while(i.next() != -1) {
            byte[] key = i.getKey();
            buf.append(new String(key));
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
        int prime = Primes.findLeastPrimeNumber(newCapacity);
        rehash(prime);
        this._threshold = Math.round(prime * _loadFactor);
    }

    private void rehash(int newCapacity) {
        int oldCapacity = _keys.length;
        if(newCapacity <= oldCapacity) {
            throw new IllegalArgumentException("new: " + newCapacity + ", old: " + oldCapacity);
        }
        final byte[][] newkeys = new byte[newCapacity][];
        final long[] newValues = new long[newCapacity];
        final byte[] newStates = new byte[newCapacity];
        int used = 0;
        for(int i = 0; i < oldCapacity; i++) {
            if(_states[i] == FULL) {
                used++;
                byte[] k = _keys[i];
                long v = _values[i];
                int hash = keyHash(k);
                int keyIdx = hash % newCapacity;
                if(newStates[keyIdx] == FULL) {// second hashing
                    int decr = 1 + (hash % (newCapacity - 2));
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

    private static int keyHash(final byte[] key) {
        int hash = Arrays.hashCode(key);
        return ((int) (hash ^ (hash >>> 32))) & 0x7fffffff;
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
            IOUtils.writeBytes(itor.getKey(), out);
            out.writeLong(itor.getValue());
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
        final byte[][] keys = new byte[keylen][];
        final long[] values = new long[keylen];
        final byte[] states = new byte[keylen];
        for(int i = 0; i < used; i++) {
            byte[] k = IOUtils.readBytes(in);
            long v = in.readLong();
            int hash = keyHash(k);
            int keyIdx = hash % keylen;
            if(states[keyIdx] != FREE) {// second hash
                int decr = 1 + (hash % (keylen - 2));
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

    public interface IMapIterator {

        public boolean hasNext();

        /**
         * @return -1 if not found
         */
        public int next();

        public byte[] getKey();

        public long getValue();

    }

    private final class MapIterator implements IMapIterator {

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

        public byte[] getKey() {
            if(lastEntry == -1) {
                throw new IllegalStateException();
            }
            return _keys[lastEntry];
        }

        public long getValue() {
            if(lastEntry == -1) {
                throw new IllegalStateException();
            }
            return _values[lastEntry];
        }
    }
}
