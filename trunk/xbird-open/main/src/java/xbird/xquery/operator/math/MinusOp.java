/*
 * @(#)$Id: MinusOp.java 3619 2008-03-26 07:23:03Z yui $
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
import javax.xml.datatype.XMLGregorianCalendar;

import xbird.util.struct.Pair;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.*;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.DateTimeValue;
import xbird.xquery.dm.value.xsi.DurationValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.misc.XsDatatypeFactory;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.TypeTable;
import xbird.xquery.type.xs.*;

/**
 * fs:minus(A, B).
 * <DIV lang="en">
 * <u>denotes</u>
 * <ul>
 * Numeric - Numeric<br/>
 *  <li>op:numeric-subtract(xs:integer, xs:integer) as xs:integer</li>
 *  <li>op:numeric-subtract(xs:decimal, xs:decimal) as xs:decimal</li>
 *  <li>op:numeric-subtract(xs:float, xs:float) as xs:float</li>
 *  <li>op:numeric-subtract(xs:double, xs:double) as xs:double</li>
 * <br/>DateTime - DateTime<br/>
 *  <li>fn:subtract-dates(xs:date, xs:date) as xdt:dayTimeDuration</li>
 *  <li>fn:subtract-times(xs:time, xs:time) as xdt:dayTimeDuration</li>
 *  <li>op:subtract-dateTimes(xs:dateTime, xs:dateTime) as xdt:dayTimeDuration</li>
 * <br/>DateTime - Duration<br/>
 *  <li>op:subtract-yearMonthDuration-from-date(xs:date, xdt:yearMonthDuration) as xs:date</li>
 *  <li>op:subtract-dayTimeDuration-from-date(xs:date, xdt:dayTimeDuration) as xs:date</li>
 *  <li>op:subtract-dayTimeDuration-from-time(xs:time, xdt:dayTimeDuration) as xs:time</li>
 *  <li>op:subtract-yearMonthDuration-from-dateTime(xs:dateTime, xdt:yearMonthDuration) as xs:dateTime</li>
 *  <li>op:subtract-dayTimeDuration-from-dateTime(xs:dateTime, xdt:dayTimeDuration) as xs:dateTime</li>
 * <br/>Duration - Duration<br/>
 *  <li>op:subtract-yearMonthDurations(xdt:yearMonthDuration, xdt:yearMonthDuration) as xdt:yearMonthDuration</li>
 *  <li>op:subtract-dayTimeDurations(xdt:dayTimeDuration, xdt:dayTimeDuration) as xdt:dayTimeDuration</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#sec_operator
 * @link http://www.w3.org/TR/xquery-operators/#dateTime-arithmetic
 * @link http://www.w3.org/TR/xquery-operators/#op.numeric
 */
public class MinusOp extends NumericOp {
    private static final long serialVersionUID = -8989196632410632216L;
    public static final String SYMBOL = "fs:minus";

    public MinusOp() {
        super(SYMBOL);
    }

