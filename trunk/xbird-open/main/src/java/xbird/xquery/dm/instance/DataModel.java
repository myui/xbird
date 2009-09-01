/*
 * @(#)$Id: DataModel.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.instance;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.XQNode;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class DataModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Universally managed document Id. */
    protected static final AtomicInteger _docidCounter = new AtomicInteger(-1);

    protected int _volume = 0;

    public abstract void export(XQNode node, XQEventReceiver receiver) throws XQueryException;

    public boolean isMemoryMappedStore() {
        return false;
    }

    public final int nextDocumentId() {
        return _docidCounter.incrementAndGet();
    }

}
