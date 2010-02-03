/*
 * @(#)$Id: ClusteredRequestManager.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.server.cluster;

import java.io.Serializable;
import java.rmi.RemoteException;

import xbird.server.*;
import xbird.server.ServerConstants.ReturnType;
import xbird.server.sched.AbstractScheduler;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class ClusteredRequestManager implements RequestManager {

    private final LocalRequestManager _localRequestManager;

    public ClusteredRequestManager(LocalRequestManager localRequestManager) {
        this._localRequestManager = localRequestManager;
    }

    public int getNumberOfQueryProcessors() {
        return _localRequestManager.getNumberOfQueryProcessors();
    }

    public AbstractScheduler getScheduler() {
        return _localRequestManager.getScheduler();
    }
    
    public <T extends Serializable> T execute(Request request, ReturnType returnType) throws RemoteException {
        return null;
    }

}
