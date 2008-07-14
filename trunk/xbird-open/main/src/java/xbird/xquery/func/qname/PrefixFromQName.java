/*
 * @(#)$Id: PrefixFromQName.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.qname;

import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.QNameValue;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.func.*;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.NCNameType;

/**
 * fn:prefix-from-QName($arg as xs:QName?) as xs:NCName?
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-prefix-from-QName
 */
public final class PrefixFromQName extends BuiltInFunction {
    private static final long serialVersionUID = 4809594696986732057L;
    public static final String SYMBOL = "fn:prefix-from-QName";

    public PrefixFromQName() {
        super(SYMBOL, TypeRegistry.safeGet("xs:NCName?"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:QName?") });
        return s;
    }

    @Override
    public PrefixFromQName staticAnalysis(StaticContext context, List<? extends XQExpression> params) throws XQueryException {
        strictCheckArguments(context, params);
        return this;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        assert (argv != null && argv.size() == 1);
        Item firstItem = argv.getItem(0);
        if(firstItem.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        QNameValue arg = (QNameValue) firstItem;
        QualifiedName qname = arg.getValue();
        String prefix = qname.getPrefix();
        if(prefix.length() == 0) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        return new XString(prefix, NCNameType.NCNAME);
    }

}
