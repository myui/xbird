/*
 * @(#)$Id: XDouble.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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
import xbird.xquery.type.xs.DoubleType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author yui (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xmlschema-2/#double
 */
public final class XDouble extends XNumber {
    private static final long serialVersionUID = -8779305902783571768L;
    public static final int ID = 3;

    private static final DecimalFormat decFormat;
    private static final DecimalFormat decSciFormat;
    static {
        final DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.US);
        decFormat = new DecimalFormat("#####0.#################", symbol);
        decSciFormat = new DecimalFormat("0.0################E0##", symbol);
    }

    public static final XDouble COMPARABLE_NaN = new XDouble("NaN", Double.NaN);

    private double value;
    private transient int hashcode = -1;
    private transient String _canonical = null;

    public XDouble() {
        super();
    }

    public XDouble(String literal) {
        this(literal, parseDouble(literal));
    }

    private XDouble(String literal, double value) {
        super(literal, DoubleType.DOUBLE);
        this.value = value;
    }

    public XDouble(double value) {
        super(Double.toString(value), DoubleType.DOUBLE);
        this.value = value;
    }

    private static double parseDouble(final String literal) {
        assert (literal != null);
        if("INF".equals(literal)) {
            return Double.POSITIVE_INFINITY;
        } else if("-INF".equals(literal)) {
            return Double.NEGATIVE_INFINITY;
        } else if("NaN".equals(literal)) {
            return Double.NaN;
        } else {
            return Double.parseDouble(literal);
        }
    }

    public double getValue() {
        return value;
    }

    public Number getNumber() {
        return value;
    }

    public Double toJavaObject() throws XQueryException {
        return value;
    }

    @Override
    public int compareTo(Item trg) {
        if(this == trg && trg == COMPARABLE_NaN) {
            return 0;
        }
        if(trg instanceof XDouble) {
            if(Double.isNaN(value) && ((XDouble) trg).isNaN()) {
                return -1; // incomparable: this object set to be smaller than trg.
            }
            final double trgValue = ((XDouble) trg).value;
            return Double.compare(value, trgValue);
        }
        return super.compareTo(trg);
    }

    public static XDouble valueOf(double value) {
        return new XDouble(value);
    }

    public boolean isNaN() {
        return Double.isNaN(value);
    }

    public XDouble negate() {
        this.value = (-value);
        onUpdate();
        return this;
    }

    public XNumber ceil() {
        this.value = Math.ceil(value);
        onUpdate();
        return this;
    }

    public XNumber floor() {
        this.value = Math.floor(value);
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
        this.value = rounded.doubleValue();
        onUpdate();
        return this;
    }

    public BigDecimal asDecimal() {
        return BigDecimal.valueOf(value);
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public int hashCode() {
        if(hashcode != -1) {
            return hashcode;
        }
        final long bits = Double.doubleToLongBits(value);
        final int h = (int) (bits ^ (bits >>> 32));
        this.hashcode = h;
        return h;
    }

    @Override
    public String stringValue() {
        return this.toString();
    }

    @Override
    protected void onUpdate() {
        this._canonical = null;
        this.hashcode = -1;
        String sv = toString();
        setStringValue(sv);
    }

    @Override
    public synchronized String toString() {
        if(_canonical == null) {
            final String c;
            if(value == Double.POSITIVE_INFINITY) {
                c = "INF";
            } else if(value == Double.NEGATIVE_INFINITY) {
                c = "-INF";
            } else if(value != value) {
                c = "NaN";
            } else if(value == 0) {
                c = (1.0 / value) == Double.POSITIVE_INFINITY ? "0" : "-0";
            } else {
                final double abs = Math.abs(value);
                if(abs >= 1e-6 && abs < 1e6) {
                    synchronized(decFormat) {
                        c = decFormat.format(value);
                    }
                } else {
                    c = decSciFormat.format(value);
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
                final boolean ebv = (value != 0.0 && !Double.isNaN(value));
                v = new BooleanValue(ebv);
                break;
            case TypeTable.DOUBLE_TID:
            case TypeTable.NUMERIC_TID:
                v = this;
                break;
            case TypeTable.INTEGER_TID:
                if(Double.isNaN(value)) {
                    throw new DynamicError("err:FOCA0002", "Can't convert xs:double(" + toString()
                            + ") to xs:integer");
                }
                if(Double.isInfinite(value)) {
                    throw new DynamicError("err:FOCA0002", "Can't convert xs:double(" + toString()
                            + ") to xs:integer");
                }
                v = XInteger.valueOf(asLong());
                break;
            case TypeTable.FLOAT_TID:
                if(Double.isNaN(value)) {
                    v = XFloat.valueOf(Float.NaN);
                } else if(value == Double.POSITIVE_INFINITY) {
                    v = XFloat.valueOf(Float.POSITIVE_INFINITY);
                } else if(value == Double.NEGATIVE_INFINITY) {
                    v = XFloat.valueOf(Float.NEGATIVE_INFINITY);
                } else {
                    v = XFloat.valueOf((float) value);
                }
                break;
            case TypeTable.DECIMAL_TID:
                if(Double.isNaN(value)) {
                    throw new DynamicError("err:FORG0001", "Can't convert xs:double(" + toString()
                            + ") to xs:decimal");
                }
                if(Double.isInfinite(value)) {
                    throw new DynamicError("err:FOCA0002", "Can't convert xs:double(" + toString()
                            + ") to xs:decimal");
                }
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
        this._type = DoubleType.DOUBLE;
        this.value = in.readDouble();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeString(out, _lexicalValue);
        out.writeDouble(value);
    }

    @Override
    public int getIdentifier() {
        return ID;
    }

    @Override
    public AtomicValue asGroupingValue() {
        return isNaN() ? COMPARABLE_NaN : this;
    }

}