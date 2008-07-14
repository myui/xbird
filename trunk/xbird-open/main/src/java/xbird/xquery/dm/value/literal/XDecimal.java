/*
 * @(#)$Id: XDecimal.java 3619 2008-03-26 07:23:03Z yui $
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
import java.math.RoundingMode;

import xbird.util.lang.ObjectUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.TypeTable;
import xbird.xquery.type.xs.DecimalType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class XDecimal extends XNumber {
    private static final long serialVersionUID = -8768229815740382391L;
    public static final int ID = 10;
    
    private static final int DECIMAL_PRECISION = 18;

    private BigDecimal value;

    public XDecimal() {
        super();
    }

    public XDecimal(String literal) {
        this(literal, DecimalType.DECIMAL);
    }

    public XDecimal(String literal, DecimalType type) {
        this(new BigDecimal(literal), type);
    }

    public XDecimal(BigDecimal value) {
        this(value, DecimalType.DECIMAL);
    }

    public XDecimal(BigDecimal value, DecimalType type) {
        super(value.toPlainString(), type); /* value assumed to be trimed */
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Number getNumber() {
        return value;
    }

    @Override
    public int compareTo(Item trg) {
        if(trg instanceof XDecimal) {
            BigDecimal trgValue = ((XDecimal) trg).value;
            return value.compareTo(trgValue);
        }
        return super.compareTo(trg);
    }

    public BigDecimal toJavaObject() throws XQueryException {
        return value;
    }

    public static XDecimal valueOf(BigDecimal v) {
        return new XDecimal(v);
    }

    public boolean isNaN() {
        return false;
    }

    public XDecimal negate() {
        this.value = value.negate();
        onUpdate();
        return this;
    }

    public XNumber ceil() {
        this.value = value.setScale(0, RoundingMode.CEILING);
        onUpdate();
        return this;
    }

    public XNumber floor() {
        this.value = value.setScale(0, RoundingMode.FLOOR);
        onUpdate();
        return this;
    }

    public XNumber round() {
        final int signum = value.signum();
        if(signum > 0) {
            this.value = value.setScale(0, RoundingMode.HALF_UP);
        } else if(signum < 0) {
            this.value = value.setScale(0, RoundingMode.HALF_DOWN);
        }
        onUpdate();
        return this;
    }

    public XNumber roundHalfToEven(int precision) {
        this.value = value.setScale(precision, RoundingMode.HALF_EVEN);
        onUpdate();
        return this;
    }

    private String _canonical = null;

    @Override
    protected void onUpdate() {
        _canonical = null;
        String sv = toString();
        setStringValue(sv);
    }

    @Override
    public BigDecimal asDecimal() {
        return value;
    }

    @Override
    public String stringValue() {
        return this.toString();
    }

    @Override
    public String toString() {
        if(_canonical == null) {
            String src = value.toPlainString();
            final String res;
            if(src.indexOf('.') == -1) {
                res = src;
            } else {
                // remove trailing zeros
                int last = src.length();
                while(--last > 0) {
                    if(src.charAt(last) != '0') {
                        break;
                    }
                }
                if(src.charAt(last) == '.') { // for the case 'xxx.000'
                    --last;
                }
                res = src.substring(0, last + 1);
            }
            this._canonical = res;
            return res;
        }
        return _canonical;
    }

    @Override
    public <T extends AtomicValue> T castAs(AtomicType trgType, DynamicContext dynEnv)
            throws XQueryException {
        final int ttid = trgType.getTypeId();
        final AtomicValue v;
        switch(ttid) {
            case TypeTable.BOOLEAN_TID:
                v = new BooleanValue(value.signum() != 0);
                break;
            case TypeTable.DECIMAL_TID:
            case TypeTable.NUMERIC_TID:
                v = this;
                break;
            case TypeTable.INTEGER_TID:
                v = XInteger.valueOf(asLong());
                break;
            case TypeTable.FLOAT_TID:
                v = XFloat.valueOf(value.floatValue());
                break;
            case TypeTable.DOUBLE_TID:
                v = XDouble.valueOf(asDouble());
                break;
            default:
                v = super.castAs(trgType, dynEnv);
                break;
        }
        return (T) v;
    }

    public static BigDecimal divide(BigDecimal src, BigDecimal divisor) {
        return src.divide(divisor, DECIMAL_PRECISION, BigDecimal.ROUND_HALF_EVEN);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._lexicalValue = ObjectUtils.readString(in);
        this._type = AtomicType.readAtomicType(in);
        this.value = (BigDecimal) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeString(out, _lexicalValue);
        _type.writeExternal(out);
        out.writeObject(value);
    }
    
    @Override
    public int getIdentifier() {
        return ID;
    }

}