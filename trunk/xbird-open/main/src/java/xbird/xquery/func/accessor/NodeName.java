/*
 * @(#)$Id: NodeName.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.accessor;

import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.QNameValue;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.func.*;
import xbird.xquery.func.opt.EagerEvaluated;
import xbird.xquery.meta.*;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;

/**
 * fn:node-name($arg as node()?) as xs:QName?.
 * <DIV lang="en">
 * Returns an expanded-QName for node kinds that can have names.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-node-name
 */
public final class NodeName extends BuiltInFunction {
    private static final long serialVersionUID = 3720642424037052117L;
    public static final String SYMBOL = "fn:node-name";

    public NodeName() {
        super(SYMBOL, TypeRegistry.safeGet("xs:QName?"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("node()?") });
        return s;
    }

    public Function staticAnalysis(StaticContext context, List<? extends XQExpression> params)
            throws XQueryException {
        assert (params != null);
        if(params.isEmpty()) {
            return new EagerEvaluated(this, ValueSequence.EMPTY_SEQUENCE);
        }
        return this;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        if(argv.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        final Item it = argv.getItem(0);
        final IFocus<? extends Item> itemItor = it.iterator();
        if(!itemItor.hasNext()) {
            itemItor.closeQuietly();
            return ValueSequence.EMPTY_SEQUENCE;
        }
        final Item first = itemItor.next();
        assert (!itemItor.hasNext()) : it;
        itemItor.closeQuietly();
        final XQNode node = (XQNode) first;
        final QualifiedName nodename = node.nodeName();
        if(nodename != null) {
            return new QNameValue(nodename);
        }
        return ValueSequence.EMPTY_SEQUENCE;
    }

}
