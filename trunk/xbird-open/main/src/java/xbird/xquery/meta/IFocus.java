/*
 * @(#)$Id: IFocus.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.util.iterator.CloseableIterator;
import xbird.xquery.dm.value.Item;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public interface IFocus<E extends Item> extends CloseableIterator<E>, Iterable<E> {

    public <T extends Item> Iterator<T> getBaseFocus();

    public E getContextItem();

    public E setContextItem(E item);

    public int getContextPosition();

    public void setContextPosition(final int pos);

    public void offerItem(E item);

    public E pollItem();

    public boolean reachedEnd();

    public void setReachedEnd(final boolean end);

    public int getPosition();

    public int incrPosition();

    public int getLast();

    public void setLast(final int last);

    public IFocus<E> iterator();

    public void closeQuietly();
}