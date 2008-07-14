/*
 * @(#)$Id: GYearMonthType.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.AtomicValue;
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
public final class GYearMonthType extends GregorianBaseType {
    private static final long serialVersionUID = 4334296427526552257L;
    public static final String SYMBOL = "xs:gYearMonth";

    public static final GYearMonthType GYEARMONTH = new GYearMonthType();

    public GYearMonthType() {
        super(TypeTable.GYEAR_MONTH_TID, SYMBOL);
    }

    public AtomicValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        return new GregorianDateTimeValue(literal, this);
    }

    public GregorianDateTimeValue createInstance(XMLGregorianCalendar value) {
        final DatatypeFactory factory = XsDatatypeFactory.getDatatypeFactory();
        int year = value.getYear();
        int month = value.getMonth();
        int tz = value.getTimezone();
        XMLGregorianCalendar cal = factory.newXMLGregorianCalendarDate(year, month, DatatypeConstants.FIELD_UNDEFINED, tz);
        return new GregorianDateTimeValue(cal, GYEARMONTH);
    }

    @Override
    public boolean isDaySet() {
        return false;
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof GYearMonthType;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_GYEARMONTH;
    }

}
