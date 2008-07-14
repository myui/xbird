/*
 * @(#)$Id: ServiceManager.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ServiceManager {

    private final Map<String, Service> services = new HashMap<String, Service>(8);

    public ServiceManager() {}

    /**
     * @return false if there is a same named service
     */
    public boolean addService(Service srv) {
        Service old = services.put(srv.getName(), srv);
        return old == null;
    }

    public boolean removeService(String srvName) {
        if(srvName == null) {
            throw new IllegalArgumentException();
        }
        Service removed = services.remove(srvName);
        return removed != null;
    }

    public Collection<Service> listServices() {
        return services.values();
    }

    public Service getService(String srvName) {
        return services.get(srvName);
    }

    public void startServices() throws ServiceException {
        for(Service srv : services.values()) {
            srv.initialize();
            srv.start();
        }
    }

    public void stopServices() throws ServiceException {
        for(Service srv : services.values()) {
            srv.stop();
        }
    }

}
