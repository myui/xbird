/*
 * @(#)$Id: DefaultCollation.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.context;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.xs.StringType;

/**
 * fn:default-collation() as xs:string.
 * <DIV lang="en">
 * Returns the value of the default collation property
 * from the static context.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-default-collation
 */
public final class DefaultCollation extends BuiltInFunction {
    private static final long serialVersionUID = 1811285582110034199L;
    
    public static final String SYMBOL = "fn:default-collation";

    public DefaultCollation() {
        super(SYMBOL, StringType.STRING);
        this._evalPocily = EvaluationPolicy.eager;
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName());
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        final String collation = dynEnv.getStaticContext().getDefaultCollation();
        return XString.valueOf(collation);
    }

}
