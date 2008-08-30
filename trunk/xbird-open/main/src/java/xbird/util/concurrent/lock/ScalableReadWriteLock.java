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
package xbird.util.concurrent.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * A fair fast scalable reader-writer lock.
 * <DIV lang="en">
 * O. Krieger, M. Stumm, R. Unrau, J. Hanna, "A fair fast scalable reader-writer lock," 
 * In Proc. International Conference on Parallel Processing  (CRC Press), vol. II - Software, pp. II-201-II-204, 1993.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @deprecated
 */
public final class ScalableReadWriteLock implements ReadWriteLock {

    private final ThreadLocal<QNode> myNode;
    private final AtomicReference<QNode> tail;

    private final Lock readLock; // readers apply here
    private final Lock writeLock; // writers apply here 

    public ScalableReadWriteLock() {
        this.myNode = new ThreadLocal<QNode>() {
            protected QNode initialValue() {
                return new QNode();
            }
        };
        this.tail = new AtomicReference<QNode>(null);
        this.readLock = new ReadLock(this);
        this.writeLock = new WriteLock(this);
    }

    public Lock readLock() {
        return readLock;
    }

    public Lock writeLock() {
        return writeLock;
    }

    private static final class QNode {
        State state;
        boolean locked; // a local spin variable
        QNode next, prev; // neighbor pointers
        final ILock el; // a spin lock 

        QNode() {
            this.el = new AtomicBackoffLock();
        }
    }

    private enum State {
        reader, writer, active_reader
    }

    private static final class WriteLock implements Lock {

        private final ScalableReadWriteLock rwlock;

        WriteLock(ScalableReadWriteLock rwlock) {
            this.rwlock = rwlock;
        }

        public void lock() {
            QNode i = rwlock.myNode.get();
            i.state = State.writer;
            i.locked = true;
            i.next = null;
            QNode pred = rwlock.tail.getAndSet(i);
            if(pred != null) {
                pred.next = i;
                // wait until predecessor gives up the lock
                while(pred.locked)
                    ;
            }
        }

        public void unlock() {
            QNode i = rwlock.myNode.get();
            if(i.next == null) {
                if(rwlock.tail.compareAndSet(i, null)) {
                    return;
                }
                // wait until predecessor fills in its next fields
                while(i.next == null)
                    ;
            }
            i.next.prev = null;
            i.next.locked = false;
        }

        public boolean isLocked() {
            throw new UnsupportedOperationException();
        }

        public void lockInterruptibly() throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }

        public boolean tryLock() {
            throw new UnsupportedOperationException();
        }

        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException();
        }
    }

    private static final class ReadLock implements Lock {

        private final ScalableReadWriteLock rwlock;

        ReadLock(ScalableReadWriteLock rwlock) {
            this.rwlock = rwlock;
        }

        public void lock() {
            QNode i = rwlock.myNode.get();
            i.state = State.reader;
            i.locked = true;
            i.next = i.prev = null;
            QNode pred = rwlock.tail.getAndSet(i);
            if(pred != null) {
                i.prev = pred;
                pred.next = i;
                if(pred.state != State.active_reader) {
                    // wait until predecessor gives up the lock
                    while(pred.locked)
                        ;
                }
            }
            if(i.next != null && i.next.state == State.reader) {
                i.next.locked = false;
            }
            i.state = State.active_reader;
        }

        public void unlock() {
            QNode i = rwlock.myNode.get();
            QNode prev = i.prev;
            if(prev != null) {
                prev.el.lock();
                while(prev != i.prev) {
                    prev.el.unlock();
                    prev = i.prev;
                    if(prev == null) {
                        break;
                    }
                    prev.el.lock();
                }
                if(prev != null) {
                    i.el.lock();
                    prev.next = null;
                    if(i.next == null) {
                        if(!rwlock.tail.compareAndSet(i, i.prev)) {
                            while(i.next == null)
                                ;
                        }
                    }
                    if(i.next != null) {
                        i.next.prev = i.prev;
                        i.prev.next = i.next;
                    }
                    i.el.unlock();
                    prev.el.unlock();
                    return;
                }
            }
            i.el.lock();
            if(i.next == null) {
                if(!rwlock.tail.compareAndSet(i, null)) {
                    while(i.next == null)
                        ;
                }
            }
            if(i.next != null) {
                i.next.locked = false;
                i.prev.prev = null;
            }
            i.el.unlock();
        }

        public boolean isLocked() {
            throw new UnsupportedOperationException();
        }

        public void lockInterruptibly() throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }

        public boolean tryLock() {
            throw new UnsupportedOperationException();
        }

        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            throw new UnsupportedOperationException();
        }
    }
}
