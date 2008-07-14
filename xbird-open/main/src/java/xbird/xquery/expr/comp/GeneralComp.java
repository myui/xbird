/*
 * @(#)$Id: GeneralComp.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.AtomizedSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.*;

/**
 * General Comparisons.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#sec_general_comparisons
 */
public final class GeneralComp extends ComparisonOp {
    private static final long serialVersionUID = -345663555720071549L;

    public enum Operator {
        // bit :    4    2    1
        // cmp :   -1(<) 0(=) >(1)
        // -------------------------
        // =   :    0    1    0    ->  2
        // >=  :    0    1    1    ->  3
        EQ("=", (byte) 2), GE(">=", (byte) 3), GT(">", (byte) 1), LE("<=", (byte) 6), LT("<",
                (byte) 4), NE("!=", (byte) 5);

        private final byte align;
        public final String sgn;

        private Operator(String sgn, byte align) {
            this.sgn = sgn;
            this.align = align;
        }

        public boolean acceptSgn(int cmp) {
            byte cmpsgn = (cmp == 0) ? EQ.align : (cmp > 0) ? GT.align : LT.align;
            return (align & cmpsgn) == cmpsgn;
        }

        @Override
        public String toString() {
            return sgn;
        }
    }

    private Operator _operator;

    public GeneralComp(Operator operator, XQExpression leftOperand) {
        super(leftOperand);
        this._operator = operator;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public String getOperator() {
        return _operator.toString();
    }

    @Override
    protected AbstractXQExpression optimize(StaticContext ctxt) throws XQueryException {
        return super.optimize(ctxt);
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final BooleanValue v = effectiveBooleanValue(contextSeq, dynEnv) ? BooleanValue.TRUE
                : BooleanValue.FALSE;
        return v;
    }

    //--------------------------------------------
    // helper

    private boolean effectiveBooleanValue(final Sequence contextSeq, final DynamicContext dynEnv)
            throws XQueryException {
        final Sequence rawleft = _leftOperand.eval(contextSeq, dynEnv);
        if(rawleft == ValueSequence.EMPTY_SEQUENCE) {
            return false;
        }
        final Sequence rawright = _rightOperand.eval(contextSeq, dynEnv);
        if(rawright == ValueSequence.EMPTY_SEQUENCE) {
            return false;
        }
        return compare(_operator, rawleft, rawright, dynEnv);
    }

    public static boolean compare(final Operator op, final Sequence lhs, final Sequence rhs, final DynamicContext dynEnv)
            throws XQueryException {
        // atomize
        final IAtomized<AtomicValue> left = AtomizedSequence.<AtomicValue> wrap(lhs, dynEnv);
        final IAtomized<AtomicValue> right = AtomizedSequence.<AtomicValue> wrap(rhs, dynEnv);
        final IFocus<AtomicValue> leftItor = left.iterator();
        final IFocus<AtomicValue> rightItor = right.iterator();
        // TODO cache inner
        for(final AtomicValue leftItem : leftItor) {
            for(final AtomicValue rightItem : rightItor) {
                if(compare(op, leftItem, rightItem, dynEnv)) {
                    leftItor.closeQuietly();
                    rightItor.closeQuietly();
                    return true;
                }
            }
        }
        leftItor.closeQuietly();
        rightItor.closeQuietly();
        return false;
    }

    private static boolean compare(final Operator op, final AtomicValue a1, final AtomicValue a2, final DynamicContext dynEnv)
            throws XQueryException {
        final AtomicValue v1 = convertOperand(a1, a2, dynEnv);
        final AtomicValue v2 = convertOperand(a2, a1, dynEnv);
        if(op == Operator.EQ) { // for uncomparable situation
            return v1.equals(v2);
        }
        final int cmp = v1.compareTo(v2);
        final boolean res = op.acceptSgn(cmp);
        return res;
    }

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#sec_convert_operand
     */
    private static AtomicValue convertOperand(final AtomicValue actual, final AtomicValue expected, final DynamicContext dynEnv)
            throws XQueryException {
        // If $actual is of type xdt:untypedAtomic, then
        final Type actualType = actual.getType();
        final Type expectedType = expected.getType();
        if(actualType == expectedType) {
            return actual;
        }
        if(actualType == UntypedAtomicType.UNTYPED_ATOMIC) {
            if(expectedType == UntypedAtomicType.UNTYPED_ATOMIC) {
                // a. if $expected is of type xdt:untypedAtomic, returns $actual cast to xs:string;
                return actual.castAs(StringType.STRING, dynEnv);
            } else if(expectedType instanceof NumericType) {
                // b. if $expected is of numeric type, returns $actual cast to xs:double
                return actual.castAs(DoubleType.DOUBLE, dynEnv);
            } else {
                // c. otherwise returns $actual cast to the type of $expected.
                try {
                    return actual.castAs((AtomicType) expectedType, dynEnv);
                } catch (XQueryException e) {
                    throw new DynamicError("err:FORG0001", e);
                }
            }
        }
        return actual;
    }

    public void switchOperand() {
        final Operator op;
        switch(_operator) {
            case LT:
                op = Operator.GT;
                break;
            case GT:
                op = Operator.LT;
                break;
            case LE:
                op = Operator.GE;
                break;
            case GE:
                op = Operator.LE;
                break;
            default:
                op = _operator;
        }
        final XQExpression tmp = _leftOperand;
        this._leftOperand = _rightOperand;
        this._rightOperand = tmp;
        this._operator = op;
    }

}
