
package Algorithm;

import Core.ScheduleTable;
import Core.*;
import Queue.*;
import Core.Process;
import java.util.Map;

/**
 *
 * @version 1.0
 */
public final class RR extends PreemptiveScheduling {
    public static final String RR_REGEX = "^(?i)(RR|Round *Robin) *\\( *q *= *\\d* *\\)$";
    private int mQuantum;
    private int mTimeTillQuantum;
    
    public RR(ScheduleTable table, CycleComponent component, ReadyQueue queue, int quantum) throws IllegalArgumentException {
        super(table, component, queue);
        
        if(quantum > 0) {
            mQuantum = quantum;
            mTimeTillQuantum = quantum;
        }
        else {
            throw new IllegalArgumentException(quantum + " <= 0");
        }
    }
    
    public void setQuantum(int quantum) throws IllegalArgumentException {
        if(quantum > 0) {
            mQuantum = quantum;
            mTimeTillQuantum = quantum;
        }
        else {
            throw new IllegalArgumentException(quantum + " <= 0");
        }
    }
    
    public int getQuantum() {
        return mQuantum;
    }
    
    public int getTimeTillQuantum() {
        return mTimeTillQuantum;
    }
    
    @Override
    public QueueChangeEvent execute(int timePoint, int time, Map<Process, Integer> processWaitingTimes) {
        if(mQueue.isEmpty())
            return null;
        
        int timePassed = 0;
        int currentTimePoint = timePoint;
        
        // Generally, for every quantum passed, queue's elements shift once
        // Therefore, we consider the state of the queue for every quantum passed or the entire time left if the time is less than designated quantum
        while(timePassed < time) {
            // Time left after this iteration
            int timeLeft = time - timePassed;
            int advancedTime;
            // If time left is less than time till quantum
            if(timeLeft < mTimeTillQuantum) {
                // 'TimeTillQuantum' will be the difference of its previous amount and time left
                mTimeTillQuantum = mTimeTillQuantum - timeLeft;
                // The advanced time will be the time left
                advancedTime = timeLeft;
            }
            // Otherwise,
            else {
                // The advanced time will be 'TimeTillQuantum'
                advancedTime = mTimeTillQuantum;
                // TimeTillQuantum will be 0 since the queue are going to be advanced by a 'TimeTillQuantum' amount of time
                mTimeTillQuantum = 0;
            }
            
            // Advanced the queue by 'advancedTime'
            QueueElement advancedElement = mQueue.advance(advancedTime);
            Process advancedProcess = advancedElement.getProcess();
            
            // Every process in the queue, except the advanced one, has its waiting time increased by 'timePassed'
            if(processWaitingTimes != null) {
                for(var element : mQueue.elements()) {
                    Process elementProcess = element.getProcess();
                    if(!elementProcess.equals(advancedProcess)) {
                        Integer waitingTime = processWaitingTimes.get(elementProcess);
                        if(waitingTime != null) {
                            processWaitingTimes.put(elementProcess, waitingTime + advancedTime);
                        }
                    }
                }
            }
            
            // Add the process into the chart
            mComponent.getGanttChart().add(advancedProcess, new Interval(currentTimePoint, currentTimePoint + advancedTime));
            
            // If the element's remaining processing time is <= 0
            if(advancedElement.getTime() <= 0) {
                // Reset the time till quantum to its inital value
                mTimeTillQuantum = mQuantum;
                // Stop the execution of the algorithm, remove the element from the queue and fire the corresponding QueueChangeEvent
                return new QueueChangeEvent(mQueue.remove().getProcess());
            }
            
            // If now is the quantum
            if(mTimeTillQuantum <= 0) {
                // Remove the head element of the queue and add it to the queue again so that it will be at the tail of the queue
                mQueue.add(mQueue.remove());
                // Reset the time till quantum to its initial value
                mTimeTillQuantum = mQuantum;
            }
            
            currentTimePoint += advancedTime;
            timePassed += advancedTime;
        }
        
        return null;
    }
}
