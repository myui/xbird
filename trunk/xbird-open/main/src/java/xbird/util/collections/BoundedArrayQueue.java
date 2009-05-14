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
public final class BoundedArrayQueue<T> extends ArrayQueue<T> {
    private static final long serialVersionUID = 7186414338617185605L;

    public BoundedArrayQueue(int arysize) {
        super(arysize);
    }

    public BoundedArrayQueue(T[] array) {
        super(array);
    }

    @Override
    public boolean offer(T x) {
        if(isFull()) {
            return false;
        }
        _array[_lastIndex++] = x;
        return true;
    }

}
