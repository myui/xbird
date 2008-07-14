/*
 * @(#)$Id: UntypedAtomicValue.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value.xsi;

import java.io.*;

import xbird.util.lang.ObjectUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.type.xs.UntypedAtomicType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class UntypedAtomicValue extends AtomicValue {
    private static final long serialVersionUID = 9022267317129562786L;
    public static final int ID = 1;
    
    public UntypedAtomicValue() {
        super();
    }

    public UntypedAtomicValue(String literal) {
        super(literal, UntypedAtomicType.UNTYPED_ATOMIC);
    }

    public String getUntypedValue() {
        return stringValue();
    }

    public String toJavaObject() throws XQueryException {
        return stringValue();
    }

    @Override
    public String stringValue() {
        return super.stringValue();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._lexicalValue = ObjectUtils.readString(in);
        this._type = UntypedAtomicType.UNTYPED_ATOMIC;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeString(out, _lexicalValue);
    }

    @Override
    public int getIdentifier() {
        return ID;
    }
}
