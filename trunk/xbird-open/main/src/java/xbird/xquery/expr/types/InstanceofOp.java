/*
 * @(#)$Id: InstanceofOp.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.types;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.expr.*;
import xbird.xquery.meta.*;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.BooleanType;
import xbird.xquery.type.xs.Untyped;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#id-instance-of
 */
public final class InstanceofOp extends AbstractXQExpression implements BooleanOp {
    private static final long serialVersionUID = -6339474577754975201L;

    private XQExpression expr;
    private final Type testType;

    public InstanceofOp(XQExpression expr, Type type) {
        super();
        if(expr == null) {
            throw new IllegalArgumentException();
        }
        this.expr = expr;
        this.testType = type;
        this._type = BooleanType.BOOLEAN;
    }

    public XQExpression getExpression() {
        return this.expr;
    }

    @Override
    public Type getType() {
        return this.testType;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            this.expr = expr.staticAnalysis(statEnv);
            final Type t = expr.getType();
            if(t == Untyped.UNTYPED) {
                return this;
            }
            if(TypeUtil.subtypeOf(t, testType)) {
                return new LiteralExpr(BooleanValue.TRUE);
            }
        }
        return this;
    }

    public BooleanValue eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence seq = expr.eval(contextSeq, dynEnv);
        return TypeUtil.instanceOf(seq, testType) ? BooleanValue.TRUE : BooleanValue.FALSE;
    }

}
