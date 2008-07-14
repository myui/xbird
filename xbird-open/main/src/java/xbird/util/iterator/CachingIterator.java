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
package xbird.util.iterator;

import java.util.Iterator;
import java.util.List;

import xbird.util.collections.SparseArrayList;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class CachingIterator<E> implements Iterator<E> {

    private final Iterator<E> delegate;
    private final List<E> values = new SparseArrayList<E>(4);
    private int vindex = 0;

    public CachingIterator(Iterator<E> delegate) {
        this.delegate = delegate;
    }

    public boolean hasNext() {
        if(vindex < values.size()) {
            return true;
        } else {
            return delegate.hasNext();
        }
    }

    public E next() {
        if(vindex < values.size()) {
            return values.get(vindex++);
        }
        E nextval = delegate.next();
        values.add(nextval);
        ++vindex;
        return nextval;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void rewind() {
        this.vindex = 0;
    }

}
