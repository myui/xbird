/*
 * @(#)$Id: ProxyNodeSequence.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.dm.instance.XQueryDataModel;
import xbird.xquery.dm.value.*;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class ProxyNodeSequence<T extends XQNode> extends ProxySequence<T>
        implements INodeSequence<T> {
    private static final long serialVersionUID = -4038922105873847127L;

    protected ProxyNodeSequence(Sequence delegate, DynamicContext dynEnv) {
        super(delegate, dynEnv);
    }

    public boolean next(IFocus focus) throws XQueryException {
        final Iterator<? extends XQNode> delItor = focus.getBaseFocus();
        if(delItor.hasNext()) {
            Item delItem = delItor.next();
            final XQNode node;
            if(delItem instanceof XQNode) {
                node = (XQNode) delItem;
            } else { // TODO REVIEWME work around
                node = XQueryDataModel.createText(delItem.stringValue());
            }
            focus.setContextItem(node);
            return true;
        }
        focus.closeQuietly();
        return false;
    }

    public static final INodeSequence<XQNode> wrap(Sequence src, DynamicContext dynEnv) {
        return wrap(src, dynEnv, false);
    }

    public static final INodeSequence<XQNode> wrap(Sequence src, DynamicContext dynEnv, boolean forceDistinctSort) {
        assert (src != null);
        if(src instanceof INodeSequence) {
            return (INodeSequence) src;
        } else if(src instanceof DistinctDocOrderSequence) {
            return (DistinctDocOrderSequence) src;
        } else {
            if(forceDistinctSort) {
                if(!(src instanceof CompositeSequence) && !(src instanceof XQNode)) {
                    final DistinctDocOrderSequence wrapped = new DistinctDocOrderSequence(src, dynEnv, src.getType());
                    return wrapped;
                }
            }
            return new ProxyNodeSequence(src, dynEnv);
        }
    }

}
