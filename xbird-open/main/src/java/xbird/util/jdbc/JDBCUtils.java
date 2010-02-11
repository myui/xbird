/*
 * @(#)$Id$
 *
 * Copyright 2003-2004 The Apache Software Foundation
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
package xbird.util.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.config.Settings;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class JDBCUtils {

    private static final Log LOG = LogFactory.getLog(JDBCUtils.class);
    private static final boolean showSQL = "true".equalsIgnoreCase(Settings.get("xbird.showsql"));

    private JDBCUtils() {}

    public static Connection getConnection(@Nonnull String connectUrl, @Nullable String driverClassName, @Nullable String userName, @Nullable String password)
            throws ClassNotFoundException, SQLException {
        if(driverClassName != null) {
            Class.forName(driverClassName);
        }
        final Connection conn;
        if(userName == null) {
            conn = DriverManager.getConnection(connectUrl);
        } else {
            conn = DriverManager.getConnection(connectUrl, userName, password);
        }
        return conn;
    }

    public static Connection getConnection(@Nonnull String connectUrl, @Nullable String userName, @Nullable String password)
            throws SQLException {
        final Connection conn;
        if(userName == null) {
            conn = DriverManager.getConnection(connectUrl);
        } else {
            conn = DriverManager.getConnection(connectUrl, userName, password);
        }
        return conn;
    }

    /**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
     * 
     * @param conn The Connection to use to run the query.  The caller is
     * responsible for closing this Connection.
     * @param sql The SQL to execute.
     * @param params An array of query replacement parameters.  Each row in
     * this array is one set of batch replacement values. 
     * @return The number of rows updated per statement.
     * @throws SQLException
     */
    public static int[] batch(Connection conn, String sql, Object[][] params) throws SQLException {
        PreparedStatement stmt = null;
        int[] rows = null;
        try {
            stmt = conn.prepareStatement(sql);
            for(int i = 0; i < params.length; i++) {
                fillStatement(stmt, params[i]);
                stmt.addBatch();
            }
            verboseQuery(sql, (Object[]) params);
            rows = stmt.executeBatch();

        } catch (SQLException e) {
            rethrow(e, sql, (Object[]) params);
        } finally {
            close(stmt);
        }
        return rows;
    }

    public static int[] batch(Connection conn, String... sqls) throws SQLException {
        final boolean autoCommit = conn.getAutoCommit();
        if(autoCommit) {
            conn.setAutoCommit(false);
        }
        Statement stmt = null;
        int[] rows = null;
        try {
            stmt = conn.createStatement();
            for(int i = 0; i < sqls.length; i++) {
                verboseQuery(sqls[i], (Object[]) null);
                stmt.addBatch(sqls[i]);
            }
            rows = stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            rethrow(e, sqls);
        } finally {
            close(stmt);
        }
        // change back commit mode.
        if(autoCommit) {
            conn.setAutoCommit(true);
        }
        return rows;
    }

    /**
     * Fill the <code>PreparedStatement</code> replacement parameters with 
     * the given objects.
     * 
     * @param params Query replacement parameters; <code>null</code> is a valid value to pass in.
     */
    public static void fillStatement(PreparedStatement stmt, Object[] params) throws SQLException {
        if(params == null) {
            return;
        }
        for(int i = 0; i < params.length; i++) {
            if(params[i] != null) {
                stmt.setObject(i + 1, params[i]);
            } else {
                // VARCHAR works with many drivers regardless
                // of the actual column type.  Oddly, NULL and 
                // OTHER don't work with Oracle's drivers.
                stmt.setNull(i + 1, Types.VARCHAR);
            }
        }
    }

    /**
     * Execute an SQL SELECT query with a single replacement parameter. The
     * caller is responsible for closing the connection.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param param The replacement parameter.
     * @param rsh The handler that converts the results into an object.
     * @return The object returned by the handler.
     */
    public static Object query(Connection conn, String sql, Object param, ResultSetHandler rsh)
            throws SQLException {
        return query(conn, sql, new Object[] { param }, rsh);
    }

    /**
     * Execute an SQL SELECT query with replacement parameters.  The
     * caller is responsible for closing the connection.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param params The replacement parameters.
     * @param rsh The handler that converts the results into an object.
     * @return The object returned by the handler.
     */
    public static Object query(Connection conn, String sql, Object[] params, ResultSetHandler rsh)
            throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Object result = null;
        try {
            stmt = conn.prepareStatement(sql);
            fillStatement(stmt, params);
            verboseQuery(sql, params);
            rs = stmt.executeQuery();
            result = rsh.handle(rs);
        } catch (SQLException e) {
            rethrow(e, sql, params);
        } finally {
            try {
                close(rs);
            } finally {
                close(stmt);
            }
        }
        return result;
    }

    /**
     * Execute an SQL SELECT query without any replacement parameters.  The
     * caller is responsible for closing the connection.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param rsh The handler that converts the results into an object.
     * @return The object returned by the handler.
     */
    public static Object query(Connection conn, String sql, ResultSetHandler rsh)
            throws SQLException {
        return query(conn, sql, (Object[]) null, rsh);
    }

    /**
     * Execute an SQL SELECT query with a single replacement parameter. The
     * caller is responsible for closing the connection.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param param The replacement parameter.
     * @return The object represents ResultSet.
     */
    public static ResultSet fetch(Connection conn, String sql, Object param) throws SQLException {
        return fetch(conn, sql, new Object[] { param });
    }

    /**
     * Execute an SQL SELECT query with replacement parameters.  The
     * caller is responsible for closing the connection.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @param params The replacement parameters.
     * @return The object represents ResultSet.
     */
    public static ResultSet fetch(Connection conn, String sql, Object[] params) throws SQLException {
        ResultSet rs = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            fillStatement(stmt, params);
            verboseQuery(sql, params);
            rs = stmt.executeQuery();
        } catch (SQLException e) {
            rethrow(e, sql, params);
        }
        return rs;
    }

    /**
     * Execute an SQL SELECT query without any replacement parameters.  The
     * caller is responsible for closing the connection.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @return The object represents ResultSet.
     */
    public static ResultSet fetch(Connection conn, String sql) throws SQLException {
        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            verboseQuery(sql, (Object[]) null);
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            rethrow(e, sql, (Object[]) null);
        }
        return rs;
    }

    /**
     * Throws a new exception with a more informative error message.
     * 
     * @param cause The original exception that will be chained to the new 
     * exception when it's rethrown. 
     * @param sql The query that was executing when the exception happened.     
     * @param params The query replacement paramaters; <code>null</code> is a 
     * valid value to pass in.
     */
    private static void rethrow(SQLException cause, String sql, Object[] params)
            throws SQLException {
        StringBuilder msg = new StringBuilder(cause.getMessage());
        msg.append(" Query: ");
        msg.append(sql);
        msg.append(" Parameters: ");
        if(params == null) {
            msg.append("[]");
        } else {
            msg.append(Arrays.asList(params));
        }
        SQLException e = new SQLException(msg.toString());
        e.setNextException(cause);
        throw e;
    }

    private static void rethrow(SQLException cause, String[] sqls) throws SQLException {
        StringBuilder msg = new StringBuilder(cause.getMessage());
        msg.append(" Query: ");
        if(sqls == null) {
            msg.append("[]");
        } else {
            msg.append(Arrays.asList(sqls));
        }
        SQLException e = new SQLException(msg.toString());
        e.setNextException(cause);
        throw e;
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query with a single replacement
     * parameter.
     * 
     * @param conn The connection to use to run the query.
     * @param sql The SQL to execute.
     * @param param The replacement parameter.
     * @return The number of rows updated.
     */
    public static int update(Connection conn, String sql, Object param) throws SQLException {
        return update(conn, sql, new Object[] { param });
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query.
     * 
     * @param conn The connection to use to run the query.
     * @param sql The SQL to execute.
     * @param params The query replacement parameters.
     * @return The number of rows updated.
     */
    public static int update(Connection conn, String sql, Object[] params) throws SQLException {
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            stmt = conn.prepareStatement(sql);
            fillStatement(stmt, params);
            verboseQuery(sql, params);
            rows = stmt.executeUpdate();
        } catch (SQLException e) {
            rethrow(e, sql, params);
        } finally {
            close(stmt);
        }
        return rows;
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query without replacement
     * parameters.
     * 
     * @param conn The connection to use to run the query.
     * @param sql The SQL to execute.
     * @return The number of rows updated.
     */
    public static int update(Connection conn, String sql) throws SQLException {
        Statement stmt = null;
        int rows = 0;
        try {
            stmt = conn.createStatement();
            verboseQuery(sql, (Object[]) null);
            rows = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            rethrow(e, sql, (Object[]) null);
        } finally {
            close(stmt);
        }
        return rows;
    }

    public static boolean call(Connection conn, String sql, Object[] params) throws SQLException {
        CallableStatement proc = null;
        try {
            proc = conn.prepareCall(sql);
            fillStatement(proc, params);
            verboseQuery(sql, params);
            return proc.execute();
        } catch (SQLException e) {
            rethrow(e, sql, params);
        } finally {
            close(proc);
        }
        return false;
    }

    public static boolean call(Connection conn, String sql, Object params) throws SQLException {
        return call(conn, sql, new Object[] { params });
    }

    public static boolean call(Connection conn, String sql) throws SQLException {
        return call(conn, sql, (Object[]) null);
    }

    private static void verboseQuery(String sql, Object[] params) {
        if(showSQL) {
            StringBuilder msg = new StringBuilder(128);
            msg.append("Executing: ").append(sql);
            if(params != null) {
                msg.append(" Parameters: ");
                msg.append(Arrays.asList(params));
            }
            LOG.info(msg.toString());
        }
    }

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
}
