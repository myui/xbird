/*
 * @(#)$Id: DbCollection.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.storage;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.config.Settings;
import xbird.storage.tx.Transaction;
import xbird.util.concurrent.reference.FinalizableSoftValueReferenceMap;
import xbird.util.concurrent.reference.ReferentFinalizer;
import xbird.util.io.FileUtils;
import xbird.util.io.IOUtils;
import xbird.util.resource.PropertyMap;
import xbird.xquery.dm.dtm.DocumentTableLoader;
import xbird.xquery.dm.dtm.IDocumentTable;
import xbird.xquery.dm.dtm.LazyDTMDocument;
import xbird.xquery.dm.instance.DocumentTableModel;
import xbird.xquery.dm.instance.DocumentTableModel.DTMDocument;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.IStringChunk;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.StringChunkLoader;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DbCollection implements Closeable {
    private static final Log LOG = LogFactory.getLog(DbCollection.class);
    
    public static final String QNAMES_FILE_SUFFIX = ".qnames";
    private static final String DTM_PROPS_FILE_SUFFIX = ".dtmp";
    private static final String ROOT_COLLECTION_NAME = "/";

    public static final String DATA_DIR;
    private static final DbCollection _rootCol = new DbCollection();
    private static final Map<String, DbCollection> _collectionCache;

    static {
        String dataDir = Settings.get("xbird.database.datadir");
        if(dataDir == null) {
            String tmp = System.getProperty("java.io.tmpdir");
            File file = new File(tmp, "xbird");
            if(file.canRead()) {
                if(!file.exists() || file.isFile()) {
                    if(file.canWrite()) {
                        file.mkdir();
                    }
                }
            }
            LOG.info("Use `" + dataDir + "' for the data repository");
        }
        DATA_DIR = dataDir;
        _collectionCache = new FinalizableSoftValueReferenceMap<String, DbCollection>(new ReferentFinalizer<String, DbCollection>() {
            public void finalize(String key, DbCollection reclaimed) {
                IOUtils.closeQuietly(reclaimed);
            }
        });
    }

    private final DbCollection _parent;
    private final String _colName;
    private final String _absolutePath;
    private final Symbols _symbols;

    private volatile PropertyMap _properties = null;
    private volatile IStringChunk _stringChunk = null;

    /** creates root collection */
    private DbCollection() {
        this._parent = null;
        this._colName = ROOT_COLLECTION_NAME;
        this._absolutePath = DATA_DIR;
        this._symbols = null;
    }

    private DbCollection(String name, DbCollection parent) throws DbException {
        if(name == null || name.indexOf('/') != -1) {
            throw new DbException("Collection name must not contain '/', but was '" + name + '\'');
        }
        this._parent = parent;
        this._colName = name;
        this._absolutePath = parent.getAbsolutePath() + '/' + name;
        this._symbols = (parent == _rootCol) ? loadSymbols() : parent.getSymbols();
    }

    public PropertyMap getCollectionProperties() throws IOException {
        if(_properties == null) {
            synchronized(this) {
                if(_properties == null) {
                    this._properties = generatePropertyMap(_absolutePath, _colName);
                }
            }
        }
        return _properties;
    }

    private static PropertyMap generatePropertyMap(String colDir, String colName)
            throws IOException {
        String propFilename = colName + DTM_PROPS_FILE_SUFFIX;
        File propFile = new File(colDir, propFilename);
        if(!propFile.exists()) {
            return new PropertyMap(propFile);
        }
        PropertyMap map = PropertyMap.load(propFile);
        return map;
    }

    public IStringChunk getStringChunk() throws IOException {
        synchronized(this) {
            if(_stringChunk == null || _stringChunk.getAndIncrementReferenceCount() == 0) {
                this._stringChunk = StringChunkLoader.load(this);
            }
        }
        return _stringChunk;
    }

    public static DbCollection getRootCollection() {
        return _rootCol;
    }

    private final Symbols loadSymbols() throws DbException {
        return new Symbols(loadQNameTable());
    }

    private final QNameTable loadQNameTable() throws DbException {
        File colDir = new File(getAbsolutePath());
        if(!colDir.exists()) {
            throw new DbException("Collection does not exist: " + colDir.getAbsolutePath());
        }
        if(!colDir.isDirectory()) {
            throw new DbException("Collection '" + colDir.getAbsolutePath()
                    + "' is not a directory, but was a file");
        }
        File symbolFile = new File(colDir, _colName + QNAMES_FILE_SUFFIX);
        if(!symbolFile.exists()) {
            return new QNameTable(128);
        }
        // load symbols
        final ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new FileInputStream(symbolFile));
        } catch (FileNotFoundException fe) {
            throw new DbException(fe);
        } catch (IOException ioe) {
            throw new DbException(ioe);
        }
        final QNameTable symbols;
        try {
            symbols = (QNameTable) ois.readObject();
        } catch (IOException ioe) {
            throw new DbException(ioe);
        } catch (ClassNotFoundException ce) {
            throw new DbException(ce);
        } finally {
            try {
                ois.close();
            } catch (IOException e) {
            }
        }
        symbols.setDirty(false);
        return symbols;
    }

    public final DbCollection createCollection(String colName) throws DbException {
        if(colName == null || colName.indexOf('/') != -1) {
            throw new DbException("Collection name must not contain '/', but was " + colName);
        }
        DbCollection coll = _collectionCache.get(colName);
        if(coll != null) {
            return coll;
        }
        File baseDir = getDirectory();
        File colDir = new File(baseDir, colName);
        if(colDir.exists()) {
            return new DbCollection(colName, this);
        }
        if(!baseDir.canWrite()) {
            throw new DbException("could not write file. Check the pertition of "
                    + baseDir.getAbsolutePath());
        }
        if(!colDir.mkdir()) {
            throw new IllegalStateException("create directory failed: " + colDir.getAbsolutePath());
        }
        coll = new DbCollection(colName, this);
        _collectionCache.put(colName, coll);
        return coll;
    }

    public final boolean removeCollection(String colName) throws DbException {
        File baseDir = getDirectory();
        File colDir = new File(baseDir, colName);
        if(!colDir.exists()) {
            return false;
        }
        boolean deleted = colDir.delete();
        return deleted;
    }

    public final void putDocument(String docName, IDocumentTable doc) throws DbException {
        Transaction tx = new Transaction();
        putDocument(tx, docName, doc);
        tx.commit();
    }

    public final void putDocument(Transaction tx, String docName, IDocumentTable doc)
            throws DbException {
        try {
            doc.flush(this, docName);
        } catch (IOException e) {
            throw new DbException("putDocument failed: " + docName, e);
        }
    }

    public final Map<String, DTMDocument> listDocuments(DynamicContext dynEnv) throws DbException {
        return listDocuments(null, true, dynEnv);
    }

    public final Map<String, DTMDocument> listDocuments(String filterExp, boolean lazy, DynamicContext dynEnv)
            throws DbException {
        final Collection<File> files = FileUtils.listFiles(getDirectory(), new String[] { IDocumentTable.DTM_SEGMENT_FILE_SUFFIX }, false);
        final Map<String, DTMDocument> colls = new IdentityHashMap<String, DTMDocument>(files.size());
        for(File f : files) {
            String fname = FileUtils.getFileName(f);
            String dname = fname.substring(0, fname.lastIndexOf('.'));
            if(filterExp != null && !dname.matches(filterExp)) {
                continue;
            }
            final DTMDocument doc;
            if(lazy) {
                doc = new LazyDTMDocument(dname, this, dynEnv);
            } else {
                IDocumentTable doctbl = getDocument(null, dname, dynEnv);
                doc = new DocumentTableModel(doctbl, true).documentNode();
            }
            colls.put(dname, doc);
        }
        return colls;
    }

    public final IDocumentTable getDocument(Transaction tx, String docName, DynamicContext dynEnv)
            throws DbException {
        final IDocumentTable dtm;
        try {
            dtm = DocumentTableLoader.load(this, docName, dynEnv);
        } catch (IOException e) {
            throw new DbException("loading document failed: " + docName, e);
        }
        return dtm;
    }

    public void flushSymbols() throws DbException {
        _symbols.flush(this);
    }

    public final DbCollection getParentCollection() {
        return _parent;
    }

    public final String getCollectionName() {
        return _colName;
    }

    public final String getAbsolutePath() {
        return _absolutePath;
    }

    public final Symbols getSymbols() {
        return _symbols;
    }

    public final File getDirectory() {
        String baseDir = getAbsolutePath();
        File colDir = new File(baseDir);
        return colDir;
    }

    public static class Symbols {

        private boolean dirty = true;
        private final QNameTable qnameTable;

        public Symbols(QNameTable qnameTable) {
            if(qnameTable == null) {
                throw new IllegalArgumentException();
            }
            this.qnameTable = qnameTable;
        }

        public QNameTable getQnameTable() {
            return qnameTable;
        }

        public boolean isDirty() {
            return dirty;
        }

        public void setDirty(boolean dirty) {
            this.dirty = dirty;
        }

        public synchronized void flush(DbCollection col) throws DbException {
            if(!dirty) {
                return;
            }
            qnameTable.flush(col);
        }
    }

    public static final DbCollection getCollection(String colpath) {
        if(colpath == null) {
            throw new IllegalArgumentException();
        }
        if(!colpath.startsWith("/")) {
            throw new IllegalArgumentException("Illegal collection path: " + colpath);
        }
        DbCollection coll = _collectionCache.get(colpath);
        if(coll != null) {
            return coll;
        }
        String[] colnames = colpath.split("/");
        int lastidx = colnames.length - 1;
        for(int i = 0; i < colnames.length; i++) {
            String colname = colnames[i];
            if(colname.contains(".")) {
                lastidx = i - 1;
            }
        }
        try {
            coll = DbCollection.getRootCollection();
            for(int i = 1; i <= lastidx; i++) {
                String colname = colnames[i];
                coll = new DbCollection(colname, coll);
            }
            _collectionCache.put(colpath, coll);
            return coll;
        } catch (DbException e) {
            return null;
        }
    }

    public static final String getDocumentFilterExp(String colpath) {
        String[] colnames = colpath.split("/");
        if(colnames.length == 0) {
            return null;
        }
        String lastName = colnames[colnames.length - 1];
        return lastName.contains(".") ? lastName : null;
    }

    @Override
    public String toString() {
        return _colName + " [" + _absolutePath + ']';
    }

    public void close() throws IOException {
        this._properties = null;
        IStringChunk sc = _stringChunk;
        if(sc != null) {
            sc.close();
            this._stringChunk = null;
        }
    }

}
