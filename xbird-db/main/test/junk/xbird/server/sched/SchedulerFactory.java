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
package xbird.server.sched;

import xbird.config.Settings;
import xbird.server.backend.BackendProcessor;
import xbird.server.repository.IDocumentRepository;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SchedulerFactory {

    private static final String DEFAULT_SCHED_POLICY = RoundRobinScheduler.RR;

    private static final String SCHED_POLICY = Settings.get("xbird.sched.policy", DEFAULT_SCHED_POLICY);

    private SchedulerFactory() {}

    public static AbstractScheduler createScheduler(final BackendProcessor[] procs, final IDocumentRepository repository) {
        final AbstractScheduler sched;
        if (RoundRobinScheduler.RR.equals(SCHED_POLICY)) {
            sched = new RoundRobinScheduler(procs, repository);
        } else if (LeastPendingRequestsFirstScheduler.LPRF.equals(SCHED_POLICY)) {
            sched = new LeastPendingRequestsFirstScheduler(procs, repository);
        } else {
            throw new IllegalStateException("Unsupported scheduring policy: " + SCHED_POLICY);
        }
        return sched;
    }

}
