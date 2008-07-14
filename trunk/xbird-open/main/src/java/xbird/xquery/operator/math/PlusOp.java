/*
 * @(#)$Id: PlusOp.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.meta.StaticContext;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.operator.InternalFunction;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.*;

/**
 * fs:plus(A, B).
 * <DIV lang="en">
 * <u>Denotes</u>
 * <ul>
 * <u>Numeric + Numeric</u>
 *  <li>op:numeric-add(xs:integer, xs:integer) as xs:integer</li>
 *  <li>op:numeric-add(xs:decimal, xs:decimal) as xs:decimal</li>
 *  <li>op:numeric-add(xs:float, xs:float) as xs:float</li>
 *  <li>op:numeric-add(xs:double, xs:double) as xs:double</li>
 * <br/><u>DateTime + Duration</u><br/>
 *  <li>op:add-yearMonthDuration-to-date(xs:date, xdt:yearMonthDuration) as xs:date</li>
 *  <li>op:add-yearMonthDuration-to-date(xdt:yearMonthDuration, xs:date) as xs:date</li>
 *  <li>op:add-dayTimeDuration-to-date(xs:date, xdt:dayTimeDuration) as xs:date</li>
 *  <li>op:add-dayTimeDuration-to-date(xdt:dayTimeDuration, xs:date) as xs:date</li>
 *  <li>op:add-dayTimeDuration-to-time(xs:time, xdt:dayTimeDuration) as xs:time</li>
 *  <li>op:add-dayTimeDuration-to-time(xdt:dayTimeDuration, xs:time) as xs:time</li>
 *  <li>op:add-yearMonthDuration-to-dateTime(xs:dateTime, xdt:yearMonthDuration) as xs:dateTime</li>
 *  <li>op:add-yearMonthDuration-to-dateTime(xdt:yearMonthDuration, xs:dateTime) as xs:dateTime</li>
 *  <li>op:add-dayTimeDuration-to-dateTime(xs:dateTime, xdt:dayTimeDuration) as xs:dateTime</li>
 *  <li>op:add-dayTimeDuration-to-dateTime(xdt:dayTimeDuration, xs:dateTime) as xs:dateTime</li>
 * <br/><u>Duration + Duraion</u><br/>
 *  <li>op:add-yearMonthDurations(xdt:yearMonthDuration, xdt:yearMonthDuration) as xdt:yearMonthDuration</li>
 *  <li>op:add-dayTimeDurations(xdt:dayTimeDuration, xdt:dayTimeDuration) as xdt:dayTimeDuration</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#op.numeric
 * @link http://www.w3.org/TR/xmlschema-2/#adding-durations-to-dateTimes
 */
public class PlusOp extends NumericOp {
    private static final long serialVersionUID = -4869081525731294694L;
    public static final String SYMBOL = "fs:plus";

    public PlusOp() {
        super(SYMBOL);
    }

