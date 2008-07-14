/*
 * @(#)$Id: NumericOp.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.operator.math;

import static xbird.xquery.type.TypeTable.*;

import java.math.BigDecimal;

import xbird.util.string.StringUtils;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.literal.*;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.operator.OverloadedFunction;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class NumericOp extends OverloadedFunction {

    protected static final int INT_INT = (INTEGER_TID << 4) + INTEGER_TID;
    protected static final int INT_FLOAT = (INTEGER_TID << 4) + FLOAT_TID;
    protected static final int INT_DOUBLE = (INTEGER_TID << 4) + DOUBLE_TID;
    protected static final int INT_DECIMAL = (INTEGER_TID << 4) + DECIMAL_TID;
    protected static final int FLOAT_INT = (FLOAT_TID << 4) + INTEGER_TID;
    protected static final int FLOAT_FLOAT = (FLOAT_TID << 4) + FLOAT_TID;
    protected static final int FLOAT_DOUBLE = (FLOAT_TID << 4) + DOUBLE_TID;
    protected static final int FLOAT_DECIMAL = (FLOAT_TID << 4) + DECIMAL_TID;
    protected static final int DOUBLE_INT = (DOUBLE_TID << 4) + INTEGER_TID;
    protected static final int DOUBLE_FLOAT = (DOUBLE_TID << 4) + FLOAT_TID;
    protected static final int DOUBLE_DOUBLE = (DOUBLE_TID << 4) + DOUBLE_TID;
    protected static final int DOUBLE_DECIMAL = (DOUBLE_TID << 4) + DECIMAL_TID;
    protected static final int DECIMAL_INT = (DECIMAL_TID << 4) + INTEGER_TID;
    protected static final int DECIMAL_FLOAT = (DECIMAL_TID << 4) + FLOAT_TID;
    protected static final int DECIMAL_DOUBLE = (DECIMAL_TID << 4) + DOUBLE_TID;
    protected static final int DECIMAL_DECIMAL = (DECIMAL_TID << 4) + DECIMAL_TID;

    public NumericOp(String funcName) {
        super(funcName);
    }

    protected static int combinedArgTypes(Item it1, Item it2) {
        int t1, t2;
        Type type1 = it1.getType();
        if(type1 instanceof AtomicType) {
            t1 = ((AtomicType) type1).getTypeId();
            if(t1 == UNTYPED_ATOMIC_TID) {
                t1 = DOUBLE_TID;
            }
        } else {
            t1 = UNRESOLVED;
        }
        Type type2 = it2.getType();
        if(type2 instanceof AtomicType) {
            t2 = ((AtomicType) type2).getTypeId();
            if(t2 == UNTYPED_ATOMIC_TID) {
                t2 = DOUBLE_TID;
            }
        } else {
            t2 = UNRESOLVED;
        }
        return (t1 << 4) + t2;
    }

    protected static double asDouble(final Item it, final DynamicContext dynEnv)
            throws XQueryException {
        final double d;
        if(it instanceof XDouble) {
            d = ((XDouble) it).getValue();
        } else if(it instanceof XNumber) {
            d = ((XNumber) it).asDouble();
        } else if(it instanceof AtomicValue) {
            XDouble v = ((AtomicValue) it).castAs(DoubleType.DOUBLE, dynEnv);
            d = v.getValue();
        } else {
            final String sv = DoubleType.DOUBLE.processWhitespace(it.stringValue());
            if(StringUtils.isNumber(sv)) {
                d = Double.parseDouble(sv);
            } else {
                throw new DynamicError("err:FORG0001", "Illegal value for xs:double: " + sv);
            }
        }
        return d;
    }

    protected static long asLong(final Item it, final DynamicContext dynEnv) throws XQueryException {
        final long l;
        if(it instanceof XInteger) {
            l = ((XInteger) it).getValue();
        } else if(it instanceof XNumber) {
            l = ((XNumber) it).asLong();
        } else if(it instanceof AtomicValue) {
            XInteger v = ((AtomicValue) it).castAs(LongType.LONG, dynEnv);
            l = v.getValue();
        } else {
            final String sv = LongType.LONG.processWhitespace(it.stringValue());
            if(StringUtils.isNumber(sv)) {
                l = Long.parseLong(sv);
            } else {
                throw new DynamicError("err:FORG0001", "Illegal value for xs:long: " + sv);
            }
        }
        return l;
    }

    protected static float asFloat(final Item it, final DynamicContext dynEnv)
            throws XQueryException {
        final float f;
        if(it instanceof XFloat) {
            f = ((XFloat) it).getValue();
        } else if(it instanceof XNumber) {
            f = ((XNumber) it).asFloat();
        } else if(it instanceof AtomicValue) {
            XFloat v = ((AtomicValue) it).castAs(FloatType.FLOAT, dynEnv);
            f = v.getValue();
        } else {
            final String sv = FloatType.FLOAT.processWhitespace(it.stringValue());
            if(StringUtils.isNumber(sv)) {
                f = Float.parseFloat(sv);
            } else {
                throw new DynamicError("err:FORG0001", "Illegal value for xs:long: " + sv);
            }
        }
        return f;
    }

    protected static BigDecimal asDecimal(final Item it, final DynamicContext dynEnv)
            throws XQueryException {
        final BigDecimal d;
        if(it instanceof XDecimal) {
            d = ((XDecimal) it).getValue();
        } else if(it instanceof XNumber) {
            d = ((XNumber) it).asDecimal();
        } else if(it instanceof AtomicValue) {
            XDecimal v = ((AtomicValue) it).castAs(DecimalType.DECIMAL, dynEnv);
            d = v.getValue();
        } else {
            final String sv = DecimalType.DECIMAL.processWhitespace(it.stringValue());
            if(StringUtils.isNumber(sv)) {
                d = new BigDecimal(sv);
            } else {
                throw new DynamicError("err:FORG0001", "Illegal value for xs:long: " + sv);
            }
        }
        return d;
    }

    protected static abstract class Exec {

        protected Exec() {}

        public abstract AtomicValue eval(DynamicContext dynEnv, Item v1, Item v2)
                throws XQueryException;

        public static long addL(final long a, final long b) throws DynamicError {
            final long sum = a + b;
            if(a < 0 && b < 0) {
                if(sum > 0) {
                    throw new DynamicError("err:FOAR0002", "long overflows: " + a + " + " + b
                            + " = " + sum);
                }
            } else if(a > 0 && b > 0) {
                if(sum < 0) {
                    throw new DynamicError("err:FOAR0002", "long overflows: " + a + " + " + b
                            + " = " + sum);
                }
            }
            return sum;
        }

        public static long subtractL(final long a, final long b) throws DynamicError {
            final long surplus = a - b;
            if(a < 0 && b > 0) {
                if(surplus > 0) {
                    throw new DynamicError("err:FOAR0002", "long overflows: " + a + " - " + b
                            + " = " + surplus);
                }
            } else if(a > 0 && b < 0) {
                if(surplus < 0) {
                    throw new DynamicError("err:FOAR0002", "long overflows: " + a + " - " + b
                            + " = " + surplus);
                }
            }
            return surplus;
        }

        public static long multiplyL(final long a, final long b) throws DynamicError {
            if(a == 0L) {
                return 0L;
            }
            final long product = a * b;
            final long b2 = product / a;
            if(b2 != b) {
                throw new DynamicError("err:FOAR0002", "long overflows: " + a + " * " + b + " = "
                        + product);
            }
            return product;
        }

        @Deprecated
        public static long divideL(final long a, final long b) throws DynamicError {
            final long divided = a / b;
            final long a2 = divided * b;
            if(a2 != a) {
                throw new DynamicError("err:FOAR0002", "long overflows: " + a + " / " + b + " = "
                        + divided);
            }
            return divided;
        }
    }

}
