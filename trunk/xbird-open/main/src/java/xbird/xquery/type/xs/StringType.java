/*
 * @(#)$Id: StringType.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#casting-from-strings
 */
public class StringType extends AtomicType {
    private static final long serialVersionUID = 391859647450634892L;
    public static final String SYMBOL = "xs:string";

    public static final StringType STRING = new StringType();

    public StringType() {
        this(SYMBOL);
    }

    protected StringType(String type) {
        super(TypeTable.STRING_TID, type);
        setWhitespaceProcessing(PRESERVE);
    }

    public Class getJavaObjectType() {
        return String.class;
    }

    public AtomicValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        return new XString(literal, this);
    }

    @Override
    protected boolean isSuperTypeOf(AtomicType expected) {
        if(expected instanceof StringType) {
            return true;
        } else if(expected instanceof UntypedAtomicType) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_STRING;
    }
}
