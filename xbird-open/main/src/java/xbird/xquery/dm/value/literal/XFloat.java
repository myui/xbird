/*
 * @(#)$Id: XFloat.java 3619 2008-03-26 07:23:03Z yui $
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import xbird.util.lang.ObjectUtils;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.TypeTable;
import xbird.xquery.type.xs.FloatType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xmlschema-2/#float
 */
public final class XFloat extends XNumber {
    private static final long serialVersionUID = 7062236467847477920L;
    public static final int ID = 5;

    private static final DecimalFormat decFormat;
    static {
        final DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.US);
        decFormat = new DecimalFormat("#####0.#######", symbol);
    }

    private float value;
    private transient String _canonical = null;

    public XFloat() {
        super();
    }

    public XFloat(String literal) {
        super(literal, FloatType.FLOAT);
        this.value = toFloat(literal);
    }

    public XFloat(float f) {
        super(Float.toString(f), FloatType.FLOAT);
        this.value = f;
    }

    public float getValue() {
        return value;
    }

    public Number getNumber() {
        return value;
    }

    public Float toJavaObject() throws XQueryException {
        return value;
    }

    public int compareTo(Item trg) {
        if(trg instanceof XFloat) {
            final float trgValue = ((XFloat) trg).getValue();
            return Float.compare(value, trgValue);
        }
        return super.compareTo(trg);
    }

    public static XFloat valueOf(float value) {
        return new XFloat(value);
    }

    private static Float toFloat(String s) {
        if(s.equals("NaN")) {
            return new Float(Float.NaN);
        } else if(s.equals("INF")) {
            return new Float(Float.POSITIVE_INFINITY);
        } else if(s.equals("-INF")) {
            return new Float(Float.NEGATIVE_INFINITY);
        } else {
            return Float.valueOf(s);
        }
    }

    public boolean isNaN() {
        return Float.isNaN(value);
    }

    public XFloat negate() {
        this.value = (-value);
        onUpdate();
        return this;
    }

    public XNumber ceil() {
        this.value = (float) Math.ceil(value);
        onUpdate();
        return this;
    }

    public XNumber floor() {
        this.value = (float) Math.floor(value);
        onUpdate();
        return this;
    }

    public XNumber round() {
        this.value = Math.round(value);
        onUpdate();
        return this;
    }

    public XNumber roundHalfToEven(int precision) {
        final BigDecimal rounded = BigDecimal.valueOf(value).setScale(precision, RoundingMode.HALF_EVEN);
        this.value = rounded.floatValue();
        onUpdate();
        return this;
    }

    @Override
    public BigDecimal asDecimal() {
        return BigDecimal.valueOf(value);
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(value);
    }

    @Override
    public String stringValue() {
        return this.toString();
    }

    @Override
    protected void onUpdate() {
        this._canonical = null;
        String sv = toString();
        setStringValue(sv);
    }

    @Override
    public synchronized String toString() {
        if(_canonical == null) {
            final String c;
            if(value == Float.POSITIVE_INFINITY) {
                c = "INF";
            } else if(value == Float.NEGATIVE_INFINITY) {
                c = "-INF";
            } else if(value != value) {
                c = "NaN";
            } else if(value == 0f) {
                c = (1.0f / value) == Float.POSITIVE_INFINITY ? "0" : "-0";
            } else {
                final float abs = Math.abs(value);
                if(abs >= 1e-6f && abs < 1e6f) {
                    synchronized(decFormat) {
                        c = decFormat.format(value);
                    }
                } else {
                    c = Float.toString(value);
                }
            }
            this._canonical = c;
            return c;
        }
        return _canonical;
    }

    /**
     * @link http://www.w3.org/TR/xpath-functions/#casting-to-numerics
     */
    @Override
    public <T extends AtomicValue> T castAs(AtomicType trgType, DynamicContext dynEnv)
            throws XQueryException {
        final int ttid = trgType.getTypeId();
        final AtomicValue v;
        switch(ttid) {
            case TypeTable.BOOLEAN_TID:
                final boolean ebv = (value != 0.0 && !Float.isNaN(value));
                v = new BooleanValue(ebv);
                break;
            case TypeTable.FLOAT_TID:
            case TypeTable.NUMERIC_TID:
                v = this;
                break;
            case TypeTable.INTEGER_TID:
                if(Float.isNaN(value)) {
                    throw new DynamicError("err:FOCA0002", "Can't convert xs:double(" + toString()
                            + ") to xs:integer");
                }
                if(Float.isInfinite(value)) {
                    throw new DynamicError("err:FOCA0002", "Can't convert xs:double(" + toString()
                            + ") to xs:integer");
                }
                v = XInteger.valueOf(asLong());
                break;
            case TypeTable.DOUBLE_TID:
                v = XDouble.valueOf(asDouble());
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
        this._type = FloatType.FLOAT;
        this.value = in.readFloat();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeString(out, _lexicalValue);
        out.writeFloat(value);
    }

    @Override
    public int getIdentifier() {
        return ID;
    }
}
