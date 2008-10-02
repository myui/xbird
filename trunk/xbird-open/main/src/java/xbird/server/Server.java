/*
 * @(#)$Id: Server.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.server.services.DbService;
import xbird.util.lang.ClassResolver;
import xbird.util.lang.ObjectUtils;
import xbird.util.resource.PropertiesLoader;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class Server {
    private static final Log LOG = LogFactory.getLog(Server.class);
    private static final Properties SRV_CONV = PropertiesLoader.load(Server.class, "service.list");

    private final ServiceManager services;

    public Server() {
        this.services = new ServiceManager();
    }

    public ServiceManager getServices() {
        return services;
    }

    /**
     * @param srvNames if none specified, register all services in the 'service.list'.
     */
    public void start(String... srvNames) {
        // #1 register core services
        registCoreServices();
        // #2 register optional services
        if(srvNames.length > 0) {
            String[] srvClazz = toServiceClasses(srvNames);
            registServices(srvClazz);
        } else {
            final String[] srvList = new String[SRV_CONV.size()];
            int i = 0;
            for(Object v : SRV_CONV.values()) {
                String s = v.toString();
                assert (s != null);
                srvList[i++] = s;
            }
            registServices(srvList);
        }
        // #3 shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread("Server_ShutdownHook") {
            public void run() {
                stopServices();
            }
        });
        // #4 start registered services
        try {
            services.startServices();
        } catch (ServiceException e) {
            throw new IllegalStateException("starting services failed", e.getCause());
        }
    }

    public void shutdown(boolean forceExit) {
        stopServices();
        if(forceExit) {
            System.exit(0);
        }
    }

    private final void stopServices() {
        try {
            services.stopServices();
        } catch (ServiceException e) {
            LOG.warn("shutdown failed", e);
        }
    }

    /**
     * Core services are: <code>DbService</code>, and <code>XQEngineService</code>.
     */
    private void registCoreServices() {
        services.addService(new DbService());
    }

    private boolean registServices(final String[] srvClazz) {
        boolean success = true;
        for(String srvClass : srvClazz) {
            assert (srvClass != null);
            final Class clazz;
            try {
                clazz = ClassResolver.get(srvClass);
            } catch (ClassNotFoundException e) {
                success = true;
                LOG.fatal("Service class '" + srvClass + "' is not found");
                continue;
            }
            Service srv = ObjectUtils.instantiate(clazz);
            services.addService(srv);
        }
        return success;
    }

    private static String[] toServiceClasses(final String[] srvNames) {
        final String[] clazz = new String[srvNames.length];
        for(int i = 0; i < srvNames.length; i++) {
            String srvName = srvNames[i];
            assert (srvName != null);
            String c = SRV_CONV.getProperty(srvName);
            assert (c != null);
            clazz[i] = c;
        }
        return clazz;
    }

    public static void main(String[] args) {
        if(System.getSecurityManager() == null) {// needed by RMI client
            System.setSecurityManager(new java.rmi.RMISecurityManager());
        }
        Server server = new Server();
        server.start(args);
    }

}
