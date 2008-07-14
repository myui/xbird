/*
 * @(#)$Id: DocumentUri.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.accessor;

import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.instance.DocumentTableModel.DTMDocument;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.node.DMDocument;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.AnyURIValue;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.func.*;
import xbird.xquery.func.opt.EagerEvaluated;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.node.DocumentTest;

/**
 * fn:document-uri($arg as node()?) as xs:anyURI?.
 * <DIV lang="en">
 * Returns the value of the document-uri property for $arg as defined by 
 * the dm:document-uri accessor function.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-document-uri
 */
public final class DocumentUri extends BuiltInFunction {
    private static final long serialVersionUID = 3691361485629917174L;

    public static final String SYMBOL = "fn:document-uri";

    public DocumentUri() {
        super(SYMBOL, TypeRegistry.safeGet("xs:anyURI?"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("node()?") });
        return s;
    }

    @Override
    public Function staticAnalysis(StaticContext context, List<? extends XQExpression> params)
            throws XQueryException {
        assert (params != null);
        if(params.isEmpty()) {
            return new EagerEvaluated(this, ValueSequence.EMPTY_SEQUENCE);
        }
        return this;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        assert (argv != null);
        // If $arg is the empty sequence, the empty sequence is returned.
        if(argv.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        // Returns the empty sequence if the node is not a document node.
        // Otherwise, returns the value of the dm:document-uri accessor of the document node.
        assert (argv.size() == 1);
        Item arg = argv.getItem(0);
        Type type = arg.getType();
        if(!TypeUtil.subtypeOf(type, DocumentTest.ANY_DOCUMENT)) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        final String docuri;
        if(arg instanceof DTMDocument) {
            docuri = ((DTMDocument) arg).documentUri();
        } else if(arg instanceof DMDocument) {
            docuri = ((DMDocument) arg).documentUri();
        } else {
            throw new IllegalStateException("Invalid document node type: "
                    + arg.getClass().getName());
        }
        if(docuri == null) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        return AnyURIValue.valueOf(docuri);
    }

}
