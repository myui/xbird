/*
 * @(#)$Id: Settings.java 3619 2008-03-26 07:23:03Z yui $
 * 
 * Copyright 2004 Hewlett-Packard Development Company, LP
 * [See end of file]
 * 
 * Contributors:
 *     HP Jena project - initial implementation
 *     Makoto YUI - applied some modifications
 */
package xbird.util.distribution;

/**
 * This class defines the Zipfian distribution.
 * <pre>
 * Usage:
 *  Distribution dist = new ZipfianDist(100, 0.5);
 *  long v = dist.nextRandomValue().longValue();
 * </pre>
 * 
 * @author LUPINGD
 */
public final class ZipfianDist extends Distribution {
    /** The maximum value. */
    private long maxValue;
    /** The skew. */
    private double skew;

    public ZipfianDist() {
        this(Long.MAX_VALUE, 0);
    }

    public ZipfianDist(int seed1, int seed2) {
        this(seed1, seed2, Long.MAX_VALUE, 0);
    }

    public ZipfianDist(long maxVal, double skew) {
        super();
        this.maxValue = maxVal;
        this.skew = skew;
    }

    public ZipfianDist(int seed1, int seed2, long maxVal, double skew) {
        super(seed1, seed2);
        this.maxValue = maxVal;
        this.skew = skew;
    }

    public long getMaxValue() {
        return maxValue;
    }

    public double getSkew() {
        return skew;
    }

    public void setMaxValue(long maxValue) {
        this.maxValue = maxValue;
    }

    public void setSkew(double skew) {
        this.skew = skew;
    }

    public Long nextRandomValue() {
        return nextRandomLong();
    }

    @Override
    public final long nextRandomLong() {
        double alpha = 1 / (1 - skew);
        double zetan = zeta(maxValue, skew);
        double eta = (1 - Math.pow(2.0 / maxValue, 1 - skew)) / (1 - zeta(2, skew) / zetan);
        double u = randomEle.raw();
        double uz = u * zetan;
        if(uz < 1) {
            return 1L;
        }
        if(uz < 1 + Math.pow(0.5, skew)) {
            return 2L;
        }
        return 1L + (long) (maxValue * Math.pow(eta * u - eta + 1, alpha));
    }

    private double zeta(long n, double theta) {
        double sum = 0.0;
        for(int i = 1; i <= n; i++) {
            sum += Math.pow(1.0 / (double) i, theta);
        }
        return sum;
    }
}

/*
 * (c) Copyright 2004 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
