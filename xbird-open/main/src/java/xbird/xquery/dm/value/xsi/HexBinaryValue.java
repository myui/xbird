/*
 * @(#)$Id: HexBinaryValue.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.util.string.StringUtils;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.TypeTable;
import xbird.xquery.type.xs.HexBinaryType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class HexBinaryValue extends BinaryValue {
    private static final long serialVersionUID = -780927982889919873L;
    public static final int ID = 14;

    private static final String HEX_CHARS = "0123456789ABCDEFabcdef";

    public HexBinaryValue() {
        super();
    }

    private HexBinaryValue(String litreal, byte[] binaryValue) {
        super(litreal, binaryValue, HexBinaryType.HEXBINARY);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final String literal = ObjectUtils.readString(in);
        this._lexicalValue = literal;
        try {
            this._binaryValue = decodeHex(literal);
        } catch (DynamicError e) {
            throw new IOException(e);
        }
        this._type = HexBinaryType.HEXBINARY;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeString(out, _lexicalValue);
    }

    @Override
    public int getIdentifier() {
        return ID;
    }

    @Override
    public <T extends AtomicValue> T castAs(AtomicType trgType, DynamicContext dynEnv)
            throws XQueryException {
        final int tid = trgType.getTypeId();
        if(tid == TypeTable.HEX_BINARY_TID) {
            return (T) this;
        } else if(tid == TypeTable.BASE64_TID) {
            byte[] binaryValue = getBinaryValue();
            return (T) Base64BinaryValue.createInstance(binaryValue);
        }
        return super.<T> castAs(trgType, dynEnv);
    }

    public static HexBinaryValue createInstance(final String literal) throws DynamicError {
        final byte[] binaryValue = decodeHex(literal);
        return new HexBinaryValue(literal.toUpperCase(), binaryValue);
    }

    public static HexBinaryValue createInstance(final byte[] binary) throws DynamicError {
        final String literal = StringUtils.encodeHex(binary);
        return new HexBinaryValue(literal, binary);
    }

    private static byte[] decodeHex(final String literal) throws DynamicError {
        final int len = literal.length();
        if((len & 1) != 0) {
            throw new DynamicError("err:FORG0001", "A xs:hexBinary value MUST contains an even number of characters, but was '"
                    + literal + '\'');
        }
        final byte[] binaryValue = new byte[len / 2];
        for(int i = 0, j = 0; i < len; i += 2, j++) {
            binaryValue[j] = (byte) ((decodeHex(literal, i) << 4) + decodeHex(literal, i + 1));
        }
        return binaryValue;
    }

    private static int decodeHex(final String literal, final int index) throws DynamicError {
        final int c = literal.charAt(index);
        final int idx = HEX_CHARS.indexOf(c);
        if(idx == -1) {
            throw new DynamicError("err:FORG0001", "Illegal representation as xs:hexBinary: "
                    + literal);
        }
        if(idx > 15) {
            return (idx - 6) & 0xF;
        } else {
            return idx;
        }
    }
}
