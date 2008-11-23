/*
 * @(#)$Id:DocumentManager.java 2335 2007-07-17 04:14:15Z yui $
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
package xbird.xquery.misc;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import xbird.util.collections.LRUMap;
import xbird.util.collections.SoftHashMap;
import xbird.util.collections.SoftHashMap.SoftValue;
import xbird.util.xml.NamespaceBinder;
import xbird.util.xml.XMLUtils;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.instance.DocumentTableModel;
import xbird.xquery.dm.instance.DocumentTableModel.DTMDocument;
import xbird.xquery.meta.DynamicContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @see DynamicContext
 */
public final class DocumentManager {
    private static final int DOC_CACHE_SIZE = Integer.getInteger("xbird.doc_caches", 8);

    private static final Map<URL, DTMDocument> _sharedCache = new SoftHashMap<URL, DTMDocument>(new LRUMap<URL, SoftValue<URL, DTMDocument>>(DOC_CACHE_SIZE));

    public DocumentManager() {}

    public static void put(URL url, DTMDocument dtm) {
        _sharedCache.put(url, dtm);
    }

    public static DTMDocument get(URL url) {
        return _sharedCache.get(url);
    }

    public boolean isDocumentAvailable(URI docuri) {
        final URL docurl;
        try {
            docurl = docuri.toURL();
        } catch (MalformedURLException e) {
            return false;
        }
        if(_sharedCache.containsKey(docurl)) {
            return true;
        } else {
            final InputStream is;
            try {
                is = docurl.openStream();
            } catch (IOException e) {
                return false;
            }
            try {
                if(is.available() > 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                return false;
            }
        }
    }

    public DTMDocument loadDocument(URL docurl, DynamicContext dynEnv) throws XQueryException {
        if(docurl == null) {
            throw new IllegalArgumentException("docurl is null");
        }
        final String urlStr = docurl.toString();
        final String unescaped = XMLUtils.unescapeXML(urlStr);
        if(!unescaped.equals(urlStr)) {
            try {
                docurl = new URL(unescaped);
            } catch (MalformedURLException e) {
                throw new IllegalStateException("failed to decode as URL: " + unescaped, e);
            }
        }
        final DTMDocument xqdoc;
        if(_sharedCache.containsKey(docurl)) {
            xqdoc = _sharedCache.get(docurl);
        } else {
            final InputStream is;
            boolean parseAsHtml = false;
            try {
                final URLConnection conn = docurl.openConnection();
                // TODO REVIEWME workaround code                
                final String contentType = conn.getContentType();
                if(unescaped.endsWith(".html")
                        || (contentType != null && contentType.contains("html"))) {
                    parseAsHtml = true;
                    try {
                        conn.setRequestProperty("User-agent", "Mozilla/5.0");
                    } catch (IllegalStateException ace) {// already connected                        
                        ;
                    }
                }
                is = conn.getInputStream();
            } catch (IOException e) {
                throw new DynamicError("Openning a document failed: " + unescaped, e);
            }
            final boolean resolveEntity = unescaped.startsWith("http");
            final DocumentTableModel dtm = new DocumentTableModel(parseAsHtml, resolveEntity);
            try {
                dtm.loadDocument(is, dynEnv);
            } catch (XQueryException e) {
                throw new DynamicError("loading a document failed: " + unescaped, e);
            }
            xqdoc = dtm.documentNode();
            xqdoc.setDocumentUri(unescaped);
            _sharedCache.put(docurl, xqdoc);
        }
        // import namespace decl
        Map<String, String> nsmap = xqdoc.documentTable().getDeclaredNamespaces();
        NamespaceBinder nsResolver = dynEnv.getStaticContext().getStaticalyKnownNamespaces();
        nsResolver.declarePrefixs(nsmap);
        return xqdoc;
    }

}
