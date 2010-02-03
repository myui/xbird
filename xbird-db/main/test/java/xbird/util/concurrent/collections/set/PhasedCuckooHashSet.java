/*
 * PhasedCuckooHashSet.java
 *
 * Created on November 1, 2006, 3:22 PM
 *
 * From "The Art of Multiprocessor Programming",
 * by Maurice Herlihy and Nir Shavit.
 *
 * This work is licensed under a Creative Commons Attribution-Share Alike 3.0 United States License.
 * http://i.creativecommons.org/l/by-sa/3.0/us/88x31.png
 */
package xbird.util.concurrent.collections.set;

import java.util.ArrayList;
import java.util.List;

/**
 * Phased concurrent cuckoo hash set.
 *  * @param <T> type
 * @author Maurice Herlihy
 */
public abstract class PhasedCuckooHashSet<T> {
    /**
     * Number of entries in the table. Should be about twice the number of items.
     */
    volatile int capacity;
    volatile List<T>[][] table;
    // resize when overflow reaches this size
    static final int PROBE_SIZE = 8;
    static final int THRESHOLD = PROBE_SIZE / 2;
    // resize when chain of phases exceeds this
    static final int LIMIT = 8;

    private final static long multiplier = 0x5DEECE66DL;
    private final static long addend = 0xBL;
    private final static long mask = (1L << 31) - 1;

