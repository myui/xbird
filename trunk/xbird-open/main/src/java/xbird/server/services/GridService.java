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
package xbird.server.services;

import java.rmi.RemoteException;

import xbird.engine.InternalException;
import xbird.grid.GridServer;
import xbird.server.ServiceException;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class GridService extends ServiceBase {

    public static final String SRV_NAME = "GridService";

    private final GridServer grid;

    public GridService() {
        super(SRV_NAME);
        this.grid = new GridServer();
    }

    public void start() throws ServiceException {
        try {
            grid.start();
        } catch (InternalException e) {
            try {
                grid.shutdown(true);
            } catch (RemoteException re) {
                ;
            }
            throw new ServiceException(e.getMessage(), e);
        }
    }

    public void stop() throws ServiceException {
        try {
            grid.shutdown(true);
        } catch (RemoteException e) {
            ;
        }
    }

}
