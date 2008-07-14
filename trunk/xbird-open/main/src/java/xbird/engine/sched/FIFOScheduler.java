/*
 * @(#)$Id: FIFOScheduler.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.engine.sched;

import java.util.concurrent.BlockingQueue;

import xbird.engine.RequestContext;
import xbird.engine.ResponseListener;
import xbird.util.concurrent.collections.ConcurrentLinkedBlockingQueue;
import xbird.util.concurrent.jsr166.LinkedTransferQueue;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class FIFOScheduler extends AbstractQueuingScheduler {

    public static final String FIFO = "FIFO";

    public FIFOScheduler(ResponseListener resHandler) {
        super(getBlockingQueue(), resHandler);
    }

    public FIFOScheduler(ResponseListener resHandler, ScheduledEventListener... listeners) {
        super(getBlockingQueue(), resHandler, listeners);
    }

    private static BlockingQueue<RequestContext> getBlockingQueue() {
        if(Boolean.getBoolean("xbird.sched.use_clbq")) {
            return new ConcurrentLinkedBlockingQueue<RequestContext>();
        }
        return new LinkedTransferQueue<RequestContext>();
    }

}
