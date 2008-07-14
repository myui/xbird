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
package xbird.engine.sched;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import xbird.engine.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @deprecated
 */
public class ManagedFIFOScheduler extends Scheduler {
    public static final String M_FIFO = "M_FIFO";
    
    private final Queue<RequestContext> _tasks;

    public ManagedFIFOScheduler(ResponseListener resHandler) {
        super(resHandler);
        this._tasks = new ConcurrentLinkedQueue<RequestContext>();
    }

    public ManagedFIFOScheduler(ResponseListener resHandler, ScheduledEventListener... listeners) {
        super(resHandler, listeners);
        this._tasks = new ConcurrentLinkedQueue<RequestContext>();
    }

    @Override
    public void addTask(RequestContext rctxt) {
        _tasks.offer(rctxt);
        togglePause(false); // starts scheduling
    }

    @Override
    public void removeTask(Request request) {
        _tasks.remove(request);
    }

    @Override
    protected void processTask() {
        final Queue<RequestContext> task = _tasks;
        RequestContext rc = task.poll();
        while(rc != null) {
            invokeFire(rc);
            rc = task.poll();
        }
    }

}
