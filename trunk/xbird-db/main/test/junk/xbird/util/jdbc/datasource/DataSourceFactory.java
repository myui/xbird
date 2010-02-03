/*
 * @(#)$Id: DataSourceFactory.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.config.Settings;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public final class DataSourceFactory {

    private DataSourceFactory() {}

    public static DataSource createDataSource() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        static final DataSource INSTANCE;
        static {
            final String connectUri = Settings.get("xbird.db.protocol")
                    + Settings.get("xbird.db.dbname");
            final DataSourceProvider provider = new DbcpDataSourceProvider();
            INSTANCE = provider.setupDataSource(connectUri);
        }
    }

}