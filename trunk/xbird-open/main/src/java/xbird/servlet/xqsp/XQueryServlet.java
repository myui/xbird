/*
 * @(#)$Id: XQueryServlet.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.servlet.xqsp;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import xbird.util.cache.*;
import xbird.util.lang.PrintUtils;
import xbird.util.xml.SAXWriter;
import xbird.xquery.XQueryException;
import xbird.xquery.XQueryModule;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.dm.ser.Serializer;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.expr.var.Variable.GlobalVariable;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.XQueryParser;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class XQueryServlet extends HttpServlet {
    private static final long serialVersionUID = -4576560807978002697L;

    public static final String XQSP_NSURI = "xbird://xqsp";

    private final ReadWriteLock _lock = new ReentrantReadWriteLock();
    private final Cache<String, CachedQuery> _caches = CacheFactory.createCache(XQueryServlet.class.getName());

    public XQueryServlet() {
        super();
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        process(req, resp);
    }

    protected void process(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        String queryPath = req.getServletPath();
        final CachedQuery loaded;
        try {
            loaded = loadQuery(queryPath);
        } catch (XQueryException e) {
            reportError("Query loading failed: " + queryPath, e, out);
            return;
        } catch (CacheException e) {
            reportError("Caching failed: " + queryPath, e, out);
            return;
        }
        XQueryModule loadedModule = loaded.queryObject;
        XQExpression body = loadedModule.getExpression();
        if(body == null) {
            return;
        }
        // set parameters
        Enumeration<String> paramNames = req.getParameterNames();
        while(paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            String value = req.getParameter(name);
            QualifiedName qname = QNameUtil.parse(name, XQSP_NSURI);
            Variable var = new GlobalVariable(qname, null);
            var.setResult(XString.valueOf(value));
            try {
                loadedModule.putVariable(qname, var);
            } catch (XQueryException e) {
                reportError("Setting parameter failed: " + qname, e, out);
                return;
            }
        }
        // execute
        final Sequence<? extends Item> result;
        try {
            result = body.eval(null, new DynamicContext(loaded.staticEnv));
        } catch (XQueryException e) {
            reportError("Execution failed: " + queryPath, e, out);
            return;
        }
        // serialize        
        SAXWriter writer = new SAXWriter(out);
        Serializer ser = new SAXSerializer(writer);
        try {
            ser.emit(result);
        } catch (XQueryException e) {
            reportError("Serialization failed: " + queryPath, e, out);
            return;
        }
    }

    private CachedQuery loadQuery(String path) throws IOException, XQueryException, CacheException {
        URL url = getServletContext().getResource(path);
        assert (url != null);
        long lastModified = url.openConnection().getLastModified();
        _lock.readLock().lock();
        CachedQuery cached = _caches.get(path);
        if(cached == null || cached.loadTimeStamp < lastModified) {
            if(cached == null) {
                cached = new CachedQuery();
            }
            // parse XQuery expression
            InputStream is = url.openStream();
            XQueryParser parser = new XQueryParser(is);
            StaticContext staticEnv = parser.getStaticContext();
            staticEnv.setBaseURI(path);
            XQueryModule module = parser.parse();
            _lock.readLock().unlock();
            _lock.writeLock().lock();
            // set query cache
            cached.queryObject = module;
            cached.staticEnv = staticEnv;
            cached.loadTimeStamp = System.currentTimeMillis();
            _caches.put(path, cached);
            _lock.writeLock().unlock();
            _lock.readLock().lock();
            // static analysis
            module.staticAnalysis(staticEnv);
        }
        _lock.readLock().unlock();
        return cached;
    }

    private static void reportError(String title, Exception e, PrintWriter out) {
        out.println("<h2>" + title + "</h2>");
        out.println("<pre>");
        String trace = PrintUtils.prettyPrintStackTrace(e);
        out.println(trace);
        out.println("</pre>");
    }

    private static final class CachedQuery {
        private XQueryModule queryObject;
        private StaticContext staticEnv;
        private long loadTimeStamp;

        private CachedQuery() {}

    }

}
