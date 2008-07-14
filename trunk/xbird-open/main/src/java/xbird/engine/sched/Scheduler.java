/*
 * @(#)$Id: Scheduler.java 3619 2008-03-26 07:23:03Z yui $
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

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.config.Settings;
import xbird.engine.*;
import xbird.engine.Request.Signature;
import xbird.util.concurrent.ExecutorFactory;
import xbird.util.lang.SystemUtils;

/**
 * Scheduler runs in a thread.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class Scheduler implements Runnable {
    private static final Log LOG = LogFactory.getLog(Scheduler.class);
    private static final int MAX_BACKENDS;
    static {
        int maxPerProc = Integer.parseInt(Settings.get("xbird.sched.max_backends_per_proc", "8"));
        MAX_BACKENDS = maxPerProc * SystemUtils.availableProcessors();
    }

    protected final ResponseListener _resHandler;
    protected final List<ScheduledEventListener> _listeners;

    protected boolean _paused = false;
    private final Object _pauseLock = new Object();

    private final ExecutorService _executors;

    Scheduler(ResponseListener resHandler) {
        this._resHandler = resHandler;
        this._listeners = new ArrayList<ScheduledEventListener>(4);
        this._executors = ExecutorFactory.newBoundedThreadPool(4, MAX_BACKENDS, 60L, "BackendProc"); // TODO REVIEWME max threads
    }

    Scheduler(ResponseListener resHandler, ScheduledEventListener... listeners) {
        this._resHandler = resHandler;
        final List<ScheduledEventListener> list = new ArrayList<ScheduledEventListener>(listeners.length + 2);
        for(ScheduledEventListener listener : listeners) {
            list.add(listener);
        }
        this._listeners = list;
        this._executors = ExecutorFactory.newBoundedThreadPool(4, MAX_BACKENDS, 60L, "BackendProc"); // TODO REVIEWME max threads
    }

    public final void addListener(ScheduledEventListener listener) {
        _listeners.add(listener);
    }

    /** run as a thread and wait for notify */
    public final void standby() {
        if(_listeners.isEmpty()) {
            throw new IllegalStateException("no listener is registered");
        }
        final Thread thread = new Thread(this, "Sched");
        thread.start();
    }

    /**
     * @see #addTask(RequestContext)
     */
    public void run() {
        while(true) {
            togglePause(true); // wait for task added
            processTask();
        }
    }

    protected void togglePause(boolean pause) {
        if(pause == _paused) {
            return;
        }
        synchronized(_pauseLock) {
            if(pause) {
                this._paused = true;
                try {
                    _pauseLock.wait();
                } catch (InterruptedException e) {
                    LOG.warn("could not acquire the thread lock", e);
                }
            } else if(_paused) {
                _pauseLock.notifyAll();
                this._paused = false;
            }
        }
    }

    public abstract void addTask(RequestContext rctxt);

    public abstract void removeTask(Request request);

    protected abstract void processTask();

    protected void invokeFire(final RequestContext rc) {
        final Request request = rc.getRequest();
        final Signature rtype = request.getSignature();
        boolean fired = false;
        for(ScheduledEventListener listener : _listeners) {
            final Signature ltype = listener.associatedWith();
            if(ltype == rtype) {
                final long timeout = request.getTimeout();
                if(timeout != Request.NO_TIMEOUT) {
                    execute(rc, listener, timeout);
                } else {
                    execute(rc, listener);
                }
                fired = true;
                break;
            }
        }
        if(!fired) {
            final String errmsg = "No listener is found for " + request;
            LOG.error(errmsg);
            rc.setFault(new SchedulerException(errmsg));
            try {
                _resHandler.onResponse(rc);
            } catch (RemoteException e) {
                LOG.error("onResponse failed: " + request, e);
                throw new IllegalStateException(e);
            }
        }
    }

    private final void execute(final RequestContext rc, final ScheduledEventListener listener, final long timeout) {
        if(timeout <= 0) {
            throw new IllegalArgumentException("Illegal timeout value: " + timeout);
        }
        final Request request = rc.getRequest();
        final TimerTask cancel = new TimerTask() {
            public void run() {
                try {
                    listener.cancel(rc);
                } catch (RemoteException e) {
                    LOG.warn("canceling task is failed: " + request.getIdentifier(), e);
                }
            }
        };
        final Timer timer = new Timer("SchedTimeout");
        timer.schedule(cancel, timeout);
        execute(rc, listener);
        timer.cancel();
    }

    private final void execute(final RequestContext rc, final ScheduledEventListener listener) {
        final Request request = rc.getRequest();
        final Runnable r = new Runnable() {
            public void run() {
                try {
                    listener.fire(rc);
                } catch (RemoteException e) {
                    LOG.error("Request failed: " + request.getIdentifier(), e);
                }
            }
        };
        _executors.execute(r);
    }

}
