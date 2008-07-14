/*
 * @(#)$Id: MergeAdjacentTextSequence.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value.sequence;

import java.util.Iterator;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.node.DMText;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;

/**
 * Adjacent text nodes in the content sequence are merged into a single text node 
 by concatenating their contents, with no intervening blanks.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class MergeAdjacentTextSequence extends ProxySequence<Item> {
    private static final long serialVersionUID = -8309716270458744214L;

    public MergeAdjacentTextSequence(Sequence delegate, DynamicContext dynEnv) {
        super(delegate, dynEnv);
    }

    public boolean next(IFocus focus) throws XQueryException {
        Item pending = focus.pollItem();
        if(pending != null) {
            focus.setContextItem(pending);
            return true;
        }
        Iterator<? extends Item> delItor = focus.getBaseFocus();
        if(!delItor.hasNext()) {
            return false;
        }
        final StringBuilder buf = new StringBuilder(256);
        do {
            Item it = delItor.next();
            if(it instanceof XQNode) {
                XQNode n = (XQNode) it;
                if(n.nodeKind() == NodeKind.TEXT) {
                    final String sv = n.stringValue();
                    assert (sv != null);
                    if(sv.length() > 0) {
                        buf.append(sv);
                    }
                    continue;
                }
            } else if(it instanceof XString) {
                buf.append(it.stringValue());
                continue;
            }
            focus.offerItem(it);
            break;
        } while(delItor.hasNext());
        if(buf.length() > 0) {
            final String c = buf.toString();
            focus.setContextItem(new DMText(c));
        } else {
            pending = focus.pollItem();
            if(pending == null) {
                return false; // may comes here.
            }
            focus.setContextItem(pending);
            assert (focus.pollItem() == null) : "should not have more than one polling item!";
        }
        return true;
    }

}
