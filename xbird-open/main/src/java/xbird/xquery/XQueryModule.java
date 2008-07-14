/*
 * @(#)$Id: XQueryModule.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.func.UserFunction;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class XQueryModule extends AbstractModule {
    private static final long serialVersionUID = -1443644248351404146L;

    private String version = XQueryConstants.XQUERY_VERSION;
    private String encoding = null;
    private XQExpression exprBody = null;

    public XQueryModule() {
        super();
    }

    public void visit(XQueryParserVisitor visitor, XQueryContext ctxt) throws XQueryException {
        visitor.visit(this, ctxt);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public XQExpression getExpression() {
        return exprBody;
    }

    public void setExpression(XQExpression expression) {
        this.exprBody = expression;
    }

    public void importModule(Module module) throws XQueryException {
        importVariables(module);
        importFunctions(module);
    }

    public void importVariables(Module module) throws XQueryException {
        if(module == null) { // sanity check
            throw new IllegalArgumentException("Given module is null");
        }
        Collection<Variable> globals = module.getGlobalVariables();
        final String expectedNamespace = module.getNamespace();
        if(expectedNamespace == null) {
            throw new XQueryException("err:XQST0046", "Library module MUST have a target namespace");
        }
        for(Variable v : globals) {
            QualifiedName varName = v.getVarName();
            XQExpression value = v.getValue();
            declareGlobalVariable(varName, value);
        }
    }

    public void importFunctions(Module module) throws XQueryException {
        if(module == null) { // sanity check
            throw new IllegalArgumentException("Given module is null");
        }
        final Collection<UserFunction> functions = module.getLocalFunctions();
        for(UserFunction udf : functions) {
            declareLocalFunction(udf);
        }
    }

    public void staticAnalysis(StaticContext context) throws XQueryException {
        if(exprBody != null) {
            this.exprBody = exprBody.staticAnalysis(context);
        }
    }

}