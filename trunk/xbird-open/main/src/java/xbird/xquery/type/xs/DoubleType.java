/*
 * @(#)$Id: DoubleType.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.literal.XDouble;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#casting-to-numerics
 */
public final class DoubleType extends NumericType {
    private static final long serialVersionUID = -3040415387092162958L;
    public static final String SYMBOL = "xs:double";

    public static final DoubleType DOUBLE = new DoubleType();

    public DoubleType() {
        super(TypeTable.DOUBLE_TID, SYMBOL);
    }

    public Class getJavaObjectType() {
        return double.class;
    }

    public XDouble createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        try {
            return new XDouble(literal);
        } catch (NumberFormatException e) {
            throw new DynamicError("err:FORG0001", e);
        }
    }

    @Override
    protected boolean isSuperTypeOf(AtomicType expected) {
        return expected instanceof DoubleType; // TODO REVIEWME
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_DOUBLE;
    }

}
