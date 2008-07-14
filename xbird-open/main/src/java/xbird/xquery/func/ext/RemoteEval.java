/*
 * @(#)$Id: RemoteEval.java 3619 2008-03-26 07:23:03Z yui $
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

import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.config.Settings;
import xbird.engine.Request;
import xbird.engine.XQEngineClient;
import xbird.engine.Request.ReturnType;
import xbird.engine.remote.RemoteSequence;
import xbird.engine.remote.RemoteSequenceProxy;
import xbird.engine.request.QueryRequest;
import xbird.util.net.TimeoutSocketProdiver;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.MarshalledSequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.Evaluable;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.opt.ShippedVariable;
import xbird.xquery.expr.seq.SequenceExpression;
import xbird.xquery.expr.var.VarRef;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.Function;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.StringType;

/**
 * ext:remote-eval($remoteEndpoint as xs:string, $queryStr as xs:string, $varsToShip?) as item()*.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RemoteEval extends BuiltInFunction implements Evaluable {
    private static final long serialVersionUID = 1839282676743361557L;
    private static final Log LOG = LogFactory.getLog(RemoteEval.class);

    public static final boolean ENV_NOWRAP_VARSHIP = System.getProperty("xbird.remote.nowrap_var") != null;
    public static final String SYMBOL = EXT_NSPREFIX + ':' + "remote-eval";
    public static final ReturnType RETURN_TYPE;
    static {
        String type = Settings.get("xbird.remote.returntype", "AUTO");
        RETURN_TYPE = Request.resolveReturnType(type);
    }

    private XQExpression thirdParam = null;

    public RemoteEval() {
        super(SYMBOL, SequenceType.ANY_ITEMS);
        this._evalPocily = EvaluationPolicy.threaded;
    }

    @Override
    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        s[0] = new FunctionSignature(getName(), new Type[] { StringType.STRING, StringType.STRING });
        s[1] = new FunctionSignature(getName(), new Type[] { StringType.STRING, StringType.STRING,
                SequenceType.ANY_ITEMS });
        return s;
    }

    @Override
    public boolean isReusable() {
        return false;
    }

    @Override
    public Function staticAnalysis(StaticContext context, List<? extends XQExpression> params)
            throws XQueryException {
        final int psize = params.size();
        if(psize == 3) {
            this.thirdParam = params.get(2);
        }
        return this;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        String endpoint = argv.getItem(0).stringValue();
        String query = argv.getItem(1).stringValue();

        if(LOG.isInfoEnabled()) {
            LOG.info("Invoking remote query at [" + endpoint + "]:\n " + query);
        }

        XQEngineClient client = new XQEngineClient(endpoint);
        QueryRequest request = new QueryRequest(query, RETURN_TYPE);
        StaticContext statEnv = dynEnv.getStaticContext();
        URI baseUri = statEnv.getBaseURI();
        if(baseUri == null) {
            baseUri = statEnv.getSystemBaseURI();
        }
        request.setBaseUri(baseUri);
        prepareVariablesToShip(request, argv, dynEnv);
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

    private void prepareVariablesToShip(QueryRequest request, ValueSequence contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        if(thirdParam != null) {
            final List<ShippedVariable> vars = new ArrayList<ShippedVariable>(4);
            if(thirdParam instanceof VarRef) {
                final ShippedVariable shiped = wrapVariableToShip(request, (VarRef) thirdParam, contextSeq, dynEnv);
                vars.add(shiped);
            } else if(thirdParam instanceof SequenceExpression) {
                final SequenceExpression seq = (SequenceExpression) thirdParam;
                final List<XQExpression> exprs = seq.getExpressions();
                for(XQExpression e : exprs) {
                    if(e instanceof VarRef) {
                        final ShippedVariable shiped = wrapVariableToShip(request, (VarRef) e, contextSeq, dynEnv);
                        vars.add(shiped);
                    }
                }
            }
            final int varsSize = vars.size();
            if(varsSize > 0) {
                final ShippedVariable[] varAry = vars.toArray(new ShippedVariable[varsSize]);
                request.setVariablesToShip(varAry);
            }
        }
    }

    private static ShippedVariable wrapVariableToShip(QueryRequest request, VarRef ref, ValueSequence contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        QualifiedName varname = ref.getName();
        Variable var = ref.getValue();
        Sequence result = var.getResult();
        if(result == null) {
            result = var.eval(contextSeq, dynEnv);
        }
        ReturnType rettype = request.getReturnType();
        final ShippedVariable shiped;
        if(ENV_NOWRAP_VARSHIP || !rettype.isRemoteSequnece()) {
            shiped = new ShippedVariable(varname, new MarshalledSequence(result, dynEnv));
        } else {
            final RemoteSequenceProxy proxy = new RemoteSequenceProxy(result, request);
            try {
                UnicastRemoteObject.exportObject(proxy, 0, TimeoutSocketProdiver.createClientSocketFactory(), TimeoutSocketProdiver.createServerSocketFactory());
            } catch (RemoteException e) {
                throw new XQueryException("failed exporting variable: " + varname, e);
            }
            RemoteSequence remote = new RemoteSequence(proxy, result.getType());
            shiped = new ShippedVariable(varname, remote);
        }
        return shiped;
    }

}
