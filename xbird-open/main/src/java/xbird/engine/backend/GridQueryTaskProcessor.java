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
package xbird.engine.backend;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.engine.RequestContext;
import xbird.engine.ResponseListener;
import xbird.engine.Request.Signature;
import xbird.engine.request.GridQueryTaskRequest;
import xbird.grid.GridException;
import xbird.xquery.ext.grid.QueryTask;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class GridQueryTaskProcessor extends BackendProcessor {
    private static final Log LOG = LogFactory.getLog(GridQueryTaskRequest.class);

    public GridQueryTaskProcessor(ResponseListener handler) {
        super(handler);
    }

    public Signature associatedWith() {
        return Signature.GRID_QTASK;
    }

    public void cancel(RequestContext rc) throws RemoteException {
    // TODO not supported yet
    }

    public void fire(RequestContext rc) throws RemoteException {
        GridQueryTaskRequest request = (GridQueryTaskRequest) rc.getRequest();
        final QueryTask task = request.getTask();

        final Serializable result;
        try {
            result = task.execute();
        } catch (GridException e) {
            LOG.error(e.getMessage(), e);
            rc.setFault(e);
            _resHandler.onResponse(rc);
            return;
        }

        rc.setResult(result);
        try {
            _resHandler.onResponse(rc);
        } catch (RemoteException re) {
            LOG.error("The respond for the request '" + rc.getRequest().getIdentifier()
                    + "' is failed", re);
        }
    }

}
