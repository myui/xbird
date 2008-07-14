/*
 * @(#)$Id: SingleCollection.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.type.Type;

/**
 * Wraps a Sequence within an Item.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SingleCollection extends AbstractSequence<Item> implements Item {
    private static final long serialVersionUID = -4844926367486145853L;

    private final Sequence<? extends Item> src;

    public SingleCollection(Sequence<? extends Item> src, DynamicContext dynEnv) {
        super(dynEnv);
        assert (src != null);
        this.src = src;
    }

    public Sequence<? extends Item> getSource() {
        return src;
    }

    @Override
    public boolean isEmpty() {
        return src.isEmpty();
    }

    @Override
    public Sequence<? extends Item> atomize(DynamicContext dynEnv) {
        return (Sequence<? extends Item>) AtomizedSequence.wrap(src, dynEnv);
    }

    public Type getType() {
        return src.getType();
    }

    public String stringValue() {
        final StringBuilder buf = new StringBuilder();
        boolean first = true;
        final IFocus<? extends Item> itor = src.iterator();
        for(Item it : itor) {
            if(first) {
                first = false;
            } else {
                buf.append(' ');
            }
            buf.append(it.stringValue());
        }
        itor.closeQuietly();
        return buf.toString();
    }

    public boolean next(IFocus focus) throws XQueryException {
        final Iterator<Item> itor = focus.getBaseFocus();
        if(itor.hasNext()) {
            Item it = itor.next();
            focus.setContextItem(it);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public IFocus iterator() {
        return src.iterator();
    }

    public int compareTo(Item trg) {
        return compareTo(trg, false);
    }

    public int compareTo(Item trg, boolean emptyGreatest) {
        int cmp = 0;
        final IFocus<Item> thisItor = iterator();
        final IFocus<Item> trgItor = trg.iterator();
        for(Item thisItem : thisItor) {
            if(trgItor.hasNext()) {
                final Item trgItem = trgItor.next();
                cmp = thisItem.compareTo(trgItem);
                if(cmp != 0) {
                    thisItor.closeQuietly();
                    trgItor.closeQuietly();
                    return cmp;
                }
            } else {
                thisItor.closeQuietly();
                trgItor.closeQuietly();
                return emptyGreatest ? -1 : 1;
            }
        }
        thisItor.closeQuietly();
        trgItor.closeQuietly();
        cmp = trg.isEmpty() ? (this.isEmpty() ? 0 : (emptyGreatest ? -1 : 1)) : cmp;
        return cmp;
    }

    public static Item wrap(Sequence<? extends Item> src, DynamicContext dynEnv) {
        assert (src != null);
        if(src instanceof Item) {
            return (Item) src;
        } else {
            return new SingleCollection(src, dynEnv);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Item) {
            final int cmp = compareTo((Item) obj, false);
            return cmp == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return src.hashCode();
    }

    public boolean deepEquals(final SingleCollection trg) {
        return src == trg.src;
    }

}
