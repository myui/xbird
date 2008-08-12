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
package xbird.client.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.util.lang.ArrayUtils;
import xbird.util.resource.PropertyMap;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.dtm.IDocumentTable;
import xbird.xquery.dm.instance.DocumentTableModel;
import xbird.xquery.dm.instance.DocumentTableModel.DTMDocument;
import xbird.xquery.dm.labeling.LabelingHandler;
import xbird.xquery.meta.DynamicContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CreateIndex extends CommandBase {

    private static final String[] COMMAND = new String[] { "create", null, "index" };
    private static final String[] IDXTYPE = new String[] { "value", "path", "fulltext" };
    private static final int VALUE_IDX = 0, PATH_IDX = 1, FULLTEXT_IDX = 2;
    private static final String DATATYPE_CMD = "as";

    public CreateIndex(Session session) {
        super(session);
    }

    public boolean match(String[] args) {
        if(args.length < COMMAND.length) {
            return false;
        }
        if(!(COMMAND[0].equals(args[0]) & COMMAND[2].equals(args[2]))) {
            return false;
        }
        if(!ArrayUtils.contains(IDXTYPE, args[1])) {
            return false;
        }
        return true;
    }

    public boolean process(String[] args) throws CommandFailedException {
        final DbCollection col = session.getContextCollection();
        if(col == null) {
            throwException("Context collection is not set");
        }
        final int type = ArrayUtils.indexOf(IDXTYPE, args[1]);
        if(type == ArrayUtils.INDEX_NOT_FOUND) {
            throwException("Unexpected command");
        }
        final boolean status;
        switch(type) {
            case PATH_IDX: {
                try {
                    status = createPathIndex(col);
                } catch (Exception e) {
                    rethrowException(e);
                    return false;
                }
                break;
            }
            case VALUE_IDX: {
                final String[] result = parsePattern(args, DATATYPE_CMD);
                status = createValueIndex(col, result[0]);
                break;
            }
            case FULLTEXT_IDX: {
                status = createFullTextIndex(col);
                break;
            }
            default:
                throw new IllegalStateException("Illegal type: " + type);
        }
        return status;
    }

    private static boolean createPathIndex(final DbCollection col) throws DbException, IOException,
            XQueryException {
        Map<String, DTMDocument> map = col.listDocuments(DynamicContext.DUMMY);
        for(Map.Entry<String, DTMDocument> entry : map.entrySet()) {
            String docName = entry.getKey();
            DTMDocument doc = entry.getValue();
            IDocumentTable doctbl = doc.documentTable();
            DocumentTableModel dtm = doc.getDataModel();
            PropertyMap props = col.getCollectionProperties();
            LabelingHandler handler = new LabelingHandler(doctbl, col, docName, props);
            dtm.export(0L, handler);
            doctbl.close();
        }
        return true;
    }

    private static boolean createValueIndex(final DbCollection col, final String dataType) {
        // TODO
        return false;
    }

    private static boolean createFullTextIndex(final DbCollection col) {
        // TODO
        return false;
    }

    private static String[] parsePattern(final String[] args, final String... commands) {
        final int ptnlen = commands.length;
        final String[] result = new String[ptnlen];
        int ri = 0;
        final int arglen = args.length;
        for(int i = COMMAND.length; i + 1 < arglen; i += 2) {
            final String s = args[i];
            final int pi = ArrayUtils.indexOf(commands, s, ri);
            if(pi == ArrayUtils.INDEX_NOT_FOUND) {
                continue;
            }
            result[pi] = args[i + 1];
            ri = pi + 1;
            if(ri >= ptnlen) {
                break;
            }
        }
        return result;
    }

    public String usage() {
        return constructHelp("Create an index for the specified collection", "create 'indexType' index [as 'dataType']");
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(parsePattern("create value index as xs:dateTime".split(" "), DATATYPE_CMD)));
    }

}
