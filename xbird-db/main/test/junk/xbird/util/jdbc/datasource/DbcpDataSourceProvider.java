/*
 * @(#)$Id: DbcpDataSourceProvider.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.jdbc.datasource;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import xbird.config.Settings;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public class DbcpDataSourceProvider extends DataSourceProvider {

    private static final int DEFAULT_MAXWAIT = 10000;

    private static final int DEFAULT_INITIAL_POLLSIZE = 2;

    private static final int MAX_OPEN_PREPARED_STATEMENTS = 12;

    public DbcpDataSourceProvider() {
        super();
    }

    public DataSource setupDataSource(String connectURI) {
        // creates DataSource
        BasicDataSource ds = new BasicDataSource();

        // for debugging.
        if(Settings.isLoggingEnabled) {
            ds.setAccessToUnderlyingConnectionAllowed(true);
        }

        ds.setDriverClassName(DriverClassNameResolver.resolve(Settings.get("xbird.db.kind")));

        // sets up DataSource
        ds.setUrl(connectURI);
        final String dbuser = Settings.get("xbird.db.user");
        final String dbpasswd = Settings.get("xbird.db.passwd");
        if(dbuser != null && dbuser.length() != 0) {
            ds.setUsername(dbuser);
            ds.setPassword(dbpasswd);
        }

        // addtinal settings.
        final String maxactive = Settings.get("xbird.db.pool.maxactive");
        if(maxactive != null)
            ds.setMaxActive(Integer.parseInt(maxactive));

        final String maxidle = Settings.get("xbird.db.pool.maxidle");
        if(maxidle != null)
            ds.setMaxIdle(Integer.parseInt(maxidle));

        final String maxwait = Settings.get("xbird.db.pool.maxwait");
        ds.setMaxWait(maxwait == null ? DEFAULT_MAXWAIT : Integer.parseInt(maxwait));

        ds.setDefaultAutoCommit(true);
        //ds.setDefaultReadOnly(false);

        final String initialsize = Settings.get("xbird.db.pool.initialsize");
        ds.setInitialSize(initialsize == null ? DEFAULT_INITIAL_POLLSIZE
                : Integer.parseInt(initialsize));

        // sets up for PreparedStatements.
        ds.setPoolPreparedStatements(true);

        final String maxOpenPreparedStatements = Settings.get("xbird.db.pool.statement.cache_size");
        ds.setMaxOpenPreparedStatements(maxOpenPreparedStatements == null ? MAX_OPEN_PREPARED_STATEMENTS
                : Integer.parseInt(maxOpenPreparedStatements));

        return ds;
    }

}