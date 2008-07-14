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
 *     Hanson Char - initially written and released to the public domain
 *     Makoto YUI - imported
 */
package xbird.util.concurrent.collections;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * An unbounded concurrent blocking queue implemented upon 
 * {@link java.util.concurrent.ConcurrentLinkedQueue ConcurrentLinkedQueue}.
 * <DIV lang="en">
 * In contrast to {@link java.util.concurrent.LinkedBlockingQueue LinkedBlockingQueue}
 * which is always bounded, a ConcurrentLinkedBlockingQueue is unbounded.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Hanson Char
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://hansonchar.blogspot.com/2006/09/concurrentlinkedblockingqueue.html
 */
public class ConcurrentLinkedBlockingQueue<E> extends AbstractQueue<E>
        implements Serializable, BlockingQueue<E> {
    private static final long serialVersionUID = -2892746910905397931L;

    private transient final ConcurrentLinkedQueue<ThreadMarker> _takeparkq;
    private final ConcurrentLinkedQueue<E> _clq;

    public ConcurrentLinkedBlockingQueue() {
        this._clq = new ConcurrentLinkedQueue<E>();
        this._takeparkq = new ConcurrentLinkedQueue<ThreadMarker>();
    }

    public ConcurrentLinkedBlockingQueue(Collection<? extends E> c) {
        this._clq = new ConcurrentLinkedQueue<E>(c);
        this._takeparkq = new ConcurrentLinkedQueue<ThreadMarker>();
    }

    @Override
    public Iterator<E> iterator() {
        return _clq.iterator();
    }

    @Override
    public int size() {
        return _clq.size();
    }

    public boolean offer(E e) {
        _clq.offer(e);
        unparkTakeRequests();
        return true;
    }

    public E poll() {
        return _clq.poll();
    }

    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return offer(e);
    }

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
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        if(timeout < 0) {
            return take();
        }
        final long t1 = System.nanoTime() + unit.toNanos(timeout);
        for(;;) {
            E e = _clq.poll();
            if(e != null) {
                return e;
            }
            final long duration = t1 - System.nanoTime();
            if(duration <= 0) {
                return null; // timeout
            }
            final Thread thread = Thread.currentThread();
            if(thread.isInterrupted()) {// avoid the parkq.offer(m) if already interrupted
                throw new InterruptedException();
            }
            final ThreadMarker m = new ThreadMarker(Thread.currentThread());
            _takeparkq.offer(m);
            // check again in case there is data race
            e = _clq.poll();
            if(e != null) {// data race indeed
                m.parked = false;
                return e;
            }
            LockSupport.parkNanos(duration);
            m.parked = false;
            if(Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    /**
     * Retrieves and removes the head of this queue, waiting if necessary until
     * an element becomes available.
     * 
     * @return the head of this queue
     * @throws InterruptedException if interrupted while waiting
     */
    public E take() throws InterruptedException {
        for(;;) {
            E e = _clq.poll();
            if(e != null) {
                return e;
            }

            final Thread thread = Thread.currentThread();
            if(thread.isInterrupted()) {// avoid the parkq.offer(m) if already interrupted
                throw new InterruptedException();
            }
            final ThreadMarker m = new ThreadMarker(thread);
            _takeparkq.offer(m);

            // check again in case there is data race
            e = _clq.poll();
            if(e != null) {// data race indeed
                m.parked = false;
                return e;
            }

            LockSupport.park(); // RACE1
            m.parked = false;

            if(Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    public void put(E e) throws InterruptedException {
        offer(e);
    }

    protected final void unparkTakeRequests() {
        for(;;) {
            final ThreadMarker marker = _takeparkq.poll();
            if(marker == null) {
                break;
            }
            if(marker.parked) {
                LockSupport.unpark(marker.thread);
                break;
            }
        }
    }

    protected void unparkPutRequests() {}

    public E peek() {
        return _clq.peek();
    }

    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    public int drainTo(Collection<? super E> c) {
        int i = 0;
        E e;
        for(; (e = _clq.poll()) != null; i++) {
            c.add(e);
        }
        return i;
    }

    public int drainTo(Collection<? super E> c, int maxElements) {
        int i = 0;
        E e;
        for(; i < maxElements && (e = _clq.poll()) != null; i++) {
            c.add(e);
        }
        return i;
    }

    protected static final class ThreadMarker {
        final Thread thread;
        volatile boolean parked = true;

        ThreadMarker(Thread thread) {
            this.thread = thread;
        }
    }

}
