/*
 * @(#)$Id: RemoteBase.java 3619 2008-03-26 07:23:03Z yui $
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
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.naming.NamingException;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.config.Settings;
import xbird.util.lang.ObjectUtils;
import xbird.util.net.NetUtils;
import xbird.util.net.TimeoutSocketProdiver;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class RemoteBase implements Remote, Serializable {
    private static final long serialVersionUID = -9122824621530533606L;
    private static final Log LOG = LogFactory.getLog(RemoteBase.class);

    public static final String RMI_PROTOCOL_JRMP = "jrmp";
    public static final String RMI_PROTOCOL_JRMP_SSL = "jrmp-ssl";

    public static final String rmiProtocol = Settings.get("xbird.rmi.protocol", RMI_PROTOCOL_JRMP);

    public static final int localRegistryPort = Integer.parseInt(Settings.get("xbird.rmi.registry.local.port"));

    private final String endpointUrl;
    private final int exportPort;

    public RemoteBase(String bindName, int exportPort) {
        if(bindName == null) {
            throw new IllegalArgumentException();
        }
        this.endpointUrl = "//" + NetUtils.getLocalHostName() + ":" + localRegistryPort + '/'
                + bindName;
        this.exportPort = exportPort;
    }

    public void start() throws InternalException {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    shutdown(false);
                } catch (RemoteException e) {
                    LOG.debug("shutdown failed", e);
                }
            }
        });
        try {
            prepareRegistry(localRegistryPort);
        } catch (RemoteException e) {
            throw new InternalException("failed to prepare local registry on port: "
                    + localRegistryPort, e);
        }
        try {
            bind();
        } catch (Exception e) {
            throw new InternalException("bind failed", e);
        }
    }

    protected void bind() throws RemoteException, NamingException {
        if(System.getSecurityManager() == null) {// create and install a security manager
            System.setSecurityManager(new RMISecurityManager());
        }

        final Remote stub;
        if(rmiProtocol.equals(RMI_PROTOCOL_JRMP_SSL)) {
            stub = UnicastRemoteObject.exportObject(this, exportPort, new SslRMIClientSocketFactory(), new SslRMIServerSocketFactory());
        } else {
            assert (rmiProtocol.equals(RMI_PROTOCOL_JRMP));
            stub = UnicastRemoteObject.exportObject(this, exportPort, TimeoutSocketProdiver.createClientSocketFactory(), null);
        }
        try {// bind the remote object's stub in the registry
            Naming.rebind(endpointUrl, stub);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Illegal regist url: " + endpointUrl, e);
        }
        LOG.info("Remote object is bounded at " + endpointUrl + " for "
                + ObjectUtils.identityToString(this));
    }

    public void shutdown(boolean forceExit) throws RemoteException {
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException e) {
            LOG.warn("object is not registered: " + ObjectUtils.identityToString(this), e);
        }
        unbind();
        if(forceExit) {
            System.exit(0);
        }
    }

    protected void unbind() {
        try {
            Naming.unbind(endpointUrl);
        } catch (Exception e) {
            LOG.debug("unbind failed", e);
        }
    }

    private static Registry prepareRegistry(int port) throws RemoteException {
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(port);
            LOG.info("created local rmi-registry at port: " + port);
        } catch (RemoteException e) {
            registry = LocateRegistry.getRegistry(port);
            LOG.info("re-used existing local rmi-registry of port: " + port);
        }
        assert (registry != null);
        return registry;
    }
}
