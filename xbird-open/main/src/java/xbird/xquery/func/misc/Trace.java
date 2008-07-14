/*
 * @(#)$Id: Trace.java 3619 2008-03-26 07:23:03Z yui $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.StringType;

/**
 * fn:trace($value as item()*, $label as xs:string) as item()*.
 * <DIV lang="en">
 * The input $value is returned, unchanged, as the result of the function.
 * In addition, the inputs $value, converted to an xs:string, and $label may be directed to 
 * a trace data set.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-trace
 */
public final class Trace extends BuiltInFunction {
    private static final long serialVersionUID = 1402147053811134219L;
    public static final String SYMBOL = "fn:trace";
    public static final Log LOGGER = LogFactory.getLog("TRACE");

    public Trace() {
        super(SYMBOL, SequenceType.ANY_ITEMS);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS,
                StringType.STRING });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv) throws XQueryException {
        assert (argv != null && argv.size() == 2);
        Item value = argv.getItem(0);
        Item secondItem = argv.getItem(1);
        XString label = (XString) secondItem;
        assert (label != null);
        final String message = label.getValue() + ": { " + traceItem(value, ", ") + " }.";
        LOGGER.trace(message);  // TODO create trace listener?
        return value;
    }

    private static String traceItem(Item items, String delimitter) {
        assert (items != null);
        final StringBuilder buf = new StringBuilder(256);
        boolean first = true;
        for (Item it : items) {
            if (first) {
                first = false;
            } else {
                buf.append(delimitter);
            }
            buf.append(it.stringValue());
        }
        return buf.toString();
    }

}
