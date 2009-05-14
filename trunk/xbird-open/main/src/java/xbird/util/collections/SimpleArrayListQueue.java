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
package xbird.util.collections;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class SimpleArrayListQueue<T> {

    public static final int DEFAULT_ARY_SIZE = 16;

    private transient int _index = 0;
    private int _lastIndex = 0;

    private int _arraySize;
    private Object[] _array;

    public SimpleArrayListQueue() {
        this(DEFAULT_ARY_SIZE);
    }

    public SimpleArrayListQueue(int arysize) {
        this._array = new Object[arysize];
        this._arraySize = arysize;
    }

    public SimpleArrayListQueue(T[] array) {
        this._array = array;
        this._arraySize = array.length;
    }

    public SimpleArrayListQueue(T[] array, int cur, int last) {
        this._array = array;
        this._arraySize = array.length;
        this._index = cur;
        this._lastIndex = last;
    }

    public final void offer(T x) {
        if(_lastIndex >= _arraySize) {
            growArray();
        }
        _array[_lastIndex++] = x;
    }

    @SuppressWarnings("unchecked")
    public final T poll() {
        if(_index < _lastIndex) {
            T obj = (T) _array[_index++];
            if(_index == _lastIndex) {
                clear();
            }
            return obj;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public final T peek() {
        return (_index < _lastIndex) ? (T) _array[_index] : null;
    }

    public T get(int index) {
        if(index >= _lastIndex) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + _lastIndex);
        }
        return (T) _array[index];
    }

    public final boolean isEmpty() {
        return _index >= _lastIndex;
    }

    public final void clear() {
        _index = 0;
        _lastIndex = 0;
    }

    public final int size() {
        return _lastIndex - _index;
    }

    private void growArray() {
        Object[] newArray = new Object[_arraySize * 2];
        System.arraycopy(_array, 0, newArray, 0, _arraySize);
        _array = newArray;
        _arraySize = newArray.length;
    }

    @SuppressWarnings("unchecked")
    public T[] toArray() {
        if(_arraySize == 0) {
            return (T[]) new Object[0];
        }
        final int size = size();
        final Object[] ary = new Object[size];
        System.arraycopy(_array, _index, ary, 0, size);
        return (T[]) ary;
    }

}
