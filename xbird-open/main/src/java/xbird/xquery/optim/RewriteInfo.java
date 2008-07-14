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
package xbird.xquery.optim;

import java.io.File;
import java.util.*;

import xbird.storage.DbCollection;
import xbird.storage.DbCollection.Symbols;
import xbird.util.io.FileUtils;
import xbird.util.io.FileUtils.IOFileFilter;
import xbird.util.lang.ObjectUtils;
import xbird.util.struct.Pair;
import xbird.xquery.dm.dtm.IDocumentTable;
import xbird.xquery.dm.labeling.LabelingHandler;
import xbird.xquery.dm.labeling.RevPathCoder;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.path.axis.*;
import xbird.xquery.expr.var.BindingVariable.ForVariable;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RewriteInfo {

    private RevPathCoder coder = new RevPathCoder();
    private StringBuilder original = new StringBuilder(128);
    private boolean prevDescOrSelf = false;

    // collection infomation
    private DbCollection collection;
    private String colFilter;
    private QNameTable qnameTable;

    private ForVariable forVariable = null;

    public RewriteInfo() {}

    public boolean hasPreviousStep() {
        return collection != null;
    }

    public void setCollection(DbCollection col, String filter) {
        if(col == null) {
            throw new IllegalArgumentException("argument 'col' is null");
        }
        if(collection != null) {
            throw new IllegalStateException("Collection already set: "
                    + collection.getCollectionName());
        }
        this.collection = col;
        this.colFilter = filter;
        Symbols symbol = col.getSymbols();
        if(symbol == null) {
            throw new IllegalStateException("Symbol is not set for the collection: "
                    + collection.getCollectionName());
        }
        QNameTable qnames = symbol.getQnameTable();
        if(qnames == null) {
            throw new IllegalStateException("QNameTable is not set for the collection: "
                    + collection.getCollectionName());
        }
        this.qnameTable = qnames;
        original.append("fn:collection('" + filter + "')");
    }

    public void markDescendantOrSelf() {
        this.prevDescOrSelf = true;
    }

    public void trackStep(XQExpression step, int[] frag) {
        if(step == null || frag == null) {
            throw new IllegalArgumentException();
        }
        if(step instanceof AttributeStep) {
            if(prevDescOrSelf) {
                coder.separatorPercentSlash();
            } else {
                coder.separatorSlash();
            }
        } else if(step instanceof ChildStep) {
            if(prevDescOrSelf) {
                coder.separatorPercentSlash();
            } else {
                coder.separatorSlash();
            }
        } else if(step instanceof DescendantStep) {
            coder.separatorPercentSlash();
        } else {
            throw new IllegalStateException("Unexpected step: " + step);
        }
        coder.addAll(frag);
        this.prevDescOrSelf = false;
        original.append('/').append(step.toString());
    }

    public byte[] getRewrittedPath() {
        if(coder.isEmpty()) {
            throw new IllegalStateException();
        }
        return coder.encode();
    }

    public int identifyQName(QualifiedName qname) {
        final QualifiedName found = qnameTable.find(qname);
        if(found == null) {
            return -1;
        }
        return found.identity();
    }

    public List<Pair<DbCollection, String>> listDocumentsInfo() {
        final File colDir = collection.getDirectory();
        final String colDirPath = colDir.getAbsolutePath();
        final int colDirPathLength = colDirPath.length();

        final String filterExpr = DbCollection.getDocumentFilterExp(colFilter);
        final IOFileFilter idxFileFiler;
        if(filterExpr == null) {
            idxFileFiler = new FileUtils.SuffixFileFilter(IDocumentTable.DTM_SEGMENT_FILE_SUFFIX);
        } else {
            String fname = filterExpr + IDocumentTable.DTM_SEGMENT_FILE_SUFFIX;
            idxFileFiler = new FileUtils.NameFileFilter(fname);
        }
        final IOFileFilter idxDirFilter = new FileUtils.NameFileFilter(LabelingHandler.INDEX_DIR_NAME);
        final List<File> files = FileUtils.listFiles(colDir, idxFileFiler, idxDirFilter);
        final List<Pair<DbCollection, String>> lst = new ArrayList<Pair<DbCollection, String>>(files.size());
        for(File f : files) {
            String fname = FileUtils.getFileName(f);
            String dname = fname.substring(0, fname.lastIndexOf('.'));
            String parentDirPath = f.getParent();
            final DbCollection col;
            if(parentDirPath.length() == colDirPathLength) {
                col = collection;
            } else {
                String relative = parentDirPath.substring(colDirPathLength);
                col = DbCollection.getCollection(relative);
            }
            lst.add(new Pair<DbCollection, String>(col, dname));
        }
        return lst;
    }

    public String getFilter() {
        return colFilter;
    }

    public DbCollection getCollection() {
        return collection;
    }

    public void requestLookahead(ForVariable var) {
        this.forVariable = var;
    }

    public boolean isLookaheadRequired() {
        return forVariable != null;
    }

    public ForVariable getForVariable() {
        return forVariable;
    }

    @Override
    public String toString() {
        return toString(getRewrittedPath());
    }

    public String toString(byte[] revpath) {
        return original + " <#" + ObjectUtils.identityToString(this) + " -> "
                + Arrays.toString(revpath)
                + (forVariable == null ? '>' : (", filter $" + forVariable.getName() + '>'));
    }

    public void succeeds(RewriteInfo prevInfo) {
        // rewrite items
        this.original = new StringBuilder(prevInfo.original);
        this.coder = prevInfo.coder.clone();
        // collection related items
        this.collection = prevInfo.collection;
        this.colFilter = prevInfo.colFilter;
        this.qnameTable = prevInfo.qnameTable;
    }

}
