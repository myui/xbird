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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class SortedLinkedList<E> extends LinkedList<E> implements SortedList<E> {
    private static final long serialVersionUID = 1622428598612863659L;

    private Comparator<E> cmp;
    private boolean duplicateAllowed = true;

    public SortedLinkedList() {
        this(new DefaultComparator<E>());
    }

    @SuppressWarnings("unchecked")
    public SortedLinkedList(boolean comparable) {
        this(comparable ? new ComparableComparator() : new DefaultComparator<E>());
    }

    public SortedLinkedList(Comparator<E> comparator) {
        super();
        this.cmp = comparator;
    }

    public boolean isDuplicateAllowed() {
        return duplicateAllowed;
    }

    public void allowDuplicate(boolean allow) {
        this.duplicateAllowed = allow;
    }

    public void setComparator(Comparator<E> cmp) {
        this.cmp = cmp;
    }

    @Override
    public boolean add(E e) {
        if(!duplicateAllowed) {
            return addDuplicateDisallowed(e);
        }
        int i = 0;
        final int size = size();
        for(; i < size; i++) {
            if(cmp.compare(get(i), e) < 0) {
                break;
            }
        }
        super.add(i, e);
        return true;
    }

    private boolean addDuplicateDisallowed(E e) {
        int i = 0;
        int lastCmp = -1;
        final int size = size();
        for(; i < size; i++) {
            lastCmp = cmp.compare(get(i), e);
            if(lastCmp < 0) {
                break;
            }
        }
        if(lastCmp == 0) {
            return false;
        }
        super.add(i, e);
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for(E e : c) {
            if(!add(e)) {
                return false;
            }
        }
        return true;
    }

    public E get(E probe) {
        final int idx = Collections.binarySearch(this, probe, cmp);
        if(idx >= 0) {
            return get(idx);
        }
        return null;
    }

    /**
     * An efficient version of {@link #contains(Object)}
     */
    public boolean contains2(E o) {
        return indexOf2(o) >= 0;
    }

    /**
     * An efficient version of {@link #indexOf(Object)}
     */
    public int indexOf2(E o) {
        final int idx = Collections.binarySearch(this, o, cmp);
        return idx < 0 ? -1 : idx;
    }

    // -----------------------------------------------
    // Unsupported Operations

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addFirst(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addLast(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offerFirst(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offerLast(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void push(E e) {
        throw new UnsupportedOperationException();
    }

}
