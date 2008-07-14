/*
 * @(#)$Id: ComparisonOp.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.comp;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.expr.*;
import xbird.xquery.expr.var.Variable.PreEvaluatedVariable;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.type.*;
import xbird.xquery.type.node.NodeType;
import xbird.xquery.type.xs.BooleanType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class ComparisonOp extends AbstractXQExpression implements BooleanOp {
    private static final long serialVersionUID = 1L;

    protected XQExpression _leftOperand;
    protected XQExpression _rightOperand = null;

    public ComparisonOp(XQExpression leftOperand) {
        this._leftOperand = leftOperand;
        this._type = BooleanType.BOOLEAN;
    }

    //--------------------------------------------
    // getter/setter

    public abstract String getOperator();

    public void setLeftOperand(XQExpression leftOperand) {
        this._leftOperand = leftOperand;
    }

    public XQExpression getLeftOperand() {
        return this._leftOperand;
    }

    public void setRightOperand(XQExpression rightOperand) {
        this._rightOperand = rightOperand;
    }

    public XQExpression getRightOperand() {
        if(_rightOperand == null) {
            throw new IllegalStateException("Right operand is not set.");
        }
        return this._rightOperand;
    }

    //--------------------------------------------
    // static analysis / dynamic evaluation

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            if(_rightOperand == null) {
                throw new IllegalStateException("Right operand is not set");
            }
            this._analyzed = true;
            this._leftOperand = _leftOperand.staticAnalysis(statEnv);
            this._rightOperand = _rightOperand.staticAnalysis(statEnv);
            if(_leftOperand.getType() == SequenceType.EMPTY
                    || _rightOperand.getType() == SequenceType.EMPTY) {
                return new LiteralExpr(BooleanValue.FALSE);
            }
            XQExpression optimized = optimize(statEnv);
            return optimized;
        }
        return this;
    }

    //--------------------------------------------
    // helper

    protected AbstractXQExpression optimize(StaticContext ctxt) throws XQueryException {
        if(_leftOperand instanceof Evaluable && _rightOperand instanceof Evaluable) {
            Sequence<? extends Item> eagerEvaluated = eval(null, DynamicContext.DUMMY);
            return new PreEvaluatedVariable(eagerEvaluated);
        }
        // switch operand
        Type rtype = _rightOperand.getType();
        Type ltype = _leftOperand.getType();
        if(rtype instanceof AtomicType) {
            if(ltype instanceof SequenceType || ltype instanceof NodeType) {
                switchOperand();
            }
        }
        return this;
    }

    public abstract void switchOperand();

}
