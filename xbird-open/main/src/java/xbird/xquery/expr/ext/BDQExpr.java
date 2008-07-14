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
package xbird.xquery.expr.ext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.engine.XQEngineClient;
import xbird.engine.Request.ReturnType;
import xbird.engine.remote.RemoteSequence;
import xbird.engine.remote.RemoteSequenceProxy;
import xbird.engine.request.CompileRequest;
import xbird.engine.request.PreparedQueryRequest;
import xbird.engine.request.QueryRequest;
import xbird.util.concurrent.ExecutorFactory;
import xbird.util.concurrent.ExecutorUtils;
import xbird.util.net.TimeoutSocketProdiver;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.SingleItem;
import xbird.xquery.dm.value.sequence.ChainedSequence;
import xbird.xquery.dm.value.sequence.MarshalledSequence;
import xbird.xquery.dm.value.sequence.PipelinedSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.LiteralExpr;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.flwr.ForClause;
import xbird.xquery.expr.flwr.LetClause;
import xbird.xquery.expr.opt.ShippedVariable;
import xbird.xquery.expr.opt.ThreadedVariable;
import xbird.xquery.expr.path.axis.AxisStep;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.expr.var.VarRef;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.expr.var.BindingVariable.ExecHostVariable;
import xbird.xquery.expr.var.BindingVariable.ForVariable;
import xbird.xquery.expr.var.BindingVariable.LetVariable;
import xbird.xquery.func.ext.RemoteEval;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.parser.visitor.AbstractXQueryParserVisitor;

