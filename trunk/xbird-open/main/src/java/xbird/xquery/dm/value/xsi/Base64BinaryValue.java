/*
 * @(#)$Id: Base64BinaryValue.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.util.codec.Base64Codec;
import xbird.util.lang.ObjectUtils;
import xbird.util.primitive.Primitives;
import xbird.util.string.StringUtils;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.TypeTable;
import xbird.xquery.type.xs.Base64BinaryType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xmlschema-2/#base64Binary
 */
public final class Base64BinaryValue extends BinaryValue {
    private static final long serialVersionUID = 2844976671739394425L;
    public static final int ID = 13;

    private static final String DOMAIN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    // for Externalizable
    public Base64BinaryValue() {
        super();
    }

    private Base64BinaryValue(final String literal, final byte[] binaryValue) {
        super(literal, binaryValue, Base64BinaryType.BASE64BINARY);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final String literal = ObjectUtils.readString(in);
        this._lexicalValue = literal;
        this._binaryValue = Base64Codec.decode(StringUtils.getBytes(literal));
        this._type = Base64BinaryType.BASE64BINARY;
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
        if(tid == TypeTable.BASE64_TID) {
            return (T) this;
        } else if(tid == TypeTable.HEX_BINARY_TID) {
            byte[] binaryValue = getBinaryValue();
            return (T) HexBinaryValue.createInstance(binaryValue);
        }
        return super.<T> castAs(trgType, dynEnv);
    }

    public static Base64BinaryValue createInstance(final String literal) throws DynamicError {
        char[] normalized = normalize(literal);
        byte[] binaryValue = Base64Codec.decode(Primitives.toBytes(normalized), false);
        String str = new String(normalized);
        return new Base64BinaryValue(str, binaryValue);
    }

    public static Base64BinaryValue createInstance(final byte[] binary) throws DynamicError {
        final String literal = Base64Codec.toBase64String(binary);
        return new Base64BinaryValue(literal, binary);
    }

    private static char[] normalize(final String data) throws DynamicError {
        final int len = data.length();
        final char[] trimed = new char[len];
        int bytesCopied = 0;
        for(int i = 0; i < len; i++) {
            final char c = data.charAt(i);
            switch(c) {
                case ' ':
                case '\n':
                case '\r':
                case '\t':
                    break;
                default:
                    if(DOMAIN.indexOf(c) == -1) {
                        throw new DynamicError("err:FORG0001", "Illegal representation as xs:base64Binary: "
                                + data);
                    }
                    trimed[bytesCopied++] = c;
            }
        }
        if(bytesCopied == len) {
            return trimed;
        } else {
            final char packed[] = new char[bytesCopied];
            System.arraycopy(trimed, 0, packed, 0, bytesCopied);
            return packed;
        }
    }

}