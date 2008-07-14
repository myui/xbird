/*
 * @(#)$Id: VirtualView.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.ext;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import xbird.xquery.DynamicError;
import xbird.xquery.Module;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.LiteralExpr;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.seq.SequenceExpression;
import xbird.xquery.func.misc.ResolveUri;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.parser.XQueryParser;
import xbird.xquery.type.SequenceType;

/**
 * ext:view($viewloc as xs:string?) as item()*
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class VirtualView extends View {
    private static final long serialVersionUID = -5985760396501454915L;
    public static final String SYMBOL = EXT_NSPREFIX + ":view";

    public VirtualView() {
        super(SYMBOL, SequenceType.ANY_ITEMS);
        this._evalPocily = EvaluationPolicy.rewritten;
    }

    @Override
    public XQExpression rewrite(List<? extends XQExpression> params, StaticContext context)
            throws XQueryException {
        assert (_evalPocily == EvaluationPolicy.rewritten);
        if(params == null || params.isEmpty()) {
            return SequenceExpression.EMPTY_SEQUENCE;
        }
        int paramlen = params.size();
        assert (paramlen == 1);
        XQExpression locExpr = params.get(0);
        if(locExpr instanceof LiteralExpr) {
            AtomicValue atomv = ((LiteralExpr) locExpr).getValue();
            String locStr = atomv.stringValue();
            assert (locStr != null);
            Reader qr = readQuery(locStr, context);
            XQExpression resolved = resolveExpression(qr, context);
            return resolved;
        }
        return null;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        assert (argv != null && argv.size() == 1);
        Item firstItem = argv.getItem(0);
        if(firstItem.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        assert (firstItem instanceof XString);
        String viewloc = firstItem.stringValue();
        Reader reader = readQuery(viewloc, dynEnv.getStaticContext());
        Sequence res = Eval.evaluateQuery(reader, dynEnv);
        return res;
    }

    private static XQExpression resolveExpression(Reader reader, StaticContext staticEnv)
            throws XQueryException {
        XQueryParser parser = new XQueryParser(reader);
        parser.setStaticContext(staticEnv);
        Module mod = parser.parse();
        XQExpression body = mod.getExpression();
        return body;
    }

    private static Reader readQuery(String viewloc, StaticContext staticEnv) throws XQueryException {
        URI resolved = ResolveUri.resolveURI(viewloc, staticEnv);
        final URL queryUrl;
        try {
            queryUrl = resolved.toURL();
        } catch (MalformedURLException e) {
            throw new DynamicError("Invalid URL form: " + viewloc);
        }
        final InputStream is;
        try {
            is = queryUrl.openStream();
        } catch (IOException e) {
            throw new DynamicError("failed to open stream for " + queryUrl);
        }
        Reader reader = new InputStreamReader(is);
        return reader;
    }
}
