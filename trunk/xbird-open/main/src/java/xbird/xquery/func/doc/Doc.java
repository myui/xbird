/*
 * @(#)$Id: Doc.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.doc;

import java.net.*;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.instance.XQueryDataModel;
import xbird.xquery.dm.instance.DocumentTableModel.DTMDocument;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.DocumentManager;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.*;
import xbird.xquery.type.Type.Occurrence;
import xbird.xquery.type.node.DocumentTest;

/**
 * fn:doc($uri as xs:string?) as document-node()?.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class Doc extends BuiltInFunction {
    private static final long serialVersionUID = 4068874949328220264L;

    public static final String SYMBOL = "fn:doc";
    public static final QualifiedName FUNC_NAME = resolve(SYMBOL);

    public Doc() {
        super(FUNC_NAME, new SequenceType(new DocumentTest(), Occurrence.OCC_ZERO_OR_ONE));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?") });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        Item docuri = argv.getItem(0);
        if(docuri.isEmpty()) {
            return XQueryDataModel.createDocument();
        } else {
            final String docName = docuri.stringValue();
            URI baseuri = dynEnv.getStaticContext().getBaseURI();
            if(baseuri == null) { // TODO REVIEWME workaround
                baseuri = dynEnv.getStaticContext().getSystemBaseURI();
            }
            final URI resolvedUri = baseuri.resolve(docName);
            final URL docurl;
            try {
                docurl = resolvedUri.toURL();
            } catch (MalformedURLException e) {
                throw new DynamicError("Malformed URL: " + resolvedUri, e);
            }
            DocumentManager docmgr = dynEnv.getDocumentManager();
            DTMDocument doc = docmgr.loadDocument(docurl, dynEnv);
            return doc;
        }
    }
}
