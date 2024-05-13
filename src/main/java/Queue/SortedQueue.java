
package Queue;

import java.util.*;

/**
 * This class represents a queue where processes are sorted based on their remaining CPU burst time.
 * This can be part of several scheduling algorithms, including Shortest Job Next (SJN) or
 * Shortest Remaining Time First (SRTF), where the process with the shortest time is scheduled next.
 *</p>
 * Processes in this queue are ordered first by their remaining time and then by their process ID in
 * ascending order to break ties deterministically.
 *</p>
 * Mathematical Representation:</p>
 * Let Q be the sorted set of processes {P1, P2, ..., Pn}.</p>
 * For processes Pi and Pj with burst times ti and tj,</p>
 *    Pi precedes Pj if (ti < tj) or (ti == tj and IDi < IDj)</p>
 *</p>
 * This ensures that the process with the shortest burst time is at the front of the queue.
 * If two processes have the same burst time, the one with the smaller ID comes first.
 *
 * @version 1.0
 */
public class SortedQueue implements ReadyQueue {
    protected final TreeSet<QueueElement> mQueue;// A TreeSet is used here to keep the elements sorted automatically

    /**
     * Constructs an empty Sorted Queue. The sorting is primarily based on the
     * remaining processing time and secondarily on the process ID.
     */
    public SortedQueue() {
        mQueue = new TreeSet<>((e1, e2) -> {
            int t1 = e1.getTime();
            int t2 = e2.getTime();
            if(t1 > t2) {
                return 1;
            }
            else if(t1 < t2) {
                return -1;
            }
            else {
                int id1 = e1.getProcess().getID();
                int id2 = e2.getProcess().getID();
                return (id1 > id2) ? 1 :
                       (id1 < id2) ? -1 : 0;
            }
        });
    }

    /**
     * Returns the number of processes in the queue.
     *
     * @return The number of processes.
     */
    @Override
    public int size() {
        return mQueue.size();
    }

    @Override
    public void add(QueueElement element) {
        mQueue.add(element);
    }

    /**
     * Removes and returns the process at the front of the queue (the one with the shortest remaining time).
     *
     * @return The process with the shortest remaining time, or null if the queue is empty.
     */
    @Override
    public QueueElement remove() {
        return mQueue.pollFirst();
    }

    /**
     * Returns the process at the front of the queue (the one with the shortest remaining time)
     * without removing it.
     *
     * @return The process with the shortest remaining time, or null if the queue is empty.
     */
    @Override
    public QueueElement nextElement() {
        return !mQueue.isEmpty() ? mQueue.first() : null;
    }

    /**
     * Returns an array of all processes in the queue, sorted by remaining time and process ID.
     *
     * @return An array containing all the processes in the queue.
     */
    @Override
    public QueueElement[] elements() {
        return mQueue.toArray(QueueElement[]::new);
    }
    
    @Override
    public String toString() {
        return mQueue.toString();
    }

    /**
     * Replaces the process time at the head of the queue without removing the process.
     * Throws IllegalArgumentException if the new time is negative.
     *
     * @param time The new time to replace the old time.
     * @return The modified head element with the new time.
     * @throws IllegalArgumentException If the new time is less than 0.
     */
    protected QueueElement replaceHeadElementTime(int time) throws IllegalArgumentException {
        if(time < 0)
            throw new IllegalArgumentException(Integer.toString(time));
        
        QueueElement headElement = mQueue.pollFirst();
        if(headElement == null)
            return null;
        
        headElement = new QueueElement(headElement.getProcess(), time);
        mQueue.add(headElement);
        return headElement;
    }

    /**
     * Advances the process at the head of the queue by reducing its time by the specified amount.
     * If the remaining time after advancing is greater than 0, it updates the head's time;
     * otherwise, it removes the head from the queue.
     *
     * @param time The time to advance.
     * @return The updated head element after advancing, or null if the queue is empty.
     * @throws IllegalArgumentException If the time is negative.
     */
    @Override
    public QueueElement advance(int time) throws IllegalArgumentException {
        if(time < 0)
            throw new IllegalArgumentException(Integer.toString(time));
        
        QueueElement headElement = this.nextElement();
        if(headElement != null) {
            int newTime = headElement.getTime() - time;
            headElement = replaceHeadElementTime((newTime > 0) ? newTime : 0);
            return headElement;
        }
        else {
            return null;
        }
    }

    /**
     * Attempts to advance the time of the process at the head of the queue by the specified amount.
     * If advancing the time does not exhaust the process's time, it updates the head's time;
     * otherwise, it does nothing and returns null.
     *
     * @param time The time to attempt to advance.
     * @return The updated head element if the time is sufficient, or null otherwise.
     * @throws IllegalArgumentException If the time is negative.
     */
    @Override
    public QueueElement tryAdvance(int time) throws IllegalArgumentException {
        if(time < 0)
            throw new IllegalArgumentException(Integer.toString(time));

        QueueElement headElement = this.nextElement();
        if (headElement == null) {
            return null;
        }

        int newTime = headElement.getTime() - time;
        if(newTime >= 0) {
            return replaceHeadElementTime(newTime);
        }
        else {
            return null;
        }    
    }
}
