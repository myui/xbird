/*
 * @(#)$Id: AttributeStep.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.instance.DocumentTableModel.*;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.node.DMElement;
import xbird.xquery.dm.value.sequence.*;
import xbird.xquery.expr.path.NodeTest;
import xbird.xquery.meta.*;
import xbird.xquery.optim.RewriteInfo;

/**
 * The attribute axis contains the attributes of the context node, 
 * which are the nodes returned by the dm:attributes accessor.
 * <DIV lang="en">
 * This axis will be empty unless the context node is an element.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#axes
 */
public final class AttributeStep extends AxisStep {
    private static final long serialVersionUID = 2367320129192881124L;

    public AttributeStep(NodeTest test) {
        super(ATTR, test);
    }

    @Override
    public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info) {
        if(!info.hasPreviousStep()) {
            return false;
        }
        final int[] frag = _nodeTest.toQuery(info, true);
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
            if(n.nodeKind() == NodeKind.ELEMENT) {
                if(n instanceof DMElement) {
                    return ((DMElement) n).attribute(_nodeTest, dynEnv);
                } else {
                    return new AttributeEmurationSequence((DTMElement) n, _nodeTest, dynEnv);
                }
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
            if(n.nodeKind() == NodeKind.ELEMENT) {
                srcItor.closeQuietly();
                return new AttributeEmurationSequence((DTMElement) n, _nodeTest, dynEnv);
            }
        }
        srcItor.closeQuietly();
        return NodeSequence.<XQNode> emptySequence();
    }

    public static class AttributeEmurationSequence extends AxisEmurationSequence {
        private static final long serialVersionUID = 9146959047684939044L;

        protected final DTMElement elem;
        protected final NodeTest filterNodeTest;

        public AttributeEmurationSequence(DTMElement elem, NodeTest nodeTest, DynamicContext dynEnv) {
            super(elem, dynEnv);
            this.elem = elem;
            this.filterNodeTest = nodeTest;
        }

        public boolean next(IFocus focus) throws XQueryException {
            final int attsize = elem.getAttributesCount();
            for(int i = focus.getPosition(); i < attsize; i = focus.incrPosition()) {
                DTMAttribute att = elem.attribute(i);
                if(att != null && filterNodeTest.accepts(att)) {
                    focus.setContextItem(att);
                    focus.incrPosition();
                    return true;
                }
            }
            return false;
        }

        @Override
        public Focus<XQNode> iterator() {
            return new Focus<XQNode>(this, _dynEnv);
        }
    }

    public static final class NamespaceEmurationSequence extends AttributeEmurationSequence {
        private static final long serialVersionUID = -2911328865696037636L;

        public NamespaceEmurationSequence(DTMElement elem, NodeTest nodeTest, DynamicContext dynEnv) {
            super(elem, nodeTest, dynEnv);
        }

        @Override
        public boolean next(IFocus focus) throws XQueryException {
            final int nslen = elem.getNamespaceDeclCount();
            for(int i = focus.getPosition(); i < nslen; i = focus.incrPosition()) {
                DTMNamespace ns = elem.getNamespace(i);
                if(ns != null && filterNodeTest.accepts(ns)) {
                    focus.setContextItem(ns);
                    focus.incrPosition();
                    return true;
                }
            }
            return false;
        }
    }

}
