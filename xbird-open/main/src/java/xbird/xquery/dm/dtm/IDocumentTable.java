/*
 * @(#)$Id: IDocumentTable.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.dtm;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import xbird.config.Settings;
import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.storage.io.Segments;
import xbird.xquery.dm.IDocument;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public interface IDocumentTable extends IDocument {

    static final boolean ENV_USE_JNI = System.getProperty("xbird.use_jni") != null;
    static final boolean ENV_USE_2QCACHE = System.getProperty("xbird.cache.disable_2q") == null;

    static final boolean BUILDIDX_AT_BULKLOAD = Boolean.getBoolean(Settings.get("xbird.database.bulkload.buildidx", "true"));

    /**
     * Default buffer size is 128M(256MB for BigDTM).
     * <pre>
     * Normal DTM: 2K * 16 * 512 = 16MB, Big DTM(over 2GB): 4K * 16 * 512 = 32MB
     * Normal DTM: 2K * 32 * 512 = 32MB, Big DTM(over 2GB): 4K * 32 * 512 = 64MB
     * Normal DTM: 2K * 64 * 512 = 64MB, Big DTM(over 2GB): 4K * 128 * 512 = 128MB
     * Normal DTM: 2K * 128 * 512 = 128MB, Big DTM(over 2GB): 4K * 128 * 512 = 256MB
     * Normal DTM: 2K * 256 * 512 = 256MB, Big DTM(over 2GB): 4K * 256 * 512 = 512MB
     * Normal DTM: 2K * 512 * 512 = 512MB, Big DTM(over 2GB): 4K * 512 * 512 = 1024MB
     * Normal DTM: 2K * 1024 * 512 = 1024MB, Big DTM(over 2GB): 4K * 1024 * 512 = 2048MB
     * </pre>
     */
    static final int PAGE_CACHE_SIZE = Integer.getInteger("xbird.page_caches", 128) * 512;

    static final String KEY_DTM_CLASS = "dtmClass";
    static final String KEY_BLOCK_PTR = "blockPtr";
    static final String KEY_USED_PAGES = "usedPages";
    static final String DTM_SEGMENT_FILE_SUFFIX = ".dtms";

    static final int PARENT_OFFSET = 1;
    static final int NEXTSIB_OFFSET = 2;
    static final int ATTR_NAME_OFFSET = 2;
    static final int CONTENT_OFFSET = 3;
    static final int BLOCKS_PER_NODE = 4;

    // --------------------------------------------

    public void ensureOpen();
    
    public AtomicInteger getReferenceCount();

    public QNameTable getNameTable();

    public PagingProfile getPagingProfile();

    public Map<String, String> getDeclaredNamespaces();

    public long firstChild(long curnode);

    public long lastChild(long curnode);

    public long nextSibling(long curnode);

    public long parent(long curnode);

    public long previousSibling(long curnode);

    public byte getNodeKindAt(long at);

    public long getAttribute(long elemid, int index);

    public int getAttributeCountAt(long addr);

    public int getAttributeNameCode(long at);

    public QualifiedName getNameAt(long at);

    public QualifiedName getAttributeName(long at);

    public int getNameCode(long at);

    public QualifiedName getName(long id);

    public int getNamespaceCountAt(long addr);

    public long getNamespaceDecl(long elemid, int index);

    /** synchronization required */
    public String getText(long at);

    /** synchronization required */
    public void getText(long at, StringBuilder sb);

    public long getTextPageAddr(long at);

    public String stringValue(long nid);

    public void flush(DbCollection coll, String docName) throws IOException, DbException;

    public Segments getPaged(DbCollection coll, String docName);

    public long dataAt(long at);

    public void close() throws IOException;

}