/*
 * @(#)$Id: DateType.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.Date;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import xbird.xqj.XQJConstants;
import xbird.xquery.XQueryException;
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
public final class DateType extends DateTimeBaseType {
    private static final long serialVersionUID = 3205307048271414430L;
    public static final String SYMBOL = "xs:date";

    public static final DateType DATE = new DateType();

    public DateType() {
        super(TypeTable.DATE_TID, SYMBOL);
    }

    public Class getJavaObjectType() {
        return Date.class;
    }

    public DateTimeValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        return new DateTimeValue(literal, this);
    }

    public DateTimeValue createInstance(XMLGregorianCalendar value) {
        final DatatypeFactory factory = XsDatatypeFactory.getDatatypeFactory();
        int year = value.getYear();
        int month = value.getMonth();
        int day = value.getDay();
        int tz = value.getTimezone();
        XMLGregorianCalendar cal = factory.newXMLGregorianCalendarDate(year, month, day, tz);
        return new GregorianDateTimeValue(cal, DATE);
    }

    @Override
    public boolean isTimeSet() {
        return false;
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof DateType;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_DATE;
    }

}
