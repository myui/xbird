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
package xbird.xquery.dm.value.sequence;

import xbird.util.iterator.ChainedIterator;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AbstractSequence;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.Focus;
import xbird.xquery.meta.IFocus;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ChainedSequence extends AbstractSequence<Item> {
    private static final long serialVersionUID = -8420239647824043988L;

    private final Sequence firstItem;
    private final Sequence secItem;

    public ChainedSequence(Sequence firstItem, Sequence secItem, DynamicContext dynEnv) {
        super(dynEnv);
        this.firstItem = firstItem;
        this.secItem = secItem;
    }

    @Override
    public IFocus<Item> iterator() {
        IFocus firstItor = firstItem.iterator();
        IFocus secItor = secItem.iterator();
        ChainedIterator<Item> pipedItor = new ChainedIterator<Item>(firstItor, secItor);
        return new Focus<Item>(this, pipedItor, _dynEnv);
    }

    @SuppressWarnings("unchecked")
    public boolean next(IFocus<Item> focus) throws XQueryException {
        ChainedIterator<Item> itor = (ChainedIterator<Item>) focus.getBaseFocus();
        while(itor.hasNext()) {
            Item it = itor.next();
            focus.setContextItem(it);
            return true;
        }
        focus.setReachedEnd(true);
        IFocus firstItor = (IFocus) itor.getFirstIterator();
        firstItor.closeQuietly();
        IFocus secItor = (IFocus) itor.getSecondIterator();
        secItor.closeQuietly();
        return false;
    }

    public Type getType() {
        return SequenceType.ANY_ITEMS;
    }

}