/*
 * @(#)$Id: ValueComp.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.*;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.AtomizedSequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.*;
import xbird.xquery.type.xs.StringType;
import xbird.xquery.type.xs.UntypedAtomicType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#sec_value_comparisons
 */
public final class ValueComp extends ComparisonOp {
    private static final long serialVersionUID = 2656012626688705161L;

    public enum Operator {
        // bit :    4    2    1
        // cmp :   -1(<) 0(=) >(1)
        // -------------------------
        // =   :    0    1    0    ->  2
        // >=  :    0    1    1    ->  3
        EQ("eq", (byte) 2), NE("ne", (byte) 5), LT("lt", (byte) 4), LE("le", (byte) 6), GT("gt",
                (byte) 1), GE("ge", (byte) 3);

        public final String sgn;
        private final byte align;

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

    private Operator operator;

    public ValueComp(Operator operator, XQExpression leftOperand) {
        super(leftOperand);
        this.operator = operator;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public String getOperator() {
        return operator.toString();
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence left = _leftOperand.eval(contextSeq, dynEnv);
        final Sequence right = _rightOperand.eval(contextSeq, dynEnv);
        final boolean ebv = effectiveBooleanValue(operator, left, right, dynEnv);
        return ebv ? BooleanValue.TRUE : BooleanValue.FALSE;
    }

    //--------------------------------------------
    // helper

    public static boolean effectiveBooleanValue(Operator op, Sequence leftSeq, Sequence rightSeq, DynamicContext dynEnv)
            throws XQueryException {
        final IAtomized<AtomicValue> left = AtomizedSequence.<AtomicValue> wrap(leftSeq, dynEnv);
        final IAtomized<AtomicValue> right = AtomizedSequence.<AtomicValue> wrap(rightSeq, dynEnv);
        final IFocus<AtomicValue> leftitor = left.iterator();
        final IFocus<AtomicValue> rightitor = right.iterator();
        if(leftitor.hasNext() && rightitor.hasNext()) {
            final AtomicValue v1 = leftitor.next();
            final AtomicValue v2 = rightitor.next();
            if(leftitor.hasNext() || rightitor.hasNext()) {
                leftitor.closeQuietly();
                rightitor.closeQuietly();
                throw new DynamicError("err:XPTY0004", "values are not comparable");
            }
            if(compare(op, v1, v2, dynEnv)) {
                leftitor.closeQuietly();
                rightitor.closeQuietly();
                return true;
            }
        }
        leftitor.closeQuietly();
        rightitor.closeQuietly();
        return false;
    }

    private static boolean compare(final Operator op, final AtomicValue a1, final AtomicValue a2, final DynamicContext dynEnv)
            throws XQueryException {
        final AtomicValue v1 = convertOperand(a1, dynEnv);
        final AtomicValue v2 = convertOperand(a2, dynEnv);
        final AtomicType t1 = v1.getType();
        final AtomicType t2 = v2.getType();
        if(!TypeTable.isMayCastable(t1, t2)) {
            throw new TypeError("err:XPTY0004", "values are not comparable: v1(" + t1 + "): " + v1
                    + ", v2(" + v2.getType() + "):" + v2);
        }
        if(op == Operator.EQ) { // for uncomparable situation
            return v1.equals(v2);
        }
        final int cmp = v1.compareTo(v2);
        final boolean res = op.acceptSgn(cmp);
        return res;
    }

    private static AtomicValue convertOperand(final AtomicValue actual, final DynamicContext dynEnv)
            throws XQueryException {
        final Type actualType = actual.getType();
        if(actualType == UntypedAtomicType.UNTYPED_ATOMIC) {
            return actual.castAs(StringType.STRING, dynEnv);
        }
        return actual;
    }

    public void switchOperand() {
        final Operator op;
        switch(operator) {
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
                op = operator;
        }
        final XQExpression tmp = _leftOperand;
        this._leftOperand = _rightOperand;
        this._rightOperand = tmp;
        this.operator = op;
    }

}
