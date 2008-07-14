/*
 * @(#)$Id: FnName.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.StringType;

/**
 * fn:name.
 * <DIV lang="en">
 * Returns the name of a node, as an xs:string that is either the zero-length string, 
 * or has the lexical form of an xs:QName.
 * <ul>
 * <li>fn:name() as xs:string</li>
 * <li>fn:name($arg as node()?) as xs:string</li?
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-name
 */
public final class FnName extends BuiltInFunction {
    private static final long serialVersionUID = 6577180006130235840L;
    
    public static final String SYMBOL = "fn:name";

    public FnName() {
        super(SYMBOL, StringType.STRING);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        s[0] = new FunctionSignature(getName());
        s[1] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("node()?") });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        final QualifiedName nodeName;
        if (argv == null) {
            // If the argument is omitted, it defaults to the context node.
            Item contextItem = dynEnv.contextItem();
            if (contextItem == null) {
                throw new DynamicError("err:XPDY0002", "ContextItem is not set");
            }
            if (!(contextItem instanceof XQNode)) {
                throw new DynamicError("err:XPTY0004", "context item is expected to be a node, but was "
                        + contextItem.getType());
            } else {
                nodeName = ((XQNode) contextItem).nodeName();
            }
        } else {
            assert (argv.size() == 1);
            Item firstItem = argv.getItem(0);
            if (firstItem.isEmpty()) {
                // If the argument is supplied and is the empty sequence, 
                // the function returns the zero-length string.
                return XString.valueOf("");
            }
            XQNode arg = (XQNode) firstItem;
            nodeName = arg.nodeName();
        }
        // If the target node has no name, the function returns the zero-length string.
        if (nodeName == null) {
            return XString.valueOf("");
        }
        final String name = QNameUtil.toLexicalForm(nodeName);
        return XString.valueOf(name);
    }

}
