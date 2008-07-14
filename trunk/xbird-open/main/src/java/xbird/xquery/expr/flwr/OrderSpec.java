/*
 * @(#)$Id: OrderSpec.java 3619 2008-03-26 07:23:03Z yui $
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

import java.net.URI;
import java.text.Collator;

import xbird.util.resource.CollationUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.SingleCollection;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#doc-xquery-OrderSpec
 * @link http://www.w3.org/TR/xquery/#id-orderby-return
 * @link http://www.w3.org/TR/xquery-semantics/#id_orderby_clause
 */
public class OrderSpec extends AbstractXQExpression {
    private static final long serialVersionUID = -258563761038835180L;

    private XQExpression keyExpr;
    private boolean descending = false;
    private boolean isEmptyGreatest = false;
    private URI collation = null;
    private Collator collator = null;

    public OrderSpec(XQExpression expr) {
        assert (expr != null);
        this.keyExpr = expr;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    //--------------------------------------------
    // getter/setter

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public boolean isEmptyGreatest() {
        return isEmptyGreatest;
    }

    public void setEmptyGreatest(boolean emptyGreatest) {
        this.isEmptyGreatest = emptyGreatest;
    }

    public URI getCollation() {
        return collation;
    }

    public void setCollation(URI collation) {
        this.collation = collation;
    }

    public XQExpression getKeyExpr() {
        return this.keyExpr;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(128);
        buf.append("order by ");
        buf.append(keyExpr.toString());
        if(descending) {
            buf.append(" descending");
        }
        if(isEmptyGreatest) {
            buf.append(" empty greatest");
        }
        if(collation != null) {
            buf.append(" collation ");
            buf.append(collation.toString());
        }
        return buf.toString();
    }

    //--------------------------------------------
    // static analysis/dynamic evaluation

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            this.keyExpr = keyExpr.staticAnalysis(statEnv);
            if(collation != null) {
                this.collator = CollationUtils.resolve(collation, statEnv);
            }
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        throw new IllegalStateException("OrderSpec#eval() should not be called.");
    }

    public int compare(Item one, Item two, DynamicContext dynEnv) {
        final int cmp;
        if(one == null) { // null means empty.
            cmp = (two == null) ? 0 : (isEmptyGreatest ? 1 : -1);
        } else if(two == null) {
            cmp = isEmptyGreatest ? -1 : 1;
        } else if(one instanceof XString) {
            assert (two instanceof XString);
            final String s1 = ((XString) one).getValue();
            final String s2 = ((XString) two).getValue();
            assert (s1 != null && s2 != null);
            if(collator != null) {
                cmp = collator.compare(s1, s2);
            } else {
                cmp = s1.compareTo(s2);
            }
        } else if(one instanceof SingleCollection) {
            SingleCollection sc1 = (SingleCollection) one;
            cmp = sc1.compareTo(two, isEmptyGreatest);
        } else {
            cmp = one.compareTo(two);
        }
        return descending ? -cmp : cmp;
    }

    public int compare(Comparable one, Comparable two) {
        final int cmp;
        if(one == null) { // null means empty.
            cmp = (two == null) ? 0 : (isEmptyGreatest ? 1 : -1);
        } else if(two == null) {
            cmp = isEmptyGreatest ? -1 : 1;
        } else {
            cmp = one.compareTo(two);
        }
        return descending ? -cmp : cmp;
    }

}
