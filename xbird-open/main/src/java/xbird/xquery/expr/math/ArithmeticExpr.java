/*
 * @(#)$Id: ArithmeticExpr.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.math;

import xbird.xquery.XQueryException;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.StaticContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#id-arithmetic
 * @link http://www.w3.org/TR/xquery-semantics/#sec_arithmetic
 */
public abstract class ArithmeticExpr extends AbstractXQExpression {
    private static final long serialVersionUID = 1L;
    
    protected XQExpression _leftOperand, _rightOperand;

    public ArithmeticExpr(XQExpression leftOperand, XQExpression rightOperand) {
        this._leftOperand = leftOperand;
        this._rightOperand = rightOperand;
    }

    public XQExpression getLeftOperand() {
        return this._leftOperand;
    }

    public XQExpression getRightOperand() {
        return this._rightOperand;
    }
    
    public void setLeftOperand(XQExpression e) {
        this._leftOperand = e;
    }
    
    public void setRightOperand(XQExpression e) {
        this._rightOperand = e;
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if (!_analyzed) {
            this._analyzed = true;
            this._leftOperand = _leftOperand.staticAnalysis(statEnv);
            this._rightOperand = _rightOperand.staticAnalysis(statEnv);
        }
        return this;
    }

}
