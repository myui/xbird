/*
 * @(#)$Id: NodeType.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.labeling.RevPathCoder;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.type.ItemType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class NodeType extends ItemType {
    private static final long serialVersionUID = -50684513621274113L;

    public static final NodeType ANYNODE = new NodeType(NodeKind.ANY);
    public static final NodeType TEXT = new NodeType(NodeKind.TEXT);
    public static final NodeType COMMENT = new NodeType(NodeKind.COMMENT);
    public static final NodeType ELEMENT = new NodeType(NodeKind.ELEMENT);
    public static final NodeType ATTRIBUTE = new NodeType(NodeKind.ATTRIBUTE);

    private final byte nodeKind;

    protected NodeType(byte nodeKind) {
        this.nodeKind = nodeKind;
    }

    public byte getNodeKind() {
        return nodeKind;
    }

    public QualifiedName getNodeName() {
        return null;
    }

    public Class getJavaObjectType() {
        return this.getClass();
    }

    public boolean acceptNodeType(NodeType expected) {
        final byte kind = expected.getNodeKind();
        return acccept(kind);
    }

    public boolean acccept(byte type) {
        if(this.nodeKind == NodeKind.ANY) {
            return true;
        } else {
            return this.nodeKind == type;
        }
    }

    @Override
    public String toString() {
        switch(nodeKind) {
            case NodeKind.ANY:
                return "node()";
            case NodeKind.TEXT:
                return "text()";
            case NodeKind.COMMENT:
                return "comment()";
            case NodeKind.ELEMENT:
            case NodeKind.ATTRIBUTE:
                return "*";
            default:
                throw new IllegalStateException("Illegal node kind was detected: "
                        + NodeKind.resolveName(nodeKind));
        }
    }

    public int[] toQuery(RewriteInfo info, boolean isAttrStep) {
        switch(nodeKind) {
            case NodeKind.ANY:
                return isAttrStep ? new int[] { RevPathCoder.ATTRIBUTE, RevPathCoder.PERCENT }
                        : new int[] { RevPathCoder.PERCENT };
            case NodeKind.TEXT:
                return null; //"&T";
            case NodeKind.COMMENT:
                return null; //"&C";
            case NodeKind.ELEMENT:
                return isAttrStep ? null : new int[] { RevPathCoder.PERCENT };
            case NodeKind.ATTRIBUTE:
                return isAttrStep ? new int[] { RevPathCoder.ATTRIBUTE, RevPathCoder.PERCENT }
                        : null;
            default:
                throw new IllegalStateException("Illegal node kind was detected: "
                        + NodeKind.resolveName(nodeKind));
        }
    }

    public static String getNodeKindLiteral(NodeType nt) {
        if(nt == null) {
            return "*";
        }
        return NodeKind.resolveName(nt.getNodeKind());
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof NodeType)) {
            return false;
        }
        return this.nodeKind == ((NodeType) obj).nodeKind;
    }

}