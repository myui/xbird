/*
 * @(#)$Id: ModOp.java 3619 2008-03-26 07:23:03Z yui $
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

import java.math.BigDecimal;

import xbird.util.struct.Pair;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.*;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.xs.*;

/**
 * fs:mod(A, B).
 * <DIV lang="en">
 * <u>denotes</u>
 * <ul>
 *  <li>op:numeric-mod(xs:integer, xs:integer) as xs:integer</li>
 *  <li>op:numeric-mod(xs:decimal, xs:decimal) as xs:decimal</li>
 *  <li>op:numeric-mod(xs:float, xs:float) as xs:float</li>
 *  <li>op:numeric-mod(xs:double, xs:double) as xs:double</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#op.numeric
 */
public class ModOp extends NumericOp {
    private static final long serialVersionUID = 1094162533350340563L;
    public static final String SYMBOL = "fs:mod";

    public ModOp() {
        super(SYMBOL);
    }

    @SuppressWarnings("unchecked")
    protected final Pair<AtomicType[], AtomicType>[] signatures() {
        final Pair<AtomicType[], AtomicType>[] s = new Pair[] {
                // op:numeric-mod(xs:integer, xs:integer) as xs:integer
                new Pair(new AtomicType[] { IntegerType.INTEGER, IntegerType.INTEGER }, IntegerType.INTEGER),
                // op:numeric-mod(xs:decimal, xs:decimal) as xs:decimal
                new Pair(new AtomicType[] { DecimalType.DECIMAL, DecimalType.DECIMAL }, DecimalType.DECIMAL),
                // op:numeric-mod(xs:float, xs:float) as xs:float
                new Pair(new AtomicType[] { FloatType.FLOAT, FloatType.FLOAT }, FloatType.FLOAT),
                // workaround
                new Pair(new AtomicType[] { DecimalType.DECIMAL, FloatType.FLOAT }, FloatType.FLOAT),
                new Pair(new AtomicType[] { FloatType.FLOAT, DecimalType.DECIMAL }, FloatType.FLOAT),
                // op:numeric-mod(xs:double, xs:double) as xs:double
                new Pair(new AtomicType[] { DoubleType.DOUBLE, DoubleType.DOUBLE }, DoubleType.DOUBLE),
                // workaround
                new Pair(new AtomicType[] { NumericType.getInstance(), NumericType.getInstance() }, DoubleType.DOUBLE),
                new Pair(new AtomicType[] { UntypedAtomicType.UNTYPED_ATOMIC,
                        NumericType.getInstance() }, DoubleType.DOUBLE),
                new Pair(new AtomicType[] { NumericType.getInstance(),
                        UntypedAtomicType.UNTYPED_ATOMIC }, DoubleType.DOUBLE),
                new Pair(new AtomicType[] { UntypedAtomicType.UNTYPED_ATOMIC,
                        UntypedAtomicType.UNTYPED_ATOMIC }, DoubleType.DOUBLE) };
        return s;
    }

    public Sequence eval(DynamicContext ctxt, Item... args) throws XQueryException {
        assert (args.length == 2) : args;
        // dispatch exec
        final AtomicType retType = getReturnType();
        final Exec exec;
        if(TypeUtil.subtypeOf(retType, IntegerType.INTEGER)) {
            exec = ModInteger.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DecimalType.DECIMAL)) {
            exec = ModDecimal.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, FloatType.FLOAT)) {
            exec = ModFloat.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DoubleType.DOUBLE)) {
            exec = ModDouble.INSTANCE;
        } else {
            throw new IllegalStateException("Unexpected return type: " + retType);
        }
        AtomicValue ret = exec.eval(ctxt, args[0], args[1]);
        return ret == null ? ValueSequence.EMPTY_SEQUENCE : ret;
    }

    private static final class ModInteger extends Exec {
        static final ModInteger INSTANCE = new ModInteger();

        public XInteger eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            long i1 = asLong(v1, dynEnv);
            long i2 = asLong(v2, dynEnv);
            final long res = i1 % i2;
            return XInteger.valueOf(res);
        }
    }

    private static final class ModFloat extends Exec {
        static final ModFloat INSTANCE = new ModFloat();

        public XFloat eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            float f1 = asFloat(v1, dynEnv);
            float f2 = asFloat(v2, dynEnv);
            float res = f1 % f2;
            return XFloat.valueOf(res);
        }
    }

    private static final class ModDouble extends Exec {
        static final ModDouble INSTANCE = new ModDouble();

        public XDouble eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            double d1 = asDouble(v1, dynEnv);
            double d2 = asDouble(v2, dynEnv);
            final double res = d1 % d2;
            if(res == Double.POSITIVE_INFINITY) {
                throw new DynamicError("err:FOAR0002", "result overflow");
            }
            if(res == Double.NEGATIVE_INFINITY) {
                throw new DynamicError("err:FOAR0002", "result underflow");
            }
            return XDouble.valueOf(res);
        }
    }

    private static final class ModDecimal extends Exec {
        static final ModDecimal INSTANCE = new ModDecimal();

        public XDecimal eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            final BigDecimal divisor = asDecimal(v2, dynEnv);
            if(divisor.equals(BigDecimal.ZERO)) {
                throw new DynamicError("err:FOAR0001", "mod by zero");
            }
            BigDecimal bd1 = asDecimal(v1, dynEnv);
            BigDecimal res = bd1.remainder(divisor); // result may be negative
            return XDecimal.valueOf(res);
        }
    }

}
