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
package xbird.util.jdbc.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import xbird.util.jdbc.ResultSetHandler;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ColumnListHandler<E> implements ResultSetHandler {

    private final int columnIndex;
    private final String columnName;
    
    public ColumnListHandler(int columnIndex) {
        this.columnIndex = columnIndex;
        this.columnName = null;
    }
    
    public ColumnListHandler(String columnName) {
        this.columnIndex = -1;
        this.columnName = columnName;
    }

    @SuppressWarnings("unchecked")
    public List<E> handle(ResultSet rs) throws SQLException {
        final List<E> result = new ArrayList<E>();
        while(rs.next()) {
            if(this.columnName == null) {
                result.add((E) rs.getObject(this.columnIndex));
            } else {
                result.add((E) rs.getObject(this.columnName));
            }
        }
        return result;
    }

}
