/*
 * @(#)$Id: TimesOp.java 3619 2008-03-26 07:23:03Z yui $
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

import javax.xml.datatype.Duration;

import xbird.util.struct.Pair;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.*;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.DurationValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.misc.XsDatatypeFactory;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.TypeTable;
import xbird.xquery.type.xs.*;

/**
 * fs:times(A, B).
 * <DIV lang="en">
 * <u>denotes</u>
 * <ul>
 * Numeric * Numeric<br/>
 *  <li>op:numeric-multiply(xs:integer, xs:integer) as xs:integer</li>
 *  <li>op:numeric-multiply(xs:decimal, xs:decimal) as xs:decimal</li>
 *  <li>op:numeric-multiply(xs:float, xs:float) as xs:float</li>
 *  <li>op:numeric-multiply(xs:double, xs:double) as xs:double</li>
 * <br/>Duration * Numeric<br/>
 *  <li>op:multiply-yearMonthDuration(xdt:yearMonthDuration, xs:double) as xdt:yearMonthDuration</li>  
 *  <li>op:multiply-yearMonthDuration(xs:double, xdt:yearMonthDuration) as xdt:yearMonthDuration</li>
 *  <li>op:multiply-dayTimeDuration(xdt:dayTimeDuration, xs:double) as xdt:dayTimeDuration</li>
 *  <li>op:multiply-dayTimeDuration(xs:double, xdt:dayTimeDuration) as xdt:dayTimeDuration</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#op.numeric
 */
public class TimesOp extends NumericOp {
    private static final long serialVersionUID = 5575376556224289890L;
    public static final String SYMBOL = "fs:times";

    public TimesOp() {
        super(SYMBOL);
    }

