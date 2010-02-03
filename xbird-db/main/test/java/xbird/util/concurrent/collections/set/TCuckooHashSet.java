/*
 * TCuckooHashSet.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Concurrent Cuckoo hashing.
 * @param <T> type
 * @author Maurice Herlihy
 */
public class TCuckooHashSet<T> {
    volatile T[][] table;
    volatile int size;
    static final int LIMIT = 32;
    static final int LOCKS = 8;
    Lock[][] locks;
    // used for resizeing
    Random random = new Random();

    /**
     * Constructor
     * @param capacity Initial array size.
     */
    public TCuckooHashSet(int capacity) {
        locks = new Lock[2][LOCKS];
        table = (T[][]) new Object[2][capacity];
        size = capacity;
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < LOCKS; j++) {
                locks[i][j] = new ReentrantLock();
            }
        }
    }

    private final int hash0(Object x) {
        return Math.abs(x.hashCode() % size);
    }

    private final int hash1(Object x) {
        random.setSeed(x.hashCode());
        return random.nextInt(size);
    }

    /**
     * Is item in set?
     * @param x Item to test.
     * @return <code>true</code> iff item is present.
     */
    public boolean contains(T x) {
        if(x == null) {
            throw new IllegalArgumentException();
        }
        int h0 = hash0(x);
        Lock lock0 = locks[0][h0 % LOCKS];
        try {
            lock0.lock();
            if(x.equals(table[0][h0])) {
                return true;
            } else {
                int h1 = hash1(x);
                Lock lock1 = locks[1][h1 % LOCKS];
                try {
                    lock1.lock();
                    if(x.equals(table[1][h1])) {
                        return true;
                    }
                    return false;
                } finally {
                    lock1.unlock();
                }
            }
        } finally {
            lock0.unlock();
        }
    }

    /**
     * Remove item from set.
     * @param x Item to remove.
     * @return <code>true</code> iff set was changed.
     */
    public boolean remove(T x) {
        if(x == null) {
            throw new IllegalArgumentException();
        }
        int h0 = hash0(x);
        Lock lock0 = locks[0][h0 % LOCKS];
        try {
            lock0.lock();
            if(x.equals(table[0][h0])) {
                table[0][h0] = null;
                return true;
            } else {
                int h1 = hash1(x);
                Lock lock1 = locks[1][h1 % LOCKS];
                try {
                    lock1.lock();
                    if(x.equals(table[1][h1])) {
                        table[1][h1] = null;
                        return true;
                    }
                    return false;
                } finally {
                    lock1.unlock();
                }
            }
        } finally {
            lock0.unlock();
        }
    }

    /**
     * Add item to set.
     * @param x Item to add.
     * @return <code>true</code> iff set was changed.
     */
    public boolean add(T x) {
        if(x == null) {
            throw new IllegalArgumentException();
        }
        TreeSet<Intent>[] intentions = (TreeSet<Intent>[]) new TreeSet[2];
        for(int i = 0; i < intentions.length; i++) {
            intentions[i] = new TreeSet<Intent>();
        }
        List<Lock> acquired = new ArrayList<Lock>(locks.length);
        while(true) { // forever true
            try {
                // already present?
                if(optContains(intentions, x)) {
                    if(validate(intentions, acquired)) {
                        return false;
                    } else {
                        continue;
                    }
                }
                // not already present?
                if(optAdd(intentions, x)) {
                    if(validate(intentions, acquired)) {
                        doAdd(x);
                        return true;
                    } else {
                        continue;
                    }
                }
            } finally {
                release(acquired);
                intentions[0].clear();
                intentions[1].clear();
            }
        }
    }

    private void release(List<Lock> acquired) {
        for(Lock lock : acquired) {
            lock.unlock();
        }
        acquired.clear();
    }

    private T swap(int i, int j, T value) {
        T result = table[i][j];
        table[i][j] = value;
        return result;
    }

    protected boolean optContains(TreeSet<Intent>[] intentions, T x) {
        int h0 = hash0(x);
        T y = table[0][h0];
        intentions[0].add(new Intent(h0, y));
        if(x.equals(y)) {
            return true;
        } else {
            int h1 = hash1(x);
            y = table[1][h1];
            intentions[1].add(new Intent(h1, y));
            if(x.equals(y)) {
                return true;
            }
            return false;
        }
    }

    boolean validate(TreeSet<Intent>[] intentions, List<Lock> acquired) {
        for(int phase = 0; phase < 2; phase++) {
            for(Intent intent : intentions[phase]) {
                // lock and prepare to unlock
                Lock lock = locks[phase][intent.index % LOCKS];
                lock.lock();
                acquired.add(lock);
                // beware that expected could be null
                T expected = intent.item;
                T found = table[phase][intent.index];
                if(expected == null) {
                    if(found != null) {
                        return false;
                    }
                } else if(!expected.equals(found)) {
                    return false;
                }
            }
        }
        return true;
    }

    void unlock(TreeSet<Intent>[] intentions) {
        for(int phase = 0; phase < 2; phase++) {
            for(Intent intent : intentions[phase]) {
                locks[phase][intent.index % LOCKS].unlock();
            }
        }
    }

    boolean optAdd(TreeSet<Intent>[] intentions, T x) {
        for(int i = 0; i < LIMIT; i++) {
            int h0 = hash0(x);
            x = table[0][h0];
            intentions[0].add(new Intent(h0, x));
            if(x == null) {
                return true;
            } else {
                int h1 = hash1(x);
                x = table[1][h1];
                intentions[1].add(new Intent(h1, x));
                if(x == null) {
                    return true;
                }
            }
        }
        resize(size);
        return false;
    }

    void doAdd(T x) {
        for(int i = 0; i < LIMIT; i++) {
            if((x = swap(0, hash0(x), x)) == null) {
                return;
            } else if((x = swap(1, hash1(x), x)) == null) {
                return;
            }
        }
    }

    void resize(int oldSize) {
        if(size > oldSize) { // someone beat us to it
            return;
        }
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < LOCKS; j++) {
                locks[i][j].lock();
            }
        }
        T[][] oldtable = table;
        size = 2 * size;
        table = (T[][]) new Object[2][size];
        for(int i = 0; i < 2; i++) {
            for(T x : oldtable[i]) {
                if(x != null) {
                    add(x);
                }
            }
        }
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < LOCKS; j++) {
                locks[i][j].unlock();
            }
        }
    }

    class Intent implements Comparable<Intent> {
        public int index;
        public T item;

        Intent(int index, T item) {
            this.index = index;
            this.item = item;
        }

        public int compareTo(TCuckooHashSet.Intent o) {
            // equal items never compared
            if((this.index % LOCKS) < (o.index % LOCKS)) {
                return -1;
            } else if((this.index % LOCKS) > (o.index % LOCKS)) {
                return 1;
            } else {
                return 0;
            }
        }

    }
}