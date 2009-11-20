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
package xbird.util.jdbc;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SQLTypes {

    private static final Map<Class<?>, Integer> sqlTypes;
    static {
        sqlTypes = new HashMap<Class<?>, Integer>(30);
        // primitive
        sqlTypes.put(Boolean.TYPE, Types.BOOLEAN);
        sqlTypes.put(Byte.TYPE, Types.TINYINT);
        sqlTypes.put(Short.TYPE, Types.SMALLINT);
        sqlTypes.put(Integer.TYPE, Types.INTEGER);
        sqlTypes.put(Long.TYPE, Types.BIGINT);
        sqlTypes.put(Float.TYPE, Types.REAL);
        sqlTypes.put(Double.TYPE, Types.DOUBLE);
        // java object
        sqlTypes.put(String.class, Types.VARCHAR);
        sqlTypes.put(java.math.BigDecimal.class, Types.NUMERIC);
        sqlTypes.put(Boolean.class, Types.BOOLEAN);
        sqlTypes.put(Byte.class, Types.TINYINT);
        sqlTypes.put(Short.class, Types.SMALLINT);
        sqlTypes.put(Integer.class, Types.INTEGER);
        sqlTypes.put(Long.class, Types.BIGINT);
        sqlTypes.put(Float.class, Types.REAL);
        sqlTypes.put(Double.class, Types.DOUBLE);
        sqlTypes.put(byte[].class, Types.VARBINARY);
        sqlTypes.put(java.sql.Date.class, Types.DATE);
        sqlTypes.put(java.sql.Time.class, Types.TIME);
        sqlTypes.put(java.sql.Timestamp.class, Types.TIMESTAMP);
        sqlTypes.put(java.sql.Clob.class, Types.CLOB);
        sqlTypes.put(java.sql.Blob.class, Types.BLOB);
        sqlTypes.put(java.sql.Array.class, Types.ARRAY);
        sqlTypes.put(java.sql.Struct.class, Types.STRUCT);
        sqlTypes.put(java.sql.Ref.class, Types.REF);
        // additional supports
        sqlTypes.put(java.util.Date.class, Types.TIMESTAMP);
        sqlTypes.put(java.util.Calendar.class, Types.TIMESTAMP);
        sqlTypes.put(java.util.GregorianCalendar.class, Types.TIMESTAMP);
    }

    private SQLTypes() {}

    /**
     * @link http://java.sun.com/javase/6/docs/technotes/guides/jdbc/getstart/mapping.html#1038075
     */
    public static int getSQLType(@Nonnull final Class<?> clazz) {
        Class<?> clazzType = clazz;
        do {
            Integer type = sqlTypes.get(clazzType);
            if(type != null) {
                return type.intValue();
            }
            clazzType = clazzType.getSuperclass();
        } while(clazzType != null);
        return java.sql.Types.JAVA_OBJECT;
    }
}
