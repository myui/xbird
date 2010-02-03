/*
 * @(#)$Id: DBConnectionFactory.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.jdbc.helper;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A helper class for creating a connection.
 * 
 * @author Makoto YUI
 */
public final class DBConnectionFactory {

    private DBConnectionFactory() {}

    public static Connection getConnection() throws SQLException {
        return getConnection(false);
    }

    public static Connection getConnection(boolean sharable) throws SQLException {
        try {
            return DBAccessorFactory.create().getConnection(sharable);
        } catch (Exception e) {
            throw new SQLException("Exception caused while create connection");
        }
    }

}
