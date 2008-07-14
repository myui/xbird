/*
 * @(#)$Id: SchedulerFactory.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.config.Settings;
import xbird.engine.ResponseListener;
import xbird.engine.backend.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SchedulerFactory {

    private static final String DEFAULT_SCHED_POLICY = FIFOScheduler.FIFO;
    private static final String SCHED_POLICY = Settings.get("xbird.sched.policy", DEFAULT_SCHED_POLICY);

    private SchedulerFactory() {}

    public static Scheduler createScheduler(ResponseListener resHandler) {
        // create a scheduler
        final ScheduledEventListener[] listeners = createListeners(resHandler);
        final Scheduler sched;
        if(FIFOScheduler.FIFO.equals(SCHED_POLICY)) {
            sched = new FIFOScheduler(resHandler, listeners);
        } else if(PriorityScheduler.PRIORITY.equals(SCHED_POLICY)) {
            sched = new PriorityScheduler(resHandler, listeners);
        } else if(ManagedFIFOScheduler.M_FIFO.equals(SCHED_POLICY)) {
            sched = new ManagedFIFOScheduler(resHandler, listeners);
        } else {
            throw new IllegalStateException("Unsupported scheduring policy: " + SCHED_POLICY);
        }
        // TODO configure from file (listeners/interval/threadpool)
        // run the scheduler in a separeted thread
        sched.standby();
        return sched;
    }

    private static ScheduledEventListener[] createListeners(ResponseListener resHandler) {
        final ScheduledEventListener[] listeners = new ScheduledEventListener[4];
        listeners[0] = new QueryProcessor(resHandler);
        listeners[1] = new DistributedCompiler(resHandler);
        listeners[2] = new PreparedQueryProcessor(resHandler);
        listeners[3] = new CommandProcessor(resHandler);
        return listeners;
    }

}
