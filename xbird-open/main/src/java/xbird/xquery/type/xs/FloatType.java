/*
 * @(#)$Id: FloatType.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.literal.XFloat;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class FloatType extends NumericType {
    private static final long serialVersionUID = -1249247820283368862L;
    public static final String SYMBOL = "xs:float";

    public static final FloatType FLOAT = new FloatType();

    public FloatType() {
        super(TypeTable.FLOAT_TID, SYMBOL);
    }

    public Class getJavaObjectType() {
        return float.class;
    }

    /**
     * @see AtomicValue#castAs(AtomicType, DynamicContext)
     */
    public AtomicValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        try {
            return new XFloat(literal);
        } catch (NumberFormatException e) {
            throw new DynamicError("err:FORG0001", e);
        }
    }

    @Override
    protected boolean isSuperTypeOf(AtomicType expected) {
        if(expected == this || expected instanceof FloatType) {
            return true;
        } else if(expected instanceof IntType) { // TODO REVIEWME
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_FLOAT;
    }

}
