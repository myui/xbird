/*
 * @(#)$Id: QueryProcessor.java 3619 2008-03-26 07:23:03Z yui $
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
import java.rmi.server.UnicastRemoteObject;
import java.util.ConcurrentIdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.engine.Request;
import xbird.engine.RequestContext;
import xbird.engine.ResponseListener;
import xbird.engine.Request.ReturnType;
import xbird.engine.Request.Signature;
import xbird.engine.remote.RemoteSequence;
import xbird.engine.remote.RemoteSequenceProxy;
import xbird.engine.remote.RunnableRemoteSequenceProxy;
import xbird.engine.remote.ThrottedRemoteSequenceProxy;
import xbird.engine.request.QueryRequest;
import xbird.util.concurrent.ExecutorFactory;
import xbird.util.net.TimeoutSocketProdiver;
import xbird.xquery.XQueryException;
import xbird.xquery.XQueryModule;
import xbird.xquery.XQueryProcessor;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.MarshalledSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.ext.BDQExpr;
import xbird.xquery.expr.func.DirectFunctionCall;
import xbird.xquery.expr.opt.ShippedVariable;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.func.doc.Doc;
import xbird.xquery.func.doc.DocAvailable;
import xbird.xquery.func.doc.FnCollection;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.visitor.AbstractXQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405 AT gmail.com)
 */
public class QueryProcessor extends BackendProcessor {
    private static final Log LOG = LogFactory.getLog(QueryProcessor.class);

    protected final Semaphore _throttle;
    protected final Map<RequestContext, Thread> _runningThreads = new ConcurrentIdentityHashMap<RequestContext, Thread>(12);
    protected final ExecutorService _executors = ExecutorFactory.newCachedThreadPool(60L, "ParallelQP");

    public QueryProcessor(ResponseListener handler) {
        super(handler);
        this._throttle = new Semaphore(ThrottedRemoteSequenceProxy.NUM_THROTTLE);
    }

    public Signature associatedWith() {
        return Signature.QUERY;
    }

    public void fire(RequestContext rc) throws RemoteException {
        rc.setFired(System.currentTimeMillis());

        final Request request = rc.getRequest();
        final Signature rsig = request.getSignature();
        if(rsig != Signature.QUERY) {
            throw new IllegalStateException("Illegal command is passed to QueryProcessor: " + rsig);
        }
        final QueryRequest queryRequest = (QueryRequest) request;
        final String query = queryRequest.getQuery();
        if(query == null) {
            throw new IllegalStateException("query was null for: " + queryRequest);
        }

        // load shipped variables
        final XQueryModule module = new XQueryModule();
        final ShippedVariable[] vars = queryRequest.getShippedVariables();
        if(vars != null) {
            for(ShippedVariable var : vars) {
                try {
                    module.putVariable(var.getVarName(), var);
                } catch (XQueryException e) {
                    throw new RemoteException("failed to declare shipped variable: " + var, e);
                }
            }
        }

        final XQueryProcessor proccessor = new XQueryProcessor(module);
        try {
            proccessor.parse(query, queryRequest.getBaseUri());
        } catch (XQueryException e) {
            LOG.error("parse failed: \n" + query, e);
            rc.setFault(e);
            _resHandler.onResponse(rc);
            return;
        }
        final Sequence<? extends Item> resultSeq;
        _runningThreads.put(rc, Thread.currentThread());
        final DynamicContext dynEnv = new DynamicContext(proccessor.getStaticContext());
        try {
            resultSeq = proccessor.execute(module, dynEnv);
        } catch (Exception e) {
            LOG.error("execute failed: \n" + query, e);
            rc.setFault(e);
            _resHandler.onResponse(rc);
            return;
        } finally {
            _runningThreads.remove(rc);
        }
        final Serializable result = wrapResult(resultSeq, module.getExpression(), queryRequest, dynEnv);
        rc.setResult(result);

        try {
            _resHandler.onResponse(rc);
        } catch (RemoteException re) {
            LOG.error("The respond for the request '" + rc.getRequest().getIdentifier()
                    + "' is failed", re);
        }
    }

    public void cancel(RequestContext rc) {
        final Thread thread = _runningThreads.remove(rc);
        if(thread != null) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        _executors.shutdown();
    }

