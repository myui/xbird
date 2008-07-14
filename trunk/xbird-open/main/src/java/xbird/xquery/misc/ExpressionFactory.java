/*
 * @(#)$Id:ExpressionFactory.java 2335 2007-07-17 04:14:15Z yui $
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
package xbird.xquery.misc;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.util.lang.ObjectUtils;
import xbird.xquery.*;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.func.*;
import xbird.xquery.expr.opt.TypePromotedExpr;
import xbird.xquery.expr.path.NodeTest;
import xbird.xquery.expr.path.axis.*;
import xbird.xquery.expr.seq.SequenceExpression;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.expr.var.BindingVariable.ParametricVariable;
import xbird.xquery.expr.var.Variable.ExternalVariable;
import xbird.xquery.func.Function;
import xbird.xquery.func.UserFunction;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.SyntaxError;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.Untyped;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ExpressionFactory {

    private static final Log LOG = LogFactory.getLog("xbird.expr");

    private ExpressionFactory() {}

    public static Variable createExternalVariable(QualifiedName varName, XQueryModule currentModule) {
        Variable defined = currentModule.getVariable(varName);
        assert (!(defined instanceof ExternalVariable));
        if(defined != null) {
            return defined;
        }
        return new ExternalVariable(varName, currentModule);
    }

    public static XQExpression createExpression(List<XQExpression> expr) {
        if(expr == null) {
            throw new IllegalArgumentException();
        }
        final int size = expr.size();
        if(size == 0) {
            return null;
        } else if(size == 1) {
            return expr.get(0);
        } else {
            return createSequenceExpression(expr);
        }
    }

    public static XQExpression createSequenceExpression(List<XQExpression> expr) {
        return new SequenceExpression(expr);
    }

    public static AxisStep createAxisStep(int kind, NodeTest nodeTest) {
        final AxisStep step;
        switch(kind) {
            case AxisStep.CHILD:
                step = new ChildStep(nodeTest);
                break;
            case AxisStep.DESC:
                step = new DescendantStep(nodeTest);
                break;
            case AxisStep.ATTR:
                step = new AttributeStep(nodeTest);
                break;
            case AxisStep.SELF:
                step = new SelfStep(nodeTest);
                break;
            case AxisStep.DESC_OR_SELF:
                step = new DescendantOrSelfStep(nodeTest);
                break;
            case AxisStep.FOLLOWING_SIBLING:
                step = new FollowingSiblingStep(nodeTest);
                break;
            case AxisStep.FOLLOWING:
                step = new FollowingStep(nodeTest);
                break;
            case AxisStep.PARENT:
                step = new ParentStep(nodeTest);
                break;
            case AxisStep.ANCESTOR:
                step = new AncestorStep(nodeTest);
                break;
            case AxisStep.PRECEDING_SIBLING:
                step = new PrecedingSibling(nodeTest);
                break;
            case AxisStep.PRECEDING:
                step = new Preceding(nodeTest);
                break;
            case AxisStep.ANCESTOR_OR_SELF:
                step = new AncestorOrSelfStep(nodeTest);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return step;
    }

    public static UserFunction createUserFunction(Module declaredModule, QualifiedName funcName, List<ParametricVariable> parameters, Type returnType)
            throws XQueryException {
        if(declaredModule.lookupFunction(funcName, parameters) != null) {
            throw new SyntaxError("err:XPST0017");
        }
        final UserFunction func = new UserFunction(declaredModule, funcName, parameters, returnType);
        declaredModule.declareLocalFunction(func);
        return func;
    }

    public static XQExpression createFunctionCall(Module module, QualifiedName funcName, List<XQExpression> argv, StaticContext statEnv)
            throws XQueryException {
        final FunctionManager funcMgr = module.getFunctionManager();
        final Function func = module.lookupFunction(funcName, argv);
        if(func == null) {
            return new LazyFunctionCall(funcMgr, funcName, argv);
        }
        // [note] ~ attempt function inlining ~
        //  function call in function decl is not inlined
        //  unless the calling function is already declared.
        // FIXME: type promotion of params
        if(func instanceof UserFunction) {
            return inlineFunction(funcMgr, (UserFunction) func, argv, statEnv);
        } else {
            return new DirectFunctionCall(func, argv);
        }
    }

    public static XQExpression inlineFunction(final FunctionManager funcMgr, final UserFunction func, final List<XQExpression> argv, final StaticContext statEnv)
            throws XQueryException {
        final XQExpression body = func.getBodyExpression();
        if(body == null) {
            return new RecursiveCall(func, argv);
        }
        final List<ParametricVariable> params = func.getParameters();
        if(params.isEmpty()) {
            return body;
        } else {
            // Note that create function always generates new body expression for each call,
            // and must care about parameters reference.            
            UserFunction newuf = ObjectUtils.deepCopy(func);
            List<ParametricVariable> newParams = newuf.getParameters();
            final int psize = newParams.size();
            for(int i = 0; i < psize; i++) {
                ParametricVariable p = newParams.get(i);
                Type implicitParamType = p.getType();
                XQExpression arg = argv.get(i);
                Type argType = arg.getType();
                if(implicitParamType != Untyped.UNTYPED && !implicitParamType.accepts(argType)) {
                    // type promotion is required for arguments
                    p.setValue(new TypePromotedExpr(arg, implicitParamType));
                } else {
                    p.setValue(arg);
                }
            }
            XQExpression newbody = newuf.getBodyExpression();
            statEnv.setFunctionManager(funcMgr);
            if(LOG.isDebugEnabled()) {
                LOG.debug("Inlined the function '" + QNameUtil.toLexicalForm(newuf.getName()) + "'");
            }
            return newbody;
        }
    }
}
