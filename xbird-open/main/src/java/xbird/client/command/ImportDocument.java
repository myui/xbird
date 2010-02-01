/*
 * @(#)$Id: ImportDocument.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.client.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import xbird.config.Settings;
import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.storage.tx.Transaction;
import xbird.util.cmdline.CommandBase;
import xbird.util.cmdline.CommandException;
import xbird.util.io.FileUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.dtm.BigDocumentTable;
import xbird.xquery.dm.dtm.DocumentTable;
import xbird.xquery.dm.dtm.IDocumentTable;
import xbird.xquery.dm.dtm.MemoryMappedDocumentTable;
import xbird.xquery.dm.instance.DocumentTableModel;

/**
 * Command syntax: import document 'filespec' recursive?
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class ImportDocument extends CommandBase {

    private static final boolean USE_MMAP = Boolean.parseBoolean(Settings.get("xbird.database.dtm.use_mmap", "false"));

    private static final long FIVE_HUNDREDS_MEGA_BYTES = 500 * 1024 * 1024;
    private static final long GIGA_BYTES = 1024 * 1024 * 1024;
    private static final long TWO_GIGA_BYTES = 2 * GIGA_BYTES;

    private static final String[] COMMAND = new String[] { "import", "document" };

    private final Session session;
    private boolean recursive = false;

    public ImportDocument(Session session) {
        super();
        this.session = session;
    }

    public boolean match(String[] args) {
        switch(args.length) {
            case 4:
                if("recursive".equals(args[3])) {
                    this.recursive = true;
                    return true;
                } else {
                    return false;
                }
            case 3:
                if(COMMAND[0].equals(args[0]) && COMMAND[1].equals(args[1])) {
                    return true;
                }
            default:// error case
                return false;
        }
    }

    public boolean process(String[] args) throws CommandException {
        DbCollection col = session.getContextCollection();
        if(col == null) {
            throwException("Context collection is not set");
        }
        String docpath = args[2];
        File docFile = new File(docpath);
        if(!docFile.exists()) {
            throwException("File not exists: " + docpath);
        }
        if(docFile.isDirectory()) {
            final List<File> files = FileUtils.listFiles(docFile, ".xml", recursive);
            for(File f : files) {
                try {
                    loadDocument(col, f);
                } catch (Exception e) {
                    rethrowException(e);
                }
            }
        } else {
            try {
                loadDocument(col, docFile);
            } catch (Exception e) {
                rethrowException(e);
            }
        }
        return true;
    }

    public static IDocumentTable loadDocument(DbCollection col, File docFile)
            throws FileNotFoundException, XQueryException, DbException {
        long fileSize = docFile.length();
        IDocumentTable doc = preferedDocumentTable(col, docFile, fileSize);
        DocumentTableModel dtm = new DocumentTableModel(doc);
        dtm.loadDocument(new FileInputStream(docFile));
        col.putDocument(new Transaction(), docFile.getName(), doc);
        return doc;
    }

    private static IDocumentTable preferedDocumentTable(DbCollection col, File docFile, long filesize) {
        final String fileName = FileUtils.getFileName(docFile);
        if(USE_MMAP) {
            return new MemoryMappedDocumentTable(col, fileName, null, false);
        } else {
            if(filesize < FIVE_HUNDREDS_MEGA_BYTES) {
                return new DocumentTable(col, fileName);
            } else if(filesize < TWO_GIGA_BYTES) {
                return new DocumentTable.PersistentDocumentTable(col, fileName);
            } else {
                return new BigDocumentTable.PersistentBigDocumentTable(col, fileName);
            }
        }
    }

    public String usage() {
        return constructHelp("Imports a document into the context collection", "import document 'filespec' recursive?");
    }

}
