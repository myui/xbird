/*
 * @(#)$Id$
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
package xbird.util.concurrent;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

import javax.annotation.concurrent.ThreadSafe;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
@ThreadSafe
public final class BoundedWorkQueueExecutor implements Executor, Closeable {

    private final ExecutorService executor;
    private final Semaphore semaphore;

    public BoundedWorkQueueExecutor(ExecutorService exec, int bound) {
        this.executor = exec;
        this.semaphore = new Semaphore(bound);
    }

    public BoundedWorkQueueExecutor(int threadPoolSize, int workQueueSize, String threadName) {
        this.executor = ExecutorFactory.newFixedThreadPool(threadPoolSize, threadName);
        int bound = workQueueSize + threadPoolSize;
        this.semaphore = new Semaphore(bound);
    }

    public void execute(Runnable command) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RejectedExecutionException(e);
        }
        try {
            executor.execute(command);
        } catch (RejectedExecutionException ree) {
            throw ree;
        } finally {
            semaphore.release();
        }
    }

    public void close() throws IOException {
        executor.shutdown();
    }

}
