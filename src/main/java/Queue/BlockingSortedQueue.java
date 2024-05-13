
package Queue;

import java.util.*;

/**
 *
 * @version 1.0
 */
public class BlockingSortedQueue extends SortedQueue {
    protected QueueElement mBlockedElement;
    
    public BlockingSortedQueue() {
        super();
        mBlockedElement = null;
    }
    
    @Override
    public void add(QueueElement element) {
        mQueue.add(element);
    }
    
    @Override
    public QueueElement remove() {
        if(mBlockedElement == null) {
            return super.remove();
        }
        else {
            QueueElement result = mBlockedElement;
            mQueue.remove(mBlockedElement);
            mBlockedElement = null;
            return result;
        }
    }
    
    @Override
    public QueueElement nextElement() {
        return (mBlockedElement != null) ? mBlockedElement : super.nextElement();
    }
    
    protected QueueElement replaceBlockedElementTime(int time) throws IllegalArgumentException {
        if(time < 0)
            throw new IllegalArgumentException(Integer.toString(time));
        
        if(mBlockedElement == null)
            return null;
        
        mQueue.remove(mBlockedElement);
        mBlockedElement = new QueueElement(mBlockedElement.getProcess(), time);
        mQueue.add(mBlockedElement);
        
        return mBlockedElement;
    }
    
    @Override
    public QueueElement advance(int time) throws IllegalArgumentException {
        if(time < 0)
            throw new IllegalArgumentException(Integer.toString(time));
        
        if(mBlockedElement == null) {
            mBlockedElement = super.advance(time);
            return mBlockedElement;
        }
        else {
            int newTime = mBlockedElement.getTime() - time;
            replaceBlockedElementTime((newTime > 0) ? newTime : 0);
            return mBlockedElement;
        }
    }
    
    @Override
    public QueueElement tryAdvance(int time) throws IllegalArgumentException {
        if(time < 0)
            throw new IllegalArgumentException(Integer.toString(time));
        
        if(mBlockedElement == null) {
            mBlockedElement = super.tryAdvance(time);
            return mBlockedElement;
        }
        else {
            int newTime = mBlockedElement.getTime() - time;
            if(newTime < 0) {
                return null;
            }
            else {
                replaceBlockedElementTime(newTime);
                return mBlockedElement;
            }
        }
    }
    
    @Override
    public QueueElement[] elements() {
        if(mBlockedElement == null) {
            return super.elements();
        }
        else {
            List<QueueElement> elementList = new ArrayList<>(mQueue.size());
            elementList.add(mBlockedElement);
            mQueue.forEach(e -> {
                if(!e.equals(mBlockedElement))
                    elementList.add(e);
            });
            
            return elementList.toArray(QueueElement[]::new);
        }
    }
    
    @Override
    public String toString() {
        if(mBlockedElement == null) {
            return super.toString();
        }
        else {
            List<QueueElement> elementList = new ArrayList<>(mQueue.size());
            elementList.add(mBlockedElement);
            mQueue.forEach(e -> {
                if(!e.equals(mBlockedElement))
                    elementList.add(e);
            });

            return "@Blocked " + elementList.toString();  
        }
    }
}
