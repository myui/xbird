/*
 * @(#)$Id: NameExprConstructor.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.constructor;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.AtomizedSequence;
import xbird.xquery.dm.value.xsi.QNameValue;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class NameExprConstructor extends AbstractXQExpression implements NodeConstructor {
    private static final long serialVersionUID = 1L;

    protected XQExpression _nameExpr = null;

    public NameExprConstructor() {
        super();
    }

    public XQExpression getNameExpr() {
        return this._nameExpr;
    }

    public void setNameExpr(XQExpression e) {
        this._nameExpr = e;
    }

    protected QualifiedName processNameExpr(Sequence contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence instNameSeq = _nameExpr.eval(contextSeq, dynEnv);
        final Sequence<AtomicValue> qnameSeq = AtomizedSequence.wrap(instNameSeq, dynEnv);
        final IFocus<AtomicValue> qnameItor = qnameSeq.iterator();
        if(!qnameItor.hasNext()) {
            qnameItor.closeQuietly();
            reportError("err:XPTY0004", "Conversion of the atomized name expression to an expanded QName was not successful.");
        }
        final AtomicValue it = qnameItor.next();
        if(qnameItor.hasNext()) {
            qnameItor.closeQuietly();
            reportError("err:XPTY0004", "Conversion of the atomized name expression to an expanded QName was not successful.");
        }
        qnameItor.closeQuietly();
        final QualifiedName namev;
        final Type t = it.getType();
        if(TypeUtil.subtypeOf(t, QNameType.QNAME)) {
            final QNameValue qname = ((AtomicValue) it).castAs(QNameType.QNAME, dynEnv);
            namev = qname.getValue();
        } else if(TypeUtil.subtypeOf(t, StringType.STRING) || t == UntypedAtomicType.UNTYPED_ATOMIC) {
            final StaticContext sc = dynEnv.getStaticContext();
            try {
                final CharSequence namecs = it.stringValue();
                final String namestr = (namecs == null) ? null : namecs.toString();
                namev = QNameUtil.parse(namestr, sc.getStaticalyKnownNamespaces(), sc.getDefaultElementNamespace());
            } catch (Exception e) {
                throw new DynamicError("err:XQDY0074", e);
            }
        } else {
            reportError("err:XPTY0004", "Convertion to name expression is failed.");
            throw new IllegalStateException();
        }
        return namev;
    }

}
