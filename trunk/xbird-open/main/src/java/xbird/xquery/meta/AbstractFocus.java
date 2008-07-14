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
package xbird.xquery.meta;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import xbird.xquery.dm.value.Item;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class AbstractFocus<T extends Item> implements IFocus<T> {

    private T citem = null;
    private int cpos = 0;

    private int pos = 0;
    private int last = -1;
    private boolean reachedEnd = false;

    private LinkedList<T> pendings = null;

    public AbstractFocus() {}

    public <E extends Item> Iterator<E> getBaseFocus() {
        return null;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------

    public T setContextItem(T item) {
        return citem;
    }

    public T getContextItem() {
        return citem;
    }

    public void setContextPosition(int pos) {
        this.cpos = pos;
    }

    public int getContextPosition() {
        return cpos;
    }

    // ------------------------------------------------------

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public int getPosition() {
        return pos;
    }

    public int incrPosition() {
        return ++pos;
    }

    public boolean reachedEnd() {
        return reachedEnd;
    }

    public void setReachedEnd(boolean end) {
        this.reachedEnd = end;
    }

    public void offerItem(T item) {
        synchronized(this) {
            if(pendings == null) {
                pendings = new LinkedList<T>();
            }
        }
        pendings.offer(item);
    }

    public T pollItem() {
        if(pendings == null) {
            return null;
        }
        return pendings.poll();
    }

    public final IFocus<T> iterator() {
        return this;
    }

    public final void closeQuietly() {
        try {
            close();
        } catch (IOException e) {
            ;
        }
    }

}
