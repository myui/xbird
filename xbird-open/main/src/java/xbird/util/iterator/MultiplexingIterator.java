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
package xbird.util.iterator;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class MultiplexingIterator<E> implements CloseableIterator<E> {

    private final CloseableIterator<E>[] _itors;
    private final int _numItors;

    private int _itorIdx = 0;
    private E _seeked = null;
    private boolean _hasNoMore;

    public MultiplexingIterator(CloseableIterator<E>... itors) {
        if(itors == null) {
            throw new IllegalArgumentException();
        }
        this._itors = itors;
        this._numItors = itors.length;
        this._hasNoMore = (_numItors == 0);
    }

    public boolean hasNext() {
        if(_seeked != null) {
            return true;
        }
        if(_hasNoMore) {
            return false;
        }
        final E nextItem = emurateNext();
        if(nextItem != null) {
            this._seeked = nextItem;
            return true;
        } else {
            this._hasNoMore = true;
            return false;
        }
    }

    private E emurateNext() {
        if(_seeked != null) {
            throw new IllegalStateException("Illegal condition was met");
        }

        final CloseableIterator<E>[] itors = _itors;
        final int numItors = _numItors;

        assert (_itorIdx < numItors) : _itorIdx;
        final int firstIdx;
        for(int i = _itorIdx;;) {
            if(itors[i++] != null) {
                firstIdx = i;
                break;
            }
            if(i >= numItors) {
                return null;
            }
        }
        final int lastItorIdx = numItors - 1;
        for(int i = firstIdx; i < numItors; i++) {
            final CloseableIterator<E> itor = itors[i];
            if(itor != null) {
                if(itor.hasNext()) {
                    this._itorIdx = (i == lastItorIdx) ? 0 : i + 1;
                    return itor.next();
                } else {
                    try {
                        itor.close();
                    } catch (IOException e) {
                        ;
                    }
                    itors[i] = null;
                }
            }
        }
        return null;
    }

    public E next() {
        if(!hasNext()) {
            throw new NoSuchElementException();
        }
        E seeked = _seeked;
        this._seeked = null;
        return seeked;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void close() throws IOException {
        final CloseableIterator<E>[] itors = _itors;
        final int numItors = _numItors;
        IOException ioe = null;
        for(int i = 0; i < numItors; i++) {
            final CloseableIterator itor = itors[i];
            if(itor != null) {
                try {
                    itor.close();
                } catch (IOException e) {
                    if(ioe != null) {//throws first IOE
                        ioe = e;
                    }
                }
            }
        }
        if(ioe != null) {
            throw ioe;
        }
    }

}
