/*
 * @(#)$Id: DataSourceProvider.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import xbird.XBirdError;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public abstract class DataSourceProvider {

    public abstract DataSource setupDataSource(String connectURI);

    protected static final class DriverClassNameResolver {
        private static final Properties MAPPINGS = new Properties();
        static {
            InputStream is = DataSourceProvider.class.getResourceAsStream("driver.properties");
            try {
                MAPPINGS.load(is);
            } catch (IOException e) {
                throw new XBirdError(e);
            }
        }

        private DriverClassNameResolver() {}

        public static String resolve(String dbkind) {
            if(dbkind == null) {
                throw new IllegalArgumentException("Invalid parameter: " + dbkind);
            }
            final String driver = MAPPINGS.getProperty(dbkind);
            if(driver == null) {
                throw new XBirdError("Could not find JDBC-Driver for: " + dbkind);
            }
            return driver;
        }
    }

}
