/*
 * @(#)$Id: MapHandler.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.jdbc.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import xbird.util.jdbc.ResultSetHandler;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public class MapHandler implements ResultSetHandler {

    //--------------------------------------------
    // Constants

    private static final int DEFAULT_KEY_INDEX = 1;

    private static final int DEFAULT_VALUE_INDEX = 2;

    //--------------------------------------------
    // Instance variables

    protected final int keyIndex;

    protected final int valueIndex;

    protected int extraCapacity = 8;

    protected Map map = null;

    //--------------------------------------------

    public MapHandler(int extraCapacity) {
        this(DEFAULT_KEY_INDEX, DEFAULT_VALUE_INDEX, extraCapacity);
    }

    public MapHandler(int keyIndex, int valueIndex, int extraCapacity) {
        this.keyIndex = keyIndex;
        this.valueIndex = valueIndex;
        this.extraCapacity = extraCapacity;
    }

    public MapHandler(Map map) {
        this(map, DEFAULT_KEY_INDEX, DEFAULT_VALUE_INDEX);
    }

    public MapHandler(Map map, int keyIndex, int valueIndex) {
        this.keyIndex = keyIndex;
        this.valueIndex = valueIndex;
        this.map = map;
    }

    public Map handle(ResultSet rs) throws SQLException {
        Map retMap = this.map;
        if(rs.next()) {
            if(retMap == null) {
                final int columnSize = rs.getMetaData().getColumnCount();
                retMap = new HashMap(columnSize + extraCapacity);
            }
        } else {
            return Collections.EMPTY_MAP; // immutable
        }
        do {
            Object key = rs.getObject(keyIndex);
            if(key == null) { // sanity check
                throw new SQLException("key value is not found: " + keyIndex);
            }
            retMap.put(key, rs.getObject(valueIndex));
        } while(rs.next());
        return retMap;
    }

}
