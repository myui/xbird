/*
 * @(#)$Id: XsDatatypeFactory.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.misc;

import java.util.GregorianCalendar;

import javax.xml.datatype.*;

import xbird.xquery.XQRTException;
import xbird.xquery.type.xs.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xmlschema-2/#isoformats
 * @link http://www.w3.org/TR/xmlschema-2/#dateTime-order
 */
public final class XsDatatypeFactory {

    private static final DatatypeFactory factory;
    static {
        try {
            factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new XQRTException("failed to create DatatypeFactory instance!", e);
        }
    }

    private XsDatatypeFactory() {}

    public static DatatypeFactory getDatatypeFactory() {
        return factory;
    }

    public static XMLGregorianCalendar createXMLGregorianCalendar(final String literal) {
        return factory.newXMLGregorianCalendar(literal);
    }

    public static XMLGregorianCalendar createXMLGregorianCalendar(final GregorianCalendar cal) {
        return factory.newXMLGregorianCalendar(cal);
    }

    public static Duration createDuration(final String literal) {
        return factory.newDuration(literal);
    }

    public static Duration createDuration(final long durationInMills) {
        return factory.newDuration(durationInMills);
    }

    public static Duration createDuration(final String literal, DurationType targetType) {
        if(targetType instanceof DayTimeDurationType) {
            return factory.newDurationDayTime(literal);
        } else if(targetType instanceof YearMonthDurationType) {
            return factory.newDurationYearMonth(literal);
        } else {
            return factory.newDuration(literal);
        }
    }

    public static Duration createDuration(final Duration src, final DurationType targetType) {
        if(targetType instanceof DayTimeDurationType) {
            return factory.newDurationDayTime(src.getSign() == 1, src.getDays(), src.getHours(), src.getMinutes(), src.getSeconds());
        } else if(targetType instanceof YearMonthDurationType) {
            return factory.newDurationYearMonth(src.getSign() == 1, src.getYears(), src.getMonths());
        } else {
            return src;
        }
    }

    public static Duration createYearMonthDuration(long months) {
        final boolean sign;
        if(months < 0L) {
            sign = false;
            months = -months;
        } else {
            sign = true;
        }
        int y = (int) (months / 12);
        int m = (int) (months % 12);
        return factory.newDurationYearMonth(sign, y, m);
    }

    public static Duration createYearMonthDuration(final boolean isPositive, final int year, final int month) {
        return factory.newDurationYearMonth(isPositive, year, month);
    }

}
