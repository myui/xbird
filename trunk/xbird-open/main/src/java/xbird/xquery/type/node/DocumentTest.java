/*
 * @(#)$Id: DocumentTest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.type.node;

import javax.xml.xquery.XQException;

import xbird.xquery.dm.NodeKind;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.type.ItemType;

/**
 * document-node(E) matches any document node that contains
 * exactly one element node, optionally accompanied by one or more comment
 * and processing instruction nodes, if E is an ElementTest or SchemaElementTest
 * that matches the element node.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class DocumentTest extends NodeType {
    private static final long serialVersionUID = -8484678694422113208L;

    public static final DocumentTest ANY_DOCUMENT = new DocumentTest();

    private final ItemType nodeType;

    public DocumentTest() {
        this(null);
    }

    public DocumentTest(ItemType nodeType) {
        super(NodeKind.DOCUMENT);
        this.nodeType = nodeType;
    }

    public ItemType getNodeType() {
        return nodeType;
    }

    @Override
    public boolean acceptNodeType(NodeType expected) {
        if(!(expected instanceof DocumentTest)) {
            return false;
        }
        if(nodeType != null) {
            ItemType targetNodeType = ((DocumentTest) expected).getNodeType();
            if(!nodeType.accepts(targetNodeType)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "document-node(" + ((nodeType == null) ? "" : nodeType.toString()) + ")";
    }

    @Override
    public int[] toQuery(RewriteInfo info, boolean isAttrStep) {
        return null;
    }

    @Override
    public int getXQJBaseType() throws XQException {
        final ItemType basetype = nodeType;
        if(basetype == null) {
            throw new XQException("Illegal item kind: " + toString(), "err:XQJxxxx");
        }
        return basetype.getXQJBaseType();
    }
}
