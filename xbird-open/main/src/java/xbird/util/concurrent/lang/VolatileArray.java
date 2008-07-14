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

import xbird.util.lang.UnsafeUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @see http://www.cs.umd.edu/~pugh/java/memoryModel/archive/index.html#332
 */
public final class VolatileArray<T> implements Serializable {
    private static final long serialVersionUID = -7091287888227632010L;

    private static final sun.misc.Unsafe unsafe = UnsafeUtils.getUnsafe();
    private static final int base = unsafe.arrayBaseOffset(Object[].class);
    private static final int scale = unsafe.arrayIndexScale(Object[].class);

    private final T[] array;
    private volatile int macguffin;

    public VolatileArray(int cap) {
        this.array = (T[]) new Object[cap];
        this.macguffin = 0;
    }

    public T get(final int i) {
        return array[i + macguffin];
    }

    public void set(final int i, final T value) {
        array[i] = value;
        macguffin = 0;
    }

    public boolean compareAndSet(final int idx, final T expect, final T update) {
        return unsafe.compareAndSwapObject(array, rawIndex(idx), expect, update);
    }

    private long rawIndex(final int i) {
        assert (i >= 0 && i < array.length) : "index: " + i;
        return base + i * scale;
    }
}
