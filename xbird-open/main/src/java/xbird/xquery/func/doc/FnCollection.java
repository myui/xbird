/*
 * @(#)$Id: FnCollection.java 3619 2008-03-26 07:23:03Z yui $
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
import java.util.*;

import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.util.xml.XMLUtils;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.dtm.IDocumentTable;
import xbird.xquery.dm.dtm.LazyDTMDocument;
import xbird.xquery.dm.dtm.PagingProfile;
import xbird.xquery.dm.instance.DocumentTableModel.DTMDocument;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.*;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.*;
import xbird.xquery.misc.DocumentManager;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;

/**
 * fn:collection.
 * <DIV lang="en">
 * This function takes an xs:string as argument and returns a sequence
 * of nodes obtained by interpreting $arg as an xs:anyURI and resolving it
 * according to the mapping specified in Available collections.
 * <ul>
 * <li>fn:collection() as node()*</li>
 * <li>fn:collection($arg as xs:string?) as node()*</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-collection
 */
public final class FnCollection extends BuiltInFunction {
    private static final long serialVersionUID = -4004290401270090421L;

    public static final String SYMBOL = "fn:collection";
    public static final QualifiedName FUNC_NAME = resolve(SYMBOL);
    private static final String COLLECTION_CATALOG_NSURI = "./catalog.xml";

    public FnCollection() {
        super(FUNC_NAME, TypeRegistry.safeGet("node()*"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        s[0] = new FunctionSignature(getName());
        s[1] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?") });
        return s;
    }

    public INodeSequence<DTMDocument> eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        final Map<String, DTMDocument> catalog;
        if(argv == null) {
            StaticContext statEnv = dynEnv.getStaticContext();
            final Map<String, DTMDocument> defaultCol = statEnv.getDefaultCollection();
            if(defaultCol != null) {
                catalog = defaultCol;
            } else {
                try {
                    DbCollection rootColl = DbCollection.getRootCollection();
                    catalog = rootColl.listDocuments(dynEnv);
                } catch (DbException e) {
                    throw new XQueryException("failed loading default collection", e);
                }
            }
        } else {
            Item arg = argv.getItem(0);
            String argStr = arg.stringValue();
            final String unescapedArg = XMLUtils.unescapeXML(argStr);
            if(unescapedArg.endsWith(".catalog")) {
                URI baseUri = dynEnv.getStaticContext().getBaseURI();
                if(baseUri == null) { // TODO REVIEWME workaround
                    baseUri = dynEnv.getStaticContext().getSystemBaseURI();
                }
                URI resolved = baseUri.resolve(unescapedArg);
                final URL colurl;
                try {
                    colurl = resolved.toURL();
                } catch (MalformedURLException e) {
                    throw new DynamicError("Invalid uri: " + argStr);
                }
                DocumentManager docmgr = dynEnv.getDocumentManager();
                DTMDocument doc = docmgr.loadDocument(colurl, dynEnv);
                catalog = resolveCatalog(doc, dynEnv);
            } else if(unescapedArg.startsWith("/")) {
                DbCollection coll = DbCollection.getCollection(unescapedArg);
                if(coll == null) {
                    catalog = Collections.emptyMap();
                } else {
                    final String filter = DbCollection.getDocumentFilterExp(unescapedArg);
                    try {
                        catalog = coll.listDocuments(filter, true, dynEnv);
                    } catch (DbException e) {
                        throw new XQueryException("failed loading collection: " + unescapedArg, e);
                    }
                }
            } else {
                throw new XQueryException("Illeagl collection name: " + unescapedArg);
            }
        }
        Profiler profiler = dynEnv.getProfiler();
        final Collection<DTMDocument> docs = catalog.values();
        final NodeSequence<DTMDocument> ret = new NodeSequence<DTMDocument>(dynEnv);
        for(DTMDocument d : docs) {
            ret.addItem(d);
            if(profiler != null && !(d instanceof LazyDTMDocument)) {
                IDocumentTable doctbl = d.documentTable();
                PagingProfile profile = doctbl.getPagingProfile();
                if(profile != null) {
                    profile.setProfiler(profiler);
                }
            }
        }
        return ret;
    }

    private static Map<String, DTMDocument> resolveCatalog(DTMDocument catalog, DynamicContext dynEnv)
            throws XQueryException {
        QualifiedName top = catalog.nodeName();
        if(QNameUtil.isSame(top, COLLECTION_CATALOG_NSURI, "collection")) {
            Map<String, DTMDocument> map = new HashMap<String, DTMDocument>();
            for(XQNode child = catalog.firstChild(); child != null; child = child.nextSibling()) {
                QualifiedName sec = child.nodeName();
                if(QNameUtil.isSame(sec, COLLECTION_CATALOG_NSURI, "document")) {
                    String urlstr = child.stringValue();
                    final URL url;
                    try {
                        url = new URL(urlstr);
                    } catch (MalformedURLException e) {
                        throw new IllegalStateException("/collection/document/text() value is malformed url: "
                                + urlstr);
                    }
                    DocumentManager docman = dynEnv.getDocumentManager();
                    DTMDocument doc = docman.loadDocument(url, dynEnv);
                    map.put(urlstr, doc);
                }
            }
            return map;
        } else {
            return Collections.<String, DTMDocument> emptyMap();
        }
    }
}
