/*
 * @(#)$Id: Sum.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.agg;

import java.util.Iterator;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.literal.XNumber;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.DurationValue;
import xbird.xquery.dm.value.xsi.UntypedAtomicValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.operator.math.PlusOp;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.DoubleType;

/**
 * fn:sum.
 * <DIV lang="en">
 * Returns a value obtained by adding together the values in $arg. If $zero is not specified, 
 * then the value returned for an empty sequence is the xs:integer value 0.
 * If $zero is specified, then the value returned for an empty sequence is $zero.
 * <ul>
 * <li>fn:sum($arg as xdt:anyAtomicType*) as xdt:anyAtomicType</li>
 * <li>fn:sum($arg as xdt:anyAtomicType*, $zero as xdt:anyAtomicType?) as xdt:anyAtomicType?</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-sum
 */
public final class Sum extends BuiltInFunction {
    private static final long serialVersionUID = -7240838372260445840L;
    public static final String SYMBOL = "fn:sum";

    public Sum() {
        super(SYMBOL, TypeRegistry.safeGet("xs:anyAtomicType?")); // TODO correct type
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        Type anyatom = TypeRegistry.safeGet("xs:anyAtomicType*");
        s[0] = new FunctionSignature(getName(), new Type[] { anyatom });
        s[1] = new FunctionSignature(getName(), new Type[] { anyatom,
                TypeRegistry.safeGet("xs:anyAtomicType?") });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        final int arglen = argv.size();
        final Item zero;
        if(arglen == 2) {
            zero = argv.getItem(1);
        } else if(arglen == 1) {
            zero = XInteger.valueOf(0);
        } else {
            throw new IllegalStateException("Illefal args: " + arglen);
        }
        Item arg = argv.getItem(0);
        Iterator<? extends Item> argItor = arg.iterator();
        if(!argItor.hasNext()) {
            // If the converted sequence is empty, then the single-argument form
            // of the function returns the xs:integer value 0; 
            // the two-argument form returns the value of the argument $zero.
            return zero;
        }
        Item firstItem = argItor.next();
        if(firstItem instanceof UntypedAtomicValue) {
            // Values of type xdt:untypedAtomic in $arg are cast to xs:double.
            firstItem = ((UntypedAtomicValue) firstItem).castAs(DoubleType.DOUBLE, dynEnv);
        }
        if(!argItor.hasNext()) {
            return firstItem;
        }
        if(firstItem instanceof XNumber) {
            XNumber sum = (XNumber) firstItem;
            while(argItor.hasNext()) {
                Item toadd = argItor.next();
                if(toadd instanceof UntypedAtomicValue) {
                    toadd = ((UntypedAtomicValue) toadd).castAs(DoubleType.DOUBLE, dynEnv);
                } else if(!(toadd instanceof XNumber)) {
                    throw new DynamicError("err:FORG0006", "fs:plus(" + sum.getType() + ", "
                            + toadd.getType() + ") is not defined.");
                }
                // TODO more efficient processing
                final PlusOp op = new PlusOp();
                op.staticAnalysis(dynEnv.getStaticContext(), sum, toadd);
                sum = (XNumber) op.eval(dynEnv, sum, toadd);
            }
            return sum;
        } else if(firstItem instanceof DurationValue) {
            // Duration values must either all be xdt:yearMonthDuration values 
            // or must all be xdt:dayTimeDuration values.
            DurationValue sum = (DurationValue) firstItem;
            Type firstType = firstItem.getType();
            assert (firstType != null);
            while(argItor.hasNext()) {
                Item toadd = argItor.next();
                if(toadd instanceof DurationValue) {
                    throw new DynamicError("err:FORG0006", "Duration values must all be `"
                            + firstType + "`, but found `" + toadd.getType() + "`");
                }
                final PlusOp op = new PlusOp();
                op.staticAnalysis(dynEnv.getStaticContext(), sum, toadd);
                sum = (DurationValue) op.eval(dynEnv, sum, toadd);
            }
            return sum;
        } else {
            throw new DynamicError("err:FORG0006", "Invalid argument type: " + firstItem.getType());
        }

    }

}
