/*
 * @(#)$Id: FilterExpr.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.literal.XNumber;
import xbird.xquery.dm.value.sequence.ProxySequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.LiteralExpr;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.func.FunctionCall;
import xbird.xquery.expr.opt.PathIndexAccessExpr;
import xbird.xquery.expr.opt.PathVariable;
import xbird.xquery.expr.seq.SequenceExpression;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.expr.var.BindingVariable.LetVariable;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.PredefinedFunctions;
import xbird.xquery.func.context.Last;
import xbird.xquery.func.seq.FnBoolean;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.parser.visitor.AbstractXQueryParserVisitor;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;
import xbird.xquery.type.schema.ListType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#id-predicates
 */
public final class FilterExpr extends AbstractXQExpression implements StepExpr {
    private static final long serialVersionUID = -6807805026058557126L;

    private XQExpression _srcExpr;
    private final List<XQExpression> _predicates = new ArrayList<XQExpression>(4);
    private boolean _hasContextualFilter = false;

    public FilterExpr(XQExpression src) {
        if(src == null) {
            throw new IllegalArgumentException();
        }
        this._srcExpr = src;
    }

    public XQExpression getSourceExpr() {
        return this._srcExpr;
    }

    public List<XQExpression> getPredicates() {
        return this._predicates;
    }

