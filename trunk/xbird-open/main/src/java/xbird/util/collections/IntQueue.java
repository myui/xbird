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

import java.util.Arrays;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class IntQueue {

    public static final int DEFAULT_ARY_SIZE = 16;

    private int _index = 0;
    private int _lastIndex = 0;

    private int _arraySize;
    private int[] _array;

    public IntQueue() {
        this(DEFAULT_ARY_SIZE);
    }

    public IntQueue(int arysize) {
        this(new int[arysize]);
    }

    public IntQueue(int[] array) {
        this._array = array;
        this._arraySize = array.length;
    }
    
    public IntQueue(int[] array, int cur, int last) {
        this._array = array;
        this._arraySize = array.length;
        this._index = cur;
        this._lastIndex = last;
    }

    public final void add(int i) {
        if(_lastIndex >= _arraySize) {
            growArray();
        }
        _array[_lastIndex++] = i;
    }

    public final void enqueue(int i) {
        this.add(i);
    }

    public final int next() {
        return _array[_index++];
    }

    public final int dequeue() {
        return this.next();
    }

    public final boolean isEmpty() {
        return _index < _lastIndex;
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
        int[] newArray = new int[_arraySize * 2];
        System.arraycopy(_array, 0, newArray, 0, _arraySize);
        _array = newArray;
        _arraySize *= 2;
    }
}
