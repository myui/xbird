/*
 * @(#)$Id: ExtensionExpr.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.decorative;

import java.util.LinkedList;
import java.util.List;

import xbird.xquery.Pragma;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.ext.BDQExpr;
import xbird.xquery.expr.ext.MapExpr;
import xbird.xquery.expr.flwr.Binding;
import xbird.xquery.expr.flwr.FLWRExpr;
import xbird.xquery.expr.flwr.ForClause;
import xbird.xquery.expr.func.DirectFunctionCall;
import xbird.xquery.expr.path.PathExpr;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.expr.var.VarRef;
import xbird.xquery.expr.var.BindingVariable.ForVariable;
import xbird.xquery.func.doc.FnCollection;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ExtensionExpr extends AbstractXQExpression implements DecorativeExpression {
    private static final long serialVersionUID = 7078273458388928255L;

    private final List<Pragma> pragmas;
    private final XQExpression expr;

    public ExtensionExpr(XQExpression expr, Pragma pragma) {
        this(expr, new LinkedList<Pragma>());
        pragmas.add(pragma);
    }

    public ExtensionExpr(XQExpression expr, List<Pragma> pragmas) {
        this.expr = expr;
        this.pragmas = pragmas;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression getExpr() {
        return this.expr;
    }

    public List<Pragma> getPragmas() {
        return this.pragmas;
    }

    public void addPragma(Pragma pragma) {
        pragmas.add(pragma);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        assert (!_analyzed);
        if(!pragmas.isEmpty()) {
            if(expr instanceof BDQExpr) {
                for(Pragma p : pragmas) {
                    if("parallel".equals(p.getName().getLocalPart())) {
                        ((BDQExpr) expr).setParallel(true);
                        return expr.staticAnalysis(statEnv);
                    }
                }
            } else if(expr instanceof FLWRExpr) {
                FLWRExpr flwrExpr = ((FLWRExpr) expr);
                List<Binding> clauses = flwrExpr.getClauses();
                if(!clauses.isEmpty()) {
                    Binding firstClause = clauses.get(0);
                    BindingVariable bindingVar = firstClause.getVariable();
                    XQExpression bindingExpr = bindingVar.getValue();
                    if(bindingExpr instanceof DirectFunctionCall) {
                        DirectFunctionCall funcall = (DirectFunctionCall) bindingExpr;
                        if(FnCollection.FUNC_NAME.equals(funcall.getFuncName())) {
                            List<XQExpression> params = funcall.getParams();
                            final String colpath;
                            if(params.isEmpty()) {
                                colpath = "/";
                            } else {
                                XQExpression argExpr = params.get(0);
                                colpath = argExpr.eval(null, DynamicContext.DUMMY).toString();
                            }
                            MapExpr mapExpr = new MapExpr(colpath, bindingVar, flwrExpr);
                            return mapExpr.staticAnalysis(statEnv);
                        }
                    }
                }
            } else if(expr instanceof PathExpr) {
                PathExpr pathExpr = (PathExpr) expr;
                List<XQExpression> steps = pathExpr.getSteps();
                if(steps.size() > 1) {
                    XQExpression firstStep = steps.get(0);
                    if(firstStep instanceof DirectFunctionCall) {
                        DirectFunctionCall funcall = (DirectFunctionCall) firstStep;
                        if(FnCollection.FUNC_NAME.equals(funcall.getFuncName())) {
                            List<XQExpression> params = funcall.getParams();
                            final String colpath;
                            if(params.isEmpty()) {
                                colpath = "/";
                            } else {
                                XQExpression argExpr = params.get(0);
                                colpath = argExpr.eval(null, DynamicContext.DUMMY).toString();
                            }

                            FLWRExpr newFlwr = new FLWRExpr();
                            ForVariable forVar = new ForVariable();

                            forVar.setValue(funcall);
                            newFlwr.addClause(new ForClause(forVar));
                            VarRef varref = new VarRef(forVar);
                            steps.set(0, varref);
                            newFlwr.setReturnExpr(pathExpr);

                            MapExpr mapExpr = new MapExpr(colpath, forVar, newFlwr);
                            return mapExpr.staticAnalysis(statEnv);
                        }
                    }
                }
            }
        }
        return expr.staticAnalysis(statEnv);
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        throw new IllegalStateException("ExtensionExpr should be pruned.");
    }
}
