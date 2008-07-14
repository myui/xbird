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

import xbird.util.io.IOUtils;
import xbird.util.iterator.MultiplexingIterator;
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
public final class PipelinedSequence extends AbstractSequence {
    private static final long serialVersionUID = -4626518122143252247L;

    private final Sequence[] _sequences;

    public PipelinedSequence(DynamicContext dynEnv, Sequence... seqs) {
        super(dynEnv);
        if(seqs == null) {
            throw new IllegalArgumentException();
        }
        this._sequences = seqs;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IFocus iterator() {
        final int seqlen = _sequences.length;
        final IFocus[] focuz = new IFocus[seqlen];
        for(int i = 0; i < seqlen; i++) {
            focuz[i] = _sequences[i].iterator();
        }
        MultiplexingIterator<Item> itor = new MultiplexingIterator<Item>(focuz);
        return new Focus<Item>(this, itor, _dynEnv);
    }

    @SuppressWarnings("unchecked")
    public boolean next(IFocus focus) throws XQueryException {
        final MultiplexingIterator<Item> itor = (MultiplexingIterator<Item>) focus.getBaseFocus();
        while(itor.hasNext()) {
            Item it = itor.next();
            focus.setContextItem(it);
            return true;
        }
        focus.setReachedEnd(true);
        IOUtils.closeQuietly(itor);
        return false;
    }

    public Type getType() {
        return SequenceType.ANY_ITEMS;
    }

}
