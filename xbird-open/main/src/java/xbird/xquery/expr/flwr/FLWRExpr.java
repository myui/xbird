/*
 * @(#)$Id: FLWRExpr.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.flwr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.AbstractSequence;
import xbird.xquery.dm.value.ISorted;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.SingleItem.DummySingleItem;
import xbird.xquery.dm.value.sequence.GroupedSequence;
import xbird.xquery.dm.value.sequence.ProxySequence;
import xbird.xquery.dm.value.sequence.SortedSequence;
import xbird.xquery.dm.value.sequence.GroupedSequence.PreGroupingVariableExtractor;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.cond.IfExpr;
import xbird.xquery.expr.logical.AndExpr;
import xbird.xquery.expr.opt.Join.JoinDetector;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.expr.var.BindingVariable.LetVariable;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.EmptyFocus;
import xbird.xquery.meta.Focus;
import xbird.xquery.meta.IFocus;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.optim.FLWORArranger;
import xbird.xquery.optim.LoopInvariantsTransformer;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#doc-xquery-FLWORExpr
 * @link http://www.w3.org/TR/xquery-semantics/#sec_flwor-expressions
 */
public final class FLWRExpr extends AbstractXQExpression {
    private static final long serialVersionUID = -6280282168372880602L;
    private static final boolean ENV_NO_JOIN = System.getProperty("xbird.nojoins") != null;
    private static final boolean ENV_ENABLE_LOOPOPTIM = System.getProperty("xbird.enable_loopopt") != null;

    private List<Binding> _clauses = new LinkedList<Binding>();
    private Grouping _groupByClause = null;
    private/* final */List<OrderSpec> _orderSpecs = new ArrayList<OrderSpec>(4);
    private XQExpression _returnExpr;
    private boolean _stableOrdering = false; // always stable for now
    private XQExpression _whereExpr = null;

    public XQExpression _filteredReturnExpr; // set at evaluation(normalization) time
    private boolean _transformed = false;

    public FLWRExpr() {}

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public boolean isTransformed() {
        return _transformed;
    }

    public void setTransformed(boolean transformed) {
        this._transformed = transformed;
    }

    public void addClause(Binding clause) {
        _clauses.add(clause);
    }

    public void addClauses(List<Binding> list) {
        _clauses.addAll(list);
    }

    public void addOrderSpec(OrderSpec spec) {
        _orderSpecs.add(spec);
    }

    public void setOrderSpecs(List<OrderSpec> specs) {
        this._orderSpecs = specs;
    }

    public List<Binding> getClauses() {
        return this._clauses;
    }

    public XQExpression getFilteredReturnExpr() {
        return _filteredReturnExpr;
    }

    public void setFilteredReturnExpr(XQExpression expr) {
        this._filteredReturnExpr = expr;
    }

    public List<OrderSpec> getOrderSpecs() {
        return this._orderSpecs;
    }

    public XQExpression getReturnExpr() {
        return _returnExpr;
    }

    public XQExpression getWhereExpr() {
        return _whereExpr;
    }

    public boolean isStableOrdering() {
        return _stableOrdering;
    }

    public void setReturnExpr(XQExpression returnExpr) {
        this._returnExpr = returnExpr;
    }

    public void setStableOrdering(boolean stableOrdering) {
        this._stableOrdering = stableOrdering;
    }

    public void setWhereExpr(XQExpression expr) {
        this._whereExpr = expr;
    }

    public void addWhereExpr(XQExpression expr) {
        assert (expr != null);
        if(_whereExpr == null) {
            this._whereExpr = expr;
        } else {
            this._whereExpr = new AndExpr(_whereExpr, expr);
        }
    }

    public void setGroupByClause(Grouping groupByClause) {
        this._groupByClause = groupByClause;
    }

    public Grouping getGroupByClause() {
        return _groupByClause;
    }

    //--------------------------------------------
    // static analysis/dynamic evaluation

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;

            int loopDepth = statEnv.getLoopDepth();
            // #1 for, let                                    
            ForClause forClause = null;
            assert (_clauses.size() > 0) : "Binding clause is not found.";
            for(Binding clause : _clauses) {
                clause.staticAnalysis(statEnv);
                if(clause.getExpressionType() == Binding.FOR_CLAUSE) {
                    if(forClause != null) {
                        throw new IllegalStateException("nested forClause: " + this);
                    }
                    forClause = (ForClause) clause;
                }
            }
            // #2 group by
            if(_groupByClause != null) {
                _groupByClause.staticAnalysis(statEnv);
            }
            // #3 where, return
            if(_filteredReturnExpr == null) {
                throw new IllegalStateException();
            }
            XQExpression normRetExpr = _filteredReturnExpr.staticAnalysis(statEnv);
            this._type = normRetExpr.getType();
            this._filteredReturnExpr = normRetExpr;
            // #4 order by
            for(OrderSpec orderby : _orderSpecs) {
                orderby.staticAnalysis(statEnv);
            }
            statEnv.setLoopDepth(loopDepth);

