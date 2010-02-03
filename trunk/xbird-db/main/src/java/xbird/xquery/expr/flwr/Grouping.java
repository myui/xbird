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

import java.util.ArrayList;
import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * Grouping Stuffs including a GroupByClause and LetClause(s) after the GroupByClause.
 * <DIV lang="en">
 * <code>GroupByClause LetClause* WhereClause?</code>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @see http://www.w3.org/TR/xquery-11/#id-flwor-expressions
 */
public final class Grouping extends AbstractXQExpression {
    private static final long serialVersionUID = -1903195421994496153L;

    private final List<GroupingSpec> groupingKeys;
    private boolean ordering = false;

    private List<Binding> letClauses = null;
    private XQExpression whereExpr = null;

    public Grouping() {
        this.groupingKeys = new ArrayList<GroupingSpec>(2);
    }

    public List<GroupingSpec> getGroupingKeys() {
        return groupingKeys;
    }

    public GroupingSpec[] getGroupingKeysAsArray() {
        return groupingKeys.toArray(new GroupingSpec[groupingKeys.size()]);
    }

    public void addGroupingKey(GroupingSpec key) {
        groupingKeys.add(key);
    }

    public List<Binding> getLetClauses() {
        return letClauses;
    }

    public void addLetClauses(List<Binding> clauses) {
        if(letClauses == null) {
            letClauses = clauses;
        } else {
            letClauses.addAll(clauses);
        }
    }

    public XQExpression getWhereExpression() {
        return whereExpr;
    }

    public void setWhereExpression(XQExpression expr) {
        this.whereExpr = expr;
    }

    public boolean isOrdering() {
        return ordering;
    }

    public void setOrdering(boolean ordering) {
        this.ordering = ordering;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        throw new UnsupportedOperationException();
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(_analyzed) {
            this._analyzed = true;
            for(GroupingSpec key : groupingKeys) {
                key.staticAnalysis(statEnv);
            }
            if(letClauses != null) {
                for(Binding lc : letClauses) {
                    lc.staticAnalysis(statEnv);
                }
            }
            if(whereExpr != null) {
                this.whereExpr = whereExpr.staticAnalysis(statEnv);
            }
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        throw new IllegalStateException("GroupingStuff#eval should not be called.");
    }

    public boolean isGroupingAndOrderingCombinable(final List<OrderSpec> orderSpecs) {
        if(orderSpecs == null) {
            return false;
        }
        final int size = orderSpecs.size();
        if(size != groupingKeys.size()) {
            return false;
        }
        for(int i = 0; i < size; i++) {
            OrderSpec orderSpec = orderSpecs.get(i);
            GroupingSpec groupSpec = groupingKeys.get(i);
            assert (orderSpec != null);
            if(groupSpec.equals(orderSpec)) {
                return false;
            }
        }
        return true;
    }

    public void composite(final List<OrderSpec> orderSpecs) {
        final int size = orderSpecs.size();
        for(int i = 0; i < size; i++) {
            OrderSpec orderSpec = orderSpecs.get(i);
            GroupingSpec groupSpec = groupingKeys.get(i);
            groupSpec.composite(orderSpec);
        }
    }

}
