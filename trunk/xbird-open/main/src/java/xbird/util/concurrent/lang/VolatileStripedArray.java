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

import xbird.util.hashes.HashUtils;
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
public final class VolatileStripedArray<T> implements Serializable {
    private static final long serialVersionUID = -6939017419610276945L;

    private static final sun.misc.Unsafe unsafe = UnsafeUtils.getUnsafe();
    private static final int base = unsafe.arrayBaseOffset(Object[].class);
    private static final int scale = unsafe.arrayIndexScale(Object[].class);

    private final T[] array;
    private final int numDims;
    private final int mask;
    /** capacity of each dimension */
    private final int capacity;

    /** used for barrier */
    private volatile int macguffin;

    public VolatileStripedArray(int cap) {
        this(cap, SystemUtils.is64BitVM() ? 8 : 4);
    }

    public VolatileStripedArray(int cap, int dim) {
        assert (HashUtils.isPowerOfTwo(cap)) : cap;
        assert (HashUtils.isPowerOfTwo(dim)) : dim;
        if(cap < dim) {
            throw new IllegalArgumentException("cap must be greater than or equals to " + dim
                    + ", but was .. " + cap);
        }
        this.array = (T[]) new Object[cap];
        this.numDims = dim;
        this.mask = dim - 1;
        this.capacity = cap / dim;
        this.macguffin = 0;
    }

    public T get(final int i) {
        int idx = toPhysicalIndex(i);
        return array[idx + macguffin];
    }

    public void set(final int i, final T value) {
        int idx = toPhysicalIndex(i);
        array[idx] = value;
        macguffin = 0;
    }

    public boolean compareAndSet(final int idx, final T expect, final T update) {
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
}
