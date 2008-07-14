/*
 * @(#)$Id: DocAvailable.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.doc;

import java.net.URI;
import java.net.URISyntaxException;

import xbird.util.xml.XMLUtils;
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.BooleanType;

/**
 * fn:doc-available($uri as xs:string?) as xs:boolean.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-doc-available
 */
public final class DocAvailable extends BuiltInFunction {
    private static final long serialVersionUID = -5172353587233816241L;

    public static final String SYMBOL = "fn:doc-available";
    public static final QualifiedName FUNC_NAME = resolve(SYMBOL);

    public DocAvailable() {
        super(FUNC_NAME, BooleanType.BOOLEAN);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:string?") });
        return s;
    }

    public BooleanValue eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        Item uriItem = argv.getItem(0);
        if(uriItem.isEmpty()) {
            return BooleanValue.FALSE;
        }
        String uriStr = uriItem.stringValue();
        // If $uri is not a valid xs:anyURI, an error is raised [err:FODC0005].
        final URI docuri;
        try {
            final String unescaped = XMLUtils.unescapeXML(uriStr);
            docuri = new URI(unescaped);
        } catch (URISyntaxException e) {
            throw new DynamicError("err:FODC0005", "Invalid uri: " + uriStr, e);
        }
        if(dynEnv.getDocumentManager().isDocumentAvailable(docuri)) {
            return BooleanValue.TRUE;
        } else {
            return BooleanValue.FALSE;
        }
    }

}
