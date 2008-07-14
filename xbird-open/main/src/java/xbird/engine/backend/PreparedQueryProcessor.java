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
package xbird.engine.backend;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.engine.*;
import xbird.engine.Request.Signature;
import xbird.engine.request.PreparedQueryRequest;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.opt.ThreadedVariable;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.meta.*;
import xbird.xquery.parser.visitor.AbstractXQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class PreparedQueryProcessor extends QueryProcessor {
    private static final Log LOG = LogFactory.getLog(PreparedQueryProcessor.class);

    public PreparedQueryProcessor(ResponseListener handler) {
        super(handler);
    }

    @Override
    public Signature associatedWith() {
        return Signature.PREPARED_QUERY;
    }

    @Override
    public void fire(RequestContext rc) throws RemoteException {
        rc.setFired(System.currentTimeMillis());

        final Request request = rc.getRequest();
        final Signature rsig = request.getSignature();
        if(rsig != Signature.PREPARED_QUERY) {
            throw new IllegalStateException("Illegal command is passed to PreparedQueryProcessor: "
                    + rsig);
        }
        final PreparedQueryRequest queryRequest = (PreparedQueryRequest) request;
        final XQExpression queryExpr = queryRequest.getCompiledExpression();
        if(queryExpr == null) {
            throw new IllegalStateException("query was null for: " + queryRequest);
        }

        final ThreadedVariableCollector collector = new ThreadedVariableCollector();
        try {
            collector.visit(queryExpr, null);
        } catch (XQueryException e) {
            throw new RemoteException("failed runnning ThreadVariables", e);
        }
        final DynamicContext dynEnv = new DynamicContext(new StaticContext());
        dynEnv.setQueryExpression(queryExpr);
        collector.invokeThreadedVariables(dynEnv); // TODO REVIEWME cancelling

        final Sequence<? extends Item> resultSeq;
        _runningThreads.put(rc, Thread.currentThread());
        try {
            resultSeq = queryExpr.eval(null, dynEnv);
        } catch (XQueryException e) {
            LOG.error("execute failed: \n" + queryExpr, e);
            rc.setFault(e);
            _resHandler.onResponse(rc);
            return;
        } finally {
            _runningThreads.remove(rc);
        }

        final Serializable result = wrapResult(resultSeq, queryExpr, queryRequest, dynEnv);
        rc.setResult(result);

        try {
            _resHandler.onResponse(rc);
        } catch (RemoteException re) {
            LOG.error("The respond for the request '" + rc.getRequest().getIdentifier()
                    + "' is failed", re);
        }
    }

    private static final class ThreadedVariableCollector extends AbstractXQueryParserVisitor {

        private final List<ThreadedVariable> _threadedVars = new ArrayList<ThreadedVariable>(4);

        public ThreadedVariableCollector() {
            super();
        }

        public void invokeThreadedVariables(final DynamicContext dynEnv) {
            for(ThreadedVariable v : _threadedVars) {
                v.setDynamicContext(dynEnv);
                final String threadName = "ThreadedVariable#" + v.getName();
                final Thread task = new Thread(v, threadName);
                task.setDaemon(true);
                task.start();
            }
        }

        @Override
        public XQExpression visit(Variable variable, XQueryContext ctxt) throws XQueryException {
            if(variable instanceof ThreadedVariable) {
                assert (!_threadedVars.contains(variable));
                ThreadedVariable thVar = (ThreadedVariable) variable;
                _threadedVars.add(thVar);
                return variable;
            }
            return super.visit(variable, ctxt);
        }

    }
}
