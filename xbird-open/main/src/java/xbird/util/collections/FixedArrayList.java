/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * Contributors:
 *     Makoto YUI - imported from Android JDK and made some modification.
 */
package xbird.util.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

/**
 * ArrayList is an implementation of {@link List}, backed by an array. All
 * optional operations adding, removing, and replacing are supported. The
 * elements can be any objects.
 */
public class FixedArrayList<E> extends AbstractList<E>
        implements List<E>, Cloneable, Serializable, RandomAccess {
    private static final long serialVersionUID = 8683452581122892189L;

    private transient int firstIndex;
    private transient int lastIndex;
    private transient E[] array;

    /**
     * Constructs a new instance of {@code ArrayList} with the specified
     * capacity.
     *
     * @param capacity
     *            the initial capacity of this {@code ArrayList}.
     */
    public FixedArrayList(int capacity) {
        firstIndex = lastIndex = 0;
        try {
            array = newElementArray(capacity);
        } catch (NegativeArraySizeException e) {
            throw new IllegalArgumentException();
        }
    }

    public FixedArrayList(E[] array) {
        firstIndex = lastIndex = 0;
        this.array = array;
    }

    @SuppressWarnings("unchecked")
    private E[] newElementArray(int size) {
        return (E[]) new Object[size];
    }

    /**
     * Inserts the specified object into this {@code ArrayList} at the specified
     * location. The object is inserted before any previous element at the
     * specified location. If the location is equal to the size of this
     * {@code ArrayList}, the object is added at the end.
     *
     * @param location
     *            the index at which to insert the object.
     * @param object
     *            the object to add.
     * @throws IndexOutOfBoundsException
     *             when {@code location < 0 || > size()}
     */
    @Override
    public void add(int location, E object) {
        int size = lastIndex - firstIndex;
        if(0 < location && location < size) {
            if(firstIndex == 0 && lastIndex == array.length) {
                throw new UnsupportedOperationException("BoundedArrayList cannot grow");
            } else if((location < size / 2 && firstIndex > 0) || lastIndex == array.length) {
                System.arraycopy(array, firstIndex, array, --firstIndex, location);
            } else {
                int index = location + firstIndex;
                System.arraycopy(array, index, array, index + 1, size - location);
                lastIndex++;
            }
            array[location + firstIndex] = object;
        } else if(location == 0) {
            if(firstIndex == 0) {
                throw new UnsupportedOperationException("BoundedArrayList cannot grow");
            }
            array[--firstIndex] = object;
        } else if(location == size) {
            if(lastIndex == array.length) {
                throw new UnsupportedOperationException("BoundedArrayList cannot grow");
            }
            array[lastIndex++] = object;
        } else {
            throw new IndexOutOfBoundsException();
        }

        modCount++;
    }

    /**
     * Adds the specified object at the end of this {@code ArrayList}.
     *
     * @param object
     *            the object to add.
     * @return always true
     */
    @Override
    public boolean add(E object) {
        if(lastIndex == array.length) {
            throw new UnsupportedOperationException("BoundedArrayList cannot grow");
        }
        array[lastIndex++] = object;
        modCount++;
        return true;
    }

    /**
     * Inserts the objects in the specified collection at the specified location
     * in this List. The objects are added in the order they are returned from
     * the collection's iterator.
     *
     * @param location
     *            the index at which to insert.
     * @param collection
     *            the collection of objects.
     * @return {@code true} if this {@code ArrayList} is modified, {@code false}
     *         otherwise.
     * @throws IndexOutOfBoundsException
     *             when {@code location < 0 || > size()}
     */
    @Override
    public boolean addAll(int location, Collection<? extends E> collection) {
        int size = size();
        if(location < 0 || location > size) {
            throw new IndexOutOfBoundsException();
        }
        int growSize = collection.size();
        if(0 < location && location < size) {
            if(array.length - size < growSize) {
                throw new UnsupportedOperationException("BoundedArrayList cannot grow");
            } else if((location < size / 2 && firstIndex > 0)
                    || lastIndex > array.length - growSize) {
                int newFirst = firstIndex - growSize;
                if(newFirst < 0) {
                    int index = location + firstIndex;
                    System.arraycopy(array, index, array, index - newFirst, size - location);
                    lastIndex -= newFirst;
                    newFirst = 0;
                }
                System.arraycopy(array, firstIndex, array, newFirst, location);
                firstIndex = newFirst;
            } else {
                int index = location + firstIndex;
                System.arraycopy(array, index, array, index + growSize, size - location);
                lastIndex += growSize;
            }
        } else if(location == 0) {
            throw new UnsupportedOperationException("BoundedArrayList cannot grow");
        } else if(location == size) {
            if(lastIndex > array.length - growSize) {
                throw new UnsupportedOperationException("BoundedArrayList cannot grow");
            }
            lastIndex += growSize;
        }

        if(growSize > 0) {
            Iterator<? extends E> it = collection.iterator();
            int index = location + firstIndex;
            int end = index + growSize;
            while(index < end) {
                array[index++] = it.next();
            }
            modCount++;
            return true;
        }
        return false;
    }

    /**
     * Adds the objects in the specified collection to this {@code ArrayList}.
     *
     * @param collection
     *            the collection of objects.
     * @return {@code true} if this {@code ArrayList} is modified, {@code false}
     *         otherwise.
     */
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        int growSize = collection.size();
        if(growSize > 0) {
            if(lastIndex > array.length - growSize) {
                throw new UnsupportedOperationException("BoundedArrayList cannot grow");
            }
            Iterator<? extends E> it = collection.iterator();
            int end = lastIndex + growSize;
            while(lastIndex < end) {
                array[lastIndex++] = it.next();
            }
            modCount++;
            return true;
        }
        return false;
    }

    /**
     * Removes all elements from this {@code ArrayList}, leaving it empty.
     *
     * @see #isEmpty
     * @see #size
     */
    @Override
    public void clear() {
        if(firstIndex != lastIndex) {
            Arrays.fill(array, firstIndex, lastIndex, null);
            firstIndex = lastIndex = 0;
            modCount++;
        }
    }

    /**
     * Non-null version of {@link #clear()}.
     */
    public void trimToZero() {
        firstIndex = lastIndex = 0;
        modCount++;
    }

    /**
     * Returns a new {@code ArrayList} with the same elements, the same size and
     * the same capacity as this {@code ArrayList}.
     *
     * @return a shallow copy of this {@code ArrayList}
     * @see java.lang.Cloneable
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {
        try {
            FixedArrayList<E> newList = (FixedArrayList<E>) super.clone();
            newList.array = array.clone();
            return newList;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Searches this {@code ArrayList} for the specified object.
     *
     * @param object
     *            the object to search for.
     * @return {@code true} if {@code object} is an element of this
     *         {@code ArrayList}, {@code false} otherwise
     */
    @Override
    public boolean contains(Object object) {
        if(object != null) {
            for(int i = firstIndex; i < lastIndex; i++) {
                if(object.equals(array[i])) {
                    return true;
                }
            }
        } else {
            for(int i = firstIndex; i < lastIndex; i++) {
                if(array[i] == null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public E get(int location) {
        int _firstIndex = firstIndex;
        if(0 <= location && location < lastIndex - _firstIndex) {
            return array[_firstIndex + location];
        }
        throw new IndexOutOfBoundsException("Invalid location " + location + ", size is "
                + (lastIndex - _firstIndex));
    }

    @Override
    public int indexOf(Object object) {
        if(object != null) {
            for(int i = firstIndex; i < lastIndex; i++) {
                if(object.equals(array[i])) {
                    return i - firstIndex;
                }
            }
        } else {
            for(int i = firstIndex; i < lastIndex; i++) {
                if(array[i] == null) {
                    return i - firstIndex;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean isEmpty() {
        return lastIndex == firstIndex;
    }

    @Override
    public int lastIndexOf(Object object) {
        if(object != null) {
            for(int i = lastIndex - 1; i >= firstIndex; i--) {
                if(object.equals(array[i])) {
                    return i - firstIndex;
                }
            }
        } else {
            for(int i = lastIndex - 1; i >= firstIndex; i--) {
                if(array[i] == null) {
                    return i - firstIndex;
                }
            }
        }
        return -1;
    }

    /**
     * Replaces the element at the specified location in this {@code ArrayList}
     * with the specified object.
     *
     * @param location
     *            the index at which to put the specified object.
     * @param object
     *            the object to add.
     * @return the previous element at the index.
     * @throws IndexOutOfBoundsException
     *             when {@code location < 0 || >= size()}
     */
    @Override
    public E set(int location, E object) {
        // BEGIN android-changed: slight performance improvement
        if(0 <= location && location < (lastIndex - firstIndex)) {
            // END android-changed
            E result = array[firstIndex + location];
            array[firstIndex + location] = object;
            return result;
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Returns the number of elements in this {@code ArrayList}.
     *
     * @return the number of elements in this {@code ArrayList}.
     */
    @Override
    public int size() {
        return lastIndex - firstIndex;
    }

    /**
     * Returns a new array containing all elements contained in this
     * {@code ArrayList}.
     *
     * @return an array of the elements from this {@code ArrayList}
     */
    @Override
    public E[] toArray() {
        return array;
    }

    /**
     * Returns an array containing all elements contained in this
     * {@code ArrayList}. If the specified array is large enough to hold the
     * elements, the specified array is used, otherwise an array of the same
     * type is created. If the specified array is used and is larger than this
     * {@code ArrayList}, the array element following the collection elements
     * is set to null.
     *
     * @param contents
     *            the array.
     * @return an array of the elements from this {@code ArrayList}.
     * @throws ArrayStoreException
     *             when the type of an element in this {@code ArrayList} cannot
     *             be stored in the type of the specified array.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] contents) {
        int size = size();
        if(size > contents.length) {
            Class<?> ct = contents.getClass().getComponentType();
            contents = (T[]) Array.newInstance(ct, size);
        }
        System.arraycopy(array, firstIndex, contents, 0, size);
        if(size < contents.length) {
            contents[size] = null;
        }
        return contents;
    }

    /**
     * Sets the capacity of this {@code ArrayList} to be the same as the current
     * size.
     *
     * @see #size
     */
    public void trimToSize() {
        throw new UnsupportedOperationException();
    }

    private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("size", Integer.TYPE) }; //$NON-NLS-1$

    private void writeObject(ObjectOutputStream stream) throws IOException {
        ObjectOutputStream.PutField fields = stream.putFields();
        fields.put("size", size()); //$NON-NLS-1$
        stream.writeFields();
        stream.writeInt(array.length);
        Iterator<?> it = iterator();
        while(it.hasNext()) {
            stream.writeObject(it.next());
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = stream.readFields();
        lastIndex = fields.get("size", 0); //$NON-NLS-1$
        array = newElementArray(stream.readInt());
        for(int i = 0; i < lastIndex; i++) {
            array[i] = (E) stream.readObject();
        }
    }
}
