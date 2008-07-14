/*
 * @(#)$Id: DistinctSequence.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.*;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.meta.*;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DistinctSequence<T extends Item> extends AbstractSequence<T>
        implements IMaterializedSequence<T> {
    private static final long serialVersionUID = 2360687137077065466L;

    private boolean reachedEnd = false;
    private Sequence<? extends T> delegate;
    private final Type type;
    private final Set<T> uniq = new LinkedHashSet<T>(32);

    public DistinctSequence(Sequence<? extends T> delegate, DynamicContext dynEnv) {
        super(dynEnv);
        this.delegate = delegate;
        this.type = delegate.getType();
    }

    public Collection<T> getItems() {
        if(!reachedEnd) {
            final IFocus<? extends T> itor = delegate.iterator();
            for(T it : itor) {
                uniq.add(it);
            }
            itor.closeQuietly();
            this.reachedEnd = true;
        }
        return uniq;
    }

    //--------------------------------------------
    // Sequence interface implimentation

    public boolean next(IFocus focus) throws XQueryException {
        final Iterator<T> delItor = focus.getBaseFocus();
        while(delItor.hasNext()) {
            T n = delItor.next();
            if(reachedEnd) {
                focus.setContextItem(n);
                return true;
            } else {
                boolean hasNot = uniq.add(n);
                if(hasNot) {
                    focus.setContextItem(n);
                    return true;
                }
            }
        }
        this.reachedEnd = true;
        this.delegate = null;
        return false;
    }

    @Override
    public IFocus<T> iterator() {
        if(reachedEnd) {
            return new Focus<T>(this, uniq.iterator(), _dynEnv);
        }
        return new Focus<T>(this, delegate.iterator(), _dynEnv);
    }

    @Override
    public boolean isEmpty() {
        if(reachedEnd) {
            return uniq.isEmpty();
        }
        return super.isEmpty();
    }

    public Type getType() {
        return type;
    }

    //--------------------------------------------
    // helpers

    public static <T extends Item> DistinctSequence<T> wrap(Sequence src, DynamicContext dynEnv) {
        if(src instanceof DistinctSequence) {
            return (DistinctSequence<T>) src;
        } else {
            return new DistinctSequence<T>(src, dynEnv);
        }
    }

}
