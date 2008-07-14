/*
 * @(#)$Id: DecimalType.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xqj.XQJConstants;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.literal.XDecimal;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class DecimalType extends NumericType {
    private static final long serialVersionUID = 8819389402256344438L;
    public static final String SYMBOL = "xs:decimal";

    public static final DecimalType DECIMAL = new DecimalType();

    public DecimalType() {
        super(TypeTable.DECIMAL_TID, SYMBOL);
    }

    protected DecimalType(final int typeId, final String type) {
        super(typeId, type);
    }

    public Class getJavaObjectType() {
        return BigDecimal.class;
    }

    public AtomicValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        if(literal.indexOf('E') != -1 || literal.indexOf('e') != -1) {
            throw new DynamicError("err:FORG0001", "Illegal representation as xs:decimal: "
                    + literal);
        }
        try {
            return new XDecimal(literal, this);
        } catch (NumberFormatException nfe) {
            throw new DynamicError("err:FORG0001", "failed to cast as '" + SYMBOL + "': " + literal);
        }
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof DecimalType;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_DECIMAL;
    }

}
