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
import java.util.ArrayList;
import java.util.List;

import xbird.xquery.dm.value.Item;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CachedFocus<T extends Item> extends AbstractFocus<T> implements Cloneable {

    private IFocus<T> delegate;
    private final List<T> queue;
    private int index = 0;

    private boolean reachedEnd = false;
    private int lastIndex = -1;

    public CachedFocus(IFocus<T> delegate) {
        super();
        this.delegate = delegate;
        this.queue = new ArrayList<T>(12);
    }

    protected CachedFocus(IFocus<T> delegate, List<T> queue) {
        super();
        this.delegate = delegate;
        this.queue = queue;
    }

    public boolean hasNext() {
        if(reachedEnd & (index >= lastIndex)) {
            return false;
        }
        if(index < queue.size()) {
            return true;
        } else {
            boolean hasNext = delegate.hasNext();
            if(!hasNext) {
                this.reachedEnd = true;
                this.lastIndex = index;
            }
            return hasNext;
        }
    }

    public T next() {
        if(reachedEnd & (index >= lastIndex)) {
            return null;
        }
        List<T> q = queue;
        if(index < q.size()) {
            return q.get(index++);
        }
        T nextval = delegate.next();
        if(nextval == null) {
            this.reachedEnd = true;
            this.lastIndex = index;
            return null;
        }
        q.add(nextval);
        ++index;
        return nextval;
    }

    @Override
    public CachedFocus clone() {
        CachedFocus<T> focus = new CachedFocus<T>(delegate, queue);
        focus.reachedEnd = this.reachedEnd;
        focus.lastIndex = this.lastIndex;
        return focus;
    }

    public final void close() throws IOException {
        delegate.close();
    }
}
