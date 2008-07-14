/*
 * @(#)$Id: XQueryParserVisitor.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.parser;

import xbird.xquery.XQueryException;
import xbird.xquery.XQueryModule;
import xbird.xquery.expr.LiteralExpr;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.comp.ComparisonOp;
import xbird.xquery.expr.cond.IfExpr;
import xbird.xquery.expr.cond.QuantifiedExpr;
import xbird.xquery.expr.constructor.AttributeConstructor;
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
import xbird.xquery.expr.flwr.FLWRExpr;
import xbird.xquery.expr.flwr.ForClause;
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

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public interface XQueryParserVisitor {

    public XQExpression visit(AdditiveExpr op, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(AndExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(AttributeConstructor constructor, XQueryContext ctxt)
            throws XQueryException;

    public XQExpression visit(AxisStep step, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(BindingVariable variable, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(BuiltInFunction function, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(CaseClause clause, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(CastableExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(CastExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(CommentConstructor constructor, XQueryContext ctxt)
            throws XQueryException;

    public XQExpression visit(ComparisonOp comp, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(CompositePath fragment, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(ContextItemExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(DirectFunctionCall call, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(DistinctSortExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(DocConstructor constructor, XQueryContext ctxt)
            throws XQueryException;

    public XQExpression visit(ElementConstructor constructor, XQueryContext ctxt)
            throws XQueryException;

    public XQExpression visit(ExpressionProxy proxy, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(ExtensionExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(ExternalVariable var, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(FilterExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(FLWRExpr expr, XQueryContext ctxt) throws XQueryException;

    public ForClause visit(ForClause clause, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(FunctionCall call, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(IfExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(InstanceofOp op, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(Instruction instruction, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(LazyFunctionCall call, XQueryContext ctxt) throws XQueryException;

    public LetClause visit(LetClause clause, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(LiteralExpr expression, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(MultiplicativeExpr op, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(NamespaceConstructor constructor, XQueryContext ctxt)
            throws XQueryException;

    public XQExpression visit(NegativeExpr op, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(NodeTest test, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(OrderSpec spec, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(OrExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(ParametricVariable variable, XQueryContext ctxt)
            throws XQueryException;

    public XQExpression visit(ParenthesizedExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(PathExpr path, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(PathIndexAccessExpr variable, XQueryContext ctxt)
            throws XQueryException;

    public XQExpression visit(PathVariable variable, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(PIConstructor constructor, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(PreEvaluatedVariable variable, XQueryContext ctxt)
            throws XQueryException;

    public XQExpression visit(PromoteJoinExpression expression, XQueryContext ctxt)
            throws XQueryException;

    public XQExpression visit(QuantifiedExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(RecursiveCall call, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(SequenceExpression expression, XQueryContext ctxt)
            throws XQueryException;

    public XQExpression visit(SequenceOp op, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(TextConstructor constructor, XQueryContext ctxt)
            throws XQueryException;

    public XQExpression visit(TreatExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(TypePromotedExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(TypeswitchExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(UnionOp op, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(UnorderedExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(UserFunction function, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(ValidateOp op, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(Variable variable, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(VarRef ref, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(BDQExpr expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(XQExpression expr, XQueryContext ctxt) throws XQueryException;

    public XQExpression visit(XQueryModule module, XQueryContext ctxt) throws XQueryException;

}
