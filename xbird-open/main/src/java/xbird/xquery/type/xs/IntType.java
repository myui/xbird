/*
 * @(#)$Id: IntType.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.type.AtomicType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class IntType extends LongType {
    private static final long serialVersionUID = 9023525234706894166L;
    public static final String SYMBOL = "xs:int";

    public static final IntType INT = new IntType();

    public IntType() {
        super(SYMBOL);
    }

    protected IntType(String type) {
        super(type);
    }

    @Override
    public Class getJavaObjectType() {
        return int.class;
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof IntType;
    }

    @Override
    protected long lowerBound() {
        return Integer.MIN_VALUE;
    }

    @Override
    protected long upperBound() {
        return 0x7fffffffL;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_INT;
    }

    public static boolean inRange(final long value) {
        return value >= 0x80000000L && value <= 0x7fffffffL;
    }

}
