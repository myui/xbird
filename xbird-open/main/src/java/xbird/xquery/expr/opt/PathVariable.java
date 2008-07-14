/*
 * @(#)$Id: PathVariable.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.opt;

import java.io.*;
import java.util.*;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.dyna.ContextItemExpr;
import xbird.xquery.expr.opt.Join.PromoteJoinExpression;
import xbird.xquery.expr.path.FilterExpr;
import xbird.xquery.expr.path.PathExpr.CompositePath;
import xbird.xquery.expr.path.axis.AxisStep;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.meta.*;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.parser.visitor.AbstractXQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class PathVariable extends Variable implements Externalizable {
    private static final long serialVersionUID = 682368925811449249L;
    private static final boolean ENV_PATH_ELIM = System.getProperty("xbird.disable_pathelim") == null; //TODO REVIEWME

    private boolean _isImmutable = true;
    private boolean _materialized = false;
    private boolean _isIndexAccessable = false;

    private PathVariable(XQExpression bindingExpr) {
        super(null, bindingExpr);
    }

    public PathVariable() {}// for Externalizable

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this._isImmutable = in.readBoolean();
        this._materialized = in.readBoolean();
        this._isIndexAccessable = in.readBoolean();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeBoolean(_isImmutable);
        out.writeBoolean(_materialized);
        out.writeBoolean(_isIndexAccessable);
    }

    @Override
    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        if(_result == null || !_isImmutable) {
            this._result = _value.eval(contextSeq, dynEnv);
        } else if(ENV_PATH_ELIM) {
            if(!_materialized) {
                if(!(_result instanceof Item)) {
                    Collection<Item> c = _result.materialize();
                    ValueSequence vs = new ValueSequence(new ArrayList<Item>(c), dynEnv);
                    this._result = vs;
                }
                this._materialized = true;
            }
        }
        return _result;
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        _value.evalAsEvents(handler, contextSeq, dynEnv);
    }

    public static PathVariable create(XQExpression bindingExpr, StaticContext statEnv, boolean indexAccessable) {
        if(bindingExpr == null) {
            throw new IllegalArgumentException();
        }
        Map<String, PathVariable> pathMap = statEnv.getDeclaredPathVariables();
        String exprStr = bindingExpr.toString();
        PathVariable var = pathMap.get(exprStr);
        if(var == null) {
            var = new PathVariable(bindingExpr);
            if(isImmutable(bindingExpr)) {
                var.setImmutable(true);
                if(indexAccessable) {
                    pathMap.put(exprStr, var);
                }
            } else {
                var.setImmutable(false);
            }
            var._isIndexAccessable = indexAccessable;
            return var;
        }
        assert (var.isImmutable());
        return var;
    }

    public void setImmutable(boolean immutable) {
        this._isImmutable = immutable;
    }

    private static boolean isImmutable(XQExpression expr) {
        if(expr instanceof AxisStep) {//relative path
            return false;
        }
        final ImmutableDetector visitor = new ImmutableDetector();
        try {
            expr.visit(visitor, DynamicContext.DUMMY);
        } catch (XQueryException e) {
            throw new IllegalStateException(e);
        }
        return visitor.isImmutable();
    }

    @Override
    public void setValue(XQExpression value) {
        if(value == null) {
            throw new IllegalArgumentException();
        }
        if(_isImmutable) {
            this._isImmutable = isImmutable(value);
        }
        super.setValue(value);
    }

    @Override
    public String toString() {
        return _value.toString();
    }

    private static final class ImmutableDetector extends AbstractXQueryParserVisitor {

        private boolean _immutable = true;
        private boolean _inPredicate = false;

        public ImmutableDetector() {
            super();
        }

        public boolean isImmutable() {
            return _immutable;
        }

        @Override
        public XQExpression visit(BindingVariable variable, XQueryContext ctxt)
                throws XQueryException {
            boolean immutable = variable.isImmutable();
            if(!immutable) {
                this._immutable = false;
            }
            return variable;
        }

        @Override
        public XQExpression visit(Variable variable, XQueryContext ctxt) throws XQueryException {
            assert (variable != null);
            final boolean immutable = variable.isImmutable();
            if(!immutable) {
                _immutable = false;
            }
            super.visit(variable, ctxt);
            return variable;
        }

        @Override
        public XQExpression visit(ContextItemExpr expr, XQueryContext ctxt) throws XQueryException {
            if(!_inPredicate) {
                _immutable = false;
            }
            return expr;
        }

        @Override
        public XQExpression visit(PromoteJoinExpression expr, XQueryContext ctxt)
                throws XQueryException {
            this._immutable = false;
            super.visit(expr, ctxt);
            return expr;
        }

        @Override
        public XQExpression visit(CompositePath fragment, XQueryContext ctxt)
                throws XQueryException {
            XQExpression src = fragment.getSourceExpr();
            assert (src != null);
            if(src instanceof AxisStep) {
                this._immutable = false;
            }
            super.visit(fragment, ctxt);
            return fragment;
        }

        @Override
        public XQExpression visit(FilterExpr expr, XQueryContext ctxt) throws XQueryException {
            this._inPredicate = true;
            super.visit(expr, ctxt);
            this._inPredicate = false;
            return expr;
        }

    }

    @Override
    public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info) {
        return _isIndexAccessable ? _value.isPathIndexAccessable(statEnv, info) : false;
    }

}
