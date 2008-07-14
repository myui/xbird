/*
 * @(#)$Id: Module.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.func.Function;
import xbird.xquery.func.UserFunction;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.FunctionManager;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public interface Module extends Serializable {

    public void staticAnalysis(StaticContext context) throws XQueryException;

    public void visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException;

    public void setParentModule(Module module);

    public Module getParentModule();

    public String getLocation();

    public String getNamespace();

    public XQExpression getExpression();

    public Variable declareGlobalVariable(QualifiedName varName, XQExpression value)
            throws XQueryException;

    public void putVariable(QualifiedName varName, Variable variable)
            throws XQueryException;

    public Collection<Variable> getGlobalVariables();

    public Variable getVariable(QualifiedName varName);

    public void declareLocalFunction(UserFunction func) throws XQueryException;

    public Collection<UserFunction> getLocalFunctions();

    public Function lookupFunction(QualifiedName funcName, List<? extends XQExpression> params);

    public FunctionManager getFunctionManager();
    
    public void pushVarScope();

    public void popVarScope();

}