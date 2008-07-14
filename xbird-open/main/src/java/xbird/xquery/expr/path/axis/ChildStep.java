/*
 * @(#)$Id: ChildStep.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.*;
import xbird.xquery.expr.path.NodeTest;
import xbird.xquery.meta.*;
import xbird.xquery.optim.RewriteInfo;

/**
 * The child axis contains the children of the context node, 
 * which are the nodes returned by the dm:children accessor.
 * <DIV lang="en">
 * Only document nodes and element nodes have children.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ChildStep extends AxisStep {
    private static final long serialVersionUID = -6956840988990166435L;

    public ChildStep(NodeTest test) {
        super(CHILD, test);
    }

    @Override
    public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info) {
        if(!info.hasPreviousStep()) {
            return false;
        }
        final int[] frag = _nodeTest.toQuery(info, false);
        if(frag == null) {
            return false;
        }
        info.trackStep(this, frag);
        return true;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        if(contextSeq == null) {
            throw new DynamicError("err:XPDY0002", "ContextItem is not set");
        }
        if(contextSeq instanceof XQNode) {
            final XQNode n = (XQNode) contextSeq;
            final byte kind = n.nodeKind();
            if(kind == NodeKind.ELEMENT || kind == NodeKind.DOCUMENT) {
                return new ChildEmurationSequence(n, _nodeTest, dynEnv);
            } else {
                return NodeSequence.<XQNode> emptySequence();
            }
        }
        final INodeSequence<XQNode> src = ProxyNodeSequence.wrap(contextSeq, dynEnv);
        final IFocus<XQNode> srcItor = src.iterator();
        if(srcItor.hasNext()) {
            final XQNode n = srcItor.next();
            if(srcItor.hasNext()) {
                srcItor.closeQuietly();
                reportError("err:XPTY0020", "Context item is expected to be a node, but was node sequence.");
            }
            final byte kind = n.nodeKind();
            if(kind == NodeKind.ELEMENT || kind == NodeKind.DOCUMENT) {
                srcItor.closeQuietly();
                return new ChildEmurationSequence(n, _nodeTest, dynEnv);
            }
        }
        srcItor.closeQuietly();
        return NodeSequence.<XQNode> emptySequence();
    }

    public static final class ChildEmurationSequence extends AxisEmurationSequence {
        private static final long serialVersionUID = 8810265374273451660L;

        private final NodeTest filterNodeTest;

        public ChildEmurationSequence(XQNode element, NodeTest nodeTest, DynamicContext dynEnv) {
            super(element.firstChild(), dynEnv);
            this.filterNodeTest = nodeTest;
        }

        public boolean next(IFocus<XQNode> focus) throws XQueryException {
            XQNode curNode = focus.getContextItem();
            if(curNode == null) {
                return false;
            }
            int curpos = focus.getContextPosition();
            for(int pos = curpos; curNode != null; pos++) {
                if(pos != 0) {
                    curNode = curNode.nextSibling(filterNodeTest);
                    if(curNode != null) {
                        focus.setContextItem(curNode);
                        return true;
                    }
                } else if(filterNodeTest.accepts(curNode)) {
                    focus.setContextItem(curNode);
                    return true;
                }
            }
            return false;
        }

    }
}
