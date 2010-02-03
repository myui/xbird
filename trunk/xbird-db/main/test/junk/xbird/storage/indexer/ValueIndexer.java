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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.storage.index.*;

/**
 * ValueIndexer is a basic implementation of the Indexer interface.
 * It is used for maintaining element and element@attribute value
 * indexes.
 */
public final class ValueIndexer extends BTree implements Indexer {

    private static final Log LOG = LogFactory.getLog(ValueIndexer.class);
    
    private static final IndexMatch[] EmptyMatches = new IndexMatch[0];
    private static final Value EmptyValue = new Value(new byte[0]);

    private static final long MATCH_INFO = -1000;

    private static final String NAME = "name";
    private static final String PATTERN = "pattern";
    private static final String TYPE = "type";

    private static final int STRING = 0;
    private static final int TRIMMED = 1;
    private static final int INTEGER = 2;
    private static final int FLOAT = 3;
    private static final int BYTE = 4;
    private static final int CHAR = 5;
    private static final int BOOLEAN = 6;

    private static final int[] sizes = {-1, -1, 8, 8, 1, 2, 1};

    private static final String STRING_VAL = "string";
    private static final String TRIMMED_VAL = "trimmed";
    private static final String SHORT_VAL = "short";
    private static final String INT_VAL = "int";
    private static final String LONG_VAL = "long";
    private static final String FLOAT_VAL = "float";
    private static final String DOUBLE_VAL = "double";
    private static final String BYTE_VAL = "byte";
    private static final String CHAR_VAL = "char";
    private static final String BOOLEAN_VAL = "boolean";

    private DbCollection collection;
    private SymbolTable symbols;

    private String name;
    private String pattern;
    private int type;
    private int typeSize = 32;
    private boolean wildcard = false;


    public ValueIndexer() {
        super();
    }

    /**
     * Override createFileHeader - set page size to 1024
     */
    public Paged.FileHeader createFileHeader() {
        Paged.FileHeader header = super.createFileHeader();
        header.setPageSize(1024);
        return header;
    }

    public void setConfig(Configuration config) {
        super.setConfig(config);
        try {
            name = config.getAttribute(NAME);

            pattern = config.getAttribute(PATTERN);
            wildcard = pattern.indexOf('*') != -1;

            // Determine the Index Type
            String tv = config.getAttribute(TYPE, STRING_VAL).toLowerCase();
            if (tv.equals(STRING_VAL)) {
                type = STRING;
            } else if (tv.equals(TRIMMED_VAL)) {
                type = TRIMMED;
            } else if (tv.equals(SHORT_VAL)) {
                type = INTEGER;
            } else if (tv.equals(INT_VAL)) {
                type = INTEGER;
            } else if (tv.equals(LONG_VAL)) {
                type = INTEGER;
            } else if (tv.equals(FLOAT_VAL)) {
                type = FLOAT;
            } else if (tv.equals(DOUBLE_VAL)) {
                type = FLOAT;
            } else if (tv.equals(BYTE_VAL)) {
                type = BYTE;
            } else if (tv.equals(CHAR_VAL)) {
                type = CHAR;
            } else if (tv.equals(BOOLEAN_VAL)) {
                type = BOOLEAN;
            } else {
                if (pattern.indexOf('@') != -1) {
                    type = STRING;
                } else {
                    type = TRIMMED;
                }
            }

            typeSize = sizes[type];

            setLocation(name);
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("ignored exception", e);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setLocation(String location) {
        setFile(new File(collection.getCollectionRoot(), location + ".idx"));
    }

    public void setCollection(DbCollection collection) {
        try {
            this.collection = collection;
            symbols = collection.getSymbols();
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("ignored exception", e);
            }
        }
    }

    public String getIndexStyle() {
        return STYLE_NODEVALUE;
    }

    public String getPattern() {
        return pattern;
    }

    public Value getTypedValue(String value) {
        if (type != STRING && type != TRIMMED) {
            value = value.trim();

            if (value.length() == 0) {
                return EmptyValue;
            }

            byte[] b = new byte[typeSize];
            try {
                switch (type) {
                    case INTEGER:
                        long l = Long.parseLong(value);
                        b[0] = (byte) ((l >>> 56) & 0xFF);
                        b[1] = (byte) ((l >>> 48) & 0xFF);
                        b[2] = (byte) ((l >>> 40) & 0xFF);
                        b[3] = (byte) ((l >>> 32) & 0xFF);
                        b[4] = (byte) ((l >>> 24) & 0xFF);
                        b[5] = (byte) ((l >>> 16) & 0xFF);
                        b[6] = (byte) ((l >>> 8) & 0xFF);
                        b[7] = (byte) ((l >>> 0) & 0xFF);
                        break;
                    case FLOAT:
                        double d = Double.parseDouble(value);
                        int i1 = (int) Math.round(d);
                        int i2 = (int) Math.round((d - i1) * 1000000000);
                        b[0] = (byte) ((i1 >>> 24) & 0xFF);
                        b[1] = (byte) ((i1 >>> 16) & 0xFF);
                        b[2] = (byte) ((i1 >>> 8) & 0xFF);
                        b[3] = (byte) ((i1 >>> 0) & 0xFF);
                        b[4] = (byte) ((i2 >>> 24) & 0xFF);
                        b[5] = (byte) ((i2 >>> 16) & 0xFF);
                        b[6] = (byte) ((i2 >>> 8) & 0xFF);
                        b[7] = (byte) ((i2 >>> 0) & 0xFF);
                        break;
                    case BYTE:
                        b[0] = Byte.parseByte(value);
                        break;
                    case CHAR:
                        char c = value.charAt(0);
                        b[0] = (byte) ((c >>> 8) & 0xFF);
                        b[1] = (byte) ((c >>> 0) & 0xFF);
                        break;
                    case BOOLEAN:
                        if ("[true][yes][1][y][on]".indexOf("[" + value.toLowerCase() + "]") != -1) {
                            b[0] = 1;
                        } else if ("[false][no][0][n][off]".indexOf("[" + value.toLowerCase() + "]") != -1) {
                            b[0] = 0;
                        } else {
                            return EmptyValue;
                        }
                        break;
                    default:
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("invalid type : " + type);
                        }
                }
                return new Value(b);
            } catch (Exception e) {
                return EmptyValue;
            }
        }

        if (type == TRIMMED) {
            value = QueryEngine.normalizeString(value);
        }

        return new Value(value);
    }

