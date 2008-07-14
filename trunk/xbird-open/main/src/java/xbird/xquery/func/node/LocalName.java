/*
 * @(#)$Id: LocalName.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.StringType;

/**
 * fn:local-name.
 * <DIV lang="en">
 * Returns the local part of the name of $arg as an xs:string that will either be 
 * the zero-length string or will have the lexical form of an xs:NCName.
 * <ul>
 * <li>fn:local-name() as xs:string</li>
 * <li>fn:local-name($arg as node()?) as xs:string</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-local-name
 */
public final class LocalName extends BuiltInFunction {
    private static final long serialVersionUID = 7137754089857349444L;
    public static final String SYMBOL = "fn:local-name";

    public LocalName() {
        super(SYMBOL, StringType.STRING);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        s[0] = new FunctionSignature(getName());
        s[1] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("node()?") });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        final XQNode node;
        if (argv == null) {
            // If the argument is omitted, it defaults to the context node. 
            Item contextItem = dynEnv.contextItem();
            if (contextItem == null) {
                throw new DynamicError("err:XPDY0002", "ContextItem is not set");
            }
            if (!(contextItem instanceof XQNode)) {
                throw new DynamicError("err:XPTY0004", "context item is expected to be a node, but was "
                        + contextItem.getType());
            }
            node = (XQNode) contextItem;
        } else {
            assert (argv.size() == 1);
            Item firstItem = argv.getItem(0);
            node = (XQNode) firstItem;
        }
        final String lpart;
        if (node.isEmpty()) {
            lpart = "";
        } else {
            QualifiedName nodename = node.nodeName();
            if (nodename == null) {
                lpart = "";
            } else {
                lpart = nodename.getLocalPart();
            }
        }
        return XString.valueOf(lpart);
    }

}