    @SuppressWarnings("unchecked")
    protected final Pair<AtomicType[], AtomicType>[] signatures() {
        final Pair<AtomicType[], AtomicType>[] s = new Pair[] {
                // op:numeric-subtract(xs:integer, xs:integer) as xs:integer
                new Pair(new AtomicType[] { IntegerType.INTEGER, IntegerType.INTEGER }, IntegerType.INTEGER),
                // op:numeric-subtract(xs:decimal, xs:decimal) as xs:decimal
                new Pair(new AtomicType[] { DecimalType.DECIMAL, DecimalType.DECIMAL }, DecimalType.DECIMAL),
                // op:numeric-subtract(xs:float, xs:float) as xs:float
                new Pair(new AtomicType[] { FloatType.FLOAT, FloatType.FLOAT }, FloatType.FLOAT),
                // workaround
                new Pair(new AtomicType[] { DecimalType.DECIMAL, FloatType.FLOAT }, FloatType.FLOAT),
                new Pair(new AtomicType[] { FloatType.FLOAT, DecimalType.DECIMAL }, FloatType.FLOAT),
                // op:numeric-subtract(xs:double, xs:double) as xs:double
                new Pair(new AtomicType[] { DoubleType.DOUBLE, DoubleType.DOUBLE }, DoubleType.DOUBLE),
                // fn:subtract-dates(xs:date, xs:date) as xdt:dayTimeDuration
                new Pair(new AtomicType[] { DateType.DATE, DateType.DATE }, DayTimeDurationType.DAYTIME_DURATION),
                // op:subtract-yearMonthDuration-from-date(xs:date, xdt:yearMonthDuration) as xs:date
                new Pair(new AtomicType[] { DateType.DATE, YearMonthDurationType.YEARMONTH_DURATION }, DateType.DATE),
                // op:subtract-dayTimeDuration-from-date(xs:date, xdt:dayTimeDuration) as xs:date
                new Pair(new AtomicType[] { DateType.DATE, DayTimeDurationType.DAYTIME_DURATION }, DateType.DATE),
                // fn:subtract-times(xs:time, xs:time) as xdt:dayTimeDuration
                new Pair(new AtomicType[] { TimeType.TIME, TimeType.TIME }, DayTimeDurationType.DAYTIME_DURATION),
                // op:subtract-dayTimeDuration-from-time(xs:time, xdt:dayTimeDuration) as xs:time
                new Pair(new AtomicType[] { TimeType.TIME, DayTimeDurationType.DAYTIME_DURATION }, TimeType.TIME),
                // fn:get-dayTimeDuration-from-dateTimes(xs:dateTime, xs:dateTime) as xdt:dayTimeDuration
                new Pair(new AtomicType[] { DateTimeType.DATETIME, DateTimeType.DATETIME }, DayTimeDurationType.DAYTIME_DURATION),
                // op:subtract-yearMonthDuration-from-dateTime(xs:dateTime, xdt:yearMonthDuration) as xs:dateTime
                new Pair(new AtomicType[] { DateTimeType.DATETIME,
                        YearMonthDurationType.YEARMONTH_DURATION }, DateTimeType.DATETIME),
                // op:subtract-dayTimeDuration-from-dateTime(xs:dateTime, xdt:dayTimeDuration) as xs:dateTime
                new Pair(new AtomicType[] { DateTimeType.DATETIME,
                        DayTimeDurationType.DAYTIME_DURATION }, DateTimeType.DATETIME),
                // op:subtract-yearMonthDurations(xdt:yearMonthDuration, xdt:yearMonthDuration) as xdt:yearMonthDuration
                new Pair(new AtomicType[] { YearMonthDurationType.YEARMONTH_DURATION,
                        YearMonthDurationType.YEARMONTH_DURATION }, YearMonthDurationType.YEARMONTH_DURATION),
                // op:subtract-dayTimeDurations(xdt:dayTimeDuration, xdt:dayTimeDuration) as xdt:dayTimeDuration
                new Pair(new AtomicType[] { DayTimeDurationType.DAYTIME_DURATION,
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
            exec = NumericSubI.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DecimalType.DECIMAL)) {
            exec = NumericSubDecimal.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, FloatType.FLOAT)) {
            exec = NumericSubF.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DoubleType.DOUBLE)) {
            exec = NumericSubD.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DateTimeBaseType.BASE_DATETIME)) {
            exec = SubDurationFromDateTime.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DurationType.DURATION)) {
            if(args[0] instanceof DateTimeValue && args[1] instanceof DateTimeValue) {
                exec = DateTimeSub.INSTANCE;
            } else {
                exec = DurationSub.INSTANCE;
            }
        } else {
            throw new IllegalStateException("Unexpected return type: " + retType);
        }
        AtomicValue ret = exec.eval(ctxt, args[0], args[1]);
        return ret == null ? ValueSequence.EMPTY_SEQUENCE : ret;
    }

    private static final class NumericSubI extends Exec {
        static final NumericSubI INSTANCE = new NumericSubI();

        public XInteger eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            long i1 = asLong(v1, dynEnv);
            long i2 = asLong(v2, dynEnv);
            final long res = subtractL(i1, i2);
            return XInteger.valueOf(res);
        }
    }

    private static final class NumericSubF extends Exec {
        static final NumericSubF INSTANCE = new NumericSubF();

        public XFloat eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            float f1 = asFloat(v1, dynEnv);
            float f2 = asFloat(v2, dynEnv);
            float res = f1 - f2;
            return XFloat.valueOf(res);
        }
    }

    private static final class NumericSubD extends Exec {
        static final NumericSubD INSTANCE = new NumericSubD();

        public XDouble eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            double d1 = asDouble(v1, dynEnv);
            double d2 = asDouble(v2, dynEnv);
            final double res = d1 - d2;
            if(res == Double.POSITIVE_INFINITY) {
                throw new DynamicError("err:FOAR0002", "result overflow");
            }
            if(res == Double.NEGATIVE_INFINITY) {
                throw new DynamicError("err:FOAR0002", "result underflow");
            }
            return XDouble.valueOf(res);
        }
    }

    private static final class NumericSubDecimal extends Exec {
        static final NumericSubDecimal INSTANCE = new NumericSubDecimal();

        public XDecimal eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            BigDecimal dv1 = asDecimal(v1, dynEnv);
            BigDecimal dv2 = asDecimal(v2, dynEnv);
            BigDecimal res = dv1.subtract(dv2);
            return new XDecimal(res);
        }
    }

    private static final class SubDurationFromDateTime extends Exec {
        static final SubDurationFromDateTime INSTANCE = new SubDurationFromDateTime();

        public DateTimeValue eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            final DurationValue d1;
            final DateTimeValue dt2;
            if(v1 instanceof DurationValue) {
                d1 = (DurationValue) v1;
                dt2 = (DateTimeValue) v2;
            } else {
                d1 = (DurationValue) v2;
                dt2 = (DateTimeValue) v1;
            }
            XMLGregorianCalendar cal2 = dt2.getValue();
            cal2.add(d1.getValue().negate());
            return new DateTimeValue(cal2, dt2.getDateTimeType());
        }
    }

    private static final class DurationSub extends Exec {
        static final DurationSub INSTANCE = new DurationSub();

        public DurationValue eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            DurationValue d1 = (DurationValue) v1;
            DurationValue d2 = (DurationValue) v2;
            Duration dv1 = d1.getValue();
            Duration res = dv1.subtract(d2.getValue());
            DurationType dt1 = d1.getDurationType();
            DurationType dt2 = d2.getDurationType();
            if(dt1 == dt2) {
                return new DurationValue(res, dt1);
            } else {
                return new DurationValue(res);
            }
        }
    }

    private static final class DateTimeSub extends Exec {
        static final DateTimeSub INSTANCE = new DateTimeSub();

        public DurationValue eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            DateTimeValue d1 = (DateTimeValue) v1;
            DateTimeValue d2 = (DateTimeValue) v2;
            long l1 = d1.getValue().toGregorianCalendar().getTimeInMillis();
            long l2 = d2.getValue().toGregorianCalendar().getTimeInMillis();
            Duration d = XsDatatypeFactory.createDuration(l1 - l2);
            DateTimeBaseType dt1 = d1.getDateTimeType();
            DateTimeBaseType dt2 = d2.getDateTimeType();
            if(dt1 == dt2 && (dt1.getTypeId() == TypeTable.YEAR_MONTH_DURATION_TID)) {
                return new DurationValue(d, YearMonthDurationType.YEARMONTH_DURATION);
            } else {
                return new DurationValue(d, DayTimeDurationType.DAYTIME_DURATION);
            }
        }
    }

}
