/*
 * @(#)$Id: AbstractModule.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.Collection;
import java.util.List;

import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.func.Function;
import xbird.xquery.func.UserFunction;
import xbird.xquery.misc.FunctionManager;
import xbird.xquery.misc.VariableManager;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class AbstractModule implements Module {

    private transient final VariableManager varManager = new VariableManager();
    private transient final FunctionManager funcManager = new FunctionManager();

    private String location = null;
    private String namespace = null;
    private Module parentModule = null;

    public AbstractModule() {}

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Module getParentModule() {
        return parentModule;
    }

    public void setParentModule(Module parentModule) {
        this.parentModule = parentModule;
    }

    public Variable declareGlobalVariable(QualifiedName varName, XQExpression value)
            throws XQueryException {
        return varManager.declareGlobalVariable(varName, value);
    }

    public void putVariable(QualifiedName varName, Variable variable) throws XQueryException {
        varManager.putVariable(varName, variable);
    }

    public Collection<Variable> getGlobalVariables() {
        return varManager.getGlobalVariables();
    }

    public Variable getVariable(QualifiedName varName) {
        return varManager.getVariable(varName);
    }

    public void declareLocalFunction(UserFunction func) throws XQueryException {
        funcManager.declareLocalFunction(func);
    }

    public Collection<UserFunction> getLocalFunctions() {
        return funcManager.getLocalFunctions();
    }

    public Function lookupFunction(QualifiedName funcName, List<? extends XQExpression> params) {
        return funcManager.lookupFunction(funcName, params);
    }

    public void popVarScope() {
        varManager.popContext();
    }

    public void pushVarScope() {
        varManager.pushContext();
    }

    public FunctionManager getFunctionManager() {
        return funcManager;
    }

}
