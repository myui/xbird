/*
 * @(#)$Id: NodeSequence.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.Serializable;
import java.util.*;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.dtm.LazyDTMDocument;
import xbird.xquery.dm.value.*;
import xbird.xquery.meta.*;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class NodeSequence<E extends XQNode> extends AbstractSequence<E>
        implements INodeSequence<E>, IRandomAccessSequence<E> {
    private static final long serialVersionUID = -5188045070228658246L;
    private static final INodeSequence<XQNode> EMPTY = new NodeSequence<XQNode>(Collections.<XQNode> emptyList(), DynamicContext.DUMMY);

    private final List<E> src; // must be serializable

    public NodeSequence(DynamicContext dynEnv) {
        this(new ArrayList<E>(16), dynEnv);
    }

    public NodeSequence(List<? extends E> src, DynamicContext dynEnv) {
        super(dynEnv);
        if(!(src instanceof Serializable)) {
            throw new IllegalArgumentException("src list must be Serializable: "
                    + src.getClass().getName());
        }
        this.src = (List<E>) src;
    }

    public void addItem(E i) {
        src.add(i);
    }

    public E getLastItem() {
        final int len = src.size();
        if(len > 0) {
            return src.get(len - 1);
        }
        return null;
    }

    public boolean next(IFocus<E> focus) throws XQueryException {
        final int pos = focus.getContextPosition();
        if(pos < src.size()) {
            ReclaimingFocus rfocus = (ReclaimingFocus) focus;
            LazyDTMDocument toReclaim = rfocus.pollReclaim();
            if(toReclaim != null) {
                toReclaim.reclaim();
            }
            E item = src.get(pos);
            if(item instanceof LazyDTMDocument) {
                LazyDTMDocument lazydoc = (LazyDTMDocument) item;
                lazydoc.preload();
                rfocus.setToReclaim(lazydoc);
            }
            rfocus.setContextItem(item);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final ReclaimingFocus iterator() {
        return new ReclaimingFocus(this, _dynEnv);
    }

    public Type getType() {
        return TypeRegistry.safeGet("node()*");
    }

    @Override
    public boolean isEmpty() {
        return src.isEmpty();
    }

    public static final <T extends XQNode> INodeSequence<T> emptySequence() {
        return (NodeSequence<T>) EMPTY;
    }

    //--------------------------------------------
    // IRandomAccessSequence interface implimentation

    public E getItem(int index) {
        return src.get(index);
    }

    public Collection<E> getItems() {
        return src;
    }

    public Sequence subSequence(int fromIndex, int toIndex) {
        final List<E> sub = src.subList(fromIndex, toIndex);
        return new NodeSequence<E>(sub, _dynEnv);
    }

    public int size() {
        return src.size();
    }

    private final class ReclaimingFocus extends Focus<E> {
        private static final long serialVersionUID = -7018502338061029303L;

        private LazyDTMDocument toReclaim = null;

        public ReclaimingFocus(final Sequence<E> src, final DynamicContext dynEnv) {
            super(src, dynEnv);
        }

        public LazyDTMDocument pollReclaim() {
            if(toReclaim == null) {
                return null;
            }
            final LazyDTMDocument ret = toReclaim;
            this.toReclaim = null;
            return ret;
        }

        public void setToReclaim(final LazyDTMDocument toReclaim) {
            this.toReclaim = toReclaim;
        }

    }
}
