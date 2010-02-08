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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SimpleDataSource implements DataSource {

    @Nonnull
    private final String driverClass;
    @Nonnull
    private final String dbUrl;
    @Nullable
    private String userName = null;
    @Nullable
    private String password = null;

    private int loginTimeout = 0;
    private PrintWriter logWriter = null;

    public SimpleDataSource(@CheckForNull String driverClass, @CheckForNull String dbUrl) {
        this(driverClass, dbUrl, null, null);
    }

    public SimpleDataSource(@CheckForNull String driverClass, @CheckForNull String dbUrl, @Nullable String userName, @Nullable String password) {
        if(driverClass == null) {
            throw new IllegalArgumentException();
        }
        if(dbUrl == null) {
            throw new IllegalArgumentException();
        }
        this.driverClass = driverClass;
        this.dbUrl = dbUrl;
        this.userName = userName;
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLoginTimeout() throws SQLException {
        return loginTimeout;
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        this.loginTimeout = seconds;
    }

    @Nullable
    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        this.logWriter = out;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("SimpleDataSource is not a wrapper for: " + iface.getName());
    }

    // ---------------------------------------------------

    public Connection getConnection() throws SQLException {
        return getConnection(userName, password);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver class cannot loaded: " + driverClass, e);
        }
        /*
        final Driver driver;
        try {
            driver = DriverManager.getDriver(dbUrl);
        } catch (Throwable e) {
            throw new SQLException("Driver is not registered: " + driverClass, e);
        }
        if(driver == null) {//sanity check
            throw new SQLException("Driver is not registered: " + driverClass);
        }
        */
        final Connection conn;
        if(userName == null) {
            conn = DriverManager.getConnection(dbUrl);
        } else {
            conn = DriverManager.getConnection(dbUrl, userName, password);
        }
        return conn;
    }

}