    /**
     * Create new set holding at least this many entries.
     * @param size number of entries to expect
     */
    public PhasedCuckooHashSet(int size) {
        capacity = size;
        table = (List<T>[][]) new java.util.ArrayList[2][capacity];
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < capacity; j++) {
                table[i][j] = new ArrayList<T>(PROBE_SIZE);
            }
        }
    }

    /**
     * First hash function
     * @param x Item to hash.
     * @return non-negative hash value
     */
    final public int hash0(T x) {
        return (x.hashCode() & 0xffffff) % capacity;
    }

    /**
     * Second hash function
     * @param x item to hash
     * @return non-negative hash value
     */
    final public synchronized int hash1(T x) {
        return (int) Math.abs((x.hashCode() * multiplier + addend) & mask);
    }

    /**
     * Is item in the set?
     * @param x search for this item
     * @return true iff item is found
     */
    public boolean contains(T x) {
        acquire(x);
        try {
            int h0 = hash0(x) % capacity;
            if(table[0][h0].contains(x)) {
                return true;
            } else {
                int h1 = hash1(x) % capacity;
                if(table[1][h1].contains(x)) {
                    return true;
                }
            }
            return false;
        } finally {
            release(x);
        }
    }

    /**
     * remove this entry from the set
     * @param x entry to remove
     * @return true iff entry found in set
     */
    public boolean remove(T x) {
        acquire(x);
        try {
            List<T> set0 = table[0][hash0(x) % capacity];
            if(set0.contains(x)) {
                set0.remove(x);
                return true;
            } else {
                List<T> set1 = table[1][hash1(x) % capacity];
                if(set1.contains(x)) {
                    set1.remove(x);
                    return true;
                }
            }
            return false;
        } finally {
            release(x);
        }
    }

    /**
     * Add an item to the set.
     * @return true iff entry was not preset
     * @param x entry to add
     */
    public boolean add(T x) {
        T y = null;
        acquire(x);
        int h0 = hash0(x) % capacity;
        int h1 = hash1(x) % capacity;
        int i = -1, h = -1;
        boolean mustResize = false;
        try {
            if(present(x)) {
                return false;
            }
            List<T> set0 = table[0][h0];
            List<T> set1 = table[1][h1];
            if(set0.size() < THRESHOLD) {
                set0.add(x);
                return true;
            } else if(set1.size() < THRESHOLD) {
                set1.add(x);
                return true;
            } else if(set0.size() < PROBE_SIZE) {
                set0.add(x);
                i = 0;
                h = h0;
            } else if(set1.size() < PROBE_SIZE) {
                set1.add(x);
                i = 1;
                h = h1;
            } else {
                mustResize = true;
            }
        } finally {
            release(x);
        }
        if(mustResize) {
            resize();
            add(x);
        } else if(!relocate(i, h)) {
            resize();
        }
        return true; // x must have been present
    }

    /**
     * Synchronize before adding, removing, or testing for item
     * @param x item involved
     */
    public abstract void acquire(T x);

    /**
     * synchronize after adding, removing, or testing for item
     * @param x item involved
     */
    public abstract void release(T x);

    /**
     * double the set size
     */
    public abstract void resize();

    /**
     * Repeatedly moves excess items to other probe sets.
     * @param i Probe set column (0 or 1)
     * @param hi Probe set row (hash0 or hash1)
     * @return <CODE>true</CODE> if all probe sets less than <CODE>THRESHOLD</CODE>,
     * <CODE>false</CODE> to trigger global resizing.
     */
    protected boolean relocate(int i, int hi) {
        int hj = 0;
        int j = 1 - i;
        for(int round = 0; round < LIMIT; round++) {
            List<T> iSet = table[i][hi];
            T y = iSet.get(0);
            switch(i) {
                case 0:
                    hj = hash1(y) % capacity;
                    break;
                case 1:
                    hj = hash0(y) % capacity;
                    break;
            }
            acquire(y);
            List<T> jSet = table[j][hj];
            try {
                if(iSet.remove(y)) {
                    if(jSet.size() < THRESHOLD) {
                        jSet.add(y);
                        return true;
                    } else if(jSet.size() < PROBE_SIZE) {
                        jSet.add(y);
                        i = 1 - i;
                        hi = hj;
                        j = 1 - j;
                    } else {
                        iSet.add(y);
                        return false;
                    }
                } else if(iSet.size() >= THRESHOLD) {
                    continue;
                } else {
                    return true;
                }
            } finally {
                release(y);
            }
        }
        return false;
    }

    /**
     * Simple sanity check for debugging
     * @return <CODE>true</CODE> iff structure passes tests
     */
    public boolean check() {
        for(int i = 0; i < capacity; i++) {
            List<T> set = table[0][i];
            for(T x : set) {
                if((hash0(x) % capacity) != i) {
                    System.out.printf("Unexpected value %d at table[0][%d] hash %d\n", x, i, hash0(x)
                            % capacity);
                    return false;
                }
            }
        }
        for(int i = 0; i < capacity; i++) {
            List<T> set = table[1][i];
            for(T x : set) {
                if((hash1(x) % capacity) != i) {
                    System.out.printf("Unexpected value %d at table[0][%d] hash %d\n", x, i, hash1(x)
                            % capacity);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Unsynchronized version of contains()
     * @param x search for this item
     * @return true iff item is found
     */
    private boolean present(T x) {
        int h0 = hash0(x) % capacity;
        if(table[0][h0].contains(x)) {
            return true;
        } else {
            int h1 = hash1(x) % capacity;
            if(table[1][h1].contains(x)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Simple sanity check for debugging
     * @param expectedSize expected size
     * @return <CODE>true</CODE> iff structure passes tests
     */
    public boolean check(int expectedSize) {
        int size = 0;
        for(int i = 0; i < capacity; i++) {
            for(T x : table[0][i]) {
                if(x != null) {
                    size++;
                    if((hash0(x) % capacity) != i) {
                        System.out.printf("Unexpected value %d at table[0][%d] hash %d\n", x, i, hash0(x)
                                % capacity);
                        return false;
                    }
                }
            }
            for(T x : table[1][i]) {
                if(x != null) {
                    size++;
                    if((hash1(x) % capacity) != i) {
                        System.out.printf("Unexpected value %d at table[0][%d] hash %d\n", x, i, hash1(x)
                                % capacity);
                        return false;
                    }
                }
            }
        }
        if(size != expectedSize) {
            System.out.printf("Bad size: found %d, expected %d\n", size, expectedSize);
            return false;
        }
        return true;
    }

}
