/*
 * @(#)$Id: TypeswitchExpr.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.types;

import java.util.ArrayList;
import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.BindingVariable.CaseVariable;
import xbird.xquery.meta.*;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#id-typeswitch
 * @link http://www.w3.org/TR/xquery-semantics/#sec_typeswitch
 * @link http://www.w3.org/TR/xquery/#doc-xquery-TypeswitchExpr
 */
public final class TypeswitchExpr extends AbstractXQExpression {
    private static final long serialVersionUID = 1930059488843041889L;

    private XQExpression _operandExpr;
    private final List<CaseClause> _caseClauses = new ArrayList<CaseClause>(4);
    private CaseClause _defaultClause = null;

    public TypeswitchExpr(XQExpression expr) {
        this._operandExpr = expr;
    }

    public List<CaseClause> getCaseClauses() {
        return this._caseClauses;
    }

    public CaseClause getDefaultClause() {
        return _defaultClause;
    }

    public XQExpression getOperandExpr() {
        return this._operandExpr;
    }

    public void addCaseClause(CaseClause cc) {
        _caseClauses.add(cc);
    }

    public void setDefaultClause(CaseClause defaultClause) {
        this._defaultClause = defaultClause;
        _caseClauses.add(defaultClause);
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            this._operandExpr = _operandExpr.staticAnalysis(statEnv);
            final Type oprType = _operandExpr.getType();
            Type retType = null;
            Type lastMatchedType = null;
            CaseClause lastCC = null;
            for(CaseClause cc : _caseClauses) {
                cc.staticAnalysis(statEnv);
                if(cc != _defaultClause) {
                    final Type caseType = cc.getVariable().getType();
                    if(oprType == caseType) {
                        lastCC = cc;
                        break;
                    } else if(TypeUtil.subtypeOf(oprType, caseType)) {
                        if(lastMatchedType == null || TypeUtil.subtypeOf(caseType, lastMatchedType)) {
                            lastMatchedType = caseType;
                            lastCC = cc;
                        }
                        continue;
                    }
                }
                if(retType == null) {
                    retType = cc.getType();
                } else {
                    retType = TypeUtil.union(retType, cc.getType());
                }
            }
            if(lastCC != null) {
                CaseVariable cv = lastCC.getVariable();
                cv.setValue(_operandExpr);
                XQExpression retExpr = lastCC.getReturnExpr();
                XQExpression analyzed = retExpr.staticAnalysis(statEnv);
                return analyzed;
            }
            assert (retType != null);
            this._type = retType;
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence<? extends Item> opr = _operandExpr.eval(contextSeq, dynEnv);
        for(CaseClause cc : _caseClauses) {
            final CaseVariable cv = cc.getVariable();
            final Type ccType = cv.getType();
            if(TypeUtil.instanceOf(opr, ccType)) {
                cv.allocateResult(opr, dynEnv);
                final XQExpression ccExpr = cc.getReturnExpr();
                return ccExpr.eval(contextSeq, dynEnv);
            }
        }
        throw new IllegalStateException("Typeswitch abnormally failed");
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence<? extends Item> opr = _operandExpr.eval(contextSeq, dynEnv);
        for(CaseClause cc : _caseClauses) {
            final CaseVariable cv = cc.getVariable();
            final Type ccType = cv.getType();
            if(TypeUtil.instanceOf(opr, ccType)) {
                cv.allocateResult(opr, dynEnv);
                final XQExpression ccExpr = cc.getReturnExpr();
                ccExpr.evalAsEvents(handler, contextSeq, dynEnv);
                return;
            }
        }
        throw new IllegalStateException("Typeswitch abnormally failed");
    }

}
