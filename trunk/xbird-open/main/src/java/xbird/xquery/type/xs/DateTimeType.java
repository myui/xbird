/*
 * @(#)$Id: DateTimeType.java 3619 2008-03-26 07:23:03Z yui $
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

import javax.xml.datatype.XMLGregorianCalendar;

import xbird.xqj.XQJConstants;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.xsi.DateTimeValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DateTimeType extends DateTimeBaseType {
    private static final long serialVersionUID = 3725074145431735052L;
    public static final String SYMBOL = "xs:dateTime";

    public static final DateTimeType DATETIME = new DateTimeType();

    public DateTimeType() {
        super(TypeTable.DATETIME_TID, SYMBOL);
    }

    public Class getJavaObjectType() {
        return Date.class;
    }

    public AtomicValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        return new DateTimeValue(literal, this);
    }

    public DateTimeValue createInstance(XMLGregorianCalendar value) {
        return new DateTimeValue(value, DATETIME);
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof DateTimeType;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_DATETIME;
    }
}
