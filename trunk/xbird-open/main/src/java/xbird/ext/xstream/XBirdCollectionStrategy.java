/*
 * @(#)$Id: codetemplate_xbird.xml 3792 2008-04-21 21:39:23Z yui $
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
package xbird.ext.xstream;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.ContentHandler;

import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.storage.tx.Transaction;
import xbird.util.io.IOUtils;
import xbird.xquery.dm.dtm.DocumentTable;
import xbird.xquery.dm.dtm.DocumentTableBuilder;
import xbird.xquery.dm.dtm.IDocumentTable;
import xbird.xquery.dm.instance.DocumentTableModel.DTMDocument;
import xbird.xquery.meta.DynamicContext;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.SaxWriter;
import com.thoughtworks.xstream.persistence.StreamStrategy;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class XBirdCollectionStrategy<K, V> implements StreamStrategy {

    private final XStream xstream;
    private final DbCollection collection;

    public XBirdCollectionStrategy(String collectionName) {
        this(collectionName, getAnnotationProcessableXStreamInstance());
    }

    public static XStream getAnnotationProcessableXStreamInstance() {
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        return xstream;
    }

    public XBirdCollectionStrategy(String collectionName, XStream xstream) {
        this.xstream = xstream;
        try {
            this.collection = DbCollection.getRootCollection().createCollection(collectionName);
        } catch (DbException e) {
            throw new IllegalStateException("Could not prepare collection: " + collectionName, e);
        }
    }

    public V get(Object key) {
        String docName = docName(key);
        return retrieveObject(docName);
    }

    public V put(Object key, Object value) {
        String docName = docName(key);
        return _put(docName, (V) value);
    }

    private V _put(String docName, V value) {
        V prev = _remove(docName);

        IDocumentTable doc = new DocumentTable(collection, docName);
        //String docid = collection.getAbsolutePath() + File.separatorChar + docName;
        //DocumentTableLoader.putDocumentIfAbsent(docid, doc);
        SaxWriter writer = new SaxWriter();
        ContentHandler builder = new DocumentTableBuilder(doc);
        writer.setContentHandler(builder);

        xstream.marshal(value, writer);
        writer.flush();

        try {
            collection.putDocument(new Transaction(), docName, doc);
        } catch (DbException e) {
            throw new IllegalStateException(e);
        }

        return prev;
    }

    public V remove(Object key) {
        String docName = docName(key);
        return _remove(docName);
    }

    private V _remove(String docName) {
        V doc = retrieveObject(docName);
        if(doc != null) {
            final boolean removed;
            try {
                removed = collection.removeDocument(null, docName);
            } catch (DbException e) {
                throw new IllegalStateException("Could not delete: " + docName, e);
            }
            if(!removed) {
                throw new IllegalStateException("Could not remove: " + docName);
            }
        }
        return doc;
    }

    public int size() {
        return collection.listDocumentFiles().size();
    }

    public Iterator<Map.Entry<String, V>> iterator() {
        Map<String, DTMDocument> docmap;
        try {
            docmap = collection.listDocuments(DynamicContext.DUMMY);
        } catch (DbException e) {
            throw new IllegalStateException(e);
        }
        return new XmlMapEntriesIterator(docmap.entrySet().iterator());
    }

    private final class XmlMapEntriesIterator implements Iterator<Map.Entry<String, V>> {

        private final Iterator<Entry<String, DTMDocument>> iterator;
        private String currentKey = null;

        XmlMapEntriesIterator(Iterator<Entry<String, DTMDocument>> iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Entry<String, V> next() {
            final Entry<String, DTMDocument> entry = iterator.next();
            final String docName = entry.getKey();
            this.currentKey = docName;
            return new Entry<String, V>() {

                public String getKey() {
                    return extractKey(docName);
                }

                public V getValue() {
                    DTMDocument doc = entry.getValue();
                    Object value = xstream.unmarshal(new DTMReader(doc), doc);
                    IOUtils.closeQuietly(doc);
                    return (V) value;
                }

                public V setValue(V value) {
                    return _put(entry.getKey(), value);
                }
            };
        }

        public void remove() {
            _remove(currentKey);
        }

    }

    private V retrieveObject(String docName) {
        if(!collection.containsDocument(docName)) {
            return null;
        }
        final DTMDocument doc;
        try {
            doc = collection.getDocument(null, docName, DynamicContext.DUMMY);
        } catch (DbException e) {
            throw new IllegalStateException(e);
        }
        if(doc == null) {
            return null;
        }
        Object value = xstream.unmarshal(new DTMReader(doc));
        IOUtils.closeQuietly(doc);
        return (V) value;
    }

    private static String docName(final Object key) {
        return escape(key.toString()) + ".xml";
    }

    private static String escape(String key) {
        final StringBuilder buffer = new StringBuilder(64);
        final char[] array = key.toCharArray();
        for(int i = 0; i < array.length; i++) {
            char c = array[i];
            if(Character.isDigit(c) || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                buffer.append(c);
            } else if(c == '_') {
                buffer.append("__");
            } else {
                buffer.append("_" + (Integer.toHexString(c)) + "_");
            }
        }
        return buffer.toString();
    }

    private static String extractKey(String name) {
        return unescape(name.substring(0, name.length() - 4));
    }

    private static String unescape(String name) {
        final StringBuilder buffer = new StringBuilder(64);
        int currentValue = -1;
        final char[] array = name.toCharArray();
        for(int i = 0; i < array.length; i++) {
            char c = array[i];
            if(c == '_' && currentValue != -1) {
                if(currentValue == 0) {
                    buffer.append('_');
                } else {
                    buffer.append((char) currentValue);
                }
                currentValue = -1;
            } else if(c == '_') {
                currentValue = 0;
            } else if(currentValue != -1) {
                currentValue = currentValue * 16 + Integer.parseInt(String.valueOf(c), 16);
            } else {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }
}
