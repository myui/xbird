/*
 * @(#)$Id: codetemplate_xbird.xml 3792 2008-04-21 21:39:23Z yui $
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
package xbird.xquery.expr.flwr;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.expr.var.VarRef;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en">
 * <code>GroupingSpec ::= "$" VarName ("collation" URILiteral)?</code>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class GroupingSpec extends OrderSpec {
    private static final long serialVersionUID = 2368547892799885492L;

    private final BindingVariable groupingVariable;

    public GroupingSpec(BindingVariable bindingVar) {
        super(bindingVar);
        bindingVar.incrementReferenceCount();
        this.groupingVariable = bindingVar;
    }

    @Override
    public BindingVariable getKeyExpr() {
        return groupingVariable;
    }

    @Override
    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    @Override
    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        throw new IllegalStateException("GroupingSpec#eval should not be called.");
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(128);
        buf.append("group by ");
        buf.append(groupingVariable.toString());
        return buf.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj instanceof OrderSpec) {
            OrderSpec other = (OrderSpec) obj;
            return equals(other);
        }
        return false;
    }

    public boolean equals(OrderSpec other) {
        XQExpression orderExpr = other.getKeyExpr();
        if(!(orderExpr instanceof VarRef)) {
            return false;
        }
        VarRef orderingVarRef = (VarRef) orderExpr;
        Variable orderingVar = orderingVarRef.getValue();
        if(orderingVar != groupingVariable) {
            return false;
        }
        if(other.getCollation() != getCollation()) {
            return false;
        }
        return false;
    }
    
    public void composite(OrderSpec other) {
        setDescending(other.isDescending());
        setEmptyGreatest(other.isEmptyGreatest());
    }

}
