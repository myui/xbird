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
package xbird.util.concurrent.collections;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Almost Nonblocking stack using Treiber's algorithm.
 * <DIV lang="en">
 * This stack is robust for ABA problem.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NonBlockingStack<T> {

    private final AtomicReference<Entry<T>> top = new AtomicReference<Entry<T>>();

    public NonBlockingStack() {}

    public void push(final T value) {
        final Entry<T> newHead = new Entry<T>(value);
        Entry<T> oldHead;
        do {
            oldHead = top.get();
            newHead.next = oldHead;
        } while(!top.compareAndSet(oldHead, newHead));
    }

    public T pop() {
        Entry<T> oldHead, newHead;
        do {
            oldHead = top.get();
            if(oldHead == null) {
                return null;
            }
            newHead = oldHead.next;
        } while(!top.compareAndSet(oldHead, newHead));
        return oldHead.item;
    }

    public T peek() {
        final Entry<T> oldHead = top.get();
        if(oldHead == null) {
            return null;
        }
        return oldHead.item;
    }
    
    public void set(final T value) {
        final Entry<T> newHead = new Entry<T>(value);
        top.set(newHead);
    }
    
    public void clear() {
        top.set(null);
    }

    private static final class Entry<T> {
        final T item;
        Entry<T> next;

        Entry(final T item) {
            this.item = item;
        }
    }

}
