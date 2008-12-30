/*
 * @(#)$Id: DMNamespace.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.util.xml.XMLUtils;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xpath-datamodel/#NamespaceNode
 */
public class DMNamespace extends DMAttribute {
    private static final long serialVersionUID = 6814727486676491909L;

    public DMNamespace() {//for serialization
        super();
    }

    public DMNamespace(QualifiedName name, String content) {
        super(name, content);
    }

    protected DMNamespace(int id, QualifiedName name, String content) {
        super(id, name, content);
    }

    //--------------------------------------------
    // DM interfaces

    @Override
    public byte nodeKind() {
        return NodeKind.NAMESPACE;
    }

    /**
     * If the prefix is not empty, returns an xs:QName with the value of the prefix property 
     * in the local-name and an empty namespace name, otherwise returns the empty sequence.
     */
    @Override
    public QualifiedName nodeName() {
        final String prefix = _name.getPrefix();
        if(prefix == null || prefix.length() == 0) {
            return QNameTable.instantiate(XMLUtils.NULL_NS_URI, prefix);
        }
        return _name;
    }

    @Deprecated
    public String getPrefix() {
        return _name.getPrefix();
    }

}
