
package Queue;

import java.util.*;

/**
 * This class represents a standard FIFO (First In, First Out) queue used in various process scheduling
 * scenarios. In a FIFO scheduling, the process that arrives first is attended to first.
 * This class models a simple queue where processes are managed in the order they arrive.
 *
 * This is often used in scenarios where fairness is prioritized, and every process gets the CPU in
 * the exact order they request it.
 *
 * @version 1.0
 */
public class StandardQueue implements ReadyQueue {
    protected final Deque<QueueElement> mQueue;
    
    public StandardQueue() {
        mQueue = new ArrayDeque<>();
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
        return mQueue.peek();
    }
    
    @Override
    public QueueElement[] elements() {
        return mQueue.toArray(QueueElement[]::new);
    }
    
    @Override
    public String toString() {
        return mQueue.toString();
    }
    
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
}
