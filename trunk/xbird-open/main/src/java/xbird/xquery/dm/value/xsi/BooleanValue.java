/*
 * @(#)$Id: BooleanValue.java 3619 2008-03-26 07:23:03Z yui $
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
import java.math.BigDecimal;

import xbird.util.lang.ObjectUtils;
import xbird.xquery.XQRTException;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.literal.*;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.TypeTable;
import xbird.xquery.type.xs.BooleanType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class BooleanValue extends AtomicValue {
    private static final long serialVersionUID = 7379750560294251377L;
    public static final int ID = 6;

    public static final BooleanValue TRUE = new BooleanValue(true);
    public static final BooleanValue FALSE = new BooleanValue(false);

    private/* final */boolean _value;

    public BooleanValue() {
        super();
    }

    public BooleanValue(String literal) {
        super(literal, BooleanType.BOOLEAN);
        this._value = toBoolean(literal);
    }

    public BooleanValue(boolean value) {
        super(Boolean.toString(value), BooleanType.BOOLEAN);
        this._value = value;
    }

    public boolean booleanValue() {
        return _value;
    }

    public Boolean toJavaObject() throws XQueryException {
        return _value;
    }

    @Override
    public int compareTo(Item trg) {
        if(!(trg instanceof BooleanValue)) {
            throw new XQRTException("err:XPTY0004", "Imcomparable "
                    + trg.getClass().getSimpleName() + " with BooleanValue");
        }
        boolean trgValue = ((BooleanValue) trg)._value;
        return (trgValue == _value) ? 0 : (_value ? 1 : -1);
    }

    public static boolean toBoolean(String literal) {
        if(literal.equals("true") || literal.equals("1")) {
            return true;
        } else if(literal.equals("false") || literal.equals("0")) {
            return false;
        } else {
            throw new IllegalArgumentException("Invalid format: " + literal);
        }
    }

    @Deprecated
    public static BooleanValue valueOf(String literal) {
        return toBoolean(literal) ? TRUE : FALSE;
    }

    @Override
    public String stringValue() {
        return this.toString();
    }

    @Override
    public String toString() {
        return _value ? "true" : "false";
    }

    @Override
    public <T extends AtomicValue> T castAs(AtomicType trgType, DynamicContext dynEnv)
            throws XQueryException {
        final int ttid = trgType.getTypeId();
        final AtomicValue v;
        switch(ttid) {
            case TypeTable.BOOLEAN_TID:
                v = this;
                break;
            case TypeTable.NUMERIC_TID:
            case TypeTable.INTEGER_TID:
                v = XInteger.valueOf(_value ? 1 : 0);
                break;
            case TypeTable.FLOAT_TID:
                v = XFloat.valueOf(_value ? 1 : 0);
                break;
            case TypeTable.DOUBLE_TID:
                v = XDouble.valueOf(_value ? 1 : 0);
                break;
            case TypeTable.DECIMAL_TID:
                v = XDecimal.valueOf(_value ? BigDecimal.ONE : BigDecimal.ZERO);
                break;
            default:
                v = super.castAs(trgType, dynEnv);
                break;
        }
        return (T) v;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._lexicalValue = ObjectUtils.readString(in);
        this._type = BooleanType.BOOLEAN;
        this._value = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeString(out, _lexicalValue);
        out.writeBoolean(_value);
    }

    @Override
    public int getIdentifier() {
        return ID;
    }

}
