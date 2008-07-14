/*
 * @(#)$Id: CurrentDateTime.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.context;

import javax.xml.datatype.XMLGregorianCalendar;

import xbird.util.struct.Pair;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.DateTimeValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.XsDatatypeFactory;
import xbird.xquery.type.xs.*;

/**
 * fn:current-dateTime() as xs:dateTime.
 * <DIV lang="en">
 * Note that this function is stable.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-current-dateTime
 */
public class CurrentDateTime extends BuiltInFunction {
    private static final long serialVersionUID = 427932038591136931L;

    public static final String SYMBOL = "fn:current-dateTime";

    private final Pair<DynamicContext, DateTimeValue> previous = new Pair<DynamicContext, DateTimeValue>(null, null);

    public CurrentDateTime() {
        super(SYMBOL, DateTimeType.DATETIME);
        this._evalPocily = EvaluationPolicy.eager;
    }

    protected CurrentDateTime(String symbol, DateTimeBaseType retType) {
        super(symbol, retType);
    }

    @Override
    public DateTimeBaseType getReturnType() {
        return (DateTimeBaseType) super.getReturnType();
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName());
        return s;
    }

    public DateTimeValue eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        if(previous.first == dynEnv) {
            return previous.second;
        } else {
            final XMLGregorianCalendar cal = XsDatatypeFactory.createXMLGregorianCalendar(dynEnv.currentDateTime());
            DateTimeBaseType casttoType = getReturnType();
            final DateTimeValue date = new DateTimeValue(cal, casttoType);
            previous.first = dynEnv;
            previous.second = date;
            return date;
        }
    }

    public static final class CurrentDate extends CurrentDateTime {
        private static final long serialVersionUID = 8445680492892478804L;
        
        public static final String SYMBOL = "fn:current-date";

        public CurrentDate() {
            super(SYMBOL, DateType.DATE);
        }
    }

    public static final class CurrentTime extends CurrentDateTime {
        private static final long serialVersionUID = -3134397023834562929L;
        
        public static final String SYMBOL = "fn:current-time";

        public CurrentTime() {
            super(SYMBOL, TimeType.TIME);
        }
    }

}
