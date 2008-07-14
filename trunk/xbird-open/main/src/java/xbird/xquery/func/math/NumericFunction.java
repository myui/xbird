/*
 * @(#)$Id: NumericFunction.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.literal.XNumber;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.UntypedAtomicValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.DoubleType;

/**
 * 
 * <DIV lang="en">
 * <ul>
 * <li>If the argument is the empty sequence, the empty sequence is returned.</li>
 * <li>For xs:float and xs:double arguments, if the argument is "NaN", "NaN" is returned.</li>
 * <li>Except for fn:abs(), for xs:float and xs:double arguments, if the argument 
 * is positive or negative infinity, positive or negative infinity is returned.</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#numeric-value-functions
 */
public abstract class NumericFunction extends BuiltInFunction {

    public NumericFunction(String funcName, Type retType) {
        super(funcName, retType);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] t = new FunctionSignature[1];
        t[0] = new FunctionSignature(getName(), new Type[] { getReturnType() });
        return t;
    }

    protected abstract XNumber promote(XNumber value);

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null && argv.size() == 1);
        Item arg = argv.getItem(0);
        Iterator<? extends Item> argItor = arg.iterator();
        if (!argItor.hasNext()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        Item firstItem = argItor.next();
        assert (!argItor.hasNext());
        if (firstItem instanceof UntypedAtomicValue) {
            firstItem = ((UntypedAtomicValue) firstItem).castAs(DoubleType.DOUBLE, dynEnv);
        }
        if (firstItem instanceof XNumber) {
            XNumber num = (XNumber) firstItem;
            XNumber promoted = promote(num);
            return promoted;
        } else {
            throw new DynamicError("err:FORG0006", "Invalid argument type: " + firstItem.getType());
        }
    }

}
