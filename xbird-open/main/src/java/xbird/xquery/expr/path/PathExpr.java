/*
 * @(#)$Id: PathExpr.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.CompositeSequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.dyna.ContextItemExpr;
import xbird.xquery.expr.ext.BDQExpr;
import xbird.xquery.expr.opt.*;
import xbird.xquery.expr.path.axis.*;
import xbird.xquery.expr.var.VarRef;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.expr.var.BindingVariable.AnonymousLetVariable;
import xbird.xquery.expr.var.BindingVariable.ForVariable;
import xbird.xquery.meta.*;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.*;
import xbird.xquery.type.node.NodeType;

/**
 * 
 * <DIV lang="en">
 * Note: This expression will be pruned after static analysis.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class PathExpr extends AbstractXQExpression implements Externalizable {
    private static final long serialVersionUID = 1L;
    private static final boolean ENV_DISABLE_INDEX_ACCESS = System.getProperty("xbird.disable_index_access") != null;
    private static final int ENV_INDEXING_THRESHOLD = Integer.getInteger("xbird.indexing_threshold", 1);
    private static final Log LOG = LogFactory.getLog(PathExpr.class);

    // Constants
    public static final String ABBREV_ROOT_DESC_NODE = "//";
    public static final String ROOT_DESC_NODE = "/descendant-or-self::node()/";
    public static final String REVERSE_STEP = "parent::node()";
    public static final String ABBREV_REVERSE_STEP = "..";
    public static final String FORWARD_STEP = "attribute::";
    public static final String ABBREV_FORWARD_STEP = "@";

    // Variables
    protected/* final */List<XQExpression> _steps;
    private XQExpression _analyzedExpr = null;

    public PathExpr(List<XQExpression> stepList) {
        if(stepList == null || stepList.isEmpty()) {
            stepList = Collections.emptyList();
        }
        this._steps = stepList;
        this._type = NodeType.ANYNODE;
    }

    public PathExpr(XQExpression... steps) {
        if(steps.length == 0) {
            throw new IllegalArgumentException();
        }
        final List<XQExpression> list = new LinkedList<XQExpression>();
        for(XQExpression step : steps) {
            list.add(step);
        }
        this._steps = list;
        this._type = NodeType.ANYNODE;
    }

    public PathExpr() { // for Externalizable
        super();
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final int numSteps = in.readInt();
        final ArrayList<XQExpression> steps = new ArrayList<XQExpression>(numSteps);
        for(int i = 0; i < numSteps; i++) {
            XQExpression step = (XQExpression) in.readObject();
            steps.add(step);
        }
        this._steps = steps;
        final boolean hasAnalyzedExpr = in.readBoolean();
        if(hasAnalyzedExpr) {
            this._analyzedExpr = (XQExpression) in.readObject();
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        final List<XQExpression> steps = _steps;
        final int numSteps = steps.size();
        out.writeInt(numSteps);
        for(int i = 0; i < numSteps; i++) {
            XQExpression step = steps.get(i);
            out.writeObject(step);
        }
        final XQExpression analyzed = _analyzedExpr;
        if(analyzed == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(analyzed);
        }
    }

    public XQExpression setStep(int i, XQExpression step) {
        return _steps.set(i, step);
    }

    public List<XQExpression> getSteps() {
        return _steps;
    }

    public void addPredicate(XQExpression pred) {
        int lastidx = _steps.size() - 1;
        XQExpression lastStep = _steps.get(lastidx);
        if(lastStep instanceof FilterExpr) {
            ((FilterExpr) lastStep).addPredicate(pred);
        } else {
            FilterExpr filter = new FilterExpr(lastStep);
            filter.addPredicate(pred);
            _steps.set(lastidx, filter);
        }
    }

    //--------------------------------------------
    // static analysis/dynamic evaluation

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        this._analyzed = true;
        final List<XQExpression> step = _steps;
        if(step.isEmpty()) {
            return new ContextItemExpr();
        }
        // simplify PathExpr
        simplify();
        // pre-static analyse
        final int stepsize = step.size();
        final int lastpos = stepsize - 1;
        for(int i = 0; i < stepsize; i++) {
            XQExpression e = step.get(i);
            XQExpression analyzed = e.staticAnalysis(statEnv);
            if(i != lastpos) {
                final Type type = analyzed.getType();
                if(type instanceof AtomicType) {
                    reportError("err:XPTY0019", "Illegal result type was detected in '" + analyzed
                            + "', its type: " + type);
                }
            }
            step.set(i, analyzed);
        }
        // take the first/last StepExpr off from PathExpr
        if(stepsize == 1) { // TODO needs review (relative path case, and so on..)
            XQExpression last = step.get(0);
            if(last instanceof PathExpr) {
                throw new IllegalStateException("Unexpected expression class: "
                        + last.getClass().getName());
            }
            this._analyzedExpr = last;
            return last;
        }
        // formal semantics annotation
        final boolean annotate = isDistinctDocOrderRequired();
        boolean reverse = false;
        if(annotate) {
            final int size = step.size();
            if(size > 1) {
                XQExpression last = step.get(size - 1);
                if(last instanceof ReverseAxis) {
                    reverse = true;
                }
            }
        }
        // normalize
        XQExpression normed = normalize(statEnv);
        XQExpression analysed = normed.staticAnalysis(statEnv);
        assert (analysed != this);
        this._type = analysed.getType();
        this._analyzedExpr = analysed;
        if(annotate) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("Applying Distinct-document-order: " + analysed);
            }
            analysed = applyDistinctDocOrder(analysed, reverse);
        }
        return analysed;
    }

    /**
     * Rewrite(simplify) PathExpr based on huristic rules.
     */
    private void simplify() throws XQueryException {
        XQExpression prevStep = null;
        final List<XQExpression> step = _steps;
        for(int i = 0; i < step.size(); i++) {
            XQExpression currStep = step.get(i);
            if(i > 0) {
                if(prevStep instanceof DescendantOrSelfStep) {
                    if(currStep instanceof ChildStep) {
                        NodeTest prevNodeTest = ((DescendantOrSelfStep) prevStep).getNodeTest();
                        if(NodeTest.ANYNODE.equals(prevNodeTest)) {
                            //  rewrite rule #1
                            //    - '/descendant-or-self::node()/child::` => `/descendant::`
                            XQExpression removed = step.remove(--i);
                            assert (removed == prevStep);
                            NodeTest replacedNodeTest = ((ChildStep) currStep).getNodeTest();
                            DescendantStep replaced = new DescendantStep(replacedNodeTest);
                            step.set(i, replaced);
                        }
                    } else if(currStep instanceof DescendantOrSelfStep
                            || currStep instanceof DescendantStep) {
                        //  rewrite rule #2
                        //   - '/descendant-or-self::node()/descendant-or-self::` => `/descendant-or-self::`
                        //  rewrite rule #3
                        //   - '/descendant-or-self::node()/descendant::` => `/descendant::`
                        NodeTest prevNodeTest = ((DescendantOrSelfStep) prevStep).getNodeTest();
                        if(NodeTest.ANYNODE.equals(prevNodeTest)) {
                            XQExpression removed = step.remove(i - 1);
                            assert (removed == prevStep);
                        }
                    }
                }
            }
            prevStep = currStep;
        }
    }

    /**
     * Annotates fn:distinct-docorder.
     * 
     * @link http://www.w3.org/TR/xquery-semantics/#sec_distinct_docorder_or_atomic_sequence
     */
    private boolean isDistinctDocOrderRequired() {
        XQExpression rawPrevStep = null;
        final List<XQExpression> step = _steps;
        final int len = step.size();
        for(int i = 0; i < len; i++) {
            XQExpression curStep = step.get(i);
            if(i > 0) {
                final boolean sortRequired = sortRequired(rawPrevStep, curStep);
                if(sortRequired) {
                    return true;
                }
            }
            rawPrevStep = curStep;
        }
        return false;
    }

    /**
     * Apply KNormalization.
     */
    private AbstractXQExpression normalize(StaticContext statEnv) throws XQueryException {
        final List<XQExpression> step = _steps;
        if(step.isEmpty()) {
            throw new IllegalStateException();
        }
        PathVariable var = null;
        final int steplen = step.size();
        final RewriteInfo info = new RewriteInfo();
        boolean indexAccessable = ENV_DISABLE_INDEX_ACCESS ? false : true;
        int lastRewritten = -1;
        for(int i = 0; i < steplen; i++) {
            final XQExpression curStep = step.get(i);
            if(indexAccessable) {
                indexAccessable &= curStep.isPathIndexAccessable(statEnv, info);
            }
            if(indexAccessable && info.isLookaheadRequired() && (i + 1) < steplen) {
                indexAccessable = false;
                int ni = i + 1;
                XQExpression nextStep = step.get(ni);
                if(nextStep instanceof DescendantStep) {
                    indexAccessable = nextStep.isPathIndexAccessable(statEnv, info);
                    if(indexAccessable) {
                        FilteredPathIndexAcccessExpr filtered = new FilteredPathIndexAcccessExpr(info, curStep.getType());
                        var = PathVariable.create(filtered, statEnv, false);
                        step.set(ni, var);
                        lastRewritten = ni;
                        ++i;
                        continue;
                    }
                }
            }
            if(i == 0) {
                var = PathVariable.create(curStep, statEnv, false);
            } else {
                if(indexAccessable) {
                    boolean isFilter = (curStep instanceof FilterExpr);
                    // If filtered, no more index access is enabled for the time being
                    indexAccessable = !isFilter;
                    if(i >= ENV_INDEXING_THRESHOLD) {
                        if(isFilter) {
                            var = PathVariable.create(curStep, statEnv, false);
                        } else {
                            PathIndexAccessExpr idxExpr = new PathIndexAccessExpr(info, curStep.getType());
                            var = PathVariable.create(idxExpr, statEnv, true);
                        }
                        lastRewritten = i;
                    } else {
                        final CompositePath cp = new CompositePath(var, curStep);
                        var = PathVariable.create(cp, statEnv, false);
                    }
                } else {
                    final CompositePath cp = new CompositePath(var, curStep);
                    var = PathVariable.create(cp, statEnv, false);
                }
            }
            step.set(i, var);
        }
        for(int i = 0; i < lastRewritten; i++) {
            step.remove(0);
        }
        assert (var != null);
        return var;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        if(_analyzedExpr == null) {
            throw new IllegalStateException("PathExpr should be pruned.");
        }
        return _analyzedExpr.eval(contextSeq, dynEnv);
    }

    private static XQExpression applyDistinctDocOrder(XQExpression e, boolean reverse) {
        return new DistinctSortExpr(e, reverse);
    }

    /**
     * If the input is a sequence of nodes, is sorts those nodes by document order 
     * and removes duplicates.
     * 
     * @link http://www.w3.org/TR/xquery-semantics/#sec_distinct_docorder_or_atomic_sequence
     */
    private static boolean sortRequired(XQExpression prev, XQExpression curr) {
        assert (prev != null && curr != null);
        // Note that sorting by document order enforces the restriction
        // that input and output sequences contains only nodes, 
        // and that the last step in a path expression may actually return atomic values.       
        Type currType = curr.getType();
        if(TypeUtil.subtypeOf(currType, TypeRegistry.safeGet("node()*"))) {
            if(prev instanceof FilterExpr) {
                return false;
            } else if(prev instanceof AxisStep && curr instanceof AxisStep) {
                int prevAxisKind = ((AxisStep) prev).getAxisKind();
                int currAxisKind = ((AxisStep) curr).getAxisKind();
                switch(currAxisKind) {
                    case AxisStep.CHILD:
                    case AxisStep.ATTR:
                        return false;
                    default:
                        break;
                }
                switch(prevAxisKind) {
                    case AxisStep.CHILD:
                        if(currAxisKind == AxisStep.CHILD || currAxisKind == AxisStep.SELF) {
                            return false;
                        }
                        break;
                    case AxisStep.FOLLOWING_SIBLING:
                    case AxisStep.PRECEDING_SIBLING:
                        if(currAxisKind == AxisStep.CHILD || currAxisKind == AxisStep.DESC
                                || currAxisKind == AxisStep.DESC_OR_SELF
                                || currAxisKind == AxisStep.SELF) {
                            return false;
                        }
                        break;
                    case AxisStep.DESC:
                    case AxisStep.DESC_OR_SELF:
                    case AxisStep.FOLLOWING:
                    case AxisStep.PRECEDING:
                        if(currAxisKind == AxisStep.SELF) { /* || currAxisKind == AxisStep.CHILD */
                            return false;
                        }
                        break;
                    case AxisStep.PARENT:
                    case AxisStep.SELF:
                        return false;
                    case AxisStep.ANCESTOR:
                    case AxisStep.ANCESTOR_OR_SELF:
                        if(currAxisKind == AxisStep.CHILD // TODO REVIEW
                                || currAxisKind == AxisStep.FOLLOWING_SIBLING
                                || currAxisKind == AxisStep.PRECEDING_SIBLING
                                || currAxisKind == AxisStep.PARENT || currAxisKind == AxisStep.SELF) {
                            return false;
                        }
                        break;
                    case AxisStep.ATTR:
                        return false;
                    default:
                        throw new IllegalStateException("Invalid axis type: " + prevAxisKind);
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    //--------------------------------------------
    // helpers

    public static final class CompositePath extends AbstractXQExpression {
        private static final long serialVersionUID = 303229194702711722L;

        private final Variable srcVar;
        private XQExpression filterExpr;

        public CompositePath(Variable srcVar, XQExpression filterExpr) {
            if(filterExpr instanceof CompositePath) {
                throw new IllegalStateException();
            }
            this.srcVar = srcVar;
            this.filterExpr = filterExpr;
        }

        public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
                throws XQueryException {
            return visitor.visit(this, ctxt);
        }

        public Variable getSourceVariable() {
            return srcVar;
        }

        public XQExpression getSourceExpr() {
            return srcVar.getValue();
        }

        public XQExpression getFilterExpr() {
            return filterExpr;
        }

        public void setFilterExpr(XQExpression filter) {
            this.filterExpr = filter;
        }

        public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
            if(!_analyzed) {
                this._analyzed = true;
                // #1 static type analysis
                //srcVar.staticAnalysis(statEnv);
                final XQExpression analysedFilter = filterExpr.staticAnalysis(statEnv);
                final XQExpression arrangedExpr = rewrite(srcVar, analysedFilter, statEnv);
                if(arrangedExpr != null) {
                    return arrangedExpr.staticAnalysis(statEnv);
                } else {
                    XQExpression prev = srcVar.getValue();
                    srcVar.staticAnalysis(statEnv);
                    XQExpression curr = srcVar.getValue();
                    if(curr != prev) {
                        this._analyzed = false;
                        return this.staticAnalysis(statEnv);
                    }
                }
                this.filterExpr = analysedFilter;
                this._type = analysedFilter.getType();
            }
            return this;
        }

        private static XQExpression rewrite(final Variable srcVar, final XQExpression filterExpr, final StaticContext statEnv)
                throws XQueryException {
            if(srcVar instanceof ForVariable) {
                return null; //TODO REVIEWME
            }
            final XQExpression srcExpr = srcVar.getValue();
            if(srcExpr instanceof VarRef) {
                VarRef ref = (VarRef) srcExpr;
                Variable var = ref.getValue();
                assert (var != null);
                return rewrite(var, filterExpr, statEnv);
            } else if(srcExpr instanceof Variable) {
                Variable var = (Variable) srcExpr;
                return rewrite(var, filterExpr, statEnv);
            } else if(srcExpr instanceof BDQExpr) {
                final BDQExpr distExpr = (BDQExpr) srcExpr;
                final XQExpression bodyExpr = distExpr.getBodyExpression();
                if(bodyExpr instanceof PathVariable) {
                    PathVariable bodyVar = (PathVariable) bodyExpr;
                    CompositePath optExpr = new CompositePath(bodyVar, filterExpr);
                    distExpr.setBodyExpression(optExpr);
                    return distExpr;
                } else {
                    AnonymousLetVariable wrappedVar = new AnonymousLetVariable(bodyExpr);
                    CompositePath optExpr = new CompositePath(wrappedVar, filterExpr);
                    distExpr.setBodyExpression(optExpr);
                    return distExpr;
                }
            }
            return null;
        }

        public CompositeSequence<XQNode> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
                throws XQueryException {
            final Sequence<? extends Item> src = srcVar.eval(contextSeq, dynEnv);
            final CompositeSequence<XQNode> cs = new CompositeSequence<XQNode>(src, filterExpr, dynEnv);
            return cs;
        }
    }
}