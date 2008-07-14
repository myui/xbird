/*
 * @(#)$Id: SelfStep.java 3619 2008-03-26 07:23:03Z yui $
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
 * The self axis contains just the context node itself.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SelfStep extends AxisStep {
    private static final long serialVersionUID = -5466293225938504391L;

    public SelfStep(NodeTest test) {
        super(SELF, test);
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
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
            if(_nodeTest.accepts(n)) {
                srcitor.closeQuietly();
                return n;
            }
        }
        srcitor.closeQuietly();
        return NodeSequence.<XQNode> emptySequence();
    }

}
