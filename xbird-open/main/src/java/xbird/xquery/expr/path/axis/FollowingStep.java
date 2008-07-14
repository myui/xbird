/*
 * @(#)$Id: FollowingStep.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class FollowingStep extends AxisStep {
    private static final long serialVersionUID = -7992676436526853577L;

    public FollowingStep(NodeTest test) {
        super(FOLLOWING, test);
    }
    
    @Override
    public final boolean isNonDownwardAxis() {
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
            return new FollowingEmurationSequence(n, _nodeTest, dynEnv);
        }
        srcItor.closeQuietly();
        return NodeSequence.<XQNode> emptySequence();
    }

    private static final class FollowingEmurationSequence extends AxisEmurationSequence {
        private static final long serialVersionUID = 8895934190903807062L;

        private final NodeTest filterNodeTest;

        public FollowingEmurationSequence(XQNode baseNode, NodeTest nodeTest, DynamicContext dynEnv) {
            super(baseNode, dynEnv);
            this.filterNodeTest = nodeTest;
        }

        public boolean next(IFocus<XQNode> focus) throws XQueryException {
            XQNode curNode = focus.getContextItem();
            for(int i = 0; curNode != null; i++) {
                final int cpos = focus.getContextPosition();
                curNode = curNode.following(i == 0 && cpos == 0); // REVIEWME
                if(filterNodeTest.accepts(curNode)) {
                    focus.setContextItem(curNode);
                    return true;
                }
            }
            return false;
        }
    }
}
