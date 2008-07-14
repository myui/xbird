/*
 * @(#)$Id: Id.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.*;

import javax.xml.XMLConstants;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.instance.DocumentTableModel.DTMAttribute;
import xbird.xquery.dm.instance.DocumentTableModel.DTMElement;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.node.DMAttribute;
import xbird.xquery.dm.value.node.DMElement;
import xbird.xquery.dm.value.sequence.NodeSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.type.*;
import xbird.xquery.type.Type.Occurrence;
import xbird.xquery.type.node.ElementTest;
import xbird.xquery.type.node.NodeType;
import xbird.xquery.type.xs.IDREFType;
import org.w3c.dom.Document;

/**
 * fn:id.
 * <DIV lang="en">
 * Returns the sequence of element nodes that have an ID value matching the value
 *  of one or more of the IDREF values supplied in $arg.
 * <ul>
 * <li>fn:id($arg as xs:string*) as element()*</li>
 * <li>fn:id($arg as xs:string*, $node as node()) as element()*</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-id
 * @see Document#getElementById(String)
 */
public final class Id extends BuiltInFunction {
    private static final long serialVersionUID = 1701394637808364674L;
    public static final String SYMBOL = "fn:id";

    public Id() {
        super(SYMBOL, new SequenceType(ElementTest.ANY_ELEMENT, Occurrence.OCC_ZERO_OR_MORE));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        Type stra = TypeRegistry.safeGet("xs:string*");
        s[0] = new FunctionSignature(getName(), new Type[] { stra });
        s[1] = new FunctionSignature(getName(), new Type[] { stra, NodeType.ANYNODE });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null);
        final int arglen = argv.size();
        assert (arglen == 1 || arglen == 2);
        final XQNode node;
        if(arglen == 2) {
            Item second = argv.getItem(1);
            if(!(second instanceof XQNode)) {
                throw new DynamicError("err:XPTY0004", "Second argument is expected to be a node, but was "
                        + second.getType());
            }
            node = (XQNode) second;
        } else {
            Item contextItem = dynEnv.contextItem();
            if(contextItem == null) {
                throw new DynamicError("err:XPDY0002", "ContextItem is not set");
            } else if(!(contextItem instanceof XQNode)) {
                throw new DynamicError("err:XPTY0004", "Context item is expected to be a node, but was "
                        + contextItem.getType());
            }
            node = (XQNode) contextItem;
        }
        // An error is raised if node's root is not a document node
        final XQNode root = node.getRoot();
        if(root.nodeKind() != NodeKind.DOCUMENT) {
            throw new DynamicError("err:FODC0001", "root node was not document-node.");
        }
        final IFocus<Item> firstItemItor = argv.getItem(0).iterator();
        final NodeSequence<XQNode> nodes = new NodeSequence<XQNode>(dynEnv);
        for(Item it : firstItemItor) {
            assert (it instanceof XString);
            final String idrefs = it.stringValue();
            final StringTokenizer tok = new StringTokenizer(idrefs, " ");
            while(tok.hasMoreTokens()) {
                final String idref = tok.nextToken();
                if(!IDREFType.IDREF.isValid(idref)) {
                    // If any of the tokens is not a lexically valid IDREF, it is ignored. 
                    continue;
                }
                final List<XQNode> founds = getElementsById(root, idref, false);
                for(XQNode n : founds) {
                    nodes.addItem(n);
                }
            }
        }
        firstItemItor.closeQuietly();
        return nodes;
    }

    public static List<XQNode> getElementsById(XQNode document, String id, boolean byIDRef) {
        assert (document != null && id != null);
        final List<XQNode> nodes = new LinkedList<XQNode>();
        XQNode stopNode = document.following(true);
        long stopNID = (stopNode == null) ? -1 : stopNode.getPosition();
        // search in document order
        for(XQNode curNode = document.firstChild(); curNode != null; curNode = curNode.nextNode()) {
            long nid = curNode.getPosition();
            if(nid == stopNID) {
                break;
            }
            byte nodekind = curNode.nodeKind();
            if(nodekind == NodeKind.ELEMENT) {
                if(curNode instanceof DMElement) {
                    DMElement e = ((DMElement) curNode);
                    if(!byIDRef) {
                        if(e.isId()) {
                            // The is-id property of the element node is true, and the typed value 
                            // of the element node is equal to V under the rules of the eq operator 
                            // using the Unicode code point collation.
                            //String val = e.typedValue().stringValue();
                            String val = e.stringValue();
                            if(id.equals(val)) {
                                nodes.add(e);
                            }
                            continue;
                        }
                    } else {
                        if(e.isIdrefs()) {
                            //String val = e.typedValue().stringValue();
                            String val = e.stringValue();
                            if(id.equals(val)) {
                                nodes.add(e);
                            }
                            continue;
                        }
                    }
                    DMAttribute idatt = e.getAttribute(XMLConstants.XML_NS_URI, "id");
                    if(idatt != null && id.equals(idatt.getContent())) {
                        nodes.add(e);
                    }
                } else {
                    assert (curNode instanceof DTMElement);
                    // If no candidate ID value matches the IDREF value of any element or attribute, 
                    // the function returns the empty sequence.
                    return Collections.<XQNode> emptyList();
                }
            } else if(byIDRef && nodekind == NodeKind.ATTRIBUTE) {
                if(curNode instanceof DMAttribute) {
                    DMAttribute att = (DMAttribute) curNode;
                    if(att.isIdrefs()) {
                        String val = att.typedValue().stringValue();
                        if(id.equals(val)) {
                            nodes.add(att);
                        }
                        continue;
                    }
                } else {
                    assert (curNode instanceof DTMAttribute);
                    // If no candidate ID value matches the IDREF value of any element or attribute, 
                    // the function returns the empty sequence.
                    return Collections.<XQNode> emptyList();
                }
            }
        }
        return nodes;
    }

}
