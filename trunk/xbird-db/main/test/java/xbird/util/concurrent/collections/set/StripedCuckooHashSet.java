/*
 * StripedCuckooHashSet.java
 *
 * Created on November 1, 2006, 3:59 PM
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Concurrent Cuckoo hashing using lock striping.
 * @param <T> type
 * @author Maurice Herlihy
 */
public class StripedCuckooHashSet<T> extends PhasedCuckooHashSet<T> {
    final ReentrantLock[][] lock;

    /**
     * Constructor
     * @param capacity Internal array size.
     */
    public StripedCuckooHashSet(int capacity) {
        super(capacity);
        lock = new ReentrantLock[2][capacity];
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < capacity; j++) {
                lock[i][j] = new ReentrantLock();
            }
        }
    }

    /**
     * double the set size
     */
    @Override
    public void resize() {
        int oldCapacity = capacity;
        for(Lock _lock : lock[0]) {
            _lock.lock();
        }
        try {
            if(capacity != oldCapacity) { // someone else resized first
                return;
            }
            List<T>[][] oldTable = table;
            capacity = 2 * capacity;
            table = (List<T>[][]) new List[2][capacity];
            for(List<T>[] row : table) {
                for(int i = 0; i < row.length; i++) {
                    row[i] = new ArrayList<T>(PROBE_SIZE);
                }
            }
            for(List<T>[] row : oldTable) {
                for(List<T> set : row) {
                    for(T z : set) {
                        add(z);
                    }
                }
            }
        } finally {
            for(Lock _lock : lock[0]) {
                _lock.unlock();
            }
        }
    }

    /**
     * Synchronize before adding, removing, or testing for item
     * @param x item involved
     */
    @Override
    public final void acquire(T x) {
        Lock lock0 = lock[0][hash0(x) % lock[0].length];
        Lock lock1 = lock[1][hash1(x) % lock[1].length];
        lock0.lock();
        lock1.lock();
    }

    /**
     * synchronize after adding, removing, or testing for item
     * @param x item involved
     */
    @Override
    public final void release(T x) {
        Lock lock0 = lock[0][hash0(x) % lock[0].length];
        Lock lock1 = lock[1][hash1(x) % lock[1].length];
        lock0.unlock();
        lock1.unlock();
    }
}
