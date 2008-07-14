/*
 * @(#)$Id: LiteralExpr.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.meta.*;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.xs.StringType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#id-literals
 */
public class LiteralExpr extends AbstractXQExpression implements Evaluable {
    private static final long serialVersionUID = -8296973401860633348L;

    protected AtomicValue value;

    public LiteralExpr(AtomicValue value) {
        if(value == null) {
            throw new IllegalArgumentException("LiternalExpr argument expects non-null value.");
        }
        this.value = value;
        this._type = value.getType();
    }

    public LiteralExpr(String value) {
        this(XString.valueOf(value));
        this._type = StringType.STRING;
    }

    public AtomicValue getValue() {
        return this.value;
    }

    //--------------------------------------------
    // overrides/implements

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    @Override
    public String toString() {
        return StringType.STRING.equals(value.getType()) ? "\"" + value + "\"" : value.toString();
    }

    //--------------------------------------------
    // static analysis/dynamic evaluation

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        return value;
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        handler.evAtomicValue(value);
    }

}
