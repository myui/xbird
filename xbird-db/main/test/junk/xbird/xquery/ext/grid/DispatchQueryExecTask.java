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
package xbird.xquery.ext.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gridool.GridConfiguration;
import gridool.GridException;
import gridool.GridJob;
import gridool.GridKernel;
import gridool.GridNode;
import gridool.GridTask;
import gridool.annotation.DirectoryResource;
import gridool.annotation.GridConfigResource;
import gridool.annotation.GridKernelResource;
import gridool.communication.payload.GridNodeInfo;
import gridool.construct.GridTaskAdapter;
import gridool.directory.LocalDirectory;
import gridool.locking.LockManager;
import gridool.routing.GridNodeSelector;
import gridool.routing.GridTaskRouter;
import gridool.util.GridUtils;
import xbird.storage.DbException;
import xbird.util.struct.Pair;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.BindingVariable;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DispatchQueryExecTask extends GridTaskAdapter {
    private static final long serialVersionUID = 5561645251880268369L;
    private static final Log LOG = LogFactory.getLog(DispatchQueryExecTask.class);

    private final BindingVariable bindingVar;
    private final XQExpression bodyExpr;
    private final List<String> relativePaths;
    private final List<GridNode> excludeNodeList;
    private final boolean redirectable;

    // ----------------------------------------
    // injected resources

    @GridKernelResource
    private transient GridKernel grid;
    @GridConfigResource
    private transient GridConfiguration config;
    @DirectoryResource
    private transient LocalDirectory directory;

    // ----------------------------------------

    @SuppressWarnings("unchecked")
    public DispatchQueryExecTask(GridJob job, BindingVariable bindingVar, XQExpression bodyExpr, List<String> relativePaths, boolean redirectable) {
        super(job, true);
        assert (bindingVar != null);
        assert (bodyExpr != null);
        assert (relativePaths != null);
        this.bindingVar = bindingVar;
        this.bodyExpr = bodyExpr;
        this.relativePaths = relativePaths;
        this.excludeNodeList = new ArrayList<GridNode>(2);
        this.redirectable = redirectable;
    }

    @Override
    public boolean isAsyncTask() {
        return false;
    }

    @Override
    public boolean injectResources() {
        return true;
    }

    public boolean isRedirectable() {
        return redirectable;
    }

    public BindingVariable getBindingVariable() {
        return bindingVar;
    }

    public XQExpression getBodyExpression() {
        return bodyExpr;
    }

    public List<String> getRelativePaths() {
        return relativePaths;
    }

    @Override
    public Sequence<? extends Item> execute() throws GridException {
        final Future<Sequence<? extends Item>> future = grid.execute(QueryExecJob.class, this);
        try {
            return future.get();
        } catch (InterruptedException ie) {
            LOG.error(ie.getMessage());
            throw new GridException(ie.getMessage(), ie);
        } catch (ExecutionException ee) {
            LOG.error(ee.getMessage());
            throw new GridException(ee.getMessage(), ee);
        }
    }

    /**
     * @see QueryExecJob#map(gridool.routing.GridTaskRouter, DispatchQueryExecTask)
     */
    public Map<GridTask, GridNode> mapQueryTask(QueryExecJob execJob) throws GridException {
        checkInjectedResources();

        final GridNodeInfo localNode = GridUtils.getLocalNode(config);
        if(!excludeNodeList.contains(localNode)) {
            excludeNodeList.add(localNode);
        }
        final GridNodeSelector nodeSelector = config.getNodeSelector();
        final LockManager lockManager = directory.getLockManager();

        final Map<GridNode, List<String>> assignMap = new HashMap<GridNode, List<String>>(relativePaths.size());
        final List<Pair<String, Lock>> localExecResources = new ArrayList<Pair<String, Lock>>(relativePaths.size());

        int totalLocked = 0;
        for(String path : relativePaths) {
            ReadWriteLock lock = lockManager.obtainLock(path);
            final Lock rlock = lock.readLock();
            if(rlock.tryLock()) {
                localExecResources.add(new Pair<String, Lock>(path, rlock));
            } else {
                totalLocked++;
                final List<GridNode> replicatedNodes;
                try {
                    replicatedNodes = directory.exactSearch(path, excludeNodeList);
                } catch (DbException e) {
                    LOG.error(e.getMessage());
                    throw new GridException("Exception caused while lookup: " + path, e);
                }
                if(replicatedNodes == null || replicatedNodes.isEmpty()) {
                    throw new GridException("No replicated document found for path: " + path);
                }
                // TODO Select a node that least recently used for write requests.
                GridNode node = nodeSelector.selectNode(replicatedNodes, this, config);
                assert (node != null);
                List<String> mappedPaths = assignMap.get(node);
                if(mappedPaths == null) {
                    mappedPaths = new ArrayList<String>(16);
                    assignMap.put(node, mappedPaths);
                }
                mappedPaths.add(path);
            }
        }

        final Map<GridTask, GridNode> map = new IdentityHashMap<GridTask, GridNode>(assignMap.size() + 1);
        for(Map.Entry<GridNode, List<String>> e : assignMap.entrySet()) {
            GridNode node = e.getKey();
            List<String> mappedPaths = e.getValue();
            DispatchQueryExecTask dispatchTask = new DispatchQueryExecTask(execJob, bindingVar, bodyExpr, mappedPaths, true);
            map.put(dispatchTask, node);
        }
        if(!localExecResources.isEmpty()) {
            boolean doForwarding = redirectable;
            LocalQueryExecTask localTask = new LocalQueryExecTask(execJob, bindingVar, bodyExpr, localExecResources, doForwarding);
            map.put(localTask, localNode);
        }

        if(LOG.isInfoEnabled()) {
            LOG.info("DispatchQueryExecTask is mapped to " + assignMap.size()
                    + " DispatchQueryExecTask and " + (localExecResources.isEmpty() ? '0' : '1')
                    + " LocalQueryExecTask (" + localExecResources.size()
                    + " localExecResources), " + totalLocked + " documents are write-locked");
        }
        return map;
    }

    private void checkInjectedResources() {
        if(config == null) {
            throw new IllegalStateException("GridConfiguration is not injected");
        }
        if(directory == null) {
            throw new IllegalStateException("LocalDirectory is not injected");
        }
    }

    @Override
    public List<GridNode> listFailoverCandidates(GridTask task, GridTaskRouter router) {
        GridNode[] liveNodes = router.getAllNodes();
        return GridUtils.selectSuperNodes(liveNodes);
    }

}
