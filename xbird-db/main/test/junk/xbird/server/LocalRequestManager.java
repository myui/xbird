/*
 * @(#)$Id: LocalRequestManager.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.server;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.*;
import java.util.*;

import javax.naming.NamingException;

import xbird.config.Settings;
import xbird.server.ServerConstants.ReturnType;
import xbird.server.backend.BackendProcessor;
import xbird.server.repository.IDocumentRepository;
import xbird.server.request.QueryRequest;
import xbird.server.sched.AbstractScheduler;
import xbird.server.sched.SchedulerFactory;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class LocalRequestManager implements RequestManager {

    private static final String _repositoryUrl = ServerConstants.GLOBAL_REGISTORY + '/'
            + Settings.get("xbird.rmi.repository.name");

    private static final String _qpUrlPrefix = ServerConstants.GLOBAL_REGISTORY + '/'
            + Settings.get(ServerConstants.QP_NAME_PREFIX);

    private transient IDocumentRepository _repository = null;

    private transient AbstractScheduler _sched = null;

    private int _numQueryProcessors = -1;

    public LocalRequestManager(final Properties props) {
        assert (props != null);
        setup(props);
        assert (_sched != null);
        assert (_numQueryProcessors > 0);
    }

    private void setup(final Properties props) {
        // #1 set query processors
        final BackendProcessor[] procs;
        try {
            procs = prepareQueryProcessors(props);
        } catch (Exception e) {
            throw new IllegalStateException("Failed preparing query processors!", e);
        }
        this._numQueryProcessors = procs.length;
        // #2 set document repository        
        final IDocumentRepository repos;
        try {
            repos = lookupRegistry(LocalRequestManager._repositoryUrl);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to lookup document registory: "
                    + LocalRequestManager._repositoryUrl, e);
        }
        this._repository = repos;
        // #3 set scheduler
        this._sched = SchedulerFactory.createScheduler(procs, repos);
    }

    private static BackendProcessor[] prepareQueryProcessors(final Properties props)
            throws RemoteException, MalformedURLException, NamingException, NotBoundException {
        final List<String> procNames = new ArrayList<String>(4);
        final String[] names = Naming.list(ServerConstants.GLOBAL_REGISTORY);
        for(String n : names) {
            if (n.startsWith(_qpUrlPrefix)) {
                procNames.add(n);
            }
        }
        assert (!procNames.isEmpty()) : "QueryProcessor is not bouned at "
                + ServerConstants.GLOBAL_REGISTORY;
        final int psize = procNames.size();
        final BackendProcessor[] procs = new BackendProcessor[psize];
        for(int i = 0; i < psize; i++) {
            final String name = procNames.get(i);
            BackendProcessor remote = lookupRegistry(name);
            procs[i] = remote;
        }
        return procs;
    }

    private static <T extends Remote> T lookupRegistry(final String bindedUrl)
            throws MalformedURLException, RemoteException, NotBoundException, NamingException {
        assert (bindedUrl != null);
        return (T) Naming.lookup(bindedUrl);
    }

    public int getNumberOfQueryProcessors() {
        return _numQueryProcessors;
    }
    
    public AbstractScheduler getScheduler() {
        return _sched;
    }

    public <T extends Serializable> T execute(Request request, ReturnType returnType) throws RemoteException {
        assert (_sched != null);
        final Serializable result;
        final int sign = request.getSignature();
        switch (sign) {
            case QueryRequest.SIGNATURE:
                final String query = ((QueryRequest) request).getQuery();
                BackendProcessor proc = _sched.dispatchRequest(request);
                result = proc.execute(query, this, returnType);
                _sched.notifyCompletion(request);
                break;
            default:
                throw new IllegalStateException("Illegal return type: " + returnType);
        }
        return (T) result;
    }

}
