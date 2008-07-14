/*
 * @(#)$Id: IntegerType.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xqj.XQJConstants;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class IntegerType extends DecimalType {
    private static final long serialVersionUID = 4303420600472848916L;
    public static final String SYMBOL = "xs:integer";

    public static final IntegerType INTEGER = new IntegerType();

    public IntegerType() {
        super(TypeTable.INTEGER_TID, SYMBOL);
    }

    protected IntegerType(final String type) {
        super(TypeTable.INTEGER_TID, type);
    }

    @Override
    public Class getJavaObjectType() {
        return long.class;
    }

    @Override
    public XInteger createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        final long lv;
        try {
            lv = Long.parseLong(literal);
        } catch (NumberFormatException pe) {
            throw new DynamicError("err:FORG0001", "failed to cast as '" + SYMBOL + "': " + literal);
        }
        if(lv < lowerBound() || lv > upperBound()) {
            throw new DynamicError("err:FORG0001", "Illegal value for '" + SYMBOL + "': " + literal);
        }
        return new XInteger(lv, this);
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof IntegerType;
    }

    protected long upperBound() {
        return Long.MAX_VALUE;
    }

    protected long lowerBound() {
        return Long.MIN_VALUE;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_INTEGER;
    }
}
