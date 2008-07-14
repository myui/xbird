/*
 * @(#)$Id: RequestContext.java 3619 2008-03-26 07:23:03Z yui $
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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class RequestContext implements Serializable, Comparable<RequestContext> {
    private static final long serialVersionUID = -1822731673242860761L;

    private final Request _request;
    private Serializable _result = null;

    private final long _created;
    private long _fired = -1L;
    private long _finished = -1L;

    private final int _hash;
    private Throwable _fault;

    private transient boolean _notified = false;

    public RequestContext(Request request) {
        if(request == null) {
            throw new IllegalArgumentException();
        }
        this._request = request;
        this._created = System.currentTimeMillis();
        this._hash = System.identityHashCode(this);
    }

    public Request getRequest() {
        return _request;
    }

    public Serializable getResult() {
        return _result;
    }

    public void setResult(Serializable result) {
        this._finished = System.currentTimeMillis();
        this._result = result;
    }

    public long getCreated() {
        return _created;
    }

    public long getFinished() {
        return _finished;
    }

    public void setFinished(long finished) {
        this._finished = finished;
    }

    public long getFired() {
        return _fired;
    }

    public void setFired(long fired) {
        this._fired = fired;
    }

    public Throwable getFault() {
        return _fault;
    }

    public void setFault(Throwable fault) {
        this._fault = fault;
    }

    public int compareTo(RequestContext trg) {
        short myPriority = _request.getPriority();
        short trgPriority = trg.getRequest().getPriority();
        return myPriority < trgPriority ? -1 : (myPriority == trgPriority ? 0 : 1);
    }

    public String printableStatus() {
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        final long elapsed = _finished - _fired;
        return "Elapsed time on this server: " + elapsed + "(msec), created: "
                + df.format(new Date(_created)) + ", fired: "
                + (_fired == -1L ? "nil" : df.format(new Date(_fired))) + ", finished: "
                + (_finished == -1L ? "nil" : df.format(new Date(_finished))) + ". " + _request;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public int hashCode() {
        return _hash;
    }

    public void invokeNotify() {
        this._notified = true;
        notify();
    }

    public boolean isNotified() {
        return _notified;
    }
}
