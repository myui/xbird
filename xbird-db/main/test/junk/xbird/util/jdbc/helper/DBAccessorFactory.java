/*
 * @(#)$Id: DBAccessorFactory.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.config.Settings;
import xbird.util.jdbc.DBAccessor;
import xbird.util.jdbc.impl.DerbyAccessor;
import xbird.util.jdbc.impl.PGAccessor;

/**
 * Database accessor factory.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI <yuin405+xbird@gmail.com>
 */
public final class DBAccessorFactory {

    // ---------------------------------------------------------
    // Constants

    private static final String KIND_POSTGRES = "postgres";

    private static final String KIND_DERBY = "derby";

    private static final String DB_KIND = Settings.get("xbird.db.kind").toLowerCase();

    // ---------------------------------------------------------
    // Shared variables

    private static DBAccessor dbAccessor = null;

    private DBAccessorFactory() {}

    /**
     * Creates Database accessor.
     */
    public static DBAccessor create() {
        if(dbAccessor != null)
            return dbAccessor;
        final DBAccessor dba;
        if(KIND_POSTGRES.equals(DB_KIND)) {
            dba = new PGAccessor();
        } else if(KIND_DERBY.equals(DB_KIND)) {
            dba = new DerbyAccessor();
        } else {
            throw new UnsupportedOperationException("Unsupported DB kind: " + DB_KIND);
        }
        dbAccessor = dba; // set reference.
        return dba;
    }

}
