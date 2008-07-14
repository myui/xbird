/*
 * @(#)$Id: HexBinaryType.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.xsi.HexBinaryValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#casting-to-binary
 */
public final class HexBinaryType extends BinaryType {
    private static final long serialVersionUID = 3395786703735580423L;
    public static final String SYMBOL = "xs:hexBinary";

    public static final HexBinaryType HEXBINARY = new HexBinaryType();

    public HexBinaryType() {
        super(TypeTable.HEX_BINARY_TID, SYMBOL);
    }

    public HexBinaryValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        assert (literal != null);
        return HexBinaryValue.createInstance(literal);
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof HexBinaryType;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_HEXBINARY;
    }

}
