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

import gridool.GridException;
import gridool.GridJob;
import gridool.construct.GridTaskAdapter;

import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.engine.remote.RemoteSequence;
import xbird.engine.remote.ThrottedRemoteSequenceProxy;
import xbird.storage.DbCollection;
import xbird.util.io.FileUtils;
import xbird.util.lang.ClassUtils;
import xbird.util.net.TimeoutSocketProdiver;
import xbird.util.struct.Pair;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.dtm.IDocumentTable;
import xbird.xquery.dm.dtm.LazyDTMDocument;
import xbird.xquery.dm.instance.DocumentTableModel.DTMDocument;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.IncrEvalSequence;
import xbird.xquery.dm.value.sequence.MarshalledSequence;
import xbird.xquery.dm.value.sequence.NodeSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.meta.DynamicContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class LocalQueryExecTask extends GridTaskAdapter {
    private static final long serialVersionUID = -1356142633700786830L;
    private static final Log LOG = LogFactory.getLog(LocalQueryExecTask.class);
    private static final boolean queryShipping = !Boolean.getBoolean("gridool.query.data_shipping");
    private static final int INCR_EVAL_STOCK_SIZE = 512;

    private final BindingVariable bindingVar;
    private final XQExpression bodyExpr;
    private final List<Pair<String, Lock>> relativePaths;
    private final boolean forwardingEnabled;

    @SuppressWarnings("unchecked")
    public LocalQueryExecTask(GridJob job, BindingVariable bindingVar, XQExpression bodyExpr, List<Pair<String, Lock>> relativePaths, boolean doForwarding) {
        super(job, false);
        this.bindingVar = bindingVar;
        this.bodyExpr = bodyExpr;
        this.relativePaths = relativePaths;
        this.forwardingEnabled = doForwarding;
    }

    @Override
    public boolean isAsyncTask() {
        return false;
    }

    public Serializable execute() throws GridException {
        NodeSequence<DTMDocument> docs = listDocuments(relativePaths);
        assert (docs != null);
        bindingVar.allocateResult(docs, DynamicContext.DUMMY);

        final Sequence<? extends Item> result;
        try {
            result = bodyExpr.eval(ValueSequence.EMPTY_SEQUENCE, DynamicContext.DUMMY);
        } catch (XQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new GridException(e);
        }
        return wrapResult(result, this, forwardingEnabled, queryShipping);
    }

    public static NodeSequence<DTMDocument> listDocuments(List<Pair<String, Lock>> relativePaths)
            throws GridException {
        final List<DTMDocument> docList = new ArrayList<DTMDocument>(relativePaths.size());
        final Map<String, List<Pair<String, Lock>>> cfmap = mapFilesToCollections(relativePaths);

        for(Map.Entry<String, List<Pair<String, Lock>>> e : cfmap.entrySet()) {
            final String colpath = e.getKey();
            final List<Pair<String, Lock>> fileResources = e.getValue();

            DbCollection col = DbCollection.getCollection(colpath);
            File colDir = col.getDirectory();
            if(!colDir.exists()) {
                throw new GridException("Collection not found: " + colpath);
            }

            for(Pair<String, Lock> rsc : fileResources) {
                String fname = rsc.getFirst();
                File docFile = new File(colDir, fname + IDocumentTable.DTM_SEGMENT_FILE_SUFFIX);
                if(!docFile.exists()) {
                    throw new GridException("Document not found: " + docFile.getAbsolutePath());
                }
                LazyDTMDocument doc = new LazyDTMDocument(fname, col, DynamicContext.DUMMY);
                Lock lock = rsc.getSecond();
                assert (lock != null);
                doc.setLock(lock);
                docList.add(doc);
            }
        }
        return new NodeSequence<DTMDocument>(docList, DynamicContext.DUMMY);
    }

    private static Map<String, List<Pair<String, Lock>>> mapFilesToCollections(List<Pair<String, Lock>> paths) {
        if(paths == null) {
            throw new IllegalStateException();
        }
        final Map<String, List<Pair<String, Lock>>> map = new HashMap<String, List<Pair<String, Lock>>>();
        for(Pair<String, Lock> rsc : paths) {
            String path = rsc.getFirst();
            String colPath = FileUtils.dirName(path, '/');
            List<Pair<String, Lock>> files = map.get(colPath);
            if(files == null) {
                files = new ArrayList<Pair<String, Lock>>(12);
                map.put(colPath, files);
            }
            String fileName = FileUtils.basename(path);
            rsc.setFirst(fileName);
            files.add(rsc);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static Sequence wrapResult(final Sequence result, final LocalQueryExecTask task, final boolean doForwarding, final boolean queryShipping) {
        if(doForwarding) {
            if(queryShipping) {// TODO take CPU usage into consideration
                if(LOG.isInfoEnabled()) {
                    LOG.info("Response method for a task [" + task.getTaskId()
                            + "]: QueryShipping [type:" + ClassUtils.getSimpleClassName(result)
                            + ']');
                }
                final ThrottedRemoteSequenceProxy remote = new ThrottedRemoteSequenceProxy(result);
                try {
                    UnicastRemoteObject.exportObject(remote, 0, TimeoutSocketProdiver.createClientSocketFactory(), TimeoutSocketProdiver.createServerSocketFactory());
                } catch (RemoteException e) {
                    LOG.error(e.getMessage(), e);
                    throw new IllegalStateException("failed exporting result sequence", e);
                }
                final RemoteSequence ret = new RemoteSequence(remote, result.getType());
                final Thread th = new Thread(remote, "LocalQueryTask[" + task.getTaskId()
                        + "]#AsyncQueryProcessor(RemoteSequence)");
                th.start();
                return ret;
            } else {
                if(LOG.isInfoEnabled()) {
                    LOG.info("Response method for a task [" + task.getTaskId()
                            + "]: DataShipping [type:" + ClassUtils.getSimpleClassName(result)
                            + ']');
                }
                final MarshalledSequence seq = new MarshalledSequence(result, DynamicContext.DUMMY);
                seq.setRemotePaging(true);
                return seq;
            }
        } else {
            if(LOG.isInfoEnabled()) {
                LOG.info("Response method for a task [" + task.getTaskId() + "]: IncrEvalSequence");
            }
            final IncrEvalSequence seq = new IncrEvalSequence(result, INCR_EVAL_STOCK_SIZE, DynamicContext.DUMMY);
            final Thread th = new Thread(seq, "LocalQueryTask[" + task.getTaskId()
                    + "]#AsyncQueryProcessor(IncrEvalSequence)");
            th.start();
            /*
            final EncodedSequence seq = new EncodedSequence(result, DynamicContext.DUMMY);
            try {
                seq.doEncode();
            } catch (IOException e) {
                String errmsg = "failed encoding a task result: " + task.getTaskId();
                LOG.error(errmsg);
                throw new IllegalStateException(errmsg, e);
            }
            */
            return seq;
        }
    }
}