    private Value getCombinedValue(Key key, int pos, int len, short elemID, short attrID) {
        Value result;
        try {
            int l = key.getLength();
            byte[] b = new byte[l + 13];

            // Write the key
            key.copyTo(b, 0, l);
            b[l] = 0;

            // Write the pos
            b[l + 1] = (byte) ((pos >>> 24) & 0xFF);
            b[l + 2] = (byte) ((pos >>> 16) & 0xFF);
            b[l + 3] = (byte) ((pos >>> 8) & 0xFF);
            b[l + 4] = (byte) ((pos >>> 0) & 0xFF);

            // Write the len
            b[l + 5] = (byte) ((len >>> 24) & 0xFF);
            b[l + 6] = (byte) ((len >>> 16) & 0xFF);
            b[l + 7] = (byte) ((len >>> 8) & 0xFF);
            b[l + 8] = (byte) ((len >>> 0) & 0xFF);

            // Write the elemID
            b[l + 9] = (byte) ((elemID >>> 8) & 0xFF);
            b[l + 10] = (byte) ((elemID >>> 0) & 0xFF);

            // Write the attrID
            b[l + 11] = (byte) ((attrID >>> 8) & 0xFF);
            b[l + 12] = (byte) ((attrID >>> 0) & 0xFF);

            result = new Value(b);
        } catch (Exception e) {
            result = null; // This will never happen
        }
        return result;
    }

    private IndexMatch getIndexMatch(Value v) {
        byte[] b = v.getData();
        int l = b.length - 13;
        Key key = new Key(b, 0, b.length - 13);

        int pos = ((b[l + 1] << 24) | (b[l + 2] << 16) | (b[l + 3] << 8) | b[l + 4]);
        int len = ((b[l + 5] << 24) | (b[l + 6] << 16) | (b[l + 7] << 8) | b[l + 8]);
        short elemID = (short) ((b[l + 9] << 8) | b[l + 10]);
        short attrID = (short) ((b[l + 11] << 8) | b[l + 12]);

        return new IndexMatch(key, pos, len, elemID, attrID);
    }

    public void remove(String value, Key key, int pos, int len, short elemID, short attrID) throws DBException {
        Value v = getTypedValue(value);
        if (type != STRING && type != TRIMMED && v.getLength() == 0) {
            return;
        }

        try {
            BTreeRootInfo root = findBTreeRoot(v);
            Value cv = getCombinedValue(key, pos, len, elemID, attrID);
            removeValue(root, cv);
        } catch (DBException e) {
            throw e;
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("ignored exception", e);
            }
        }
    }

    public void add(String value, Key key, int pos, int len, short elemID, short attrID) throws DBException {
        Value v = getTypedValue(value);
        if (type != STRING && type != TRIMMED && v.getLength() == 0) {
            return;
        }

        try {
            BTreeRootInfo root;

            try {
                root = findBTreeRoot(v);
            } catch (BTreeNotFoundException e) {
                root = createBTreeRoot(v);
            }

            Value cv = getCombinedValue(key, pos, len, elemID, attrID);
            addValue(root, cv, MATCH_INFO);
        } catch (DBException e) {
            throw e;
        } catch (IOException e) {
            throw new BTreeCorruptException("Corruption detected on add", e);
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("ignored exception", e);
            }
        }
    }

    public void flush() throws DbException {
        super.flush();
    }

    public IndexMatch[] matches(final IndexQuery query) throws DbException {
        // Pre-process the value-set for typing and trimming
        if (type != STRING) {
            Value[] vals = query.getValues();
            for (int i = 0; i < vals.length; i++) {
                vals[i] = getTypedValue(vals[i].toString());
            }
        }

        // Now issue the query
        final List results = new ArrayList();

        try {
            search(query, new BTreeCallback() {
                public boolean indexInfo(Value value, long pos) {
                    try {
                        if (pos == MATCH_INFO) {
                            IndexMatch match = getIndexMatch(value);
                            if (!wildcard)
                                results.add(match);
                            else {
                                IndexPattern pt = new IndexPattern(symbols, match.getElement(), match.getAttribute());
                                if (pt.getMatchLevel(query.getPattern()) > 0) {
                                    results.add(match);
                                }
                            }
                        } else {
                            BTreeRootInfo root = new BTreeRootInfo(value, pos);
                            query(root, null, this);
                        }
                        return true;
                    } catch (Exception e) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("ignored exception", e);
                        }
                    }
                    return true;
                }
            });
        } catch (IOException e) {
            throw new BTreeCorruptException("Corruption detected on query", e);
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("ignored exception", e);
            }
        }

        return (IndexMatch[]) results.toArray(EmptyMatches);
    }
}
