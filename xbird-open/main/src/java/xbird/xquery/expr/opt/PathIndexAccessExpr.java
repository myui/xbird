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
package xbird.xquery.expr.opt;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.storage.index.Value;
import xbird.storage.indexer.BTreeIndexer;
import xbird.storage.indexer.BasicIndexQuery;
import xbird.storage.indexer.ByteLikeIndexQuery;
import xbird.storage.indexer.IndexMatch;
import xbird.storage.indexer.IndexQuery;
import xbird.util.collections.LongQueue;
import xbird.util.io.IOUtils;
import xbird.util.lang.ArrayUtils;
import xbird.util.struct.Pair;
import xbird.xquery.XQRTException;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.dtm.DocumentTableLoader;
import xbird.xquery.dm.dtm.IDocumentTable;
import xbird.xquery.dm.dtm.PagingProfile;
import xbird.xquery.dm.dtm.PagingProfile.Strategy;
import xbird.xquery.dm.instance.DocumentTableModel;
import xbird.xquery.dm.labeling.LabelingHandler;
import xbird.xquery.dm.labeling.RevPathCoder;
import xbird.xquery.dm.value.AbstractSequence;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.XQNode;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.Focus;
import xbird.xquery.meta.IFocus;
import xbird.xquery.meta.Profiler;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class PathIndexAccessExpr extends AbstractXQExpression {
    private static final Log LOG = LogFactory.getLog(PathIndexAccessExpr.class);
    private static final long serialVersionUID = -2778204278710428250L;

    protected final RewriteInfo _accessInfo;
    private final String _accessPath;
    private final byte[] _query;

    public PathIndexAccessExpr(RewriteInfo info, Type type) {
        super();
        this._accessInfo = info;
        byte[] revpath = info.getRewrittedPath();
        this._query = revpath;
        this._accessPath = info.toString(revpath);
        this._type = type;
        this._analyzed = true;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        return this;
    }

    @Override
    public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info) {
        info.succeeds(_accessInfo);
        return true;
    }

    @Override
    public String toString() {
        return _accessPath;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final IndexQuery idxCond = getIndexCond();
        final List<Pair<DbCollection, String>> lst = _accessInfo.listDocumentsInfo();
        final IndexMatchedSequence seq = new IndexMatchedSequence(dynEnv, idxCond, lst, _type);
        return seq;
    }

    protected IndexQuery getIndexCond() {
        final IndexQuery idxCond;
        final byte[] query = _query;
        final int percentidx = ArrayUtils.indexOf(query, RevPathCoder.PERCENT_CODE, 0);
        if(percentidx < 0) {
            final Value key = new Value(query);
            idxCond = new BasicIndexQuery.IndexConditionEQ(key);
        } else {
            final byte[] keyStr = ArrayUtils.copyOfRange(query, 0, percentidx);
            final byte[] filter = ArrayUtils.copyOfRange(query, percentidx, query.length);
            final Value key = new Value(keyStr);
            idxCond = new ByteLikeIndexQuery(key, filter, RevPathCoder.PERCENT_CODE);
        }
        return idxCond;
    }

    public static class IndexMatchedSequence extends AbstractSequence<XQNode> {
        private static final long serialVersionUID = 911097990361708811L;

        private final IndexQuery idxCond;
        private final List<Pair<DbCollection, String>> lst;
        private final Type type;

        public IndexMatchedSequence(DynamicContext dynEnv, IndexQuery idxCond, List<Pair<DbCollection, String>> lst, Type type) {
            super(dynEnv);
            this.idxCond = idxCond;
            this.lst = lst;
            this.type = type;
        }

        @Override
        public final boolean isEmpty() {
            return super.isEmpty();
        }

        @Override
        public final IFocus<XQNode> iterator() {
            final Iterator<Pair<DbCollection, String>> itor = lst.iterator();
            final IndexMatchFocus resultFocus = new IndexMatchFocus(this, _dynEnv, itor);
            return resultFocus;
        }

        public final boolean next(IFocus<XQNode> focus) throws XQueryException {
            if(focus.reachedEnd()) {
                return false;
            }
            IndexMatchFocus ffcous = (IndexMatchFocus) focus;
            LongQueue pendings = ffcous.getPtrsQueue();
            outer: if(pendings != null && !pendings.isEmpty()) {
                long ptr = pendings.dequeue();
                while(ptr == -1L) {
                    if(pendings.isEmpty()) {
                        break outer;
                    }
                    ptr = pendings.dequeue();
                }
                DocumentTableModel dtm = ffcous.getDocumentTableModel();
                XQNode node = dtm.createNode(ptr);
                ffcous.setContextItem(node);
                return true;
            }
            final Profiler profiler = _dynEnv.getProfiler();
            final Iterator<Pair<DbCollection, String>> itor = ffcous.eachDocument();
            while(itor.hasNext()) {
                final Pair<DbCollection, String> pair = itor.next();
                final DbCollection col = pair.getFirst();
                final String docName = pair.getSecond();
                final File idxFile = getIndexFile(col, docName);
                BTreeIndexer indexer = new BTreeIndexer(idxFile);
                final IndexMatch matched;
                try {
                    matched = indexer.find(idxCond);
                } catch (DbException e) {
                    throw new XQRTException("failed to query index: " + idxFile.getAbsolutePath(), e);
                }
                // TODO REVIEWME sort really required?
                final long[] ptrs = filter(matched.getMatchedUnsorted(), docName, idxFile);
                final int matchCounts = ptrs.length;
                if(LOG.isInfoEnabled()) {
                    LOG.info("Index scan done. matched: " + matched.countMatched() + ", filtered: "
                            + matchCounts);
                }
                if(matchCounts > 0) {
                    final IDocumentTable doctbl;
                    try {
                        doctbl = DocumentTableLoader.load(col, docName, _dynEnv);
                    } catch (IOException e) {
                        throw new XQRTException("failed to load document '" + docName
                                + "' is the collection '" + col.getAbsolutePath() + '\'', e);
                    }
                    final PagingProfile profile = doctbl.getPagingProfile();
                    Strategy origStrategy = null;
                    if(profile != null) {
                        profile.setProfiler(profiler);
                        origStrategy = profile.getStrategy();
                        profile.setStrategy(Strategy.index);
                    }
                    DocumentTableModel dtm = new DocumentTableModel(doctbl, true);
                    final int last = matchCounts - 1;
                    for(int i = 0; i <= last; i++) {
                        long ptr = ptrs[i];
                        if(ptr != -1) {
                            XQNode node = dtm.createNode(ptr);
                            ffcous.setContextItem(node);
                            if(i != last) {
                                LongQueue ptrsQueue = new LongQueue(ptrs, i + 1, last);
                                ffcous.enqueue(dtm, ptrsQueue);
                            }
                            if(profile != null) {
                                profile.setStrategy(origStrategy);
                            }
                            return true;
                        }
                    }
                    if(profile != null) {
                        profile.setStrategy(origStrategy);
                    }
                    return false;
                }
            }
            ffcous.setReachedEnd(true);
            ffcous.clear();
            return false;
        }

        public final int totalCount() throws XQueryException {
            for(Pair<DbCollection, String> pair : lst) {
                final DbCollection col = pair.getFirst();
                final String docName = pair.getSecond();
                final File idxFile = getIndexFile(col, docName);
                BTreeIndexer indexer = new BTreeIndexer(idxFile);
                final IndexMatch matched;
                try {
                    matched = indexer.find(idxCond);
                } catch (DbException e) {
                    throw new XQRTException("failed to query index: " + idxFile.getAbsolutePath(), e);
                }
                final long[] ptrs = filter(matched.getMatchedUnsorted(), docName, idxFile);
                final int matchCounts = ptrs.length;
                if(LOG.isInfoEnabled()) {
                    LOG.info("Index scan done. matched: " + matched.countMatched() + ", filtered: "
                            + matchCounts);
                }
                return matchCounts;
            }
            return 0;
        }

        protected long[] filter(long[] ptrs, String docName, File idxFile) throws XQueryException {
            return ptrs;
        }

        public Type getType() {
            return type;
        }

        private static File getIndexFile(final DbCollection col, final String docName) {
            File idxDir = new File(col.getDirectory(), LabelingHandler.INDEX_DIR_NAME);
            if(!idxDir.exists()) {
                throw new IllegalStateException("Index dir does not exist: "
                        + idxDir.getAbsolutePath());
            }
            File idxFile = new File(idxDir, docName + LabelingHandler.PATHS_FILE_SUFFIX);
            if(!idxFile.exists()) {
                throw new IllegalStateException("Path Index file does not exist: "
                        + idxFile.getAbsolutePath());
            }
            return idxFile;
        }
    }

    private static final class IndexMatchFocus extends Focus<XQNode> {
        private static final long serialVersionUID = -2814427667735216757L;

        private final Iterator<Pair<DbCollection, String>> itor;

        private DocumentTableModel dtm = null;
        private LongQueue ptrsQueue = null;

        public IndexMatchFocus(Sequence<XQNode> src, DynamicContext dynEnv, Iterator<Pair<DbCollection, String>> itor) {
            super(src, dynEnv);
            this.itor = itor;
        }

        public Iterator<Pair<DbCollection, String>> eachDocument() {
            return itor;
        }

        public void enqueue(DocumentTableModel dtm, LongQueue ptrsQueue) {
            this.dtm = dtm;
            this.ptrsQueue = ptrsQueue;
        }

        public LongQueue getPtrsQueue() {
            return ptrsQueue;
        }

        public DocumentTableModel getDocumentTableModel() {
            return dtm;
        }

        public void clear() {
            IOUtils.closeQuietly(dtm.getDocumentTable());
            this.dtm = null;
            this.ptrsQueue = null;
        }

    }

}