            XQExpression normed = normalize();
            XQExpression analyzed = normed.staticAnalysis(statEnv);
            // join detection
            if(!ENV_NO_JOIN) {
                JoinDetector detector = new JoinDetector();
                analyzed.visit(detector, null);
            }
            // loop-invariants code motion
            if(ENV_ENABLE_LOOPOPTIM) {//TODO
                if(forClause != null) {
                    LoopInvariantsTransformer transformer = new LoopInvariantsTransformer(this, forClause);
                    transformer.visit(this, null);
                    transformer.transform();
                }
            }
            return analyzed;
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        Sequence input = new DummySequence(dynEnv);
        for(Binding bc : _clauses) {
            final int type = bc.getExpressionType();
            if(type == Binding.LET_CLAUSE) {
                LetVariable lv = ((LetClause) bc).getVariable();
                int refcnt = lv.getReferenceCount();
                if(refcnt == 0) {
                    continue;
                } else {
                    input = new ActionSequence(input, bc, dynEnv);
                }
            } else {
                input = new PipedActionSequence(input, bc, dynEnv);
            }
        }
        // group by
        if(_groupByClause != null) {
            GroupingSpec[] specs = _groupByClause.getGroupingKeysAsArray();

            PreGroupingVariableExtractor extractor = new PreGroupingVariableExtractor(specs);
            extractor.visit(_filteredReturnExpr, dynEnv);
            List<BindingVariable> nonGroupingVariables = extractor.getNonGroupingVariables();

            input = new GroupedSequence(input, specs, nonGroupingVariables, contextSeq, dynEnv, _groupByClause.isOrdering());
        }
        // where + return
        Sequence ret = new PipedActionSequence(input, _filteredReturnExpr, dynEnv);
        // order by     TODO PERFORMANCE eager ordering 
        if(!_orderSpecs.isEmpty()) {
            return sorted(ret, _orderSpecs, contextSeq, dynEnv);
        }
        return ret;
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        if(_groupByClause != null || !_orderSpecs.isEmpty()) {
            super.evalAsEvents(handler, contextSeq, dynEnv);
            return;
        }
        Sequence<? extends Item> input = new DummySequence(dynEnv);
        for(Binding bc : _clauses) {
            final int bindingType = bc.getExpressionType();
            if(bindingType == Binding.LET_CLAUSE) {
                final LetVariable lv = ((LetClause) bc).getVariable();
                final int refcnt = lv.getReferenceCount();
                if(refcnt == 0) {
                    continue;
                }
                input = new ActionSequence(input, bc, dynEnv);
            } else if(bindingType == Binding.FOR_CLAUSE) {
                input = new PipedActionSequence(input, bc, dynEnv);
            } else {
                throw new IllegalStateException("Illegal bindingType: " + bindingType);
            }
        }

