/*
 * @(#)$Id: RequestManager.java 3624 2008-03-26 08:01:20Z yui $
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
package xbird.engine;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.engine.Request.ReplyPattern;
import xbird.engine.sched.Scheduler;
import xbird.engine.sched.SchedulerFactory;
import xbird.util.cache.Cache;
import xbird.util.cache.CacheException;
import xbird.util.cache.CacheFactory;

/**
 * The Dispatcher/Manager of various requests.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RequestManager implements ResponseListener {
    private static final Log LOG = LogFactory.getLog(RequestManager.class);

    private final Scheduler _sched;
    private final Cache<String, Serializable> _pendingPolled;

    @SuppressWarnings("unchecked")
    public RequestManager() {
        this._sched = SchedulerFactory.createScheduler(this);
        this._pendingPolled = CacheFactory.createCache(RequestManager.class.getName());
    }

    public Serializable dispatchRequest(Request request) throws RemoteException {
        // schedule task
        RequestContext rc = new RequestContext(request);
        _sched.addTask(rc);

        // execute task
        final Serializable result;
        ReplyPattern replyPtn = request.getReplyPattern();
        if(replyPtn == ReplyPattern.RESPONSE) {// synchronous response
            Serializable promptResult = rc.getResult();
            if(promptResult != null) {
                result = promptResult;
            } else {
                syncSignal(true, rc);
                result = rc.getResult();
            }
            if(result == null) {
                Throwable fault = rc.getFault();
                if(fault != null) {
                    throw new RemoteException("Request# " + request.getIdentifier() + " failed.\n"
                            + rc.printableStatus(), fault);
                }
                long timeout = request.getTimeout();
                if(timeout != Request.NO_TIMEOUT) {
                    String timeoutMsg = "Request# " + request.getIdentifier()
                            + " has been timeout since the request took over " + timeout / 1000
                            + " seconds";
                    LOG.warn(timeoutMsg);
                    throw new RemoteException(timeoutMsg);
                }
                LOG.warn("null result for Request#" + request.getIdentifier());
            }
        } else {// asynchronous response
            result = null;
        }
        if(LOG.isInfoEnabled()) {
            LOG.info(rc.printableStatus());
        }
        return result;
    }

    /** handle the responding callback of the task */
    public void onResponse(RequestContext rc) throws RemoteException {
        rc.setFinished(System.currentTimeMillis());
        syncSignal(false, rc);

        Request request = rc.getRequest();
        ReplyPattern type = request.getReplyPattern();
        if(type == ReplyPattern.CALLBACK) {
            ResultHandler handler = request.getResultHandler();
            if(handler == null) {
                throw new IllegalStateException("ResultHandler is not set for the Callback reply pattern on Request#"
                        + request.getIdentifier());
            }
            Serializable result = rc.getResult();
            if(result == null) {
                Throwable fault = rc.getFault();
                if(fault != null) {
                    handler.handleError(request, fault);
                    return;
                }
                long timeout = request.getTimeout();
                if(timeout != Request.NO_TIMEOUT) {
                    String timeoutMsg = "Request# " + request.getIdentifier()
                            + " has been timeout since the request took over " + timeout / 1000
                            + " seconds";
                    LOG.warn(timeoutMsg);
                    handler.handleError(request, timeoutMsg);
                    return;
                }
                LOG.warn("null result for Request#" + request.getIdentifier());
            }
            handler.handleResult(result);
        } else if(type == ReplyPattern.POLL) {
            String rid = request.getIdentifier();
            Serializable result = rc.getResult();
            try {
                _pendingPolled.put(rid, result);
            } catch (CacheException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public Object pollRequest(Request request) {
        final String rid = request.getIdentifier();
        final Object result;
        try {
            result = _pendingPolled.get(rid);
            if(result != null) {
                _pendingPolled.remove(rid);
            }
        } catch (CacheException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }

    protected final void syncSignal(final boolean pause, final RequestContext rctxt) {
        if(pause) {
            final long timeout = rctxt.getRequest().getTimeout();
            try {
                synchronized(rctxt) {
                    if(rctxt.isNotified()) {
                        return;
                    }
                    if(timeout != Request.NO_TIMEOUT) {
                        rctxt.wait(timeout);
                    } else {
                        rctxt.wait();
                    }
                }
            } catch (InterruptedException e) {
                LOG.warn("could not acquire a thread lock", e);
            }
        } else {
            synchronized(rctxt) {
                rctxt.invokeNotify();
            }
        }
    }

}
