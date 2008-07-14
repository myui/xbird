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

import java.util.*;

import xbird.util.lang.ArrayUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CyclicQueue<E> implements Queue<E> {

    private final E[] data;
    private final int capacity;

    private int front = 0;
    private int rear = -1;

    private int counter = 0;

    @SuppressWarnings("unchecked")
    public CyclicQueue(int size) {
        this.data = (E[]) new Object[size];
        this.capacity = size;
    }

    public boolean isFull() {
        return counter == capacity;
    }

    private E get(int i) {
        return data[i];
    }

    public boolean isEmpty() {
        return counter == 0;
    }

    public boolean add(E e) {
        if(!offer(e)) {
            throw new IllegalStateException("Exceeds capacity: " + capacity);
        }
        return true;
    }

    public boolean addAll(final Collection<? extends E> c) {
        for(E e : c) {
            add(e);
        }
        return true;
    }

    public boolean offer(E e) {
        if(isFull()) {
            return false;
        }
        rear = (rear + 1) % capacity;
        data[rear] = e;
        ++counter;
        return true;
    }

    public E element() {
        if(isEmpty()) {
            throw new NoSuchElementException();
        }
        return data[front];
    }

    public E peek() {
        if(isEmpty()) {
            return null;
        }
        return data[front];
    }

    public E poll() {
        if(isEmpty()) {
            return null;
        }
        --counter;
        E e = data[front];
        front = (front + 1) % capacity;
        return e;
    }

    public E remove() {
        if(isEmpty()) {
            throw new NoSuchElementException();
        }
        --counter;
        E e = data[front];
        front = (front + 1) % capacity;
        return e;
    }

    public void clear() {
        this.front = 0;
        this.rear = 0;
        this.counter = 0;
    }

    public int size() {
        return counter;
    }

    public Iterator<E> iterator() {
        return new Itr(front, counter);
    }

    public boolean contains(Object o) {
        if(front > rear) {
            for(int i = front; i < rear; i++) {
                if(o.equals(data[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsAll(final Collection<?> c) {
        for(Object o : c) {
            if(!contains(o)) {
                return false;
            }
        }
        return true;
    }

    public Object[] toArray() {
        return ArrayUtils.copyOfRange(data, front, rear);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        return (T[]) ArrayUtils.copyOfRange(data, front, rear);
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    private final class Itr implements Iterator<E> {

        private int cursor;
        private int rests;

        public Itr(int cursor, int rests) {
            this.cursor = cursor;
            this.rests = rests;
        }

        public boolean hasNext() {
            return rests > 0;
        }

        public E next() {
            if(!hasNext()) {
                throw new IllegalStateException();
            }
            --rests;
            E e = get(cursor);
            cursor = (cursor + 1) % capacity;
            return e;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(256);
        buf.append('[');
        final Iterator<E> itr = iterator();
        if(itr.hasNext()) {
            buf.append(itr.next());
            while(itr.hasNext()) {
                buf.append(',');
                buf.append(itr.next());
            }
        }
        buf.append(']');
        return buf.toString();
    }
}
