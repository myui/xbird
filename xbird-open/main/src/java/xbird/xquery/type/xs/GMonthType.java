/*
 * @(#)$Id: GMonthType.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.xsi.GregorianDateTimeValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.XsDatatypeFactory;
import xbird.xquery.type.*;

/**
 * gMonth is a gregorian month that recurs every year.
 * <DIV lang="en">
 * The lexical representation for gMonth is the left and 
 * right truncated lexical representation for date: --MM.
 * An optional following time zone qualifier is allowed as for date.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class GMonthType extends GregorianBaseType {
    private static final long serialVersionUID = 4873426469372749878L;
    public static final String SYMBOL = "xs:gMonth";

    public static final GMonthType GMONTH = new GMonthType();

    public GMonthType() {
        super(TypeTable.GMONTH_TID, SYMBOL);
    }

    public GregorianDateTimeValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        if(literal == null) {
            throw new IllegalArgumentException();
        }
        final int strlen = literal.length();
        if(strlen < 4 || !literal.startsWith("--")) {
            throw new DynamicError("err:FORG0001", "Illegal value for xs:gMonth: " + literal);
        }
        StringBuilder buf = new StringBuilder(strlen + 2);
        buf.append(literal.substring(0, 4));
        // [workaround] gMonth, --MM(z?), but per XML Schema Errata, used to be --MM--(z?)
        buf.append("--");
        if(strlen >= 5) {
            buf.append(literal.substring(4));
        }
        final String gmonth = buf.toString();
        return new GregorianDateTimeValue(gmonth, this);
    }

    public GregorianDateTimeValue createInstance(XMLGregorianCalendar value) {
        final DatatypeFactory factory = XsDatatypeFactory.getDatatypeFactory();
        int month = value.getMonth();
        int tz = value.getTimezone();
        XMLGregorianCalendar cal = factory.newXMLGregorianCalendarDate(DatatypeConstants.FIELD_UNDEFINED, month, DatatypeConstants.FIELD_UNDEFINED, tz);
        return new GregorianDateTimeValue(cal, GMONTH);
    }

    @Override
    public boolean isDaySet() {
        return false;
    }

    @Override
    public boolean isYearSet() {
        return false;
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof GMonthType;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_GMONTH;
    }
}
