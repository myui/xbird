/*
 * @(#)$Id: FLWORArranger.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.*;

import xbird.xquery.XQueryException;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.comp.ComparisonOp;
import xbird.xquery.expr.comp.GeneralComp;
import xbird.xquery.expr.cond.IfExpr;
import xbird.xquery.expr.dyna.ContextItemExpr;
import xbird.xquery.expr.flwr.*;
import xbird.xquery.expr.func.DirectFunctionCall;
import xbird.xquery.expr.func.RecursiveCall;
import xbird.xquery.expr.logical.AndExpr;
import xbird.xquery.expr.opt.PathVariable;
import xbird.xquery.expr.opt.TypePromotedExpr;
import xbird.xquery.expr.path.*;
import xbird.xquery.expr.path.PathExpr.CompositePath;
import xbird.xquery.expr.path.axis.SelfStep;
import xbird.xquery.expr.var.*;
import xbird.xquery.expr.var.BindingVariable.*;
import xbird.xquery.func.Function;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.func.context.Position;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.parser.visitor.AbstractXQueryParserVisitor;
import xbird.xquery.type.Type;
import xbird.xquery.type.Type.Occurrence;
import xbird.xquery.type.xs.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class FLWORArranger {

    private FLWORArranger() {}

    public static Map<Binding, XQExpression> getDependentInWhereExpr(final FLWRExpr flwr, final List<XQExpression> nodeps)
            throws XQueryException {
        final XQExpression whereExpr = flwr.getWhereExpr();
        if(whereExpr == null) {
            return Collections.<Binding, XQExpression> emptyMap();
        }
        final List<Binding> clauses = flwr.getClauses();
        // try to split 'and' expr
        final Map<Binding, XQExpression> dependancies = new IdentityHashMap<Binding, XQExpression>(4);
        final DependancyChecker dependancyChecker = new DependancyChecker(clauses);
        if(whereExpr instanceof AndExpr) {
            final List<XQExpression> andExprs = ((AndExpr) whereExpr).getExpressions();
            for(final XQExpression andExpr : andExprs) {
                andExpr.visit(dependancyChecker, null);
                if(dependancyChecker.hasDependancy()) {
                    final Binding dep = dependancyChecker.getDependent();
                    if(dependancies.containsKey(dep)) {
                        final AndExpr e = new AndExpr(dependancies.get(dep), andExpr);
                        e.copyLocations(andExpr);
                        dependancies.put(dep, e);
                    } else {
                        dependancies.put(dep, andExpr);
                    }
                } else {
                    nodeps.add(andExpr);
                }
            }
        } else {
            whereExpr.visit(dependancyChecker, null);
            if(dependancyChecker.hasDependancy()) {
                dependancies.put(dependancyChecker.getDependent(), whereExpr);
            } else {
                nodeps.add(whereExpr);
            }
        }
        return dependancies;
    }

    /**
     * Apply 'for' bindings normalization.
     * <pre>
     * <b>[For unnesting]</b>
     * Before normalization:
     *      for $a in /aaa, $b in /bbb, $c in /ccc
     *      return ($a, $b, $c)
     *
     * After normalization:
     *      for $a in /aaa
     *      return (
     *          for $b in /bbb
     *          return (
     *              for $c in /ccc
     *              return ($a, $b, $c)
     *          )
     *      )
     * 
     * - eg#1 has dependancies
     * Before normalization
     *      for $a in /aaa, $b in /bbb, $c in /ccc 
     *      where $b = $c
     *      return ($a, $b, $c)
     *   
     * After normalization
     *      for $a in /aaa
     *      return (
     *          for $b in /bbb          
     *          return (
     *              for $c in /ccc[. = $b]
     *              return ($a, $b, $c)
     *          )
     *      )
     * 
     * - eg#2 no dependancies
     * Before normalization
     *      let $d := $doc/ddd
     *      for $a in /aaa, $b in /bbb, $c in /ccc 
     *      where $d = 3
     *      return ($a, $b, $c)
     *   
     * After normalization
     *      if ($d = 3) then                -- pull up to cond
     *          let $d := $doc/ddd
     *          for $a in /aaa
     *          return (
     *              for $b in /bbb
     *              return (
     *                  for $c in /ccc
     *                  return ($a, $b, $c)
     *              )
     *          )
     *      else ()
     *      
     * - eg#3 has multiple(in this case 2) dependancies
     * Before normalization
     *      let $d := $doc/ddd
     *      for $a in /aaa, $b in /bbb, $c in /ccc 
     *      where $d = 3 and $c = $b        -- `and` expression is optimized (or is not).
     *      return ($a, $b, $c)
     *   
     * After normalization
     *      if ($d = 3) then                -- pull up to cond
     *          let $d := $doc/ddd
     *          for $a in /aaa
     *          return (
     *              for $b in /bbb
     *              return (
     *                  for $c in /ccc       
     *                  where $c = $b           -- put to right position
     *                  return ($a, $b, $c)
     *              )
     *          )
     *     else ()
     *      
     * -- eg#4 for + where case(no dep)
     * Before normalization
     *      for $d in $doc/ddd
     *      where $e = 3
     *      return $d
     *      
     * After normalization
     *      if ($e = 3) then
     *          for $d in $doc/ddd
     *          return $d
     *      else ()
     * </pre>
     */
    public static XQExpression applyForNormalization(final FLWRExpr flwr, final Map<Binding, XQExpression> dependancies)
            throws XQueryException {
        assert (flwr != null);
        final List<Binding> clauses = flwr.getClauses();
        // apply for unnesting
        int size;
        for(int i = 0; i < ((size = clauses.size()) - 1); i++) {
            final Binding first = clauses.get(i);
            if(first.getExpressionType() == Binding.FOR_CLAUSE) {
                while((i + 1) < size) { // find next 'for'
                    final Binding second = clauses.get(i + 1);
                    if(second.getExpressionType() == Binding.FOR_CLAUSE) { // found nesting
                        final FLWRExpr parent = new FLWRExpr();
                        if(dependancies.containsKey(second)) {
                            final XQExpression old = flwr.getWhereExpr();
                            if(old != null) {
                                dependancies.put(first, old);
                            }
                            flwr.setWhereExpr(dependancies.remove(second));
                        } else {
                            flwr.setWhereExpr(null);
                        }
                        final List<OrderSpec> specs = flwr.getOrderSpecs();
                        if(!specs.isEmpty()) {
                            parent.setOrderSpecs(specs);
                            flwr.setOrderSpecs(Collections.<OrderSpec> emptyList());
                        }
                        for(int j = 0; j <= i; j++) {
                            final Binding b = clauses.remove(0);
                            if(dependancies.containsKey(b)) { // copy depended where clauses
                                parent.addWhereExpr(dependancies.remove(b));
                            }
                            parent.addClause(b); // copy for, let.. to be parent
                        }
                        if(size > (i + 2)) {
                            final XQExpression forUnnested = applyForNormalization(flwr, dependancies);
                            parent.setReturnExpr(forUnnested);
                        } else { // inner most
                            final XQExpression transformed = transform(flwr, dependancies);
                            parent.setReturnExpr(transformed);
                        }
                        return transform(parent, dependancies);
                    } else {
                        i++;
                    }
                }
                break;
            } else {
                final FLWRExpr letUnnested = applyLetWhereToIfCondConversion(flwr, dependancies);
                if(letUnnested != flwr) {
                    return transform(letUnnested, dependancies);
                }
            }
        }
        if(!dependancies.isEmpty()) {
            for(XQExpression expr : dependancies.values()) {
                flwr.addWhereExpr(expr);
            }
            dependancies.clear();
        }
        return transform(flwr, dependancies);
    }

    /**
     * Convert where clause with If Conditional expression.
     * <pre>
     * -- eg#1 has relation to binding variable
     * Before:
     *      for $i in /aaa
     *      where $i/bbb > 0
     *      return $r
     * 
     * After:
     *      for $i in /aaa
     *      return 
     *          if ($i/bbb > 0) then $r
     *          else ()
     * </pre>
     */
    private static void applyWhereToIfCondConversion(final FLWRExpr flwr) {
        if(flwr._filteredReturnExpr == null) {
            if(flwr.getWhereExpr() == null) {
                flwr._filteredReturnExpr = flwr.getReturnExpr();
            } else {
                flwr._filteredReturnExpr = new IfExpr(flwr.getWhereExpr(), flwr.getReturnExpr());
                flwr.setWhereExpr(null);
            }
            flwr.setReturnExpr(null);
        }
    }

    /**
     * Pull up IF conditional expr that does not have dependancies.
     * <pre>
     * -- eg#1 no decpendancies
     * Before:
     *      for $i in /aaa/bbb
     *      return 
     *          if $j = 3 then $r
     *          else ()
     *  
     * After:
     *      if ($j = 3) then
     *          for $i in /aaa/bbb
     *          return $r
     *      else ()
     * </pre>
     * @throws XQueryException
     * @depends {@link #applyWhereToIfCondConversion(FLWRExpr)}
     */
    private static XQExpression applyIfCondPullup(final FLWRExpr flwr) throws XQueryException {
        final XQExpression retExpr = flwr.getFilteredReturnExpr();
        if(retExpr instanceof IfExpr) {
            final DependancyChecker dependancyChecker = new DependancyChecker(flwr.getClauses());
            retExpr.visit(dependancyChecker, null);
            if(!dependancyChecker.hasDependancy()) {
                final IfExpr ifExpr = (IfExpr) retExpr;
                flwr._filteredReturnExpr = ifExpr.getThenExpr();
                flwr.setReturnExpr(null);
                ifExpr.setThenExpr(flwr);
                return ifExpr;
            }
        }
        return flwr;
    }

    /**
     * <pre>
     * -- eg#1
     * Before:
     *      for $i in /aaa/bbb return $i/ccc
     * After:
     *      /aaa/bbb/ccc
     * 
     * -- eg#2
     * Before:
     *      let $i := /aaa/bbb
     *      return $i/ccc
     * After:
     *      /aaa/bbb/ccc
     * 
     * -- eg#3
     * Before
     *      for $i in /aaa/bbb return $i
     * After
     *      /aaa/bbb
     *      
     * -- eg#3
     * Before
     *       for $b in a/b/c return $b/d[text()=$b/name]
     *       
     * After
     *       -- if $b's reference count is greater than 1, could not cut flwr.
     *       for $b in a/b/c return $b/d[text()=$b/name]
     * </pre>
     * @throws XQueryException 
     */
    private static XQExpression applyFLWRCutting(final FLWRExpr flwr) throws XQueryException {
        if(flwr.getWhereExpr() == null) {
            final XQExpression retExpr = flwr.getFilteredReturnExpr();
            if(retExpr instanceof DirectFunctionCall && !(retExpr instanceof RecursiveCall)) {
                final DirectFunctionCall funcall = (DirectFunctionCall) retExpr;
                final List<XQExpression> params = funcall.getParams();
                if(params.size() == 1) {
                    FunctionSignature sig = funcall.getFunction().getFunctionSignature(1);
                    Type type = sig.getArgumentType(0);
                    Occurrence occ = type.quantifier();
                    if(!occ.accepts(Occurrence.OCC_ZERO_OR_MORE.getAlignment())) {
                        return flwr; //TODO REVIEWME
                    }
                    final XQExpression firstArg = params.get(0);
                    final XQExpression cutted = recApplyFLWRCuttingInternal(flwr, firstArg);
                    if(cutted != flwr) {
                        return retExpr;
                    }
                }
            } else {
                return recApplyFLWRCuttingInternal(flwr, retExpr);
            }
        }
        return flwr;
    }

    private static XQExpression recApplyFLWRCuttingInternal(final FLWRExpr flwr, final XQExpression retExpr) {
        if(retExpr instanceof RelativePath) {//find the front item
            final RelativePath pathExpr = ((RelativePath) retExpr);
            final XQExpression firstStep = pathExpr.getSteps().get(0);
            if(firstStep instanceof VarRef) {
                final Variable referent = ((VarRef) firstStep).getValue();
                if(referent instanceof BindingVariable) {
                    final int refcnt = ((BindingVariable) referent).getReferenceCount();
                    if(refcnt == 1) {
                        final int csize = flwr.getClauses().size();
                        if(csize > 0) {
                            final Binding clause = flwr.getClauses().get(0);
                            final BindingVariable bindingVar = clause.getVariable();
                            if(bindingVar == referent) {
                                final XQExpression bindingExpr = bindingVar.getValue();
                                pathExpr.setStep(0, bindingExpr);
                                return pathExpr;
                            }
                        }
                    }
                }
            }
        } else if(retExpr instanceof VarRef) {
            final Variable referent = ((VarRef) retExpr).getValue();
            if(referent instanceof BindingVariable) {
                final int refcnt = ((BindingVariable) referent).getReferenceCount();
                if(refcnt == 1) {
                    for(Binding clause : flwr.getClauses()) {
                        final BindingVariable bindingVar = clause.getVariable();
                        if(bindingVar == referent) {
                            XQExpression bindingExpr = bindingVar.getValue();
                            Type type = bindingVar.getType();
                            if(type != Untyped.UNTYPED) {
                                return new TypePromotedExpr(bindingExpr, type);
                            } else {
                                return bindingExpr;
                            }
                        }
                    }
                }
            }
        }
        return flwr;
    }

    private static XQExpression applyRemoveUnnessaryLetClause(final FLWRExpr flwr) {
        assert ((flwr.getReturnExpr() == null) && (flwr.getWhereExpr() == null));
        assert (flwr._filteredReturnExpr != null);
        final List<Binding> clauses = flwr.getClauses();
        int csize = clauses.size();
        for(int i = 0; i < csize; i++) {
            final Binding binding = clauses.get(i);
            if(binding.getExpressionType() == Binding.LET_CLAUSE) {
                BindingVariable bv = binding.getVariable();
                final int refcnt = bv.getReferenceCount();
                if((refcnt == 0) || (refcnt == 1)) {
                    clauses.remove(i--);
                    --csize;
                }
            }
        }
        final int cleanAfter = clauses.size();
        if(cleanAfter == 0) {
            return flwr.getFilteredReturnExpr();
        }
        return flwr;
    }

    private static XQExpression applyFLWRFlatting(final FLWRExpr flwr) {
        if(!flwr.getOrderSpecs().isEmpty()) {
            return flwr;
        }
        final XQExpression ret = flwr.getFilteredReturnExpr();
        if(ret == null) {
            throw new IllegalStateException();
        }
        final List<Binding> clauses = flwr.getClauses();
        final int clen = clauses.size();
        if(clen == 0) {
            return ret;
        } else if(clen == 1) {
            final Binding firstBinding = clauses.get(0);
            if(ret instanceof VarRef) {
                return applyFLWRFlattingInternal((VarRef) ret, firstBinding, flwr);
            } else if(ret instanceof RelativePath) {
                final RelativePath path = (RelativePath) ret;
                final List<XQExpression> steps = path.getSteps();
                final XQExpression firstStep = steps.get(0);
                if(firstStep instanceof VarRef) {
                    final XQExpression cutted = applyFLWRFlattingInternal((VarRef) firstStep, firstBinding, flwr);
                    if(cutted != flwr) {
                        steps.set(0, cutted);
                        return path;
                    }
                }
            }
        }
        return flwr;
    }

    private static final XQExpression applyFLWRFlattingInternal(final VarRef ret, final Binding firstBinding, final FLWRExpr flwr) {
        final Variable referent = ret.getValue();
        final BindingVariable bindingVar = firstBinding.getVariable();
        if(referent == bindingVar) {
            return bindingVar.getValue();
        }
        final XQExpression bindingExpr = firstBinding.getExpression();
        if(referent == bindingExpr) {
            return bindingExpr;
        }
        return flwr;
    }

    /**
     * <pre>
     * -- eg#1
     * Before
     *      let $p = /aaa/bbb
     *      for $i in /ccc 
     *      return 
     *          for $j in /ddd
     *          return $p
     *      
     * After
     *      -- $p will be rewritten.
     *      for $i in /ccc
     *      return 
     *          for $j in /ddd
     *          return /aaa/bbb
     * </pre>
     */
    private static void applyConstantLetVariableFolding(final FLWRExpr flwr) {
        final List<Binding> clauses = flwr.getClauses();
        final int clen = clauses.size();
        if(clen > 0) {
            final Binding clause = clauses.get(0);
            final XQExpression bindingExpr = clause.getVariable().getValue();
            if(bindingExpr instanceof RelativePath) {
                final RelativePath pathExpr = ((RelativePath) bindingExpr);
                final XQExpression firstStep = pathExpr.getSteps().get(0);
                if(firstStep instanceof VarRef) {
                    final Variable referent = ((VarRef) firstStep).getValue();
                    if(referent instanceof LetVariable) {
                        final int refcnt = ((LetVariable) referent).getReferenceCount();
                        if(refcnt == 1) { // varref variable seems to be a constant.
                            final XQExpression refExpr = referent.getValue();
                            pathExpr.setStep(0, refExpr);
                        }
                    }
                }
            } else if(bindingExpr instanceof VarRef) {
                final Variable referent = ((VarRef) bindingExpr).getValue();
                if(referent instanceof LetVariable) {
                    final int refcnt = ((LetVariable) referent).getReferenceCount();
                    if(refcnt == 1) {
                        final XQExpression refExpr = referent.getValue();
                        final BindingVariable bv = clause.getVariable();
                        bv.setValue(refExpr);
                    }
                }
            }
        }
    }

    /**
     * <pre>
     * -- eg#1
     * Before:
     *      for $c in /ccc
     *      where $b = $c
     *      return ($a, $b, $c)
     * After:
     *      for $c in /ccc[. = $b]
     *      return ($a, $b, $c)
     * 
     * -- eg#2
     * Before:
     *      let $c := /ccc
     *      where $b = $c
     *      return ($a, $b, $c)
     * After:
     *      let $c in /ccc[. = $b]
     *      return ($a, $b, $c)
     *      
     * -- eg#3
     * Before:
     *      for $x in /aaa/bbb 
     *      where $x/ccc eq "zzz"
     *      return true
     * After:
     *      for $x in /aaa/bbb[ccc eq "zzz"]
     *      return true
     *      
     * -- eg#4
     * Before normalization
     *      for $a in /aaa at $pos where $pos = 2 return $a,
     *      for $a in /aaa where fn:position() = 2 return $a
     *      
     * After normalization
     *      for $a in /aaa[2] return $a
     *      
     * -- eg#5
     * Before:
     *      for $c in /aaa
     *      where $d = 3 and $c = $b
     *      return $a
     * 
     * After:
     *      if ($d = 3) then
     *          for $c in /aaa[. = $b]
     *          return $a
     *      else ()
     * </pre>
     * @param dependancies 
     */
    private static void applyWhereToPredicateConversion(final FLWRExpr flwr, final Map<Binding, XQExpression> dependancies)
            throws XQueryException {
        final XQExpression whereExpr = flwr.getWhereExpr();
        if(whereExpr == null) {
            return;
        }

        // first binding entries        
        final List<Binding> clauses = flwr.getClauses();
        final Binding firstBinding = clauses.get(0);
        final int firstBindingType = firstBinding.getExpressionType();
        final BindingVariable firstBindingVar = firstBinding.getVariable();
        XQExpression firstBindingExpr = firstBinding.getExpression();

        final List<XQExpression> whereExprs;
        if(whereExpr instanceof AndExpr) {
            whereExprs = ((AndExpr) whereExpr).flatten();
        } else {
            whereExprs = new ArrayList<XQExpression>(1);
            whereExprs.add(whereExpr);
        }
        int nWhereSize = whereExprs.size();
        for(int oi = 0; oi < nWhereSize; oi++) {
            final XQExpression eachExpr = whereExprs.get(oi);
            boolean innerModified = false;
            if(eachExpr instanceof ComparisonOp) {
                final ComparisonOp cmpOpr = (ComparisonOp) eachExpr;
                XQExpression replaceVarRef = null;
                inner: for(int ii = 0; ii < 2; ii++) {
                    final XQExpression left = cmpOpr.getLeftOperand();
                    final XQExpression right = cmpOpr.getRightOperand();
                    if(left instanceof VarRef) {
                        final Variable referent = ((VarRef) left).getValue();
                        if(firstBindingExpr instanceof PathExpr) {
                            final PathExpr bindingPathExpr = (PathExpr) firstBindingExpr;
                            if(firstBindingVar == referent) {
                                cmpOpr.setLeftOperand(new ContextItemExpr(bindingPathExpr.getType()));
                                bindingPathExpr.addPredicate(cmpOpr);
                                innerModified = true;
                                replaceVarRef = firstBindingVar;
                                break inner;
                            } else if((firstBindingType == Binding.FOR_CLAUSE)
                                    && (cmpOpr instanceof GeneralComp)) {
                                final PositionalVariable posVar = ((ForClause) firstBinding).getPositionVariable();
                                if(posVar == referent) {
                                    bindingPathExpr.addPredicate(right);
                                    innerModified = true;
                                    replaceVarRef = posVar;
                                    break inner;
                                }
                            }
                        }
                    } else if(left instanceof RelativePath) {
                        final RelativePath leftPathExpr = ((RelativePath) left);
                        final List<XQExpression> steps = leftPathExpr.getSteps();
                        final XQExpression firstStep = steps.get(0);
                        if(firstStep instanceof VarRef) {
                            final Variable referent = ((VarRef) firstStep).getValue();
                            if(firstBindingVar == referent) {
                                if(firstBindingExpr instanceof VarRef) {
                                    final Variable ref = ((VarRef) firstBindingExpr).getValue();
                                    final XQExpression refval = ref.getValue();
                                    if(refval instanceof PathExpr) {
                                        firstBindingExpr = refval;
                                    }
                                }
                                if(firstBindingExpr instanceof PathExpr) {
                                    final PathExpr bindingPathExpr = (PathExpr) firstBindingExpr;
                                    steps.remove(0);
                                    assert (!steps.isEmpty());
                                    bindingPathExpr.addPredicate(cmpOpr);
                                    innerModified = true;
                                    replaceVarRef = firstBindingVar;
                                    break inner;
                                }
                            } else if(ii == 1) {// delete an unnessesary where clause
                                innerModified = true;
                                break inner;
                            }
                        }
                    } else if(left instanceof DirectFunctionCall) {
                        final Function func = ((DirectFunctionCall) left).getFunction();
                        if(func instanceof Position) {
                            if(firstBindingExpr instanceof PathExpr) {
                                final PathExpr bindingPathExpr = (PathExpr) firstBindingExpr;
                                if(TypeUtil.subtypeOf(right.getType(), NumericType.getInstance())) {
                                    bindingPathExpr.addPredicate(right);
                                } else {
                                    TypePromotedExpr typePromoted = new TypePromotedExpr(right, IntegerType.INTEGER);
                                    bindingPathExpr.addPredicate(typePromoted);
                                }
                                innerModified = true;
                                break inner;
                            }
                        }
                    } else {
                        if(firstBindingExpr instanceof PathExpr) {
                            final PathExpr bindingPathExpr = (PathExpr) firstBindingExpr;
                            final VarRefDetector detector1 = new VarRefDetector(firstBindingVar, true);
                            detector1.visit(left, null);
                            if(detector1.isDetected()) {
                                final VarRefDetector detector2 = new VarRefDetector(firstBindingVar, false);
                                detector2.visit(right, null);
                                if(detector2.isJoinDisabled() || detector2.isDetected()) {
                                    break inner;
                                } else {
                                    bindingPathExpr.addPredicate(cmpOpr);
                                    innerModified = true;
                                    break inner;
                                }
                            }
                        }
                    }
                    cmpOpr.switchOperand();
                }//inner
                if(replaceVarRef != null) {
                    final PredicateReplacer replacer = new PredicateReplacer(replaceVarRef);
                    replacer.visit(cmpOpr, null);
                }
            }//outer
            if(innerModified) {
                whereExprs.remove(oi--);
                --nWhereSize;
            }
        }
        if(nWhereSize == 0) {
            flwr.setWhereExpr(null);
        } else if(nWhereSize == 1) {
            flwr.setWhereExpr(whereExprs.get(0));
        } else {
            flwr.setWhereExpr(new AndExpr(whereExprs));
        }
    }

    private static XQExpression transform(final FLWRExpr flwr, final Map<Binding, XQExpression> dependancies)
            throws XQueryException {
        applyWhereToPredicateConversion(flwr, dependancies);
        applyConstantLetVariableFolding(flwr);
        applyWhereToIfCondConversion(flwr);
        XQExpression normedRet = applyIfCondPullup(flwr);
        if(normedRet instanceof FLWRExpr) {
            normedRet = applyFLWRCutting((FLWRExpr) normedRet);
        }
        if(normedRet instanceof FLWRExpr) {
            normedRet = applyRemoveUnnessaryLetClause((FLWRExpr) normedRet);
        }
        if(normedRet instanceof FLWRExpr) {
            normedRet = applyFLWRFlatting((FLWRExpr) normedRet);
        }
        return normedRet;
    }

    /**
     * <pre>
     * -- eg#1
     * Before:
     *  let $i := 3
     *  for $j in /aaa/ccc
     *  where $i > 2
     *  return $j
     * 
     * After:
     *  let $i := 3
     *  return
     *      if $i > 2 then
     *          for $j in /aaa/ccc
     *          return $j
     *      else ()
     * </pre>
     */
    private static FLWRExpr applyLetWhereToIfCondConversion(final FLWRExpr flwr, final Map<Binding, XQExpression> dependancies)
            throws XQueryException {
        final List<Binding> clauses = flwr.getClauses();
        final int size = clauses.size();
        if(size > 0) {
            final Binding b = clauses.get(0);
            if(b.getExpressionType() == Binding.LET_CLAUSE) {
                if(dependancies.containsKey(b)) {
                    clauses.remove(0);
                    final FLWRExpr parent = new FLWRExpr();
                    final List<OrderSpec> specs = flwr.getOrderSpecs();
                    if(!specs.isEmpty()) {
                        parent.setOrderSpecs(specs);
                        flwr.setOrderSpecs(Collections.<OrderSpec> emptyList());
                    }
                    parent.addClause(b);
                    final XQExpression cond = dependancies.remove(b);
                    final XQExpression then = applyForNormalization(flwr, dependancies);
                    final IfExpr ifExpr = new IfExpr(cond, then);
                    parent.setReturnExpr(ifExpr);
                    return parent;
                } else {
                    final LetVariable lv = ((LetClause) b).getVariable();
                    final int refcnt = lv.getReferenceCount();
                    if((refcnt == 0) || (refcnt == 1)) {
                        clauses.remove(0);
                    }
                }
            }
        }
        return flwr;
    }

    private static final class VarRefDetector extends AbstractXQueryParserVisitor {
        private final BindingVariable _targetVar;
        private final int _birthId;
        private final boolean _doReplace;

        private boolean _detected = false;
        private boolean _disableJoin = false;

        VarRefDetector(final BindingVariable var, final boolean doReplace) {
            super();
            this._targetVar = var;
            this._birthId = var.getBirthId();
            this._doReplace = doReplace;
        }

        boolean isDetected() {
            return _detected;
        }

        boolean isJoinDisabled() {
            return _disableJoin;
        }

        @Override
        public XQExpression visit(final PathExpr path, final XQueryContext ctxt)
                throws XQueryException {
            if(path instanceof RelativePath) {
                final List<XQExpression> steps = path.getSteps();
                final int steplen = steps.size();
                for(int i = 0; i < steplen; i++) {
                    final XQExpression step = steps.get(i);
                    if(step instanceof VarRef) {
                        final VarRef ref = (VarRef) step;
                        final Variable referent = ref.getValue();
                        if(_targetVar == referent) {
                            if(_doReplace) {
                                steps.remove(i);
                            }
                            _detected = true;
                            return path;
                        }
                    }
                    step.visit(this, ctxt);
                }
            }
            super.visit(path, ctxt);
            return path;
        }

        @Override
        public XQExpression visit(BindingVariable variable, XQueryContext ctxt)
                throws XQueryException {
            final int targetBirthId = variable.getBirthId();
            if(targetBirthId > _birthId) {
                this._disableJoin = true;
                return variable;
            }
            return super.visit(variable, ctxt);
        }

    }

    private static final class PredicateReplacer extends AbstractXQueryParserVisitor {

        private final XQExpression _target;

        PredicateReplacer(final XQExpression target) {
            super();
            assert (target != null);
            _target = target;
        }

        @Override
        public XQExpression visit(final PathVariable variable, final XQueryContext ctxt)
                throws XQueryException {
            final XQExpression pathExpr = variable.getValue();
            if(pathExpr instanceof CompositePath) {
                final CompositePath cp = (CompositePath) pathExpr;
                final XQExpression srcExpr = cp.getSourceExpr();
                assert (srcExpr != null);
                final XQExpression filterExpr = cp.getFilterExpr();
                assert (filterExpr != null);
                srcExpr.visit(this, ctxt);
                if(srcExpr == _target) {
                    variable.setValue(filterExpr);
                }
                filterExpr.visit(this, ctxt);
            }
            return variable;
        }

        @Override
        public XQExpression visit(final PathExpr path, final XQueryContext ctxt)
                throws XQueryException {
            final List<XQExpression> steps = path.getSteps();
            int steplen = steps.size();
            for(int i = 0; i < steplen; i++) {
                final XQExpression s = steps.get(i);
                if(s instanceof VarRef) {
                    final Variable var = ((VarRef) s).getValue();
                    if(var == _target) {
                        if(steplen > 1) {
                            final XQExpression next = steps.get(i + 1);
                            if(next instanceof StepExpr) {
                                steps.remove(i);
                                --steplen;
                                continue;
                            }
                        }
                        steps.set(i, new SelfStep(NodeTest.ANYNODE));
                    }
                    s.visit(this, ctxt);
                }
            }
            return path;
        }
    }

    private static final class DependancyChecker extends AbstractXQueryParserVisitor {
        private final List<Binding> _clauses;
        private Binding _dependent = null;
        private boolean _found = false;

        DependancyChecker(final List<Binding> clauses) {
            super();
            _clauses = clauses;
        }

        Binding getDependent() {
            return _dependent;
        }

        boolean hasDependancy() {
            return _found;
        }

        @Override
        public XQExpression visit(final FLWRExpr expr, final XQueryContext ctxt)
                throws XQueryException {
            for(final Binding b : expr.getClauses()) {
                b.visit(this, ctxt);
            }
            return expr;
        }

        @Override
        public XQExpression visit(final VarRef ref, final XQueryContext ctxt)
                throws XQueryException {
            XQExpression referent = ref.getValue();
            if(referent instanceof ParametricVariable) {
                XQExpression argv = ((ParametricVariable) referent).getValue();
                if(argv instanceof TypePromotedExpr) {
                    argv = ((TypePromotedExpr) argv).getPromotedExpr();
                    if(argv instanceof VarRef) {
                        referent = ((VarRef) argv).getValue();
                    }
                }
            }
            ForClause lastForClause = null;
            for(final Binding target : _clauses) {
                final int type = target.getExpressionType();
                if(type == Binding.FOR_CLAUSE) {
                    lastForClause = (ForClause) target;
                    final Variable bvar = lastForClause.getVariable();
                    final PositionalVariable posVar = lastForClause.getPositionVariable();
                    if((bvar == referent) || (posVar == referent)) {
                        if(_found) {
                            final int posTrgDep = _clauses.indexOf(target);
                            final int posLastDep = _clauses.indexOf(_dependent); // return -1 with null arg
                            if(posLastDep > posTrgDep) {
                                // Considering following case.
                                // | for $a in /aaa, $b in /bbb, $c in /ccc 
                                // | where $c = $b ..
                                // | ..    ~~~~~~~
                                continue;
                            }
                        }
                        _found = true;
                        _dependent = target;
                    }
                } else {
                    LetClause letClause = (LetClause) target;
                    final LetVariable lv = letClause.getVariable();
                    if(lv == referent) {
                        if(_found) {
                            final int posTrgDep = _clauses.indexOf(target);
                            final int posLastDep = _clauses.indexOf(_dependent);
                            if(posLastDep > posTrgDep) {
                                continue;
                            }
                        }
                        _found = true;
                        _dependent = target;
                    }
                }
            }
            return ref;
        }

        @Override
        public XQExpression visit(final DirectFunctionCall call, final XQueryContext ctxt)
                throws XQueryException {
            final Function func = (call).getFunction();
            if(func instanceof Position) {
                for(final Binding target : _clauses) {
                    final int type = target.getExpressionType();
                    if(type == Binding.FOR_CLAUSE) {
                        _dependent = target;
                        break;
                    }
                }
                _found = true;
                assert (_dependent != null);
            } else {
                super.visit(call, ctxt);
            }
            return call;
        }
    }

}
