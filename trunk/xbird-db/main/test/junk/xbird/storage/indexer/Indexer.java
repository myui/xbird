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
 */
package xbird.storage.indexer;

import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.storage.index.Key;

/**
 * Indexer is the abstract indexing interface for XBird.  An Indexer
 * object is implemented in order to retrieve and manage indexes.
 * <br><br>
 * Any number of Indexer instances may be associated with a single
 * Collection.  The type of Indexer utilized by a query depends on
 * the 'Style' of Indexer and the type of QueryResolver that is being
 * used to performt he query.  Currently, Xindice only internally
 * supports one kind of Indexer: 'XPath'.
 */
public interface Indexer {

    /**
     * setCollection tells the Indexer who its parent is.
     *
     * @param collection The owner Collection
     */
    void setCollection(DbCollection collection);

    /**
     * getIndexStyle returns the Index style.  Different query languages
     * will need to draw from different indexing styles. For example, A
     * query that is written in quilt will require XPath indexing.
     *
     * @return The index style
     */
    String getIndexStyle();

    /**
     * getPattern returns the pattern recognized by this Indexer.  Patterns
     * must be in the form of (elem|*)[@(attr|*)] to tell the IndexManager
     * which element types to send to it, so for example:
     * <pre>
     *    contact@name  Indexes all contacts by name attribute
     *    memo          Indexes the text of all memo elements
     *    contact@*     Indexes all contact attributes
     *    *@name        Indexes the name attribute for all elements
     *    *             Indexes the text of all elements
     *    *@*           Indexes all attributes of all elements
     * </pre>
     * These patterns are used by the IndexManager when handling SAX events.
     * All events that match the specified pattern will result in an add or
     * remove call to the Indexer.
     *
     * @return The Pattern used
     */
    String getPattern();

    /**
     * remove removes all references to the specified Key from the Indexer.
     *
     * @param value The value to remove
     * @param key The Object ID
     * @param pos The offset into the stream the Element occurs at
     * @param len The length of the substream for the Element
     * @param elemID The Element ID of the value
     * @param attrID The Attribute ID of the value (if any, else -1)
     */
    void remove(String value, Key key, int pos, int len, short elemID, short attrID) throws DbException;

    /**
     * add adds a Document to the Indexer.
     *
     * @param value The value to remove
     * @param key The Object ID
     * @param pos The offset into the stream the Element occurs at
     * @param len The length of the substream for the Element
     * @param elemID The Element ID of the value
     * @param attrID The Attribute ID of the value (if any, else -1)
     */
    void add(String value, Key key, int pos, int len, short elemID, short attrID) throws DbException;

    /**
     * queryMatches retrieves a set of IndexMatch instances that match
     * the supplied query.  The matches are then used by the  QueryEngine
     * in co-sequential processing.  If this indexer doesn't support the
     * passed value, it should return 'null'.  If no matches are found,
     * it should return an empty set.  queryMatches will typically be
     * used in XPath processing.
     *
     * @param query The IndexQuery to use
     * @return The resulting matches
     */
    IndexMatch[] matches(IndexQuery query) throws DbException;

    /**
     * flush forcefully flushes any unwritten buffers to disk.
     */
    void flush() throws DbException;
}
