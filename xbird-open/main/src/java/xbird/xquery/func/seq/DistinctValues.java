/*
 * @(#)$Id: DistinctValues.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.seq;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.DistinctSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.StringType;

/**
 * fn:distinct-values.
 * <DIV lang="en">
 * Returns the sequence that results from removing from $arg all but one of
 * a set of values that are eq to one other. 
 * <ul>
 * <li>fn:distinct-values($arg as xdt:anyAtomicType*) as xdt:anyAtomicType*</li>
 * <li>fn:distinct-values($arg as xdt:anyAtomicType*, $collation as xs:string) as xdt:anyAtomicType*</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-distinct-values
 */
public final class DistinctValues extends BuiltInFunction {
    private static final long serialVersionUID = 8071229137218293533L;
    
    public static final String SYMBOL = "fn:distinct-values";

    public DistinctValues() {
        super(SYMBOL, TypeRegistry.safeGet("xs:anyAtomicType*"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        Type anyatom = TypeRegistry.safeGet("xs:anyAtomicType*");
        s[0] = new FunctionSignature(getName(), new Type[] { anyatom });
        s[1] = new FunctionSignature(getName(), new Type[] { anyatom, StringType.STRING });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        if (argv == null) {
            throw new IllegalArgumentException();
        }
        // NaN does not equal itself, if $arg contains multiple NaN values
        // a single NaN is returned.
        // TODO handle collation
        Item arg = argv.getItem(0);
        return DistinctSequence.<Item> wrap(arg, dynEnv);
    }

}
