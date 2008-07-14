/*
 * @(#)$Id: IndexOf.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.seq;

import java.io.ObjectStreamException;
import java.text.Collator;
import java.util.Iterator;

import xbird.util.resource.CollationUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.ProxySequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.type.*;
import xbird.xquery.type.xs.StringType;

/**
 * fn:index-of.
 * <DIV lang="en">
 * Returns a sequence of positive integers giving the positions 
 * within the sequence $seqParam of items that are equal to $srchParam.
 * <ul>
 * <li>fn:index-of($seqParam as xdt:anyAtomicType*, $srchParam as xdt:anyAtomicType) as xs:integer*</li>
 * <li>fn:index-of($seqParam as xdt:anyAtomicType*, $srchParam as xdt:anyAtomicType, $collation as xs:string) as xs:integer*</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-index-of
 */
public final class IndexOf extends BuiltInFunction {
    private static final long serialVersionUID = 1498600419388016663L;
    public static final String SYMBOL = "fn:index-of";

    public IndexOf() {
        super(SYMBOL, TypeRegistry.safeGet("xs:integer*"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        final Type anyatom = TypeRegistry.safeGet("xs:anyAtomicType*");
        s[0] = new FunctionSignature(getName(), new Type[] { anyatom, AtomicType.ANY_ATOMIC_TYPE });
        s[1] = new FunctionSignature(getName(), new Type[] { anyatom, AtomicType.ANY_ATOMIC_TYPE,
                StringType.STRING });
        return s;
    }

    /**
     * Note:
     *  The first item in a sequence is at position 1, not position 0.
     *  The result sequence is in ascending numeric order.
     */
    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence<? extends Item> src = argv.getItem(0).atomize(dynEnv);
        final Item target = argv.getItem(1);
        final AtomicValue searchItem = (AtomicValue) target;
        final Item coll = (argv.size() == 3) ? argv.getItem(2) : null;
        return new IndexOfEmuration(src, searchItem, (coll != null) ? coll.stringValue() : null, dynEnv);
    }

    private static final class IndexOfEmuration extends ProxySequence {
        private static final long serialVersionUID = 3824666835912342600L;

        private final AtomicValue _searchItem;
        private final String _collation;
        /** The collation is used when string comparison is required. */
        private transient Collator _collator = null;
        private String _searchItemStr;

        public IndexOfEmuration(Sequence<? extends Item> src, AtomicValue searchItem, String collation, DynamicContext dynEnv) {
            super(src, dynEnv);
            assert (searchItem != null);
            this._searchItem = searchItem;
            this._collation = collation;
            if(TypeUtil.subtypeOf(searchItem.getType(), StringType.STRING)) {
                this._searchItemStr = searchItem.stringValue();
                if(collation != null) {
                    this._collator = CollationUtils.resolve(collation, dynEnv.getStaticContext());
                }
            }
        }

        public boolean next(IFocus focus) throws XQueryException {
            Iterator<? extends Item> srcItor = focus.getBaseFocus();
            while(srcItor.hasNext()) {
                Item srcItem = srcItor.next();
                assert (srcItem != null);
                if(found(srcItem)) {
                    focus.setContextItem(srcItem);
                    return true;
                }
            }
            return false;
        }

        private boolean found(Item srcItem) {
            final int cmp;
            // TODO when string comparison is required?
            if(_searchItemStr != null && _collator != null) {
                cmp = _collator.compare(srcItem, _searchItemStr);
            } else {
                cmp = srcItem.compareTo(_searchItem);
            }
            return cmp == 0;
        }

        @Override
        public Type getType() {
            return TypeRegistry.safeGet("xs:integer*");
        }

        private Object readResolve() throws ObjectStreamException {
            if(_collator == null && _collation != null) {
                assert (_dynEnv != null);
                this._collator = CollationUtils.resolve(_collation, _dynEnv.getStaticContext());
            }
            return this;
        }

    }

}
