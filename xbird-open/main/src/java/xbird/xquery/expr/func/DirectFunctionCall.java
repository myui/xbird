/*
 * @(#)$Id: DirectFunctionCall.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import xbird.storage.DbCollection;
import xbird.util.struct.ThreeLogic;
import xbird.xquery.TypeError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.labeling.LabelingHandler;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.SingleCollection;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.Evaluable;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.opt.ThreadedVariable;
import xbird.xquery.expr.var.Variable.PreEvaluatedVariable;
import xbird.xquery.func.*;
import xbird.xquery.func.Function.EvaluationPolicy;
import xbird.xquery.func.doc.FnCollection;
import xbird.xquery.meta.*;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.*;
import xbird.xquery.type.xs.UntypedAtomicType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class DirectFunctionCall extends FunctionCall {
    private static final long serialVersionUID = 1L;

    protected/* final */Function func;

    private transient ThreeLogic indexAccessable = ThreeLogic.NIL;
    private transient RewriteInfo prevInfo = null;

    public DirectFunctionCall(Function func, List<XQExpression> params) {
        super(func.getName(), params);
        this.func = func;
    }

    public DirectFunctionCall() {// for Externalizable
        super();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        final boolean isBuiltIn = in.readBoolean();
        if(isBuiltIn) {
            this.func = BuiltInFunction.readFrom(in);
        } else {
            this.func = (Function) in.readObject();
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        if(func instanceof BuiltInFunction) {
            out.writeBoolean(true);
            BuiltInFunction builtIn = (BuiltInFunction) func;
            builtIn.writeTo(out);
        } else {
            out.writeBoolean(false);
            out.writeObject(func);
        }
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public Function getFunction() {
        return func;
    }

    //--------------------------------------------
    // static analysis/dynamic evaluation

    @Override
    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            super.staticAnalysis(statEnv);
            func.staticAnalysis(statEnv, _params);
            this._type = func.getReturnType(_params);
            final EvaluationPolicy evalPolicy = func.getEvaluationPolicy();
            if(evalPolicy == EvaluationPolicy.eager) {
                Sequence preEvaled = eval(ValueSequence.EMPTY_SEQUENCE, new DynamicContext(statEnv));
                return new PreEvaluatedVariable(preEvaled, _type);
            } else if(evalPolicy == EvaluationPolicy.rewritten) {
                XQExpression rewritted = func.rewrite(_params, statEnv);
                XQExpression analyzed = rewritted.staticAnalysis(statEnv);
                return analyzed;
            } else if(evalPolicy == EvaluationPolicy.threaded) {
                ThreadedVariable threadedVar = new ThreadedVariable(this);
                statEnv.addThreadedVariable(threadedVar);
                return threadedVar;
            }
        }
        return this;
    }

    @Override
    public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info) {
        if(indexAccessable != ThreeLogic.NIL) {
            if(indexAccessable == ThreeLogic.TRUE) {
                if(prevInfo == null) {
                    throw new IllegalStateException("prevInfo is not set.. bug?");
                }
                info.setCollection(prevInfo.getCollection(), prevInfo.getFilter());
                return true;
            } else {
                return false;
            }
        }
        if(info.hasPreviousStep()) {
            this.indexAccessable = ThreeLogic.FALSE;
            return false;
        }
        final boolean isCol = (func instanceof FnCollection);
        if(isCol) {
            final int paramSize = _params.size();
            final String colpath;
            if(paramSize == 0) {
                colpath = "/";
            } else if(paramSize == 1) {
                XQExpression expr = _params.get(0);
                if(expr instanceof Evaluable) {
                    final Sequence<? extends Item> result;
                    try {
                        result = expr.eval(null, DynamicContext.DUMMY);
                    } catch (XQueryException e) {
                        throw new IllegalStateException(e);
                    }
                    colpath = result.toString();
                } else {
                    this.indexAccessable = ThreeLogic.FALSE;
                    return false;
                }
            } else {
                throw new IllegalStateException("Unexpected parameters: " + _params);
            }
            DbCollection col = DbCollection.getCollection(colpath);
            if(col == null) {
                this.indexAccessable = ThreeLogic.FALSE;
                return false;
            }
            File idxDir = new File(col.getDirectory(), LabelingHandler.INDEX_DIR_NAME);
            if(!idxDir.exists()) {
                this.indexAccessable = ThreeLogic.FALSE;
                return false;
            }
            info.setCollection(col, colpath);
            this.indexAccessable = ThreeLogic.TRUE;
            this.prevInfo = info;
            return true;
        } else {
            this.indexAccessable = ThreeLogic.FALSE;
            return false;
        }
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final ValueSequence argv;
        final int arity = _params.size();
        if(arity == 0) {
            argv = null;
        } else {
            argv = new ValueSequence(dynEnv);
            FunctionSignature sign = func.getFunctionSignature(arity);
            for(int i = 0; i < arity; i++) {
                XQExpression p = _params.get(i);
                Sequence seq = p.eval(contextSeq, dynEnv);
                Sequence converted = mapFunctionArgument(seq, sign.getArgumentType(i), dynEnv);
                argv.addItem(SingleCollection.wrap(converted, dynEnv));
            }
        }
        return func.eval(contextSeq, argv, dynEnv);
    }

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#jd_map_function_argument
     */
    public static Sequence mapFunctionArgument(Sequence argv, Type expectedArgType, DynamicContext dynEnv)
            throws XQueryException {
        assert (argv != null);
        assert (expectedArgType != null);
        // If the function expects atomic parameters, then fn:data is called 
        // to obtain them.
        final Type primeType = expectedArgType.prime();
        if(primeType instanceof AtomicType) {
            // Inserts conversions of function arguments that depend only on 
            // the expected SequenceType of the corresponding parameters.
            if(expectedArgType instanceof SequenceType) {
                expectedArgType = ((SequenceType) expectedArgType).prime();
            }
            if(!(expectedArgType instanceof AtomicType)) {
                throw new TypeError("expectedArgType must be built-in atomic type, but was "
                        + expectedArgType);
            }
            final AtomicType expected = (AtomicType) expectedArgType;
            final ValueSequence res = new ValueSequence(dynEnv);
            final IFocus<? extends Item> atomizedItor = argv.atomize(dynEnv).iterator();
            for(Item it : atomizedItor) {
                final Type actualType = it.getType();
                // apply fs:ConvertSimpleOperand
                if(actualType == UntypedAtomicType.UNTYPED_ATOMIC && expected.getTypeId() >= 0) {
                    final AtomicValue casted = ((AtomicValue) it).castAs(expected, dynEnv);
                    res.addItem(casted);
                } else {
                    res.addItem(it);
                }
            }
            atomizedItor.closeQuietly();
            return res.atomize(dynEnv);
        }
        return argv;
    }

    //--------------------------------------------
    // helpers

    public static DirectFunctionCall create(Function func, XQExpression... params) {
        final List<XQExpression> p = new ArrayList<XQExpression>(params.length);
        for(XQExpression param : params) {
            p.add(param);
        }
        final DirectFunctionCall f = new DirectFunctionCall(func, p);
        f.copyLocations(params[0]);
        return f;
    }

}
