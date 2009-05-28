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
 *     Maurice Herlihy and Nir Shavit - original author.
 *     Makoto YUI - modified.
 */
/*
 * UDEQueue.java
 *
 * Created on March 3, 2007, 11:48 PM
 *
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 *
 * This work is licensed under a Creative Commons Attribution-Share Alike 3.0 United States License.
 * http://i.creativecommons.org/l/by-sa/3.0/us/88x31.png
 */
package xbird.util.concurrent.collections;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Unbounded double-ended Queue
 * 
 * @author Maurice Herlihy
 */
public final class NonblockingUnboundedDeque<T> {

    private volatile CircularArray<T> tasks;
    private volatile int bottom;
    private final AtomicReference<Integer> top;

    public NonblockingUnboundedDeque() {
        this(4); // 2^4 = 16
    }

    public NonblockingUnboundedDeque(int logCapacity) {
        this.tasks = new CircularArray<T>(logCapacity);
        this.top = new AtomicReference<Integer>(0);
        this.bottom = 0;
    }

    boolean isEmpty() {
        int localTop = top.get();
        int localBottom = bottom; // read bottom after top 
        return (localBottom <= localTop);
    }

    public void pushBottom(T r) {
        int oldBottom = bottom;
        int oldTop = top.get();
        CircularArray<T> currentTasks = tasks;
        int size = oldBottom - oldTop;
        if(size >= currentTasks.capacity() - 1) {
            currentTasks = currentTasks.resize(oldBottom, oldTop);
            tasks = currentTasks;
        }
        tasks.put(oldBottom, r);
        bottom = oldBottom + 1;
    }

    public T popTop() {
        int oldTop = top.get();
        int newTop = oldTop + 1;
        int oldBottom = bottom; // important that top read before bottom `\label{line:steal:UDEQ-BOTTOP}`
        @SuppressWarnings("unused")
        CircularArray<T> currentTasks = tasks;
        int size = oldBottom - oldTop;
        if(size <= 0) {
            return null; // empty `\label{line:steal:UDEQ-POPEmpty}`
        }
        T r = tasks.get(oldTop);
        if(top.compareAndSet(oldTop, newTop)) {// fetch and increment `\label{line:steal:UDEQ-CAS}`
            return r;
        }
        return null;
    }

    public T popBottom() {
        @SuppressWarnings("unused")
        CircularArray<T> currentTasks = tasks;
        bottom--;
        int oldTop = top.get();
        int newTop = oldTop + 1;
        int size = bottom - oldTop;
        if(size < 0) {
            bottom = oldTop;
            return null;
        }
        T r = tasks.get(bottom);
        if(size > 0) {
            return r;
        }
        if(!top.compareAndSet(oldTop, newTop)) { // fetch and increment `\label{line:steal:UDEQ-t+1}`
            return null; // queue is empty
        }
        bottom = oldTop + 1;
        return r;
    }

    private static final class CircularArray<T> {
        private final int logCapacity;
        private final T[] currentTasks;

        @SuppressWarnings("unchecked")
        CircularArray(int logCapacity) {
            this.logCapacity = logCapacity;
            currentTasks = (T[]) new Object[1 << logCapacity];
        }

        int capacity() {
            return 1 << this.logCapacity;
        }

        T get(int i) {
            return this.currentTasks[i % capacity()];
        }

        void put(int i, T task) {
            this.currentTasks[i % capacity()] = task;
        }

        CircularArray<T> resize(int bottom, int top) {
            final CircularArray<T> newTasks = new CircularArray<T>(this.logCapacity + 1);
            for(int i = top; i < bottom; i++) {
                newTasks.put(i, this.get(i));
            }
            return newTasks;
        }
    }
}
