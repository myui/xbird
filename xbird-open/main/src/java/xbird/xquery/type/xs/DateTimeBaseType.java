/*
 * @(#)$Id: DateTimeBaseType.java 3619 2008-03-26 07:23:03Z yui $
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

import javax.xml.datatype.XMLGregorianCalendar;

import xbird.xqj.XQJConstants;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.xsi.DateTimeValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#casting-to-datetimes
 */
public abstract class DateTimeBaseType extends AtomicType {

    public static final DateTimeBaseType BASE_DATETIME = new DateTimeBaseType() {
        private static final long serialVersionUID = -7138510290768398302L;

        public Class getJavaObjectType() {
            return Object.class;
        }

        public AtomicValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
                throws XQueryException {
            throw new UnsupportedOperationException("DateTimeBaseType#createInstance is allowed");
        }

        @Override
        public DateTimeValue createInstance(XMLGregorianCalendar value) {
            throw new UnsupportedOperationException("DateTimeBaseType#createInstance is allowed");
        }

        @Override
        public boolean accepts(Type expected) {
            return expected instanceof DateTimeBaseType;
        }

        @Override
        public int getXQJBaseType() {
            return XQJConstants.XQBASETYPE_ANYATOMICTYPE; // REVIEWME
        }
    };

    public DateTimeBaseType() {
        super(TypeTable.DATETIME_BASE_TID, QNameTable.instantiate("", "dateTimeBase"), "dateTimeBase");
    }

    protected DateTimeBaseType(final int typeId, final String type) {
        super(typeId, type);
    }

    public abstract DateTimeValue createInstance(XMLGregorianCalendar value);

    public boolean isDateSet() {
        return isYearSet() || isMonthSet() || isDaySet();
    }

    public boolean isYearSet() {
        return true;
    }

    public boolean isMonthSet() {
        return true;
    }

    public boolean isDaySet() {
        return true;
    }

    public boolean isTimeSet() {
        return true;
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof DateTimeBaseType;
    }

}
