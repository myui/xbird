/*
 * @(#)$Id: CompositeSequence.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CompositeSequence<T extends Item> extends AbstractSequence<T> {
    private static final long serialVersionUID = 100110868711189545L;

    private final Sequence<? extends Item> _src;
    private final XQExpression _filterStepExpr;
    private final DynamicContext _dynEnv;

    public CompositeSequence(Sequence<? extends Item> src, XQExpression filterExpr, DynamicContext dynEnv) {
        super(dynEnv);
        if(src == null || filterExpr == null) {
            throw new IllegalArgumentException("src: " + src + ", filterExpr: " + filterExpr);
        }
        this._src = src;
        this._filterStepExpr = filterExpr;
        this._dynEnv = dynEnv;
    }

    @Override
    public CompositeFocus<T> iterator() {
        final CompositeFocus<T> focus = new CompositeFocus<T>(this, _src.iterator(), _dynEnv);
        return focus;
    }

    public boolean next(final IFocus focus) throws XQueryException {
        final CompositeFocus<T> cfocus = (CompositeFocus<T>) focus;
        Iterator<T> filterItor = cfocus.getFilterIterator();
        final Iterator<? extends Item> srcItor = cfocus.getBaseFocus();
        while(true) {
            if(filterItor != null && filterItor.hasNext()) { // false if filteredExpr is not evaled
                T n = filterItor.next();
                focus.setContextItem(n);
                return true;
            }
            if(!srcItor.hasNext()) { // src sequence has no more elements
                return false;
            }
            final Item nextit = srcItor.next();
            final Sequence<? extends Item> filtered;
            try {
                filtered = _filterStepExpr.eval(nextit, _dynEnv);
            } catch (XQueryException e) {
                throw new XQRTException(e.getErrCode(), "failed at path evaluation: "
                        + _filterStepExpr, e);
            }
            filterItor = (Iterator<T>) filtered.iterator();
            cfocus.setFilterIterator(filterItor);
        }
    }

    public Type getType() {
        return _filterStepExpr.getType();
    }

    private static final class CompositeFocus<T extends Item> extends Focus<T> {
        private static final long serialVersionUID = -4933948644036633051L;

        private Iterator<T> filterItor = null;

        public CompositeFocus(Sequence<T> src, Iterator<? extends Item> base, DynamicContext dynEnv) {
            super(src, base, dynEnv);
        }

        public Iterator<T> getFilterIterator() {
            return filterItor;
        }

        public void setFilterIterator(Iterator<T> filterItor) {
            this.filterItor = filterItor;
        }

    }
}