/*
 * @(#)$Id: BaseURI.java 3619 2008-03-26 07:23:03Z yui $
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

import java.net.URI;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.instance.XQueryDataModel;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.SingleCollection;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.AnyURIValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;

/**
 * fn:base-uri.
 * <DIV lang="en">
 * Returns the value of the base-uri property for $arg as defined by the accessor function 
 * dm:base-uri() for that kind of node.
 * <ul>
 * <li>fn:base-uri() as xs:anyURI?</li>
 * <li>fn:base-uri($arg as node()?) as xs:anyURI?</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-base-uri
 */
public final class BaseURI extends BuiltInFunction {
    private static final long serialVersionUID = 3238299697550247261L;

    public static final String SYMBOL = "fn:base-uri";

    public BaseURI() {
        super(SYMBOL, TypeRegistry.safeGet("xs:anyURI?"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        s[0] = new FunctionSignature(getName());
        s[1] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("node()?") });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        if(argv == null) {
            // If $arg is not specified, returns the value of the base-uri property 
            // of the context item (.)
            final Item contextItem = dynEnv.contextItem();
            if(argv == null) {
                if(contextItem == null) {
                    throw new DynamicError("err:XPDY0002", "Context Item is not set");
                } else {
                    if(!(contextItem instanceof XQNode)) {
                        throw new DynamicError("err:XPTY0004", "Context item is not a node: "
                                + contextItem.getClass().getName());
                    }
                }
            } else if(argv.isEmpty()) {// If $arg is the empty sequence, the empty sequence is returned.
                return ValueSequence.EMPTY_SEQUENCE;
            }
        }
        Item it = argv.getItem(0);
        if(it instanceof SingleCollection) {
            final Sequence<? extends Item> src = ((SingleCollection) it).getSource();
            final IFocus<? extends Item> itor = src.iterator();
            if(!itor.hasNext()) {
                itor.closeQuietly();
                return ValueSequence.EMPTY_SEQUENCE;
            }
            it = itor.next();
            if(itor.hasNext()) {
                itor.closeQuietly();
                throw new DynamicError("err:XPTY0004", "Context item is not a node: " + src);
            }
            itor.closeQuietly();
        }
        if(!(it instanceof XQNode)) {
            if(it.isEmpty()) {
                return ValueSequence.EMPTY_SEQUENCE;
            } else {
                throw new DynamicError("err:XPTY0004", "Item was not a node: "
                        + it.getClass().getName());
            }
        }
        final XQNode node = (XQNode) it;
        final byte nodekind = node.nodeKind();
        // Document, element and processing-instruction nodes have a base-uri property 
        // which may be empty. The base-uri property of all other node types is the empty sequence.        
        switch(nodekind) {
            case NodeKind.DOCUMENT:
            case NodeKind.ELEMENT: {
                final String uri = node.baseUri();
                final URI baseUri = dynEnv.getStaticContext().getBaseURI();
                if(uri == null) {
                    if(node.getDataModel() == XQueryDataModel.INSTANCE) {
                        if(baseUri != null) {
                            return AnyURIValue.valueOf(baseUri);
                        }
                    }
                    break;
                } else {
                    if(baseUri == null) {
                        return AnyURIValue.valueOf(uri);
                    } else {
                        final URI resolved = baseUri.resolve(uri);
                        return AnyURIValue.valueOf(resolved);
                    }
                }
            }
            case NodeKind.PROCESSING_INSTRUCTION:
                final String uri = node.baseUri();
                if(uri != null) {
                    return AnyURIValue.valueOf(uri);
                }
            default:
                break;
        }
        return ValueSequence.EMPTY_SEQUENCE;
    }

}
