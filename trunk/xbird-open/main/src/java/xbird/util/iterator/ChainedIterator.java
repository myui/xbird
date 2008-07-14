/*
 * @(#)$Id: ChainedIterator.java 3619 2008-03-26 07:23:03Z yui $
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
import java.util.NoSuchElementException;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class ChainedIterator<T> implements Iterator<T> {

    private final Iterator<? extends T> _first;
    private final Iterator<? extends T> _second;
    private boolean _reachedEnd = false;

    public ChainedIterator(Iterator<? extends T> first, Iterator<? extends T> second) {
        this._first = first;
        this._second = second;
    }

    public Iterator<? extends T> getFirstIterator() {
        return _first;
    }

    public Iterator<? extends T> getSecondIterator() {
        return _second;
    }

    public boolean hasNext() {
        if(_reachedEnd) {
            return false;
        }
        if(_first.hasNext()) {
            return true;
        } else {
            final boolean hasmore = _second.hasNext();
            if(!hasmore) {
                this._reachedEnd = true;
            }
            return hasmore;
        }
    }

    public T next() {
        if(_reachedEnd) {
            throw new NoSuchElementException();
        }
        if(_first.hasNext()) {
            return _first.next();
        } else if(_second.hasNext()) {
            return _second.next();
        } else {
            _reachedEnd = true;
            throw new NoSuchElementException();
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

}