    @SuppressWarnings("unchecked")
    protected final Pair<AtomicType[], AtomicType>[] signatures() {
        final Pair<AtomicType[], AtomicType>[] s = new Pair[] {
                // op:numeric-add(xs:integer, xs:integer) as xs:integer
                new Pair(new AtomicType[] { IntegerType.INTEGER, IntegerType.INTEGER }, IntegerType.INTEGER),
                // op:numeric-add(xs:decimal, xs:decimal) as xs:decimal
                new Pair(new AtomicType[] { DecimalType.DECIMAL, DecimalType.DECIMAL }, DecimalType.DECIMAL),
                // op:numeric-add(xs:float, xs:float) as xs:float
                new Pair(new AtomicType[] { FloatType.FLOAT, FloatType.FLOAT }, FloatType.FLOAT),
                // workaround
                new Pair(new AtomicType[] { DecimalType.DECIMAL, FloatType.FLOAT }, FloatType.FLOAT),
                new Pair(new AtomicType[] { FloatType.FLOAT, DecimalType.DECIMAL }, FloatType.FLOAT),
                // op:numeric-add(xs:double, xs:double) as xs:double
                new Pair(new AtomicType[] { DoubleType.DOUBLE, DoubleType.DOUBLE }, DoubleType.DOUBLE),
                // op:add-yearMonthDuration-to-date(xs:date, xdt:yearMonthDuration) as xs:date
                new Pair(new AtomicType[] { DateType.DATE, YearMonthDurationType.YEARMONTH_DURATION }, DateType.DATE),
                // op:add-yearMonthDuration-to-date(xdt:yearMonthDuration, xs:date) as xs:date
                new Pair(new AtomicType[] { YearMonthDurationType.YEARMONTH_DURATION, DateType.DATE }, DateType.DATE),
                // op:add-dayTimeDuration-to-date(xs:date, xdt:dayTimeDuration) as xs:date
                new Pair(new AtomicType[] { DateType.DATE, DayTimeDurationType.DAYTIME_DURATION }, DateType.DATE),
                // op:add-dayTimeDuration-to-date(xdt:dayTimeDuration, xs:date) as xs:date
                new Pair(new AtomicType[] { DayTimeDurationType.DAYTIME_DURATION, DateType.DATE }, DateType.DATE),
                // op:add-dayTimeDuration-to-time(xs:time, xdt:dayTimeDuration) as xs:time
                new Pair(new AtomicType[] { TimeType.TIME, DayTimeDurationType.DAYTIME_DURATION }, TimeType.TIME),
                // op:add-dayTimeDuration-to-time(xdt:dayTimeDuration, xs:time) as xs:time
                new Pair(new AtomicType[] { DayTimeDurationType.DAYTIME_DURATION, TimeType.TIME }, TimeType.TIME),
                // op:add-yearMonthDuration-to-dateTime(xs:dateTime, xdt:yearMonthDuration) as xs:dateTime
                new Pair(new AtomicType[] { DateTimeType.DATETIME,
                        YearMonthDurationType.YEARMONTH_DURATION }, DateTimeType.DATETIME),
                // op:add-yearMonthDuration-to-dateTime(xdt:yearMonthDuration, xs:dateTime) as xs:dateTime
                new Pair(new AtomicType[] { YearMonthDurationType.YEARMONTH_DURATION,
                        DateTimeType.DATETIME }, DateTimeType.DATETIME),
                // op:add-dayTimeDuration-to-dateTime(xs:dateTime, xdt:dayTimeDuration) as xs:dateTime
                new Pair(new AtomicType[] { DateTimeType.DATETIME,
                        DayTimeDurationType.DAYTIME_DURATION }, DateTimeType.DATETIME),
                // op:add-dayTimeDuration-to-dateTime(xdt:dayTimeDuration, xs:dateTime) as xs:dateTime
                new Pair(new AtomicType[] { DayTimeDurationType.DAYTIME_DURATION,
                        DateTimeType.DATETIME }, DateTimeType.DATETIME),
                // op:add-yearMonthDurations(xdt:yearMonthDuration, xdt:yearMonthDuration) as xdt:yearMonthDuration
                new Pair(new AtomicType[] { YearMonthDurationType.YEARMONTH_DURATION,
                        YearMonthDurationType.YEARMONTH_DURATION }, YearMonthDurationType.YEARMONTH_DURATION),
                // op:add-dayTimeDurations(xdt:dayTimeDuration, xdt:dayTimeDuration) as xdt:dayTimeDuration
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
        final AtomicType retType = getReturnType();
        final Exec exec;
        if(TypeUtil.subtypeOf(retType, IntegerType.INTEGER)) {
            exec = NumericAddI.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DecimalType.DECIMAL)) {
            exec = NumericAddDecimal.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, FloatType.FLOAT)) {
            exec = NumericAddF.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DoubleType.DOUBLE)) {
            exec = NumericAddD.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DateTimeBaseType.BASE_DATETIME)) {
            exec = AddDurationToDateTime.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DurationType.DURATION)) {
            exec = DurationAdd.INSTANCE;
        } else {
            throw new IllegalStateException("Unexpected return type: " + retType);
        }
        final AtomicValue ret = exec.eval(ctxt, args[0], args[1]);
        return ret == null ? ValueSequence.EMPTY_SEQUENCE : ret;
    }

    private static final class NumericAddI extends Exec {
        static final NumericAddI INSTANCE = new NumericAddI();

        public XInteger eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            long l1 = asLong(v1, dynEnv);
            long l2 = asLong(v2, dynEnv);
            final long res = addL(l1, l2);
            return XInteger.valueOf(res);
        }
    }

    private static final class NumericAddF extends Exec {
        static final NumericAddF INSTANCE = new NumericAddF();

        public XFloat eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            float f1 = asFloat(v1, dynEnv);
            float f2 = asFloat(v2, dynEnv);
            float res = f1 + f2;
            return XFloat.valueOf(res);
        }
    }

    private static final class NumericAddD extends Exec {
        static final NumericAddD INSTANCE = new NumericAddD();

        public XDouble eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            double d1 = asDouble(v1, dynEnv);
            double d2 = asDouble(v2, dynEnv);
            final double res = d1 + d2;
            if(res == Double.POSITIVE_INFINITY) {
                throw new DynamicError("err:FOAR0002", "result overflow");
            }
            if(res == Double.NEGATIVE_INFINITY) {
                throw new DynamicError("err:FOAR0002", "result underflow");
            }
            return XDouble.valueOf(res);
        }
    }

    private static final class NumericAddDecimal extends Exec {
        static final NumericAddDecimal INSTANCE = new NumericAddDecimal();

        public XDecimal eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            BigDecimal dv1 = asDecimal(v1, dynEnv);
            BigDecimal dv2 = asDecimal(v2, dynEnv);
            BigDecimal res = dv1.add(dv2);
            return new XDecimal(res);
        }
    }

    /**
     * @link http://www.w3.org/TR/xmlschema-2/#adding-durations-to-dateTimes
     * @link http://www.w3.org/TR/xmlschema-2/#dateTime
     */
    private static final class AddDurationToDateTime extends Exec {
        static final AddDurationToDateTime INSTANCE = new AddDurationToDateTime();

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
            cal2.add(d1.getValue());
            return new DateTimeValue(cal2, dt2.getDateTimeType());
        }
    }

    private static final class DurationAdd extends Exec {
        static final DurationAdd INSTANCE = new DurationAdd();

        public DurationValue eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            DurationValue d1 = (DurationValue) v1;
            DurationValue d2 = (DurationValue) v2;
            Duration dv1 = d1.getValue();
            Duration res = dv1.add(d2.getValue());
            DurationType dt1 = d1.getDurationType();
            DurationType dt2 = d2.getDurationType();
            if(dt1 == dt2) {
                return new DurationValue(res, dt1);
            } else {
                return new DurationValue(res);
            }
        }
    }

}
