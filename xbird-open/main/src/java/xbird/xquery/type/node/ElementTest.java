/*
 * @(#)$Id: ElementTest.java 3619 2008-03-26 07:23:03Z yui $
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
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#id-element-test
 * @link http://www.w3.org/TR/xquery/#doc-xquery-ConstructionDecl
 */
public class ElementTest extends NodeType {
    private static final long serialVersionUID = -5133518640855261334L;

    public static final ElementTest ANY_ELEMENT = new ElementTest();

    private final QualifiedName elementName;
    private QualifiedName typeName;
    private Type type = null;
    private final boolean isNillable;

    private ElementTest() {
        this(null, null, false);
    }

    public ElementTest(QualifiedName elementName) {
        this(elementName, null);
    }

    public ElementTest(QualifiedName elementName, QualifiedName typeName) {
        this(elementName, typeName, false);
    }

    public ElementTest(QualifiedName elementName, QualifiedName typeName, boolean isNillable) {
        super(NodeKind.ELEMENT);
        if(typeName != null) {
            final Type t = TypeRegistry.safeGet(typeName);
            if(t == null) {
                throw new XQRTException("err:XPST0001", "type '" + typeName + "' not resolved");
            }
            this.type = t;
        }
        this.elementName = elementName;
        this.typeName = typeName;
        this.isNillable = isNillable;
    }

    public QualifiedName getElementName() {
        return elementName;
    }

    public boolean isNillable() {
        return isNillable;
    }

    public QualifiedName getTypeName() {
        return typeName;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean acceptNodeType(NodeType expected) {
        if(!(expected instanceof ElementTest)) {
            return false;
        }
        ElementTest target = (ElementTest) expected;
        if(elementName != null) {
            if(!elementName.equals(target.getElementName())) {
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
        final StringBuilder buf = new StringBuilder(64);
        buf.append("element(");
        if(elementName == null) {
            buf.append("*");
        } else {
            buf.append(elementName);
        }
        if(typeName != null) {
            buf.append(",");
            buf.append(typeName);
            if(isNillable) {
                buf.append("?");
            }
        }
        buf.append(")");
        return buf.toString();
    }

    @Override
    public int[] toQuery(RewriteInfo info, boolean isAttrStep) {
        if(typeName != null && !isNillable) {
            return null;
        }
        if(elementName == null) {
            return new int[] { RevPathCoder.PERCENT };
        } else {
            String prefix = elementName.getPrefix();
            if(ANY.equals(prefix)) {
                return null;
            }
            String localName = elementName.getLocalPart();
            if(ANY.equals(localName)) {
                return null;
            }
            int qid = info.identifyQName(elementName);
            if(qid == -1) {
                return null;
            }
            return new int[] { RevPathCoder.IDENTIFIER_OFFSET + qid };
        }
    }

    @Override
    public QualifiedName getNodeName() {
        return elementName;
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
