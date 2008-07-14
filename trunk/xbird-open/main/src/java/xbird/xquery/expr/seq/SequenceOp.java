/*
 * @(#)$Id: SequenceOp.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.seq;

import xbird.xquery.TypeError;
import xbird.xquery.XQueryException;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#id-sequence-expressions
 */
public abstract class SequenceOp extends AbstractXQExpression {
    private static final long serialVersionUID = 1L;

    protected XQExpression _leftOperand, _rightOperand;

    protected SequenceOp(XQExpression leftOperand, XQExpression rightOperand, Type type) {
        this._leftOperand = leftOperand;
        this._rightOperand = rightOperand;
        this._type = type;
    }

    public XQExpression getLeftOperand() {
        return this._leftOperand;
    }

    public XQExpression getRightOperand() {
        return this._rightOperand;
    }

    public abstract String getOperator();

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            this._leftOperand = _leftOperand.staticAnalysis(statEnv);
            this._rightOperand = _rightOperand.staticAnalysis(statEnv);
            if(!TypeUtil.subtypeOf(_leftOperand.getType(), _type)) {
                throw new TypeError("Inferred type of left operand is invalid: "
                        + _leftOperand.getType());
            }
            if(!TypeUtil.subtypeOf(_rightOperand.getType(), _type)) {
                throw new TypeError("Inferred type of left operand is invalid: "
                        + _rightOperand.getType());
            }
        }
        return this;
    }

}
