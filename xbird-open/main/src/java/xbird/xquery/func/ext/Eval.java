/*
 * @(#)$Id: Eval.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.Reader;
import java.io.StringReader;

import xbird.xquery.Module;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.parser.XQueryParser;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.StringType;

/**
 * ext:eval($query as xs:string?) as item()*.
 * <DIV lang="en">
 * evaluates specified xquery query dynamically.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class Eval extends BuiltInFunction {
    private static final long serialVersionUID = 7742369457258869206L;
    
    public static final String SYMBOL = EXT_NSPREFIX + ':' + "eval";

    public Eval() {
        super(SYMBOL, SequenceType.ANY_ITEMS);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { StringType.STRING });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null && argv.size() == 1) : "argument must be one, but was "
                + argv.size();
        Item firstItem = argv.getItem(0);
        if(firstItem.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        final String query = firstItem.stringValue();
        final Sequence res = evaluateQuery(query, dynEnv);
        return res;
    }

    public static Sequence evaluateQuery(String query, DynamicContext dynEnv)
            throws XQueryException {
        return evaluateQuery(new StringReader(query), dynEnv);
    }

    public static Sequence evaluateQuery(Reader reader, DynamicContext dynEnv)
            throws XQueryException {
        assert (dynEnv != null);
        // parse query
        XQueryParser parser = new XQueryParser(reader);
        StaticContext sc = dynEnv.getStaticContext();
        parser.setStaticContext(sc);
        Module mod = parser.parse();
        // static analysis
        mod.staticAnalysis(sc);
        // evaluate
        XQExpression body = mod.getExpression();
        Sequence res = body.eval(ValueSequence.EMPTY_SEQUENCE, dynEnv);
        return res;
    }

}
