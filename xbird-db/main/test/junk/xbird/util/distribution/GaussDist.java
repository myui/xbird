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
 * This class defines the Gaussian (Normal) Distribution.
 * 
 * @author LUPINGD
 */
public final class GaussDist extends Distribution {
    private double mean;
    private double stdev;

    public GaussDist() {
        this(0.0, 1.0);
    }

    public GaussDist(int seed1, int seed2) {
        this(seed1, seed2, 0.0, 1.0);
    }

    public GaussDist(double mean, double stdev) {
        super();
        this.mean = mean;
        this.stdev = stdev;
    }

    public GaussDist(int seed1, int seed2, double mean, double stdev) {
        super(seed1, seed2);
        this.mean = mean;
        this.stdev = stdev;
    }

    public Double nextRandomValue() {
        return nextRandomDouble();
    }

    @Override
    public final double nextRandomDouble() {
        return randomEle.gaussian(stdev) + mean;
    }

    public double getMean() {
        return mean;
    }

    public double getStdev() {
        return stdev;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public void setStdev(double stdev) {
        this.stdev = stdev;
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
