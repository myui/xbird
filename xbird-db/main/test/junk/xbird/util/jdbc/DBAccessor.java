/*
 * @(#)$Id: DBAccessor.java 3619 2008-03-26 07:23:03Z yui $
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

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xbird.util.jdbc.datasource.DataSourceFactory;

/**
 * An utility class manages database accesses.
 * <DIV lang="en">
 * This class also provides Connection Pooling feature and 
 * PreparedStatement Caching feature.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public abstract class DBAccessor {

    private static final Log LOG = LogFactory.getLog(DBAccessor.class);

    private static final DataSource dataSource = DataSourceFactory.createDataSource();

    /** Database connection object. */
    protected static Connection sharedConnection;

    public Connection getConnection() {
        return getConnection(false);
    }

    /**
     * Returns sharable connection or new connection.
     * 
     * @param share Is requirement sharable connection. 
     */
    public Connection getConnection(boolean share) {
        if(share && sharedConnection != null) {
            return sharedConnection;
        } else {
            Connection conn = createConnection();
            if(share && sharedConnection == null) {
                sharedConnection = conn;
            }
            return conn;
        }
    }

    public abstract String getDatabaseName();

    /**
     * Escapes SQL String.
     */
    public abstract String escapeString(String inputString);

    /**
     * Converts java byte array to the database specific value.
     */
    public abstract String toBytea(byte[] b);

    public abstract boolean supportsForUpdate();

    public abstract boolean supportsForBulkload();

    public abstract BulkLoader getBulkLoader();

    public final DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Closes the DataSource.
     */
    public final void shutdownDataSource(DataSource ds) throws SQLException {
        // TODO who call me.
        BasicDataSource bds = (BasicDataSource) ds;
        bds.close();
    }

    /**
     * Connection to db and return the connection.
     */
    private static Connection createConnection() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            LOG.fatal(e);
        }

        if(conn == null)
            LOG.fatal("getConnection(): could not get connection.");
        return conn;
    }

}