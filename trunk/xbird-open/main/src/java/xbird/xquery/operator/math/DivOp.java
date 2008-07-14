/*
 * @(#)$Id: DivOp.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.operator.math;

import java.math.BigDecimal;

import xbird.util.struct.Pair;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.*;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.DurationValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.operator.InternalFunction;
import xbird.xquery.type.*;
import xbird.xquery.type.xs.*;

/**
 * fs:div(A, B).
 * <DIV lang="en">
 * <u>denotes</u>
 * <ul>
 * Numeric / Numeric<br/>
 *  <li>op:numeric-divide(xs:integer, xs:integer) as xs:double</li>
 *  <li>op:numeric-divide(xs:decimal, xs:decimal) as xs:decimal</li>
 *  <li>op:numeric-divide(xs:float, xs:float) as xs:float</li>
 *  <li>op:numeric-divide(xs:double, xs:double) as xs:double</li>
 * <br/>Duration / Numeric<br/>
 *  <li>op:divide-yearMonthDuration(xdt:yearMonthDuration, xs:double) as xdt:yearMonthDuration</li>
 *  <li>op:divide-dayTimeDuration(xdt:dayTimeDuration, xs:double) as xdt:dayTimeDuration</li>
 * <br/>Duration / Duration<br/>
 *  <li>op:divide-yearMonthDuration-by-yearMonthDuration(xdt:yearMonthDuration, xdt:yearMonthDuration) as xs:decimal</li>
 *  <li>op:divide-dayTimeDuration-by-dayTimeDuration(xdt:dayTimeDuration, xdt:dayTimeDuration) as xs:decimal</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xpath-functions/#func-numeric-divide
 * @link http://www.w3.org/TR/xquery-operators/#op.numeric
 */
public class DivOp extends NumericOp {
    private static final long serialVersionUID = -3633714711839515327L;
    public static final String SYMBOL = "fs:div";

    public DivOp() {
        super(SYMBOL);
    }

    @SuppressWarnings("unchecked")
    protected Pair<AtomicType[], AtomicType>[] signatures() {
        final Pair<AtomicType[], AtomicType>[] s = new Pair[] {
                // op:numeric-divide(xs:integer, xs:integer) as xs:double
                // As a special case, if the types of both $arg1 and $arg2 are xs:integer, then the return type is xs:decimal.
                new Pair(new AtomicType[] { IntegerType.INTEGER, IntegerType.INTEGER }, DecimalType.DECIMAL),
                // op:numeric-divide(xs:decimal, xs:decimal) as xs:decimal
                new Pair(new AtomicType[] { DecimalType.DECIMAL, DecimalType.DECIMAL }, DecimalType.DECIMAL),
                // op:numeric-divide(xs:float, xs:float) as xs:float
                new Pair(new AtomicType[] { FloatType.FLOAT, FloatType.FLOAT }, FloatType.FLOAT),
                // workaround
                new Pair(new AtomicType[] { DecimalType.DECIMAL, FloatType.FLOAT }, FloatType.FLOAT),
                new Pair(new AtomicType[] { FloatType.FLOAT, DecimalType.DECIMAL }, FloatType.FLOAT),
                // op:numeric-divide(xs:double, xs:double) as xs:double
                new Pair(new AtomicType[] { DoubleType.DOUBLE, DoubleType.DOUBLE }, DoubleType.DOUBLE),
                // op:divide-yearMonthDuration(xdt:yearMonthDuration, xs:double) as xdt:yearMonthDuration
                new Pair(new AtomicType[] { YearMonthDurationType.YEARMONTH_DURATION,
                        NumericType.getInstance() }, YearMonthDurationType.YEARMONTH_DURATION),
                // op:divide-dayTimeDuration(xdt:dayTimeDuration, xs:double) as xdt:dayTimeDuration
                new Pair(new AtomicType[] { DayTimeDurationType.DAYTIME_DURATION,
                        NumericType.getInstance() }, DayTimeDurationType.DAYTIME_DURATION),
                // op:divide-yearMonthDuration-by-yearMonthDuration(xdt:yearMonthDuration, xdt:yearMonthDuration) as xs:decimal
                new Pair(new AtomicType[] { YearMonthDurationType.YEARMONTH_DURATION,
                        YearMonthDurationType.YEARMONTH_DURATION }, DecimalType.DECIMAL),
                // op:divide-dayTimeDuration-by-dayTimeDuration(xdt:dayTimeDuration, xdt:dayTimeDuration) as xs:decimal
                new Pair(new AtomicType[] { DayTimeDurationType.DAYTIME_DURATION,
                        DayTimeDurationType.DAYTIME_DURATION }, DecimalType.DECIMAL),
                // workaround
                new Pair(new AtomicType[] { NumericType.getInstance(), NumericType.getInstance() }, DoubleType.DOUBLE),
                new Pair(new AtomicType[] { UntypedAtomicType.UNTYPED_ATOMIC,
                        NumericType.getInstance() }, DoubleType.DOUBLE),
                new Pair(new AtomicType[] { NumericType.getInstance(),
                        UntypedAtomicType.UNTYPED_ATOMIC }, DoubleType.DOUBLE),
                new Pair(new AtomicType[] { UntypedAtomicType.UNTYPED_ATOMIC,
                        UntypedAtomicType.UNTYPED_ATOMIC }, DoubleType.DOUBLE) };
        return s;
    }

