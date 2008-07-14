/*
 * @(#)$Id: XQEngineServer.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.config.Settings;

/**
 * The frontend engine server for various requests to database server.
 * <DIV lang="en">
 * Caution: <br/>
 * When polling with the new <code>Request</code> object, ensure that 
 * enought interval of method call between {@link #execute(Request)} and {@link #poll(Request)}
 * is promised.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class XQEngineServer extends RemoteBase implements XQEngine {
    private static final long serialVersionUID = 5410190251826182651L;
    private static final Log LOG = LogFactory.getLog(XQEngineServer.class);

    private static final String bindName = Settings.get("xbird.rmi.engine.name");
    private static final int exportPort = Integer.parseInt(Settings.get("xbird.rmi.engine.port", "0"));

    private final RequestManager requestManager;

    public XQEngineServer() {
        super(bindName, exportPort);
        this.requestManager = new RequestManager();
    }

    public StampedResult execute(Request request) throws RemoteException {
        if(LOG.isDebugEnabled()) {
            final long latency = System.currentTimeMillis() - request.getInvoked();
            LOG.debug("Latency of the request `" + request + "': "
                    + (latency < 0 ? "N/A" : (latency + "(msec)")));
        }
        final Serializable result = requestManager.dispatchRequest(request);
        return new StampedResult(result);
    }

    @Deprecated
    public Object execute(Request request, ResultHandler handler) throws RemoteException {
        if(handler != null) {
            request.setResultHandler(handler);
        }
        return execute(request);
    }

    public Object poll(Request request) throws RemoteException {
        return requestManager.pollRequest(request);
    }

    public void shutdown() throws RemoteException {
        shutdown(false);
    }
}
