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

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import xbird.util.concurrent.jsr166.LinkedTransferQueue;
import xbird.util.concurrent.jsr166.TransferQueue;

/**
 * 
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class BoundedTransferQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> {
    private static final long serialVersionUID = 7794723509269497435L;

    private final int _maxCapacity;
    private final AtomicInteger _remainingCapacity;
    private final TransferQueue<E> _queue;

    public BoundedTransferQueue(int capacity) {
        if(capacity < 1) {
            throw new IllegalArgumentException();
        }
        this._maxCapacity = capacity;
        this._remainingCapacity = new AtomicInteger(capacity);
        this._queue = new LinkedTransferQueue<E>();
    }

    public boolean offer(E e) {
        if(tryDecrementCapacity()) {
            return _queue.offer(e);
        }
        return false;
    }

    public E poll() {
        final E e = _queue.poll();
        if(e != null) {
            _remainingCapacity.incrementAndGet();
        }
        return e;
    }

    public void put(E e) throws InterruptedException {
        if(tryDecrementCapacity()) {
            _queue.put(e);
        } else {
            _queue.transfer(e);
            _remainingCapacity.decrementAndGet();
        }
    }

    public E take() throws InterruptedException {
        E e = _queue.take();
        _remainingCapacity.incrementAndGet();
        return e;
    }

    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        if(tryDecrementCapacity()) {
            return _queue.offer(e, timeout, unit);
        } else {
            final boolean succeed = _queue.tryTransfer(e, timeout, unit);
            if(succeed) {
                _remainingCapacity.decrementAndGet();
            }
            return succeed;
        }
    }

    public E poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        final E e = _queue.poll(timeout, unit);
        if(e != null) {
            _remainingCapacity.incrementAndGet();
        }
        return e;
    }

    private boolean tryDecrementCapacity() {
        int capa;
        do {
            capa = _remainingCapacity.get();
            if(capa == 0) {
                return false;
            }
        } while(!_remainingCapacity.compareAndSet(capa, capa - 1));
        return true;
    }

    // -------------------------------------------------------
    // delegates everything

    public int remainingCapacity() {
        return _remainingCapacity.get();
    }

    public int drainTo(Collection<? super E> c, int maxElements) {
        return _queue.drainTo(c, maxElements);
    }

    public int drainTo(Collection<? super E> c) {
        return _queue.drainTo(c);
    }

    public Iterator<E> iterator() {
        return _queue.iterator();
    }

    public E peek() {
        return _queue.peek();
    }

    public int size() {
        return _queue.size();
    }

    @Override
    public void clear() {
        _queue.clear();
        _remainingCapacity.set(_maxCapacity);
    }
}