    public void addPredicate(XQExpression predicate) {
        _predicates.add(predicate);
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    //--------------------------------------------
    // static analysis/dynamic analysis

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            XQExpression src = _srcExpr.staticAnalysis(statEnv);
            EagarAnnotator annotator = new EagarAnnotator();
            annotator.visit(src, null);
            Type inferredType = src.getType();
            this._srcExpr = src;
            for(int i = 0; i < _predicates.size(); i++) {
                XQExpression p = _predicates.get(i);
                statEnv.setContextItemStaticType(inferredType);
                XQExpression anylyzed = p.staticAnalysis(statEnv);
                _predicates.set(i, anylyzed);
            }
            //this._type = inferredType;
            return simplify(statEnv);
        }
        return this;
    }

    private static final class EagarAnnotator extends AbstractXQueryParserVisitor {

        public EagarAnnotator() {
            super();
        }

        @Override
        public XQExpression visit(BindingVariable variable, XQueryContext ctxt)
                throws XQueryException {
            if(variable instanceof LetVariable) {
                LetVariable lv = (LetVariable) variable;
                lv.setEagarEvaluated(true);
            }
            return variable;
        }

    }

    @Override
    public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info) {
        if(!info.hasPreviousStep()) {
            return false;
        }
        if(_hasContextualFilter) {
            return false;
        }
        if(_srcExpr.isPathIndexAccessable(statEnv, info)) {
            PathIndexAccessExpr idxAccessExpr = new PathIndexAccessExpr(info, _srcExpr.getType());
            _srcExpr = PathVariable.create(idxAccessExpr, statEnv, true);
            return true;
        } else {
            return false;
        }
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        Sequence<? extends Item> res = _srcExpr.eval(contextSeq, dynEnv); // TODO REVIEWME        
        for(XQExpression p : _predicates) {
            res = p.eval(res, dynEnv); // apply filtering
        }
        return res;
    }

    /**
     * Some optimization(de-normalization) is applied.
     */
    private AbstractXQExpression simplify(StaticContext statEnv) throws XQueryException {
        if(TypeUtil.subtypeOf(_type, SequenceType.EMPTY)) {
            return SequenceExpression.EMPTY_SEQUENCE;
        }
        Type baseType = _srcExpr.getType();
        for(int i = 0; i < _predicates.size(); i++) {
            XQExpression e = _predicates.get(i);
            if(e instanceof LiteralExpr) {
                AtomicValue literal = ((LiteralExpr) e).getValue();
                if(literal instanceof XNumber) {
                    DynamicContext probe = DynamicContext.PROBE;
                    probe.setStaticContext(statEnv);
                    XNumber xnum = (XNumber) literal;
                    double xdouble = xnum.asDouble();
                    long xint = xnum.asLong();
                    if(xdouble != xint) {
                        return SequenceExpression.EMPTY_SEQUENCE;
                    }
                    if(xint > Integer.MAX_VALUE) {
                        throw new IllegalStateException();
                    }
                    int pos = (int) xint;
                    if(pos < 1) {
                        return SequenceExpression.EMPTY_SEQUENCE;
                    }
                    final PositionFilter filter = new PositionFilter(pos, baseType);
                    _predicates.set(i, filter);
                    baseType = filter.getType();
                    _hasContextualFilter = true;
                    continue;
                }
            } else if(e instanceof FunctionCall) {
                FunctionCall fc = (FunctionCall) e;
                BuiltInFunction fnlast = PredefinedFunctions.lookup(Last.SYMBOL);
                if(fnlast.getName().equals(fc.getFuncName())) { // if fn:last()
                    final LastFilter filter = new LastFilter(baseType);
                    _predicates.set(i, filter);
                    baseType = filter.getType();
                    _hasContextualFilter = true;
                    continue;
                }
            } else if(e instanceof PathVariable) {
                final ExistsFilter filter = new ExistsFilter(e, baseType);
                _predicates.set(i, filter);
                _hasContextualFilter = true;
                continue;
            }
            final ConditionalFilter filter = new ConditionalFilter(e, baseType);
            _predicates.set(i, filter);
        }
        this._type = baseType;
        return this;
    }

    public abstract static class Instruction extends AbstractXQExpression {
        private static final long serialVersionUID = 1L;

        public Instruction() {
            this._analyzed = true;
        }

        public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
            return this;
        }

        public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
                throws XQueryException {
            return visitor.visit(this, ctxt);
        }
    }

    private static final class PositionFilter extends Instruction {
        private static final long serialVersionUID = -8028124545542650688L;

        private final int pos;

        public PositionFilter(int pos, Type baseType) {
            super();
            if(pos < 1) {
                throw new IllegalArgumentException("Illegal position: " + pos);
            }
            this.pos = pos;
            if(baseType instanceof ListType) {
                ListType list = ((ListType) baseType);
                int size = list.size();
                if(pos > size) {
                    // REVIEWME item seems to be not exist in the position
                    this._type = baseType.prime();
                    //this._type = SequenceType.EMPTY;
                } else {
                    int i = pos - 1;
                    this._type = list.get(i);
                }
            } else {
                this._type = baseType.prime();
            }
        }

        public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
                throws XQueryException {
            if(pos <= 0) {
                return ValueSequence.EMPTY_SEQUENCE;
            }
            return new Sliced(contextSeq, pos, pos, dynEnv, _type);
        }

        @Override
        public String toString() {
            return Integer.toString(pos);
        }
    }

    private static final class LastFilter extends Instruction {
        private static final long serialVersionUID = 8332527761105927517L;

        public LastFilter(Type baseType) {
            if(baseType instanceof ListType) {
                ListType list = ((ListType) baseType);
                int size = list.size();
                if(size > 0) {
                    this._type = list.get(size - 1);
                } else {
                    // REVIEWME item seems to be not exist
                    this._type = baseType.prime();
                    //this._type = SequenceType.EMPTY;
                }
            } else {
                this._type = baseType.prime();
            }
        }

        public LastOf eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
                throws XQueryException {
            return new LastOf(contextSeq, dynEnv, _type);
        }

        @Override
        public String toString() {
            return "last()";
        }
    }

    private static final class ExistsFilter extends Instruction {
        private static final long serialVersionUID = -162788368256060170L;
        private final XQExpression predicate;

        public ExistsFilter(XQExpression e, Type baseType) {
            super();
            this.predicate = e;
            this._type = baseType;
        }

        public Exists eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
                throws XQueryException {
            return new Exists(predicate, contextSeq, dynEnv, _type);
        }

        @Override
        public String toString() {
            return "exists(.)";
        }

    }

    public static final class ConditionalFilter extends Instruction {
        private static final long serialVersionUID = -539136919808930008L;

        private final XQExpression _predicate;

        public ConditionalFilter(XQExpression e, Type baseType) {
            if(e == null) {
                throw new IllegalArgumentException();
            }
            this._predicate = e;
            this._type = baseType;
        }

        public XQExpression getPredicate() {
            return _predicate;
        }

        @Override
        public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
                throws XQueryException {
            return _predicate.visit(visitor, ctxt);
        }

        public Filtered eval(final Sequence<? extends Item> contextSeq, final DynamicContext dynEnv)
                throws XQueryException {
            return new Filtered(_predicate, contextSeq, dynEnv, _type);
        }

        @Override
        public String toString() {
            return _predicate.toString();
        }
    }

    public static final class Sliced extends ProxySequence {
        private static final long serialVersionUID = 7613358886773972861L;

        private final int _from, _to;
        private final Type type;

        public Sliced(Sequence<? extends Item> src, int from, int to, DynamicContext dynEnv, Type type) {
            super(src, dynEnv);
            if(from > to || from < 0) {
                throw new IllegalArgumentException("Illegal from: " + from + ", to: " + to);
            }
            this._from = from;
            this._to = to;
            this.type = type;
        }

        public boolean next(IFocus focus) throws XQueryException {
            final IFocus<? extends Item> srcItor = (IFocus<? extends Item>) focus.getBaseFocus();
            while(true) {
                final int curidx = focus.incrPosition();
                if(curidx > _to || !srcItor.hasNext()) {
                    srcItor.closeQuietly();
                    focus.setReachedEnd(true);
                    return false;
                }
                Item it = srcItor.next();
                if(curidx >= _from) {
                    focus.setContextItem(it);
                    return true;
                }
            }
        }

        @Override
        public Type getType() {
            return type;
        }

    }

    public static final class LastOf extends ProxySequence {
        private static final long serialVersionUID = 4988642397046873632L;
        private final Type type;

        public LastOf(Sequence delegate, DynamicContext dynEnv, Type type) {
            super(delegate, dynEnv);
            this.type = type;
        }

        public boolean next(IFocus focus) throws XQueryException {
            if(focus.reachedEnd()) {
                return false;
            }
            Iterator<? extends Item> itor = focus.getBaseFocus();
            while(itor.hasNext()) {
                final Item it = itor.next();
                if(!itor.hasNext()) {
                    focus.setContextItem(it);
                    focus.setReachedEnd(true);
                    return true;
                }
            }
            focus.setReachedEnd(true);
            return false;
        }

        @Override
        public Type getType() {
            return type;
        }
    }

    public static final class Exists extends ProxySequence {
        private static final long serialVersionUID = 484927403180498375L;

        private final XQExpression predicate;
        private final Type type;

        public Exists(XQExpression predicate, Sequence delegate, DynamicContext dynEnv, Type type) {
            super(delegate, dynEnv);
            this.predicate = predicate;
            this.type = type;
        }

        public boolean next(IFocus focus) throws XQueryException {
            final Iterator<? extends Item> itor = focus.getBaseFocus();
            while(itor.hasNext()) {
                final Item it = itor.next();
                final Sequence cond = predicate.eval(it, _dynEnv);
                if(!cond.isEmpty()) {
                    focus.setContextItem(it);
                    focus.setReachedEnd(true);
                    return true;
                }
            }
            focus.setReachedEnd(true);
            return false;
        }

        @Override
        public Type getType() {
            return type;
        }

    }

    public static final class Filtered extends ProxySequence {
        private static final long serialVersionUID = -7637704463461420787L;

        private final XQExpression predicate;
        private final Type type;

        public Filtered(XQExpression predicate, Sequence delegate, DynamicContext dynEnv, Type type) {
            super(delegate, dynEnv);
            this.predicate = predicate;
            this.type = type;
        }

        public boolean next(IFocus focus) throws XQueryException {
            final DynamicContext dynEnv = _dynEnv;
            Iterator<? extends Item> itor = focus.getBaseFocus();
            while(itor.hasNext()) {
                int curidx = focus.incrPosition();
                Item it = itor.next();
                focus.setContextItem(it);

                // REVIEWME workaround for fn:last()
                dynEnv.pushSequence(_delegate);

                // evaluates predicate
                IFocus baseFocus = dynEnv.getFocus();
                dynEnv.setFocus(focus); // change focus
                Sequence cond = predicate.eval(it, dynEnv);
                dynEnv.setFocus(baseFocus);

                // workaround for fn:last()
                dynEnv.popSequence();

                if(cond instanceof XInteger) { // positional filtering
                    final long pos = ((XInteger) cond).getValue();
                    if(curidx == pos) {
                        return true;
                    } else {
                        continue;
                    }
                }

                dynEnv.pushSequence(_delegate);
                final boolean ebv = FnBoolean.effectiveBooleanValue(cond, curidx);
                dynEnv.popSequence();
                if(ebv) {
                    return true;
                } else {
                    continue;
                }
            }
            return false;
        }

        @Override
        public Type getType() {
            return type;
        }
    }

}