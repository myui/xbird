/*
 * @(#)$Id: SortedSequence.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.*;

import xbird.xquery.XQRTException;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.expr.flwr.OrderSpec;
import xbird.xquery.meta.*;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @see OrderSpec
 * @link http://www.w3.org/TR/xquery/#doc-xquery-OrderSpec
 * @link http://www.w3.org/TR/xquery-semantics/#id_orderby_clause
 */
public final class SortedSequence extends AbstractSequence implements ISorted {
    private static final long serialVersionUID = 4609114921086601562L;

    private final List<Comparable[]> _items;
    private final Sorter _sorter;
    private final List<OrderSpec> _orderSpecs;
    private final Type _type;

    public SortedSequence(Sequence src, List<OrderSpec> orderSpecs, Sequence contextSeq, DynamicContext dynEnv) {
        super(dynEnv);
        if(orderSpecs == null) {
            throw new IllegalArgumentException();
        }
        this._sorter = new Sorter(src, contextSeq);
        this._orderSpecs = orderSpecs;
        this._items = new ArrayList<Comparable[]>(128);
        this._type = src.getType();
    }

    public Sequence getSource() {
        return _sorter.src;
    }

    public boolean next(IFocus focus) throws XQueryException {
        final Iterator<? extends Item> itor = focus.getBaseFocus();
        if(itor.hasNext()) {
            Item it = itor.next();
            focus.setContextItem(it);
            return true;
        }
        return false;
    }

    @Override
    public Focus iterator() {
        try {
            _sorter.sort();
        } catch (XQueryException e) {
            throw new XQRTException(e);
        }
        KeyStrippingIterator itor = new KeyStrippingIterator(_items, _orderSpecs);
        return new Focus(this, itor, _dynEnv); // FIXME
    }

    @Override
    public boolean isEmpty() {
        try {
            _sorter.sort();
        } catch (XQueryException e) {
            throw new XQRTException(e);
        }
        return _items.isEmpty();
    }

    public Type getType() {
        return _type;
    }

    private final class Sorter implements Serializable {
        private static final long serialVersionUID = -228335655168929136L;

        private boolean done = false;
        private Sequence src;
        private Sequence contextSeq;

        public Sorter(Sequence src, Sequence contextSeq) {
            this.src = src;
            this.contextSeq = contextSeq;
        }

        private void sort() throws XQueryException {
            if(!done) {
                final List<Comparable[]> items = _items;
                final List<OrderSpec> oderSpecs = _orderSpecs;
                final DynamicContext dynEnv = _dynEnv;

                final int keylen = oderSpecs.size();
                final IFocus<? extends Item> itor = src.iterator();
                for(Item it : itor) {
                    final Comparable[] tuple = new Comparable[keylen + 1];
                    tuple[0] = it;
                    for(int i = 0; i < keylen; i++) {
                        final OrderSpec key = oderSpecs.get(i);
                        final Sequence<? extends Item> keyval = key.getKeyExpr().eval(contextSeq, dynEnv);
                        if(keyval.isEmpty()) {
                            tuple[i + 1] = null;
                        } else {
                            final Sequence<? extends Item> atomized = keyval.atomize(dynEnv);
                            final Item keyitem = SingleCollection.wrap(atomized, dynEnv);
                            if(keyitem instanceof AtomicValue) {
                                tuple[i + 1] = keyitem;
                            } else {//TODO typed value
                                tuple[i + 1] = keyitem.stringValue();
                            }
                        }
                    }
                    items.add(tuple);
                }
                itor.closeQuietly();
                Collections.sort(items, new KeyComparator(oderSpecs, dynEnv));
                clear();
                this.done = true;
            }
        }

        private void clear() {
            this.src = null;
            this.contextSeq = null;
        }
    }

    private static final class KeyComparator implements Comparator<Comparable[]> {

        private final List<OrderSpec> orderSpecs;
        private final DynamicContext dynEnv;

        public KeyComparator(List<OrderSpec> orderSpecs, DynamicContext dynEnv) {
            this.orderSpecs = orderSpecs;
            this.dynEnv = dynEnv;
        }

        public int compare(Comparable[] o1, Comparable[] o2) {
            final int keylen = orderSpecs.size();
            assert (o1.length == o2.length);
            for(int i = 0; i < keylen; i++) {
                final int cmp;
                final int keyidx = i + 1;
                if(o1[keyidx] instanceof Item) {
                    final Item it1 = (Item) o1[keyidx];
                    final Item it2 = (Item) o2[keyidx];
                    final OrderSpec spec = orderSpecs.get(i);
                    cmp = spec.compare(it1, it2, dynEnv);
                } else {
                    final OrderSpec spec = orderSpecs.get(i);
                    cmp = spec.compare(o1[keyidx], o2[keyidx]);
                }
                if(cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        }
    }

    private static final class KeyStrippingIterator implements Iterator<Item>, Serializable {
        private static final long serialVersionUID = -4343880330877004117L;

        private final List<Comparable[]> items;
        private final List<OrderSpec> orderSpecs;

        private transient Iterator<Comparable[]> delegate;

        public KeyStrippingIterator(List<Comparable[]> items, List<OrderSpec> orderSpecs) {
            this.items = items;
            this.orderSpecs = orderSpecs;
            this.delegate = items.iterator();
        }

        public boolean hasNext() {
            return delegate.hasNext();
        }

        public Item next() {
            final Comparable[] tuple = delegate.next();
            final Item it = (Item) tuple[0];
            final int keylen = orderSpecs.size();
            for(int i = 1; i <= keylen; i++) {
                tuple[i] = null; // hack: save memory space
            }
            return it;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private Object readResolve() throws ObjectStreamException {
            if(items == null) {
                throw new IllegalStateException("_items was null");
            }
            this.delegate = items.iterator();
            return this;
        }

    }

}
