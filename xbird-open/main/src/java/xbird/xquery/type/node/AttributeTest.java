/*
 * @(#)$Id: AttributeTest.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.XQRTException;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.labeling.RevPathCoder;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;

/**
 * An AttributeTest is used to match an attribute node
 * by its name and/or type. 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class AttributeTest extends NodeType {
    private static final long serialVersionUID = -8679140526998566249L;

    public static final AttributeTest ANY_ATTRIBUTE = new AttributeTest();

    private final QualifiedName attributeName;
    private QualifiedName typeName;
    private Type type = null;

    protected AttributeTest() {
        this(null, null);
    }

    public AttributeTest(QualifiedName attributeName) {
        this(attributeName, null);
    }

    public AttributeTest(QualifiedName attributeName, QualifiedName typeName) {
        this(NodeKind.ATTRIBUTE, attributeName, typeName);
    }

    public AttributeTest(byte nodeKind, QualifiedName attributeName, QualifiedName typeName) {
        super(nodeKind);
        if(typeName != null) {
            final Type t = TypeRegistry.safeGet(typeName);
            if(t == null) {
                throw new XQRTException("err:XPST0001", "type '" + typeName + "' not resolved");
            }
            this.type = t;
        }
        this.attributeName = attributeName;
        this.typeName = typeName;
    }

    public QualifiedName getAttributeName() {
        return attributeName;
    }

    public QualifiedName getTypeName() {
        return typeName;
    }

    @Override
    public boolean acceptNodeType(NodeType expected) {
        if(!(expected instanceof AttributeTest)) {
            return false;
        }
        AttributeTest target = (AttributeTest) expected;
        final byte thisNodeKind = getNodeKind();
        if(thisNodeKind != target.getNodeKind()) {
            return false;
        }
        if(attributeName != null) {
            if(!attributeName.equals(target.getAttributeName())) {
                return false;
            }
        }
        if(typeName != null) {
            if(!typeName.equals(target.getTypeName())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        final String attname = (attributeName == null) ? "*" : attributeName.toString();
        if(typeName == null) {
            return "attribute(" + attname + ")";
        } else {
            return "attribute(" + attname + "," + typeName + ")";
        }
    }

    @Override
    public int[] toQuery(RewriteInfo info, boolean isAttrStep) {
        if(typeName != null) {
            return null;
        }
        if(attributeName == null) {
            return new int[] { RevPathCoder.PERCENT, RevPathCoder.ATTRIBUTE };
        } else {
            String prefix = attributeName.getPrefix();
            if(ANY.equals(prefix)) {
                return null;
            }
            String localName = attributeName.getLocalPart();
            if(ANY.equals(localName)) {
                return null;
            }
            int qid = info.identifyQName(attributeName);
            if(qid == -1) {
                return null;
            }
            return new int[] { RevPathCoder.IDENTIFIER_OFFSET + qid, RevPathCoder.ATTRIBUTE };
        }
    }

    @Override
    public QualifiedName getNodeName() {
        return attributeName;
    }

    @Override
    public int getXQJBaseType() throws XQException {
        final Type basetype = type;
        if(basetype == null) {
            throw new XQException("Illegal item kind: " + toString(), "err:XQJxxxx");
        }
        return basetype.getXQJBaseType();
    }

}
