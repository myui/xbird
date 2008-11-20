/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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
package xbird.xquery.func.ext;

import java.io.File;
import java.io.FileNotFoundException;

import xbird.client.command.ImportDocument;
import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.dtm.IDocumentTable;
import xbird.xquery.dm.instance.DocumentTableModel;
import xbird.xquery.dm.instance.DocumentTableModel.DTMDocument;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.node.DocumentTest;
import xbird.xquery.type.xs.StringType;

/**
 * ext:import-document(docPath as xs:string, colPath as xs:string) as document()
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DynamicImport extends BuiltInFunction {
    private static final long serialVersionUID = -9069069603653503384L;
    
    public static final String SYMBOL = EXT_NSPREFIX + ':' + "import-document";

    public DynamicImport() {
        super(SYMBOL, new DocumentTest());
    }

    @Override
    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { StringType.STRING, StringType.STRING });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        if(argv == null || argv.size() != 2) {
            throw new IllegalStateException();
        }
        Item arg1 = argv.getItem(0);
        String docPath = arg1.stringValue();
        Item arg2 = argv.getItem(1);
        String colPath = arg2.stringValue();
        File docFile = new File(docPath);
        if(!docFile.exists()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        DbCollection col = DbCollection.getCollection(colPath);
        if(col == null) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        final IDocumentTable doc;
        try {
            doc = ImportDocument.loadDocument(col, docFile);
        } catch (FileNotFoundException fnf) {
            throw new XQueryException("File not found: " + docPath, fnf);
        } catch (DbException e) {
            throw new XQueryException("Database error", e);
        }
        DocumentTableModel dtm = new DocumentTableModel(doc);
        DTMDocument root = dtm.documentNode();
        return root;
    }

}
