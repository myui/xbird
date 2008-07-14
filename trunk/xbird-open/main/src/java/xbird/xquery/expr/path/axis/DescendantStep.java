/*
 * @(#)$Id: DescendantStep.java 3619 2008-03-26 07:23:03Z yui $
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
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DescendantStep extends DescendantOrSelfStep {
    private static final long serialVersionUID = 5699042026513133312L;

    public DescendantStep(NodeTest test) {
        super(DESC, test);
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

    @Override
    public Sequence<? extends Item> eval(final Sequence<? extends Item> contextSeq, final DynamicContext dynEnv)
            throws XQueryException {
        if(contextSeq == null) {
            throw new DynamicError("err:XPDY0002", "ContextItem is not set");
        }
        final INodeSequence<XQNode> src = ProxyNodeSequence.wrap(contextSeq, dynEnv);
        final IFocus<XQNode> srcitor = src.iterator();
        if(srcitor.hasNext()) {
            final XQNode n = srcitor.next();
            if(srcitor.hasNext()) {
                srcitor.closeQuietly();
                reportError("err:XPTY0020", "Context item is expected to be a node, but was node sequence.");
            }
            // FIXME DMNode
            srcitor.closeQuietly();
            return new DescendantOrSelfEmurationSequence(n, _nodeTest, dynEnv, true);
        }
        srcitor.closeQuietly();
        return NodeSequence.<XQNode> emptySequence();
    }

}
