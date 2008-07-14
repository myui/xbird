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
import xbird.xquery.dm.value.sequence.ChainedSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;

/**
 * ext:union-all($first as item()*, $sec as item()*) as item()*
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class UnionAll extends BuiltInFunction {
    private static final long serialVersionUID = 6651349606315469208L;
    public static final String SYMBOL = EXT_NSPREFIX + ":union-all";

    public UnionAll() {
        super(SYMBOL, SequenceType.ANY_ITEMS);
    }

    @Override
    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS,
                SequenceType.ANY_ITEMS });
        return s;
    }

    public ChainedSequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        Item firstItem = argv.getItem(0);
        Item secItem = argv.getItem(1);
        return new ChainedSequence(firstItem, secItem, dynEnv);
    }

    public static Sequence unionAll(final DynamicContext dynEnv, final Sequence... sequences) {
        final int size = sequences.length;
        if(size == 0) {
            return ValueSequence.EMPTY_SEQUENCE;
        } else if(size == 1) {
            return sequences[0];
        }
        ChainedSequence piped = new ChainedSequence(sequences[0], sequences[1], dynEnv);
        for(int i = 2; i < size; i++) {
            piped = new ChainedSequence(piped, sequences[i], dynEnv);
        }
        return piped;
    }
}
