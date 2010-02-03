/*
 * Copyright 1999-2004 The Apache Software Foundation.
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
 * CVS $Id: NameIndexer.java 1833 2007-02-27 06:26:31Z yui $
 */
package xbird.storage.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.storage.index.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * NameIndexer is a basic implementation of the Indexer interface.
 * It is used for maintaining element and element@attribute unique
 * indexes.
 *
 * @version CVS $Revision: 192824 $, $Date: 2004-02-08 11:59:39 +0900 $
 */
public final class NameIndexer extends BTree implements Indexer {

    private static final Log log = LogFactory.getLog(NameIndexer.class);

    private static final IndexMatch[] EmptyMatches = new IndexMatch[0];

    private static final String NAME = "name";
    private static final String PATTERN = "pattern";

    private DbCollection collection;
    private SymbolTable symbols;

    private String name;
    private String pattern;
    private boolean wildcard = false;


    public NameIndexer() {
        super();
    }

    public void setConfig(Configuration config) {
        super.setConfig(config);
        try {
            name = config.getAttribute(NAME);

            pattern = config.getAttribute(PATTERN);
            wildcard = pattern.indexOf('*') != -1;

            setLocation(name);
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("ignored exception", e);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setLocation(String location) {
        setFile(new File(collection.getCollectionRoot(), location + ".idx"));
    }

    public void setCollection(Collection collection) {
        try {
            this.collection = collection;
            symbols = collection.getSymbols();
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("ignored exception", e);
            }
        }
    }

    public String getIndexStyle() {
        return STYLE_NODENAME;
    }

    public String getPattern() {
        return pattern;
    }

    public void remove(String value, Key key, int pos, int len, short elemID, short attrID) throws DBException {
        try {
            removeValue(key);
        } catch (IOException e) {
            throw new BTreeCorruptException("Corruption detected on remove", e);
        }
    }

    public void add(String value, Key key, int pos, int len, short elemID, short attrID) throws DBException {
        try {
            addValue(key, 0);
        } catch (IOException e) {
            throw new BTreeCorruptException("Corruption detected on add", e);
        }
    }

    public void flush() throws DbException {
        super.flush();
    }

    public IndexMatch[] matches(final IndexQuery query) throws DbException {
        final List results = new ArrayList();
        final IndexPattern pattern = query.getPattern();

        try {
            search(query, new BTreeCallback() {
                public boolean indexInfo(Value value, long pos) {
                    results.add(new IndexMatch(new Key(value), pattern));
                    return true;
                }
            });
        } catch (DbException e) {
            throw e;
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("ignored exception", e);
            }
        }

        return (IndexMatch[]) results.toArray(EmptyMatches);
    }
}
