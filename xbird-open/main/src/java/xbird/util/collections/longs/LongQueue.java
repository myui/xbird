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
package xbird.util.collections.longs;

import java.util.Arrays;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class LongQueue {

    public static final int DEFAULT_ARY_SIZE = 16;

    private transient int _index = 0;
    private int _lastIndex = 0;

    private int _arraySize;
    private long[] _array;

    public LongQueue() {
        this(DEFAULT_ARY_SIZE);
    }

    public LongQueue(int arysize) {
        this._array = new long[arysize];
        this._arraySize = arysize;
    }

    public LongQueue(long[] array) {
        this._array = array;
        this._arraySize = array.length;
    }

    public LongQueue(long[] array, int cur, int last) {
        this._array = array;
        this._arraySize = array.length;
        this._index = cur;
        this._lastIndex = last;
    }

    public final long get(int i) {
        return _array[i];
    }

    public final void add(long i) {
        if(_lastIndex >= _arraySize) {
            growArray();
        }
        _array[_lastIndex++] = i;
    }

    public final void enqueue(long i) {
        this.add(i);
    }

    public final long next() {
        return _array[_index++];
    }

    public final long dequeue() {
        return this.next();
    }

    public final boolean isEmpty() {
        return _index > _lastIndex;
    }

    public final void clear() {
        _index = 0;
        _lastIndex = 0;
    }

    public final int size() {
        return _lastIndex - _index;
    }

    public final void sort() {
        Arrays.sort(_array, _index, _lastIndex);
    }

    private void growArray() {
        long[] newArray = new long[_arraySize * 2];
        System.arraycopy(_array, 0, newArray, 0, _arraySize);
        _array = newArray;
        _arraySize = newArray.length;
    }

    public long[] toArray() {
        if(_arraySize == 0) {
            return new long[0];
        }
        final int size = size();
        final long[] ary = new long[size];
        System.arraycopy(_array, _index, ary, 0, size);
        return ary;
    }

}
