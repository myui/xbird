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
package xbird.util.math;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class MathUtils {

    private MathUtils() {}

    public static float mean(final int[] scores) {
        long total = 0L;
        for(int s : scores) {
            total += s;
        }
        return total / scores.length;
    }

    public static float variance(final int[] scores) {
        final float mean = mean(scores);
        float variance = 0f;
        for(int s : scores) {
            float deviation = s - mean;
            variance += (deviation * deviation);
        }
        return variance / (scores.length - 1);
    }

    public static float stddev(final int[] scores) {
        final float v = variance(scores);
        return (float) Math.sqrt(v);
    }

    public static double mean(final long[] scores) {
        long total = 0L;
        for(long s : scores) {
            total += s;
        }
        return total / scores.length;
    }

    public static double variance(final long[] scores) {
        final double mean = mean(scores);
        double variance = 0f;
        for(long s : scores) {
            double deviation = s - mean;
            variance += (deviation * deviation);
        }
        return variance / (scores.length - 1);
    }

    public static double stddev(final long[] scores) {
        final double v = variance(scores);
        return Math.sqrt(v);
    }

    public static int max(final int[] values) {
        int max = -1;
        for(int v : values) {
            max = Math.max(v, max);
        }
        return max;
    }

    public static long max(final long[] values) {
        long max = -1L;
        for(long v : values) {
            max = Math.max(v, max);
        }
        return max;
    }

    public static int min(final int[] values) {
        int max = -1;
        for(int v : values) {
            max = Math.min(v, max);
        }
        return max;
    }

    public static long min(final long[] values) {
        long min = -1L;
        for(long v : values) {
            min = Math.min(v, min);
        }
        return min;
    }

    public static int sum(final int[] values) {
        int sum = 0;
        for(int v : values) {
            sum += v;
        }
        return sum;
    }

    public static long sum(final long[] values) {
        long sum = 0L;
        for(long v : values) {
            sum += v;
        }
        return sum;
    }

}