    public InternalFunction staticAnalysis(StaticContext context, Sequence left, Sequence right)
            throws XQueryException {
        final Type[] t = new Type[2];
        t[0] = left.getType();
        t[1] = right.getType();
        return resolve(t);
    }

    public Sequence eval(DynamicContext ctxt, Item... args) throws XQueryException {
        assert (args.length == 2) : args;
        // dispatch exec
        AtomicType retType = getReturnType();
        final Exec exec;
        if(TypeUtil.subtypeOf(retType, DecimalType.DECIMAL)) {
            if(TypeUtil.subtypeOf(args[0].getType(), DurationType.DURATION)
                    || TypeUtil.subtypeOf(args[1].getType(), DurationType.DURATION)) {
                exec = new DivDurationByDuration();
            } else if(TypeUtil.subtypeOf(args[0].getType(), IntegerType.INTEGER)
                    || TypeUtil.subtypeOf(args[1].getType(), IntegerType.INTEGER)) {
                exec = new DivInteger();
            } else {
                exec = new DivDecimal();
            }
        } else if(TypeUtil.subtypeOf(retType, FloatType.FLOAT)) {
            exec = new DivFloat();
        } else if(TypeUtil.subtypeOf(retType, DoubleType.DOUBLE)) {
            if(TypeUtil.subtypeOf(args[0].getType(), DurationType.DURATION)
                    || TypeUtil.subtypeOf(args[1].getType(), DurationType.DURATION)) {
                exec = new DivDurationByDuration();
            } else {
                exec = new DivDouble();
            }
        } else if(TypeUtil.subtypeOf(retType, DurationType.DURATION)) {
            exec = new DivDuration();
        } else {
            throw new IllegalStateException("Unexpected return type: " + retType);
        }
        final AtomicValue ret = exec.eval(ctxt, args[0], args[1]);
        return ret == null ? ValueSequence.EMPTY_SEQUENCE : ret;
    }

    static final class DivInteger extends Exec {
        static final DivInteger INSTANCE = new DivInteger();

        public XDecimal eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            BigDecimal res = DivDecimal.compute(v1, v2, dynEnv); // Use DivDecimal to return xs:decimal
            return XDecimal.valueOf(res);
        }

