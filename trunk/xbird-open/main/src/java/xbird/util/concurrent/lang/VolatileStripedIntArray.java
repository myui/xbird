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

import java.io.Serializable;
import java.util.Arrays;

import xbird.util.lang.HashUtils;
import xbird.util.lang.UnsafeUtils;
import xbird.util.system.SystemUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @see http://www.cs.umd.edu/~pugh/java/memoryModel/archive/index.html#332
 */
public final class VolatileStripedIntArray implements Serializable {
    private static final long serialVersionUID = -1666971843031968502L;

    private static final sun.misc.Unsafe unsafe = UnsafeUtils.getUnsafe();
    private static final int base = unsafe.arrayBaseOffset(int[].class);
    private static final int scale = unsafe.arrayIndexScale(int[].class);

    private final int[] array;
    private final int numDims;
    private final int mask;
    /** capacity of each dimension */
    private final int capacity;

    /** used for barrier */
    private volatile int macguffin;

    public VolatileStripedIntArray(int cap) {
        this(cap, SystemUtils.is64BitVM() ? 8 : 4);
    }

    public VolatileStripedIntArray(int cap, int dim) {
        assert (HashUtils.isPowerOfTwo(cap)) : cap;
        assert (HashUtils.isPowerOfTwo(dim)) : dim;
        if(cap < dim) {
            throw new IllegalArgumentException("cap must be greater than or equals to " + dim
                    + ", but was .. " + cap);
        }
        this.array = new int[cap];
        this.numDims = dim;
        this.mask = dim - 1;
        this.capacity = cap / dim;
        this.macguffin = 0;
    }

    public int get(final int i) {
        int idx = toPhysicalIndex(i);
        return array[idx + macguffin];
    }

    public void set(final int i, final int value) {
        int idx = toPhysicalIndex(i);
        array[idx] = value;
        macguffin = 0;
    }

    public boolean compareAndSet(final int idx, final int expect, final int update) {
        int phyidx = toPhysicalIndex(idx);
        return unsafe.compareAndSwapObject(array, rawIndex(phyidx), expect, update);
    }

    private int toPhysicalIndex(final int logical) {
        int remainder = logical & mask;
        int quotient = logical / numDims;
        return remainder * capacity + quotient;
    }

    private long rawIndex(final int i) {
        assert (i >= 0 && i < array.length) : "index: " + i;
        return base + i * scale;
    }

    public static void main(String[] args) {
        final int estsize = 32;
        VolatileStripedIntArray ary = new VolatileStripedIntArray(estsize, 8);
        for(int i = 0; i < estsize; i++) {
            ary.set(i, i);
        }
        for(int i = 0; i < estsize; i++) {
            if(ary.get(i) != i) {
                System.err.println("error found at " + i);
                System.exit(1);
            }
        }
        System.err.println("succeed!");
        System.out.println(Arrays.toString(ary.array));
    }
}