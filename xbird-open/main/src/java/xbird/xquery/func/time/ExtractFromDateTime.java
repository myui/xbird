/*
 * @(#)$Id: ExtractFromDateTime.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.time;

import java.math.BigDecimal;

import javax.xml.datatype.*;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XDecimal;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.DateTimeValue;
import xbird.xquery.dm.value.xsi.DurationValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.XsDatatypeFactory;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.DayTimeDurationType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class ExtractFromDateTime extends BuiltInFunction {

    protected ExtractFromDateTime(String funcName, Type retType) {
        super(funcName, retType);
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        final int arglen = argv.size();
        assert (arglen == 1) : arglen;
        Item first = argv.getItem(0);
        if(first.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        DateTimeValue arg = (DateTimeValue) first;
        AtomicValue extracted = extract(arg);
        if(extracted == null) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        return extracted;
    }

    public abstract AtomicValue extract(DateTimeValue arg);

    /**
     * fn:day-from-dateTime($arg as xs:dateTime?) as xs:integer?.
     * 
     * @link http://www.w3.org/TR/xquery-operators/#func-day-from-dateTime
     */
    public static class DayFromDateTime extends ExtractFromDateTime {
        private static final long serialVersionUID = 2192628550013189015L;
        public static final String SYMBOL = "fn:day-from-dateTime";

        public DayFromDateTime() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        protected DayFromDateTime(String symbol, Type retType) {
            super(symbol, retType);
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:dateTime?") });
            return s;
        }

        public XInteger extract(DateTimeValue arg) {
            XMLGregorianCalendar cal = arg.getValue();
            final int day = cal.getDay();
            if(day == DatatypeConstants.FIELD_UNDEFINED) {
                return null;
            }
            return XInteger.valueOf(day);
        }
    }

    /**
     * fn:day-from-date($arg as xs:date?) as xs:integer?.
     * 
     * @link http://www.w3.org/TR/xquery-operators/#func-day-from-date
     */
    public static final class DayFromDate extends DayFromDateTime {
        private static final long serialVersionUID = -4947694320039477830L;
        public static final String SYMBOL = "fn:day-from-date";

        public DayFromDate() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        @Override
        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:date?") });
            return s;
        }
    }

    /**
     * fn:hours-from-dateTime($arg as xs:dateTime?) as xs:integer?.
     * 
     * @link http://www.w3.org/TR/xquery-operators/#func-hours-from-dateTime
     */
    public static class HoursFromDateTime extends ExtractFromDateTime {
        private static final long serialVersionUID = 336253051125432653L;
        public static final String SYMBOL = "fn:hours-from-dateTime";

        public HoursFromDateTime() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        protected HoursFromDateTime(String symbol, Type retType) {
            super(symbol, retType);
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:dateTime?") });
            return s;
        }

        public XInteger extract(DateTimeValue arg) {
            XMLGregorianCalendar cal = arg.getValue();
            final int hour = cal.getHour();
            if(hour == DatatypeConstants.FIELD_UNDEFINED) {
                return null;
            }
            return XInteger.valueOf(hour);
        }
    }

    /**
     * fn:hours-from-time($arg as xs:time?) as xs:integer?.
     */
    public static final class HoursFromTime extends HoursFromDateTime {
        private static final long serialVersionUID = 7951676386726204476L;
        public static final String SYMBOL = "fn:hours-from-time";

        public HoursFromTime() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        @Override
        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:time?") });
            return s;
        }
    }

    /**
     * fn:minutes-from-dateTime($arg as xs:dateTime?) as xs:integer?.
     */
    public static class MinutesFromDateTime extends ExtractFromDateTime {
        private static final long serialVersionUID = 8191074624462015617L;
        public static final String SYMBOL = "fn:minutes-from-dateTime";

        public MinutesFromDateTime() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        public MinutesFromDateTime(String symbol, Type type) {
            super(symbol, type);
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:dateTime?") });
            return s;
        }

        public XInteger extract(DateTimeValue arg) {
            XMLGregorianCalendar cal = arg.getValue();
            final int minutes = cal.getMinute();
            if(minutes == DatatypeConstants.FIELD_UNDEFINED) {
                return null;
            }
            return XInteger.valueOf(minutes);
        }
    }

    /**
     * fn:minutes-from-time($arg as xs:time?) as xs:integer?.
     */
    public static final class MinutesFromTime extends MinutesFromDateTime {
        private static final long serialVersionUID = 1339276923707200499L;
        public static final String SYMBOL = "fn:minutes-from-time";

        public MinutesFromTime() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        @Override
        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:time?") });
            return s;
        }
    }

    /**
     * fn:month-from-dateTime($arg as xs:dateTime?) as xs:integer?.
     */
    public static class MonthFromDateTime extends ExtractFromDateTime {
        private static final long serialVersionUID = 5083767417656275600L;
        public static final String SYMBOL = "fn:month-from-dateTime";

        public MonthFromDateTime() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        public MonthFromDateTime(String symbol, Type type) {
            super(symbol, type);
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:dateTime?") });
            return s;
        }

        public XInteger extract(DateTimeValue arg) {
            XMLGregorianCalendar cal = arg.getValue();
            final int month = cal.getMonth();
            if(month == DatatypeConstants.FIELD_UNDEFINED) {
                return null;
            }
            return XInteger.valueOf(month);
        }
    }

    /**
     * fn:month-from-date($arg as xs:date?) as xs:integer?.
     */
    public static final class MonthFromDate extends MonthFromDateTime {
        private static final long serialVersionUID = -4358295313565955336L;
        public static final String SYMBOL = "fn:month-from-date";

        public MonthFromDate() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        @Override
        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:date?") });
            return s;
        }
    }

    /**
     * fn:seconds-from-dateTime($arg as xs:dateTime?) as xs:decimal?.
     */
    public static class SecondsFromDateTime extends ExtractFromDateTime {
        private static final long serialVersionUID = 6188181716724681891L;
        public static final String SYMBOL = "fn:seconds-from-dateTime";

        public SecondsFromDateTime() {
            super(SYMBOL, TypeRegistry.safeGet("xs:decimal?"));
        }

        public SecondsFromDateTime(String symbol, Type type) {
            super(symbol, type);
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:dateTime?") });
            return s;
        }

        public XDecimal extract(DateTimeValue arg) {
            XMLGregorianCalendar cal = arg.getValue();
            double sec = cal.getSecond();
            if(sec == DatatypeConstants.FIELD_UNDEFINED) {
                return null;
            }
            double fracsec = cal.getMillisecond();
            if(fracsec != DatatypeConstants.FIELD_UNDEFINED) {
                double d = fracsec / 1000;
                sec += d;
            }
            return XDecimal.valueOf(BigDecimal.valueOf(sec));
        }
    }

    /**
     * fn:seconds-from-time($arg as xs:time?) as xs:decimal?.
     */
    public static final class SecondsFromTime extends SecondsFromDateTime {
        private static final long serialVersionUID = -1791978423117244729L;
        public static final String SYMBOL = "fn:seconds-from-time";

        public SecondsFromTime() {
            super(SYMBOL, TypeRegistry.safeGet("xs:decimal?"));
        }

        @Override
        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:time?") });
            return s;
        }
    }

    /**
     * fn:timezone-from-dateTime($arg as xs:dateTime?) as xdt:dayTimeDuration?.
     */
    public static class TimezoneFromDateTime extends ExtractFromDateTime {
        private static final long serialVersionUID = 1244792585322320623L;
        public static final String SYMBOL = "fn:timezone-from-dateTime";

        public TimezoneFromDateTime() {
            super(SYMBOL, TypeRegistry.safeGet("xs:dayTimeDuration?"));
        }

        public TimezoneFromDateTime(String symbol, Type type) {
            super(symbol, type);
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:dateTime?") });
            return s;
        }

        public DurationValue extract(DateTimeValue arg) {
            XMLGregorianCalendar cal = arg.getValue();
            int tzOffsetInMinutes = cal.getTimezone();
            if(tzOffsetInMinutes == DatatypeConstants.FIELD_UNDEFINED) {
                return null;
            }
            long offsetInMillis = tzOffsetInMinutes * 60 * 1000;
            final Duration dur = XsDatatypeFactory.createDuration(offsetInMillis);
            return new DurationValue(dur, DayTimeDurationType.DAYTIME_DURATION);
        }
    }

    /**
     * fn:timezone-from-date($arg as xs:date?) as xdt:dayTimeDuration?.
     */
    public static final class TimezoneFromDate extends TimezoneFromDateTime {
        private static final long serialVersionUID = -4157642638700964024L;
        public static final String SYMBOL = "fn:timezone-from-date";

        public TimezoneFromDate() {
            super(SYMBOL, TypeRegistry.safeGet("xs:dayTimeDuration?"));
        }

        @Override
        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:date?") });
            return s;
        }
    }

    /**
     * fn:timezone-from-time($arg as xs:time?) as xdt:dayTimeDuration?.
     */
    public static final class TimezoneFromTime extends TimezoneFromDateTime {
        private static final long serialVersionUID = -4706064127619389624L;
        public static final String SYMBOL = "fn:timezone-from-time";

        public TimezoneFromTime() {
            super(SYMBOL, TypeRegistry.safeGet("xs:dayTimeDuration?"));
        }

        @Override
        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:time?") });
            return s;
        }
    }

    /**
     * fn:year-from-dateTime($arg as xs:dateTime?) as xs:integer?.
     */
    public static class YearFromDateTime extends ExtractFromDateTime {
        private static final long serialVersionUID = -2774269693959565787L;
        public static final String SYMBOL = "fn:year-from-dateTime";

        public YearFromDateTime() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        public YearFromDateTime(String symbol, Type type) {
            super(symbol, type);
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:dateTime?") });
            return s;
        }

        public XInteger extract(DateTimeValue arg) {
            XMLGregorianCalendar cal = arg.getValue();
            final int year = cal.getYear();
            if(year == DatatypeConstants.FIELD_UNDEFINED) {
                return null;
            }
            return XInteger.valueOf(year);
        }
    }

    /**
     * fn:year-from-date($arg as xs:date?) as xs:integer?.
     */
    public static final class YearFromDate extends YearFromDateTime {
        private static final long serialVersionUID = 2709448315816290882L;
        public static final String SYMBOL = "fn:year-from-date";

        public YearFromDate() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        @Override
        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:date?") });
            return s;
        }
    }

}