    @SuppressWarnings("unchecked")
    protected final Pair<AtomicType[], AtomicType>[] signatures() {
        final Pair<AtomicType[], AtomicType>[] s = new Pair[] {
                // op:numeric-multiply(xs:integer, xs:integer) as xs:integer
                new Pair(new AtomicType[] { IntegerType.INTEGER, IntegerType.INTEGER }, IntegerType.INTEGER),
                // op:numeric-multiply(xs:decimal, xs:decimal) as xs:decimal
                new Pair(new AtomicType[] { DecimalType.DECIMAL, DecimalType.DECIMAL }, DecimalType.DECIMAL),
                // op:numeric-multiply(xs:float, xs:float) as xs:float
                new Pair(new AtomicType[] { FloatType.FLOAT, FloatType.FLOAT }, FloatType.FLOAT),
                // workaround
                new Pair(new AtomicType[] { DecimalType.DECIMAL, FloatType.FLOAT }, FloatType.FLOAT),
                new Pair(new AtomicType[] { FloatType.FLOAT, DecimalType.DECIMAL }, FloatType.FLOAT),
                // op:numeric-multiply(xs:double, xs:double) as xs:double
                new Pair(new AtomicType[] { DoubleType.DOUBLE, DoubleType.DOUBLE }, DoubleType.DOUBLE),
                // op:multiply-yearMonthDuration(xdt:yearMonthDuration, xs:double) as xdt:yearMonthDuration
                new Pair(new AtomicType[] { YearMonthDurationType.YEARMONTH_DURATION,
                        NumericType.getInstance() }, YearMonthDurationType.YEARMONTH_DURATION),
                // op:multiply-yearMonthDuration(xs:double, xdt:yearMonthDuration) as xdt:yearMonthDuration
                new Pair(new AtomicType[] { NumericType.getInstance(),
                        YearMonthDurationType.YEARMONTH_DURATION }, YearMonthDurationType.YEARMONTH_DURATION),
                // op:multiply-dayTimeDuration(xdt:dayTimeDuration, xs:double) as xdt:dayTimeDuration
                new Pair(new AtomicType[] { DayTimeDurationType.DAYTIME_DURATION,
                        NumericType.getInstance() }, DayTimeDurationType.DAYTIME_DURATION),
                // op:multiply-dayTimeDuration(xs:double, xdt:dayTimeDuration) as xdt:dayTimeDuration
                new Pair(new AtomicType[] { NumericType.getInstance(),
                        DayTimeDurationType.DAYTIME_DURATION }, DayTimeDurationType.DAYTIME_DURATION),
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

    public Sequence eval(DynamicContext ctxt, Item... args) throws XQueryException {
        assert (args.length == 2) : args;
        // dispatch exec
        final AtomicType retType = getReturnType();
        final Exec exec;
        if(TypeUtil.subtypeOf(retType, IntegerType.INTEGER)) {
            exec = MulInteger.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DecimalType.DECIMAL)) {
            exec = MulDecimal.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, FloatType.FLOAT)) {
            exec = MulFloat.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DoubleType.DOUBLE)) {
            exec = MulDouble.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DurationType.DURATION)) {
            exec = MulDuration.INSTANCE;
        } else {
            throw new IllegalStateException("Unexpected return type: " + retType);
        }
        AtomicValue ret = exec.eval(ctxt, args[0], args[1]);
        return ret == null ? ValueSequence.EMPTY_SEQUENCE : ret;
    }

    private static final class MulInteger extends Exec {
        static final MulInteger INSTANCE = new MulInteger();

        public XInteger eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            long i1 = asLong(v1, dynEnv);
            long i2 = asLong(v2, dynEnv);
            final long res = multiplyL(i1, i2);
            return XInteger.valueOf(res);
        }
    }

    private static final class MulFloat extends Exec {
        static final MulFloat INSTANCE = new MulFloat();

        public XFloat eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            float f1 = asFloat(v1, dynEnv);
            float f2 = asFloat(v2, dynEnv);
            float res = f1 * f2;
            return XFloat.valueOf(res);
        }
    }

    private static final class MulDouble extends Exec {
        static final MulDouble INSTANCE = new MulDouble();

        public XDouble eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            double d1 = asDouble(v1, dynEnv);
            double d2 = asDouble(v2, dynEnv);
            final double res = d1 * d2;
            if(res == Double.POSITIVE_INFINITY) {
                throw new DynamicError("err:FOAR0002", "result overflow");
            }
            if(res == Double.NEGATIVE_INFINITY) {
                throw new DynamicError("err:FOAR0002", "result underflow");
            }
            return XDouble.valueOf(res);
        }
    }

    private static final class MulDecimal extends Exec {
        static final MulDecimal INSTANCE = new MulDecimal();

        public XDecimal eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            BigDecimal dv1 = asDecimal(v1, dynEnv);
            BigDecimal dv2 = asDecimal(v2, dynEnv);
            BigDecimal res = dv1.multiply(dv2);
            return new XDecimal(res);
        }
    }

    private static final class MulDuration extends Exec {
        static final MulDuration INSTANCE = new MulDuration();

        public DurationValue eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            final DurationValue dur1;
            final double d2;
            if(v1 instanceof DurationValue) {
                dur1 = (DurationValue) v1;
                d2 = asDouble(v2, dynEnv);
            } else {
                dur1 = (DurationValue) v2;
                d2 = asDouble(v1, dynEnv);
            }
            if(Double.isNaN(d2)) {
                throw new DynamicError("err:FOCA0005", "Illegally multiplying with NaN.");
            }
            if(Double.isInfinite(d2)) {
                throw new DynamicError("err:FODT0002", "Duration value is too large or too small to be represented.");
            }
            DurationType dt = dur1.getDurationType();
            final Duration res;
            final int dt1 = dt.getTypeId();
            if(dt1 == TypeTable.YEAR_MONTH_DURATION_TID) {
                int months = dur1.totalMonths();
                long ym = Math.round(months * d2);
                res = XsDatatypeFactory.createYearMonthDuration(ym);
            } else {
                Duration dv1 = dur1.getValue();
                res = dv1.multiply(BigDecimal.valueOf(d2));
            }
            return new DurationValue(res, dt);
        }
    }

}
