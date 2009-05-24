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

import java.io.Serializable;
import java.util.Iterator;

import xbird.util.lang.ArrayUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://en.wikipedia.org/wiki/Circular_buffer
 */
public final class RingBuffer<T> implements Serializable, Iterable<T> {
    private static final long serialVersionUID = -6567590960123023065L;

    private final T[] items;
    private int validStart;
    private int validEnd;

    @SuppressWarnings("unchecked")
    public RingBuffer(int capacity) {
        this.items = (T[]) new Object[capacity];
        this.validStart = 0;
        this.validEnd = 0;
    }

    public void insert(T item) {
        final T tmp = items[validEnd];
        if(tmp != null) {
            //overwrite
            validStart++;
            if(validStart == items.length) {
                validStart = 0;
            }
        }
        items[validEnd++] = item;
        if(validEnd == items.length) {
            validEnd = 0;
        }
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("[ ");
        for(int i = 0; i < items.length; i++) {
            T item = items[(i + validStart) % items.length];
            if(item == null) {
                break;
            }
            buf.append(item);
            buf.append(", ");
        }
        buf.append("]");
        return buf.toString();
    }

    public Iterator<T> iterator() {
        return new RingBufferIterator();
    }

    private final class RingBufferIterator implements Iterator<T> {

        private int position = 0;

        public RingBufferIterator() {}

        public boolean hasNext() {
            if(position >= items.length) {
                return false;
            }
            return items[(position + validStart) % items.length] != null;
        }

        public T next() {
            return items[((position++) + validStart) % items.length];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    public Iterator<T> snapshotIterator() {
        final T[] snapshot = ArrayUtils.copy(items);
        return new RingBufferSnapshotIterator<T>(snapshot, validStart);
    }

    private static final class RingBufferSnapshotIterator<T> implements Iterator<T> {

        private final T[] items;
        private final int validStart;
        private int position = 0;

        public RingBufferSnapshotIterator(T[] items, int validStart) {
            this.items = items;
            this.validStart = validStart;
        }

        public boolean hasNext() {
            if(position >= items.length) {
                return false;
            }
            return items[(position + validStart) % items.length] != null;
        }

        public T next() {
            return items[((position++) + validStart) % items.length];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
