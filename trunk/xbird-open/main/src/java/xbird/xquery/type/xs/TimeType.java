/*
 * @(#)$Id: TimeType.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.type.xs;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import xbird.xqj.XQJConstants;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.xsi.DateTimeValue;
import xbird.xquery.dm.value.xsi.GregorianDateTimeValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.XsDatatypeFactory;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class TimeType extends DateTimeBaseType {
    private static final long serialVersionUID = -6720367215411294847L;
    public static final String SYMBOL = "xs:time";

    public static final TimeType TIME = new TimeType();

    public TimeType() {
        super(TypeTable.TIME_TID, SYMBOL);
    }

    public Class getJavaObjectType() {
        return Date.class;
    }

    public AtomicValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        return new DateTimeValue(literal, this);
    }

    public DateTimeValue createInstance(XMLGregorianCalendar value) {
        final DatatypeFactory factory = XsDatatypeFactory.getDatatypeFactory();
        int hour = value.getHour();
        int minute = value.getMinute();
        int sec = value.getSecond();
        BigDecimal fsec = value.getFractionalSecond();
        int tz = value.getTimezone();
        XMLGregorianCalendar cal = factory.newXMLGregorianCalendarTime(hour, minute, sec, fsec, tz);
        return new GregorianDateTimeValue(cal, TIME);
    }

    @Override
    public boolean isDateSet() {
        return false;
    }

    @Override
    public boolean isDaySet() {
        return false;
    }

    @Override
    public boolean isMonthSet() {
        return false;
    }

    @Override
    public boolean isYearSet() {
        return false;
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof TimeType;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_TIME;
    }
}
