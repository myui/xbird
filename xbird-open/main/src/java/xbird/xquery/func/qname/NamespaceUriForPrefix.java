/*
 * @(#)$Id: NamespaceUriForPrefix.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.qname;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.instance.DocumentTableModel.DTMAttribute;
import xbird.xquery.dm.instance.DocumentTableModel.DTMElement;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.node.DMAttribute;
import xbird.xquery.dm.value.node.DMElement;
import xbird.xquery.dm.value.sequence.INodeSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.node.ElementTest;

/**
 * fn:namespace-uri-for-prefix($prefix as xs:string?, $element as element()) as xs:anyURI?.
 * <DIV lang="en">
 * Returns the namespace URI of one of the in-scope namespaces for $element, 
 * identified by its namespace prefix.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-namespace-uri-for-prefix
 */
public final class NamespaceUriForPrefix extends BuiltInFunction {
    private static final long serialVersionUID = -5124736970576798930L;
    public static final String SYMBOL = "fn:namespace-uri-for-prefix";

    public NamespaceUriForPrefix() {
        super(SYMBOL, TypeRegistry.safeGet("xs:anyURI?"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?"),
                ElementTest.ANY_ELEMENT });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null && argv.size() == 2);
        Item firstItem = argv.getItem(0);
        final String prefix;
        if(firstItem.isEmpty()) {
            prefix = "";
        } else {
            prefix = ((XString) firstItem).getValue();
        }
        Item secondItem = argv.getItem(1);
        assert (!secondItem.isEmpty());
        XQNode node = (XQNode) secondItem;
        assert (node.nodeKind() == NodeKind.ELEMENT);
        String nsuri = resolveNamespaceUri(node, prefix);
        if(nsuri == null) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        return XString.valueOf(nsuri);
    }

    public static String resolveNamespaceUri(XQNode node, String prefix) {
        assert (node != null && prefix != null);
        if(node.nodeKind() != NodeKind.ELEMENT) {
            return null;
        }
        if(node instanceof DMElement) {
            final INodeSequence<DMAttribute> atts = ((DMElement) node).attribute();
            for(DMAttribute att : atts) {
                final String attPrefix = att.nodeName().getPrefix();
                if(prefix.equals(attPrefix)) {
                    final String nsuri = att.getNamespaceURI();
                    return nsuri;
                }
            }
        } else {
            assert (node instanceof DTMElement);
            DTMElement e = ((DTMElement) node);
            DTMAttribute att = null;
            for(int i = 0; (att = e.attribute(0)) != null; i++) {
                final String attPrefix = att.nodeName().getPrefix();
                if(prefix.equals(attPrefix)) {
                    final String nsuri = att.getNamespaceURI();
                    return nsuri;
                }
            }
        }
        return null;
    }

}
