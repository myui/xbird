/*
 * @(#)$Id: Nilled.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.instance.DocumentTableModel.DTMElement;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.node.DMElement;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.func.*;
import xbird.xquery.func.opt.EagerEvaluated;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;

/**
 * fn:nilled($arg as node()?) as xs:boolean?.
 * <DIV lang="en">
 * Returns an xs:boolean indicating whether the argument node is "nilled".
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-nilled
 */
public final class Nilled extends BuiltInFunction {
    private static final long serialVersionUID = -8644861906953531601L;
    public static final String SYMBOL = "fn:nilled";

    public Nilled() {
        super(SYMBOL, TypeRegistry.safeGet("xs:boolean?"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("node()?") });
        return s;
    }

    @Override
    public Function staticAnalysis(StaticContext context, List<? extends XQExpression> params) throws XQueryException {
        assert (params != null);
        if (params.isEmpty()) {
            return new EagerEvaluated(this, ValueSequence.EMPTY_SEQUENCE);
        }
        return this;
    }
    
    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null);
        if (argv.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        assert (argv.size() == 1);
        Item arg = argv.getItem(0);
        //If the argument is not an element node, returns the empty sequence.
        if (arg instanceof XQNode) {
            byte kind = ((XQNode) arg).nodeKind();
            if (kind == NodeKind.ELEMENT) {
                final boolean nilled;
                if (arg instanceof DMElement) {
                    nilled = ((DMElement) arg).nilled();
                } else {
                    // check element attribute value of "xsi:nil"
                    assert (arg instanceof DTMElement);
                    nilled = ((DTMElement) arg).nilled();
                }
                return nilled ? BooleanValue.TRUE : BooleanValue.FALSE;
            }
        }
        return ValueSequence.EMPTY_SEQUENCE;
    }

}
