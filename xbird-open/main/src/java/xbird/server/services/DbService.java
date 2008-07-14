/*
 * @(#)$Id: DbService.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.config.Settings;
import xbird.server.ServiceException;

/**
 * A service that manages a database instance.
 * <DIV lang="en">
 * Lock file '_lock' is created on the top of data cluster
 * set by a property 'xbird.database.datadir'.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class DbService extends ServiceBase {
    private static final Log LOG = LogFactory.getLog(DbService.class);

    public static final String SRV_NAME = "DbInstance";
    private static final String LOCK_FILE_NAME = "_lock";

    private String dataCluster = null;
    private String lockFilePath = null;

    public DbService() {
        super(SRV_NAME);
    }

    @Override
    public void initialize() throws ServiceException {
        if(_status != Status.nil) {
            throw new IllegalStateException("Illegal service state: " + _status);
        }
        this.dataCluster = Settings.get(Settings.KEY_DATA_CLUSTER_DIR);
        this._status = Status.prepared;
    }

    public void start() throws ServiceException {
        if(_status != Status.prepared) {
            throw new IllegalStateException("Illegal service state: " + _status);
        }
        if(dataCluster == null) {
            throw new IllegalStateException("Initialization seems to be not called before");
        }
        File clusterDir = new File(dataCluster);
        if(!clusterDir.exists()) {
            boolean created = clusterDir.mkdir();
            if(!created) {
                throw new IllegalStateException("Creation of the cluster data '" + dataCluster
                        + "' is failed");
            }
        }
        if(!clusterDir.isDirectory()) {
            throw new IllegalStateException("Data cluster '" + dataCluster + "' is not directory");
        }
        final File lockFile = new File(clusterDir, LOCK_FILE_NAME);
        if(lockFile.exists()) {
            // TODO FIXME
            //throw new IllegalStateException("lock file already exists: " + lockFile.getAbsolutePath());
        }
        final boolean created;
        try {
            created = lockFile.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("Creation of the lock file failed", e);
        }
        assert (created);
        this.lockFilePath = lockFile.getAbsolutePath();
        this._status = Status.started;
        LOG.info("Service started: " + _srvName);
    }

    public void stop() throws ServiceException {
        if(_status == Status.stopped) {
            if(LOG.isDebugEnabled()) {
                LOG.debug(_srvName + " is already stopped");
            }
            return;
        }
        if(_status != Status.started) {
            throw new IllegalStateException("Illegal service state: " + _status);
        }
        assert (lockFilePath != null);
        File lockFile = new File(lockFilePath);
        if(!lockFile.exists()) {
            throw new IllegalStateException("lock file is missing: " + lockFilePath);
        }
        boolean deleted = lockFile.delete();
        if(!deleted) {
            throw new IllegalStateException("Unable to delete lock file: " + lockFilePath);
        }
        this._status = Status.stopped;
        LOG.info("Service stopped: " + _srvName);
    }

}
