/*
 * @(#)$Id: ArrayListHandler.java 3619 2008-03-26 07:23:03Z yui $
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
 *     Makoto YUI - ported from jakarta commons DBUtils
 */
/*
 * Copyright 2003-2004 The Apache Software Foundation
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
package xbird.util.jdbc.handlers;

import java.sql.*;
import java.util.*;

import xbird.util.jdbc.ResultSetHandler;

/**
 * <code>ResultSetHandler</code> implementation that converts the 
 * <code>ResultSet</code> into a <code>List</code> of <code>Object[]</code>s.
 * This class is thread safe.
 * 
 * @see ResultSetHandler
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public class ArrayListHandler<E> implements ResultSetHandler {

    public ArrayListHandler() {}

    /**
     * Convert each row's columns into an <code>Object[]</code> and store them 
     * in a <code>List</code> in the same order they are returned from the
     * <code>ResultSet.next()</code> method. 
     * 
     * @return A <code>List</code> of <code>Object[]</code>s, never 
     * <code>null</code>.
     */
    public Collection handle(ResultSet rs) throws SQLException {
        List<E[]> result = new ArrayList<E[]>();
        while(rs.next()) {
            result.add((E[]) toArray(rs));
        }
        return result;
    }

    /**
     * Convert a <code>ResultSet</code> row into an <code>Object[]</code>.
     * This implementation copies column values into the array in the same 
     * order they're returned from the <code>ResultSet</code>.  Array elements
     * will be set to <code>null</code> if the column was SQL NULL.
     */
    protected static final Object[] toArray(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        Object[] result = new Object[cols];
        for(int i = 0; i < cols; i++) {
            result[i] = rs.getObject(i + 1);
        }
        return result;
    }

    public static ResultSetHandler getInstance() {
        return Singleton.instance;
    }

    /** Lazy instantiation. */
    protected static final class Singleton {
        static final ArrayListHandler instance = new ArrayListHandler();
    }

}
