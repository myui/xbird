/*
 * @(#)$Id: BulkLoader.java 3619 2008-03-26 07:23:03Z yui $
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

import java.sql.SQLException;

import xbird.config.Settings;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public interface BulkLoader {

    static final String COLUMN_DELIMITER = Settings.get("xbird.db.delimiter");

    static final String CHARACTER_DEMIMITER = Settings.get("xbird.db.character_delimiter");

    static final String ENV_ENCODING = Settings.get("xbird.xml.encoding");

    public void importTable(String tableName, String file) throws SQLException;

}
