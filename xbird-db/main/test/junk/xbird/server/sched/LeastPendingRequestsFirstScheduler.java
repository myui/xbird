/*
 * @(#)$Id: LeastPendingRequestsFirstScheduler.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.concurrent.locks.*;

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
public class LeastPendingRequestsFirstScheduler extends AbstractScheduler {

    public static final String LPRF = "lprf";

    private final Lock _readLock;

    private final Lock _writeLock;

    private final int _numProcs;

    private final int[] _pendingCounts;

    public LeastPendingRequestsFirstScheduler(BackendProcessor[] procs, IDocumentRepository repository) {
        super(procs, repository);
        this._numProcs = procs.length;
        this._pendingCounts = new int[procs.length];
        final ReadWriteLock rwl = new ReentrantReadWriteLock();
        this._readLock = rwl.readLock();
        this._writeLock = rwl.writeLock();
    }

    @Override
    public BackendProcessor dispatchRequest(Request request) {
        int leastIndex = 0, minCnt = 0;
        _readLock.lock();
        for(int i = 0; i < _numProcs; i++) {
            final int cnt = _pendingCounts[i];
            if(cnt < minCnt) {
                leastIndex = i;
                minCnt = cnt;
            }
        }
        final BackendProcessor qp = _procs[leastIndex];
        _readLock.unlock();
        _writeLock.lock();
        ++_pendingCounts[leastIndex];
        _writeLock.unlock();
        return qp;
    }

    @Override
    public void notifyCompletion(Request request) {
        final int qpid = request.getAttachedQueryProcessorId();
        _writeLock.lock();
        --_pendingCounts[qpid];
        _writeLock.unlock();
    }

}
