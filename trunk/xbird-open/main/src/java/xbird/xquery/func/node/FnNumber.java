/*
 * @(#)$Id: FnNumber.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.node;

import java.util.Iterator;

import xbird.xquery.*;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XDouble;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.DoubleType;

/**
 * fn:number.
 * <DIV lang="en">
 * Returns the value indicated by $arg or, if $arg is not specified, 
 * the context item after atomization, converted to an xs:double.
 * <ul>
 * <li>fn:number() as xs:double</li>
 * <li>fn:number($arg as xdt:anyAtomicType?) as xs:double</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-local-name
 */
public final class FnNumber extends BuiltInFunction {
    private static final long serialVersionUID = 8825327688115851462L;
    
    public static final String SYMBOL = "fn:number";

    public FnNumber() {
        super(SYMBOL, DoubleType.DOUBLE);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        s[0] = new FunctionSignature(getName());
        s[1] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:anyAtomicType?") });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        // If $arg is the empty sequence or if $arg or the context item cannot be converted 
        // to an xs:double, the xs:double value NaN is returned.
        final AtomicValue candidate;
        if(argv == null) {
            Item contextItem = dynEnv.contextItem();
            if(contextItem == null) {
                throw new DynamicError("err:XPDY0002", "ContextItem is not set");
            }
            Sequence<? extends Item> atomized = contextItem.atomize(dynEnv);
            Iterator<? extends Item> itor = atomized.iterator();
            if(itor.hasNext()) {
                return XDouble.valueOf(Double.NaN);
            }
            Item it = itor.next();
            assert (!itor.hasNext());
            if(!(it instanceof AtomicValue)) {
                return XDouble.valueOf(Double.NaN);
            }
            candidate = (AtomicValue) it;
        } else {
            if(argv.isEmpty()) {
                return XDouble.valueOf(Double.NaN);
            }
            Item it = argv.getItem(0);
            if(it.isEmpty()) {
                return XDouble.valueOf(Double.NaN);
            }
            candidate = (AtomicValue) it;
        }
        final XDouble converted;
        try {
            converted = ((AtomicValue) candidate).castAs(DoubleType.DOUBLE, dynEnv);
        } catch (XQueryException xqe) {
            return XDouble.valueOf(Double.NaN);
        } catch (XQRTException rte) {
            return XDouble.valueOf(Double.NaN);
        }
        return converted;
    }

}
