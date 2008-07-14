/*
 * @(#)$Id: ResolveUri.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.misc;

import java.net.URI;
import java.net.URISyntaxException;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.AnyURIValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;
import xbird.xquery.type.xs.StringType;

/**
 * fn:resolve-uri.
 * <DIV lang="en">
 * The purpose of this function is to enable a relative URI to be resolved
 * against an absolute URI.
 * <ul>
 * <li>fn:resolve-uri($relative as xs:string?) as xs:anyURI?</li>
 * <li>fn:resolve-uri($relative as xs:string?, $base as xs:string) as xs:anyURI?</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-resolve-uri
 */
public final class ResolveUri extends BuiltInFunction {
    private static final long serialVersionUID = 8322638707996728737L;
    public static final String SYMBOL = "fn:resolve-uri";

    public ResolveUri() {
        super(SYMBOL, TypeRegistry.safeGet("xs:anyURI?"));
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        Type str = TypeRegistry.safeGet("xs:string?");
        s[0] = new FunctionSignature(getName(), new Type[] { str });
        s[1] = new FunctionSignature(getName(), new Type[] { str, StringType.STRING });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        // If $relative is the empty sequence, the empty sequence is returned.
        Item firstItem = argv.getItem(0);
        if(firstItem.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        final URI relative;
        String relativeStr = firstItem.stringValue();
        try {
            if(relativeStr.length() == 0) {
                relative = null;
            } else {
                relative = new URI(relativeStr);
                if(relative.isAbsolute()) {// If $relative is an absolute URI reference, it is returned unchanged.
                    return AnyURIValue.valueOf(relative);
                }
            }
        } catch (URISyntaxException e) {
            throw new DynamicError("err:FORG0002", "Invalid URI form: " + relativeStr);
        }
        final URI baseUri;
        final int arglen = argv.size();
        if(arglen == 2) {
            Item secondItem = argv.getItem(1);
            String baseStr = secondItem.stringValue();
            baseUri = URI.create(baseStr);
        } else {
            baseUri = dynEnv.getStaticContext().getBaseURI();
            if(baseUri == null) {
                throw new DynamicError("err:FONS0005", "BaseUri is not set in the static context.");
            }
        }
        if(relative == null) {// If $relative is the zero-length string, returns the value of the base-uri property.
            return AnyURIValue.valueOf(baseUri);
        }
        final URI resolved = baseUri.resolve(relative);
        return AnyURIValue.valueOf(resolved);
    }

    public static URI resolveURI(String relativeUri, StaticContext staticEnv)
            throws XQueryException {
        assert (relativeUri != null && staticEnv != null);
        URI baseUri = staticEnv.getBaseURI();
        if(baseUri == null) {
            throw new DynamicError("err:FONS0005", "BaseUri is not set in the static context.");
        }
        return resolveURI(relativeUri, baseUri);
    }

    public static URI resolveURI(String relativeUri, URI baseUri) throws XQueryException {
        assert (relativeUri != null && baseUri != null);
        if(relativeUri.length() == 0) {// If $relative is the zero-length string, returns the value of the base-uri property.
            return baseUri;
        }
        final URI resolved = baseUri.resolve(relativeUri);
        return resolved;
    }
}
