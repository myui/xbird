/*
 * @(#)$Id: XQEngineProxyService.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.server.services;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xbird.engine.InternalException;
import xbird.engine.XQEngineServer;
import xbird.server.ServiceException;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class XQEngineProxyService extends ServiceBase {
    private static final Log LOG = LogFactory.getLog(XQEngineProxyService.class);

    public static final String SRV_NAME = "XQEngineProxy";

    private final XQEngineServer engine;

    public XQEngineProxyService() {
        super(SRV_NAME);
        this.engine = new XQEngineServer();
    }

    public void start() throws ServiceException {
        if(_status != Status.prepared) {
            throw new IllegalStateException("Illegal service state: " + _status);
        }
        try {
            engine.start();
        } catch (InternalException e) {
            throw new ServiceException("failed to start an engine", e.getCause());
        }
        this._status = Status.started;
        LOG.info("Service started: " + _srvName);
    }

    public void stop() throws ServiceException {
        if(_status == Status.stopped) {
            return;
        }
        if(_status != Status.started) {
            throw new IllegalStateException("Illegal service state: " + _status);
        }
        try {
            engine.shutdown(false);
        } catch (RemoteException e) {
            throw new ServiceException("failed to shutdown the engine", e);
        }
        this._status = Status.stopped;
        LOG.info("Service stopped: " + _srvName);
    }

}
