/*
 * @(#)$Id: XInteger.java 3619 2008-03-26 07:23:03Z yui $
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
import java.math.BigDecimal;

import xbird.util.lang.ObjectUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.TypeTable;
import xbird.xquery.type.xs.IntegerType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author yui (yuin405+xbird@gmail.com)
 */
public final class XInteger extends XNumber {
    private static final long serialVersionUID = -9151271868116812898L;
    public static final int ID = 4;
    
    private long value;

    public XInteger() {
        super();
    }

    public XInteger(String literal, IntegerType type) {
        super(literal, type);
        this.value = Long.parseLong(literal);
    }

    public XInteger(String literal) {
        this(literal, IntegerType.INTEGER);
    }

    public XInteger(long value, IntegerType type) {
        super(Long.toString(value), type);
        this.value = value;
    }

    public XInteger(long value) {
        this(value, IntegerType.INTEGER);
    }

    public void setValue(long v) {
        this.value = v;
        onUpdate();
    }

    public long getValue() {
        return value;
    }

    public Number getNumber() {
        return value;
    }

    public Long toJavaObject() throws XQueryException {
        return value;
    }

    public int compareTo(Item trg) {
        if(trg instanceof XInteger) {
            final long trgValue = ((XInteger) trg).getValue();
            return Double.compare(value, trgValue);
        }
        return super.compareTo(trg);
    }

    public static XInteger valueOf(final long value) {
        return new XInteger(value);
    }

    public boolean isNaN() {
        return false;
    }

    public XInteger negate() {
        this.value = (-value);
        onUpdate();
        return this;
    }

    public XNumber ceil() {
        return this;
    }

    public XNumber floor() {
        return this;
    }

    public XNumber round() {
        return this;
    }

    public XNumber roundHalfToEven(int precision) {
        return this;
    }

    @Override
    protected void onUpdate() {
        String sv = Long.toString(value);
        setStringValue(sv);
    }

    @Override
    public BigDecimal asDecimal() {
        return new BigDecimal(value);
    }

    @Override
    public long asLong() {
        return value;
    }

    @Override
    public <T extends AtomicValue> T castAs(AtomicType trgType, DynamicContext dynEnv)
            throws XQueryException {
        final int ttid = trgType.getTypeId();
        final AtomicValue v;
        switch(ttid) {
            case TypeTable.BOOLEAN_TID:
                v = new BooleanValue(value != 0);
                break;
            case TypeTable.INTEGER_TID:
            case TypeTable.NUMERIC_TID:
                v = this;
                break;
            case TypeTable.FLOAT_TID:
                v = XFloat.valueOf(value);
                break;
            case TypeTable.DOUBLE_TID:
                v = XDouble.valueOf(value);
                break;
            case TypeTable.DECIMAL_TID:
                v = XDecimal.valueOf(asDecimal());
                break;
            default:
                v = super.castAs(trgType, dynEnv);
                break;
        }
        return (T) v;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._lexicalValue = ObjectUtils.readString(in);
        this._type = AtomicType.readAtomicType(in);
        this.value = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeString(out, _lexicalValue);
        _type.writeExternal(out);
        out.writeLong(value);
    }
    
    @Override
    public int getIdentifier() {
        return ID;
    }
}
