/*
 * @(#)$Id: RoundHalfToEven.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.math;

import java.util.Iterator;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.literal.XNumber;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.UntypedAtomicValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.DoubleType;
import xbird.xquery.type.xs.IntegerType;

/**
 * The value returned is the nearest (that is, numerically closest) numeric to $arg
 * that is a multiple of ten to the power of minus $precision. 
 * <DIV lang="en">
 * <ul>
 * <li>fn:round-half-to-even($arg as numeric?) as numeric?</li>
 * <li>fn:round-half-to-even($arg as numeric?, $precision as xs:integer) as numeric?</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RoundHalfToEven extends BuiltInFunction {
    private static final long serialVersionUID = 2605509627310272142L;
    public static final String SYMBOL = "fn:round-half-to-even";

    public RoundHalfToEven() {
        super(SYMBOL, TypeRegistry.safeGet("xs:numeric?"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] t = new FunctionSignature[2];
        t[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:numeric?") });
        t[1] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:numeric?"),
                IntegerType.INTEGER });
        return t;
    }

    protected XNumber promote(XNumber value, int scale) {
        final double v = value.getNumber().doubleValue();
        if(Double.isNaN(v)) {
            return value;
        }
        return value.roundHalfToEven(scale);
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        Item arg = argv.getItem(0);
        Iterator<? extends Item> argItor = arg.iterator();
        if(!argItor.hasNext()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        Item firstItem = argItor.next();
        if(firstItem instanceof UntypedAtomicValue) {
            firstItem = ((UntypedAtomicValue) firstItem).castAs(DoubleType.DOUBLE, dynEnv);
        }
        if(firstItem instanceof XNumber) {
            XNumber num = (XNumber) firstItem;
            final int scale;
            final int args = argv.size();
            if(args > 1) {
                Item precision = argv.getItem(1);
                if(!(precision instanceof XInteger)) {
                    throw new DynamicError("err:FORG0006", "second argument type for precision is invalid: "
                            + precision.getType());
                }
                scale = ((XInteger) precision).getNumber().intValue();
            } else {
                scale = 0;
            }
            XNumber promoted = promote(num, scale);
            return promoted;
        } else {
            throw new DynamicError("err:FORG0006", "Invalid argument type: " + firstItem.getType());
        }
    }

}
