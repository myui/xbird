/*
 * @(#)$Id: ExtractFromDuration.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XDecimal;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.DurationValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class ExtractFromDuration extends BuiltInFunction {

    protected ExtractFromDuration(String funcName, Type retType) {
        super(funcName, retType);
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null && argv.size() == 1);
        Item first = argv.getItem(0);
        if(first.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        assert (first instanceof DurationValue);
        DurationValue arg = (DurationValue) first;
        return extract(arg);
    }

    public abstract AtomicValue extract(DurationValue arg);

    /**
     * fn:days-from-duration($arg as xdt:dayTimeDuration?) as xs:integer?.
     */
    public static final class DaysFromDuration extends ExtractFromDuration {
        private static final long serialVersionUID = -4292534887923835225L;
        public static final String SYMBOL = "fn:days-from-duration";

        public DaysFromDuration() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:dayTimeDuration?") });
            return s;
        }

        public XInteger extract(DurationValue arg) {
            final int day = arg.getDays();
            return XInteger.valueOf(day);
        }
    }

    /**
     * fn:hours-from-duration($arg as xdt:dayTimeDuration?) as xs:integer?.
     */
    public static final class HoursFromDuration extends ExtractFromDuration {
        private static final long serialVersionUID = -7650684330203756111L;
        public static final String SYMBOL = "fn:hours-from-duration";

        public HoursFromDuration() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:dayTimeDuration?") });
            return s;
        }

        public XInteger extract(DurationValue arg) {
            final int hours = arg.getHours();
            return XInteger.valueOf(hours);
        }
    }

    /**
     * fn:minutes-from-duration($arg as xdt:dayTimeDuration?) as xs:integer?.
     */
    public static final class MinutesFromDuration extends ExtractFromDuration {
        private static final long serialVersionUID = 5405672214598184732L;
        public static final String SYMBOL = "fn:minutes-from-duration";

        public MinutesFromDuration() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:dayTimeDuration?") });
            return s;
        }

        public XInteger extract(DurationValue arg) {
            final int minutes = arg.getMinutes();
            return XInteger.valueOf(minutes);
        }
    }

    /**
     * fn:months-from-duration($arg as xdt:yearMonthDuration?) as xs:integer?.
     */
    public static final class MonthsFromDuration extends ExtractFromDuration {
        private static final long serialVersionUID = 5561751943292591702L;
        public static final String SYMBOL = "fn:months-from-duration";

        public MonthsFromDuration() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:yearMonthDuration?") });
            return s;
        }

        public XInteger extract(DurationValue arg) {
            final int months = arg.getMonths();
            return XInteger.valueOf(months);
        }
    }

    /**
     * fn:seconds-from-duration($arg as xdt:dayTimeDuration?) as xs:decimal?.
     */
    public static final class SecondsFromDuration extends ExtractFromDuration {
        private static final long serialVersionUID = -8357466350629147731L;
        public static final String SYMBOL = "fn:seconds-from-duration";

        public SecondsFromDuration() {
            super(SYMBOL, TypeRegistry.safeGet("xs:decimal?"));
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:dayTimeDuration?") });
            return s;
        }

        public XDecimal extract(DurationValue arg) {
            final int sec = arg.getSeconds();
            return XDecimal.valueOf(new BigDecimal(sec));
        }
    }

    /**
     * fn:years-from-duration($arg as xdt:yearMonthDuration?) as xs:integer?.
     */
    public static final class YearsFromDuration extends ExtractFromDuration {
        private static final long serialVersionUID = -1609385472197333314L;
        public static final String SYMBOL = "fn:years-from-duration";

        public YearsFromDuration() {
            super(SYMBOL, TypeRegistry.safeGet("xs:integer?"));
        }

        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:yearMonthDuration?") });
            return s;
        }

        public XInteger extract(DurationValue arg) {
            final int years = arg.getYears();
            return XInteger.valueOf(years);
        }
    }

}
