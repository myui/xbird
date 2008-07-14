/*
 * @(#)$Id: ImplicitTimezone.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.TimeZone;

import javax.xml.datatype.Duration;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.DurationValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.XsDatatypeFactory;
import xbird.xquery.type.xs.DayTimeDurationType;

/**
 * fn:implicit-timezone() as xdt:dayTimeDuration.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-implicit-timezone
 */
public final class ImplicitTimezone extends BuiltInFunction {
    private static final long serialVersionUID = -6492280546811932536L;
    public static final String SYMBOL = "fn:implicit-timezone";

    public ImplicitTimezone() {
        super(SYMBOL, DayTimeDurationType.DAYTIME_DURATION);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName());
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        TimeZone tz = dynEnv.implicitTimezone();
        int utcOffset = tz.getOffset(0);
        final Duration d = XsDatatypeFactory.createDuration(utcOffset);
        return new DurationValue(d, DayTimeDurationType.DAYTIME_DURATION);
    }

}
