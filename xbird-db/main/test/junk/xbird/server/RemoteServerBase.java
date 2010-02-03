/*
 * @(#)$Id: RemoteServerBase.java 3619 2008-03-26 07:23:03Z yui $
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

import static xbird.server.ServerConstants.RMI_PROTOCOL;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

import javax.naming.NamingException;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class RemoteServerBase extends ServerBase implements Remote {

    private static final Log LOG = LogFactory.getLog(RemoteServerBase.class);

    public RemoteServerBase(String bindUrl, int servPort) {
        super(bindUrl, servPort);
    }

    protected void bind() throws RemoteException, NamingException {
        final Remote stub;
        if (RMI_PROTOCOL.equals(ServerConstants.RMI_PROTOCOL_JRMP_SSL)) {
            stub = (Remote) UnicastRemoteObject.exportObject(this, _exportPort, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        } else {
            assert (RMI_PROTOCOL.equals("jrmp"));
            stub = (Remote) UnicastRemoteObject.exportObject(this, _exportPort);
        }
        // Bind the remote object's stub in the registry
        try {
            Naming.rebind(_bindUrl, stub);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Illegal regist url:" + _bindUrl, e);
        }
        LOG.info("Remote object is bounded at " + _bindUrl);
    }

    public void shutdown(boolean forceExit) throws RemoteException {
        unbind();
        if(forceExit) {
            System.exit(0);
        }
    }
}
