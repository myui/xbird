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
package xbird.xquery.dm.dtm.hooked;

import java.io.File;

import xbird.storage.DbCollection;
import xbird.util.csv.CsvWriter;
import xbird.util.resource.PropertyMap;
import xbird.xquery.dm.dtm.DocumentTable.PersistentDocumentTable;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ProfiledPersistentDocumentTable extends PersistentDocumentTable
        implements IProfiledDocumentTable {
    private static final long serialVersionUID = -1068490396439849405L;

    private static final int PAGE_SHIFT = 9;

    private final CsvWriter _csv;
    private boolean enable = true;
    private int prevPage = -1;

    public ProfiledPersistentDocumentTable(String file, DbCollection coll, String docName, PropertyMap docProps) {
        super(coll, docName, docProps);
        File f = new File(file);
        System.err.println("Access pattern to the XMLDocumentTable is profiled to "
                + f.getAbsolutePath());
        final CsvWriter csv = new CsvWriter(f);
        csv.writeRow("page");
        this._csv = csv;
    }

    public void setProfiling(boolean enable) {
        this.enable = enable;
    }

    @Override
    public long dataAt(long at) {
        if(enable) {
            long lp = at >> PAGE_SHIFT;
            if(lp > 0x7fffffffL) {
                throw new IllegalStateException("Illegal page number: " + lp);
            }
            int page = (int) lp;
            if(prevPage != page) {
                _csv.writeRow(Integer.toString(page));
            }
            this.prevPage = page;
        }
        return super.dataAt(at);
    }
}
