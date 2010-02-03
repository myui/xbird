/*
 * FineList.java
 *
 * Created on January 3, 2006, 6:50 PM
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
 * Fine-grained synchronization: lock coupling (hand-over-hand locking).
 * @param T Item type.
 * @author Maurice Herlihy
 */
public class FineList<T> {
  /**
   * First list entry
   */
  private Node head;
  /**
   * Constructor
   */
  public FineList() {
    // Add sentinels to start and end
    head      = new Node(Integer.MIN_VALUE);
    head.next = new Node(Integer.MAX_VALUE);
  }
  /**
   * Add an element.
   * @param item element to add
   * @return true iff element was not there already
   */
  public boolean add(T item) {
    int key = item.hashCode();
    head.lock();
    Node pred = head;
    try {
      Node curr = pred.next;
      curr.lock();
      try {
        while (curr.key < key) {
          pred.unlock();
          pred = curr;
          curr = curr.next;
          curr.lock();
        }
        if (curr.key == key) {
          return false;
        }
        Node newNode = new Node(item);
        newNode.next = curr;
        pred.next = newNode;
        return true;
      } finally {
        curr.unlock();
      }
    } finally {
      pred.unlock();
    }
  }
  /**
   * Remove an element.
   * @param item element to remove
   * @return true iff element was present
   */
  public boolean remove(T item) {
    Node pred = null, curr = null;
    int key = item.hashCode();
    head.lock();
    try {
      pred = head;
      curr = pred.next;
      curr.lock();
      try {
        while (curr.key < key) {
          pred.unlock();
          pred = curr;
          curr = curr.next;
          curr.lock();
        }
        if (curr.key == key) {
          pred.next = curr.next;
          return true;
        }
        return false;
      } finally {
        curr.unlock();
      }
    } finally {
      pred.unlock();
    }
  }
  public boolean contains(T item) {
    Node last = null, pred = null, curr = null;
    int key = item.hashCode();
    head.lock();
    try {
      pred = head;
      curr = pred.next;
      curr.lock();
      try {
        while (curr.key < key) {
          pred.unlock();
          pred = curr;
          curr = curr.next;
          curr.lock();
        }
        return (curr.key == key);
      } finally {
        curr.unlock();
      }
    } finally {
      pred.unlock();
    }
  }
  /**
   * list Node
   */
  private class Node {
    /**
     * actual item
     */
    T item;
    /**
     * item's hash code
     */
    int key;
    /**
     * next Node in list
     */
    Node next;
    /**
     * synchronizes individual Node
     */
    Lock lock;
    /**
     * Constructor for usual Node
     * @param item element in list
     */
    Node(T item) {
      this.item = item;
      this.key = item.hashCode();
      this.lock = new ReentrantLock();
    }
    /**
     * Constructor for sentinel Node
     * @param key should be min or max int value
     */
    Node(int key) {
      this.item = null;
      this.key = key;
      this.lock = new ReentrantLock();
    }
    /**
     * Lock Node
     */
    void lock() {lock.lock();}
    /**
     * Unlock Node
     */
    void unlock() {lock.unlock();}
  }
}

