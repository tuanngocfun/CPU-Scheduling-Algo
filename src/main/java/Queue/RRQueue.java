
package Queue;

import Algorithm.RR;
import java.util.*;

/**
 * This class represents a Round Robin scheduling queue used to manage processes
 * in a time-sharing system. In Round Robin scheduling, each process is assigned a fixed time slot
 * (quantum) in a cyclic order. When a process's time slice expires, it is placed at the end of the queue.
 * This approach ensures that all processes receive an equal share of the CPU time.
 * </p>
 * Mathematical Representation:</p>
 * Let Q be the queue of processes {P1, P2, ..., Pn}.</p>
 * Let t_i be the time slice given to process P_i.</p>
 * The process P_i is executed for min(t_i, q) time, where q is the quantum.</p>
 * If t_i > q, then P_i is re-enqueued with t_i - q remaining time.
 *
 * @version 1.0
 */
public class RRQueue implements ReadyQueue {
    protected RR mOwner;// Owner of the Round Robin scheduler, typically used to obtain quantum time
    protected final Deque<QueueElement> mQueue;// Queue to hold the processes
    
    public RRQueue() {
        mQueue = new ArrayDeque<>();
        mOwner = null;
    }
    
    public RRQueue(RR owner) {
        mQueue = new ArrayDeque<>();
        mOwner = owner;
    }
    
    public void setOwner(RR owner) {
        mOwner = owner;
    }
    
    @Override
    public int size() {
        return mQueue.size();
    }
    
    @Override
    public void add(QueueElement element) {
        mQueue.offer(element);
    }
    
    @Override
    public QueueElement remove() {
        return mQueue.poll();
    }
    
    @Override
    public QueueElement nextElement() {
        return mQueue.peekFirst();
    }

    /**
     * Returns an array of all processes in the queue.
     *
     * @return An array containing all the processes in the queue.
     */
    @Override
    public QueueElement[] elements() {
        return mQueue.toArray(QueueElement[]::new);
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
        mQueue.offerFirst(headElement);
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
        if(headElement != null) {
            int newTime = headElement.getTime() - time;
            if(newTime >= 0) {
                headElement = replaceHeadElementTime(newTime);
                return headElement;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    /**
     * Calculates the time until the next process in the queue is expected to finish its execution.
     * This method simulates the passage of time based on the quantum and the remaining times of the processes.
     *
     * @return The time until the next process finishes, or null if the queue is empty or the owner is not set.
     */
    @Override
    public Integer timeTillNextProcessFinish() {
        if(this.isEmpty() || mOwner == null)
            return null;
        
        ArrayDeque<QueueElement> queue = new ArrayDeque<>();
        mQueue.forEach(e -> {
            queue.add(new QueueElement(e.getProcess(), e.getTime()));
        });
        
        int quantum = mOwner.getQuantum();
        int timeTillQuantum = mOwner.getTimeTillQuantum();
        int timePassed = 0;
        while(true) {
            QueueElement nextElement = queue.peek();
            int time = nextElement.getTime();
            if(time <= timeTillQuantum) {
                return timePassed + time;
            }
            else {
                queue.poll();
                nextElement = new QueueElement(nextElement.getProcess(), time - timeTillQuantum);
                queue.offer(nextElement);
                timePassed += timeTillQuantum;
                timeTillQuantum = quantum;
            }
        }
    }
    
    @Override
    public String toString() {
        return mQueue.toString();
    }
}
