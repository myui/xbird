/*
 * @(#)$Id: NamespaceConstructor.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.instance.XQueryDataModel;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.node.DMNamespace;
import xbird.xquery.expr.Evaluable;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.node.NamespaceTest;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NamespaceConstructor extends AttributeConstructorBase implements Evaluable {
    private static final long serialVersionUID = -2383782736555510488L;

    private final String _prefix;
    private QualifiedName _name = null;

    public NamespaceConstructor(String prefix, String value) {
        this._prefix = prefix;
        this._value = value;
        this._type = NamespaceTest.ANY_NAMESPACE;
    }

    public String getPrefix() {
        return this._prefix;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        return this;
    }

    public QualifiedName processName(DynamicContext dynEnv) throws XQueryException {
        if(this._name == null) {
            final StaticContext sc = dynEnv.getStaticContext();
            final QualifiedName name = QNameUtil.resolveNSAttrName(_prefix, sc.getStaticalyKnownNamespaces(), sc.getDefaultElementNamespace());
            this._name = name;
        }
        return _name;
    }

    public DMNamespace eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        return XQueryDataModel.createNamespace(processName(dynEnv), _value);
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        handler.evNamespace(_prefix, _value);
    }

}
