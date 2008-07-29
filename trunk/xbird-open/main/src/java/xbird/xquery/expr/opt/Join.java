/*
 * @(#)$Id: Join.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.opt;

import static xbird.xquery.expr.comp.GeneralComp.Operator.EQ;
import static xbird.xquery.expr.comp.GeneralComp.Operator.GE;
import static xbird.xquery.expr.comp.GeneralComp.Operator.GT;
import static xbird.xquery.expr.comp.GeneralComp.Operator.LE;
import static xbird.xquery.expr.comp.GeneralComp.Operator.LT;
import static xbird.xquery.expr.comp.GeneralComp.Operator.NE;
import static xbird.xquery.expr.comp.NodeComp.Operator.FOLLOWS;
import static xbird.xquery.expr.comp.NodeComp.Operator.IS;
import static xbird.xquery.expr.comp.NodeComp.Operator.PRECEDES;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import xbird.util.iterator.NestedIterator;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AbstractSequence;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.SingleCollection;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.comp.ComparisonOp;
import xbird.xquery.expr.comp.GeneralComp;
import xbird.xquery.expr.comp.NodeComp;
import xbird.xquery.expr.flwr.Binding;
import xbird.xquery.expr.flwr.FLWRExpr;
import xbird.xquery.expr.flwr.ForClause;
import xbird.xquery.expr.flwr.LetClause;
import xbird.xquery.expr.path.FilterExpr;
import xbird.xquery.expr.path.FilterExpr.ConditionalFilter;
import xbird.xquery.expr.path.PathExpr.CompositePath;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.expr.var.VarRef;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.expr.var.BindingVariable.AnonymousLetVariable;
import xbird.xquery.expr.var.BindingVariable.LetVariable;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.Focus;
import xbird.xquery.meta.IFocus;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.parser.visitor.AbstractXQueryParserVisitor;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.Type;
import xbird.xquery.type.node.NodeType;
import xbird.xquery.type.xs.StringType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class Join {

    private static final boolean ENV_DISABLE_JOIN_THREADING = System.getProperty("xbird.optim.enable_join_threading") == null;

    private Join() {}

    public static final class JoinDetector extends AbstractXQueryParserVisitor {

        private List<Binding> _currentBindings;
        private ForClause _currentForClause;
        private PathVariable _lastPathVariable;

        public JoinDetector() {
            super();
        }

        @Override
        public XQExpression visit(FLWRExpr flwr, XQueryContext ctxt) throws XQueryException {
            this._currentBindings = flwr.getClauses();
            super.visit(flwr, ctxt);
            this._currentBindings = null;
            return flwr;
        }

        @Override
        public ForClause visit(ForClause clause, XQueryContext ctxt) throws XQueryException {
            this._currentForClause = clause;
            super.visit(clause, ctxt);
            this._currentForClause = null;
            return clause;
        }

        @Override
        public XQExpression visit(PathVariable variable, XQueryContext ctxt) throws XQueryException {
            this._lastPathVariable = variable;
            super.visit(variable, ctxt);
            this._lastPathVariable = null;
            return variable;
        }

        @Override
        public XQExpression visit(CompositePath fragment, XQueryContext ctxt)
                throws XQueryException {
            PathVariable prevVar = _lastPathVariable;
            Variable src = fragment.getSourceVariable();
            src.visit(this, ctxt);
            this._lastPathVariable = prevVar;
            XQExpression filter = fragment.getFilterExpr();
            filter.visit(this, ctxt);
            return fragment;
        }

        @Override
        public XQExpression visit(FilterExpr filterExpr, XQueryContext ctxt) throws XQueryException {
            boolean isFirst = true;
            int shipId = -1;
            List<XQExpression> srcFilter = Collections.<XQExpression> emptyList();
            List<XQExpression> persistKeyExprs = Collections.<XQExpression> emptyList();
            List<XQExpression> searchKeyExprs = Collections.<XQExpression> emptyList();
            final List<XQExpression> predicates = filterExpr.getPredicates();
            String opr = null;
            for(XQExpression p : predicates) {
                if(p instanceof ConditionalFilter) {
                    XQExpression pred = ((ConditionalFilter) p).getPredicate();
                    if((pred instanceof GeneralComp) || (pred instanceof NodeComp)) {
                        ComparisonOp cmpOp = (ComparisonOp) pred;
                        String newOpr = cmpOp.getOperator();
                        if(opr != null && opr != newOpr) {
                            break;
                        }
                        opr = newOpr;
                        XQExpression left = cmpOp.getLeftOperand();
                        if(left instanceof VarRef) {
                            left = ((VarRef) left).getValue();
                        }
                        if(left instanceof PathVariable) {
                            left = ((PathVariable) left).getValue();
                        }
                        XQExpression right = cmpOp.getRightOperand();
                        if(right instanceof VarRef) {
                            right = ((VarRef) right).getValue();
                        }
                        if(right instanceof PathVariable) {
                            right = ((PathVariable) right).getValue();
                        }
                        XQExpression leftSrc = left, rightSrc = right;
                        while(leftSrc instanceof CompositePath) {
                            leftSrc = ((CompositePath) leftSrc).getSourceExpr();
                        }
                        while(rightSrc instanceof CompositePath) {
                            rightSrc = ((CompositePath) rightSrc).getSourceExpr();
                        }
                        VarRef pathSrc = null;
                        XQExpression persistKeyExpr = null, searchKeyExpr = null;
                        if(leftSrc instanceof VarRef && !(rightSrc instanceof VarRef)) {
                            pathSrc = (VarRef) leftSrc;
                            persistKeyExpr = right;
                            searchKeyExpr = left;
                        } else if(rightSrc instanceof VarRef && !(leftSrc instanceof VarRef)) {
                            pathSrc = (VarRef) rightSrc;
                            persistKeyExpr = left;
                            searchKeyExpr = right;
                        }
                        if(pathSrc != null) {
                            if(isFirst) {
                                persistKeyExprs = new ArrayList<XQExpression>(4);
                                searchKeyExprs = new ArrayList<XQExpression>(4);
                            }
                            Variable pv = pathSrc.getValue();
                            if(pv instanceof BindingVariable) {
                                if(searchKeyExpr instanceof CompositePath
                                        && _currentForClause != null) {
                                    assert (_currentBindings != null);
                                    final LetVariable lv = new AnonymousLetVariable(searchKeyExpr);
                                    lv.incrementReferenceCount();
                                    final int bsize = _currentBindings.size();
                                    assert (bsize > 0) : bsize;
                                    boolean found = false;
                                    for(int i = 0; i < bsize; i++) {
                                        Binding binding = _currentBindings.get(i);
                                        if(binding == _currentForClause) {
                                            LetClause lc = new LetClause(lv);
                                            _currentBindings.add(i, lc);
                                            found = true;
                                            break;
                                        }
                                    }
                                    assert (found);
                                    searchKeyExpr = lv;
                                }
                                persistKeyExprs.add(persistKeyExpr);
                                searchKeyExprs.add(searchKeyExpr);
                                isFirst = false;
                                continue;
                            } else if(pv instanceof ShippedVariable) {
                                shipId = ((ShippedVariable) pv).getIdentifier();
                                persistKeyExprs.add(persistKeyExpr);
                                searchKeyExprs.add(searchKeyExpr);
                                isFirst = false;
                                continue;
                            }
                        }
                        if(isFirst) {
                            if(srcFilter == Collections.EMPTY_LIST) {
                                srcFilter = new ArrayList<XQExpression>(4);
                            }
                            srcFilter.add(p);
                        } else {
                            break;
                        }
                    }
                }
            }
            if(!isFirst && _lastPathVariable != null) { // found join
                XQExpression pvExpr = _lastPathVariable.getValue();
                final XQExpression srcExpr;
                if(pvExpr instanceof CompositePath) {
                    CompositePath srcPath = (CompositePath) pvExpr;
                    XQExpression filterBaseExpr = filterExpr.getSourceExpr();
                    srcPath.setFilterExpr(filterBaseExpr);
                    srcExpr = srcPath;
                } else if(pvExpr instanceof FilterExpr) {//FIXME REVIEWME
                    srcExpr = filterExpr.getSourceExpr();
                } else {
                    throw new IllegalStateException("Unexpected expression is binded to last PathVariable "
                            + _lastPathVariable + ": " + pvExpr);
                }
                PromoteJoinExpression promoteJoinExpr = new PromoteJoinExpression(srcExpr, srcFilter, persistKeyExprs, opr, searchKeyExprs);
                promoteJoinExpr._shippedId = shipId;
                _lastPathVariable.setValue(promoteJoinExpr);
                _lastPathVariable = null;
            }
            return filterExpr;
        }
    }

    public static final class PromoteJoinExpression extends AbstractXQExpression {
        private static final long serialVersionUID = 2994212583454668688L;
        // FIXME remove the use of WeakHashMap
        private static transient final Map<Integer, JoinTable> _tables = new WeakHashMap<Integer, JoinTable>(4);

        private XQExpression _srcExpr;
        private final List<XQExpression> _srcFilters;
        private final List<XQExpression> _pkeyExprs;
        private final List<Type> _keyTypes;
        private final String _opr;
        private final List<XQExpression> _skeyExprs;
        private JoinTable _joinTable = null;
        private final boolean _isNodeComp;

        private int _shippedId = -1;

        public PromoteJoinExpression(XQExpression srcExpr, List<XQExpression> srcFilter, List<XQExpression> persistKeyExprs, String opr, List<XQExpression> searchKeyExprs) {
            if(opr == null) {
                throw new IllegalArgumentException("Operator is not set");
            }
            if(persistKeyExprs.size() != searchKeyExprs.size()) {
                throw new IllegalArgumentException("Keys length is different.\npersistentKeyExpr: "
                        + persistKeyExprs + ", searchKeys: " + searchKeyExprs);
            }
            this._srcExpr = srcExpr;
            this._srcFilters = srcFilter;
            this._opr = opr;
            this._pkeyExprs = persistKeyExprs;
            this._skeyExprs = searchKeyExprs;
            final List<Type> keyTypes = new ArrayList<Type>(persistKeyExprs.size());
            for(XQExpression keyexpr : persistKeyExprs) {
                final Type t = keyexpr.getType();
                if(!(t instanceof NodeType) && !(t instanceof AtomicType)) {
                    keyTypes.add(StringType.STRING);
                } else {
                    keyTypes.add(t);
                }
            }
            this._keyTypes = keyTypes;
            if(srcFilter.isEmpty()) {
                this._type = srcExpr.getType();
            } else {
                final int last = srcFilter.size() - 1;
                this._type = srcFilter.get(last).getType();
            }
            this._isNodeComp = (opr == IS.sgn || opr == PRECEDES.sgn || opr == FOLLOWS.sgn);
        }

        public XQExpression getSrcExpr() {
            return _srcExpr;
        }

        public void setSrcExpr(XQExpression src) {
            this._srcExpr = src;
        }

        public List<XQExpression> getPersistKeyExprs() {
            return _pkeyExprs;
        }

        public List<XQExpression> getSearchKeyExprs() {
            return _skeyExprs;
        }

        public List<XQExpression> getFilterExprs() {
            return _srcFilters;
        }

        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder(128);
            buf.append(_srcExpr.toString());
            for(XQExpression filter : _srcFilters) {
                buf.append('[');
                buf.append(filter.toString());
                buf.append(']');
            }
            final int condsize = _pkeyExprs.size();
            for(int i = 0; i < condsize; i++) {
                buf.append('[');
                buf.append(_pkeyExprs.get(i));
                buf.append(' ');
                buf.append(_opr);
                buf.append(' ');
                buf.append(_skeyExprs.get(i));
                buf.append(']');
            }
            return buf.toString();
        }

        public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
                throws XQueryException {
            return visitor.visit(this, ctxt);
        }

        public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
            return this;
        }

        public Sequence<? extends Item> eval(final Sequence<? extends Item> contextSeq, final DynamicContext dynEnv)
                throws XQueryException {
            final Map<Integer, JoinTable> tables = _tables;
            final int shippedId = _shippedId;
            if(shippedId != -1) {
                this._joinTable = tables.get(shippedId);
            }
            if(_joinTable != null) {
                return map(contextSeq, dynEnv);
            }
            if(ENV_DISABLE_JOIN_THREADING) {
                final JoinTable joinTable = makeJoinTable(contextSeq, dynEnv);
                this._joinTable = joinTable;
                if(shippedId != -1) {
                    tables.put(shippedId, joinTable);
                }
                return map(contextSeq, dynEnv);
            }
            // threaded execution
            final ExecutorService ex = Executors.newSingleThreadExecutor();
            final Callable<JoinTable> call = new Callable<JoinTable>() {
                public JoinTable call() throws Exception {
                    final JoinTable joinTable = makeJoinTable(contextSeq, dynEnv);
                    if(shippedId != -1) {
                        tables.put(shippedId, joinTable);
                    }
                    return joinTable;
                }
            };
            final Future<JoinTable> future = ex.submit(call);
            final Sequence<? extends Item> mapped;
            try {
                mapped = map(contextSeq, dynEnv, future);
            } catch (InterruptedException e) {
                ex.shutdownNow();
                throw new IllegalStateException(e);
            } catch (ExecutionException e) {
                ex.shutdownNow();
                throw new IllegalStateException(e);
            }
            ex.shutdown(); // REVIEWME is really required?
            return mapped;
        }

        protected JoinTable makeJoinTable(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
                throws XQueryException {
            Sequence<? extends Item> src = _srcExpr.eval(contextSeq, dynEnv);
            for(XQExpression f : _srcFilters) {
                src = f.eval(src, dynEnv);
            }
            final int keysize = _pkeyExprs.size();
            assert (keysize > 0);
            final JoinTable joinTable = new JoinTable(_opr, _type);
            final IFocus<? extends Item> srcItor = src.iterator();
            int counter = 0;
            outer: for(Item it : srcItor) {
                final Comparable[] persistKeys = new Comparable[keysize];
                if(keysize == 1) {
                    final XQExpression key = _pkeyExprs.get(0);
                    final IFocus<? extends Item> pkvItor = key.eval(it, dynEnv).iterator();
                    for(Item fpv : pkvItor) {
                        if(_isNodeComp || fpv instanceof AtomicValue) {
                            persistKeys[0] = fpv;
                        } else {
                            if(counter == 0) {
                                _keyTypes.set(0, StringType.STRING);
                            }
                            persistKeys[0] = new XString(fpv.stringValue());
                        }
                        joinTable.put(persistKeys, it);
                    }
                    pkvItor.closeQuietly();
                } else {// TODO FIXME multiple join keys    
                    for(int i = 0; i < keysize; i++) {
                        final XQExpression key = _pkeyExprs.get(i);
                        final Sequence<? extends Item> pkv = key.eval(it, dynEnv);
                        final Iterator<? extends Item> pkvItor = pkv.iterator();
                        if(!pkvItor.hasNext()) {
                            continue outer;
                        }
                        final Item fpv = pkvItor.next();
                        if(pkvItor.hasNext()) {
                            persistKeys[i] = new SingleCollection(pkv, dynEnv);
                        } else if(_isNodeComp || fpv instanceof AtomicValue) {
                            persistKeys[i] = fpv;
                        } else {
                            if(counter == 0) {
                                _keyTypes.set(i, StringType.STRING);
                            }
                            persistKeys[i] = new XString(fpv.stringValue());
                        }
                    }
                    joinTable.put(persistKeys, it);
                }
                counter++;
            }
            srcItor.closeQuietly();
            return joinTable;
        }

        protected ValueSequence map(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
                throws XQueryException {
            final int searchKeyLen = _skeyExprs.size();
            Iterator<? extends Item> skvItor = null;
            for(int i = 0; i < searchKeyLen; i++) {
                final Sequence<? extends Item> skv = _skeyExprs.get(i).eval(contextSeq, dynEnv);
                if(skvItor == null) {
                    skvItor = skv.iterator();
                } else {
                    skvItor = new NestedIterator<Item>(skvItor, skv.iterator());
                }
            }
            JoinTable joinTable = _joinTable;
            final ValueSequence vs = new ValueSequence(dynEnv);
            while(skvItor.hasNext()) {
                final Comparable searchKey;
                if(searchKeyLen == 1) {
                    final Item fsv = skvItor.next();
                    searchKey = mapArgument(fsv, 0, dynEnv);
                } else {
                    final Comparable[] searchKeys = new Comparable[searchKeyLen];
                    for(int i = 0; i < searchKeyLen; i++) {
                        Item fsv = skvItor.next();
                        searchKeys[i] = mapArgument(fsv, i, dynEnv);
                    }
                    searchKey = new JoinTable.Key(searchKeys, _opr);
                }
                final IFocus<Item> jsItor = joinTable.entrySequence(searchKey, dynEnv).iterator();
                for(Item entry : jsItor) {
                    vs.addItem(entry);
                }
                jsItor.closeQuietly();
            }
            if(vs.isEmpty()) {
                return ValueSequence.EMPTY_SEQUENCE;
            }
            return vs;
        }

        private Item mapArgument(Item item, int index, DynamicContext dynEnv)
                throws XQueryException {
            if(_isNodeComp) {
                return item;
            }
            final Type t = _keyTypes.get(index);
            if(t == StringType.STRING) {
                return new XString(item.stringValue());
            }
            final AtomicValue it = (item instanceof AtomicValue) ? (AtomicValue) item
                    : new XString(item.stringValue());
            if(t instanceof AtomicType) {
                final AtomicType trgType = (AtomicType) t;
                final AtomicValue converted = it.castAs(trgType, dynEnv);
                return converted;
            }
            return it;
        }

        protected Sequence<? extends Item> map(Sequence<? extends Item> contextSeq, DynamicContext dynEnv, Future<JoinTable> future)
                throws XQueryException, InterruptedException, ExecutionException {
            final int searchKeyLen = _skeyExprs.size();
            Iterator<? extends Item> skvItor = null;
            for(int i = 0; i < searchKeyLen; i++) {
                Sequence<? extends Item> skv = _skeyExprs.get(i).eval(contextSeq, dynEnv);
                final Iterator<? extends Item> itor = skv.iterator();//skv.materialize().iterator();
                if(skvItor == null) {
                    skvItor = itor;
                } else {
                    skvItor = new NestedIterator<Item>(skvItor, itor);
                }
            }
            final ValueSequence vs = new ValueSequence(dynEnv);
            while(skvItor.hasNext()) {
                final Comparable searchKey;
                if(searchKeyLen == 1) {
                    final Item fsv = skvItor.next();
                    searchKey = mapArgument(fsv, 0, dynEnv);
                } else {
                    final Comparable[] searchKeys = new Comparable[searchKeyLen];
                    for(int i = 0; i < searchKeyLen; i++) {
                        Item fsv = skvItor.next();
                        searchKeys[i] = mapArgument(fsv, i, dynEnv);
                    }
                    searchKey = new JoinTable.Key(searchKeys, _opr);
                }
                if(_joinTable == null) {
                    this._joinTable = future.get();
                }
                final IFocus<Item> js = _joinTable.entrySequence(searchKey, dynEnv).iterator();
                for(Item entry : js) {
                    vs.addItem(entry);
                }
                js.closeQuietly();
            }
            if(vs.isEmpty()) {
                return ValueSequence.EMPTY_SEQUENCE;
            }
            return vs;
        }
    }

    private static final class JoinTable implements Serializable {
        private static final long serialVersionUID = -1029891058586613017L;

        private final Map<Comparable, Entry> _table = new HashMap<Comparable, Entry>(32, 0.8f);
        private final SortedSet<Comparable> _orderedKey;
        private final Key _probe;
        private final String _opr;
        private final boolean _equiJoin;
        private final Type _type;

        public JoinTable(String opr, Type type) {
            if(opr == null || type == null) {
                throw new IllegalArgumentException();
            }
            this._opr = opr;
            if(EQ.sgn.equals(opr) || IS.sgn.equals(opr)) {
                this._equiJoin = true;
                this._orderedKey = null;
            } else {
                this._equiJoin = false;
                this._orderedKey = new TreeSet<Comparable>();
            }
            this._type = type;
            this._probe = new Key(null, opr);
        }

        public static final class Key implements Comparable, Serializable {
            private static final long serialVersionUID = -7951500027264400851L;

            private Comparable[] keys;
            private final int[] expected;
            private final int unexpected;

            public Key(Comparable[] keys, String opr) {
                this.keys = keys;
                if(EQ.sgn.equals(opr) || IS.sgn.equals(opr)) {
                    this.expected = new int[] { 0 };
                    this.unexpected = -1;
                } else if(NE.sgn.equals(opr)) {
                    this.expected = new int[] { 1, -1 };
                    this.unexpected = 0;
                } else if(GT.sgn.equals(opr) || FOLLOWS.sgn.equals(opr)) {
                    this.expected = new int[] { 1 };
                    this.unexpected = -1;
                } else if(LT.sgn.equals(opr) || PRECEDES.sgn.equals(opr)) {
                    this.expected = new int[] { -1 };
                    this.unexpected = 1;
                } else if(GE.sgn.equals(opr)) {
                    this.expected = new int[] { 1, 0 };
                    this.unexpected = -1;
                } else if(LE.sgn.equals(opr)) {
                    this.expected = new int[] { 0, -1 };
                    this.unexpected = 1;
                } else {
                    throw new IllegalArgumentException("Illegal operator: " + opr);
                }
            }

            public int compareTo(Object o) {
                if(!(o instanceof Key)) {
                    throw new IllegalStateException("Invalid key class: " + o.getClass().getName());
                }
                final Key key2 = (Key) o;
                assert (keys.length > 0 && keys.length == key2.keys.length);
                for(int i = 0; i < keys.length; i++) {
                    final Comparable c1 = keys[i];
                    final Comparable c2 = key2.keys[i];
                    final int cmp = c1.compareTo(c2);
                    // hack
                    boolean found = false;
                    for(int c : expected) {
                        if(c == cmp) {
                            found = true;
                        }
                    }
                    if(!found) {
                        return unexpected;
                    }
                }
                return expected[0];
            }

            @Override
            public int hashCode() {
                return Arrays.hashCode(keys);
            }

            @Override
            public boolean equals(Object obj) {
                if(obj instanceof Key) {
                    return Arrays.equals(keys, ((Key) obj).keys);
                }
                return false;
            }

            @Override
            public String toString() {
                if(keys == null) {
                    return "{}";
                }
                final StringBuilder buf = new StringBuilder(64);
                buf.append('{');
                final int size = keys.length;
                for(int i = 0; i < size; i++) {
                    if(i != 0) {
                        buf.append(',');
                    }
                    buf.append(keys[i].toString());
                }
                buf.append('}');
                return buf.toString();
            }
        }

        public static final class Entry implements Serializable {
            private static final long serialVersionUID = -6288377491722537673L;

            private Item[] values;
            private int count;

            public Entry(final Item value) {
                assert (value != null);
                this.values = new Item[] { value, null };
                this.count = 1;
            }

            public void add(Item it) {
                if(count >= values.length) {
                    final Item[] nv = new Item[values.length * 2];
                    System.arraycopy(values, 0, nv, 0, values.length);
                    this.values = nv;
                }
                values[count] = it;
                ++count;
            }

            public Item[] getItems() {
                if(values.length == count) {
                    return values;
                } else {
                    Item[] newItems = new Item[count];
                    System.arraycopy(values, 0, newItems, 0, count);
                    this.values = newItems;
                    return newItems;
                }
            }

            @Override
            public int hashCode() {
                return Arrays.hashCode(values);
            }

            @Override
            public boolean equals(Object obj) {
                if(obj instanceof Entry) {
                    return Arrays.equals(values, ((Entry) obj).values);
                }
                return false;
            }

            @Override
            public String toString() {
                final StringBuilder buf = new StringBuilder(128);
                for(int i = 0; i < count; i++) {
                    buf.append(values[i].toString());
                    if(i != (count - 1)) {
                        buf.append(",\n");
                    }
                }
                return buf.toString();
            }
        }

        private static final class JoinEntrySequence extends AbstractSequence<Item> {
            private static final long serialVersionUID = 5987456615844113724L;

            private final Map<Comparable, Entry> _table;
            private final SortedSet<Comparable> _sortedKeys;
            private final Type _type;

            public JoinEntrySequence(final Map<Comparable, Entry> table, final SortedSet<Comparable> sorted, final DynamicContext dynEnv, final Type type) {
                super(dynEnv);
                if(sorted == null) {
                    throw new IllegalArgumentException();
                }
                this._table = table;
                this._sortedKeys = sorted;
                this._type = type;
            }

            @Override
            public Focus<Item> iterator() {
                final Map<Comparable, Entry> tbl = _table;
                final int keylen = _sortedKeys.size();
                final ArrayList<Item> items = new ArrayList<Item>(keylen * 2);
                for(Comparable key : _sortedKeys) {
                    final Entry entry = tbl.get(key);
                    final Item[] ei = entry.getItems();
                    for(Item i : ei) {
                        items.add(i);
                    }
                }
                final Focus<Item> focus = new Focus<Item>(this, items.iterator(), _dynEnv);
                return focus;
            }

            public boolean next(IFocus focus) throws XQueryException {
                final Iterator<Item> itemItor = focus.getBaseFocus();
                if(itemItor.hasNext()) {
                    final Item curItem = itemItor.next();
                    focus.setContextItem(curItem);
                    return true;
                }
                return false;
            }

            public Type getType() {
                return _type;
            }

        }

        private Sequence<Item> entrySequence(Comparable key, DynamicContext dynEnv) {
            if(_equiJoin) { // ==
                final Entry entry = find(key);
                if(entry == null) {
                    return ValueSequence.EMPTY_SEQUENCE;
                }
                Item[] items = entry.getItems();
                return new ValueSequence(Arrays.asList(items), dynEnv);
            }
            assert (_orderedKey != null);
            final SortedSet<Comparable> sorted;
            if(_opr.indexOf('<') != -1) { // <
                SortedSet<Comparable> heads = _orderedKey.headSet(key); // note that headSet contains key itself.
                if(_opr.indexOf('=') == -1) {
                    heads.remove(key);
                }
                sorted = heads;
            } else if(_opr.indexOf('>') != -1) { // > 
                SortedSet<Comparable> tails = _orderedKey.tailSet(key); // note that tailSet doesn't contain key itself.
                if(_opr.indexOf('=') != -1) {
                    tails.add(key);
                }
                sorted = tails;
            } else if(NE.sgn.equals(_opr)) { // !=
                SortedSet<Comparable> excepts = new TreeSet<Comparable>(_orderedKey);
                excepts.remove(key);
                sorted = excepts;
            } else {
                throw new IllegalStateException("Illegal operator: " + _opr);
            }
            if(sorted.isEmpty()) {
                return ValueSequence.EMPTY_SEQUENCE;
            }
            return new JoinEntrySequence(_table, sorted, dynEnv, _type);
        }

        private Entry find(Comparable key) {
            return _table.get(key);
        }

        private void put(Comparable[] keys, Item value) {
            assert (keys != null);
            assert (keys.length > 0);
            final Comparable key;
            if(keys.length == 1) {
                key = keys[0];
                assert (key != null);
            } else {
                _probe.keys = keys;
                key = _probe;
            }
            Entry entry = _table.get(key);
            if(entry != null) {
                entry.add(value);
            } else {
                entry = new Entry(value);
                Comparable k = (key == _probe) ? new Key(keys, _opr) : key;
                _table.put(k, entry);
                if(!_equiJoin) {
                    _orderedKey.add(k);
                }
            }
        }
    }

}
