/*
 * @(#)$Id: PGAccessor.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.util.jdbc.*;
import xbird.util.jdbc.helper.QueryRunnerFactory;

/**
 * This is a utility class managing a database connection.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public class PGAccessor extends DBAccessor {

    private static final Log LOG = LogFactory.getLog(PGAccessor.class);

    public PGAccessor() {
        super();
    }

    public String getDatabaseName() {
        return "postgres";
    }

    /**
     * Escapes SQL String.
     */
    public final String escapeString(String inputString) {
        int strlen = inputString.length();
        if(strlen == 0)
            return null;
        int len = 2 + strlen + (strlen / 10);
        // make enough buffer
        StringBuffer output = new StringBuffer(len);
        for(int i = 0; i < strlen; ++i) {
            char c = inputString.charAt(i);
            switch(c) {
                case '\\':
                case '\'':
                    output.append('\\');
                    output.append(c);
                    break;
                case '\n':
                    output.append("\\n");
                case '\t':
                    output.append("\\t");
                    break;
                case '\0':
                    throw new IllegalArgumentException("\\0 not allowed");
                default:
                    output.append(c);
            }
        }
        return output.toString();
    }

    /**
     * This is just a wrapper method to 
     * <code>org.postgresql.util.PGbytea.toPGString()</code>.
     * if exception caused, return null.
     * 
     * representation between Java Byte Array and Postgres Bytea is different.
     * see detail on <code>org.postgresql.util.PGbytea.toPGString()</code>
     * 
     * @return PG bytea string of the specified byte array.
     */
    public final String toBytea(byte[] b) {
        return toPGString(b);
    }

    /**
     * Converts a java byte[] into a PG bytea string (i.e. the text
     * representation of the bytea data type)
     */
    private static String toPGString(byte[] p_buf) {
        if(p_buf == null)
            return null;
        StringBuffer l_strbuf = new StringBuffer(2 * p_buf.length);
        for(int i = 0; i < p_buf.length; i++) {
            int l_int = (int) p_buf[i];
            if(l_int < 0) {
                l_int = 256 + l_int;
            }
            //we escape the same non-printable characters as the backend
            //we must escape all 8bit characters otherwise when convering
            //from java unicode to the db character set we may end up with
            //question marks if the character set is SQL_ASCII
            if(l_int < 040 || l_int > 0176) {
                //escape charcter with the form \000, but need two \\ because of
                //the parser
                l_strbuf.append("\\");
                l_strbuf.append((char) (((l_int >> 6) & 0x3) + 48));
                l_strbuf.append((char) (((l_int >> 3) & 0x7) + 48));
                l_strbuf.append((char) ((l_int & 0x07) + 48));
            } else if(p_buf[i] == (byte) '\\') {
                //escape the backslash character as \\, but need four \\\\ because
                //of the parser
                l_strbuf.append("\\\\");
            } else {
                //other characters are left alone
                l_strbuf.append((char) p_buf[i]);
            }
        }
        return l_strbuf.toString();
    }

    public boolean supportsForUpdate() {
        return true;
    }

    public boolean supportsForBulkload() {
        return true;
    }

    public BulkLoader getBulkLoader() {
        return PGLoader.INSTANCE;
    }

    protected static class PGLoader implements BulkLoader {

        private static final PGLoader INSTANCE = new PGLoader();

        private static final QueryRunner queryRunner = QueryRunnerFactory.create();

        private PGLoader() {}

        public void importTable(String tableName, String file) throws SQLException {
            if(tableName == null || file == null) {
                throw new IllegalArgumentException("Arguments MUST be non-null value.");
            }
            final String sql = "COPY " + tableName + " FROM '" + file + "'" + " WITH DELIMITER '"
                    + COLUMN_DELIMITER + "'";
            int count = queryRunner.update(sql);
            if(LOG.isInfoEnabled())
                LOG.info("update the table '" + tableName + "' (" + count + ") successfully.");
        }
    }
}