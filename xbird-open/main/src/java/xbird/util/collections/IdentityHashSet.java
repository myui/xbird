/*
 * @(#)$Id: codetemplate_xbird.xml 3792 2008-04-21 21:39:23Z yui $
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

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;

import xbird.util.lang.HashUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class IdentityHashSet<E> extends AbstractSet<E> implements Serializable {
    private static final long serialVersionUID = 6894597723545148112L;

    private static final float loadFactor = 0.3f;
    private static final int initialCapacity = 256;

    private transient Object table[];
    private transient int threshold;
    private transient int count = 0;

    public IdentityHashSet(int initialCapacity, float loadFactor) {
        init(initialCapacity, loadFactor);
    }

    public IdentityHashSet() {
        init(initialCapacity, loadFactor);
    }

    public IdentityHashSet(int initialCapacity) {
        init(initialCapacity, loadFactor);
    }

    private void init(int initialCapacity, float loadFactor) {
        int actSize = HashUtils.nextPowerOfTwo(initialCapacity);
        this.table = new Object[actSize];
        this.threshold = HashUtils.nextPowerOfTwo((int) (actSize * loadFactor));
    }

    @Override
    public boolean contains(Object key) {
        final Object[] tab = table;
        final int mask = tab.length - 1;

        int index = hash(key, mask);
        while(true) {
            final Object e = tab[index];
            if(e == key) {
                return true;
            }
            if(e == null) {
                return false;
            }
            index = (index + 1) & mask;
        }
    }

    @Override
    public boolean add(E newObj) {
        if(count >= threshold) {
            rehash();
        }

        final Object[] tab = table;
        final int mask = tab.length - 1;

        int index = hash(newObj, mask);
        Object existing;
        while((existing = tab[index]) != null) {
            if(existing == newObj) {
                return false;
            }
            index = (index + 1) & mask;
        }
        tab[index] = newObj;

        count++;
        return true;
    }

    @Override
    public boolean remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public void clear() {
        for(int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        count = 0;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(256);
        buf.append("{ ");
        for(Object o : table) {
            if(o != null) {
                buf.append(o.toString());
                buf.append(", ");
            }
        }
        buf.append("} ");
        return buf.toString();
    }

    private void rehash() {
        final int oldCapacity = table.length;
        final Object oldMap[] = table;

        final int newCapacity = oldCapacity * 2;
        final Object newMap[] = new Object[newCapacity];
        final int mask = newCapacity - 1;

        for(int i = oldCapacity; i-- > 0;) {
            Object key = oldMap[i];
            if(key != null) {
                int index = hash(key, mask);
                while(newMap[index] != null) {
                    index = (index + 1) & mask;
                }
                newMap[index] = key;
            }
        }

        table = newMap;
        threshold = (int) (newCapacity * loadFactor);
    }

    private static int hash(final Object x, final int mask) {
        final int h = System.identityHashCode(x);
        // Multiply by -127, and left-shift to use least bit as part of hash
        return ((h << 1) - (h << 8)) & mask;
    }

    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        s.writeInt(count);

        for(Object key : table) {
            if(key != null) {
                s.writeObject(key);
            }
        }
    }

    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException,
            ClassNotFoundException {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        final int size = s.readInt();
        float loadFactor = (size * 4) / 3; // Allow for 33% growth
        init(size, loadFactor);

        for(int i = 0; i < size; i++) {
            E key = (E) s.readObject();
            add(key);
        }
    }
}
