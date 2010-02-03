/*
 * OptimisticList.java
 *
 * Created on January 4, 2006, 1:49 PM
 *
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 *
 * This work is licensed under a Creative Commons Attribution-Share Alike 3.0 United States License.
 * http://i.creativecommons.org/l/by-sa/3.0/us/88x31.png
 */
package lists;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Optimistic List implementation.
 * @param T Item type.
 * @author Maurice Herlihy
 */
public class OptimisticList<T> {
  /**
   * First list entry
   */
  private Entry head;
  /**
   * Constructor
   */
  public OptimisticList() {
    this.head  = new Entry(Integer.MIN_VALUE);
    this.head.next = new Entry(Integer.MAX_VALUE);
  }
  /**
   * Add an element.
   * @param item element to add
   * @return true iff element was not there already
   */
  public boolean add(T item) {
    int key = item.hashCode();
    while (true) {
      Entry pred = this.head;
      Entry curr = pred.next;
      while (curr.key <= key) {
        pred = curr; curr = curr.next;
      }
      pred.lock(); curr.lock();
      try {
        if (validate(pred, curr)) {
          if (curr.key == key) { // present
            return false;
          } else {               // not present
            Entry entry = new Entry(item);
            entry.next = curr;
            pred.next = entry;
            return true;
          }
        }
      } finally {                // always unlock
        pred.unlock(); curr.unlock();
      }
    }
  }
  /**
   * Remove an element.
   * @param item element to remove
   * @return true iff element was present
   */
  public boolean remove(T item) {
    int key = item.hashCode();
    while (true) {
      Entry pred = this.head;
      Entry curr = pred.next;
      while (curr.key < key) {
        pred = curr; curr = curr.next;
      }
      pred.lock(); curr.lock();
      try {
        if (validate(pred, curr)) {
          if (curr.key == key) { // present in list
            pred.next = curr.next;
            return true;
          } else {               // not present in list
            return false;
          }
        }
      } finally {                // always unlock
        pred.unlock(); curr.unlock();
      }
    }
  }
  /**
   * Test whether element is present
   * @param item element to test
   * @return true iff element is present
   */
  public boolean contains(T item) {
    int key = item.hashCode();
    while (true) {
      Entry pred = this.head; // sentinel node;
      Entry curr = pred.next;
      while (curr.key < key) {
        pred = curr; curr = curr.next;
      }
      try {
        pred.lock(); curr.lock();
        if (validate(pred, curr)) {
          return (curr.key == key);
        }
      } finally {                // always unlock
        pred.unlock(); curr.unlock();
      }
    }
  }
  /**
     * Check that prev and curr are still in list and adjacent
     * @param pred predecessor node
     * @param curr current node
     * @return whther predecessor and current have changed
     */
  private boolean validate(Entry pred, Entry curr) {
    Entry entry = head;
    while (entry.key <= pred.key) {
      if (entry == pred)
        return pred.next == curr;
      entry = entry.next;
    }
    return false;
  }
  /**
   * list entry
   */
  private class Entry {
    /**
     * actual item
     */
    T item;
    /**
     * item's hash code
     */
    int key;
    /**
     * next entry in list
     */
    Entry next;
    /**
     * Synchronizes entry.
     */
    Lock lock;
    /**
     * Constructor for usual entry
     * @param item element in list
     */
    Entry(T item) {
      this.item = item;
      this.key = item.hashCode();
      lock = new ReentrantLock();
    }
    /**
     * Constructor for sentinel entry
     * @param key should be min or max int value
     */
    Entry(int key) {
      this.key = key;
      lock = new ReentrantLock();
    }
    /**
     * Lock entry
     */
    void lock() {lock.lock();}
    /**
     * Unlock entry
     */
    void unlock() {lock.unlock();}
  }
}
