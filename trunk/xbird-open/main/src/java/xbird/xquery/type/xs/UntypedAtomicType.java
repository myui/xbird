/*
 * @(#)$Id: UntypedAtomicType.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.xsi.UntypedAtomicValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#casting-from-strings
 */
public final class UntypedAtomicType extends AtomicType {
    private static final long serialVersionUID = 291661989794118191L;
    public static final String SYMBOL = "xs:untypedAtomic";

    public static final UntypedAtomicType UNTYPED_ATOMIC = new UntypedAtomicType();

    public UntypedAtomicType() {
        super(TypeTable.UNTYPED_ATOMIC_TID, SYMBOL);
        setWhitespaceProcessing(PRESERVE);
    }

    @Override
    public boolean accepts(Type expected) {
        if(expected instanceof AtomicType) {
            AtomicType atomType = (AtomicType) expected;
            final int tid = atomType.getTypeId();
            switch(tid) {
                case TypeTable.UNTYPED_ATOMIC_TID:
                case TypeTable.ANY_ATOM_TID:
                case TypeTable.STRING_TID:
                    return true;
                default:
                    return false;
            }
        } else {
            Type anytexts = TypeRegistry.safeGet("node()*"); // for atomization
            return TypeUtil.subtypeOf(expected, anytexts);
        }
    }

    public Class getJavaObjectType() {
        return Object.class;
    }

    public boolean isValid(String literal) {
        return true;
    }

    public UntypedAtomicValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        return new UntypedAtomicValue(literal);
    }

    @Override
    public String processWhitespace(String literal) {
        return literal;
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof UntypedAtomicType;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_UNTYPEDATOMIC;
    }

}
