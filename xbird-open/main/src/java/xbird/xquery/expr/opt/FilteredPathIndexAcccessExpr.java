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
import java.util.List;

import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.storage.index.BIndexFile;
import xbird.storage.indexer.IndexQuery;
import xbird.util.codec.UTF8Codec;
import xbird.util.struct.Pair;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.labeling.LabelingHandler;
import xbird.xquery.dm.value.*;
import xbird.xquery.expr.var.BindingVariable.ForVariable;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class FilteredPathIndexAcccessExpr extends PathIndexAccessExpr {
    private static final long serialVersionUID = 533824636656662913L;

    private final ForVariable _forVariable;

    public FilteredPathIndexAcccessExpr(RewriteInfo info, Type type) {
        super(info, type);
        ForVariable fv = info.getForVariable();
        if(fv == null) {
            throw new IllegalStateException("for variable is not set.. bug?");
        }
        this._forVariable = fv;
    }

    @Override
    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final IndexQuery idxCond = getIndexCond();
        final List<Pair<DbCollection, String>> lst = _accessInfo.listDocumentsInfo();
        final long rowid = getAncestorRowID(_forVariable, contextSeq, dynEnv);
        final DecendantFilteredSequence filtered = new DecendantFilteredSequence(rowid, dynEnv, idxCond, lst, _type);
        return filtered;
    }

    private static long getAncestorRowID(ForVariable forVariable, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence result = forVariable.loadResult(contextSeq, dynEnv);
        if(!(result instanceof XQNode)) {
            throw new IllegalStateException("Unexpected ForVariable $" + forVariable.getName()
                    + " value: " + result);
        }
        final XQNode node = (XQNode) result;
        final long rowid = node.getPosition();
        if(rowid == -1) {
            throw new IllegalStateException("Unexpected RowID: " + rowid);
        }
        return rowid;
    }

    private static final class DecendantFilteredSequence extends IndexMatchedSequence {
        private static final long serialVersionUID = -2529479518674792458L;
        private static final boolean USE_BIN_FILTER = System.getProperty("xbird.disable_binfilter") != null;
        private final long ancestorRowId;

        public DecendantFilteredSequence(long ancestorRowId, DynamicContext dynEnv, IndexQuery idxCond, List<Pair<DbCollection, String>> lst, Type type) {
            super(dynEnv, idxCond, lst, type);
            this.ancestorRowId = ancestorRowId;
        }

        @Override
        protected long[] filter(long[] ptrs, String docName, File idxFile) throws XQueryException {
            final int ptrslen = ptrs.length;
            if(ptrslen == 0) {
                return ptrs;
            }
            final BIndexFile labelBFile = getLabelIndex(docName, idxFile);
            final byte[] ancestorLabel = retrieveLabel(labelBFile, ancestorRowId);
            if(ancestorLabel == null) {
                throw new IllegalStateException("label is not registed for ancestor rowID#: "
                        + ancestorRowId);
            }
            final byte[] firstTarget = retrieveLabel(labelBFile, ptrs[0]);
            if(!isDescendantOf(firstTarget, ancestorLabel)) {
                return new long[0];
            }
            if(ptrslen > 1) {
                final int lastIdx = ptrslen - 1;
                final byte[] lastTarget = retrieveLabel(labelBFile, ptrs[lastIdx]);
                if(isDescendantOf(lastTarget, ancestorLabel)) {//optimization
                    return ptrs;
                }
                if(USE_BIN_FILTER) {
                    // calculate next-sibling of ancestorLabel
                    final int[] ancestors = UTF8Codec.decode(ancestorLabel);
                    ancestors[ancestors.length - 1] += 1;
                    final byte[] sib = UTF8Codec.encode(ancestors);
                    final int left = binarySearch(ptrs, 1, ancestorLabel, labelBFile);
                    final int right = binarySearch(ptrs, left, sib, labelBFile);
                    final int size = right - left;
                    final long[] newPtrs = new long[size];
                    System.arraycopy(ptrs, left, newPtrs, 0, size);
                    return newPtrs;
                } else {
                    for(int i = 1; i < lastIdx; i++) {
                        final byte[] targetLabel = retrieveLabel(labelBFile, ptrs[i]);
                        if(targetLabel == null) {
                            throw new IllegalStateException("label is not registted for rowID#: "
                                    + ptrs[i]);
                        }
                        if(!isDescendantOf(targetLabel, ancestorLabel)) {
                            final int size = i - 1;
                            final long[] newPtrs = new long[size];
                            System.arraycopy(ptrs, 0, newPtrs, 0, newPtrs.length);
                            return newPtrs;
                        }
                    }
                }
            }
            return ptrs;
        }

        private static int binarySearch(final long[] ptrs, final int from, final byte[] ancKey, final BIndexFile labelBFile)
                throws XQueryException {
            int low = from;
            int high = ptrs.length - 2;
            while(low <= high) {
                final int mid = (low + high) >>> 1;
                final byte[] midKey = retrieveLabel(labelBFile, ptrs[mid]);
                if(midKey == null) {
                    throw new IllegalStateException("label is not registted for rowID#: "
                            + ptrs[mid]);
                }
                final int cmp = startsWith(midKey, ancKey);
                if(cmp < 0) {
                    low = mid + 1;
                } else if(cmp > 0) {
                    high = mid - 1;
                } else {
                    return mid; // key found
                }
            }
            return low; // key not found.
        }

        private static final byte[] retrieveLabel(final BIndexFile labelBFile, final long rowId)
                throws XQueryException {
            try {
                return labelBFile.getValueBytes(rowId);
            } catch (DbException e) {
                throw new XQueryException("failed to lookup RowID value: " + rowId, e);
            }
        }

        private static BIndexFile getLabelIndex(String docName, File idxFile)
                throws XQueryException {
            File idxDir = idxFile.getParentFile();
            File labelFile = new File(idxDir, docName + LabelingHandler.LABEL_FILE_SUFFIX);
            if(!labelFile.exists()) {
                throw new IllegalStateException("label idx file does not exist: "
                        + labelFile.getAbsolutePath());
            }
            BIndexFile labelBFile = new BIndexFile(labelFile);
            try {
                labelBFile.open();
            } catch (DbException e) {
                throw new XQueryException("failed to open BFile: " + labelFile.getAbsolutePath(), e);
            }
            return labelBFile;
        }

        private static boolean isDescendantOf(byte[] target, byte[] ancestor) {
            return startsWith(target, ancestor) >= 0;
        }

        private static int startsWith(final byte[] target, final byte[] base) {
            final int trglen = target.length;
            final int baselen = base.length;
            if(trglen < baselen) {
                return -1;
            }
            for(int i = 0; i < baselen; i++) {
                if(target[i] != base[i]) {
                    return -1;
                }
            }
            return trglen - baselen;
        }
    }

}
