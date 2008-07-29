/*
 * @(#)$Id: LoopInvariantsTransformer.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.optim;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import xbird.xquery.XQueryException;
import xbird.xquery.XQueryModule;
import xbird.xquery.expr.LiteralExpr;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.comp.ComparisonOp;
import xbird.xquery.expr.cond.IfExpr;
import xbird.xquery.expr.cond.QuantifiedExpr;
import xbird.xquery.expr.constructor.AttributeConstructor;
import xbird.xquery.expr.constructor.AttributeConstructorBase;
import xbird.xquery.expr.constructor.CommentConstructor;
import xbird.xquery.expr.constructor.DocConstructor;
import xbird.xquery.expr.constructor.ElementConstructor;
import xbird.xquery.expr.constructor.NamespaceConstructor;
import xbird.xquery.expr.constructor.PIConstructor;
import xbird.xquery.expr.constructor.TextConstructor;
import xbird.xquery.expr.decorative.ExtensionExpr;
import xbird.xquery.expr.decorative.ParenthesizedExpr;
import xbird.xquery.expr.decorative.UnorderedExpr;
import xbird.xquery.expr.dyna.ContextItemExpr;
import xbird.xquery.expr.dyna.ValidateOp;
import xbird.xquery.expr.ext.BDQExpr;
import xbird.xquery.expr.flwr.Binding;
import xbird.xquery.expr.flwr.FLWRExpr;
import xbird.xquery.expr.flwr.ForClause;
import xbird.xquery.expr.flwr.GroupingSpec;
import xbird.xquery.expr.flwr.LetClause;
import xbird.xquery.expr.flwr.OrderSpec;
import xbird.xquery.expr.func.DirectFunctionCall;
import xbird.xquery.expr.func.FunctionCall;
import xbird.xquery.expr.func.LazyFunctionCall;
import xbird.xquery.expr.func.RecursiveCall;
import xbird.xquery.expr.logical.AndExpr;
import xbird.xquery.expr.logical.OrExpr;
import xbird.xquery.expr.math.AdditiveExpr;
import xbird.xquery.expr.math.MultiplicativeExpr;
import xbird.xquery.expr.math.NegativeExpr;
import xbird.xquery.expr.opt.DistinctSortExpr;
import xbird.xquery.expr.opt.ExpressionProxy;
import xbird.xquery.expr.opt.PathIndexAccessExpr;
import xbird.xquery.expr.opt.PathVariable;
import xbird.xquery.expr.opt.TypePromotedExpr;
import xbird.xquery.expr.opt.Join.PromoteJoinExpression;
import xbird.xquery.expr.path.FilterExpr;
import xbird.xquery.expr.path.NodeTest;
import xbird.xquery.expr.path.PathExpr;
import xbird.xquery.expr.path.FilterExpr.ConditionalFilter;
import xbird.xquery.expr.path.FilterExpr.Instruction;
import xbird.xquery.expr.path.PathExpr.CompositePath;
import xbird.xquery.expr.path.axis.AxisStep;
import xbird.xquery.expr.seq.SequenceExpression;
import xbird.xquery.expr.seq.SequenceOp;
import xbird.xquery.expr.seq.UnionOp;
import xbird.xquery.expr.types.CaseClause;
import xbird.xquery.expr.types.CastExpr;
import xbird.xquery.expr.types.CastableExpr;
import xbird.xquery.expr.types.InstanceofOp;
import xbird.xquery.expr.types.TreatExpr;
import xbird.xquery.expr.types.TypeswitchExpr;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.expr.var.VarRef;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.expr.var.BindingVariable.ForVariable;
import xbird.xquery.expr.var.BindingVariable.LetVariable;
import xbird.xquery.expr.var.BindingVariable.LoopInvariantLetVariable;
import xbird.xquery.expr.var.BindingVariable.ParametricVariable;
import xbird.xquery.expr.var.BindingVariable.PositionalVariable;
import xbird.xquery.expr.var.Variable.ExternalVariable;
import xbird.xquery.expr.var.Variable.PreEvaluatedVariable;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.UserFunction;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * Attempt 'loop-invariants code motion' optimization.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @deprecated
 */
public final class LoopInvariantsTransformer implements XQueryParserVisitor {

    private final FLWRExpr _flwr;
    private final ForClause _forClause;
    private final ForVariable _loopForVar;
    private final PositionalVariable _loopPosVar;
    private final int _noFollowBefore;

    private boolean _protectHook = false;
    private final Map<XQExpression, ExpressionProxy> _invarients = new IdentityHashMap<XQExpression, ExpressionProxy>(16);
    private final Map<LetClause, List<Binding>> _pullup = new IdentityHashMap<LetClause, List<Binding>>(4);

