/*
 * @(#)$Id: Lang.java 3619 2008-03-26 07:23:03Z yui $
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

import javax.xml.XMLConstants;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.instance.DocumentTableModel.DTMAttribute;
import xbird.xquery.dm.instance.DocumentTableModel.DTMElement;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.node.*;
import xbird.xquery.dm.value.sequence.INodeSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.node.NodeType;
import xbird.xquery.type.xs.BooleanType;

/**
 * fn:lang.
 * <DIV lang="en">
 * This function tests whether the language of $node, or the context node 
 * if the second argument is omitted, as specified by xml:lang attributes is the same as, 
 * or is a sublanguage of, the language specified by $testlang.
 * <ul>
 * <li>fn:lang($testlang as xs:string?) as xs:boolean</li>
 * <li>fn:lang($testlang as xs:string?, $node as node()) as xs:boolean</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-lang
 */
public final class Lang extends BuiltInFunction {
    private static final long serialVersionUID = 2652387458576217782L;
    public static final String SYMBOL = "fn:lang";

    private static final QualifiedName LANG_ELEM = QNameUtil.parse("xml:lang", XMLConstants.XML_NS_URI);

    public Lang() {
        super(SYMBOL, BooleanType.BOOLEAN);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        Type strq = TypeRegistry.safeGet("xs:string?");
        s[0] = new FunctionSignature(getName(), new Type[] { strq });
        s[1] = new FunctionSignature(getName(), new Type[] { strq, NodeType.ANYNODE });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null);
        final int arglen = argv.size();
        assert (arglen == 1 || arglen == 2);
        final XQNode node;
        if (arglen == 1) {
            Item contextItem = dynEnv.contextItem();
            if (contextItem == null) {
                throw new DynamicError("err:XPDY0002", "ContextItem is not set");
            }
            if (!(contextItem instanceof XQNode)) {
                throw new DynamicError("err:XPTY0004", "Context item is expected to node, but was "
                        + contextItem.getType());
            }
            node = (XQNode) contextItem;
        } else {
            Item secondItem = argv.getItem(1);
            node = (XQNode) secondItem;
        }
        Item firstItem = argv.getItem(0);
        final String testlang;
        if (firstItem.isEmpty()) {
            // If $testlang is the empty sequence it is interpreted as the zero-length string.
            testlang = "";
        } else {
            testlang = ((XString) firstItem).getValue();
        }
        final boolean islang = isLang(node, testlang);
        return islang ? BooleanValue.TRUE : BooleanValue.FALSE;
    }

    private static boolean isLang(XQNode node, String lang) {
        if (node.nodeKind() != NodeKind.ELEMENT) {
            return false;
        }
        if (node instanceof DMNode) {
            return isLang((DMElement) node, lang);
        } else {
            assert (node instanceof DTMElement);
            return isLang((DTMElement) node, lang);
        }
    }

    private static boolean isLang(DTMElement elem, String lang) {
        assert (lang != null);
        for (DTMElement curNode = elem; curNode != null; curNode = curNode.parent()) {
            DTMAttribute att = null;
            for (int i = 0; (att = curNode.attribute(i)) != null; i++) {
                if (LANG_ELEM.equals(att.nodeName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isLang(DMElement elem, String lang) {
        assert (lang != null);
        for (DMNode curNode = elem; curNode != null; curNode = curNode.parent()) {
            INodeSequence<DMAttribute> atts = curNode.attribute();
            for (DMAttribute att : atts) {
                if (LANG_ELEM.equals(att.nodeName())) {
                    String attval = att.getContent();
                    final int hyphenPos = (attval == null) ? -1 : attval.indexOf('-');
                    if (hyphenPos == -1) {
                        if (lang.equalsIgnoreCase(attval)) {
                            return true;
                        }
                    } else {
                        final String doclang = attval.substring(0, hyphenPos - 1);
                        if (lang.equalsIgnoreCase(doclang)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
