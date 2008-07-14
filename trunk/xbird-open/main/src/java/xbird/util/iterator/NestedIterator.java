/*
 * @(#)$Id: NestedIterator.java 3619 2008-03-26 07:23:03Z yui $
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
public class NestedIterator<T> implements Iterator<T> {

    private boolean _innerHasNext = true;
    private boolean _isNextInnerFocus = true;
    private boolean _reachedEnd = false;
    private T _nextElement = null;
    private final Iterator<? extends T> _inner;
    private final Iterator<? extends T> _outer;

    public NestedIterator(Iterator<? extends T> inner, Iterator<? extends T> outer) {
        assert (inner != null && outer != null);
        this._inner = inner;
        this._outer = outer;
    }

    public boolean hasNext() {
        if(_nextElement != null) {
            return true;
        } else if(_reachedEnd) {
            return false;
        } else {
            assert (!_reachedEnd);
            final boolean hasMore = lookahead();
            if(!hasMore) {
                this._reachedEnd = true;
                this._nextElement = null;
            }
            return hasMore;
        }
    }

    private boolean lookahead() {
        assert (_nextElement == null && !_reachedEnd);
        if(_innerHasNext && _isNextInnerFocus) {
            this._innerHasNext = _inner.hasNext();
            this._isNextInnerFocus = false;
            if(_innerHasNext) {
                this._nextElement = _inner.next();
                assert (_nextElement != null);
                return true;
            }
        }
        assert (!_isNextInnerFocus);
        final boolean outerHasNext = _outer.hasNext();
        if(outerHasNext) {
            if(_innerHasNext) {
                this._isNextInnerFocus = true;
            }
            this._nextElement = _outer.next();
            return true;
        } else {
            if(_innerHasNext) {
                this._isNextInnerFocus = true;
                return hasNext();
            }
        }
        return false;
    }

    public T next() {
        if(hasNext()) {
            assert (_nextElement != null);
            T next = _nextElement;
            this._nextElement = null;
            return next;
        } else {
            throw new NoSuchElementException();
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

}
