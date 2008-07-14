/*
 * @(#)$Id: FnQName.java 3619 2008-03-26 07:23:03Z yui $
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

import javax.xml.XMLConstants;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.QNameValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.QNameType;
import xbird.xquery.type.xs.StringType;

/**
 * fn:QName($paramURI as xs:string?, $paramQName as xs:string) as xs:QName.
 * <DIV lang="en">
 * Returns an xs:QName with the namespace URI given in $paramURI.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-QName
 */
public final class FnQName extends BuiltInFunction {
    private static final long serialVersionUID = -5039857539055098831L;

    public static final String SYMBOL = "fn:QName";

    public FnQName() {
        super(SYMBOL, QNameType.QNAME);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?"),
                StringType.STRING });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        assert (argv.size() == 2);
        Item firstItem = argv.getItem(0);
        Item secondItem = argv.getItem(1);
        final String qname = ((XString) secondItem).getValue();
        final String paramUri;
        if(firstItem.isEmpty()) {
            if(qname.indexOf(':') != -1) {
                throw new DynamicError("err:FOCA0002", "Invalid qname: " + qname);
            }
            paramUri = XMLConstants.NULL_NS_URI;
        } else {
            paramUri = ((XString) firstItem).getValue();
        }
        final QualifiedName ret;
        try {
            ret = QNameUtil.parse(qname, paramUri);
        } catch (Throwable e) {
            throw new DynamicError("err:FOCA0002", "Incorrect lexical form for xs:QName: " + qname);
        }
        return new QNameValue(ret);
    }

}