    public LoopInvariantsTransformer(FLWRExpr flwr, ForClause forClause) {
        this._flwr = flwr;
        this._forClause = forClause;
        ForVariable forVar = forClause.getVariable();
        this._loopForVar = forVar;
        this._loopPosVar = forClause.getPositionVariable();
        this._noFollowBefore = forVar.getBirthId();
    }

    public XQExpression visit(AdditiveExpr op, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        XQExpression left = op.getLeftOperand();
        XQExpression left2 = left.visit(this, ctxt);
        if(left2 != left) {
            op.setLeftOperand(left2);
        }
        isLoopInvariant &= left.isLoopInvariant();
        XQExpression right = op.getRightOperand();
        XQExpression right2 = right.visit(this, ctxt);
        if(right2 != right) {
            op.setRightOperand(right2);
        }
        isLoopInvariant &= right.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(left);
            removeInvariants(right);
            return hookLoopInvariant(op);
        }
        return op;
    }

    public XQExpression visit(AndExpr andExpr, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        List<XQExpression> exprs = andExpr.getExpressions();
        int exprsSize = exprs.size();
        for(int i = 0; i < exprsSize; i++) {
            XQExpression e = exprs.get(i);
            XQExpression e2 = e.visit(this, ctxt);
            if(e2 != e) {
                exprs.set(i, e2);
            }
            isLoopInvariant &= e.isLoopInvariant();
        }
        if(isLoopInvariant) {
            for(XQExpression e : andExpr.getExpressions()) {
                removeInvariants(e);
            }
            return hookLoopInvariant(andExpr);
        }
        return andExpr;
    }

    public XQExpression visit(AttributeConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        boolean isLoopInvariant = true;
        QualifiedName name = constructor.getName();
        if(name == null) {
            XQExpression nameExpr = constructor.getNameExpr();
            XQExpression nameExpr2 = nameExpr.visit(this, ctxt);
            if(nameExpr2 != nameExpr) {
                constructor.setNameExpr(nameExpr2);
            }
            isLoopInvariant &= nameExpr.isLoopInvariant();
        }
        for(XQExpression ve : constructor.getValueExprs()) {
            ve.visit(this, ctxt);
            isLoopInvariant &= ve.isLoopInvariant();
        }
        if(isLoopInvariant) {
            if(name == null) {
                XQExpression nameExpr = constructor.getNameExpr();
                removeInvariants(nameExpr);
            }
            for(XQExpression ve : constructor.getValueExprs()) {
                removeInvariants(ve);
            }
            return hookLoopInvariant(constructor);
        }
        return constructor;
    }

    public XQExpression visit(AxisStep step, XQueryContext ctxt) throws XQueryException {
        NodeTest nt = step.getNodeTest();
        nt.visit(this, ctxt);
        boolean isLoopInvariant = nt.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(nt);
            return hookLoopInvariant(step);
        }
        return step;
    }

    public XQExpression visit(BindingVariable variable, XQueryContext ctxt) throws XQueryException {
        XQExpression inExpr = variable.getValue();
        if(inExpr == null) {
            assert (variable instanceof PositionalVariable) : variable.getName();
            return variable; // TODO REVIEWME
        }
        inExpr.visit(this, ctxt);
        boolean isLoopInvariant = inExpr.isLoopInvariant();//TODO REVIEWME
        if(isLoopInvariant) {
            if(variable == _loopForVar || variable == _loopPosVar) {
                removeInvariants(inExpr);
                variable.setLoopInvariant(false);
            } else {
                int birth = variable.getBirthId();
                if(birth < _noFollowBefore) {
                    removeInvariants(inExpr);
                    if(variable instanceof ForVariable || variable instanceof PositionalVariable) {
                        variable.setLoopInvariant(false);
                    } else {
                        variable.setLoopInvariant(true);
                    }
                } else {
                    variable.setLoopInvariant(false);
                }
            }
        }
        return variable;
    }

    public XQExpression visit(BuiltInFunction function, XQueryContext ctxt) throws XQueryException {
        return null;
    }

    public XQExpression visit(CaseClause clause, XQueryContext ctxt) throws XQueryException {
        XQExpression e = clause.getReturnExpr();
        e.visit(this, ctxt);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(e);
            return hookLoopInvariant(clause);
        }
        return clause;
    }

    public XQExpression visit(CastableExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression e = expr.getExpression();
        e.visit(this, ctxt);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(e);
            return hookLoopInvariant(expr);
        }
        return expr;
    }

    public XQExpression visit(CastExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression e = expr.getExpression();
        e.visit(this, ctxt);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(e);
            return hookLoopInvariant(expr);
        }
        return expr;
    }

    public XQExpression visit(CommentConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        boolean isLoopInvariant = true;
        String content = constructor.getContent();
        if(content == null) {
            XQExpression contentExpr = constructor.getContentExpr();
            assert (contentExpr != null);
            contentExpr.visit(this, ctxt);
            isLoopInvariant &= constructor.isLoopInvariant();
        }
        if(isLoopInvariant) {
            XQExpression contentExpr = constructor.getContentExpr();
            removeInvariants(contentExpr);
            return hookLoopInvariant(constructor);
        }
        return constructor;
    }

    public XQExpression visit(ComparisonOp comp, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        XQExpression left = comp.getLeftOperand();
        left.visit(this, ctxt);
        isLoopInvariant &= left.isLoopInvariant();
        XQExpression right = comp.getRightOperand();
        right.visit(this, ctxt);
        isLoopInvariant &= right.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(left);
            removeInvariants(right);
            return hookLoopInvariant(comp);
        }
        return comp;
    }

    public XQExpression visit(CompositePath fragment, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        XQExpression src = fragment.getSourceExpr();
        src.visit(this, ctxt);
        isLoopInvariant &= src.isLoopInvariant();
        if(!isLoopInvariant) {
            return fragment;
        }
        XQExpression filter = fragment.getFilterExpr();
        filter.visit(this, ctxt);
        isLoopInvariant &= filter.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(src);
            removeInvariants(filter);
            return hookLoopInvariant(fragment);
        }
        return fragment;
    }

    public XQExpression visit(ContextItemExpr expr, XQueryContext ctxt) throws XQueryException {
        expr.setLoopInvariant(false);
        return expr;
    }

    public XQExpression visit(DirectFunctionCall call, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true; // TODO REVIEWME
        for(XQExpression p : call.getParams()) {
            p.visit(this, ctxt);
            isLoopInvariant &= p.isLoopInvariant();
        }
        if(isLoopInvariant) {
            for(XQExpression p : call.getParams()) {
                removeInvariants(p);
            }
            return hookLoopInvariant(call);
        }
        return call;
    }

    public XQExpression visit(DistinctSortExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression step = expr.getStepExpr();
        assert (step != null);
        step.visit(this, ctxt);
        boolean isLoopInvariant = step.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(step);
            return hookLoopInvariant(expr);
        }
        return expr;
    }

    public XQExpression visit(DocConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        XQExpression expr = constructor.getExpr();
        expr.visit(this, ctxt);
        boolean isLoopInvariant = expr.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(expr);
            return hookLoopInvariant(constructor);
        }
        return constructor;
    }

    public XQExpression visit(ElementConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        boolean isLoopInvariant = true;
        String name = constructor.getElemName();
        if(name == null) {
            XQExpression nameExpr = constructor.getNameExpr();
            nameExpr.visit(this, ctxt);
            isLoopInvariant &= nameExpr.isLoopInvariant();
        }
        for(AttributeConstructorBase att : constructor.getAttributes()) {
            att.visit(this, ctxt);
            isLoopInvariant &= att.isLoopInvariant();
        }
        for(XQExpression v : constructor.getContents()) {
            v.visit(this, ctxt);
            isLoopInvariant &= v.isLoopInvariant();
        }
        if(isLoopInvariant) {
            if(name != null) {
                XQExpression nameExpr = constructor.getNameExpr();
                removeInvariants(nameExpr);
                for(AttributeConstructorBase att : constructor.getAttributes()) {
                    removeInvariants(att);
                }
                for(XQExpression v : constructor.getContents()) {
                    removeInvariants(v);
                }
            }
            return hookLoopInvariant(constructor);
        }
        return constructor;
    }

    public XQExpression visit(ExpressionProxy proxy, XQueryContext ctxt) throws XQueryException {
        return proxy;
    }

    public XQExpression visit(ExtensionExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression e = expr.getExpr();
        e.visit(this, ctxt);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(e);
            return hookLoopInvariant(expr);
        }
        return expr;
    }

    public XQExpression visit(ExternalVariable var, XQueryContext ctxt) throws XQueryException {
        var.setLoopInvariant(false);
        return var;
    }

    public XQExpression visit(FilterExpr expr, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        XQExpression e = expr.getSourceExpr();
        e.visit(this, ctxt);
        isLoopInvariant &= e.isLoopInvariant();
        for(XQExpression p : expr.getPredicates()) {
            p.visit(this, ctxt);
            isLoopInvariant &= p.isLoopInvariant();
        }
        if(isLoopInvariant) {
            removeInvariants(e);
            for(XQExpression p : expr.getPredicates()) {
                removeInvariants(p);
            }
            return hookLoopInvariant(expr);
        }
        return expr;
    }

    public XQExpression visit(FLWRExpr flwr, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        // #1 for loop
        List<Binding> bindings = flwr.getClauses();
        for(int i = 0; i < bindings.size(); i++) {
            Binding b = bindings.get(i);
            int type = b.getExpressionType();
            if(flwr.isTransformed()) {
                if(type == Binding.FOR_CLAUSE) {
                    // no need to analyze
                    return flwr;
                } else {
                    LetClause lc = (LetClause) b;
                    lc.visit(this, ctxt);
                    if(lc.isLoopInvariant()) {
                        _pullup.put(lc, bindings);
                    }
                }
            } else {
                if(type == Binding.FOR_CLAUSE) {
                    ForClause forClause = (ForClause) b;
                    int loopDepth = forClause.getLoopDepth();
                    if(loopDepth <= 1) {
                        _protectHook = true;
                    }
                    forClause.visit(this, ctxt);
                    _protectHook = false;
                } else {
                    b.visit(this, ctxt);
                }
            }
            isLoopInvariant &= b.isLoopInvariant();
        }
        // #2 return
        XQExpression filteredRet = flwr.getFilteredReturnExpr();
        if(filteredRet != null) {
            XQExpression filteredRet2 = filteredRet.visit(this, ctxt);
            flwr.setFilteredReturnExpr(filteredRet2);
            isLoopInvariant &= filteredRet2.isLoopInvariant();
        } else {
            // #2-1 where filtering
            XQExpression where = flwr.getWhereExpr();
            if(where != null) {
                where.visit(this, ctxt);
                isLoopInvariant &= where.isLoopInvariant();
            }
            // #2-2 return
            XQExpression ret = flwr.getReturnExpr();
            assert (ret != null);
            ret.visit(this, ctxt);
            isLoopInvariant &= ret.isLoopInvariant();
        }
        // #3 order by sorting
        for(OrderSpec o : flwr.getOrderSpecs()) {
            o.visit(this, ctxt);
            isLoopInvariant &= o.isLoopInvariant();
        }
        if(isLoopInvariant) {
            for(Binding b : bindings) {
                removeInvariants(b);
            }
            if(filteredRet != null) {
                removeInvariants(filteredRet);
            } else {
                XQExpression where = flwr.getWhereExpr();
                if(where != null) {
                    removeInvariants(where);
                }
                XQExpression ret = flwr.getReturnExpr();
                removeInvariants(ret);
            }
            for(OrderSpec o : flwr.getOrderSpecs()) {
                removeInvariants(o);
            }
            return hookLoopInvariant(flwr);
        }
        return flwr;
    }

    public ForClause visit(ForClause clause, XQueryContext ctxt) throws XQueryException {
        XQExpression e = clause.getInExpr();
        XQExpression e2 = e.visit(this, ctxt);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            clause.setInExpr(e2);
            clause.setLoopInvariant(true);
        }
        return clause;
    }

    public XQExpression visit(FunctionCall call, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        for(XQExpression p : call.getParams()) {
            p.visit(this, ctxt);
            isLoopInvariant &= p.isLoopInvariant();
        }
        if(isLoopInvariant) {
            for(XQExpression p : call.getParams()) {
                removeInvariants(p);
            }
            return hookLoopInvariant(call);
        }
        return call;
    }
    
    public XQExpression visit(GroupingSpec spec, XQueryContext ctxt) throws XQueryException {
        return spec;
    }

    public XQExpression visit(IfExpr expr, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        // if
        XQExpression cond = expr.getCondExpr();
        cond.visit(this, ctxt);
        isLoopInvariant &= cond.isLoopInvariant();
        // then
        XQExpression then = expr.getThenExpr();
        then.visit(this, ctxt);
        isLoopInvariant &= then.isLoopInvariant();
        // else
        XQExpression els = expr.getElseExpr();
        els.visit(this, ctxt);
        isLoopInvariant &= els.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(cond);
            removeInvariants(then);
            removeInvariants(els);
            return hookLoopInvariant(expr);
        }
        return expr;
    }

    public XQExpression visit(InstanceofOp op, XQueryContext ctxt) throws XQueryException {
        XQExpression e = op.getExpression();
        e.visit(this, ctxt);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(e);
            return hookLoopInvariant(op);
        }
        return op;
    }

    public XQExpression visit(Instruction instruction, XQueryContext ctxt) throws XQueryException {
        assert (!(instruction instanceof ConditionalFilter));
        instruction.setLoopInvariant(false);
        return instruction;
    }

    public XQExpression visit(LazyFunctionCall call, XQueryContext ctxt) throws XQueryException {
        this.visit((FunctionCall) call, ctxt);
        return call;
    }

    public LetClause visit(LetClause clause, XQueryContext ctxt) throws XQueryException {
        XQExpression e = clause.getExpression();
        e.visit(this, ctxt);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(e);
            clause.setLoopInvariant(true);
        }
        return clause;
    }

    public XQExpression visit(LiteralExpr expr, XQueryContext ctxt) throws XQueryException {
        expr.setLoopInvariant(true);
        return expr;
    }

    public XQExpression visit(MultiplicativeExpr op, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        XQExpression left = op.getLeftOperand();
        left.visit(this, ctxt);
        isLoopInvariant &= left.isLoopInvariant();
        XQExpression right = op.getRightOperand();
        right.visit(this, ctxt);
        isLoopInvariant &= right.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(left);
            removeInvariants(right);
            return hookLoopInvariant(op);
        }
        return op;
    }

    public XQExpression visit(NamespaceConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        constructor.setLoopInvariant(true);
        return constructor;
    }

    public XQExpression visit(NegativeExpr op, XQueryContext ctxt) throws XQueryException {
        XQExpression e = op.getExpression();
        e.visit(this, ctxt);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(e);
            return hookLoopInvariant(op);
        }
        return op;
    }

    public XQExpression visit(NodeTest nt, XQueryContext ctxt) throws XQueryException {
        nt.setLoopInvariant(true);
        return nt;
    }

    public XQExpression visit(OrderSpec spec, XQueryContext ctxt) throws XQueryException {
        XQExpression e = spec.getKeyExpr();
        e.visit(this, ctxt);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(e);
            return hookLoopInvariant(spec);
        }
        return spec;
    }

    public XQExpression visit(OrExpr expr, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        for(XQExpression e : expr.getExpressions()) {
            e.visit(this, ctxt);
            isLoopInvariant &= e.isLoopInvariant();
        }
        if(isLoopInvariant) {
            for(XQExpression e : expr.getExpressions()) {
                removeInvariants(e);
            }
            return hookLoopInvariant(expr);
        }
        return expr;
    }

    public XQExpression visit(ParametricVariable variable, XQueryContext ctxt)
            throws XQueryException {
        this.visit((Variable) variable, ctxt);
        return variable;
    }

    public XQExpression visit(ParenthesizedExpr paren, XQueryContext ctxt) throws XQueryException {
        XQExpression e = paren.getExpr();
        e.visit(this, ctxt);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(e);
            return hookLoopInvariant(paren);
        }
        return paren;
    }

    public XQExpression visit(PathExpr path, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        List<XQExpression> steps = path.getSteps();
        int steplen = steps.size();
        for(int i = 0; i < steplen; i++) {
            XQExpression s = steps.get(i);
            XQExpression s2 = s.visit(this, ctxt);
            if(s != s2) {
                steps.set(i, s2);
            }
            isLoopInvariant &= s.isLoopInvariant();
        }
        if(isLoopInvariant) {
            for(XQExpression s : path.getSteps()) {
                removeInvariants(s);
            }
            return hookLoopInvariant(path);
        }
        return path;
    }

    public XQExpression visit(PathIndexAccessExpr variable, XQueryContext ctxt)
            throws XQueryException {
        variable.setLoopInvariant(true);
        return variable;
    }

    public XQExpression visit(PathVariable variable, XQueryContext ctxt) throws XQueryException {
        XQExpression e = variable.getValue();
        assert (e != null);
        XQExpression e2 = e.visit(this, ctxt);
        variable.setValue(e2);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(e);
            return hookLoopInvariant(variable);
        }
        return variable;
    }

    public XQExpression visit(PIConstructor constructor, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        String target = constructor.getTarget();
        if(target == null) {
            XQExpression t = constructor.getTargetExpr();
            t.visit(this, ctxt);
            isLoopInvariant &= t.isLoopInvariant();
        }
        String content = constructor.getContent();
        if(content == null) {
            XQExpression c = constructor.getContentExpr();
            c.visit(this, ctxt);
            isLoopInvariant &= c.isLoopInvariant();
        }
        if(isLoopInvariant) {
            XQExpression t = constructor.getTargetExpr();
            if(t != null) {
                removeInvariants(t);
            }
            XQExpression c = constructor.getContentExpr();
            if(c != null) {
                removeInvariants(c);
            }
            return hookLoopInvariant(constructor);
        }
        return constructor;
    }

    public XQExpression visit(PreEvaluatedVariable variable, XQueryContext ctxt)
            throws XQueryException {
        variable.setLoopInvariant(true);
        return variable;
    }

    public XQExpression visit(PromoteJoinExpression join, XQueryContext ctxt)
            throws XQueryException {
        XQExpression srcExpr = join.getSrcExpr();
        XQExpression replaced = srcExpr.visit(this, ctxt);
        join.setSrcExpr(replaced);

        List<XQExpression> searchKeys = join.getSearchKeyExprs();
        for(int i = 0; i < searchKeys.size(); i++) {
            XQExpression key = searchKeys.get(i);
            XQExpression newKey = key.visit(this, ctxt);
            if(newKey != key) {
                searchKeys.set(i, newKey);
            }
        }

        join.setLoopInvariant(false);
        return join;
    }

    public XQExpression visit(QuantifiedExpr expr, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        BindingVariable binding = expr.getBinding();
        visit(binding, ctxt);
        isLoopInvariant &= binding.isLoopInvariant();

        XQExpression cond = expr.getCondExpr();
        cond.visit(this, ctxt);
        isLoopInvariant &= cond.isLoopInvariant();

        if(isLoopInvariant) {
            removeInvariants(binding);
            removeInvariants(cond);
            return hookLoopInvariant(expr);
        }
        return expr;
    }

    public XQExpression visit(RecursiveCall call, XQueryContext ctxt) throws XQueryException {
        visit((FunctionCall) call, ctxt);
        return call;
    }

    public XQExpression visit(SequenceExpression expr, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        for(XQExpression e : expr.getExpressions()) {
            e.visit(this, ctxt);
            isLoopInvariant &= e.isLoopInvariant();
        }
        if(isLoopInvariant) {
            List<XQExpression> exprs = expr.getExpressions();
            if(exprs.isEmpty()) {
                expr.setLoopInvariant(true);
            } else {
                for(XQExpression e : exprs) {
                    removeInvariants(e);
                }
                return hookLoopInvariant(expr);
            }
        }
        return expr;
    }

    public XQExpression visit(SequenceOp op, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        XQExpression left = op.getLeftOperand();
        left.visit(this, ctxt);
        isLoopInvariant &= left.isLoopInvariant();
        XQExpression right = op.getRightOperand();
        right.visit(this, ctxt);
        isLoopInvariant &= right.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(left);
            removeInvariants(right);
            return hookLoopInvariant(op);
        }
        return op;
    }

    public XQExpression visit(TextConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        XQExpression content = constructor.getContent();
        content.visit(this, ctxt);
        boolean isLoopInvariant = content.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(content);
            return hookLoopInvariant(constructor);
        }
        return constructor;
    }

    public XQExpression visit(TreatExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression e = expr.getExpression();
        e.visit(this, ctxt);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(e);
            return hookLoopInvariant(expr);
        }
        return expr;
    }

    public XQExpression visit(TypePromotedExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression e = expr.getPromotedExpr();
        e.visit(this, ctxt);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(e);
            return hookLoopInvariant(expr);
        }
        return expr;
    }

    public XQExpression visit(TypeswitchExpr expr, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = true;
        XQExpression opr = expr.getOperandExpr();
        opr.visit(this, ctxt);
        isLoopInvariant &= opr.isLoopInvariant();
        for(XQExpression c : expr.getCaseClauses()) {
            c.visit(this, ctxt);
            isLoopInvariant &= c.isLoopInvariant();
        }
        XQExpression def = expr.getDefaultClause();
        if(def != null) {
            def.visit(this, ctxt);
            isLoopInvariant &= def.isLoopInvariant();
        }
        if(isLoopInvariant) {
            removeInvariants(opr);
            for(XQExpression c : expr.getCaseClauses()) {
                removeInvariants(c);
            }
            def = expr.getDefaultClause();
            if(def != null) {
                removeInvariants(def);
            }
            return hookLoopInvariant(expr);
        }
        return expr;
    }

    public XQExpression visit(UnionOp op, XQueryContext ctxt) throws XQueryException {
        visit((SequenceOp) op, ctxt);
        return op;
    }

    public XQExpression visit(UnorderedExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression e = expr.getExpr();
        e.visit(this, ctxt);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(e);
            return hookLoopInvariant(expr);
        }
        return expr;
    }

    public XQExpression visit(UserFunction function, XQueryContext ctxt) throws XQueryException {
        XQExpression body = function.getBodyExpression();
        body.visit(this, ctxt);
        boolean isLoopInvariant = body.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(body); // TODO REVIEWME
        }
        return null;
    }

    public XQExpression visit(ValidateOp op, XQueryContext ctxt) throws XQueryException {
        XQExpression e = op.getExpression();
        e.visit(this, ctxt);
        boolean isLoopInvariant = e.isLoopInvariant();
        if(isLoopInvariant) {
            removeInvariants(e);
            return hookLoopInvariant(op);
        }
        return op;
    }

    public XQExpression visit(Variable variable, XQueryContext ctxt) throws XQueryException {
        boolean isLoopInvariant = false;
        if(variable.getResult() == null) {
            XQExpression e = variable.getValue();
            if(e == null) {
                return variable;
            }
            e.visit(this, ctxt);
            isLoopInvariant = e.isLoopInvariant();
            if(isLoopInvariant) {
                removeInvariants(e);
            }
        }
        if(isLoopInvariant) {
            return hookLoopInvariant(variable);
        }
        return variable;
    }

    public XQExpression visit(VarRef ref, XQueryContext ctxt) throws XQueryException {
        Variable var = ref.getValue();
        var.visit(this, ctxt);
        boolean isLoopInvariant = var.isLoopInvariant();
        if(isLoopInvariant) {
            return hookLoopInvariant(ref);
        }
        return ref;
    }

    public XQExpression visit(BDQExpr expr, XQueryContext ctxt) throws XQueryException {
        // TODO REVIEWME
        XQExpression body = expr.getBodyExpression();
        expr.visit(this, ctxt);
        return expr;
    }

    public XQExpression visit(XQueryModule module, XQueryContext ctxt) throws XQueryException {
        XQExpression body = module.getExpression();
        body.visit(this, ctxt);
        return null;
    }

    public XQExpression visit(XQExpression expr, XQueryContext ctxt) throws XQueryException {
        if(expr instanceof AdditiveExpr) {
            return visit((AdditiveExpr) expr, ctxt);
        } else if(expr instanceof AndExpr) {
            return visit((AndExpr) expr, ctxt);
        } else if(expr instanceof AttributeConstructor) {
            return visit((AttributeConstructor) expr, ctxt);
        } else if(expr instanceof AxisStep) {
            return visit((AxisStep) expr, ctxt);
        } else if(expr instanceof BindingVariable) {
            return visit((BindingVariable) expr, ctxt);
        } else if(expr instanceof BuiltInFunction) {
            return visit((BuiltInFunction) expr, ctxt);
        } else if(expr instanceof CaseClause) {
            return visit((CaseClause) expr, ctxt);
        } else if(expr instanceof CastableExpr) {
            return visit((CastableExpr) expr, ctxt);
        } else if(expr instanceof CastExpr) {
            return visit((CastExpr) expr, ctxt);
        } else if(expr instanceof CommentConstructor) {
            return visit((CommentConstructor) expr, ctxt);
        } else if(expr instanceof ComparisonOp) {
            return visit((ComparisonOp) expr, ctxt);
        } else if(expr instanceof CompositePath) {
            return visit((CompositePath) expr, ctxt);
        } else if(expr instanceof ContextItemExpr) {
            return visit((ContextItemExpr) expr, ctxt);
        } else if(expr instanceof DirectFunctionCall) {
            return visit((DirectFunctionCall) expr, ctxt);
        } else if(expr instanceof DistinctSortExpr) {
            return visit((DistinctSortExpr) expr, ctxt);
        } else if(expr instanceof DocConstructor) {
            return visit((DocConstructor) expr, ctxt);
        } else if(expr instanceof ElementConstructor) {
            return visit((ElementConstructor) expr, ctxt);
        } else if(expr instanceof ExtensionExpr) {
            return visit((ExtensionExpr) expr, ctxt);
        } else if(expr instanceof ExternalVariable) {
            return visit((ExternalVariable) expr, ctxt);
        } else if(expr instanceof FilterExpr) {
            return visit((FilterExpr) expr, ctxt);
        } else if(expr instanceof FLWRExpr) {
            return visit((FLWRExpr) expr, ctxt);
        } else if(expr instanceof ForClause) {
            return visit((ForClause) expr, ctxt);
        } else if(expr instanceof FunctionCall) {
            return visit((FunctionCall) expr, ctxt);
        } else if(expr instanceof IfExpr) {
            return visit((IfExpr) expr, ctxt);
        } else if(expr instanceof InstanceofOp) {
            return visit((InstanceofOp) expr, ctxt);
        } else if(expr instanceof Instruction) {
            return visit((Instruction) expr, ctxt);
        } else if(expr instanceof LetClause) {
            return visit((LetClause) expr, ctxt);
        } else if(expr instanceof LiteralExpr) {
            return visit((LiteralExpr) expr, ctxt);
        } else if(expr instanceof MultiplicativeExpr) {
            return visit((MultiplicativeExpr) expr, ctxt);
        } else if(expr instanceof NamespaceConstructor) {
            return visit((NamespaceConstructor) expr, ctxt);
        } else if(expr instanceof NegativeExpr) {
            return visit((NegativeExpr) expr, ctxt);
        } else if(expr instanceof NodeTest) {
            return visit((NodeTest) expr, ctxt);
        } else if(expr instanceof OrderSpec) {
            return visit((OrderSpec) expr, ctxt);
        } else if(expr instanceof OrExpr) {
            return visit((OrExpr) expr, ctxt);
        } else if(expr instanceof ParenthesizedExpr) {
            return visit((ParenthesizedExpr) expr, ctxt);
        } else if(expr instanceof PathExpr) {
            return visit((PathExpr) expr, ctxt);
        } else if(expr instanceof PathVariable) {
            return visit((PathVariable) expr, ctxt);
        } else if(expr instanceof PIConstructor) {
            return visit((PIConstructor) expr, ctxt);
        } else if(expr instanceof PreEvaluatedVariable) {
            return visit((PreEvaluatedVariable) expr, ctxt);
        } else if(expr instanceof PromoteJoinExpression) {
            return visit((PromoteJoinExpression) expr, ctxt);
        } else if(expr instanceof QuantifiedExpr) {
            return visit((QuantifiedExpr) expr, ctxt);
        } else if(expr instanceof SequenceExpression) {
            return visit((SequenceExpression) expr, ctxt);
        } else if(expr instanceof SequenceOp) {
            return visit((SequenceOp) expr, ctxt);
        } else if(expr instanceof TextConstructor) {
            return visit((TextConstructor) expr, ctxt);
        } else if(expr instanceof TreatExpr) {
            return visit((TreatExpr) expr, ctxt);
        } else if(expr instanceof TypePromotedExpr) {
            return visit((TypePromotedExpr) expr, ctxt);
        } else if(expr instanceof TypeswitchExpr) {
            return visit((TypeswitchExpr) expr, ctxt);
        } else if(expr instanceof UnionOp) {
            return visit((UnionOp) expr, ctxt);
        } else if(expr instanceof UnorderedExpr) {
            return visit((UnorderedExpr) expr, ctxt);
        } else if(expr instanceof UserFunction) {
            return visit((UserFunction) expr, ctxt);
        } else if(expr instanceof ValidateOp) {
            return visit((ValidateOp) expr, ctxt);
        } else if(expr instanceof Variable) {
            return visit((Variable) expr, ctxt);
        } else if(expr instanceof VarRef) {
            return visit((VarRef) expr, ctxt);
        } else if(expr instanceof BDQExpr) {
            return visit((BDQExpr) expr, ctxt);
        } else if(expr instanceof XQueryModule) {
            return visit((XQueryModule) expr, ctxt);
        } else {
            throw new IllegalStateException("Unexpected expression: " + expr.getClass().getName());
        }
    }

    private void removeInvariants(XQExpression expr) {
        ExpressionProxy removed = _invarients.remove(expr);
    }

    private XQExpression hookLoopInvariant(XQExpression expr) {
        if(_protectHook) {
            expr.setLoopInvariant(true);
            return expr;
        }
        ExpressionProxy proxy = new ExpressionProxy(expr);
        proxy.setLoopInvariant(true);
        _invarients.put(expr, proxy);
        return proxy;
    }

    @Deprecated
    public void transform() {//TODO
        _flwr.setTransformed(true);

        List<Binding> clauses = _flwr.getClauses();
        int clauselen = clauses.size();
        int insertionPoint = 0;
        for(int i = 0; i < clauselen; i++) {
            Binding binding = clauses.get(i);
            if(binding == _forClause) {
                insertionPoint = i;
                break;
            }
        }

        if(!_pullup.isEmpty()) {
            final Set<Entry<LetClause, List<Binding>>> entries = _pullup.entrySet();
            for(Entry<LetClause, List<Binding>> e : entries) {
                final LetClause lc = e.getKey();
                clauses.add(insertionPoint, lc);
                final List<Binding> srcClauses = e.getValue();
                srcClauses.remove(lc);
            }
            _pullup.clear();
        }

        final Collection<ExpressionProxy> proxys = _invarients.values();
        if(!proxys.isEmpty()) {
            for(ExpressionProxy proxy : proxys) {
                final XQExpression orig = proxy.getOriginalExpr();
                final LetVariable lv = new LoopInvariantLetVariable(orig);
                proxy.setReplacedExpr(lv);
                final LetClause lc = new LetClause(lv);
                clauses.add(insertionPoint, lc);
            }
            _invarients.clear();
        }
    }

}
