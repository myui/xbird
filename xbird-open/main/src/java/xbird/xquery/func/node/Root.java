/*
 * @(#)$Id: Root.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.node.NodeType;

/**
 * fn:root.
 * <DIV lang="en">
 * Returns the root of the tree to which $arg belongs. 
 * This will usually, but not necessarily, be a document node.
 * <ul>
 * <li>fn:root() as node()</li>
 * <li>fn:root($arg as node()?) as node()?</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-root
 */
public final class Root extends BuiltInFunction {
    private static final long serialVersionUID = -6777771959484142261L;
    public static final String SYMBOL = "fn:root";

    public Root() {
        super(SYMBOL, NodeType.ANYNODE);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        s[0] = new FunctionSignature(getName());
        s[1] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("node()?") });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        if(argv == null) {
            // If the function is called without an argument, the context item is used 
            // as the default argument.
            Item i = dynEnv.contextItem();
            if(i == null) {
                throw new DynamicError("err:XPDY0002", "ContextItem is not set");
            }
            if(i instanceof XQNode) {
                ((XQNode) i).getRoot();
            } else {
                throw new DynamicError("err:XPTY0004", "context item is not a node");
            }
        } else if(argv.size() == 1) {
            Item i = argv.getItem(0);
            if(i.isEmpty()) {
                return ValueSequence.EMPTY_SEQUENCE;
            }
            if(i instanceof XQNode) {
                return ((XQNode) i).getRoot();
            }
        }
        throw new DynamicError("Arguments size of " + SYMBOL + " must be 0 or 1, but was "
                + argv.size());
    }

}
