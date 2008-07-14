/*
 * @(#)$Id: Sequence.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value;

import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.meta.*;

/**
 * Sequence is the base value representation in XQuery, and
 * our <code>Sequence</code> interface yields Iterator.
 * <DIV lang="en">
 * Note that <code>Sequence</code> does'nt hold states.
 * States are held in the <code>Focus</code>.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @see Focus
 */
public interface Sequence<E extends Item> extends Value, Iterable<E> {

    /** 
     * @see Focus#setContextItem(Item)
     * @see Focus#next()
     */
    public boolean next(IFocus<E> focus) throws XQueryException;

    @Deprecated
    public boolean isEmpty();

    public Sequence<? extends Item> atomize(DynamicContext dynEnv);

    public IFocus<E> iterator();

    public List<E> materialize();

}