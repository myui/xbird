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
package xbird.util.collections;

import java.util.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class ListDelegate<T> implements List<T> {

    protected final List<T> _delegate;

    public ListDelegate(List<T> delegate) {
        if(delegate == null) {
            throw new IllegalArgumentException();
        }
        this._delegate = delegate;
    }
    
    public final List<T> getList() {
        return _delegate;
    }

    public void add(int index, T element) {
        _delegate.add(index, element);
    }

    public boolean add(T e) {
        return _delegate.add(e);
    }

    public boolean addAll(Collection<? extends T> c) {
        return _delegate.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        return _delegate.addAll(index, c);
    }

    public void clear() {
        _delegate.clear();
    }

    public boolean contains(Object o) {
        return _delegate.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return _delegate.containsAll(c);
    }

    public T get(int index) {
        return _delegate.get(index);
    }

    public int indexOf(Object o) {
        return _delegate.indexOf(o);
    }

    public boolean isEmpty() {
        return _delegate.isEmpty();
    }

    public Iterator<T> iterator() {
        return _delegate.iterator();
    }

    public int lastIndexOf(Object o) {
        return _delegate.lastIndexOf(o);
    }

    public ListIterator<T> listIterator() {
        return _delegate.listIterator();
    }

    public ListIterator<T> listIterator(int index) {
        return _delegate.listIterator(index);
    }

    public T remove(int index) {
        return _delegate.remove(index);
    }

    public boolean remove(Object o) {
        return _delegate.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return _delegate.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return _delegate.retainAll(c);
    }

    public T set(int index, T element) {
        return _delegate.set(index, element);
    }

    public int size() {
        return _delegate.size();
    }

    public List<T> subList(int fromIndex, int toIndex) {
        return _delegate.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return _delegate.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return _delegate.toArray(a);
    }

    @Override
    public boolean equals(Object o) {
        return _delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return _delegate.hashCode();
    }

}
