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
 * This class defines the Poisson distribution.
 * 
 * @author LUPINGD
 */
public final class PoissonDist extends Distribution {
    /** The mean value. */
    private long mean;

    public PoissonDist() {
        this(0);
    }

    public PoissonDist(int seed1, int seed2) {
        this(seed1, seed2, 0);
    }

    public PoissonDist(long mean) {
        super();
        this.mean = mean;
    }

    public PoissonDist(int seed1, int seed2, long mean) {
        super(seed1, seed2);
        this.mean = mean;
    }

    public void setMean(long mean) {
        this.mean = mean;
    }

    public Long nextRandomValue() {
        return nextRandomLong();
    }

    @Override
    public final long nextRandomLong() {
        // even though a Poisson distribution with mean 10 can be well
        // approximated by a normal distribution, I will use the standard
        // algorithm for means up to 100
        if(mean < 100) {
            // See Knuth, TAOCP, vol. 2, second print
            // section 3.4.1, algorithm Q on page 117
            // Q1. [Calculate exponential]
            double p = Math.exp(-(double) mean);
            long N = 0;
            double q = 1.0;

            while(true) {
                // Q2. [Get uniform variable]
                double U = randomEle.raw();
                // Q3. [Multiply]
                q = q * U;
                // Q4. [Test]
                if(q >= p) {
                    N = N + 1;
                } else {
                    return N;
                }
            }
        } else {
            // for larger mean values we approximate the Poisson distribution
            // using a normal distribution
            double z = randomEle.gaussian();
            long value = (long) (mean + z * Math.sqrt(mean) + 0.5);
            if(value >= 0) {
                return value;
            } else {
                return 0L;
            }
        }
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