        /**
         * Compute the result without the remainder.
         * 
         * @see IdivOp#eval(DynamicContext, Item[])
         */
        public static long compute(final Item v1, final Item v2, final DynamicContext dynEnv)
                throws XQueryException {
            final long divisor = asLong(v2, dynEnv);
            if(divisor == 0) {
                throw new DynamicError("err:FOAR0001", "divide by zero");
            }
            long src = asLong(v1, dynEnv);
            long res = src / divisor;
            return res;
        }
    }

    static final class DivFloat extends Exec {
        static final DivFloat INSTANCE = new DivFloat();

        public XFloat eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            float res = compute(v1, v2, dynEnv);
            return XFloat.valueOf(res);
        }

        public static float compute(final Item v1, final Item v2, final DynamicContext dynEnv)
                throws XQueryException {
            final float divisor = asFloat(v2, dynEnv);
            if(divisor == 0) {
                return Float.NaN;
            }
            final float f1 = asFloat(v1, dynEnv);
            // If either operand is NaN or if $arg1 is INF or -INF then an error is raised 
            // [err:FOAR0002].
            if(f1 != f1 || Float.isInfinite(f1)) {
                throw new DynamicError("err:FOAR0002", "Invalid $arg1 operand: "
                        + Double.toString(f1));
            }
            if(divisor != divisor) {
                throw new DynamicError("err:FOAR0002", "Invalid $arg2 operand: "
                        + Double.toString(divisor));
            }
            float res = f1 / divisor;
            return res;
        }
    }

    static final class DivDouble extends Exec {
        static final DivDouble INSTANCE = new DivDouble();

        public XDouble eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            double res = compute(v1, v2, dynEnv);
            return XDouble.valueOf(res);
        }

        public static double compute(final Item v1, final Item v2, final DynamicContext dynEnv)
                throws XQueryException {
            final double divisor = asDouble(v2, dynEnv);
            if(divisor == 0) {
                return Double.NaN;
            }
            double d1 = asDouble(v1, dynEnv);
            // If either operand is NaN or if $arg1 is INF or -INF then an error is raised 
            // [err:FOAR0002].
            if(d1 != d1 || Double.isInfinite(d1)) {
                throw new DynamicError("err:FOAR0002", "Invalid $arg1 operand: "
                        + Double.toString(d1));
            }
            if(divisor != divisor) {
                throw new DynamicError("err:FOAR0002", "Invalid $arg2 operand: "
                        + Double.toString(divisor));
            }
            final double res = d1 / divisor;
            return res;
        }
    }

    static final class DivDecimal extends Exec {
        static final DivDecimal INSTANCE = new DivDecimal();

        public XDecimal eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            BigDecimal res = compute(v1, v2, dynEnv);
            return new XDecimal(res);
        }

        public static BigDecimal compute(final Item v1, final Item v2, final DynamicContext dynEnv)
                throws XQueryException {
            final BigDecimal divisor = asDecimal(v2, dynEnv);
            if(divisor.equals(BigDecimal.ZERO)) {
                throw new DynamicError("err:FOAR0001", "divide by zero");
            }
            BigDecimal dv1 = asDecimal(v1, dynEnv);
            BigDecimal res = XDecimal.divide(dv1, divisor);
            return res;
        }
    }

    public static final class DivDuration extends Exec {
        static final DivDuration INSTANCE = new DivDuration();

        public DurationValue eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            final DurationValue dur1;
            final double divisor;
            if(v1 instanceof DurationValue) {
                dur1 = (DurationValue) v1;
                divisor = NumericOp.asDouble(v2, dynEnv);
            } else {
                dur1 = (DurationValue) v2;
                divisor = NumericOp.asDouble(v1, dynEnv);
            }
            if(divisor == 0) {
                throw new DynamicError("err:FODT0002", "Overflow in duration arithmetic, divided by zero");
            }
            if(Double.isNaN(divisor)) {
                throw new DynamicError("err:FOCA0005", "$arg2 is NaN");
            }
            double res = 1.0 / divisor;
            DurationValue resDur = dur1.multiply(res);
            return resDur;
        }

    }

    public static final class DivDurationByDuration extends Exec {
        static final DivDurationByDuration INSTANCE = new DivDurationByDuration();

        public XDecimal eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            DurationValue d1 = (DurationValue) v1;
            DurationValue d2 = (DurationValue) v2;

            final int dt1 = d1.getDurationType().getTypeId();
            final int dt2 = d2.getDurationType().getTypeId();
            if(dt1 == dt2) {
                if(dt1 == TypeTable.YEAR_MONTH_DURATION_TID) {
                    return evalYearMonthDuration(d1, d2);
                }
                //TODO else if(dt1 == TypeTable.DAYTIME_DURATION_TID) {}
            }

            final double divisor = d2.getTimeInMillis();
            if(divisor == 0) {
                throw new DynamicError("err:FODT0002", "Overflow in duration arithmetic, divided by zero");
            }
            if(Double.isNaN(divisor)) {
                throw new DynamicError("err:FOCA0005", "$arg2 is NaN");
            }
            double l1 = d1.getTimeInMillis();
            double res = l1 / divisor;
            BigDecimal bdv = BigDecimal.valueOf(res);
            return XDecimal.valueOf(bdv);
        }

        private static XDecimal evalYearMonthDuration(final DurationValue d1, final DurationValue d2)
                throws DynamicError {
            final int divisor = d2.totalMonths();
            if(divisor == 0) {
                throw new DynamicError("err:FODT0002", "Overflow in duration arithmetic, divided by zero");
            }
            double m1 = d1.totalMonths();
            double res = m1 / divisor;
            BigDecimal bdv = BigDecimal.valueOf(res);
            return XDecimal.valueOf(bdv);
        }
    }

}
