/*
 * @(#)$Id: DbUtils.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.jdbc;

import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A collection of JDBC helper methods.
 * <DIV lang="en">
 * This class is based on Jakarta's 
 * <a href="http://jakarta.apache.org/commons/dbutils/">Commons DBUtils</a>.
 * And, some modification are made.
 * </DIV>
 * <DIV lang="ja">
 * </DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 * @link http://jakarta.apache.org/commons/dbutils/
 */
public final class DbUtils {

    private static final Log LOG = LogFactory.getLog(DbUtils.class);

    /**
     * Close a <code>Connection</code>, avoid closing if null.
     */
    public static void closeQuietly(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOG.error("failed closing a connection.", e);
            }
        }
    }

    /**
     * Close a <code>ResultSet</code>, avoid closing if null.
     */
    public static void close(ResultSet rs) throws SQLException {
        if(rs != null) {
            rs.close();
        }
    }

    /**
     * Close a <code>ResultSet</code>, avoid closing if null.
     */
    public static void closeAll(ResultSet rs) throws SQLException {
        if(rs != null) {
            Statement stmt = rs.getStatement();
            if(stmt != null) {
                Connection conn = stmt.getConnection();
                stmt.close();
                closeQuietly(conn);
            } else {
                rs.close();
            }
        }
    }

    /**
     * Close a <code>Statement</code>, avoid closing if null.
     */
    public static void close(Statement stmt) throws SQLException {
        if(stmt != null) {
            stmt.close();
        }
    }

    /**
     * Close a <code>Statement</code>, avoid closing if null.
     */
    public static void closeAll(Statement stmt) throws SQLException {
        if(stmt != null) {
            Connection conn = stmt.getConnection();
            stmt.close();
            closeQuietly(conn);
        }
    }

    /**
     * Commits a <code>Connection</code> then closes it, avoid closing if null.
     */
    public static void commitAndClose(Connection conn) throws SQLException {
        if(conn == null) {
            throw new IllegalArgumentException("Given connection is null although trying to commit the transaction.");
        }
        try {
            conn.commit();
        } finally {
            conn.close();
        }
    }

    /**
     * Rollback any changes made on the given connection.
     * @param conn The database Connection to rollback.  A null value is legal.
     * @throws SQLException
     */
    public static void rollback(Connection conn) throws SQLException {
        if(conn == null) {
            throw new IllegalArgumentException("Given connection is null although trying to commit the transaction.");
        }
        LOG.debug("Rollback the transaction.");
        conn.rollback();
    }

    public static void rollbackAndClose(Connection conn) throws SQLException {
        try {
            rollback(conn);
        } finally {
            conn.close();
        }
    }

    public static void setAutoCommit(Connection conn, boolean autocommit) {
        try {
            conn.setAutoCommit(autocommit);
        } catch (SQLException e) {
            LOG.error("setAutoCommit failed.", e);
        }
    }

}