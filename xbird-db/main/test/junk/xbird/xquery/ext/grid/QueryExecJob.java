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
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gridool.GridException;
import gridool.GridNode;
import gridool.GridTask;
import gridool.GridTaskResult;
import gridool.GridTaskResultPolicy;
import gridool.construct.GridJobBase;
import gridool.routing.GridTaskRouter;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.MarshalledSequence;
import xbird.xquery.dm.value.sequence.PipelinedSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.meta.DynamicContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class QueryExecJob extends
        GridJobBase<DispatchQueryExecTask, Sequence<? extends Item>> {
    private static final long serialVersionUID = 3842674427253905437L;
    private static final Log LOG = LogFactory.getLog(QueryExecJob.class);

    @Nullable
    private transient List<Sequence<? extends Item>> results = null;
    private boolean isRedirectable;

    public QueryExecJob() {}

    @Override
    public Map<GridTask, GridNode> map(GridTaskRouter router, DispatchQueryExecTask task)
            throws GridException {
        final Map<GridTask, GridNode> tasks = task.mapQueryTask(this);
        this.results = new ArrayList<Sequence<? extends Item>>(tasks.size());
        this.isRedirectable = task.isRedirectable();
        return tasks;
    }

    @Override
    public GridTaskResultPolicy result(GridTask task, GridTaskResult result) throws GridException {
        if(results == null) {
            throw new IllegalStateException();
        }
        final Sequence<? extends Item> resultSeq = result.getResult();
        if(resultSeq == null) {
            final Exception error = result.getException();
            if(error != null) {
                LOG.error(error.getMessage());
            }
            return GridTaskResultPolicy.FAILOVER;
        } else {
            results.add(resultSeq);
            return GridTaskResultPolicy.CONTINUE;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Sequence<? extends Item> reduce() throws GridException {
        final int size = results.size();
        if(size == 0) {
            return ValueSequence.emptySequence();
        } else if(size == 1) {
            Sequence<? extends Item> seq = results.get(0);
            return wrapResult(seq, isRedirectable, this);
        } else {
            PipelinedSequence piped = new PipelinedSequence(DynamicContext.DUMMY, results);
            return wrapResult(piped, isRedirectable, this);
        }
    }

    private static Sequence<? extends Item> wrapResult(final Sequence<? extends Item> seq, final boolean isRedirectable, final QueryExecJob job) {
        final MarshalledSequence res = new MarshalledSequence(seq, DynamicContext.DUMMY);
        res.setRedirectable(isRedirectable);
        if(LOG.isInfoEnabled()) {
            LOG.info("Response of a job [" + job.getJobId() + "] is "
                    + (isRedirectable ? "redirected" : "not redirected"));
        }
        return res;
    }

}
