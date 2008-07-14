/*
 * @(#)$Id: Min.java 3619 2008-03-26 07:23:03Z yui $
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

import java.text.Collator;
import java.util.Iterator;

import xbird.util.resource.CollationUtils;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XNumber;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.DurationValue;
import xbird.xquery.dm.value.xsi.UntypedAtomicValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.DoubleType;
import xbird.xquery.type.xs.StringType;

/**
 * fn:min.
 * <DIV lang="en">
 * <ul>
 * <li>fn:min($arg as xdt:anyAtomicType*) as xdt:anyAtomicType?</li>
 * <li>fn:min($arg as xdt:anyAtomicType*, $collation as string) as xdt:anyAtomicType?</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-min
 */
public final class Min extends BuiltInFunction {
    private static final long serialVersionUID = 2698480010086630239L;
    public static final String SYMBOL = "fn:min";

    public Min() {
        super(SYMBOL, TypeRegistry.safeGet("xs:anyAtomicType?"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        Type anyatom = TypeRegistry.safeGet("xs:anyAtomicType*");
        s[0] = new FunctionSignature(getName(), new Type[] { anyatom });
        s[1] = new FunctionSignature(getName(), new Type[] { anyatom, StringType.STRING });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        assert (argv != null);
        // If the converted sequence is empty, the empty sequence is returned.
        if(argv.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        assert (argv.size() <= 2) : argv.size();
        Item arg = argv.getItem(0);
        Iterator<? extends Item> argItor = arg.iterator();
        if(!argItor.hasNext()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        Item firstItem = argItor.next();
        if(firstItem instanceof UntypedAtomicValue) {
            // Values of type xdt:untypedAtomic in $arg are cast to xs:double.
            firstItem = ((UntypedAtomicValue) firstItem).castAs(DoubleType.DOUBLE, dynEnv);
        }
        if(!argItor.hasNext()) {
            return firstItem;
        }
        final Type firstType = firstItem.getType();
        if(firstItem instanceof XNumber) {
            XNumber min = (XNumber) firstItem;
            // If the converted sequence contains the value NaN, the value NaN is returned.
            if(min.isNaN()) {
                return min;
            }
            while(argItor.hasNext()) {
                Item it = argItor.next();
                if(it instanceof UntypedAtomicValue) {
                    it = ((UntypedAtomicValue) it).castAs(DoubleType.DOUBLE, dynEnv);
                } else if(!(it instanceof XNumber)) {
                    throw new DynamicError("err:FORG0006", "fs:plus(" + min.getType() + ", "
                            + it.getType() + ") is not defined.");
                }
                XNumber cmp = (XNumber) it;
                if(cmp.isNaN()) {
                    return cmp;
                }
                if(cmp.compareTo(min) < 0) {
                    min = cmp;
                }
            }
            return min;
        } else if(firstItem instanceof DurationValue) {
            // Duration values must either all be xdt:yearMonthDuration values 
            // or must all be xdt:dayTimeDuration values.
            DurationValue min = (DurationValue) firstItem;
            assert (firstType != null);
            while(argItor.hasNext()) {
                Item it = argItor.next();
                if(it instanceof DurationValue) {
                    throw new DynamicError("err:FORG0006", "Duration values must all be `"
                            + firstType + "`, but found `" + it.getType() + "`");
                }
                DurationValue cmp = (DurationValue) it;
                if(cmp.compareTo(min) < 0) {
                    min = cmp;
                }
            }
            return min;
        } else if(TypeUtil.subtypeOf(firstType, StringType.STRING)) {
            AtomicValue min = (AtomicValue) firstItem;
            final int arglen = argv.size();
            final Collator collator;
            if(arglen == 2) {
                Item sec = argv.getItem(1);
                Type secType = sec.getType();
                if(!TypeUtil.subtypeOf(secType, StringType.STRING)) {
                    throw new DynamicError("err:FORG0006", "second argument is expected to be xs:string, but was "
                            + secType);
                }
                String uri = sec.stringValue();
                collator = CollationUtils.resolve(uri, dynEnv.getStaticContext());
            } else {
                collator = Collator.getInstance(); // compare in default locale
            }
            while(argItor.hasNext()) {
                Item it = argItor.next();
                Type cmpType = it.getType();
                if(!TypeUtil.subtypeOf(cmpType, StringType.STRING)) {
                    throw new DynamicError("err:FORG0006", "imcomparable xs:string and " + cmpType);
                }
                if(collator.compare(it, firstItem) < 0) {
                    min = (AtomicValue) it;
                }
            }
            return min;
        } else {
            throw new DynamicError("err:FORG0006", "Invalid argument type: " + firstItem.getType());
        }
    }

}
