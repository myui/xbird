/*
 * @(#)$Id: GDayType.java 3619 2008-03-26 07:23:03Z yui $
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

import javax.xml.datatype.*;

import xbird.xqj.XQJConstants;
import xbird.xquery.XQueryException;
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
public final class GDayType extends GregorianBaseType {
    private static final long serialVersionUID = -458749519772620374L;
    public static final String SYMBOL = "xs:gDay";

    public static final GDayType GDAY = new GDayType();

    public GDayType() {
        super(TypeTable.GDAY_TID, SYMBOL);
    }

    public GregorianDateTimeValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        return new GregorianDateTimeValue(literal, this);
    }

    public GregorianDateTimeValue createInstance(XMLGregorianCalendar value) {
        final DatatypeFactory factory = XsDatatypeFactory.getDatatypeFactory();
        int day = value.getDay();
        int tz = value.getTimezone();
        XMLGregorianCalendar cal = factory.newXMLGregorianCalendarDate(DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, day, tz);
        return new GregorianDateTimeValue(cal, GDAY);
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
        return expected instanceof GDayType;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_GDAY;
    }

}
