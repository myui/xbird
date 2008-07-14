/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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
package xbird.xquery.func.ext;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.ItemType;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.IntegerType;

/**
 * ext:hash(str as item()) as xs:integer
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class StrHash extends BuiltInFunction {
    private static final long serialVersionUID = -4768899596763147042L;

    public static final String SYMBOL = EXT_NSPREFIX + ":hash";

    public StrHash() {
        super(SYMBOL, IntegerType.INTEGER);
    }

    @Override
    protected FunctionSignature[] signatures() {
        return new FunctionSignature[] { new FunctionSignature(getName(), new Type[] { ItemType.ANY_ITEM }) };
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        Item firstItem = argv.getItem(0);
        String str = firstItem.stringValue();
        int hashvalue = str.hashCode();
        return new XInteger(hashvalue);
    }

}
