/*
 * @(#)$Id: AdjustTimezone.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.TimeZone;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.DateTimeValue;
import xbird.xquery.dm.value.xsi.DurationValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.TypeUtil;
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
 * @link http://www.w3.org/TR/xquery-operators/#timezone.functions
 */
public abstract class AdjustTimezone extends BuiltInFunction {

    public AdjustTimezone(String funcName, Type retType) {
        super(funcName, retType);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        s[0] = new FunctionSignature(getName(), new Type[] { getReturnType() });
        s[1] = new FunctionSignature(getName(), new Type[] { getReturnType(),
                TypeRegistry.safeGet("xs:dayTimeDuration?") });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        final int arglen = argv.size();
        Item first = argv.getItem(0);
        DateTimeValue arg = (DateTimeValue) first;
        XMLGregorianCalendar cal = arg.getValue();
        final long utcOffsetInMillis;
        if(arglen == 1) {
            TimeZone tz = dynEnv.implicitTimezone();
            utcOffsetInMillis = tz.getRawOffset();
        } else if(arglen == 2) {
            Item sec = argv.getItem(1);
            if(sec.isEmpty()) {
                cal.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
                return arg;
            } else {
                Type secType = sec.getType();
                if(!TypeUtil.subtypeOf(secType, DayTimeDurationType.DAYTIME_DURATION)) {
                    throw new IllegalStateException("second argument is expected to be xdt:dayTimeDuration, but was "
                            + secType);
                }
                DurationValue dv = (DurationValue) sec;
                utcOffsetInMillis = dv.getTimeInMillis();
            }
        } else {
            throw new IllegalStateException("Illegal argument length: " + arglen);
        }
        final int offsetInMinutes = (int) (utcOffsetInMillis / 60000);
        final int origTimeZoneInMinutes = cal.getTimezone();
        if(origTimeZoneInMinutes == DatatypeConstants.FIELD_UNDEFINED) {
            cal.setTimezone(offsetInMinutes);
        } else if(offsetInMinutes != origTimeZoneInMinutes) {
            int origMillis = cal.getMillisecond();
            int diffInMinutes = offsetInMinutes - origTimeZoneInMinutes;
            long diffInMills = diffInMinutes * 60000;
            cal.add(XsDatatypeFactory.createDuration(diffInMills));
            cal.setMillisecond(origMillis); // workaround            
            cal.setTimezone(offsetInMinutes);
            cal.normalize();
        }
        return arg;
    }

    /**
     * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at all.
     * <DIV lang="en">
     * <ul>
     * <li>fn:adjust-dateTime-to-timezone($arg as xs:dateTime?) as xs:dateTime?</li>
     * <li>fn:adjust-dateTime-to-timezone($arg as xs:dateTime?, $timezone as xdt:dayTimeDuration?) as xs:dateTime?</li>
     * </ul>
     * </DIV>
     * <DIV lang="ja"></DIV>
     * @link http://www.w3.org/TR/xquery-operators/#func-adjust-dateTime-to-timezone
     */
    public static final class AdjustDateTimeToTimezone extends AdjustTimezone {
        private static final long serialVersionUID = -6189017053604627870L;

        public static final String SYMBOL = "fn:adjust-dateTime-to-timezone";

        public AdjustDateTimeToTimezone() {
            super(SYMBOL, TypeRegistry.safeGet("xs:dateTime?"));
        }
    }

    /**
     * fn:adjust-date-to-timezone.
     * <DIV lang="en">
     * <ul>
     * <li>fn:adjust-date-to-timezone($arg as xs:date?) as xs:date?</li>
     * <li>fn:adjust-date-to-timezone($arg as xs:date?, $timezone as xdt:dayTimeDuration?) as xs:date?</li>
     * </ul>
     * </DIV>
     * <DIV lang="ja"></DIV>
     */
    public static final class AdjustDateToTimezone extends AdjustTimezone {
        private static final long serialVersionUID = 4087072939001092343L;

        public static final String SYMBOL = "fn:adjust-date-to-timezone";

        public AdjustDateToTimezone() {
            super(SYMBOL, TypeRegistry.safeGet("xs:date?"));
        }
    }

    /**
     * fn:adjust-time-to-timezone.
     * <DIV lang="en">
     * <ul>
     * <li>fn:adjust-time-to-timezone($arg as xs:time?) as xs:time?</li>
     * <li>fn:adjust-time-to-timezone($arg as xs:time?, $timezone as xdt:dayTimeDuration?) as xs:time?</li>
     * </ul>
     * </DIV>
     * <DIV lang="ja"></DIV>
     */
    public static final class AdjustTimeToTimezone extends AdjustTimezone {
        private static final long serialVersionUID = -2197210300406109895L;

        public static final String SYMBOL = "fn:adjust-time-to-timezone";

        public AdjustTimeToTimezone() {
            super(SYMBOL, TypeRegistry.safeGet("xs:time?"));
        }

    }

}
