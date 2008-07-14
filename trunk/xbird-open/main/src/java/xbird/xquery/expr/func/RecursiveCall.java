/*
 * @(#)$Id: RecursiveCall.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.BindingVariable.ParametricVariable;
import xbird.xquery.func.UserFunction;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class RecursiveCall extends DirectFunctionCall {
    private static final long serialVersionUID = -9206675238870249718L;
    private static final Log LOG = LogFactory.getLog(LazyFunctionCall.class);

    public RecursiveCall(UserFunction func, List<XQExpression> params) {
        super(func, params);
        this._type = func.getReturnType();
        assert (_type != null);
    }
    
    public RecursiveCall() {// for Externalizable
        super();
    }

    @Override
    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    @Override
    public UserFunction getFunction() {
        return (UserFunction) func;
    }

    @Override
    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        UserFunction uf = this.getFunction();
        List<ParametricVariable> params = uf.getParameters();
        int paramArity = _params.size();
        int argArity = params.size();
        assert (paramArity == argArity);
        FunctionCallContext recEnv = new FunctionCallContext(dynEnv);
        for(int i = 0; i < paramArity; i++) {
            ParametricVariable paramVar = params.get(i);
            XQExpression argExpr = _params.get(i);
            Sequence argValue = argExpr.eval(contextSeq, dynEnv);
            paramVar.allocateResult(argValue, recEnv);
        }
        Sequence<? extends Item> result = uf.eval(contextSeq, ValueSequence.EMPTY_SEQUENCE, recEnv);
        if(LOG.isDebugEnabled()) {
            LOG.debug("Invoke RecursiveCall: " + QNameUtil.toLexicalForm(getFuncName()));
        }
        return result;
    }

}
