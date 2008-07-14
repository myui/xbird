/*
 * @(#)$Id: XNumber.java 3619 2008-03-26 07:23:03Z yui $
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

import java.math.BigDecimal;

import xbird.xquery.XQRTException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.type.xs.NumericType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author yui (yuin405+xbird@gmail.com)
 */
public abstract class XNumber extends AtomicValue {
    // workaround for InvalidCastException
    // http://archives.java.sun.com/cgi-bin/wa?A2=ind0008&L=rmi-users&P=20699
    private static final long serialVersionUID = 1L;

    public XNumber() {
        super();
    }
    
    public XNumber(String literal, NumericType type) {
        super(literal, type);
    }

    public abstract Number getNumber();

    public abstract boolean isNaN();

    public abstract XNumber negate();

    public abstract XNumber ceil();

    public abstract XNumber floor();

    public abstract XNumber round();

    public abstract XNumber roundHalfToEven(int precision);

    public abstract BigDecimal asDecimal();

    public long asLong() {
        return getNumber().longValue();
    }

    public float asFloat() {
        return getNumber().floatValue();
    }

    public double asDouble() {
        return getNumber().doubleValue();
    }

    @Override
    public int compareTo(Item trg) {
        if(!(trg instanceof XNumber)) {
            throw new XQRTException("err:XPTY0004", "Imcomparable "
                    + trg.getClass().getSimpleName() + " with " + this.getClass().getName());
        }
        final double thisValue = getNumber().doubleValue();
        final double trgValue = ((XNumber) trg).getNumber().doubleValue();
        return Double.compare(thisValue, trgValue);
    }
}