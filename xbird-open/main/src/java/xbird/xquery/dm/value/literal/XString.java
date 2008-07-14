/*
 * @(#)$Id: XString.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value.literal;

import java.io.*;

import xbird.util.lang.ObjectUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.xs.StringType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class XString extends AtomicValue {
    private static final long serialVersionUID = -549820909158257036L;
    public static final int ID = 2;

    public XString() {
        super();
    }

    public XString(String literal, AtomicType type) {
        super(literal, type);
    }

    public XString(String literal) {
        super(literal, StringType.STRING);
    }

    public String getValue() {
        return stringValue();
    }

    public String toJavaObject() throws XQueryException {
        return stringValue();
    }

    @Override
    public int compareTo(Item trg) {
        String sv = stringValue();
        String trgValue = trg.stringValue();
        return sv.compareTo(trgValue);
    }

    public static XString valueOf(String literal) {
        return new XString(literal);
    }

    @Override
    public final String stringValue() {// trick to gain better performance
        return super.stringValue();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._lexicalValue = ObjectUtils.readString(in);
        final boolean isStr = in.readBoolean();
        if(isStr) {
            this._type = StringType.STRING;
        } else {
            this._type = AtomicType.readAtomicType(in);
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeString(out, _lexicalValue);
        final AtomicType type = _type;
        if(type == StringType.STRING) {
            out.writeBoolean(true);
        } else {
            out.writeBoolean(false);
            _type.writeExternal(out);
        }
    }

    @Override
    public int getIdentifier() {
        return ID;
    }

}
