/*
 * CoarseList.java
 *
 * Created on January 3, 2006, 5:02 PM
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
 * List using coarse-grained synchronization.
 * @param T Item type.
 * @author Maurice Herlihy
 */
public class CoarseList<T> {
  /**
   * First list Node
   */
  private Node head;
  /**
   * Last list Node
   */
  private Node tail;
  /**
   * Synchronizes access to list
   */
  private Lock lock = new ReentrantLock();
  
  /**
   * Constructor
   */
  public CoarseList() {
    // Add sentinels to start and end
    head  = new Node(Integer.MIN_VALUE);
    tail  = new Node(Integer.MAX_VALUE);
    head.next = this.tail;
  }
  
  /**
   * Add an element.
   * @param item element to add
   * @return true iff element was not there already
   */
  
  public boolean add(T item) {
    Node pred, curr;
    int key = item.hashCode();
    lock.lock();
    try {
      pred = head;
      curr = pred.next;
      while (curr.key < key) {
        pred = curr;
        curr = curr.next;
      }
      if (key == curr.key) {
        return false;
      } else {
        Node node = new Node(item);
        node.next = curr;
        pred.next = node;
        return true;
      }
    } finally {
      lock.unlock();
    }
  }
  /**
   * Remove an element.
   * @param item element to remove
   * @return true iff element was present
   */
  public boolean remove(T item) {
    Node pred, curr;
    int key = item.hashCode();
    lock.lock();
    try {
      pred = this.head;
      curr = pred.next;
      while (curr.key < key) {
        pred = curr;
        curr = curr.next;
      }
      if (key == curr.key) {  // present
        pred.next = curr.next;
        return true;
      } else {
        return false;         // not present
      }
    } finally {               // always unlock
      lock.unlock();
    }
  }
  /**
   * Test whether element is present
   * @param item element to test
   * @return true iff element is present
   */
  public boolean contains(T item) {
    Node pred, curr;
    int key = item.hashCode();
    lock.lock();
    try {
      pred = head;
      curr = pred.next;
      while (curr.key < key) {
        pred = curr;
        curr = curr.next;
      }
      return (key == curr.key);
    } finally {               // always unlock
      lock.unlock();
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
     * Constructor for usual Node
     * @param item element in list
     */
    Node(T item) {
      this.item = item;
      this.key = item.hashCode();
    }
    /**
     * Constructor for sentinel Node
     * @param key should be min or max int value
     */
    Node(int key) {
      this.item = null;
      this.key = key;
    }
  }
}
