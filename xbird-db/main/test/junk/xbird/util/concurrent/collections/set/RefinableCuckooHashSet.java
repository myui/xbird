/*
 * RefinableCuckooHashSet.java
 *
 * Created on November 1, 2006, 4:49 PM
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
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Version of concurrent Cuckoo hashing that allows the lock array to be resized.
  * @param <T> type
 * @author Maurice Herlihy
 */
public class RefinableCuckooHashSet<T> extends PhasedCuckooHashSet<T> {
    AtomicMarkableReference<Thread> owner;
    volatile ReentrantLock[][] locks;

    /**
     * Concurrent Cuckoo hash set. Resizes lock array.
     * @param capacity Initial bucket size.
     */
    public RefinableCuckooHashSet(int capacity) {
        super(capacity);
        locks = new ReentrantLock[2][capacity];
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < capacity; j++) {
                locks[i][j] = new ReentrantLock();
            }
        }
        owner = new AtomicMarkableReference<Thread>(null, false);
    }

    /**
     * Synchronize before adding, removing, or testing for item
     * @param x item involved
     */
    @Override
    public void acquire(T x) {
        boolean[] mark = { true };
        Thread me = Thread.currentThread();
        Thread who;
        while(true) {
            do { // wait until not resizing
                who = owner.get(mark);
            } while(mark[0] && who != me);
            ReentrantLock[][] oldLocks = this.locks;
            ReentrantLock oldLock0 = oldLocks[0][hash0(x) % oldLocks[0].length];
            ReentrantLock oldLock1 = oldLocks[1][hash1(x) % oldLocks[1].length];
            oldLock0.lock(); // acquire locks
            oldLock1.lock();
            who = owner.get(mark);
            if((!mark[0] || who == me) && this.locks == oldLocks) { // recheck
                return;
            } else { //  unlock & try again
                oldLock0.unlock();
                oldLock1.unlock();
            }
        }
    }

    /**
     * synchronize after adding, removing, or testing for item
     * @param x item involved
     */
    @Override
    public final void release(T x) {
        Lock lock0 = locks[0][hash0(x) % locks[0].length];
        Lock lock1 = locks[1][hash1(x) % locks[1].length];
        lock0.unlock();
        lock1.unlock();
    }

    /**
     * Ensure that no thread is currently locking the set.
     */
    protected void quiesce() {
        for(ReentrantLock lock : locks[0]) {
            while(lock.isLocked()) {
            } // spin
        }
    }

    /**
     * double the set size
     */
    @Override
    public void resize() {
        int oldCapacity = capacity;
        Thread me = Thread.currentThread();
        if(owner.compareAndSet(null, me, false, true)) {
            try {
                if(capacity != oldCapacity) { // someone else resized first
                    return;
                }
                quiesce();
                capacity = 2 * capacity;
                List<T>[][] oldTable = table;
                table = (List<T>[][]) new List[2][capacity];
                locks = new ReentrantLock[2][capacity];
                for(int i = 0; i < 2; i++) {
                    for(int j = 0; j < capacity; j++) {
                        locks[i][j] = new ReentrantLock();
                    }
                }
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
                owner.set(null, false); // restore prior state
            }
        }
    }
}
