/*
 * @(#)$Id: InScopePrefixes.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.ArrayList;
import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.node.ElementTest;

/**
 * fn:in-scope-prefixes($element as element()) as xs:string*.
 * <DIV lang="en">
 * Returns the prefixes of the in-scope namespaces for $element.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-in-scope-prefixes
 */
public final class InScopePrefixes extends BuiltInFunction {
    private static final long serialVersionUID = 4944394253347281633L;
    public static final String SYMBOL = "fn:in-scope-prefixes";

    public InScopePrefixes() {
        super(SYMBOL, TypeRegistry.safeGet("xs:string*"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { ElementTest.ANY_ELEMENT });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null && argv.size() == 1);
        Item firstItem = argv.getItem(0);
        XQNode element = (XQNode) firstItem;
        assert (element.nodeKind() == NodeKind.ELEMENT);
        List<XString> prefixes = new ArrayList<XString>(12);
        for(XQNode curNode = element; curNode != null; curNode = curNode.parent()) {
            String prefixStr = curNode.nodeName().getPrefix();
            if(prefixStr == null) {
                prefixStr = "";
            }
            XString prefix = XString.valueOf(prefixStr);
            if(!prefixes.contains(prefix)) {
                prefixes.add(prefix);
            }
        }
        if(prefixes.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        return new ValueSequence(prefixes, dynEnv);
    }

}
