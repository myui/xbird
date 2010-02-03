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
package xbird.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import xbird.config.Settings;
import xbird.server.ServerConstants.ReturnType;
import xbird.util.PrintUtils;
import xbird.util.net.NetUtils;
import xbird.xquery.XQueryException;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class XQEngineServer extends RemoteServerBase implements XQEngine {

    private static final int _localRegistryPort = Integer.parseInt(Settings.get("xbird.rmi.registry.local.port"));

    private static final String _localRegistryUrl = "//" + NetUtils.getLocalHostName() + ":" + _localRegistryPort + '/'
            + Settings.get("xbird.rmi.serv.name");

    private static final int _servPort = Integer.parseInt(Settings.get("xbird.rmi.serv.port", "0"));

    private RequestManager _requestManager = null;

    public XQEngineServer() {
        super(_localRegistryUrl, _servPort);
    }

    @Override
    protected void run(String[] args) throws XQueryException {
        super.run(args);
        // TODO DistributedRequestManager
        this._requestManager = new LocalRequestManager(Settings.getProperties());
    }

    @Override
    protected void start() throws ServerSideException {
        try {// startup rmiregistry(global repository) on localhost
            Registry registry = prepareRegistry(_localRegistryPort);
            assert (registry != null);
        } catch (RemoteException e) {
            throw new ServerSideException("failed to launch registry on port " + _localRegistryPort, e);
        }
        super.start();
    }

    private static Registry prepareRegistry(final int port) throws RemoteException {
        try {
            return LocateRegistry.getRegistry(port);
        } catch (RemoteException e) {
            return LocateRegistry.createRegistry(port);
        }
    }

    public Object execute(Request request) throws RemoteException {
        return execute(request, ReturnType.Sequence);
    }

    public Object execute(Request request, ReturnType returnType) throws RemoteException {
        assert (_requestManager != null);
        return _requestManager.execute(request, returnType);
    }

    public static void main(String[] args) {
        try {
            new XQEngineServer().run(args);
        } catch (Throwable e) {
            PrintUtils.prettyPrintStackTrace(e, System.err);
            System.exit(1);
        }
    }

}
