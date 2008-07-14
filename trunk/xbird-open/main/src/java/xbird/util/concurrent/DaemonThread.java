/*
 * @(#)$Id: DaemonThread.java 3619 2008-03-26 07:23:03Z yui $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class DaemonThread extends Thread {

    private static final Log LOG = LogFactory.getLog(DaemonThread.class);

    private final Daemon task;
    private boolean running = false;

    private final Object pauseLock = new Object();
    private boolean paused = false;

    protected DaemonThread() {
        this(null);
    }

    public DaemonThread(Daemon task) {
        super();
        this.task = task;
        this.setDaemon(true);
    }

    public DaemonThread(String name, Daemon task) {
        super(name);
        this.task = task;
        this.setDaemon(true);
    }

    public DaemonThread(RunnableDaemon target, String name) {
        super(target, name);
        this.task = target;
        this.setDaemon(true);
    }

    public DaemonThread(RunnableDaemon target) {
        super(target);
        this.task = target;
        this.setDaemon(true);
    }

    @Override
    public synchronized void start() {
        if(running) {
            throw new IllegalStateException("start called twice");
        }
        super.start();
        this.running = true;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        if(onStartup()) {// on startup
            runDaemon();
        }
        while(true) {
            try {
                /* obtain monitor and sleep */
                synchronized(pauseLock) {
                    pauseLock.wait(interval());
                    this.paused = true;
                }
                /* no monitor for interval execution */
                // Thread.sleep(task.getInterval());
            } catch (InterruptedException e) {//avoid
                LOG.debug(Thread.currentThread().getName() + " was interrupted");
            }
            runDaemon();
        }
    }

    protected boolean onStartup() {
        if(task == null) {
            throw new IllegalStateException();
        }
        return task.runOnStartup();
    }

    protected long interval() {
        if(task == null) {
            throw new IllegalStateException();
        }
        return task.getInterval();
    }

    protected void runDaemon() {
        if(task == null) {
            throw new IllegalStateException();
        }
        task.run();
    }

    public void wakeup() {
        if(paused) {
            synchronized(pauseLock) {
                pauseLock.notifyAll();
                this.paused = false;
            }
        }
    }

}
