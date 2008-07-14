/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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
package xbird.engine.remote;

import java.util.concurrent.Semaphore;

import xbird.engine.request.QueryRequest;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ThrottedRemoteSequenceProxy extends RunnableRemoteSequenceProxy {
    private static final long serialVersionUID = 1525047704177311709L;

    private final Semaphore _throttle;

    public ThrottedRemoteSequenceProxy(Semaphore throttle, Sequence<Item> delegate, QueryRequest request) {
        super(delegate, request);
        this._throttle = throttle;
    }

    @Override
    public void run() {
        final Semaphore throttle = _throttle;
        throttle.acquireUninterruptibly();
        try {
            compute();
        } finally {
            throttle.release();
        }
    }

}
