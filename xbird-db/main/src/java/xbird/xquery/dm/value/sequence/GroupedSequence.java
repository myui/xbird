/*
 * @(#)$Id: codetemplate_xbird.xml 3792 2008-04-21 21:39:23Z yui $
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import xbird.util.collections.IdentityHashSet;
import xbird.xquery.DynamicError;
import xbird.xquery.XQRTException;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AbstractSequence;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.flwr.ForClause;
import xbird.xquery.expr.flwr.GroupingSpec;
import xbird.xquery.expr.flwr.LetClause;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.expr.var.VarRef;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.expr.var.BindingVariable.ForVariable;
import xbird.xquery.expr.var.BindingVariable.LetVariable;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.Focus;
import xbird.xquery.meta.IFocus;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.parser.visitor.AbstractXQueryParserVisitor;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/2008/WD-xquery-11-20080711/#id-group-by
 */
public final class GroupedSequence extends AbstractSequence {
    private static final long serialVersionUID = 3549494228786882066L;

    private final Map<GroupKeys, GroupEntry> _map;
    private Shuffler _shuffler;

    private final Type _type;

    public GroupedSequence(Sequence src, GroupingSpec[] specs, List<BindingVariable> nonGroupingVariables, Sequence contextSeq, DynamicContext dynEnv) {
        this(src, specs, nonGroupingVariables, contextSeq, dynEnv, false);
    }

    public GroupedSequence(Sequence src, GroupingSpec[] specs, List<BindingVariable> nonGroupingVariables, Sequence contextSeq, DynamicContext dynEnv, boolean sort) {
        super(dynEnv);
        if(specs.length < 1) {
            throw new IllegalArgumentException("No group keys was specified.");
        }
        if(sort || specs.length > 1) {
            //this._map = new ConcurrentSkipListMap<Item[], Sequence>();    // more memory spaces are required than TreeMap.
            this._map = new TreeMap<GroupKeys, GroupEntry>();
        } else {
            this._map = new HashMap<GroupKeys, GroupEntry>(256);
        }
        this._shuffler = new Shuffler(_map, src, specs, nonGroupingVariables, contextSeq, dynEnv);
        this._type = src.getType();
    }

    public Type getType() {
        return _type;
    }

