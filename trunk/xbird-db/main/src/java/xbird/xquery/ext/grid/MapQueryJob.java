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
import gridool.GridNode;
import gridool.GridTask;
import gridool.GridTaskResult;
import gridool.GridTaskResultPolicy;
import gridool.annotation.GridConfigResource;
import gridool.annotation.GridDirectoryResource;
import gridool.construct.GridJobBase;
import gridool.directory.ILocalDirectory;
import gridool.directory.helpers.GridNodeKeyValueCollector;
import gridool.routing.GridNodeSelector;
import gridool.routing.GridTaskRouter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.storage.DbException;
import xbird.util.collections.CollectionUtils;
import xbird.util.datetime.TextProgressBar;
import xbird.util.lang.ClassUtils;
import xbird.util.math.MathUtils;
import xbird.util.string.StringUtils;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.MarshalledSequence;
import xbird.xquery.dm.value.sequence.PipelinedSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.ext.MapExpr;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.meta.DynamicContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class MapQueryJob extends GridJobBase<MapExpr, Sequence<? extends Item>> {
    private static final long serialVersionUID = -2810696094868588963L;
    private static final Log LOG = LogFactory.getLog(MapQueryJob.class);

    @Nullable
    private transient List<Sequence<? extends Item>> _results;
    @Nullable
    private transient TextProgressBar _progressBar = null;

    // ======================================
    // injected resources

    @GridConfigResource
    private transient GridConfiguration config;
    @GridDirectoryResource
    private transient ILocalDirectory directory;

    // ======================================

    public MapQueryJob() {
        super();
        this._results = Collections.emptyList();
    }

    @Override
    public boolean injectResources() {
        return true;
    }

    private void checkInjectedResouces() {
        if(config == null) {
            throw new IllegalStateException("GridConfiguration is not injected");
        }
        if(directory == null) {
            throw new IllegalStateException("LocalDirectory is not injected");
        }
    }

    public Map<GridTask, GridNode> map(GridTaskRouter router, MapExpr mapExpr) throws GridException {
        checkInjectedResouces();

        final String colPath = mapExpr.getCollectionPath();
        final GridNode[] liveNodes = router.getAllNodes();
        final int numLiveNodes = liveNodes.length;
        if(numLiveNodes == 0) {// no live node
            return Collections.emptyMap();
        }

        final byte[] k = StringUtils.getBytes(colPath);
        Set<GridNode> mapableNodes = CollectionUtils.asSet(liveNodes);
        final GridNodeKeyValueCollector collector = new GridNodeKeyValueCollector(mapableNodes);
        try {
            directory.prefixSearch(k, collector);
        } catch (DbException e) {
            String errmsg = "prefixSearch failed: " + colPath;
            LOG.error(errmsg);
            throw new GridException(errmsg, e);
        }
        final Map<String, List<GridNode>> mapping = collector.getMapping();
        if(mapping.isEmpty()) {// no mapping found
            return Collections.emptyMap();
        }

        final BindingVariable bindingVar = mapExpr.getBindingVariable();
        final XQExpression bodyExpr = mapExpr.getBodyExpression();
        final byte[] exprBytes = DispatchQueryExecTask.toBytes(bindingVar, bodyExpr);

        final GridNodeSelector selector = config.getNodeSelector();
        assert (selector != null);

        final Map<GridNode, List<String>> nodeKeysMap = new HashMap<GridNode, List<String>>(numLiveNodes);
        final Map<GridTask, GridNode> map = new IdentityHashMap<GridTask, GridNode>(numLiveNodes);

        for(Map.Entry<String, List<GridNode>> entry : mapping.entrySet()) {
            final String key = entry.getKey();
            final List<GridNode> candidateNodes = entry.getValue();
            assert (!candidateNodes.isEmpty());

            final GridNode node = selector.selectNode(candidateNodes, config);
            candidateNodes.clear(); // let GC do its work

            List<String> mappedKeys = nodeKeysMap.get(node);
            if(mappedKeys == null) {
                mappedKeys = new ArrayList<String>(128);
                nodeKeysMap.put(node, mappedKeys);

                // TODO REVIEWME
                DispatchQueryExecTask task = new DispatchQueryExecTask(this, bindingVar, bodyExpr, exprBytes, mappedKeys, false);
                map.put(task, node);
            }
            mappedKeys.add(key);
        }

        if(LOG.isInfoEnabled()) {
            logJobInformation(map, getJobId());
        }

        final int numTasks = map.size();
        this._results = new ArrayList<Sequence<? extends Item>>(numTasks);
        this._progressBar = new TextProgressBar("MapQueryJob [" + getJobId() + ']', numTasks) {
            protected void show() {
                if(LOG.isInfoEnabled()) {
                    LOG.info(getInfo());
                }
            }
        };
        _progressBar.setRefreshFluctations(10);
        return map;
    }

    public GridTaskResultPolicy result(GridTask task, GridTaskResult result) throws GridException {
        if(LOG.isInfoEnabled()) {
            if(LOG.isTraceEnabled()) {
                LOG.trace("GridTask [" + result.getTaskId() + "] is returned");
            }
            if(_progressBar != null) {
                _progressBar.inc();
            }
        }
        final Sequence<? extends Item> resultSeq = result.getResult();
        if(resultSeq == null) {
            final Exception error = result.getException();
            if(error != null) {
                LOG.error(error.getMessage());
            }
            return GridTaskResultPolicy.FAILOVER;
        } else {
            _results.add(resultSeq);
            return GridTaskResultPolicy.CONTINUE;
        }
    }

    @SuppressWarnings("unchecked")
    public Sequence<? extends Item> reduce() throws GridException {
        if(LOG.isInfoEnabled() && _progressBar != null) {
            _progressBar.finish();
        }
        final int size = _results.size();
        if(size == 0) {
            return ValueSequence.emptySequence();
        } else if(size == 1) {
            Sequence<? extends Item> res = _results.get(0);
            String jobId = getJobId();
            return wrapResult(res, jobId);
        } else {
            PipelinedSequence piped = new PipelinedSequence(DynamicContext.DUMMY, _results);
            String jobId = getJobId();
            return wrapResult(piped, jobId);
        }
    }

    private static Sequence<? extends Item> wrapResult(final Sequence<? extends Item> seq, final String jobId) {
        if(LOG.isInfoEnabled()) {
            LOG.info("Response [type:" + ClassUtils.getSimpleClassName(seq) + "] of a job ["
                    + jobId + "] is redirected");
        }
        if(seq instanceof MarshalledSequence) {
            if(((MarshalledSequence) seq).isRedirectable()) {
                return seq;
            }
        }
        final MarshalledSequence res = new MarshalledSequence(seq, DynamicContext.DUMMY);
        res.setRedirectable(true);
        return res;
    }

    private static void logJobInformation(final Map<GridTask, GridNode> map, final String jobId) {
        if(LOG.isInfoEnabled()) {
            LOG.info("Mapped job [" + jobId + "] to " + map.size() + " nodes");
        }
        if(LOG.isDebugEnabled()) {
            final int[] numJobs = new int[map.size()];
            int i = 0;
            for(Map.Entry<GridTask, GridNode> e : map.entrySet()) {
                DispatchQueryExecTask task = (DispatchQueryExecTask) e.getKey();
                numJobs[i++] = task.getRelativePaths().size();
            }
            float stddev = MathUtils.stddev(numJobs);
            LOG.debug("Standard deviation of the mapped task size for " + jobId + ": " + stddev);
        }
    }

}
