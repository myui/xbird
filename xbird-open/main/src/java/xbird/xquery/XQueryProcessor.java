/*
 * @(#)$Id: XQueryProcessor.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.opt.ThreadedVariable;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.Profiler;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.parser.SyntaxError;
import xbird.xquery.parser.TokenMgrError;
import xbird.xquery.parser.XQueryParser;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class XQueryProcessor {
    private static final Log PROFILE_LOG = LogFactory.getLog("PROFILER");

    private final XQueryModule _module;
    private StaticContext _statEnv = null; //TODO REVIEWME
    private Sequence<? extends Item> contextItem = null;

    public XQueryProcessor() {
        this(null);
    }

    public XQueryProcessor(XQueryModule module) {
        super();
        this._module = module;
    }

    public void setStaticContext(StaticContext statEnv) {
        this._statEnv = statEnv;
    }

    public StaticContext getStaticContext() {
        return _statEnv;
    }

    public void setContextItem(Sequence<? extends Item> contextItem) {
        this.contextItem = contextItem;
    }

    public XQueryModule parse(String query) throws XQueryException {
        return parse(new StringReader(query));
    }

    public XQueryModule parse(String query, URI baseUri) throws XQueryException {
        return parse(new StringReader(query), baseUri);
    }

    public XQueryModule parse(InputStream is) throws XQueryException {
        return parse(is, null);
    }

    public XQueryModule parse(InputStream is, URI baseUri) throws XQueryException {
        return parse(new InputStreamReader(is), baseUri);
    }

    public XQueryModule parse(Reader r) throws XQueryException {
        return parse(r, null);
    }

    public XQueryModule parse(Reader r, URI baseUri) throws XQueryException {
        final XQueryParser parser;
        try {
            parser = new XQueryParser(r);
        } catch (TokenMgrError e) {
            throw new SyntaxError("err:XPST0003", e);
        }
        if(_statEnv != null) {
            parser.setStaticContext(_statEnv);
        } else {
            this._statEnv = parser.getStaticContext();
        }
        if(baseUri != null) {
            _statEnv.setSystemBaseURI(baseUri);
        }
        if(_module != null) {
            parser.setCurrentModule(_module);
        }
        XQueryModule m = parser.parse();
        return m;
    }

    public void compile(XQueryModule module) throws XQueryException {
        this.compile(module, _statEnv);
    }

    public void compile(XQueryModule module, StaticContext env) throws XQueryException {
        module.staticAnalysis(env);
    }

    public Sequence<? extends Item> execute(XQueryModule module) throws XQueryException {
        if(_statEnv == null) {
            throw new IllegalStateException("compile() is not performed.");
        }
        return execute(module, new DynamicContext(_statEnv));
    }

    public Sequence<? extends Item> execute(XQueryModule module, DynamicContext dynEnv)
            throws XQueryException {
        // static analysis
        StaticContext statEnv = dynEnv.getStaticContext();
        module.staticAnalysis(statEnv);
        // dynamic evaluation      
        XQExpression body = module.getExpression();
        dynEnv.setQueryExpression(body);
        evalHook(dynEnv);
        Sequence<? extends Item> result = body.eval(contextItem, dynEnv);
        if(PROFILE_LOG.isInfoEnabled()) {
            Profiler profiler = dynEnv.getProfiler();
            if(profiler != null && profiler.getDTMReads() > 0) {
                PROFILE_LOG.info(profiler);
            }
        }
        return result;
    }

    public void execute(XQueryModule module, XQEventReceiver handler) throws XQueryException {
        if(_statEnv == null) {
            throw new IllegalStateException("compile() is not performed.");
        }
        execute(module, new DynamicContext(_statEnv), handler);
    }

    public void execute(XQueryModule module, DynamicContext dynEnv, XQEventReceiver handler)
            throws XQueryException {
        // static analysis
        StaticContext statEnv = dynEnv.getStaticContext();
        module.staticAnalysis(statEnv);
        // dynamic evaluation
        XQExpression body = module.getExpression();
        dynEnv.setQueryExpression(body);
        evalHook(dynEnv);
        body.evalAsEvents(handler, contextItem, dynEnv);
        if(PROFILE_LOG.isInfoEnabled()) {
            Profiler profiler = dynEnv.getProfiler();
            if(profiler != null && profiler.getDTMReads() > 0) {
                PROFILE_LOG.info(profiler);
            }
        }
    }

    private static void evalHook(DynamicContext dynEnv) {
        final StaticContext statEnv = dynEnv.getStaticContext();
        final List<ThreadedVariable> threadedVars = statEnv.getThreadedVariables();
        for(ThreadedVariable threadedVar : threadedVars) {
            final String threadName = "ThreadedVariable#" + threadedVar.getName();
            threadedVar.setDynamicContext(dynEnv);
            final Thread task = new Thread(threadedVar, threadName);
            task.setDaemon(true);
            task.start();
        }
    }

}
