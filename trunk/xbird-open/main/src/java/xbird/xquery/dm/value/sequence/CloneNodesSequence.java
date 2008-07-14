/*
 * @(#)$Id: CloneNodesSequence.java 3619 2008-03-26 07:23:03Z yui $
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
public final class CloneNodesSequence extends ProxySequence<Item> {
    private static final long serialVersionUID = 4590559741607945245L;

    public CloneNodesSequence(Sequence delegate, DynamicContext dynEnv) {
        super(delegate, dynEnv);
    }

    public boolean next(IFocus focus) throws XQueryException {
        Iterator<? extends Item> delItor = focus.getBaseFocus();
        if(delItor.hasNext()) {
            Item delItem = delItor.next();
            if(delItem instanceof XQNode) {
                delItem = ((XQNode) delItem).clone();
            }
            focus.setContextItem(delItem);
            return true;
        }
        return false;
    }

}
