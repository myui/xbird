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
 * A Scheduler-Conscious Fair Reader-Writer Lock (RW-Smart-Q).
 * <DIV lang="en">
 * L. I. Kontothanassis, R. W. Wisniewski, and M. L. Scott: "Scheduler-conscious synchronization," 
 * ACM Trans. Comput. Syst., vol. 15, no. 1, pp. 3-40, 1997. 
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.cs.rochester.edu/research/synchronization/pseudocode/ps_and_sc.html#read-write
 */
public final class FairSmartReadWriteLock implements ReadWriteLock {

    private final ThreadLocal<QNode> myNode;
    private final AtomicReference<QNode> tail;
    private final AtomicReference<ContextBlock> context;

    private final Lock readLock; // readers apply here
    private final Lock writeLock; // writers apply here 

    public FairSmartReadWriteLock() {
        this.myNode = new ThreadLocal<QNode>() {
            protected QNode initialValue() {
                return new QNode();
            }
        };
        this.tail = new AtomicReference<QNode>(null);
        this.context = new AtomicReference<ContextBlock>(null);
        this.readLock = new ReadLock(this);
        this.writeLock = new WriteLock(this);
    }

    public Lock readLock() {
        return readLock;
    }

    public Lock writeLock() {
        return writeLock;
    }

    public IReadWriteLock asSpinLock() {
        return new ReadWriteSpinLockAdapter(this);
    }

    private enum ContextBlock {
        preempted, preemptable, unpreemptable_self, unpreemptable_other;
        boolean warning = false;

        ContextBlock() {}
    }

    private static final class QNode {
        final AtomicReference<ContextBlock> self;
        State state;
        SpinState spin; // a local spin variable
        QNode next, prev; // neighbor pointers
        final ILock el; // a spin lock 

        QNode() {
            this.self = new AtomicReference<ContextBlock>(null);
            this.el = new AtomicBackoffLock();

        }
    }

    private enum State {
        reader, active_reader, writer
    }

    private enum SpinState {
        waiting, success, failure
    }

    private static final class ReadWriteSpinLockAdapter implements IReadWriteLock {

        private final FairSmartReadWriteLock delegate;

        ReadWriteSpinLockAdapter(FairSmartReadWriteLock lock) {
            this.delegate = lock;
        }

        public void readLock() {
            delegate.readLock().lock();
        }

        public void readUnlock() {
            delegate.readLock().unlock();
        }

        public void writeLock() {
            delegate.writeLock().lock();
        }

        public void writeUnlock() {
            delegate.writeLock().unlock();
        }

    }

    private static final class WriteLock implements Lock {

        private final FairSmartReadWriteLock rwlock;
        private final AtomicReference<ContextBlock> cb;

        WriteLock(FairSmartReadWriteLock rwlock) {
            this.rwlock = rwlock;
            this.cb = rwlock.context;
        }

        public void lock() {
            QNode i = rwlock.myNode.get();
            i.self.set(rwlock.context.get());
            while(i.spin != SpinState.success) {
                rwlock.context.set(ContextBlock.unpreemptable_self);
                i.state = State.writer;
                i.spin = SpinState.waiting;
                i.next = null;
                QNode pred = rwlock.tail.getAndSet(i);
                if(pred != null) {
                    pred.next = i;
                    rwlock.context.compareAndSet(ContextBlock.unpreemptable_self, ContextBlock.preemptable);
                    // wait until predecessor gives up the lock
                    while(i.spin == SpinState.waiting)
                        ;
                }
            }
        }

        public void unlock() {
            final QNode i = rwlock.myNode.get();
            QNode shadow = i;
            QNode candidate = i.next;
            for(;;) {
                if(candidate == null) {
                    if(rwlock.tail.compareAndSet(shadow, null)) {
                        shadow.spin = SpinState.failure;
                        break;
                    }
                    while(shadow.next == null)
                        ;
                    candidate = shadow.next;
                }
                if(candidate.self.compareAndSet(ContextBlock.unpreemptable_self, ContextBlock.unpreemptable_other)
                        || candidate.self.compareAndSet(ContextBlock.preemptable, ContextBlock.unpreemptable_other)) {
                    candidate.prev = null;
                    candidate.spin = SpinState.success;
                    break;
                } else {// candidate seems to be preempted
                    shadow = candidate;
                    candidate = shadow.next;
                    shadow.spin = SpinState.failure;
                }
            }
            cb.set(ContextBlock.preemptable);
            if(cb.get().warning) {
                Thread.yield();
            }
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

        private final FairSmartReadWriteLock rwlock;

        ReadLock(FairSmartReadWriteLock rwlock) {
            this.rwlock = rwlock;
        }

        public void lock() {
            QNode i = rwlock.myNode.get();
            i.state = State.reader;
            i.spin = true;
            i.next = i.prev = null;
            QNode pred = rwlock.tail.getAndSet(i);
            if(pred != null) {
                i.prev = pred;
                pred.next = i;
                if(pred.state != State.active_reader) {
                    // wait until predecessor gives up the lock
                    while(pred.spin)
                        ;
                }
            }
            if(i.next != null && i.next.state == State.reader) {
                i.next.spin = false;
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
                i.next.spin = false;
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
