/*
 * @(#)$Id: NumberUtils.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NumberUtils {

    private NumberUtils() {}

    public static long add(final long a, final long b) {
        final long sum = a + b;
        if(a < 0 && b < 0) {
            if(sum > 0) {
                throw new ArithmeticException("long overflows: " + a + " + " + b + " = " + sum);
            }
        } else if(a > 0 && b > 0) {
            if(sum < 0) {
                throw new ArithmeticException("long overflows: " + a + " + " + b + " = " + sum);
            }
        }
        return sum;
    }

    public static long multiply(final long a, final long b) {
        final long product = a * b;
        final long b2 = product / a;
        if(b2 != b) {
            throw new ArithmeticException("long overflows: " + a + " * " + b + " = " + product);
        }
        return product;
    }
}
