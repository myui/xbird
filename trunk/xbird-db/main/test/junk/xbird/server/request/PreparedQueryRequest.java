/*
 * @(#)$Id: PreparedQueryRequest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.server.request;

import xbird.server.Request;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.meta.DynamicContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class PreparedQueryRequest extends Request {

    public static final int SIGNATURE = 2;

    protected final Sequence<? extends Item> _contextSeq;
    protected final DynamicContext _dynEnv;

    public PreparedQueryRequest(final Sequence<? extends Item> contextSeq, final DynamicContext dynEnv) {
        super();
        this._contextSeq = contextSeq;
        this._dynEnv = dynEnv;
    }

    public int getSignature() {
        return SIGNATURE;
    }
    
    public abstract Object execute() throws XQueryException;

}
