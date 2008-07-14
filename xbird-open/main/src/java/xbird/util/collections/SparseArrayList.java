/*
 * @(#)$Id: SparseArrayList.java 3619 2008-03-26 07:23:03Z yui $
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

/**
 * Sparse ArrayList.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link ftp://ftp.glenmccl.com/pub/free/jperf.pdf
 */
public class SparseArrayList<E> extends AbstractList<E> {

    private static final int PAGE_SIZE = 512;

    private E[][] _pages;
    private int _size = 0;

    public SparseArrayList() {
        this(0);
    }

    public SparseArrayList(int times) {
        assert (times >= 0);
        this._pages = (E[][]) new Object[times][];
    }

    @Override
    public void add(int index, E element) {
        if(index < 0 || index > size()) {
            throw new IllegalArgumentException();
        }
        int p = index / PAGE_SIZE;
        if(p >= _pages.length) { // expand pages
            E[][] newpages = (E[][]) new Object[p + 1][];
            System.arraycopy(_pages, 0, newpages, 0, _pages.length);
            this._pages = newpages;
        }
        if(_pages[p] == null) { // allocate new page
            _pages[p] = (E[]) new Object[PAGE_SIZE];
        }
        int pindex = index % PAGE_SIZE;
        _pages[p][pindex] = element;
        // FIXME shift following elements
        ++_size;
    }

    @Override
    public void clear() {
        for(int i = 0; i < _pages.length; i++) {
            _pages[i] = null;
        }
        this._size = 0;
    }

    /**
     * Get the value of a given array slot, null if none. 
     */
    public E get(int index) {
        if(index < 0) {
            throw new IllegalArgumentException();
        }
        int p = index / PAGE_SIZE;
        if(p >= _pages.length || _pages[p] == null) {
            return null;
        }
        return _pages[p][index % PAGE_SIZE];
    }

    @Override
    public E remove(int index) {
        int p = index / PAGE_SIZE;
        if(p >= _pages.length || _pages[p] == null) {
            return null;
        }
        E removed = _pages[p][index % PAGE_SIZE];
        _pages[p][index % PAGE_SIZE] = null;
        return removed;
    }

    /**
     * Set an array slot to given value.
     */
    @Override
    public E set(int index, E element) {
        if(index < 0 || index >= size()) {
            throw new IllegalArgumentException();
        }
        final int p = index / PAGE_SIZE;
        assert (p < _pages.length);
        if(_pages[p] == null) { // allocate new page
            _pages[p] = (E[]) new Object[PAGE_SIZE];
        }
        E old = _pages[p][index % PAGE_SIZE];
        _pages[p][index % PAGE_SIZE] = element;
        return old;
    }

    public int size() {
        return _size;
    }

    /**
     * Returns total size of allocated pages.
     */
    public int totalSize() {
        return _pages.length * PAGE_SIZE;
    }

}
