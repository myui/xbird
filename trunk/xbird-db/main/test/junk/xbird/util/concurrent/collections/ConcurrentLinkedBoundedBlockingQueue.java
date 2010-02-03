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
 *     Hanson Char - implemented and released to the public domain.
 *     Makoto YUI - imported and fixed bug in take().
 */
package xbird.util.concurrent.collections;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * An bounded concurrent blocking queue implemented upon 
 * {@link java.util.concurrent.ConcurrentLinkedQueue ConcurrentLinkedQueue}.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Hanson Char
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @param <E> the type of elements held in this collection
 * @deprecated since bug found in put/take
 */
public class ConcurrentLinkedBoundedBlockingQueue<E> extends ConcurrentLinkedBlockingQueue<E> {
    private static final long serialVersionUID = 7794723509269497435L;

    /** size of allocatable spaces */
    private final AtomicInteger _capacity;
    private transient final ConcurrentLinkedQueue<ThreadMarker> _putparkq = new ConcurrentLinkedQueue<ThreadMarker>();

    public ConcurrentLinkedBoundedBlockingQueue(int capacity) {
        if(capacity < 1) {
            throw new IllegalArgumentException();
        }
        this._capacity = new AtomicInteger(capacity);
    }

    public ConcurrentLinkedBoundedBlockingQueue(Collection<? extends E> c, int addtionalCapacity) {
        this(c.size() + addtionalCapacity);
        for(E e : c) {
            add(e);
        }
    }

    @Override
    public boolean offer(E e) {
        if(tryDecrementCapacity()) {
            return super.offer(e);
        }
        return false;
    }

    @Override
    public E poll() {
        final E e = super.poll();
        if(e != null) {
            _capacity.incrementAndGet();
            unparkPutRequests();
        }
        return e;
    }

    // -----------------------------------------------------------
    // START blocked operations

    /**
     * Retrieves and removes the head of this queue, waiting if necessary until
     * an element becomes available.
     * 
     * @return the head of this queue
     * @throws InterruptedException if interrupted while waiting
     */
    @Override
    public E take() throws InterruptedException {
        E e = super.take(); // RACE1
        _capacity.incrementAndGet();
        unparkPutRequests();
        return e;
    }

    @Override
    public void put(E e) throws InterruptedException {
        for(;;) {
            if(tryDecrementCapacity()) {
                super.put(e);
                return;
            }

            Thread thread = Thread.currentThread();
            if(thread.isInterrupted()) { // avoid the putparkq.offer(m) if already interrupted
                throw new InterruptedException();
            }
            ThreadMarker m = new ThreadMarker(thread);
            _putparkq.offer(m);

            // check again in case there is data race
            if(tryDecrementCapacity()) { // data race indeed
                m.parked = false;
                super.put(e);
                return;
            }

            LockSupport.park(); // RACE1
            m.parked = false;
            if(Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    private boolean tryDecrementCapacity() {
        int capa;
        do {
            capa = _capacity.get();
            if(capa == 0) {
                return false;
            }
        } while(!_capacity.compareAndSet(capa, capa - 1));
        return true;
    }

    // END blocked operations 
    // -----------------------------------------------------------

    /**
     * Retrieves and removes the head of this queue, waiting up to the specified
     * wait time if necessary for an element to become available.
     * 
     * @param timeout
     *            how long to wait before giving up, in units of <tt>unit</tt>.
     *            A negative timeout is treated the same as to wait forever.
     * @param unit
     *            a <tt>TimeUnit</tt> determining how to interpret the
     *            <tt>timeout</tt> parameter
     * @return the head of this queue, or <tt>null</tt> if the specified
     *         waiting time elapses before an element is available
     * @throws InterruptedException if interrupted while waiting
     */
    @Override
    public E poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        final E e = super.poll(timeout, unit);
        if(e != null) {
            this._capacity.incrementAndGet();
            unparkPutRequests();
        }
        return e;
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        if(timeout < 0) { // treat -ve timeout same as to wait forever
            this.put(e);
            return true;
        }
        final long t1 = System.nanoTime() + unit.toNanos(timeout);

        for(;;) {
            if(tryDecrementCapacity()) {
                return super.offer(e, timeout, unit);
            }
            final long duration = t1 - System.nanoTime();
            if(duration <= 0) {
                return false; // time out
            }
            Thread thread = Thread.currentThread();
            if(thread.isInterrupted()) { // avoid the putparkq.offer(m) if already interrupted
                throw new InterruptedException();
            }
            ThreadMarker m = new ThreadMarker(thread);
            _putparkq.offer(m);
            // check again in case there is data race
            if(tryDecrementCapacity()) { // data race indeed
                m.parked = false;
                super.offer(e);
                return true;
            }
            LockSupport.parkNanos(duration);
            m.parked = false;

            if(Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    @Override
    public int remainingCapacity() {
        return _capacity.get();
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        int i = 0;
        E e;
        for(; (e = this.poll()) != null; i++) {
            c.add(e);
        }
        return i;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        int i = 0;
        E e;
        for(; i < maxElements && (e = this.poll()) != null; i++) {
            c.add(e);
        }
        return i;
    }

    @Override
    protected final void unparkPutRequests() {
        for(;;) {
            final ThreadMarker marker = _putparkq.poll();
            if(marker == null) {
                return;
            }
            if(marker.parked) {
                LockSupport.unpark(marker.thread);
                return;
            }
        }
    }

}
