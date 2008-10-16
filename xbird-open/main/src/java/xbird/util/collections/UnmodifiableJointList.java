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

import java.util.AbstractList;
import java.util.List;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class UnmodifiableJointList<E> extends AbstractList<E> {

    private final List<E>[] _lists;
    private final int _size;

    public UnmodifiableJointList(List<E>... lists) {
        super();
        this._lists = lists;
        int size = 0;
        for(List<E> list : lists) {
            size += list.size();
        }
        this._size = size;
    }

    @Override
    public E get(final int index) {
        int i = index;
        for(List<E> list : _lists) {
            final int size = list.size();
            if(i < size) {
                return list.get(i);
            } else {
                i -= size;
            }
        }
        throw new IndexOutOfBoundsException("Index `" + index + "' out of bounds `" + _size + '\'');
    }

    @Override
    public int size() {
        return _size;
    }

}
