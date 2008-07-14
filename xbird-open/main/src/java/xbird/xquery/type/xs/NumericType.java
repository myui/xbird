/*
 * @(#)$Id: NumericType.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class NumericType extends AtomicType {
    public static final String SYMBOL = "xs:numeric";

    private NumericType() {
        super(TypeTable.NUMERIC_TID, SYMBOL);
    }

    protected NumericType(final int typeId, final String type) {
        super(typeId, type);
    }

    public static NumericType getInstance() {
        return Singleton.INSTANCE;
    }

    public static final class Singleton extends NumericType {
        private static final long serialVersionUID = 576024819687007297L;

        public static final NumericType INSTANCE = new Singleton();

        public Singleton() {}

        public Class getJavaObjectType() {
            return Number.class;
        }

        public AtomicValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
                throws XQueryException {
            throw new IllegalStateException("Singleton#createInstance should not be called");
        }

        @Override
        public int getXQJBaseType() {
            return XQJConstants.XQBASETYPE_ANYATOMICTYPE; // REVIEWME
        }
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof NumericType;
    }

}
