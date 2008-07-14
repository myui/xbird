/*
 * @(#)$Id: AtomizedSequence.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.XQRTException;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#dt-atomization
 */
public final class AtomizedSequence extends ProxySequence<AtomicValue>
        implements IAtomized<AtomicValue> {
    private static final long serialVersionUID = 804755669442210560L;

    public AtomizedSequence(final Sequence delegate, final DynamicContext dynEnv) {
        super(delegate, dynEnv);
    }

    public boolean next(IFocus focus) throws XQueryException {
        final Iterator<? extends Item> delItor = focus.getBaseFocus();
        final boolean hasNext = delItor.hasNext();
        if(hasNext) {
            final Item delItem = delItor.next();
            final AtomicValue v = atomize(delItem);
            focus.setContextItem(v);
            return true;
        }
        return false;
    }

    private static AtomicValue atomize(final Item it) {
        if(it instanceof AtomicValue) {
            return (AtomicValue) it;
        } else if(it instanceof XQNode) {
            final AtomicValue tv = ((XQNode) it).typedValue();
            if(tv == null) {
                throw new XQRTException("err:FOTY0012", "Atomization failed with a node has no typed value.");
            }
            return tv;
        } else {
            throw new XQRTException("err:XPTY0004", "Atomization failed for type `" + it.getType()
                    + '`');
        }
    }

    public static <T extends AtomicValue> IAtomized<T> wrap(Sequence src, DynamicContext dynEnv) {
        if(src instanceof IAtomized) {
            return (IAtomized<T>) src;
        } else {
            return (IAtomized<T>) new AtomizedSequence(src, dynEnv);
        }
    }

}
