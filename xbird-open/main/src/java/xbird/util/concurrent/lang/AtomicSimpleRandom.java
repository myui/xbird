/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
 *
 * Copyright (c) 2005-2006 Makoto YUI and Project XBird
 * All rights reserved.
 * 
 * This file is part of XBird and is distributed under the terms of
 * the Common Public License v1.0.
 * 
 * Contributors:
 *     Makoto YUI - initial implementation
 */
package xbird.util.concurrent.lang;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class AtomicSimpleRandom {

    private final static long multiplier = 0x5DEECE66DL;
    private final static long addend = 0xBL;
    private final static long mask = (1L << 48) - 1;

    private static final AtomicLong seq = new AtomicLong(-715159705);
    private long seed;

    public AtomicSimpleRandom(long s) {
        this.seed = s;
    }

    public AtomicSimpleRandom() {
        this.seed = System.nanoTime() + seq.getAndAdd(129);
    }

    public void setSeed(long s) {
        seed = s;
    }

    public int nextInt() {
        return next();
    }

    public int next() {
        long nextseed = (seed * multiplier + addend) & mask;
        this.seed = nextseed;
        return ((int) (nextseed >>> 17)) & 0x7FFFFFFF;
    }

    public int next(int bits) {
        long nextseed = (seed * multiplier + addend) & mask;
        this.seed = nextseed;
        return (int) (nextseed >>> (48 - bits));
    }

}
