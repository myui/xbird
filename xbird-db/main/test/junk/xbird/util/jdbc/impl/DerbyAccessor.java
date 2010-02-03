/*
 * @(#)$Id: DerbyAccessor.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.jdbc.impl;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xbird.XBirdError;
import xbird.config.Settings;
import xbird.util.jdbc.*;
import xbird.util.jdbc.helper.QueryRunnerFactory;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public class DerbyAccessor extends DBAccessor {

    private static final Log LOG = LogFactory.getLog(DerbyAccessor.class);

    private static final String CHAR_DELIMITER = "\"";

    private static final String DERBY_SYSTEM_HOME = "derby.system.home";
    static {
        if(System.getProperty(DERBY_SYSTEM_HOME) == null) {
            final String dbDir = Settings.get(DERBY_SYSTEM_HOME);
            if(dbDir == null) {
                throw new XBirdError("\"-Dderby.system.home\" is not set.");
            }
            LOG.info("Using Derby database: " + dbDir);
            System.setProperty(DERBY_SYSTEM_HOME, dbDir);
        }
    }

    public DerbyAccessor() {
        super();
    }

    //-----------------------------------------------------
    // Implementation of DBAccessor interface.

    public String getDatabaseName() {
        return "derby";
    }

    public String escapeString(String inputString) {
        int start = inputString.indexOf(CHAR_DELIMITER);
        StringBuffer result;
        //if delimeter is not found inside the string nothing to do
        if(start != -1) {
            result = new StringBuffer(inputString);
            int current;
            int delLength = CHAR_DELIMITER.length();
            while(start != -1) {
                //insert delimter character
                result = result.insert(start, CHAR_DELIMITER);
                current = start + delLength + 1;
                start = result.toString().indexOf(CHAR_DELIMITER, current);
            }
            return result.toString();
        }
        return inputString;
    }

    public String toBytea(byte[] b) {
        return escapeString(b.toString());
    }

    public boolean supportsForUpdate() {
        return true;
    }

    public boolean supportsForBulkload() {
        return false;
    }

    public BulkLoader getBulkLoader() {
        throw new UnsupportedOperationException("Bulk Import of binary data is not supported yet from Derby restriction.");
    }

    protected static class DerbyLoader implements BulkLoader {

        private static final Integer INSERT_MODE = new Integer(0);

        private static final QueryRunner queryRunner = QueryRunnerFactory.create();

        private DerbyLoader() {}

        public void importTable(String tableName, String file) throws SQLException {
            if(tableName == null || file == null) {
                throw new IllegalArgumentException("Arguments MUST be non-null value.");
            }

            final String[] pieces = tableName.split(".");
            final String schemaName;
            if(pieces.length == 2) {
                schemaName = pieces[0];
                tableName = pieces[1];
            } else {
                schemaName = null;
            }

            final Object[] params = new Object[] { schemaName, tableName, file, COLUMN_DELIMITER,
                    CHARACTER_DEMIMITER, ENV_ENCODING, INSERT_MODE };

            // TODO Importing Binary data is not supported in Derby.
            //queryRunner.call(IMPORT_TABLE, params);

            if(LOG.isInfoEnabled())
                LOG.info("update the table '" + tableName + "' successfully.");
        }
    }

}