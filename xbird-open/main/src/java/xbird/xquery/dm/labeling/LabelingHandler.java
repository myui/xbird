/*
 * @(#)$Id: PersistentHandler.java 2272 2007-07-11 16:55:22Z yui $
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
package xbird.xquery.dm.labeling;

import java.io.File;

import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.storage.DbCollection.Symbols;
import xbird.storage.index.BIndexFile;
import xbird.storage.indexer.BTreeIndexer;
import xbird.util.resource.PropertyMap;
import xbird.xquery.XQRTException;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.IDocument;
import xbird.xquery.dm.dtm.IDocumentTable;
import xbird.xquery.dm.ser.Serializer;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class LabelingHandler extends Serializer {
    public static final String INDEX_DIR_NAME = "__idx";
    public static final String LABEL_FILE_SUFFIX = ".label";
    public static final String PATHS_FILE_SUFFIX = ".pidx";

    private static final String TOTAL_NODES = "totalNodes";
    private static final String TOTAL_ELEMENTS = "totalElements";
    private static final String TOTAL_ATTRIBUTES = "totalAttributes";
    private static final String TOTAL_NAMESPACES = "totalNamespaces";
    private static final String TOTAL_TEXTS = "totalTexts";

    private static final String LABEL_IDX_ENTRIES = "idx.label.entries";
    private static final String PATHS_IDX_PAGES = "idx.paths.pages";
    private static final String PATHS_IDX_ENTRIES = "idx.paths.entries";
    private static final String PATHS_IDX_DUPLICATES = "idx.paths.duplicates";
    private static final String PATHS_IDX_UNIQUES = "idx.paths.uniques";

    private static final int CACHE_SIZE = 256;

    // -------------------------------------------------

    private final String docName;
    private final IDocument docTable;
    private final PropertyMap docProps;
    private final QNameTable qnameTable;

    // -------------------------------------------------

    private final byte[][] pathKeys = new byte[CACHE_SIZE][];
    private final long[] pathValues = new long[CACHE_SIZE];
    private int pathCachePtr = 0;
    private final long[] labelKeys = new long[CACHE_SIZE];
    private final byte[][] labelValues = new byte[CACHE_SIZE][];
    private int labelCachePtr = 0;

    private BTreeIndexer pathIndexer;
    private BIndexFile labelIndexer;

    // -------------------------------------------------

    private final TreeWalker _walker = new TreeWalker();

    // -------------------------------------------------

    private int cntTotalNodes = 0;
    private int totalElements = 0, totalAttributes = 0, totalNamespaces = 0, totalTexts = 0;
    private int pathIdxEntries = 0, labelIdxEntries = 0;
    private int pathIdxDuplicates = 0;

    // -------------------------------------------------

    public LabelingHandler(IDocumentTable dtm, DbCollection coll, String docName, PropertyMap properties) {
        super();
        this.docName = docName;
        this.docTable = dtm;
        this.docProps = properties;
        final Symbols symbols = coll.getSymbols();
        final QNameTable qtbl = symbols.getQnameTable();
        this.qnameTable = (qtbl == dtm.getNameTable()) ? null : qtbl;
        setupIndexers(coll, docName);
    }

    private void setupIndexers(DbCollection coll, String docName) {
        File colDir = coll.getDirectory();
        if(!colDir.exists()) {
            throw new IllegalStateException("Collection does not exist: "
                    + colDir.getAbsolutePath());
        }
        File idxDir = new File(colDir, INDEX_DIR_NAME);
        if(!idxDir.exists()) {
            boolean created = idxDir.mkdir();
            if(!created) {
                throw new IllegalStateException("Could not create an index directory: "
                        + idxDir.getAbsolutePath());
            }
        }

        File pathFile = new File(idxDir, docName + PATHS_FILE_SUFFIX);
        this.pathIndexer = new BTreeIndexer("PathIndexer#" + docName, pathFile, true);

        File labelFile = new File(idxDir, docName + LABEL_FILE_SUFFIX);
        BIndexFile labelBFile = new BIndexFile(labelFile, false);
        try {
            labelBFile.init(true);
        } catch (DbException e) {
            throw new IllegalStateException("failed initializing label BFile: "
                    + labelFile.getAbsolutePath());
        }
        this.labelIndexer = labelBFile;
    }

    private void writePaths(final long nid, final byte[] encodedPath) {
        pathKeys[pathCachePtr] = encodedPath;
        pathValues[pathCachePtr] = nid;
        if(++pathCachePtr >= CACHE_SIZE) {
            final byte[][] keys = pathKeys;
            final long[] values = pathValues;
            for(int i = 0; i < CACHE_SIZE; i++) {
                final long old;
                try {
                    old = pathIndexer.add(keys[i], values[i]);
                } catch (DbException e) {
                    throw new XQRTException("failed flushing PathIndexer. key: " + keys[i]
                            + ", value: " + values[i], e);
                }
                if(old != -1L) {
                    ++pathIdxDuplicates;
                }
            }
            pathIdxEntries += CACHE_SIZE;
            pathCachePtr = 0;
        }
    }

    private void writeLabels(final long nid, final byte[] b) {
        ++cntTotalNodes;
        labelKeys[labelCachePtr] = nid;
        labelValues[labelCachePtr] = b;
        if(++labelCachePtr >= CACHE_SIZE) {
            final long[] rowids = labelKeys;
            final byte[][] labels = labelValues;
            for(int i = 0; i < CACHE_SIZE; i++) {
                try {
                    labelIndexer.addValue(rowids[i], labels[i]);
                } catch (DbException e) {
                    throw new XQRTException("failed flushing PathIndexer. DTM_ROWID: " + rowids[i]
                            + ", LABEL: " + labels[i], e);
                }
            }
            labelIdxEntries += CACHE_SIZE;
            labelCachePtr = 0;
        }
    }

    // -------------------------------------------------
    // update local NameId with shared NameId

    public void evStartDocument() throws XQueryException {}

    @Override
    public void evStartElement(long eid, QualifiedName qname) throws XQueryException {
        if(qnameTable != null) {
            QualifiedName found = qnameTable.find(qname);
            if(found != null) {
                docTable.setName(eid, found.identity());
                qname = found;
            }
        }
        final TreeWalker walker = _walker;
        // #1 path index
        walker.moveDownElement(qname.identity());
        writePaths(eid, walker.getEncodedPath());
        // #2 label index
        writeLabels(eid, walker.getLabelAsBytea());
        ++totalElements;
    }

    @Override
    public void evAttribute(long attid, QualifiedName attName, String attValue)
            throws XQueryException {
        if(qnameTable != null) {
            final QualifiedName found = qnameTable.find(attName);
            if(found != null) {
                docTable.setAttributeName(attid, found.identity());
                attName = found;
            }
        }
        final TreeWalker walker = _walker;
        // #1 path index
        RevPathCoder coder = walker.getCoder();
        coder.separatorSlash();
        coder.identifer(attName.identity());
        coder.attribute();
        writePaths(attid, coder.encode());
        coder.popUntilNextSeparator();
        // #2 label index
        writeLabels(attid, walker.emurateRoundDependant());
        // #3 value index (optimal)
        ++totalAttributes;
    }

    @Override
    public void evNamespace(long nsid, QualifiedName nsName, String uri) throws XQueryException {
        if(qnameTable != null) {
            QualifiedName found = qnameTable.find(nsName);
            if(found != null) {
                docTable.setAttributeName(nsid, found.identity());
                nsName = found;
            }
        }
        final TreeWalker walker = _walker;
        // #2 label index
        writeLabels(nsid, walker.emurateRoundDependant());
        ++totalNamespaces;
    }

    @Override
    public void evEndElement(long eid, QualifiedName qname) throws XQueryException {
        _walker.moveUpElement();
    }

    @Override
    public void evText(long tid, String content) throws XQueryException {
        final TreeWalker walker = _walker;
        // #2 label index
        writeLabels(tid, walker.emurateRoundNode());
        // #3 value index
        ++totalTexts;
    }

    @Override
    public void evComment(long cid, String content) throws XQueryException {
        final TreeWalker walker = _walker;
        // #2 label index
        writeLabels(cid, walker.emurateRoundNode());
    }

    @Override
    public void evProcessingInstruction(long piid, String target, String content)
            throws XQueryException {
        final TreeWalker walker = _walker;
        // #2 label index
        writeLabels(piid, walker.emurateRoundNode());
    }

    // -------------------------------------------------

    public void evEndDocument() throws XQueryException {
        if(pathCachePtr > 0) {
            final byte[][] keys = pathKeys;
            final long[] values = pathValues;
            for(int i = 0; i < pathCachePtr; i++) {
                final long old;
                try {
                    old = pathIndexer.add(keys[i], values[i]);
                } catch (DbException e) {
                    throw new XQRTException("failed flushing PathIndexer. key: " + keys[i]
                            + ", value: " + values[i], e);
                }
                if(old != -1L) {
                    ++pathIdxDuplicates;
                }
            }
            pathIdxEntries += pathCachePtr;
            pathCachePtr = 0;
        }
        if(labelCachePtr > 0) {
            final long[] rowids = labelKeys;
            final byte[][] labels = labelValues;
            for(int i = 0; i < labelCachePtr; i++) {
                try {
                    labelIndexer.addValue(rowids[i], labels[i]);
                } catch (DbException e) {
                    throw new XQRTException("failed flushing PathIndexer. DTM_ROWID: " + rowids[i]
                            + ", LABEL: " + labels[i], e);
                }
            }
            labelIdxEntries += labelCachePtr;
            labelCachePtr = 0;
        }

        try {
            pathIndexer.flush(true);
            //valueIndexer.flush(true);
            labelIndexer.flush(true, true);
        } catch (DbException dbe) {
            throw new XQRTException("failed flushing indexer", dbe);
        }

        docProps.setProperty(PATHS_IDX_ENTRIES + docName, String.valueOf(pathIdxEntries));
        docProps.setProperty(PATHS_IDX_DUPLICATES + docName, String.valueOf(pathIdxDuplicates));
        docProps.setProperty(PATHS_IDX_UNIQUES + docName, String.valueOf(pathIdxEntries
                - pathIdxDuplicates));
        docProps.setProperty(LABEL_IDX_ENTRIES + docName, String.valueOf(labelIdxEntries));
        docProps.setProperty(TOTAL_NODES + docName, String.valueOf(cntTotalNodes));
        docProps.setProperty(TOTAL_ELEMENTS + docName, String.valueOf(totalElements));
        docProps.setProperty(TOTAL_ATTRIBUTES + docName, String.valueOf(totalAttributes));
        docProps.setProperty(TOTAL_NAMESPACES + docName, String.valueOf(totalNamespaces));
        docProps.setProperty(TOTAL_TEXTS + docName, String.valueOf(totalTexts));
    }

    public void evStartElement(String prefix, String localName, String namespaceURI)
            throws XQueryException {
        throw new IllegalStateException();
    }

    public void evAttribute(QualifiedName qname, String value) throws XQueryException {
        throw new IllegalStateException();
    }

    public void evNamespace(String prefix, String uri) throws XQueryException {
        throw new IllegalStateException();
    }

    public void evEndElement(String prefix, String localName, String namespaceURI)
            throws XQueryException {
        throw new IllegalStateException();
    }

    public void evText(char[] ch, int start, int length) throws XQueryException {
        throw new IllegalStateException();
    }

    public void evCData(char[] ch, int start, int length) throws XQueryException {
        throw new IllegalStateException();
    }

    public void evComment(char[] ch, int start, int length) throws XQueryException {
        throw new IllegalStateException();
    }

    public void evProcessingInstruction(String target, String data) throws XQueryException {
        throw new IllegalStateException();
    }

    public void endItem(boolean last) throws XQueryException {}

    protected void flushElement() throws XQueryException {}

}