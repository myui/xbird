/*
 * @(#)$Id: UnaryMinus.java 3619 2008-03-26 07:23:03Z yui $
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
 * fs:unary-minus(A).
 * <DIV lang="en">
 * <u>denotes</u>
 * <ul>
 *  <li>op:numeric-unary-minus(xs:integer) as xs:integer</li>
 *  <li>op:numeric-unary-minus(xs:decimal) as xs:decimal</li>
 *  <li>op:numeric-unary-minus(xs:float) as xs:float</li>
 *  <li>op:numeric-unary-minus(xs:double) as xs:double</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#op.numeric
 */
public class UnaryMinus extends NumericOp {
    private static final long serialVersionUID = -3625986361127837766L;
    public static final String SYMBOL = "fs:unary-minus";

    public UnaryMinus() {
        super(SYMBOL);
    }

    @SuppressWarnings("unchecked")
    protected final Pair<AtomicType[], AtomicType>[] signatures() {
        final Pair<AtomicType[], AtomicType>[] s = new Pair[] {
                new Pair(new AtomicType[] { IntegerType.INTEGER }, IntegerType.INTEGER),
                new Pair(new AtomicType[] { DecimalType.DECIMAL }, DecimalType.DECIMAL),
                new Pair(new AtomicType[] { FloatType.FLOAT }, FloatType.FLOAT),
                new Pair(new AtomicType[] { DoubleType.DOUBLE }, DoubleType.DOUBLE),
                // workaround
                new Pair(new AtomicType[] { UntypedAtomicType.UNTYPED_ATOMIC }, DoubleType.DOUBLE) };
        return s;
    }

    public Sequence eval(DynamicContext ctxt, Item... args) throws XQueryException {
        assert (args.length == 1) : args;
        // dispatch exec
        final AtomicType retType = getReturnType();
        final Exec exec;
        if(TypeUtil.subtypeOf(retType, IntegerType.INTEGER)) {
            exec = NegInteger.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DecimalType.DECIMAL)) {
            exec = NegDecimal.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, FloatType.FLOAT)) {
            exec = NegFloat.INSTANCE;
        } else if(TypeUtil.subtypeOf(retType, DoubleType.DOUBLE)) {
            exec = NegDouble.INSTANCE;
        } else {
            throw new IllegalStateException("Unexpected return type: " + retType);
        }
        final AtomicValue ret = exec.eval(ctxt, args[0], null);
        return ret == null ? ValueSequence.EMPTY_SEQUENCE : ret;
    }

    private static final class NegInteger extends Exec {
        static final NegInteger INSTANCE = new NegInteger();

        public XInteger eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            final long res = ((XNumber) v1).asLong();
            return XInteger.valueOf(-res);
        }
    }

    private static final class NegFloat extends Exec {
        static final NegFloat INSTANCE = new NegFloat();

        public XFloat eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            float f1 = asFloat(v1, dynEnv);
            return XFloat.valueOf(-f1);
        }
    }

    private static final class NegDouble extends Exec {
        static final NegDouble INSTANCE = new NegDouble();

        public XDouble eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            final double d1 = asDouble(v1, dynEnv);
            if(d1 == Double.POSITIVE_INFINITY) {
                throw new DynamicError("err:FOAR0002", "result overflow");
            }
            if(d1 == Double.NEGATIVE_INFINITY) {
                throw new DynamicError("err:FOAR0002", "result underflow");
            }
            return XDouble.valueOf(-d1);
        }
    }

    private static final class NegDecimal extends Exec {
        static final NegDecimal INSTANCE = new NegDecimal();

        public XDecimal eval(DynamicContext dynEnv, Item v1, Item v2) throws XQueryException {
            BigDecimal bd1 = asDecimal(v1, dynEnv);
            return XDecimal.valueOf(bd1.negate());
        }
    }
}
