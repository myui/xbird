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

import gridool.GridConfiguration;
import gridool.GridException;
import gridool.GridJob;
import gridool.GridKernel;
import gridool.GridNode;
import gridool.GridTask;
import gridool.annotation.GridConfigResource;
import gridool.annotation.GridDirectoryResource;
import gridool.annotation.GridKernelResource;
import gridool.communication.payload.GridNodeInfo;
import gridool.construct.GridTaskAdapter;
import gridool.directory.ILocalDirectory;
import gridool.directory.helpers.GridNodeValueCollector;
import gridool.locking.LockManager;
import gridool.routing.GridNodeSelector;
import gridool.routing.GridTaskRouter;
import gridool.util.GridUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.storage.DbException;
import xbird.util.io.FastByteArrayInputStream;
import xbird.util.io.FastByteArrayOutputStream;
import xbird.util.string.StringUtils;
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

    private transient byte[] _exprBytes; // place holder
    private transient BindingVariable _bindingVar;
    private transient XQExpression _bodyExpr;

    private final boolean _redirectable;
    private final List<GridNode> _excludeNodeList;
    private final List<String> _relativePaths;

    // ----------------------------------------
    // injected resources

    @GridKernelResource
    private transient GridKernel grid;
    @GridConfigResource
    private transient GridConfiguration config;
    @GridDirectoryResource
    private transient ILocalDirectory directory;

    // ----------------------------------------

    @SuppressWarnings("unchecked")
    public DispatchQueryExecTask(@CheckForNull GridJob job, @Nonnull BindingVariable bindingVar, @Nonnull XQExpression bodyExpr, @Nonnull byte[] exprBytes, @Nonnull List<String> relativePaths, boolean redirectable) {
        super(job, true);
        assert (bindingVar != null);
        assert (bodyExpr != null);
        assert (exprBytes != null);
        assert (relativePaths != null);
        this._bindingVar = bindingVar;
        this._bodyExpr = bodyExpr;
        this._exprBytes = exprBytes;
        this._relativePaths = relativePaths;
        this._excludeNodeList = new ArrayList<GridNode>(2);
        this._redirectable = redirectable;
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
        return _redirectable;
    }

    public BindingVariable getBindingVariable() {
        return _bindingVar;
    }

    public XQExpression getBodyExpression() {
        return _bodyExpr;
    }

    public List<String> getRelativePaths() {
        return _relativePaths;
    }

    public Sequence<? extends Item> execute() throws GridException {
        final Future<Sequence<? extends Item>> future = grid.execute(QueryExecJob.class, this);
        try {
            return future.get();
        } catch (InterruptedException ie) {
            LOG.error(ie.getMessage(), ie);
            throw new GridException(ie.getMessage(), ie);
        } catch (ExecutionException ee) {
            LOG.error(ee.getMessage(), ee);
            throw new GridException(ee.getMessage(), ee);
        }
    }

    /**
     * @see QueryExecJob#map(gridool.routing.GridTaskRouter, DispatchQueryExecTask)
     */
    public Map<GridTask, GridNode> mapQueryTask(QueryExecJob execJob) throws GridException {
        checkInjectedResources();

        final GridNodeInfo localNode = GridUtils.getLocalNode(config);
        if(!_excludeNodeList.contains(localNode)) {
            _excludeNodeList.add(localNode);
        }
        final GridNodeSelector nodeSelector = config.getNodeSelector();
        final LockManager lockManager = directory.getLockManager();

        final Map<GridNode, List<String>> assignMap = new HashMap<GridNode, List<String>>(_relativePaths.size());
        final List<Pair<String, Lock>> localExecResources = new ArrayList<Pair<String, Lock>>(_relativePaths.size());

        int totalLocked = 0;
        for(String path : _relativePaths) {
            ReadWriteLock lock = lockManager.obtainLock(path);
            final Lock rlock = lock.readLock();
            if(rlock.tryLock()) {
                localExecResources.add(new Pair<String, Lock>(path, rlock));
            } else {
                totalLocked++;
                final byte[] k = StringUtils.getBytes(path);
                final GridNodeValueCollector collector = new GridNodeValueCollector(_excludeNodeList);
                try {
                    directory.exactSearch(k, collector);
                } catch (DbException e) {
                    String errmsg = "Exception caused while lookup: " + path;
                    LOG.error(errmsg, e);
                    throw new GridException(errmsg, e);
                }
                final List<GridNode> replicatedNodes = collector.getMatched();
                if(replicatedNodes == null || replicatedNodes.isEmpty()) {
                    throw new GridException("No replicated document found for path: " + path);
                }
                // TODO Select a node that least recently used for write requests.
                GridNode node = nodeSelector.selectNode(replicatedNodes, config);
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
            DispatchQueryExecTask dispatchTask = new DispatchQueryExecTask(execJob, _bindingVar, _bodyExpr, _exprBytes, mappedPaths, true);
            map.put(dispatchTask, node);
        }
        if(!localExecResources.isEmpty()) {
            boolean doForwarding = _redirectable;
            LocalQueryExecTask localTask = new LocalQueryExecTask(execJob, _bindingVar, _bodyExpr, localExecResources, doForwarding);
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
        final GridNode[] liveNodes = router.getAllNodes();
        return GridUtils.selectSuperNodes(liveNodes);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        // Write out and any hidden stuff
        s.defaultWriteObject();

        s.writeInt(_exprBytes.length);
        s.write(_exprBytes);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        // Read in any hidden stuff
        s.defaultReadObject();

        final int len = s.readInt();
        final byte[] b = new byte[len];
        s.readFully(b, 0, len);

        FastByteArrayInputStream bis = new FastByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bis);
        this._bodyExpr = (XQExpression) ois.readObject();
        this._bindingVar = (BindingVariable) ois.readObject();

        this._exprBytes = b;
    }

    public static byte[] toBytes(final BindingVariable bindingVar, final XQExpression bodyExpr) {
        final FastByteArrayOutputStream bos = new FastByteArrayOutputStream(8192);
        try {
            final ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(bodyExpr);
            oos.writeObject(bindingVar);
            oos.flush();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
        return bos.toByteArray();
    }
}
