/*
 * @(#)$Id: QueryRunner.java 3619 2008-03-26 07:23:03Z yui $
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
import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xbird.config.Settings;

/**
 * Executes SQL queries with pluggable strategies for handling 
 * <code>ResultSet</code>s.
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
public final class QueryRunner {

    private static final boolean showSQL;

    private static final Log LOG;
    static {
        final String v = Settings.get("xbird.showsql");
        if("true".equalsIgnoreCase(v)) {
            showSQL = true;
            LOG = LogFactory.getLog(QueryRunner.class);
        } else {
            showSQL = false;
            LOG = null;
        }
    }

    /**
     * The DataSource to retrieve connections from.
     */
    protected final DataSource ds;

    /**
     * Constructor for QueryRunner.  Methods that do not take a 
     * <code>Connection</code> parameter will retrieve connections from this
     * <code>DataSource</code>.
     * 
     * @param ds The <code>DataSource</code> to retrieve connections from.
     */
    public QueryRunner(DataSource ds) {
        this.ds = ds;
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
     * @since DbUtils 1.1
     */
    public int[] batch(Connection conn, String sql, Object[]... params) throws SQLException {
        PreparedStatement stmt = null;
        int[] rows = null;
        try {
            stmt = prepareStatement(conn, sql);
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

    /**
     * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.  The 
     * <code>Connection</code> is retrieved from the <code>DataSource</code> 
     * set in the constructor.  This <code>Connection</code> must be in 
     * auto-commit mode or the update will not be saved. 
     * 
     * @param sql The SQL to execute.
     * @param params An array of query replacement parameters.  Each row in
     * this array is one set of batch replacement values. 
     * @return The number of rows updated per statement.
     * @throws SQLException
     * @since DbUtils 1.1
     */
    public int[] batch(String sql, Object[]... params) throws SQLException {
        Connection conn = prepareConnection();
        try {
            return batch(conn, sql, params);
        } finally {
            close(conn);
        }
    }

    public int[] batch(Connection conn, String... sqls) throws SQLException {
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
            DbUtils.rollback(conn);
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

    public int[] batch(String sqls[]) throws SQLException {
        Connection conn = prepareConnection();
        try {
            return batch(conn, sqls);
        } finally {
            close(conn);
        }
    }

    /**
     * Fill the <code>PreparedStatement</code> replacement parameters with 
     * the given objects.
     * 
     * @param params Query replacement parameters; <code>null</code> is a valid value to pass in.
     */
    protected void fillStatement(PreparedStatement stmt, Object... params) throws SQLException {
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
     * Returns the <code>DataSource</code> this runner is using.  
     * <code>QueryRunner</code> methods always call this method to get the
     * <code>DataSource</code> so subclasses can provide specialized
     * behavior.
     */
    protected DataSource getDataSource() {
        return this.ds;
    }

    /**
     * Factory method that creates and initializes a 
     * <code>PreparedStatement</code> object for the given SQL.  
     * <code>QueryRunner</code> methods always call this method to prepare 
     * statements for them.  Subclasses can override this method to provide 
     * special PreparedStatement configuration if needed.  This implementation
     * simply calls <code>conn.prepareStatement(sql)</code>.
     *  
     * @param conn The <code>Connection</code> used to create the 
     * <code>PreparedStatement</code>
     * @param sql The SQL statement to prepare.
     * @return An initialized <code>PreparedStatement</code>.
     */
    protected PreparedStatement prepareStatement(Connection conn, String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }

    /**
     * Factory method that creates and initializes a 
     * <code>Connection</code> object.  <code>QueryRunner</code> methods 
     * always call this method to retrieve connections from its DataSource.  
     * Subclasses can override this method to provide 
     * special <code>Connection</code> configuration if needed.  This 
     * implementation simply calls <code>ds.getConnection()</code>.
     * 
     * @return An initialized <code>Connection</code>.
     * @since DbUtils 1.1
     */
    protected Connection prepareConnection() throws SQLException {
        return getDataSource().getConnection();
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
    public Object query(Connection conn, String sql, Object param, ResultSetHandler rsh)
            throws SQLException {
        return this.query(conn, sql, new Object[] { param }, rsh);
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
    public Object query(Connection conn, String sql, Object[] params, ResultSetHandler rsh)
            throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Object result = null;
        try {
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params);
            verboseQuery(sql, params);
            rs = this.wrap(stmt.executeQuery());
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
    public Object query(Connection conn, String sql, ResultSetHandler rsh) throws SQLException {
        return this.query(conn, sql, (Object[]) null, rsh);
    }

    /**
     * Executes the given SELECT SQL with a single replacement parameter.
     * The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     * 
     * @param sql The SQL statement to execute.
     * @param param The replacement parameter.
     * @param rsh The handler used to create the result object from 
     * the <code>ResultSet</code>.
     * 
     * @return An object generated by the handler.
     * @throws SQLException
     */
    public Object query(String sql, Object param, ResultSetHandler rsh) throws SQLException {
        return this.query(sql, new Object[] { param }, rsh);
    }

    /**
     * Executes the given SELECT SQL query and returns a result object.
     * The <code>Connection</code> is retrieved from the 
     * <code>DataSource</code> set in the constructor.
     * 
     * @param sql The SQL statement to execute.
     * @param params Initialize the PreparedStatement's IN parameters with 
     * this array.
     * 
     * @param rsh The handler used to create the result object from 
     * the <code>ResultSet</code>.
     * 
     * @return An object generated by the handler.
     */
    public Object query(String sql, Object[] params, ResultSetHandler rsh) throws SQLException {
        Connection conn = this.prepareConnection();
        try {
            return this.query(conn, sql, params, rsh);
        } finally {
            close(conn);
        }
    }

    /**
     * Executes the given SELECT SQL without any replacement parameters.
     * The <code>Connection</code> is retrieved from the
     * <code>DataSource</code> set in the constructor.
     * 
     * @param sql The SQL statement to execute.
     * @param rsh The handler used to create the result object from 
     * the <code>ResultSet</code>.
     * 
     * @return An object generated by the handler.
     */
    public Object query(String sql, ResultSetHandler rsh) throws SQLException {
        return this.query(sql, (Object[]) null, rsh);
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
    public ResultSet fetch(Connection conn, String sql, Object param) throws SQLException {
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
    public ResultSet fetch(Connection conn, String sql, Object... params) throws SQLException {
        ResultSet rs = null;
        try {
            PreparedStatement stmt = prepareStatement(conn, sql);
            fillStatement(stmt, params);
            verboseQuery(sql, params);
            rs = stmt.executeQuery();
        } catch (SQLException e) {
            rethrow(e, sql, params);
        }
        return wrap(rs);
    }

    /**
     * Execute an SQL SELECT query without any replacement parameters.  The
     * caller is responsible for closing the connection.
     * 
     * @param conn The connection to execute the query in.
     * @param sql The query to execute.
     * @return The object represents ResultSet.
     */
    public ResultSet fetch(Connection conn, String sql) throws SQLException {
        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            verboseQuery(sql, (Object[]) null);
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            rethrow(e, sql, (Object[]) null);
        }
        return wrap(rs);
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
    protected void rethrow(SQLException cause, String sql, Object... params) throws SQLException {
        StringBuffer msg = new StringBuffer(cause.getMessage());
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

    protected void rethrow(SQLException cause, String... sqls) throws SQLException {
        StringBuffer msg = new StringBuffer(cause.getMessage());
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
    public int update(Connection conn, String sql, Object param) throws SQLException {
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
    public int update(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            stmt = prepareStatement(conn, sql);
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
     * Executes the given INSERT, UPDATE, or DELETE SQL statement with
     * a single replacement parameter.  The <code>Connection</code> is 
     * retrieved from the <code>DataSource</code> set in the constructor.
     * This <code>Connection</code> must be in auto-commit mode or the 
     * update will not be saved. 
     * 
     * @param sql The SQL statement to execute.
     * @param param The replacement parameter.
     * @return The number of rows updated.
     */
    public int update(String sql, Object param) throws SQLException {
        return update(sql, new Object[] { param });
    }

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement.  The 
     * <code>Connection</code> is retrieved from the <code>DataSource</code> 
     * set in the constructor.  This <code>Connection</code> must be in 
     * auto-commit mode or the update will not be saved. 
     * 
     * @param sql The SQL statement to execute.
     * @param params Initializes the PreparedStatement's IN (i.e. '?') 
     * parameters.
     * @return The number of rows updated.
     */
    public int update(String sql, Object... params) throws SQLException {
        Connection conn = prepareConnection();
        try {
            return update(conn, sql, params);
        } finally {
            close(conn);
        }
    }

    /**
     * Execute an SQL INSERT, UPDATE, or DELETE query without replacement
     * parameters.
     * 
     * @param conn The connection to use to run the query.
     * @param sql The SQL to execute.
     * @return The number of rows updated.
     */
    public int update(Connection conn, String sql) throws SQLException {
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

    /**
     * Executes the given INSERT, UPDATE, or DELETE SQL statement without
     * any replacement parameters. The <code>Connection</code> is retrieved 
     * from the <code>DataSource</code> set in the constructor.  This 
     * <code>Connection</code> must be in auto-commit mode or the update will 
     * not be saved. 
     * 
     * @param sql The SQL statement to execute.
     * @return The number of rows updated.
     */
    public int update(String sql) throws SQLException {
        Connection conn = prepareConnection();
        try {
            return update(conn, sql);
        } finally {
            close(conn);
        }
    }

    public boolean call(Connection conn, String sql, Object... params) throws SQLException {
        CallableStatement proc = null;
        try {
            proc = conn.prepareCall(sql);
            this.fillStatement(proc, params);
            verboseQuery(sql, params);
            return proc.execute();
        } catch (SQLException e) {
            rethrow(e, sql, params);
        } finally {
            close(proc);
        }
        return false;
    }

    public boolean call(Connection conn, String sql, Object params) throws SQLException {
        return call(conn, sql, new Object[] { params });
    }

    public boolean call(Connection conn, String sql) throws SQLException {
        return call(conn, sql, (Object[]) null);
    }

    public boolean call(String sql, Object... params) throws SQLException {
        Connection conn = this.prepareConnection();
        try {
            return call(conn, sql, params);
        } finally {
            close(conn);
        }
    }

    public boolean call(String sql, Object params) throws SQLException {
        return call(sql, new Object[] { params });
    }

    public boolean call(String sql) throws SQLException {
        return call(sql, (Object[]) null);
    }

    /**
     * Wrap the <code>ResultSet</code> in a decorator before processing it.
     * This implementation returns the <code>ResultSet</code> it is given
     * without any decoration.
     *
     * <p>
     * Often, the implementation of this method can be done in an anonymous 
     * inner class like this:
     * </p>
     * <pre> 
     * QueryRunner run = new QueryRunner() {
     *     protected ResultSet wrap(ResultSet rs) {
     *         return StringTrimmedResultSet.wrap(rs);
     *     }
     * };
     * </pre>
     * 
     * @param rs The <code>ResultSet</code> to decorate; never 
     * <code>null</code>.
     * @return The <code>ResultSet</code> wrapped in some decorator. 
     */
    protected ResultSet wrap(ResultSet rs) {
        return rs;
    }

    /**
     * Close a <code>Connection</code>.  This implementation avoids closing if 
     * null and does <strong>not</strong> suppress any exceptions.  Subclasses
     * can override to provide special handling like logging.
     */
    protected void close(Connection conn) {
        DbUtils.closeQuietly(conn);
    }

    /**
     * Close a <code>Statement</code>.  This implementation avoids closing if 
     * null and does <strong>not</strong> suppress any exceptions.  Subclasses
     * can override to provide special handling like logging.
     */
    protected void close(Statement stmt) throws SQLException {
        DbUtils.close(stmt);
    }

    /**
     * Close a <code>ResultSet</code>.  This implementation avoids closing if 
     * null and does <strong>not</strong> suppress any exceptions.  Subclasses
     * can override to provide special handling like logging.
     */
    protected void close(ResultSet rs) throws SQLException {
        DbUtils.close(rs);
    }

    private static void verboseQuery(String sql, Object... params) {
        if(showSQL) {
            StringBuffer msg = new StringBuffer();
            msg.append("Executing: ").append(sql);

            if(params != null) {
                msg.append(" Parameters: ");
                msg.append(Arrays.asList(params));
            }

            LOG.info(msg.toString());
        }
    }

}