/*
 * @(#)$Id: IdRef.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.List;
import java.util.StringTokenizer;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.NodeSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.node.NodeType;
import xbird.xquery.type.xs.IDType;

/**
 * fn:idref.
 * <DIV lang="en">
 * An element or attribute typically acquires the is-idrefs property by being validated
 * against the schema type xs:IDREF or xs:IDREFS, or (for attributes only) by being described 
 * as of type IDREF or IDREFS in a DTD.
 * <ul>
 * <li>fn:idref($arg as xs:string*) as node()*</li>
 * <li>fn:idref($arg as xs:string*, $node as node()) as node()*</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-idref
 */
public final class IdRef extends BuiltInFunction {
    private static final long serialVersionUID = 9211859623622428079L;
    public static final String SYMBOL = "fn:idref";
    
    public IdRef() {
        super(SYMBOL, TypeRegistry.safeGet("node()*"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        Type stra = TypeRegistry.safeGet("xs:string*");
        s[0] = new FunctionSignature(getName(), new Type[] { stra });
        s[1] = new FunctionSignature(getName(), new Type[] { stra,
                NodeType.ANYNODE });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null);
        final int arglen = argv.size();
        assert (arglen == 1 || arglen == 2);
        final XQNode node;
        if (arglen == 2) {
            Item second = argv.getItem(1);
            if (!(second instanceof XQNode)) {
                throw new DynamicError("err:XPTY0004", "Second argument is expected to be a node, but was "
                        + second.getType());
            }
            node = (XQNode) second;
        } else {
            Item contextItem = dynEnv.contextItem();
            if (contextItem == null) {
                throw new DynamicError("err:XPDY0002", "ContextItem is not set");
            } else if (!(contextItem instanceof XQNode)) {
                throw new DynamicError("err:XPTY0004", "Context item is expected to be a node, but was "
                        + contextItem.getType());
            }
            node = (XQNode) contextItem;
        }
        // An error is raised if node's root is not a document node
        XQNode root = node.getRoot();
        if (root.nodeKind() != NodeKind.DOCUMENT) {
            throw new DynamicError("err:FODC0001", "root node was not document-node.");
        }
        Item firstItem = argv.getItem(0);
        NodeSequence<XQNode> nodes = new NodeSequence<XQNode>(dynEnv);
        for (Item it : firstItem) {
            assert (it instanceof XString);
            String ids = it.stringValue();
            final StringTokenizer tok = new StringTokenizer(ids, " ");
            while (tok.hasMoreTokens()) {
                String id = tok.nextToken();
                if (!IDType.ID.isValid(id)) {
                    // If any of the tokens is not a lexically valid IDREF, it is ignored. 
                    continue;
                }
                List<XQNode> founds = Id.getElementsById(root, id, true);
                for (XQNode n : founds) {
                    nodes.addItem(n);
                }
            }
        }
        return nodes;
    }
}
