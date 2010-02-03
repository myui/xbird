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
package xbird.util.concurrent.collections.set;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ListBucket<T> implements Set<T> {
    private List<T> list = new LinkedList<T>();

    // add object with given key
    public boolean add(T x) {
        return list.add(x);
    }

    // remove object with given key
    public boolean remove(T x) {
        return list.remove(x);
    }

    // is object present?
    public boolean contains(T x) {
        return list.contains(x);
    }

    // iterate over Set elements
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
