/*
 * @(#)$Id: RoundRobinScheduler.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.server.Request;
import xbird.server.backend.BackendProcessor;
import xbird.server.repository.IDocumentRepository;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class RoundRobinScheduler extends AbstractScheduler {

    public static final String RR = "roundrobin";

    private final int _numProcs;

    private int _nextIndex = 0;

    public RoundRobinScheduler(final BackendProcessor[] procs, final IDocumentRepository repository) {
        super(procs, repository);
        this._numProcs = procs.length;
    }

    @Override
    public synchronized BackendProcessor dispatchRequest(Request request) {
        if(_nextIndex >= _numProcs) {
            _nextIndex = 0; // round up
        }
        return _procs[_nextIndex++];
    }

    @Override
    public void notifyCompletion(Request request) {}

}
