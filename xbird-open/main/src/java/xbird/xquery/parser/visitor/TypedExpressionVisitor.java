/*
 * @(#)$Id: TypedExpressionVisitor.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.parser.visitor;

import xbird.xquery.XQueryException;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.decorative.ExtensionExpr;
import xbird.xquery.expr.dyna.ValidateOp;
import xbird.xquery.expr.flwr.LetClause;
import xbird.xquery.func.UserFunction;
import xbird.xquery.meta.XQueryContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class TypedExpressionVisitor extends AbstractXQueryParserVisitor {

    public XQExpression visit(ExtensionExpr expr, XQueryContext ctxt) throws XQueryException {
        throw new IllegalStateException("ExtensionExpr should be pruned.");
    }

    public LetClause visit(LetClause clause, XQueryContext ctxt) throws XQueryException {
        throw new IllegalStateException("LetClause should be pruned.");
    }

    public XQExpression visit(UserFunction function, XQueryContext ctxt) throws XQueryException {
        throw new IllegalStateException("UserFunction should be pruned.");
    }

    public XQExpression visit(ValidateOp op, XQueryContext ctxt) throws XQueryException {
        throw new IllegalStateException("ValidateOp should be pruned.");
    }

}