        final boolean old = handler.setBlankRequired(false);
        final IFocus<? extends Item> inputItor = input.iterator();
        final XQExpression filteredReturnExpr = _filteredReturnExpr;
        for(Item it : inputItor) {
            filteredReturnExpr.evalAsEvents(handler, it, dynEnv);
            handler.setBlankRequired(true);
        }
        inputItor.closeQuietly();
        handler.setBlankRequired(old);
    }

    private static Sequence sorted(Sequence src, List<OrderSpec> orderSpecs, Sequence contextSeq, DynamicContext dynEnv) {
        assert (!orderSpecs.isEmpty());
        if(src instanceof ISorted) {
            src = ((ISorted) src).getSource(); // TODO REVIEWME
        }
        return new SortedSequence(src, orderSpecs, contextSeq, dynEnv);
    }

    //--------------------------------------------
    // normalization stuff

    public XQExpression normalize() throws XQueryException {
        if(_returnExpr == null) {
            return this;// already normalized
        }
        if(_groupByClause != null) {
            if(_groupByClause.isGroupingAndOrderingCombinable(_orderSpecs)) {
                _groupByClause.composite(_orderSpecs);
                _groupByClause.setOrdering(true);
                _orderSpecs.clear();
            }
            final List<Binding> letClausesInGrouping = _groupByClause.getLetClauses();
            if(letClausesInGrouping != null && !letClausesInGrouping.isEmpty()) {
                FLWRExpr innerFlwr = new FLWRExpr();
                innerFlwr._clauses = letClausesInGrouping;
                innerFlwr._whereExpr = _groupByClause.getWhereExpression();
                if(_orderSpecs != null) {
                    if(_groupByClause.isOrdering()) {
                        innerFlwr._orderSpecs = _orderSpecs;
                        this._orderSpecs = Collections.emptyList();
                    }
                }
                innerFlwr._returnExpr = _returnExpr;
                _returnExpr = innerFlwr.normalize();
            }            
            PreGroupingVariableExtractor extractor = new PreGroupingVariableExtractor(_groupByClause.getGroupingKeysAsArray());
            extractor.visit(_returnExpr, DynamicContext.DUMMY);
            List<BindingVariable> vars = extractor.getNonGroupingVariables();
            for(BindingVariable v : vars) {
                if(v instanceof LetVariable && v.getReferenceCount() == 1) {
                    v.incrementReferenceCount();
                }
            }
        }
        final List<XQExpression> nodeps = new LinkedList<XQExpression>();
        final Map<Binding, XQExpression> dependancies = FLWORArranger.getDependentInWhereExpr(this, nodeps);
        this._whereExpr = null;
        XQExpression ret = FLWORArranger.applyForNormalization(this, dependancies);
        if(!nodeps.isEmpty()) {// create ifExpr with remaining where           
            int nodepLen = nodeps.size();
            final XQExpression cond;
            if(nodepLen == 1) {
                cond = nodeps.get(0);
            } else {
                cond = new AndExpr(nodeps);
            }
            ret = new IfExpr(cond, ret);
            this._whereExpr = null;
        }
        assert (this._filteredReturnExpr != null);
        assert (this._whereExpr == null);
        if(_clauses.isEmpty()) {
            return _filteredReturnExpr;
        }
        this._returnExpr = null;
        return ret;
    }

    private static final class PipedActionSequence extends ProxySequence<Item> {
        private static final long serialVersionUID = -231486273287919000L;

        private final XQExpression _actionExpr;

        public PipedActionSequence(Sequence<? extends Item> input, XQExpression actionExpr, DynamicContext dynEnv) {
            super(input, dynEnv);
            assert (actionExpr != null);
            this._actionExpr = actionExpr;
        }

        @Override
        public ActionFocus iterator() {
            return new ActionFocus(this, _delegate.iterator(), _dynEnv);
        }

        public boolean next(IFocus focus) throws XQueryException {
            final ActionFocus actFocus = (ActionFocus) focus;
            if(actFocus.reachedEnd()) {
                return false;
            }
            final IFocus<? extends Item> inputItor = (IFocus<? extends Item>) actFocus.getBaseFocus();
            IFocus<? extends Item> actionItor = actFocus.getActionItor();
            for(int i = 0;; i++) {
                final int pos = actFocus.getContextPosition();
                if((i != 0 || pos > 0) && actionItor.hasNext()) {
                    Item actionItem = actionItor.next();
                    actFocus.setContextItem(actionItem);
                    return true;
                }
                if(!inputItor.hasNext()) {
                    break;
                }
                final Item nextit = inputItor.next();
                // iterate on action in the outer loop.
                final Sequence<? extends Item> action = _actionExpr.eval(nextit, _dynEnv);
                actionItor = action.iterator();
                actFocus.setActionItor(actionItor);
            }
            inputItor.closeQuietly();
            actionItor.closeQuietly();
            focus.setReachedEnd(true);
            return false;
        }

        @Override
        public Type getType() {
            return _actionExpr.getType();
        }
    }

    private static final class ActionFocus extends Focus<Item> {
        private static final long serialVersionUID = 1788519662776701309L;

        private IFocus<? extends Item> _actionItor = EmptyFocus.getInstance();

        public ActionFocus(Sequence<Item> src, IFocus<? extends Item> base, DynamicContext dynEnv) {
            super(src, base, dynEnv);
        }

        public IFocus<? extends Item> getActionItor() {
            return _actionItor;
        }

        public void setActionItor(IFocus<? extends Item> itor) {
            this._actionItor = itor;
        }

        @Override
        public void close() throws IOException {
            _actionItor.close();
            super.close();
        }
    }

    private static final class ActionSequence extends ProxySequence<Item> {
        private static final long serialVersionUID = -2722355672697772918L;

        private final Binding _actionExpr;

        public ActionSequence(Sequence<? extends Item> input, Binding actionExpr, DynamicContext dynEnv) {
            super(input, dynEnv);
            if(actionExpr == null) {
                throw new IllegalArgumentException();
            }
            this._actionExpr = actionExpr;
        }

        public boolean next(IFocus focus) throws XQueryException {
            final IFocus<? extends Item> inputItor = (IFocus<? extends Item>) focus.getBaseFocus();
            if(inputItor.hasNext()) {
                Item nextit = inputItor.next();
                // invoke let action in the outer loop.
                _actionExpr.eval(nextit, _dynEnv);
                focus.setContextItem(nextit);
                return true;
            }
            inputItor.closeQuietly();
            return false;
        }

        @Override
        public Type getType() {
            return _actionExpr.getType();
        }
    }

    private static final class DummySequence extends AbstractSequence<Item> {
        private static final long serialVersionUID = 5827954283477645028L;

        public DummySequence(DynamicContext dynEnv) {
            super(dynEnv);
        }

        public boolean next(IFocus focus) throws XQueryException {
            if(!focus.reachedEnd()) {
                focus.setReachedEnd(true);
                focus.setContextItem(DummySingleItem.INSTANCE);
                return true;
            }
            return false;
        }

        public Type getType() {
            return Type.ANY;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

}
