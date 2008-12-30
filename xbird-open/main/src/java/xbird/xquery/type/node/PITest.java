/*
 * @(#)$Id: PITest.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.util.xml.XMLUtils;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.optim.RewriteInfo;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class PITest extends NodeType {
    private static final long serialVersionUID = 2985894457251333801L;

    public static final PITest ANY_PI = new PITest();

    private final String name;

    public PITest() {
        this(null);
    }

    public PITest(String name) {
        super(NodeKind.PROCESSING_INSTRUCTION);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean acceptNodeType(NodeType expected) {
        if(!(expected instanceof PITest)) {
            return false;
        }
        if(name != null) {
            String targetName = ((PITest) expected).getName();
            if(!name.equals(targetName)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "processing-instruction(\"" + name + "\")";
    }

    @Override
    public int[] toQuery(RewriteInfo info, boolean isAttrStep) {
        return null;
    }

    @Override
    public QualifiedName getNodeName() {
        if(name == null) {
            return null;
        }
        return QNameTable.instantiate(XMLUtils.NULL_NS_URI, name);
    }

}
