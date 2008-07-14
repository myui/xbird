/*
 * @(#)$Id: Abs.java 3619 2008-03-26 07:23:03Z yui $
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
 * fn:abs($arg as numeric?) as numeric?.
 * <DIV lang="en">
 * Returns the absolute value of $arg. If $arg is negative returns -$arg otherwise returns $arg.
 * If type of $arg is one of the four numeric types xs:float, xs:double, xs:decimal or xs:integer 
 * the type of the result is the same as the type of $arg.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-abs
 * @link http://www.w3.org/TR/xquery-semantics/#sec_fn_abs_ceil_floor_round
 */
public final class Abs extends NumericFunction {
    private static final long serialVersionUID = 7360186556404994838L;
    public static final String SYMBOL = "fn:abs";

    public Abs() {
        super(SYMBOL, TypeRegistry.safeGet("xs:double?"));
    }

    protected XNumber promote(XNumber value) {
        assert (value != null);
        final double v = value.getNumber().doubleValue();
        if (v < 0 || v == Double.NEGATIVE_INFINITY) {// TODO negative zero
            return value.negate();
        } else {
            return value;
        }
    }

}
