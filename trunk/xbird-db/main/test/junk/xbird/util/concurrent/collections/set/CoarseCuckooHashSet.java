/*
 * CuckooHashSet.java
 *
 * Created on May 12, 2006, 11:01 AM
 *
 * From "The Art of Multiprocessor Programming",
 * by Maurice Herlihy and Nir Shavit.
 *
 * This work is licensed under a Creative Commons Attribution-Share Alike 3.0 United States License.
 * http://i.creativecommons.org/l/by-sa/3.0/us/88x31.png
 */
package xbird.util.concurrent.collections.set;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Concurrent Cuckoo hashing.
 * @param <T> type
 * @author Maurice Herlihy
 */
public class CoarseCuckooHashSet<T> {
    protected T[][] table;
    protected Lock lock;
    protected int size;
    protected static final int LIMIT = 32;
    // used for rehashing
    private Random random;

    public CoarseCuckooHashSet(int capacity) {
        lock = new ReentrantLock();
        table = (T[][]) new Object[2][capacity];
        size = capacity;
        random = new Random();
    }

    private final int hash0(Object x) {
        return x.hashCode() % size;
    }

    private final int hash1(Object x) {
        random.setSeed(x.hashCode());
        return random.nextInt(size);
    }

    public boolean contains(T x) {
        lock.lock();
        try {
            if(x.equals(table[0][hash0(x)])) {
                return true;
            } else if(x.equals(table[1][hash1(x)])) {
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean add(T x) {
        lock.lock();
        try {
            if(contains(x)) {
                return false;
            }
            for(int i = 0; i < LIMIT; i++) {
                if((x = swap(0, hash0(x), x)) == null) {
                    return true;
                } else if((x = swap(1, hash1(x), x)) == null) {
                    return true;
                }
            }
            System.out.println("uh-oh");
            throw new CuckooException();
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(T x) {
        lock.lock();
        boolean result;
        int i0 = hash0(x);
        int i1 = hash1(x);
        try {
            if(x.equals(table[0][i0])) {
                table[0][i0] = null;
                return true;
            } else if(x.equals(table[1][i1])) {
                table[0][i1] = null;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private T swap(int i, int j, T value) {
        T result = table[i][j];
        table[i][j] = value;
        return result;
    }

    public static class CuckooException extends java.lang.RuntimeException {
    }
}
