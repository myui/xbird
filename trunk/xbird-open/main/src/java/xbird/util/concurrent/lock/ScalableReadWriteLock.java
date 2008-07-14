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
            next = prev = null;
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
            QNode qnode = rwlock.myNode.get();
            qnode.state = State.writer;
            qnode.locked = true;
            qnode.next = null;
            QNode pred = rwlock.tail.getAndSet(qnode);
            if(pred != null) {
                pred.next = qnode;
                // wait until predessor gives up the lock
                while(pred.locked)
                    ;
            }
        }

        public void unlock() {
            QNode qnode = rwlock.myNode.get();
            if(qnode.next == null) {
                if(rwlock.tail.compareAndSet(qnode, null)) {
                    return;
                }
                // wait until predecessor fills in its next fields
                while(qnode.next == null)
                    ;
            }
            qnode.next.prev = null;
            qnode.next.locked = false;
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
            QNode qnode = rwlock.myNode.get();
            qnode.state = State.reader;
            qnode.locked = true;
            qnode.next = qnode.prev = null;
            QNode pred = rwlock.tail.getAndSet(qnode);
            if(pred != null) {
                qnode.prev = pred;
                pred.next = qnode;
                if(pred.state != State.active_reader) {
                    // wait until predessor gives up the lock
                    while(pred.locked)
                        ;
                }
            }
            if(qnode.next != null && qnode.next.state == State.reader) {
                qnode.next.locked = false;
            }
            qnode.state = State.active_reader;
        }

        public void unlock() {
            QNode qnode = rwlock.myNode.get();
            QNode prev = qnode.prev;
            if(prev != null) {
                prev.el.lock();
                while(prev != qnode.prev) {
                    prev.el.unlock();
                    prev = qnode.prev;
                    if(prev == null) {
                        break;
                    }
                    prev.el.lock();
                }
                if(prev != null) {
                    qnode.el.lock();
                    prev.next = null;
                    if(qnode.next == null) {
                        if(!rwlock.tail.compareAndSet(qnode, qnode.prev)) {
                            while(qnode.next == null)
                                ;
                        }
                    }
                    if(qnode.next != null) {
                        qnode.next.prev = qnode.prev;
                        qnode.prev.next = qnode.next;
                    }
                    qnode.el.unlock();
                    prev.el.unlock();
                    return;
                }
                prev.el.unlock();
            }
            qnode.el.lock();
            if(qnode.next == null) {
                if(!rwlock.tail.compareAndSet(qnode, null)) {
                    while(qnode.next == null)
                        ;
                }
            }
            if(qnode.next != null) {
                qnode.next.locked = false;
                qnode.prev.prev = null;
            }
            qnode.el.unlock();
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
