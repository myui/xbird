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

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ConcurrentBoundedQueue<E> extends AbstractQueue<E> implements Queue<E> {

    private final int _capacity;
    private final AtomicReferenceArray<E> _array;

    private final AtomicInteger _readPtr, _writePtr;
    private final AtomicInteger _used, _free; // as barriers

    public ConcurrentBoundedQueue(int capacity) {
        this._capacity = capacity;
        this._array = new AtomicReferenceArray<E>(capacity);
        this._readPtr = new AtomicInteger(0);
        this._writePtr = new AtomicInteger(0);
        this._used = new AtomicInteger(0);
        this._free = new AtomicInteger(capacity);
    }

    public boolean offer(final E elem) {
        if(elem == null) {
            throw new NullPointerException();
        }
        for(;;) {
            final int free = _free.get();
            if(free == 0) {
                return false; // no free space
            }
            if(_free.compareAndSet(free, free - 1)) {
                break; // ensured that there are free space
            }
        }
        int idx = _writePtr.get();
        while(_array.compareAndSet(idx % _capacity, null, elem)) {
            idx++;
        }
        _writePtr.getAndIncrement(); // intended.. always increment by one
        _used.getAndIncrement();
        return true;
    }

    public E poll() {
        for(;;) {
            final int used = _used.get();
            if(used == 0) {
                return null; // no entries
            }
            if(_used.compareAndSet(used, used - 1)) {
                break; // ensured that there are entries
            }
        }
        E elem;
        for(int idx = _readPtr.get();; idx++) {
            elem = _array.getAndSet(idx % _capacity, null);
            if(elem != null) {
                break;
            }
            // Since it is ensured that there are entries, it'll never be an infinity loop.
        }
        _readPtr.getAndIncrement(); // intended.. always increment by one
        _free.getAndIncrement();
        return elem;
    }

    public E peek() {
        for(;;) {
            final int used = _used.get();
            if(used == 0) {
                return null; // no entries
            }
            if(_used.compareAndSet(used, used - 1)) {
                break; // ensured that there are entries
            }
        }
        for(int idx = _readPtr.get();; idx++) {
            final E elem = _array.get(idx % _capacity);
            if(elem != null) {
                return elem;
            }
            // Since it is ensured that there are entries, it'll never be an infinity loop.
        }
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public int size() {
        return _used.get();
    }

}
