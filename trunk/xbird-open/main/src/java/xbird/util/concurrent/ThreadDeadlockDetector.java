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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.javaspecialists.co.za/archive/newsletter.do?issue=130
 * @since 1.6
 */
public final class ThreadDeadlockDetector {

    private final Timer threadCheck = new Timer("ThreadDeadlockDetector", true);
    private final ThreadMXBean mbean;
    private final boolean isObjectMonitorUsageSupported;
    private final boolean isSynchronizerUsageSupported;
    private final long deadlockCheckPeriod;

    private final Set<Listener> listeners = new CopyOnWriteArraySet<Listener>();
    private final AtomicBoolean started = new AtomicBoolean(false);

    /**
     * The number of milliseconds between checking for deadlocks.
     * It may be expensive to check for deadlocks, and it is not
     * critical to know so quickly.
     */
    private static final long DEFAULT_DEADLOCK_CHECK_PERIOD = 10000L;

    public ThreadDeadlockDetector() {
        this(DEFAULT_DEADLOCK_CHECK_PERIOD);
    }

    public ThreadDeadlockDetector(long deadlockCheckPeriod) {
        this.mbean = ManagementFactory.getThreadMXBean();
        this.isObjectMonitorUsageSupported = mbean.isObjectMonitorUsageSupported();
        this.isSynchronizerUsageSupported = mbean.isSynchronizerUsageSupported();
        this.deadlockCheckPeriod = deadlockCheckPeriod;
    }

    public void start() {
        if(started.compareAndSet(false, true)) {
            threadCheck.schedule(new TimerTask() {
                public void run() {
                    checkForDeadlocks();
                }
            }, 10, deadlockCheckPeriod);
        }
    }

    public void stop() {
        threadCheck.cancel();
    }

    private void checkForDeadlocks() {
        long[] ids = findDeadlockedThreads();
        if(ids != null && ids.length > 0) {
            ThreadInfo[] infos = mbean.getThreadInfo(ids, isObjectMonitorUsageSupported, isSynchronizerUsageSupported);
            fireDeadlockDetected(infos);
        }
    }

    private long[] findDeadlockedThreads() {
        // JDK 1.5 only supports the findMonitorDeadlockedThreads()
        // method, so you need to comment out the following three lines
        if(isSynchronizerUsageSupported) {
            return mbean.findDeadlockedThreads();
        } else {
            return mbean.findMonitorDeadlockedThreads();
        }
    }

    private void fireDeadlockDetected(ThreadInfo[] threadInfos) {
        for(Listener l : listeners) {
            l.deadlockDetected(threadInfos);
        }
    }

    public boolean addListener(Listener l) {
        return listeners.add(l);
    }

    public boolean removeListener(Listener l) {
        return listeners.remove(l);
    }

    /**
     * This is called whenever a problem with threads is detected.
     */
    public interface Listener {
        void deadlockDetected(ThreadInfo[] deadlockedThreads);
    }

    public static final class StderrDeadlockReporter implements ThreadDeadlockDetector.Listener {
        public void deadlockDetected(ThreadInfo[] threads) {
            System.err.println("Deadlocked Threads:");
            System.err.println("-------------------");
            for(ThreadInfo ti : threads) {
                System.err.println(ti);
            }
        }
    }
}
