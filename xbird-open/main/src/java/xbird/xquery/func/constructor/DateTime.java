/*
 * @(#)$Id: DateTime.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.constructor;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.DateTimeValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.*;

/**
 * fn:dateTime($arg1 as xs:date, $arg2 as xs:time) as xs:dateTime.
 * <DIV lang="en">
 * A special constructor function is provided for constructing a xs:dateTime 
 * value from a xs:date value and a xs:time value.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-dateTime
 */
public final class DateTime extends BuiltInFunction {
    private static final long serialVersionUID = -8630350815622049802L;

    public static final String SYMBOL = "fn:dateTime";

    public DateTime() {
        super(SYMBOL, DateTimeType.DATETIME);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { DateType.DATE, TimeType.TIME });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        Item arg1 = argv.getItem(0);
        Item arg2 = argv.getItem(1);
        DateTimeValue date = (DateTimeValue) arg1;
        DateTimeValue time = (DateTimeValue) arg2;
        XMLGregorianCalendar dateValue = date.getValue();
        XMLGregorianCalendar newValue = (XMLGregorianCalendar) dateValue.clone();
        XMLGregorianCalendar timeValue = time.getValue();
        final int timeTz = timeValue.getTimezone();
        if(timeTz != DatatypeConstants.FIELD_UNDEFINED) {
            final int dateTz = newValue.getTimezone();
            if(dateTz != DatatypeConstants.FIELD_UNDEFINED && dateTz != timeTz) {
                throw new DynamicError("err:FORG0008", "fn:dateTime(" + date + ", " + time
                        + ") for `" + dateValue.getTimeZone(dateTz).getID() + " and `"
                        + timeValue.getTimeZone(timeTz).getID() + "` is not allowed.");
            }
            newValue.setTimezone(timeTz);
        }
        newValue.setTime(timeValue.getHour(), timeValue.getMinute(), timeValue.getSecond(), timeValue.getMillisecond());
        DateTimeValue dt = new DateTimeValue(newValue, DateTimeType.DATETIME);
        return dt;
    }

}
