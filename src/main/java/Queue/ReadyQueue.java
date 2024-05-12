
package Queue;

import Core.Process;

/**
 *
 * @version 1.0
 */
public interface ReadyQueue {
    public int size();
    public void add(QueueElement element);
    public QueueElement remove();
    public QueueElement nextElement();
    public QueueElement[] elements();
    
    // Method to advance the element at the head of the queue in term of time
    // This method returns the advanced element after advancing
    public QueueElement advance(int time) throws IllegalArgumentException;
    
    // Method to attempt to advance the element at the head of the queue in term of time
    // This method returns the advanced element after advancing IF the given time does not exceed the time of the element
    // Otherwise, it does not advance the element and simply returns null
    public QueueElement tryAdvance(int time) throws IllegalArgumentException;

    // Return the amount of time until a process in this queue finishes
    // Generally, this is the time of the element returned by a call to a queue nextElement()
    public default Integer timeTillNextProcessFinish() {
        QueueElement nextElement = this.nextElement();
        if(nextElement == null) {
            return null;
        }
        else {
            return nextElement.getTime();
        }
    }
    
    public default boolean isEmpty() {
        return this.size() == 0;
    }
    
    public default void add(Process process, int time) {
        this.add(new QueueElement(process, time));
    }
}
