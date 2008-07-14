/*
 * @(#)$Id: NOTATIONType.java 3619 2008-03-26 07:23:03Z yui $
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

import org.w3c.dom.Notation;

import xbird.xqj.XQJConstants;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.xsi.NotationValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en">
 * [XML Schema Part 2: Datatypes Second Edition] defines xs:NOTATION as an abstract type. 
 * Thus, casting to xs:NOTATION from any other type including xs:NOTATION is not permitted.
 * However, casting from one subtype of xs:NOTATION to another subtype of xs:NOTATION is permitted.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NOTATIONType extends AtomicType {
    private static final long serialVersionUID = -6539206655845364602L;
    public static final String SYMBOL = "xs:NOTATION";

    public static final NOTATIONType NOTATION = new NOTATIONType();

    public NOTATIONType() {
        super(TypeTable.NOTATION_TID, SYMBOL);
    }

    public Class getJavaObjectType() {
        return Notation.class;
    }

    public AtomicValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        return new NotationValue(literal);
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof NOTATIONType;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_NOTATION;
    }

}
