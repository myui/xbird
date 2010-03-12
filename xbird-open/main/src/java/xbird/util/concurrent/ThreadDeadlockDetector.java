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

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.javaspecialists.co.za/archive/newsletter.do?issue=130
 */
public final class ThreadDeadlockDetector {

    private final Timer threadCheck = new Timer("ThreadDeadlockDetector", true);
    private final ThreadMXBean mbean = ManagementFactory.getThreadMXBean();
    private final Set<Listener> listeners = new CopyOnWriteArraySet<Listener>();

    /**
     * The number of milliseconds between checking for deadlocks.
     * It may be expensive to check for deadlocks, and it is not
     * critical to know so quickly.
     */
    private static final int DEFAULT_DEADLOCK_CHECK_PERIOD = 10000;

    public ThreadDeadlockDetector() {
        this(DEFAULT_DEADLOCK_CHECK_PERIOD);
    }

    public ThreadDeadlockDetector(int deadlockCheckPeriod) {
        threadCheck.schedule(new TimerTask() {
            public void run() {
                checkForDeadlocks();
            }
        }, 10, deadlockCheckPeriod);
    }

    private void checkForDeadlocks() {
        long[] ids = findDeadlockedThreads();
        if(ids != null && ids.length > 0) {
            Thread[] threads = new Thread[ids.length];
            for(int i = 0; i < threads.length; i++) {
                threads[i] = findMatchingThread(mbean.getThreadInfo(ids[i]));
            }
            fireDeadlockDetected(threads);
        }
    }

    private long[] findDeadlockedThreads() {
        // JDK 1.5 only supports the findMonitorDeadlockedThreads()
        // method, so you need to comment out the following three lines
        if(mbean.isSynchronizerUsageSupported()) {
            return mbean.findDeadlockedThreads();
        } else {
            return mbean.findMonitorDeadlockedThreads();
        }
    }

    private void fireDeadlockDetected(Thread[] threads) {
        for(Listener l : listeners) {
            l.deadlockDetected(threads);
        }
    }

    private Thread findMatchingThread(ThreadInfo inf) {
        for(Thread thread : Thread.getAllStackTraces().keySet()) {
            if(thread.getId() == inf.getThreadId()) {
                return thread;
            }
        }
        throw new IllegalStateException("Deadlocked Thread not found");
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
        void deadlockDetected(Thread[] deadlockedThreads);
    }

    public static final class StderrPrintDeadlockListener
            implements ThreadDeadlockDetector.Listener {
        public void deadlockDetected(Thread[] threads) {
            System.err.println("Deadlocked Threads:");
            System.err.println("-------------------");
            for(Thread thread : threads) {
                System.err.println(thread);
                for(StackTraceElement ste : thread.getStackTrace()) {
                    System.err.println("\t" + ste);
                }
            }
        }
    }
}
