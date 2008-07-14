/*
 * @(#)$Id: VariableManager.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.*;

import xbird.xquery.XQueryException;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.expr.var.Variable.ExternalVariable;
import xbird.xquery.expr.var.Variable.GlobalVariable;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class VariableManager {

    private final Stack<Context> contexts = new Stack<Context>();

    public VariableManager() {
        contexts.push(new Context());
    }

    public Variable declareGlobalVariable(QualifiedName varName, XQExpression value)
            throws XQueryException {
        if(varName == null) { // sanity check.
            throw new IllegalArgumentException("varName is null");
        }
        return getCurrentContext().declareGlobalVariable(varName, value);
    }

    public void putVariable(QualifiedName varName, Variable variable) throws XQueryException {
        if(varName == null) {
            throw new IllegalArgumentException("varName is null");
        }
        if(variable == null) {
            throw new IllegalArgumentException("variable is null.");
        }
        Context cc = getCurrentContext();
        assert (cc != null);
        cc.putVariable(varName, variable);
    }

    public Collection<Variable> getGlobalVariables() {
        Context globals = contexts.get(0);
        return globals.getVariables();
    }

    public Variable getVariable(QualifiedName varName) {
        return getCurrentContext().getVariable(varName);
    }

    /**
     * Revert to the previous context.
     */
    public void popContext() {
        if(contexts.size() < 2) {
            // at least one context always should be in the stack.
            throw new IllegalStateException("Illegal pop operation is detected.");
        }
        contexts.pop();
    }

    /**
     * Start a new context.
     */
    public void pushContext() {
        Context currentContext = getCurrentContext();
        Context newContext = new Context(currentContext);
        contexts.push(newContext);
    }

    private Context getCurrentContext() {
        return contexts.peek();
    }

    private static final class Context {
        private final Map<QualifiedName, Variable> vars;

        private final Set<QualifiedName> localVars = new HashSet<QualifiedName>();

        Context() {
            vars = new HashMap<QualifiedName, Variable>();
        }

        Context(Context parent) {
            vars = new HashMap<QualifiedName, Variable>(parent.vars);
        }

        Variable declareGlobalVariable(QualifiedName varName, XQExpression value)
                throws XQueryException {
            if(vars.containsKey(varName)) {
                throw new XQueryException("err:XQST0049", "Variable already defined: " + varName);
            }
            Variable v = new GlobalVariable(varName, value);
            vars.put(varName, v);
            localVars.add(varName);
            return v;
        }

        void putVariable(QualifiedName varName, Variable variable) throws XQueryException {
            if(variable instanceof GlobalVariable) {
                Variable var = vars.get(varName);
                if(var != null && !(var instanceof ExternalVariable)) {
                    throw new XQueryException("err:XQ0049", "Duplicate global variable: " + varName);
                }
            } else {
                if(localVars.contains(varName)) {
                    throw new XQueryException("err:XQ0049", "Duplicate local variable: " + varName);
                }
            }
            vars.put(varName, variable);
            localVars.add(varName);
        }

        Collection<Variable> getVariables() {
            return vars.values();
        }

        Variable getVariable(QualifiedName varName) {
            return vars.get(varName);
        }

        @Override
        public String toString() {
            return vars.toString();
        }

    }
}