/*
 * CoarseHashSet.java
 *
 * Created on December 29, 2005, 11:50 PM
 *
 * From "The Art of Multiprocessor Programming",
 * by Maurice Herlihy and Nir Shavit.
 *
 * This work is licensed under a Creative Commons Attribution-Share Alike 3.0 United States License.
 * http://i.creativecommons.org/l/by-sa/3.0/us/88x31.png
 */
package xbird.util.concurrent.set;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Coarse-grained hash set.
 * @param <T> type
 * @author Maurice Herlihy
 */
public class CoarseHashSet<T> extends BaseHashSet<T> {
    final Lock lock;

    CoarseHashSet(int capacity) {
        super(capacity);
        lock = new ReentrantLock();
    }

    /**
     * double the set size
     */
    @Override
    public void resize() {
        int oldCapacity = table.length;
        lock.lock();
        try {
            if(oldCapacity != table.length) {
                return; // someone beat us to it
            }
            int newCapacity = 2 * oldCapacity;
            List<T>[] oldTable = table;
            table = (List<T>[]) new List[newCapacity];
            for(int i = 0; i < newCapacity; i++)
                table[i] = new ArrayList<T>();
            for(List<T> bucket : oldTable) {
                for(T x : bucket) {
                    int myBucket = Math.abs(x.hashCode() % table.length);
                    table[myBucket].add(x);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Synchronize before adding, removing, or testing for item
     * @param x item involved
     */
    @Override
    public final void acquire(T x) {
        lock.lock();
    }

    /**
     * synchronize after adding, removing, or testing for item
     * @param x item involved
     */
    @Override
    public void release(T x) {
        lock.unlock();
    }

    @Override
    public boolean policy() {
        return size / table.length > 4;
    }
}
