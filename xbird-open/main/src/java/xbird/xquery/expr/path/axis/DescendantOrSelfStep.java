/*
 * @(#)$Id: DescendantOrSelfStep.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.path.axis;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.*;
import xbird.xquery.expr.path.NodeTest;
import xbird.xquery.meta.*;
import xbird.xquery.optim.RewriteInfo;

/**
 * The descendant-or-self axis contains the context node and the descendants 
 * of the context node.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class DescendantOrSelfStep extends AxisStep {
    private static final long serialVersionUID = -1036349267969466494L;

    public DescendantOrSelfStep(NodeTest test) {
        this(DESC_OR_SELF, test);
    }

    public DescendantOrSelfStep(int kind, NodeTest test) {
        super(kind, test);
    }

    @Override
    public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info) {
        if(!info.hasPreviousStep()) {
            return false;
        }
        String nt = _nodeTest.toString();
        if(!"*".equals(nt) && !"node()".equals(nt)) {
            return false;
        }
        info.markDescendantOrSelf();
        return true;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        if(contextSeq == null) {
            throw new DynamicError("err:XPDY0002", "ContextItem is not set");
        }
        final INodeSequence<XQNode> src = ProxyNodeSequence.wrap(contextSeq, dynEnv);
        final IFocus<XQNode> srcItor = src.iterator();
        if(srcItor.hasNext()) {
            final XQNode n = srcItor.next();
            if(srcItor.hasNext()) {
                srcItor.closeQuietly();
                reportError("err:XPTY0020", "Context item is expected to be a node, but was node sequence.");
            }
            srcItor.closeQuietly();
            // FIXME DMNode
            return new DescendantOrSelfEmurationSequence(n, _nodeTest, dynEnv);
        }
        srcItor.closeQuietly();
        return NodeSequence.<XQNode> emptySequence();
    }

    /** 
     * Represents "/descendant-or-self::node()/"
     */
    public static final class RootDescStep extends DescendantOrSelfStep {
        private static final long serialVersionUID = -8533383126416496506L;

        public static final RootDescStep INSTANCE = new RootDescStep();

        public RootDescStep() {
            super(NodeTest.ANYNODE);
        }
    }

    protected static final class DescendantOrSelfEmurationSequence extends AxisEmurationSequence {
        private static final long serialVersionUID = 3516008834617795445L;

        private final NodeTest _filterNodeTest;
        private final XQNode _stopNode;
        private final long _stopNID;
        private final boolean _excludeSelf;

        public DescendantOrSelfEmurationSequence(XQNode baseNode, NodeTest nodeTest, DynamicContext dynEnv) {
            this(baseNode, nodeTest, dynEnv, false);
        }

        public DescendantOrSelfEmurationSequence(XQNode baseNode, NodeTest nodeTest, DynamicContext dynEnv, boolean excludeSelf) {
            super(baseNode, dynEnv);
            this._filterNodeTest = nodeTest;
            this._stopNode = baseNode.following(true);
            this._stopNID = (_stopNode == null) ? Long.MIN_VALUE : _stopNode.getPosition();
            this._excludeSelf = excludeSelf;
        }

        public boolean next(final IFocus<XQNode> focus) throws XQueryException {
            XQNode curNode = focus.getContextItem();
            for(int pos = focus.getContextPosition(); curNode != null; pos++) {
                if(pos == 0) {
                    if(_excludeSelf) {
                        curNode = curNode.firstChild();
                    }
                } else {
                    curNode = curNode.nextNode();
                    if(curNode == null) {
                        return false;
                    }
                    if(curNode == _stopNode) {// REVIEWME DMNode
                        return false;
                    }
                    final long nid = curNode.getPosition();
                    if(nid == _stopNID) {
                        return false;
                    }
                }
                if(_filterNodeTest.accepts(curNode)) {
                    focus.setContextItem(curNode);
                    return true;
                }
            }
            return false;
        }

    }

}
