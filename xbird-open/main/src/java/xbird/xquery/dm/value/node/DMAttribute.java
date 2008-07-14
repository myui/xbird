/*
 * @(#)$Id: DMAttribute.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value.node;

import javax.xml.XMLConstants;

import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.xs.IDType;

/**
 * Attribute Nodes represent XML attributes.
 * <DIV lang="en">
 * Attributes have the following properties:
 * <ul>
 *  <li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xpath-datamodel/#AttributeNode
 */
public class DMAttribute extends DMNode {
    private static final long serialVersionUID = -1913771934851376036L;
    
    protected final QualifiedName _name;

    public DMAttribute() {
        super();
        this._name = null;
    }
    
    public DMAttribute(QualifiedName name, String content) {
        this(-1L, name, content);
    }

    protected DMAttribute(long id, QualifiedName name, String content) {
        super(id);
        this._name = name;
        if(QNameUtil.isSame(name, XMLConstants.XML_NS_URI, "id")) {
            final String idval = IDType.ID.processWhitespace(content);
            setContent(idval);
        } else {
            setContent(content);
        }
    }

    //--------------------------------------------
    // DM interfaces

    public byte nodeKind() {
        return NodeKind.ATTRIBUTE;
    }

    @Override
    public QualifiedName nodeName() {
        return _name;
    }

    @Override
    public String getNamespaceURI() {
        return _name.getNamespaceURI();
    }

    @Override
    public AtomicValue typedValue() {
        if(QNameUtil.isSame(_name, XMLConstants.XML_NS_URI, "id")) {
            return new XString(stringValue(), IDType.ID);
        }
        return super.typedValue();
    }

}
