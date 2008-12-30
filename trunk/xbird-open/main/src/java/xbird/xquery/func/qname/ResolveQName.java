/*
 * @(#)$Id: ResolveQName.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.util.xml.XMLUtils;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.XQNode;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.QNameValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.node.ElementTest;

/**
 * fn:resolve-QName($qname as xs:string?, $element as element()) as xs:QName?.
 * <DIV lang="en">
 * Returns an xs:QName value by taking an xs:string that has the lexical form of 
 * an xs:QName and resolving it using the in-scope namespaces for a given element.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-resolve-QName
 */
public final class ResolveQName extends BuiltInFunction {
    private static final long serialVersionUID = -9054654027397389917L;
    public static final String SYMBOL = "fn:resolve-QName";

    public ResolveQName() {
        super(SYMBOL, TypeRegistry.safeGet("xs:QName?"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?"),
                ElementTest.ANY_ELEMENT });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        assert (argv != null && argv.size() == 2);
        // If $qname is the empty sequence, returns the empty sequence.
        Item firstItem = argv.getItem(0);
        if(firstItem.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        XString qname = (XString) firstItem;
        String qnameStr = qname.getValue();
        final int pos = qnameStr.indexOf(':');
        final String nsuri;
        if(pos == -1) {
            // If the $qname has no prefix, and there is no namespace binding for $element 
            // corresponding to the default (unnamed) namespace, then the resulting expanded-QName 
            // has no namespace part.
            nsuri = XMLUtils.NULL_NS_URI;
        } else {
            final String prefix = qnameStr.substring(0, pos - 1);
            Item secondItem = argv.getItem(1);
            XQNode element = (XQNode) secondItem;
            nsuri = NamespaceUriForPrefix.resolveNamespaceUri(element, prefix);
            if(nsuri == null) {
                // If the $qname has a prefix and if there is no namespace binding for $element
                // that matches this prefix, then an error is raised [err:FONS0004].
                throw new DynamicError("err:FONS0004", "Namespace for the prefix `" + qnameStr
                        + "` not found");
            }
        }
        final QualifiedName resolved;
        try {
            resolved = QNameUtil.parse(qnameStr, nsuri);
        } catch (IllegalArgumentException e) {
            // If $qname does not have the correct lexical form for xs:QName an error 
            // is raised [err:FOCA0002].
            throw new DynamicError("err:FOCA0002", "Illegal qname form: " + qnameStr);
        }
        return new QNameValue(resolved);
    }

}
