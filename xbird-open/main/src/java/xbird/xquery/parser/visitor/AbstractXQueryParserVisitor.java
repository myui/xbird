/*
 * @(#)$Id: AbstractXQueryParserVisitor.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.parser.visitor;

import java.util.List;

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
import xbird.xquery.expr.var.BindingVariable.ParametricVariable;
import xbird.xquery.expr.var.Variable.ExternalVariable;
import xbird.xquery.expr.var.Variable.PreEvaluatedVariable;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.UserFunction;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class AbstractXQueryParserVisitor implements XQueryParserVisitor {

    public XQExpression visit(AdditiveExpr op, XQueryContext ctxt) throws XQueryException {
        XQExpression left = op.getLeftOperand();
        left.visit(this, ctxt);
        XQExpression right = op.getRightOperand();
        right.visit(this, ctxt);
        return op;
    }

    public XQExpression visit(AndExpr expr, XQueryContext ctxt) throws XQueryException {
        for(XQExpression e : expr.getExpressions()) {
            e.visit(this, ctxt);
        }
        return expr;
    }

    public XQExpression visit(AttributeConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        final QualifiedName name = constructor.getName();
        if(name == null) {
            XQExpression nameExpr = constructor.getNameExpr();
            nameExpr.visit(this, ctxt);
        }
        for(XQExpression v : constructor.getValueExprs()) {
            v.visit(this, ctxt);
        }
        return constructor;
    }

    public XQExpression visit(AxisStep step, XQueryContext ctxt) throws XQueryException {
        NodeTest nt = step.getNodeTest();
        assert (nt != null);
        nt.visit(this, ctxt);
        return step;
    }

    public XQExpression visit(BindingVariable variable, XQueryContext ctxt) throws XQueryException {
        return variable;
    }

    public XQExpression visit(BuiltInFunction function, XQueryContext ctxt) throws XQueryException {
        return null;
    }

    public XQExpression visit(CaseClause clause, XQueryContext ctxt) throws XQueryException {
        XQExpression e = clause.getReturnExpr();
        e.visit(this, ctxt);
        return clause;
    }

    public XQExpression visit(CastableExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression e = expr.getExpression();
        e.visit(this, ctxt);
        return expr;
    }

    public XQExpression visit(CastExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression e = expr.getExpression();
        e.visit(this, ctxt);
        return expr;
    }

    public XQExpression visit(CommentConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        final String content = constructor.getContent();
        if(content == null) {
            XQExpression contentExpr = constructor.getContentExpr();
            assert (contentExpr != null);
            contentExpr.visit(this, ctxt);
        }
        return constructor;
    }

    public XQExpression visit(ComparisonOp comp, XQueryContext ctxt) throws XQueryException {
        XQExpression left = comp.getLeftOperand();
        left.visit(this, ctxt);
        XQExpression right = comp.getRightOperand();
        right.visit(this, ctxt);
        return comp;
    }

    public XQExpression visit(CompositePath fragment, XQueryContext ctxt) throws XQueryException {
        XQExpression src = fragment.getSourceExpr();
        src.visit(this, ctxt);
        XQExpression filter = fragment.getFilterExpr();
        filter.visit(this, ctxt);
        return fragment;
    }

    public XQExpression visit(ContextItemExpr expr, XQueryContext ctxt) throws XQueryException {
        return expr;
    }

    public XQExpression visit(DirectFunctionCall call, XQueryContext ctxt) throws XQueryException {
        for(XQExpression p : call.getParams()) {
            p.visit(this, ctxt);
        }
        return call;
    }

    public XQExpression visit(DistinctSortExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression step = expr.getStepExpr();
        assert (step != null);
        step.visit(this, ctxt);
        return expr;
    }

    public XQExpression visit(DocConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        final XQExpression expr = constructor.getExpr();
        expr.visit(this, ctxt);
        return constructor;
    }

    public XQExpression visit(ElementConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        final String name = constructor.getElemName();
        if(name == null) {
            final XQExpression nameExpr = constructor.getNameExpr();
            nameExpr.visit(this, ctxt);
        }
        for(AttributeConstructorBase att : constructor.getAttributes()) {
            att.visit(this, ctxt);
        }
        for(XQExpression v : constructor.getContents()) {
            v.visit(this, ctxt);
        }
        return constructor;
    }

    public XQExpression visit(ExtensionExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression e = expr.getExpr();
        e.visit(this, ctxt);
        return expr;
    }

    public XQExpression visit(ExpressionProxy proxy, XQueryContext ctxt) throws XQueryException {
        XQExpression e = proxy.getAppropriateExpr();
        e.visit(this, ctxt);
        return proxy;
    }

    public XQExpression visit(ExternalVariable var, XQueryContext ctxt) throws XQueryException {
        return var;
    }

    public XQExpression visit(FilterExpr expr, XQueryContext ctxt) throws XQueryException {
        final XQExpression e = expr.getSourceExpr();
        e.visit(this, ctxt);
        for(XQExpression p : expr.getPredicates()) {
            p.visit(this, ctxt);
        }
        return expr;
    }

    public XQExpression visit(FLWRExpr expr, XQueryContext ctxt) throws XQueryException {
        // #1 for loop
        List<Binding> bindings = expr.getClauses();
        int bingingLen = bindings.size();
        for(int i = 0; i < bingingLen; i++) {
            Binding b = bindings.get(i);
            b.visit(this, ctxt);
        }
        // #2 return
        XQExpression filteredRet = expr.getFilteredReturnExpr();
        if(filteredRet != null) {
            filteredRet.visit(this, ctxt);
        } else {
            // #2-1 where filtering
            XQExpression where = expr.getWhereExpr();
            if(where != null) {
                where.visit(this, ctxt);
            }
            // #2-2 return
            XQExpression ret = expr.getReturnExpr();
            assert (ret != null);
            ret.visit(this, ctxt);
        }
        // #3 order by sorting
        for(OrderSpec o : expr.getOrderSpecs()) {
            o.visit(this, ctxt);
        }
        return expr;
    }

    public ForClause visit(ForClause clause, XQueryContext ctxt) throws XQueryException {
        XQExpression e = clause.getInExpr();
        e.visit(this, ctxt);
        return clause;
    }

    public XQExpression visit(FunctionCall call, XQueryContext ctxt) throws XQueryException {
        for(XQExpression p : call.getParams()) {
            p.visit(this, ctxt);
        }
        return call;
    }

    public XQExpression visit(GroupingSpec spec, XQueryContext ctxt) throws XQueryException {
        Variable var = spec.getKeyExpr();
        var.visit(this, ctxt);
        return spec;
    }

    public XQExpression visit(IfExpr expr, XQueryContext ctxt) throws XQueryException {
        // if
        XQExpression cond = expr.getCondExpr();
        cond.visit(this, ctxt);
        // then
        XQExpression then = expr.getThenExpr();
        then.visit(this, ctxt);
        // else
        XQExpression els = expr.getElseExpr();
        els.visit(this, ctxt);
        return expr;
    }

    public XQExpression visit(InstanceofOp op, XQueryContext ctxt) throws XQueryException {
        XQExpression e = op.getExpression();
        e.visit(this, ctxt);
        return op;
    }

    public XQExpression visit(Instruction instruction, XQueryContext ctxt) throws XQueryException {
        return instruction;
    }

    public XQExpression visit(LazyFunctionCall call, XQueryContext ctxt) throws XQueryException {
        this.visit((FunctionCall) call, ctxt);
        return call;
    }

    public LetClause visit(LetClause clause, XQueryContext ctxt) throws XQueryException {
        XQExpression e = clause.getExpression();
        e.visit(this, ctxt);
        return clause;
    }

    public XQExpression visit(LiteralExpr expr, XQueryContext ctxt) throws XQueryException {
        return expr;
    }

    public XQExpression visit(MultiplicativeExpr op, XQueryContext ctxt) throws XQueryException {
        XQExpression left = op.getLeftOperand();
        left.visit(this, ctxt);
        XQExpression right = op.getRightOperand();
        right.visit(this, ctxt);
        return op;
    }

    public XQExpression visit(NamespaceConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        return constructor;
    }

    public XQExpression visit(NegativeExpr op, XQueryContext ctxt) throws XQueryException {
        XQExpression e = op.getExpression();
        e.visit(this, ctxt);
        return op;
    }

    public XQExpression visit(NodeTest nt, XQueryContext ctxt) throws XQueryException {
        return nt;
    }

    public XQExpression visit(OrderSpec spec, XQueryContext ctxt) throws XQueryException {
        XQExpression e = spec.getKeyExpr();
        e.visit(this, ctxt);
        return spec;
    }

    public XQExpression visit(OrExpr expr, XQueryContext ctxt) throws XQueryException {
        for(XQExpression e : expr.getExpressions()) {
            e.visit(this, ctxt);
        }
        return expr;
    }

    public XQExpression visit(ParametricVariable variable, XQueryContext ctxt)
            throws XQueryException {
        this.visit((Variable) variable, ctxt);
        return variable;
    }

    public XQExpression visit(ParenthesizedExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression e = expr.getExpr();
        e.visit(this, ctxt);
        return expr;
    }

    public XQExpression visit(PathExpr path, XQueryContext ctxt) throws XQueryException {
        for(XQExpression s : path.getSteps()) {
            s.visit(this, ctxt);
        }
        return path;
    }

    public XQExpression visit(PathIndexAccessExpr variable, XQueryContext ctxt)
            throws XQueryException {
        return variable;
    }

    public XQExpression visit(PathVariable variable, XQueryContext ctxt) throws XQueryException {
        XQExpression e = variable.getValue();
        e.visit(this, ctxt);
        return variable;
    }

    public XQExpression visit(PIConstructor constructor, XQueryContext ctxt) throws XQueryException {
        final String target = constructor.getTarget();
        if(target == null) {
            constructor.getTargetExpr().visit(this, ctxt);
        }
        final String content = constructor.getContent();
        if(content == null) {
            constructor.getContentExpr().visit(this, ctxt);
        }
        return constructor;
    }

    public XQExpression visit(PreEvaluatedVariable variable, XQueryContext ctxt)
            throws XQueryException {
        return variable;
    }

    public XQExpression visit(PromoteJoinExpression joinExpr, XQueryContext ctxt)
            throws XQueryException {
        final XQExpression srcExpr = joinExpr.getSrcExpr();
        srcExpr.visit(this, ctxt);
        final List<XQExpression> pkeyExprs = joinExpr.getPersistKeyExprs();
        for(XQExpression pkeyExpr : pkeyExprs) {
            pkeyExpr.visit(this, ctxt);
        }
        final List<XQExpression> skeyExprs = joinExpr.getSearchKeyExprs();
        for(XQExpression skeyExpr : skeyExprs) {
            skeyExpr.visit(this, ctxt);
        }
        final List<XQExpression> filterExprs = joinExpr.getFilterExprs();
        for(XQExpression filter : filterExprs) {
            filter.visit(this, ctxt);
        }
        return joinExpr;
    }

    public XQExpression visit(QuantifiedExpr expr, XQueryContext ctxt) throws XQueryException {
        BindingVariable binding = expr.getBinding();
        XQExpression inExpr = binding.getValue();
        assert (inExpr != null);
        inExpr.visit(this, ctxt);

        XQExpression cond = expr.getCondExpr();
        cond.visit(this, ctxt);

        return expr;
    }

    public XQExpression visit(RecursiveCall call, XQueryContext ctxt) throws XQueryException {
        visit((FunctionCall) call, ctxt);
        return call;
    }

    public XQExpression visit(SequenceExpression expr, XQueryContext ctxt) throws XQueryException {
        for(XQExpression e : expr.getExpressions()) {
            e.visit(this, ctxt);
        }
        return expr;
    }

    public XQExpression visit(SequenceOp op, XQueryContext ctxt) throws XQueryException {
        XQExpression left = op.getLeftOperand();
        left.visit(this, ctxt);
        XQExpression right = op.getRightOperand();
        right.visit(this, ctxt);
        return op;
    }

    public XQExpression visit(TextConstructor constructor, XQueryContext ctxt)
            throws XQueryException {
        XQExpression content = constructor.getContent();
        content.visit(this, ctxt);
        return constructor;
    }

    public XQExpression visit(TreatExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression e = expr.getExpression();
        e.visit(this, ctxt);
        return expr;
    }

    public XQExpression visit(TypePromotedExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression e = expr.getPromotedExpr();
        e.visit(this, ctxt);
        return expr;
    }

    public XQExpression visit(TypeswitchExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression opr = expr.getOperandExpr();
        opr.visit(this, ctxt);
        for(XQExpression c : expr.getCaseClauses()) {
            c.visit(this, ctxt);
        }
        XQExpression def = expr.getDefaultClause();
        if(def != null) {
            def.visit(this, ctxt);
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
        return expr;
    }

    public XQExpression visit(UserFunction function, XQueryContext ctxt) throws XQueryException {
        XQExpression body = function.getBodyExpression();
        body.visit(this, ctxt);
        return null;
    }

    public XQExpression visit(ValidateOp op, XQueryContext ctxt) throws XQueryException {
        XQExpression e = op.getExpression();
        e.visit(this, ctxt);
        return op;
    }

    public XQExpression visit(Variable variable, XQueryContext ctxt) throws XQueryException {
        if(variable.getResult() == null) {
            XQExpression e = variable.getValue();
            //assert (e != null) : "Expression is not binded for variable '$" + variable.getName() + "'";
            if(e != null) {
                e.visit(this, ctxt);
            }
        }
        return variable;
    }

    public XQExpression visit(VarRef ref, XQueryContext ctxt) throws XQueryException {
        Variable var = ref.getValue();
        assert (var != null) : ref;
        var.visit(this, ctxt);
        return ref;
    }

    public XQExpression visit(BDQExpr expr, XQueryContext ctxt) throws XQueryException {
        XQExpression body = expr.getBodyExpression();
        body.visit(this, ctxt);
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
        } else if(expr instanceof PathIndexAccessExpr) {
            return visit((PathIndexAccessExpr) expr, ctxt);
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

}
