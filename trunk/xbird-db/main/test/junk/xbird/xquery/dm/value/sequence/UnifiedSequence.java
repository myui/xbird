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
package xbird.xquery.dm.value.sequence;

import java.util.Iterator;

import xbird.util.iterator.EmptyIterator;
import xbird.util.iterator.PipedIterator;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.meta.*;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class UnifiedSequence extends AbstractSequence {
    private static final long serialVersionUID = 6811935154925106134L;

    private final Sequence[] _seqs;
    private final Type _type;

    public UnifiedSequence(DynamicContext dynEnv, Sequence... seqs) {
        super(dynEnv);
        this._seqs = seqs;
        this._type = (seqs.length == 0) ? SequenceType.EMPTY : seqs[0].getType();
    }

    @Override
    public IFocus iterator() {
        final int seqlen = _seqs.length;
        Iterator<Item> itor = EmptyIterator.emptyIterator();
        for(int i = 0; i < seqlen; i++) {
            IFocus<Item> sec = _seqs[i].iterator();
            itor = new PipedIterator<Item>(itor, sec);
        }
        return new Focus<Item>(this, itor, _dynEnv);
    }

    public boolean next(IFocus focus) throws XQueryException {
        final Iterator<Item> itor = focus.getBaseFocus();
        while(itor.hasNext()) {
            Item it = itor.next();
            focus.setContextItem(it);
            return true;
        }
        focus.setReachedEnd(true);
        return false;
    }

    public Type getType() {
        return _type;
    }

}
