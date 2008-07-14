/*
 * @(#)$Id: LazyFunctionCall.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.func;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.opt.TypePromotedExpr;
import xbird.xquery.expr.var.BindingVariable.ParametricVariable;
import xbird.xquery.func.Function;
import xbird.xquery.func.UserFunction;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.ExpressionFactory;
import xbird.xquery.misc.FunctionManager;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405 AT gmail.com)
 */
public class LazyFunctionCall extends FunctionCall {
    private static final long serialVersionUID = -6552453197650861353L;
    private static final Log LOG = LogFactory.getLog(LazyFunctionCall.class);

    private transient FunctionManager _functionManager;
    private boolean _inlined = false;

    public LazyFunctionCall(FunctionManager functionManager, QualifiedName funcName, List<XQExpression> params) {
        super(funcName, params);
        this._functionManager = functionManager;
    }

    public LazyFunctionCall() {// for Externalizable
        super();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this._inlined = in.readBoolean();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeBoolean(_inlined);
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    @Override
    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            UserFunction udf = getUserFunction(statEnv);
            if(!_inlined) {
                // analyze body
                XQExpression body = ExpressionFactory.inlineFunction(_functionManager, udf, _params, statEnv);
                XQExpression analyzed = body.staticAnalysis(statEnv);
                XQExpression typePromoted = new TypePromotedExpr(analyzed, udf.getReturnType());
                return typePromoted;
            } else {
                // analyze params
                for(int i = 0; i < _params.size(); i++) {
                    final XQExpression p = _params.get(i);
                    _params.set(i, p.staticAnalysis(statEnv));
                }
                this._type = udf.getReturnType();
            }
        }
        return this;
    }

    private UserFunction getUserFunction(final StaticContext statEnv) throws XQueryException {
        if(_functionManager == null) {
            this._functionManager = statEnv.getFunctionManager();
        }
        final Function func = _functionManager.lookupFunction(_funcName, _params);
        if(func == null) {
            throw new XQueryException("err:XPST0017", "function not found: " + _funcName);
        }
        if(!(func instanceof UserFunction)) {
            throw new IllegalStateException("function must be UserFunction, but was '"
                    + func.getName() + '\'');
        }
        UserFunction udf = ((UserFunction) func);
        return udf;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        assert (dynEnv != null);
        UserFunction udf = getUserFunction(dynEnv.getStaticContext());
        List<ParametricVariable> params = udf.getParameters();
        int paramArity = _params.size();
        int argArity = params.size();
        assert (paramArity == argArity);
        FunctionCallContext recEnv = new FunctionCallContext(dynEnv);
        for(int i = 0; i < paramArity; i++) {
            ParametricVariable paramVar = params.get(i);
            XQExpression argExpr = _params.get(i);
            paramVar.setValue(argExpr); // for the static analzing
            Sequence argValue = argExpr.eval(contextSeq, dynEnv);
            paramVar.allocateResult(argValue, recEnv);
        }
        XQExpression body = udf.getBodyExpression();
        // lazy function may not be analyzed TODO REVIEWME
        XQExpression analyzed = body.staticAnalysis(dynEnv.getStaticContext());
        if(LOG.isDebugEnabled()) {
            LOG.debug("Invoke LazyFunctionCall: " + QNameUtil.toLexicalForm(getFuncName()));
        }
        return analyzed.eval(contextSeq, recEnv);
    }

    public void setFunctionManager(FunctionManager functionManager) {
        assert (functionManager != null);
        this._functionManager = functionManager;
    }

    protected Object writeReplace() throws ObjectStreamException {
        this._inlined = true;
        this._analyzed = false;
        return this;
    }

}
