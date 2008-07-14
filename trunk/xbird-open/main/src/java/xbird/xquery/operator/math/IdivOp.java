/*
 * @(#)$Id: IdivOp.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.operator.math.DivOp.*;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.xs.*;

/**
 * fs:idiv(A, B).
 * <DIV lang="en">
 * <u>denotes</u>
 * <ul>
 *  <li>op:numeric-integer-divide($arg1 as numeric, $arg2 as numeric) as xs:integer</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#op.numeric
 * @link http://www.w3.org/TR/xquery-operators/#func-numeric-integer-divide
 */
public class IdivOp extends NumericOp {
    private static final long serialVersionUID = -7156063345316771952L;
    public static final String SYMBOL = "fs:idiv";

    public IdivOp() {
        super(SYMBOL);
    }

    @SuppressWarnings("unchecked")
    protected final Pair<AtomicType[], AtomicType>[] signatures() {
        final Pair<AtomicType[], AtomicType>[] s = new Pair[] {
                // op:integer-div(numeric, numeric) as xs:integer
                new Pair(new AtomicType[] { NumericType.Singleton.INSTANCE,
                        NumericType.Singleton.INSTANCE }, IntegerType.INTEGER),
                // WORKAROUND
                new Pair(new AtomicType[] { NumericType.getInstance(),
                        UntypedAtomicType.UNTYPED_ATOMIC }, IntegerType.INTEGER),
                new Pair(new AtomicType[] { UntypedAtomicType.UNTYPED_ATOMIC,
                        NumericType.getInstance() }, IntegerType.INTEGER),
                // REVIEWME
                new Pair(new AtomicType[] { UntypedAtomicType.UNTYPED_ATOMIC,
                        UntypedAtomicType.UNTYPED_ATOMIC }, IntegerType.INTEGER) };
        return s;
    }

    public XInteger eval(DynamicContext dynEnv, Item... args) throws XQueryException {
        assert (args.length == 2) : args;
        final XInteger result;
        final Item it1 = args[0];
        final Item it2 = args[1];
        switch(combinedArgTypes(it1, it2)) {
            case INT_INT:
                long ll1 = DivInteger.compute(it1, it2, dynEnv);
                result = new XInteger(ll1);
                break;
            case DECIMAL_DECIMAL:
            case DECIMAL_INT:
            case INT_DECIMAL:
                BigDecimal bd2 = DivDecimal.compute(it1, it2, dynEnv);
                long ll2 = bd2.longValue();
                result = new XInteger(ll2);
                break;
            case FLOAT_INT:
            case FLOAT_FLOAT:
            case FLOAT_DECIMAL:
            case INT_FLOAT:
            case DECIMAL_FLOAT:
                result = divideByFloatFloat(it1, it2, dynEnv);
                break;
            case DOUBLE_INT:
            case DOUBLE_FLOAT:
            case DOUBLE_DOUBLE:
            case DOUBLE_DECIMAL:
            case INT_DOUBLE:
            case FLOAT_DOUBLE:
            case DECIMAL_DOUBLE:
                result = divideByDoubleDouble(it1, it2, dynEnv);
                break;
            default:
                result = divideByDoubleDouble(it1, it2, dynEnv);
                //throw new XQueryException("err:XPTY0004", "Invalid types: " + args);
        }
        return result;
    }

    private static XInteger divideByFloatFloat(final Item v1, final Item v2, final DynamicContext dynEnv)
            throws XQueryException {
        final float res = DivFloat.compute(v1, v2, dynEnv);
        if(res == Float.POSITIVE_INFINITY) {
            throw new DynamicError("err:FOAR0002", "result overflow");
        }
        if(res == Float.NEGATIVE_INFINITY) {
            throw new DynamicError("err:FOAR0002", "result underflow");
        }
        long casted = (long) res;
        return new XInteger(casted);
    }

    private static XInteger divideByDoubleDouble(final Item v1, final Item v2, final DynamicContext dynEnv)
            throws XQueryException {
        final double res = DivDouble.compute(v1, v2, dynEnv);
        if(res == Double.POSITIVE_INFINITY) {
            throw new DynamicError("err:FOAR0002", "result overflow");
        }
        if(res == Double.NEGATIVE_INFINITY) {
            throw new DynamicError("err:FOAR0002", "result underflow");
        }
        long casted = (long) res;
        return new XInteger(casted);
    }

}
