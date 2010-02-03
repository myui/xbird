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
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gridool.GridConfiguration;
import gridool.GridException;
import gridool.GridNode;
import gridool.GridTask;
import gridool.GridTaskResult;
import gridool.GridTaskResultPolicy;
import gridool.annotation.DirectoryResource;
import gridool.annotation.GridConfigResource;
import gridool.construct.GridJobBase;
import gridool.directory.LocalDirectory;
import gridool.routing.GridNodeSelector;
import gridool.routing.GridTaskRouter;
import xbird.storage.DbException;
import xbird.util.collections.CollectionUtils;
import xbird.util.math.MathUtils;
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

    // ======================================
    // injected resources

    @GridConfigResource
    private transient GridConfiguration config;
    @DirectoryResource
    private transient LocalDirectory directory;

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

    @Override
    public Map<GridTask, GridNode> map(GridTaskRouter router, MapExpr mapExpr) throws GridException {
        checkInjectedResouces();

        final String colPath = mapExpr.getCollectionPath();
        final GridNode[] liveNodes = router.getAllNodes();
        final int numLiveNodes = liveNodes.length;
        if(numLiveNodes == 0) {// no live node
            return Collections.emptyMap();
        }

        final Set<GridNode> mapableNodes = CollectionUtils.asSet(liveNodes);
        final Map<String, List<GridNode>> mapping;
        try {
            mapping = directory.prefixSearch(colPath, mapableNodes);
        } catch (DbException e) {
            throw new GridException("prefixSearch failed: " + colPath, e);
        }
        if(mapping.isEmpty()) {// no mapping found
            return Collections.emptyMap();
        }

        final BindingVariable bindingVar = mapExpr.getBindingVariable();
        final XQExpression bodyExpr = mapExpr.getBodyExpression();
        final GridNodeSelector selector = config.getNodeSelector();
        assert (selector != null);

        final Map<GridNode, List<String>> nodeKeysMap = new HashMap<GridNode, List<String>>(numLiveNodes);
        final Map<GridTask, GridNode> map = new IdentityHashMap<GridTask, GridNode>(numLiveNodes);

        for(Map.Entry<String, List<GridNode>> entry : mapping.entrySet()) {
            final String key = entry.getKey();
            final List<GridNode> candidateNodes = entry.getValue();
            assert (!candidateNodes.isEmpty());

            final GridNode node = selector.selectNode(candidateNodes, null, config);
            candidateNodes.clear(); // let GC do its work

            List<String> mappedKeys = nodeKeysMap.get(node);
            if(mappedKeys == null) {
                mappedKeys = new ArrayList<String>(128);
                nodeKeysMap.put(node, mappedKeys);

                // TODO REVIEWME
                DispatchQueryExecTask task = new DispatchQueryExecTask(this, bindingVar, bodyExpr, mappedKeys, false);
                map.put(task, node);
            }
            mappedKeys.add(key);
        }

        if(LOG.isInfoEnabled()) {
            logJobInformation(map, getJobId());
        }

        this._results = new ArrayList<Sequence<? extends Item>>(map.size());
        return map;
    }

    @Override
    public GridTaskResultPolicy result(GridTask task, GridTaskResult result) throws GridException {
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
    @Override
    public Sequence<? extends Item> reduce() throws GridException {
        final int size = _results.size();
        if(size == 0) {
            return ValueSequence.emptySequence();
        } else if(size == 1) {
            Sequence<? extends Item> res = _results.get(0);
            return wrapResult(res);
        } else {
            PipelinedSequence piped = new PipelinedSequence(DynamicContext.DUMMY, _results);
            return wrapResult(piped);
        }
    }

    private static Sequence<? extends Item> wrapResult(final Sequence<? extends Item> seq) {
        final MarshalledSequence res = new MarshalledSequence(seq, DynamicContext.DUMMY);
        res.setRedirectable(true);
        return res;
    }

    private static void logJobInformation(final Map<GridTask, GridNode> map, final String jobId) {
        LOG.info("Mapped job [" + jobId + "] to " + map.size() + " nodes");
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
