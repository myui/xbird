/*
 * @(#)$Id$
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
package xbird.util.distribution;

import java.util.Random;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://en.wikipedia.org/wiki/Zipf%27s_law
 */
public final class ZipfGenerator {

    private final Random random;
    private final double skew_d;

    public ZipfGenerator() {
        this(2d);
    }

    public ZipfGenerator(double skew) {
        if(skew <= 1d) {
            throw new IllegalArgumentException("skew should be greater than 1.0f");
        }
        this.random = new Random();
        this.skew_d = skew;
    }

    public double nextDouble() {
        double p = random.nextDouble();
        return Math.pow(1d - p, 1d - (1d - skew_d));
    }

    public long nextLong() {
        return Double.doubleToLongBits(nextDouble());
    }
}
