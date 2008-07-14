/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package xbird.util.concurrent.jsr166;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * A {@link BlockingQueue} in which producers may wait for consumers
 * to receive elements.  A <tt>TransferQueue</tt> may be useful for
 * example in message passing applications in which producers
 * sometimes (using method <tt>transfer</tt>) await receipt of
 * elements by consumers invoking <tt>take</tt> or <tt>poll</tt>,
 * while at other times enqueue elements (via method <tt>put</tt>)
 * without waiting for receipt. Non-blocking and time-out versions of
 * <tt>tryTransfer</tt> are also available.  A TransferQueue may also
 * be queried via <tt>hasWaitingConsumer</tt> whether there are any
 * threads waiting for items, which is a converse analogy to a
 * <tt>peek</tt> operation
 *
 * <p>Like any <tt>BlockingQueue</tt>, a <tt>TransferQueue</tt> may be
 * capacity bounded. If so, an attempted <tt>transfer</tt> operation
 * may initially block waiting for available space, and/or
 * subsequently block waiting for reception by a consumer.  Note that
 * in a queue with zero capacity, such as {@link SynchronousQueue},
 * <tt>put</tt> and <tt>transfer</tt> are effectively synonymous.
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @since 1.7
 * @author Doug Lea
 * @param <E> the type of elements held in this collection
 */
public interface TransferQueue<E> extends BlockingQueue<E> {
    /**
     * Transfers the specified element if there exists a consumer
     * already waiting to receive it, otherwise returning <tt>false</tt>
     * without enqueuing the element.
     *
     * @param e the element to transfer
     * @return <tt>true</tt> if the element was transferred, else
     *         <tt>false</tt>
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
     */
    boolean tryTransfer(E e);

    /**
     * Inserts the specified element into this queue, waiting if
     * necessary for space to become available and the element to be
     * dequeued by a consumer invoking <tt>take</tt> or <tt>poll</tt>.
     *
     * @param e the element to transfer
     * @throws InterruptedException if interrupted while waiting,
     *         in which case the element is not enqueued.
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
     */
    void transfer(E e) throws InterruptedException;

    /**
     * Inserts the specified element into this queue, waiting up to
     * the specified wait time if necessary for space to become
     * available and the element to be dequeued by a consumer invoking
     * <tt>take</tt> or <tt>poll</tt>.
     *
     * @param e the element to transfer
     * @param timeout how long to wait before giving up, in units of
     *        <tt>unit</tt>
     * @param unit a <tt>TimeUnit</tt> determining how to interpret the
     *        <tt>timeout</tt> parameter
     * @return <tt>true</tt> if successful, or <tt>false</tt> if
     *         the specified waiting time elapses before completion,
     *         in which case the element is not enqueued.
     * @throws InterruptedException if interrupted while waiting,
     *         in which case the element is not enqueued.
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *         element prevents it from being added to this queue
     */
    boolean tryTransfer(E e, long timeout, TimeUnit unit) 
        throws InterruptedException;

    /**
     * Returns true if there is at least one consumer waiting to
     * dequeue an element via <tt>take</tt> or <tt>poll</tt>. The
     * return value represents a momentary state of affairs.
     * @return true if there is at least one waiting consumer.
     */
    boolean hasWaitingConsumer();


    /**
     * Returns an estimate of the number of consumers waiting to
     * dequeue elements via <tt>take</tt> or <tt>poll</tt>. The return
     * value is an approximation of a momentary state of affairs, that
     * may be inaccurate if consumers have completed or given up
     * waiting. The value may be useful for monitoring and heuristics,
     * but not for synchronization control. Implementations of this
     * method are likely to be noticeably slower than those for
     * <tt>hasWaitingConsumer</tt>.
     * @return the number of consumers waiting to dequeue elements
     */
    int getWaitingConsumerCount();
}
