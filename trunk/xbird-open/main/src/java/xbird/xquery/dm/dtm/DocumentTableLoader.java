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
package xbird.xquery.dm.dtm;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import xbird.config.Settings;
import xbird.storage.DbCollection;
import xbird.util.collections.ObservableLRUMap;
import xbird.util.collections.ObservableLRUMap.Cleaner;
import xbird.util.concurrent.AtomicUtils;
import xbird.util.resource.PropertyMap;
import xbird.xquery.dm.dtm.hooked.ProfiledPersistentDocumentTable;
import xbird.xquery.meta.DynamicContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DocumentTableLoader {
    private static final String PROFILE_ACCESS_PATTERN = System.getProperty("xbird.profile_dtm");
    private static final boolean USE_MMAP = Boolean.parseBoolean(Settings.get("xbird.database.dtm.use_mmap", "false"));
    private static final int MAX_DOCS_CACHED = Integer.parseInt(Settings.get("xbird.database.dtm.max_cached", "32"));

    private static final Map<String, IDocumentTable> _cache;
    static {
        final Cleaner<String, IDocumentTable> cleaner = new Cleaner<String, IDocumentTable>() {
            public void cleanup(String key, IDocumentTable reclaimed) {
                try {
                    reclaimed.tryClose();
                } catch (IOException e) {
                    ;
                }
            }
        };
        //_cache = Collections.synchronizedMap(new ObservableLRUMap<String, IDocumentTable>(MAX_DOCS_CACHED, cleaner));
        _cache = new ObservableLRUMap<String, IDocumentTable>(MAX_DOCS_CACHED, cleaner);
    }

    private DocumentTableLoader() {}

    //TODO reduce synchronized block
    public synchronized static IDocumentTable load(final DbCollection coll, final String docName, final DynamicContext dynEnv)
            throws IOException {
        final String id = coll.getAbsolutePath() + File.separatorChar + docName;
        final IDocumentTable cachedDoc = _cache.get(id);
        if(cachedDoc != null) {
            if(AtomicUtils.tryIncrementIfGreaterThan(cachedDoc.getReferenceCount(), 0)) {// may already be closed, thus assert not closed
                return cachedDoc;
            } else {
                _cache.remove(id);
            }
        }

        final PropertyMap docProps = coll.getCollectionProperties();
        final String dtmClass = docProps.getProperty(IDocumentTable.KEY_DTM_CLASS + docName);
        final IDocumentTable table;
        if(MemoryMappedDocumentTable.MMDTM_CLASS.equals(dtmClass) || (dtmClass == null && USE_MMAP)) {
            table = new MemoryMappedDocumentTable(coll, docName, docProps, true);
        } else if(DocumentTable.DTM_CLASS.equals(dtmClass)) {
            if(PROFILE_ACCESS_PATTERN != null) {
                table = new ProfiledPersistentDocumentTable(PROFILE_ACCESS_PATTERN, coll, docName, docProps);
            } else {
                table = DocumentTable.load(coll, docName, docProps);
            }
        } else if(BigDocumentTable.DTM_CLASS.equals(dtmClass)) {
            table = BigDocumentTable.load(coll, docName, docProps);
        } else {
            throw new IllegalStateException("dtmp file format '" + dtmClass + "' is illegal: "
                    + docProps.getFile().getAbsolutePath());
        }
        _cache.put(id, table); // intended that two or more DTM table are created concurrently.
        return table;
    }

    public synchronized static IDocumentTable removeDocument(String docId) {
        return _cache.remove(docId);
    }

    public synchronized static void putDocumentIfAbsent(String docId, IDocumentTable doc) {
        if(_cache.containsKey(docId)) {
            _cache.put(docId, doc);
        }
    }

    public synchronized static void clean() {
        _cache.clear();
    }
}
