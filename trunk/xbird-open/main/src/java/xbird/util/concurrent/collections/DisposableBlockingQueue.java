/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import xbird.util.iterator.EmptyIterator;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DisposableBlockingQueue<E> implements IDisposableBlockingQueue<E> {

    private final BlockingQueue<E> _delegate;
    private volatile boolean _disposed = false;

    public DisposableBlockingQueue(BlockingQueue<E> delegate, E sentinel) {
        this._delegate = delegate;
    }

    public static <E> DisposableBlockingQueue<E> of(BlockingQueue<E> delegate, E sentinel) {
        return new DisposableBlockingQueue<E>(delegate, sentinel);
    }

    public boolean isDisposed() {
        return _disposed;
    }

    public void dispose() {
        if(!_disposed) {
            this._disposed = true;
            _delegate.clear();
        }
    }

    // ------------------------------------------------------------------
    // delegated methods    

    public boolean add(E e) {
        if(_disposed) {
            return false;
        }
        return _delegate.add(e);
    }

    public boolean addAll(Collection<? extends E> c) {
        if(_disposed) {
            return false;
        }
        return _delegate.addAll(c);
    }

    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        if(_disposed) {
            return false;
        }
        return _delegate.offer(e, timeout, unit);
    }

    public boolean offer(E e) {
        if(_disposed) {
            return false;
        }
        return _delegate.offer(e);
    }

    public void put(E e) throws InterruptedException {
        if(_disposed) {
            return;
        }
        _delegate.put(e);
    }

    /**
     * @return available?
     */
    public boolean putIfAvailable(E e) throws InterruptedException {
        if(_disposed) {
            return false;
        }
        _delegate.put(e);
        return true;
    }

    public E take() throws InterruptedException {
        if(_disposed) {
            throw new IllegalStateException("Blocking queue is already closed");
        }
        return _delegate.take();
    }

    public boolean contains(Object o) {
        if(_disposed) {
            return false;
        }
        return _delegate.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        if(_disposed) {
            return false;
        }
        return _delegate.containsAll(c);
    }

    public int drainTo(Collection<? super E> c, int maxElements) {
        if(_disposed) {
            return 0;
        }
        return _delegate.drainTo(c, maxElements);
    }

    public int drainTo(Collection<? super E> c) {
        if(_disposed) {
            return 0;
        }
        return _delegate.drainTo(c);
    }

    public E element() {
        if(_disposed) {
            throw new NoSuchElementException();
        }
        return _delegate.element();
    }

    public boolean isEmpty() {
        if(_disposed) {
            return true;
        }
        return _delegate.isEmpty();
    }

    public Iterator<E> iterator() {
        if(_disposed) {
            return EmptyIterator.emptyIterator();
        }
        return _delegate.iterator();
    }

    public E peek() {
        if(_disposed) {
            return null;
        }
        return _delegate.peek();
    }

    public E poll() {
        if(_disposed) {
            return null;
        }
        return _delegate.poll();
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        if(_disposed) {
            return null;
        }
        return _delegate.poll(timeout, unit);
    }

    public int remainingCapacity() {
        if(_disposed) {
            return 0;
        }
        return _delegate.remainingCapacity();
    }

    public E remove() {
        if(_disposed) {
            return null;
        }
        return _delegate.remove();
    }

    public boolean remove(Object o) {
        if(_disposed) {
            return false;
        }
        return _delegate.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        if(_disposed) {
            return false;
        }
        return _delegate.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        if(_disposed) {
            return false;
        }
        return _delegate.retainAll(c);
    }

    public int size() {
        if(_disposed) {
            return 0;
        }
        return _delegate.size();
    }

    public Object[] toArray() {
        if(_disposed) {
            return new Object[0];
        }
        return _delegate.toArray();
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if(_disposed) {
            return (T[]) new Object[0];
        }
        return _delegate.toArray(a);
    }

    public void clear() {
        _delegate.clear();
    }
}