    protected final Serializable wrapResult(final Sequence result, final XQExpression queryExpr, final QueryRequest request, final DynamicContext dynEnv) {
        assert (dynEnv != null);
        final ReturnType returnType = request.getReturnType();
        final Serializable ret;
        switch(returnType) {
            case SEQUENCE:
                final List<Item> materialized = result.materialize();
                ret = new ValueSequence(materialized, dynEnv);
                break;
            case MARSHALLED_SEQUENCE:
                ret = new MarshalledSequence(result, dynEnv);
                break;
            case AUTO:
            case REDIRECTABLE_MARSHALLED_SEQUENCE:
            case REMOTE_PADED_SEQUENCE: {
                final RedirectableChecker check = new RedirectableChecker();
                try {
                    check.visit(queryExpr, null);
                } catch (XQueryException e) {
                    throw new IllegalStateException(e);
                }
                if(check.isRedirectable()) {
                    MarshalledSequence seq = new MarshalledSequence(result, dynEnv);
                    seq.setRedirectable(true);
                    ret = seq;
                } else if(returnType == ReturnType.REMOTE_PADED_SEQUENCE) {
                    MarshalledSequence seq = new MarshalledSequence(result, dynEnv);
                    seq.setRemotePaging(true);
                    ret = seq;
                } else if(returnType == ReturnType.AUTO) {// treat as THROTTLED_ASYNC_REMOTE_SEQUENCE
                    final ThrottedRemoteSequenceProxy remote = new ThrottedRemoteSequenceProxy(_throttle, result, request);
                    try {
                        UnicastRemoteObject.exportObject(remote, 0, TimeoutSocketProdiver.createClientSocketFactory(), TimeoutSocketProdiver.createServerSocketFactory());
                    } catch (RemoteException e) {
                        throw new IllegalStateException("failed exporting result sequence", e);
                    }
                    ret = new RemoteSequence(remote, result.getType());
                    _executors.execute(remote);
                } else {
                    assert (returnType == ReturnType.REDIRECTABLE_MARSHALLED_SEQUENCE) : returnType;
                    ret = new MarshalledSequence(result, dynEnv);
                }
                break;
            }
            case REMOTE_SEQUENCE: {
                final RemoteSequenceProxy remote = new RemoteSequenceProxy(result, request);
                try {
                    UnicastRemoteObject.exportObject(remote, 0, TimeoutSocketProdiver.createClientSocketFactory(), TimeoutSocketProdiver.createServerSocketFactory());
                } catch (RemoteException e) {
                    throw new IllegalStateException("failed exporting result sequence", e);
                }
                ret = new RemoteSequence(remote, result.getType());
                break;
            }
            case ASYNC_REMOTE_SEQUENCE: {
                final RunnableRemoteSequenceProxy remote = new RunnableRemoteSequenceProxy(result, request);
                try {
                    UnicastRemoteObject.exportObject(remote, 0, TimeoutSocketProdiver.createClientSocketFactory(), TimeoutSocketProdiver.createServerSocketFactory());
                } catch (RemoteException e) {
                    throw new IllegalStateException("failed exporting result sequence", e);
                }
                ret = new RemoteSequence(remote, result.getType());
                _executors.execute(remote);
                break;
            }
            case THROTTLED_ASYNC_REMOTE_SEQUENCE: {
                final ThrottedRemoteSequenceProxy remote = new ThrottedRemoteSequenceProxy(_throttle, result, request);
                try {
                    UnicastRemoteObject.exportObject(remote, 0, TimeoutSocketProdiver.createClientSocketFactory(), TimeoutSocketProdiver.createServerSocketFactory());
                } catch (RemoteException e) {
                    throw new IllegalStateException("failed exporting result sequence", e);
                }
                ret = new RemoteSequence(remote, result.getType());
                _executors.execute(remote);
                break;
            }
            case STRING:
                ret = result.toString();
                break;
            default:
                throw new IllegalStateException("Unexpected return type: " + returnType);
        }
        return ret;
    }

    private static final class RedirectableChecker extends AbstractXQueryParserVisitor {

        private boolean _redirectable = false;

        public RedirectableChecker() {
            super();
        }

        public boolean isRedirectable() {
            return _redirectable;
        }

        @Override
        public XQExpression visit(DirectFunctionCall call, XQueryContext ctxt)
                throws XQueryException {
            if(!_redirectable) {
                final QualifiedName funcName = call.getFuncName();
                assert (funcName != null);
                if(Doc.FUNC_NAME.equals(funcName) || DocAvailable.FUNC_NAME.equals(funcName)
                        || FnCollection.FUNC_NAME.equals(funcName)) {
                    this._redirectable = false;
                } else {
                    return super.visit(call, ctxt);
                }
            }
            return call;
        }

        @Override
        public XQExpression visit(BDQExpr expr, XQueryContext ctxt) throws XQueryException {
            if(!_redirectable) {
                this._redirectable = true;
            }
            return expr;
        }

        @Override
        public XQExpression visit(Variable variable, XQueryContext ctxt) throws XQueryException {
            XQExpression expr = variable.getValue();
            if(expr != null) {
                expr.visit(this, ctxt);
            }
            return variable;
        }

    }

}
