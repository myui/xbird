/*
 * @(#)$Id: Round.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.math;

import xbird.xquery.dm.value.literal.XNumber;
import xbird.xquery.type.TypeRegistry;

/**
 * fn:round($arg as numeric?) as numeric?.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class Round extends NumericFunction {
    private static final long serialVersionUID = 7782595802023476303L;
    public static final String SYMBOL = "fn:round";

    public Round() {
        super(SYMBOL, TypeRegistry.safeGet("xs:numeric?"));
    }

    protected XNumber promote(XNumber value) {
        assert (value != null);
        final double v = value.getNumber().doubleValue();
        if (Double.isNaN(v)) {
            return value;
        }
        return value.round();
    }

}
