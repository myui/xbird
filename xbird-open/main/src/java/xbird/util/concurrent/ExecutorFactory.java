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
package xbird.util.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ExecutorFactory {

    private ExecutorFactory() {}

    public static ThreadPoolExecutor newCachedThreadPool(String threadName) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new NamedThreadFactory(threadName));
    }

    public static ThreadPoolExecutor newCachedThreadPool(long keepAliveTimeInSec, String threadName) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, keepAliveTimeInSec, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new NamedThreadFactory(threadName));
    }

    /**
     * @link http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6458662
     */
    public static ThreadPoolExecutor newCachedThreadPool(int corePoolSize, long keepAliveTimeInSec, String threadName) {
        return new ThreadPoolExecutor(corePoolSize, Integer.MAX_VALUE, keepAliveTimeInSec, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new NamedThreadFactory(threadName));
    }

    public static ThreadPoolExecutor newCachedThreadPool(int corePoolSize, long keepAliveTimeInSec, String threadName, boolean daemon) {
        return new ThreadPoolExecutor(corePoolSize, Integer.MAX_VALUE, keepAliveTimeInSec, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new NamedThreadFactory(threadName, daemon));
    }

    /**
     * <code>ThreadPoolExecutor</code> only grows beyond coresize if your task queue is bounded and becomes full.
     * If your queue is unbounded then core is the limit. 
     * 
     * @see ThreadPoolExecutor
     * @link http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6458662
     */
    public static ThreadPoolExecutor newBoundedWorkQueueThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTimeInSec, String threadName) {
        final int workQueueSize = Math.min(corePoolSize + ((maximumPoolSize - corePoolSize) >> 1), corePoolSize << 1);
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTimeInSec, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(workQueueSize), new NamedThreadFactory(threadName), new WaitPolicy());
    }

    public static ThreadPoolExecutor newBoundedWorkQueueThreadPool(int corePoolSize, int maximumPoolSize, int workQueueSize, String threadName) {
        return newBoundedWorkQueueThreadPool(corePoolSize, maximumPoolSize, workQueueSize, 0L, threadName, false);
    }

    public static ThreadPoolExecutor newBoundedWorkQueueThreadPool(int corePoolSize, int maximumPoolSize, int workQueueSize, long keepAliveTimeInSec, String threadName, boolean daemon) {
        if(maximumPoolSize > corePoolSize && workQueueSize < maximumPoolSize) {
            throw new IllegalArgumentException("Pool never grows to maximumPoolSize ("
                    + maximumPoolSize + ") when workQueueSize (" + workQueueSize
                    + ") was less than corePoolSize (" + corePoolSize + ")");
        }
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTimeInSec, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(workQueueSize), new NamedThreadFactory(threadName, daemon), new WaitPolicy());
    }

    /**
     * Allow N active & W queued Thread.
     */
    public static ThreadPoolExecutor newBoundedWorkQueueFixedThreadPool(int nthreads, int workQueueSize, String threadName, boolean daemon) {
        return newBoundedWorkQueueThreadPool(nthreads, nthreads, workQueueSize, 0L, threadName, daemon);
    }

    /**
     * Allow N active & N queued Thread.
     */
    public static ThreadPoolExecutor newBoundedWorkQueueFixedThreadPool(int size, String threadName, boolean daemon) {
        return new ThreadPoolExecutor(size, size, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(size), new NamedThreadFactory(threadName, daemon), new WaitPolicy());
    }

    public static ScheduledExecutorService newScheduledExecutor(int corePoolSize, String threadName) {
        return Executors.newScheduledThreadPool(corePoolSize, new NamedThreadFactory(threadName));
    }

    public static ThreadPoolExecutor newFixedThreadPool(int nThreads, String threadName) {
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(threadName));
    }

    public static ThreadPoolExecutor newFixedThreadPool(int nThreads, String threadName, boolean daemon) {
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(threadName, daemon));
    }

    public static ThreadPoolExecutor newThreadPool(int corePoolSize, int maxPoolSize, String threadName) {
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(threadName));
    }

    public static ThreadPoolExecutor newThreadPool(int corePoolSize, int maxPoolSize, long keepAliveInSec, String threadName) {
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveInSec, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(threadName));
    }

    public static ThreadPoolExecutor newThreadPool(int corePoolSize, int maxPoolSize, long keepAliveInSec, String threadName, boolean daemon) {
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveInSec, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(threadName, daemon));
    }

    /**
     * A handler for unexecutable tasks that waits until task can be submitted for execution.
     * Note that this blocking method can handle tricky scenarios such as calling shutdownNow() during an invokeAll().
     */
    public static final class WaitPolicy implements RejectedExecutionHandler {

        public WaitPolicy() {}

        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if(!executor.isShutdown()) {
                try {
                    executor.getQueue().put(r);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RejectedExecutionException(e);
                }
            }
        }
    }

}
