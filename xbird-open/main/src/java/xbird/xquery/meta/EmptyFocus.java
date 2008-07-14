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
package xbird.xquery.meta;

import java.util.Iterator;

import xbird.util.iterator.EmptyIterator;
import xbird.xquery.dm.value.Item;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class EmptyFocus<E extends Item> implements IFocus<E> {

    public static final EmptyFocus EMPTY = new EmptyFocus();

    private EmptyFocus() {}

    public boolean reachedEnd() {
        return true;
    }

    public boolean hasNext() {
        return false;
    }

    public IFocus<E> iterator() {
        return getInstance();
    }

    public Iterator<Item> getBaseFocus() {
        return EmptyIterator.emptyIterator();
    }

    public void close() {}

    public void closeQuietly() {}

    public E getContextItem() {
        throw new UnsupportedOperationException();
    }

    public int getContextPosition() {
        throw new UnsupportedOperationException();
    }

    public int getLast() {
        throw new UnsupportedOperationException();
    }

    public int getPosition() {
        throw new UnsupportedOperationException();
    }

    public int incrPosition() {
        throw new UnsupportedOperationException();
    }

    public void offerItem(Item item) {
        throw new UnsupportedOperationException();
    }

    public E pollItem() {
        throw new UnsupportedOperationException();
    }

    public E next() {
        throw new UnsupportedOperationException();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public E setContextItem(E item) {
        throw new UnsupportedOperationException();
    }

    public void setContextPosition(int pos) {
        throw new UnsupportedOperationException();
    }

    public void setLast(int last) {
        throw new UnsupportedOperationException();
    }

    public void setReachedEnd(boolean end) {
        throw new UnsupportedOperationException();
    }

    public static <E extends Item> EmptyFocus<E> getInstance() {
        return EMPTY;
    }
}
