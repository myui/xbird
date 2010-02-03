/*
 * RefinableHashSet.java
 *
 * Created on November 15, 2006, 3:59 PM
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
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Concurrent hash set that allows the lock array to be resized.
 * @param <T> type
 * @author Maurice Herlihy
 */
public class RefinableHashSet<T> extends BaseHashSet<T> {
    AtomicMarkableReference<Thread> owner;
    volatile ReentrantLock[] locks;

    /**
     * Concurrent Cuckoo hash set. Resizes lock array.
     * @param capacity Initial number of buckets.
     */
    public RefinableHashSet(int capacity) {
        super(capacity);
        locks = new ReentrantLock[capacity];
        for(int j = 0; j < capacity; j++) {
            locks[j] = new ReentrantLock();
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
            ReentrantLock[] oldLocks = this.locks;
            int myBucket = Math.abs(x.hashCode() % oldLocks.length);
            ReentrantLock oldLock = oldLocks[myBucket];
            oldLock.lock(); // acquire lock
            who = owner.get(mark);
            if((!mark[0] || who == me) && this.locks == oldLocks) { // recheck
                return;
            } else { //  unlock & try again
                oldLock.unlock();
            }
        }
    }

    /**
     * synchronize after adding, removing, or testing for item
     * @param x item involved
     */
    @Override
    public void release(T x) {
        int myBucket = Math.abs(x.hashCode() % locks.length);
        locks[myBucket].unlock();
    }

    /**
     * Ensure that no thread is currently locking the set.
     */
    protected void quiesce() {
        for(ReentrantLock lock : locks) {
            while(lock.isLocked()) {
            } // spin
        }
    }

    /**
     * double the set size
     */
    @Override
    public void resize() {
        int oldCapacity = table.length;
        int newCapacity = 2 * oldCapacity;
        Thread me = Thread.currentThread();
        if(owner.compareAndSet(null, me, false, true)) {
            try {
                if(table.length != oldCapacity) { // someone else resized first
                    return;
                }
                quiesce();
                List<T>[] oldTable = table;
                table = (List<T>[]) new List[newCapacity];
                for(int i = 0; i < newCapacity; i++)
                    table[i] = new ArrayList<T>();
                locks = new ReentrantLock[newCapacity];
                for(int j = 0; j < locks.length; j++) {
                    locks[j] = new ReentrantLock();
                }
                initializeFrom(oldTable);
            } finally {
                owner.set(null, false); // restore prior state
            }
        }
    }

    @Override
    public boolean policy() {
        return size / table.length > 4;
    }

    private void initializeFrom(List<T>[] oldTable) {
        for(List<T> bucket : oldTable) {
            for(T x : bucket) {
                int myBucket = Math.abs(x.hashCode() % table.length);
                table[myBucket].add(x);
            }
        }
    }
}