    @Override
    public Focus iterator() {
        if(_shuffler != null) {
            try {
                _shuffler.shuffle();
            } catch (XQueryException e) {
                throw new XQRTException("Failed grouping", e);
            }
            this._shuffler = null;
        }
        GroupingIterator itor = new GroupingIterator(_map, _dynEnv);
        return new Focus(this, itor, _dynEnv);
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

    private static final class Shuffler implements Serializable {
        private static final long serialVersionUID = 8499075321609368166L;

        final Map<GroupKeys, GroupEntry> map;

        final Sequence src;
        final GroupingSpec[] specs;
        final List<BindingVariable> nonGroupingVariables;
        final Sequence contextSeq;
        final DynamicContext dynEnv;

        Shuffler(Map<GroupKeys, GroupEntry> map, Sequence src, GroupingSpec[] specs, List<BindingVariable> nonGroupingVariables, Sequence contextSeq, DynamicContext dynEnv) {
            this.map = map;
            this.src = src;
            this.specs = specs;
            this.nonGroupingVariables = nonGroupingVariables;
            this.contextSeq = contextSeq;
            this.dynEnv = dynEnv;
        }

        public void shuffle() throws XQueryException {
            final IFocus<? extends Item> itor = src.iterator();
            for(Item it : itor) {//TODO parallel map
                final int len = specs.length;
                final Item[] groupKeys = new Item[len];
                final Sequence[] rawKeys = new Sequence[len];
                for(int i = 0; i < len; i++) {
                    GroupingSpec spec = specs[i];
                    Sequence result = spec.getKeyExpr().eval(contextSeq, dynEnv);
                    Sequence atomized = result.atomize(dynEnv);
                    IFocus atomizedItor = atomized.iterator();

                    final Item groupKey;
                    if(atomizedItor.hasNext()) {
                        AtomicValue atom = (AtomicValue) atomizedItor.next();
                        if(atomizedItor.hasNext()) {
                            atomizedItor.closeQuietly();
                            throw new DynamicError("err:XQDY0095", "Illegal resulting value for a grouping variable: "
                                    + atomized);
                        }
                        groupKey = atom.asGroupingValue();
                    } else {
                        groupKey = ValueSequence.EMPTY_SEQUENCE;
                    }
                    atomizedItor.closeQuietly();

                    groupKeys[i] = groupKey;
                    rawKeys[i] = result;
                }

                final GroupKeys k = new GroupKeys(specs, groupKeys, rawKeys, dynEnv);
                final GroupEntry v = new GroupEntry(it, dynEnv);

                final GroupEntry prevEntry = map.put(k, v);
                if(prevEntry != null) {
                    v.setNonGroupingVaribales(prevEntry.getNonGroupingVaribales());

                    final Sequence prevValue = prevEntry.getValue();
                    if(prevValue instanceof ValueSequence) {
                        ((ValueSequence) prevValue).addItem(it);
                        v.setValue(prevValue);
                    } else {
                        final List<Item> list = new ArrayList<Item>(4);
                        list.add(SingleCollection.wrap(prevValue, dynEnv));
                        list.add(it);
                        ValueSequence newSeq = new ValueSequence(list, dynEnv);
                        v.setValue(newSeq);
                    }
                }

                for(BindingVariable ngv : nonGroupingVariables) {
                    v.putNonGroupingVariable(ngv);
                }
            }
            itor.closeQuietly();
        }
    }

    private static final class GroupEntry implements Externalizable {
        private static final long serialVersionUID = 8594237358142781529L;

        Sequence value;
        Map<BindingVariable, Sequence> nonGroupingVaribales = null;

        transient final DynamicContext dynEnv;

        public GroupEntry() {//Externalizable
            this.dynEnv = DynamicContext.DUMMY;
        }

        public GroupEntry(Sequence value, DynamicContext dynEnv) {
            this.value = value;
            this.dynEnv = dynEnv;
        }

        public Sequence getValue() {
            return value;
        }

        public void setValue(Sequence newValue) {
            this.value = newValue;
        }

        public Map<BindingVariable, Sequence> getNonGroupingVaribales() {
            return nonGroupingVaribales;
        }

        public void setNonGroupingVaribales(Map<BindingVariable, Sequence> nonGroupingVaribales) {
            this.nonGroupingVaribales = nonGroupingVaribales;
        }

        public void putNonGroupingVariable(BindingVariable var) throws DynamicError {
            final Sequence result = var.getResult();
            if(result == null) {
                throw new DynamicError("Result is not bounded for " + var);
            }
            if(nonGroupingVaribales == null) {
                this.nonGroupingVaribales = new IdentityHashMap<BindingVariable, Sequence>(4);
                nonGroupingVaribales.put(var, result);
            } else {
                final Sequence prevValue = nonGroupingVaribales.get(var);
                if(prevValue == null) {
                    nonGroupingVaribales.put(var, result);
                } else {
                    if(prevValue instanceof ValueSequence) {
                        Item resultItem = SingleCollection.wrap(result, dynEnv);
                        ((ValueSequence) prevValue).addItem(resultItem);
                    } else {
                        final List<Item> list = new ArrayList<Item>(4);
                        list.add(SingleCollection.wrap(prevValue, dynEnv));
                        list.add(SingleCollection.wrap(result, dynEnv));
                        ValueSequence newSeq = new ValueSequence(list, dynEnv);
                        nonGroupingVaribales.put(var, newSeq);
                    }
                }
            }
        }

        public void assignNonGroupingVariables() {
            if(nonGroupingVaribales != null && !nonGroupingVaribales.isEmpty()) {
                for(Entry<BindingVariable, Sequence> entry : nonGroupingVaribales.entrySet()) {
                    BindingVariable var = entry.getKey();
                    Sequence value = entry.getValue();
                    var.allocateResult(value, dynEnv);
                }
            }
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this.value = (Sequence) in.readObject();
            this.nonGroupingVaribales = (Map<BindingVariable, Sequence>) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(value);
            out.writeObject(nonGroupingVaribales);
        }

    }

    private static final class GroupKeys implements Comparable<GroupKeys>, Externalizable {
        private static final long serialVersionUID = 665438608513377668L;

        transient/* final */GroupingSpec[] _specs;
        transient/* final */Item[] _keys;
        transient/* final */Sequence[] _rawKeys;

        transient final DynamicContext _dynEnv;

        public GroupKeys() {// for Externalizable
            this._dynEnv = DynamicContext.DUMMY;
        }

        public GroupKeys(GroupingSpec[] specs, Item[] keys, Sequence[] rawKeys, DynamicContext dynEnv) {
            assert (specs.length == keys.length);
            this._specs = specs;
            this._keys = keys;
            this._rawKeys = rawKeys;
            this._dynEnv = dynEnv;
        }

        public void assignGroupingKeys() {
            final int len = _specs.length;
            for(int i = 0; i < len; i++) {
                BindingVariable groupingVar = _specs[i].getKeyExpr();
                groupingVar.allocateResult(_rawKeys[i], _dynEnv);
            }
        }

        public int compareTo(GroupKeys other) {
            if(this == other) {
                return 0;
            }
            final int keylen = _keys.length;
            final Item[] otherKeys = other._keys;
            assert (keylen == otherKeys.length);
            for(int i = 0; i < keylen; i++) {
                final int cmp = _specs[i].compare(_keys[i], otherKeys[i], _dynEnv);
                if(cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) {
                return true;
            }
            if(obj instanceof GroupKeys) {
                return Arrays.equals(_keys, ((GroupKeys) obj)._keys);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(_keys);
        }

        @Override
        public String toString() {
            return Arrays.toString(_keys);
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            final int len = in.readInt();
            final GroupingSpec[] specs = new GroupingSpec[len];
            final Item[] keys = new Item[len];
            final Sequence[] rawKeys = new Sequence[len];
            for(int i = 0; i < len; i++) {
                specs[i] = (GroupingSpec) in.readObject();
                keys[i] = (Item) in.readObject();
                rawKeys[i] = (Sequence) in.readObject();
            }
            this._specs = specs;
            this._keys = keys;
            this._rawKeys = rawKeys;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            final int len = _specs.length;
            out.writeInt(len);
            for(int i = 0; i < len; i++) {
                out.writeObject(_specs[i]);
                out.writeObject(_keys[i]);
                out.writeObject(_rawKeys[i]);
            }
        }
    }

    private static final class GroupingIterator implements Iterator<Item>, Serializable {
        private static final long serialVersionUID = 3766846173804574765L;

        final Map<GroupKeys, GroupEntry> _map;
        final DynamicContext _dynEnv;

        transient Iterator<Entry<GroupKeys, GroupEntry>> _itor;

        public GroupingIterator(Map<GroupKeys, GroupEntry> map, DynamicContext dynEnv) {
            this._map = map;
            this._dynEnv = dynEnv;
            this._itor = map.entrySet().iterator();
        }

        public boolean hasNext() {
            return _itor.hasNext();
        }

        public Item next() {
            Entry<GroupKeys, GroupEntry> entry = _itor.next();
            entry.getKey().assignGroupingKeys();
            GroupEntry value = entry.getValue();
            value.assignNonGroupingVariables();
            Sequence postGroupingSeq = value.getValue();
            return SingleCollection.wrap(postGroupingSeq, _dynEnv);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private Object readResolve() throws ObjectStreamException {
            assert (_map != null);
            this._itor = _map.entrySet().iterator();
            return this;
        }
    }

    public static final class PreGroupingVariableExtractor extends AbstractXQueryParserVisitor {

        private final List<BindingVariable> variablesHolder;
        private final Set<Variable> excludedVariableList;

        public PreGroupingVariableExtractor(GroupingSpec[] specs) {
            this.variablesHolder = new ArrayList<BindingVariable>(4);
            this.excludedVariableList = new IdentityHashSet<Variable>(8);
            for(GroupingSpec spec : specs) {
                BindingVariable var = spec.getKeyExpr();
                excludedVariableList.add(var);
            }
        }

        public List<BindingVariable> getNonGroupingVariables() {
            return variablesHolder;
        }

        @Override
        public ForClause visit(ForClause clause, XQueryContext ctxt) throws XQueryException {
            ForVariable var = clause.getVariable();
            excludedVariableList.add(var);
            XQExpression e = var.getValue();
            e.visit(this, ctxt);
            return clause;
        }

        @Override
        public LetClause visit(LetClause clause, XQueryContext ctxt) throws XQueryException {
            LetVariable var = clause.getVariable();
            excludedVariableList.add(var);
            XQExpression e = var.getValue();
            e.visit(this, ctxt);
            return clause;
        }

        @Override
        public XQExpression visit(VarRef ref, XQueryContext ctxt) throws XQueryException {
            final Variable var = ref.getValue();
            if(var instanceof BindingVariable) {
                if(!excludedVariableList.contains(var)) {
                    variablesHolder.add((BindingVariable) var);
                }
            }
            return ref;
        }

    }

}
