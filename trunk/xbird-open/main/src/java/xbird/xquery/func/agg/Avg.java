/*
 * @(#)$Id: Avg.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.operator.math.DivOp;
import xbird.xquery.operator.math.PlusOp;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.DoubleType;

/**
 * fn:avg($arg as xdt:anyAtomicType*) as xdt:anyAtomicType?.
 * <DIV lang="en">
 * Returns the average of the values in the input sequence $arg, 
 * that is, the sum of the values divided by the number of values.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-avg
 */
public final class Avg extends BuiltInFunction {
    private static final long serialVersionUID = -1753102015622613644L;

    public static final String SYMBOL = "fn:avg";

    public Avg() {
        super(SYMBOL, TypeRegistry.safeGet("xs:anyAtomicType?"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:anyAtomicType*") });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        // If $arg is the empty sequence, the empty sequence is returned.
        if(argv.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        Item arg = argv.getItem(0);
        Iterator<? extends Item> argItor = arg.iterator();
        Item firstItem = argItor.next();
        if(firstItem instanceof UntypedAtomicValue) {
            // If $arg contains values of type xdt:untypedAtomic they are cast to xs:double.
            firstItem = ((UntypedAtomicValue) firstItem).castAs(DoubleType.DOUBLE, dynEnv);
        }
        if(!argItor.hasNext()) {
            return firstItem;
        }
        if(firstItem instanceof XNumber) {
            XNumber sum = (XNumber) firstItem;
            int size;
            for(size = 1; argItor.hasNext(); size++) {
                Item toadd = argItor.next();
                if(toadd instanceof UntypedAtomicValue) {
                    toadd = ((UntypedAtomicValue) toadd).castAs(DoubleType.DOUBLE, dynEnv);
                } else if(!(toadd instanceof XNumber)) {
                    throw new DynamicError("err:FORG0006", "fs:plus(" + sum.getType() + ", "
                            + toadd.getType() + ") is not defined.");
                }
                final PlusOp op = new PlusOp();
                op.staticAnalysis(dynEnv.getStaticContext(), sum, toadd);
                sum = (XNumber) op.eval(dynEnv, sum, toadd);
            }
            final DivOp op = new DivOp();
            final XInteger divby = XInteger.valueOf(size);
            op.staticAnalysis(dynEnv.getStaticContext(), sum, divby);
            return op.eval(dynEnv, sum, divby);
        } else if(firstItem instanceof DurationValue) {
            // Duration values must either all be xdt:yearMonthDuration values 
            // or must all be xdt:dayTimeDuration values.
            DurationValue sum = (DurationValue) firstItem;
            Type firstType = firstItem.getType();
            assert (firstType != null);
            int size;
            for(size = 1; argItor.hasNext(); size++) {
                Item toadd = argItor.next();
                if(toadd instanceof DurationValue) {
                    throw new DynamicError("err:FORG0006", "Duration values must all be `"
                            + firstType + "`, but found `" + toadd.getType() + "`");
                }
                final PlusOp op = new PlusOp();
                op.staticAnalysis(dynEnv.getStaticContext(), sum, toadd);
                sum = (DurationValue) op.eval(dynEnv, sum, toadd);
            }
            final DivOp op = new DivOp();
            final XInteger divby = XInteger.valueOf(size);
            op.staticAnalysis(dynEnv.getStaticContext(), sum, divby);
            return op.eval(dynEnv, sum, divby);
        } else {
            throw new DynamicError("err:FORG0006", "Invalid argument type: " + firstItem.getType());
        }
    }

}