/**
 * BDQExpr ::= "execute at" [ VarRef "in" ] Expr "{" Expr "}"
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class BDQExpr extends AbstractXQExpression implements Externalizable {
    private static final long serialVersionUID = 2302133457373935360L;
    private static final Log LOG = LogFactory.getLog(BDQExpr.class);

    private XQExpression _endpoint;
    private BindingVariable _hostVar; // may be null
    private XQExpression _queryExpr;

    private boolean _parallel = false;

    public BDQExpr(XQExpression endpoint, BindingVariable hostVar, XQExpression expr)
            throws XQueryException {
        super();
        if(endpoint == null) {
            throw new IllegalArgumentException();
        }
        if(expr == null) {
            throw new IllegalArgumentException();
        }
        this._endpoint = endpoint;
        this._hostVar = hostVar;
        this._queryExpr = expr;
        this._type = expr.getType();
    }

    public BDQExpr() {} // for Externalizable

    public boolean isParallel() {
        return _parallel;
    }

    public void setParallel(boolean parallel) {
        this._parallel = parallel;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._endpoint = (XQExpression) in.readObject();
        this._hostVar = (ExecHostVariable) in.readObject();
        final XQExpression queryExpr = (XQExpression) in.readObject();
        this._queryExpr = queryExpr;
        this._type = queryExpr.getType();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_endpoint);
        out.writeObject(_hostVar);
        out.writeObject(_queryExpr);
    }

    public XQExpression getRemoteEndpoint() {
        return _endpoint;
    }

    public XQExpression getBodyExpression() {
        return _queryExpr;
    }

    public void setBodyExpression(XQExpression body) {
        this._queryExpr = body;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;

            XQExpression analyzedEndpoint = _endpoint.staticAnalysis(statEnv);
            this._endpoint = analyzedEndpoint;

            // prepare to ship local variables
            final List<ShippedVariable> shippedVars = new ArrayList<ShippedVariable>(4);
            ParametricVariableDetector detector = new ParametricVariableDetector(shippedVars, statEnv);
            detector.visit(_queryExpr, null);

            final XQExpression compiledExpr;
            if(analyzedEndpoint instanceof LiteralExpr) {// distributed compile                 
                LiteralExpr epLiteral = (LiteralExpr) analyzedEndpoint;
                String endpoint = epLiteral.getValue().stringValue();

                final XQEngineClient client = new XQEngineClient(endpoint);
                final CompileRequest request = new CompileRequest(_queryExpr);

                if(LOG.isInfoEnabled()) {
                    LOG.info("Invoking remote compilation at [" + endpoint + "]:\n " + _queryExpr);
                }

                // invokes remote compilation
                final Object result;
                try {
                    result = client.execute(request);
                } catch (RemoteException e) {
                    throw new XQueryException(e.getMessage(), e.getCause());
                } finally {
                    try {
                        client.close();
                    } catch (RemoteException e) {
                        LOG.warn("shutdown failed for `" + endpoint + '\'', e);
                    }
                }

                compiledExpr = (XQExpression) result;
                // reset local expression
                if(!shippedVars.isEmpty()) {
                    ShippedVariableModifier modifier = new ShippedVariableModifier(shippedVars);
                    modifier.visit(compiledExpr, null);
                }

                this._parallel = false;

            } else { // compile locally (some access paths such as index access is disabled) 
                boolean prevState = statEnv.isIndicesAccessible();
                statEnv.setIndicesAccessible(false); //TODO remote compilation for static endpoint
                compiledExpr = _queryExpr.staticAnalysis(statEnv);
                statEnv.setIndicesAccessible(prevState);
            }

            this._queryExpr = compiledExpr;
            this._type = compiledExpr.getType();
            final ThreadedVariable threadedVar = new ThreadedVariable(this);
            statEnv.addThreadedVariable(threadedVar);
            return threadedVar;
        }
        return this;
    }

    private static final class ParametricVariableDetector extends AbstractXQueryParserVisitor {
        private final List<ShippedVariable> _variablesHolder;
        private final StaticContext _statEnv;
        private int _counter = 0;

        ParametricVariableDetector(List<ShippedVariable> variablesHolder, StaticContext statEnv) {
            super();
            this._variablesHolder = variablesHolder;
            this._statEnv = statEnv;
        }

        @Override
        public ForClause visit(ForClause clause, XQueryContext ctxt) throws XQueryException {
            ForVariable var = clause.getVariable();
            var.setInsideRemoteExpr(true);
            XQExpression e = clause.getExpression();
            e.visit(this, ctxt);
            return clause;
        }

        @Override
        public LetClause visit(LetClause clause, XQueryContext ctxt) throws XQueryException {
            LetVariable lv = clause.getVariable();
            lv.setInsideRemoteExpr(true);
            XQExpression e = clause.getExpression();
            e.visit(this, ctxt);
            return clause;
        }

        @Override
        public XQExpression visit(VarRef ref, XQueryContext ctxt) throws XQueryException {
            Variable var = ref.getValue();
            if(!var.isInsideRemoteExpr()) {
                if(var.isAnalyzed()) {
                    var.staticAnalysis(_statEnv);
                    ShippedVariable shippedVar = new ShippedVariable(var, _counter++);
                    _variablesHolder.add(shippedVar);
                    ref.setValue(shippedVar);
                }
            }
            return ref;
        }
    }

    private static final class ShippedVariableModifier extends AbstractXQueryParserVisitor {
        private final List<ShippedVariable> _variablesHolder;

        ShippedVariableModifier(List<ShippedVariable> variablesHolder) {
            super();
            this._variablesHolder = variablesHolder;
        }

        @Override
        public XQExpression visit(VarRef ref, XQueryContext ctxt) throws XQueryException {
            final Variable var = ref.getValue();
            if(var instanceof ShippedVariable) {
                ShippedVariable shippedVar = (ShippedVariable) var;
                int shippedId = shippedVar.getIdentifier();
                ShippedVariable original = _variablesHolder.get(shippedId);
                XQExpression originalExpr = original.getValue();
                shippedVar.setValue(originalExpr);
                shippedVar.setLocal(true);
            }
            return ref;
        }
    }

    private static final class OutsideNonDownwardAxisDetector extends AbstractXQueryParserVisitor {

        private boolean foundBDQExpr = false;
        private boolean foundNonDownwardAxis = false;

        public OutsideNonDownwardAxisDetector() {
            super();
        }

        @Override
        public XQExpression visit(BDQExpr expr, XQueryContext ctxt) throws XQueryException {
            foundBDQExpr = true;
            return expr;
        }

        @Override
        public XQExpression visit(AxisStep step, XQueryContext ctxt) throws XQueryException {
            if(foundBDQExpr && step.isNonDownwardAxis()) {
                this.foundNonDownwardAxis = true;
            }
            return step;
        }

        public boolean foundNonDownwardAxis() {
            return foundNonDownwardAxis;
        }

    }

    private static final class InsideNonDownwardAxisDetector extends AbstractXQueryParserVisitor {

        private boolean foundShippedVariable = false;
        private boolean foundNonDownwardAxis = false;

        public InsideNonDownwardAxisDetector() {
            super();
        }

        @Override
        public XQExpression visit(BDQExpr expr, XQueryContext ctxt) throws XQueryException {
            expr.getBodyExpression().visit(this, ctxt);
            return expr;
        }

        @Override
        public XQExpression visit(Variable variable, XQueryContext ctxt) throws XQueryException {
            if(variable instanceof ShippedVariable) {
                this.foundShippedVariable = true;
            }
            return super.visit(variable, ctxt);
        }

        @Override
        public XQExpression visit(AxisStep step, XQueryContext ctxt) throws XQueryException {
            if(foundShippedVariable && step.isNonDownwardAxis()) {
                this.foundNonDownwardAxis = true;
            }
            return step;
        }

        public boolean foundNonDownwardAxis() {
            return foundNonDownwardAxis;
        }

    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        XQExpression outsideQueryExpr = dynEnv.getQueryExpression();
        OutsideNonDownwardAxisDetector outsideNdaDetector = new OutsideNonDownwardAxisDetector();
        outsideNdaDetector.visit(outsideQueryExpr, null);
        final PreparedQueryRequest request = new PreparedQueryRequest(_queryExpr, outsideNdaDetector.foundNonDownwardAxis() ? ReturnType.REMOTE_PADED_SEQUENCE
                : RemoteEval.RETURN_TYPE);

        InsideNonDownwardAxisDetector insideNdaDetector = new InsideNonDownwardAxisDetector();
        insideNdaDetector.visit(_queryExpr, null);
        PreparedQueryRequest svRequest = insideNdaDetector.foundNonDownwardAxis() ? new PreparedQueryRequest(_queryExpr, ReturnType.REMOTE_PADED_SEQUENCE)
                : request;

        final ArrayList<ShippedVariable> shippedVars = new ArrayList<ShippedVariable>(4);
        ShippedVariableCollector collector = new ShippedVariableCollector(shippedVars);
        collector.visit(_queryExpr, null);
        final int numVars = shippedVars.size();
        for(int i = 0; i < numVars; i++) {
            ShippedVariable sv = shippedVars.get(i);
            prepareVariablesToShip(svRequest, sv, contextSeq, dynEnv);
        }

        final List<String> endpoints = getEndpoints(_endpoint, contextSeq, dynEnv);
        final int numEndpoints = endpoints.size();
        if(numEndpoints == 0) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        final Sequence result;
        if(numEndpoints == 1) {
            result = invokeRequest(endpoints.get(0), request);
        } else if(_parallel) {
            result = invokeRequestsInParallel(endpoints, _hostVar, request, dynEnv);
        } else {
            Sequence lastSeq = invokeRequest(endpoints.get(0), request);
            for(int i = 1; i < numEndpoints; i++) {
                Sequence secSeq = invokeRequest(endpoints.get(i), request);
                lastSeq = new ChainedSequence(lastSeq, secSeq, dynEnv);
            }
            result = lastSeq;
        }
        return result;
    }

    private static Sequence invokeRequestsInParallel(final List<String> endpoints, final BindingVariable hostVar, final PreparedQueryRequest request, final DynamicContext dynEnv) {
        final int numEndpoints = endpoints.size();
        final ExecutorService ex = ExecutorFactory.newFixedThreadPool(numEndpoints, "parallelRemoteExec");
        final Sequence[] results = new Sequence[numEndpoints];
        //final AtomicReferenceArray<Sequence> resultsRef = new AtomicReferenceArray<Sequence>(results);
        try {
            for(int i = 0; i < numEndpoints; i++) {
                final String endpoint = endpoints.get(i);
                final int at = i;
                Runnable r = new Runnable() {
                    public void run() {
                        final Sequence res;
                        try {
                            res = invokeRequest(endpoint, request);
                        } catch (XQueryException e) {
                            throw new IllegalStateException("An error caused while evaluating CompiledQueryRequest#"
                                    + request.getIdentifier() + " at " + endpoint, e);
                        }
                        results[at] = res;
                    }
                };
                ex.execute(r);
            }
        } finally {
            ExecutorUtils.shutdownAndAwaitTermination(ex);
        }
        assert (ex.isTerminated());
        return new PipelinedSequence(dynEnv, results);
    }

    private static Sequence invokeRequest(final String endpoint, final PreparedQueryRequest request)
            throws XQueryException {
        if(LOG.isDebugEnabled()) {
            LOG.debug("Invoking remote execution at [" + endpoint + "]:\n "
                    + request.getCompiledExpression());
        }
        final XQEngineClient client = new XQEngineClient(endpoint);
        final Object result;
        try {
            result = client.execute(request);
        } catch (RemoteException e) {
            throw new XQueryException(e.getMessage(), e.getCause());
        } finally {
            try {
                client.close();
            } catch (RemoteException e) {
                LOG.warn("shutdown failed for `" + endpoint + '\'', e);
            }
        }
        Sequence resultSeq = (Sequence) result;
        return resultSeq;
    }

    private static final class ShippedVariableCollector extends AbstractXQueryParserVisitor {
        private final List<ShippedVariable> _variablesHolder;

        public ShippedVariableCollector(final List<ShippedVariable> holder) {
            super();
            this._variablesHolder = holder;
        }

        @Override
        public XQExpression visit(VarRef ref, XQueryContext ctxt) throws XQueryException {
            final Variable var = ref.getValue();
            if(var instanceof ShippedVariable) {
                _variablesHolder.add((ShippedVariable) var);
            }
            return ref;
        }
    }

    private static List<String> getEndpoints(final XQExpression endpointExpr, final Sequence<? extends Item> contextSeq, final DynamicContext dynEnv)
            throws XQueryException {
        final Sequence ep = endpointExpr.eval(contextSeq, dynEnv);
        final IFocus<? extends Item> epFocus = ep.iterator();
        if(!epFocus.hasNext()) {
            epFocus.closeQuietly();
            throw new DynamicError("Invalid XQueryD expression. Endpoint does not found");
        }
        final List<String> endpoints = new ArrayList<String>(4);
        do {
            Item firstItem = epFocus.next();
            String endpointStr = firstItem.stringValue();
            endpoints.add(endpointStr);
        } while(epFocus.hasNext());
        epFocus.closeQuietly();
        return endpoints;
    }

    private static void prepareVariablesToShip(final QueryRequest request, final ShippedVariable shippedVar, final Sequence<? extends Item> contextSeq, final DynamicContext dynEnv)
            throws XQueryException {
        final Sequence result = shippedVar.eval(contextSeq, dynEnv);
        final ReturnType rettype = request.getReturnType();
        if(RemoteEval.ENV_NOWRAP_VARSHIP || !rettype.isRemoteSequnece()) {
            shippedVar.setResult(new MarshalledSequence(result, dynEnv));
        } else if(result instanceof SingleItem) {
            shippedVar.setResult(result);
        } else {
            final RemoteSequenceProxy proxy = new RemoteSequenceProxy(result, request);
            try {
                UnicastRemoteObject.exportObject(proxy, 0, TimeoutSocketProdiver.createClientSocketFactory(), TimeoutSocketProdiver.createServerSocketFactory());
            } catch (RemoteException e) {
                throw new XQueryException("failed exporting variable: " + shippedVar.getName(), e);
            }
            final RemoteSequence remote = new RemoteSequence(proxy, result.getType());
            shippedVar.setResult(remote);
        }
    }
}
