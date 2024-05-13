
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
public final class FCFS extends NonPreemptiveScheduling {
    public static final String FIFO_REGEX = "^(?i)FCFS|FIFO$";
    
    public FCFS(ScheduleTable table, CycleComponent component, ReadyQueue queue) {
        super(table, component, queue);
    }
    
    @Override
    public QueueChangeEvent execute(int timePoint, int time, Map<Process, Integer> processWaitingTimes) {
        if(mQueue.isEmpty())
            return null;
        
        QueueElement advancedElement = mQueue.advance(time);
        Process advancedProcess = advancedElement.getProcess();
        
        // Every process in the queue, except the advanced one, has its waiting time increased by 'time'
        if(processWaitingTimes != null) {
            for(var element : mQueue.elements()) {
                Process elementProcess = element.getProcess();
                if(!elementProcess.equals(advancedProcess)) {
                    Integer waitingTime = processWaitingTimes.get(elementProcess);
                    if(waitingTime != null) {
                        processWaitingTimes.put(elementProcess, waitingTime + time);
                    }
                }
            }
        }
        
        mComponent.getGanttChart().add(advancedProcess, new Interval(timePoint, timePoint + time));
        if(advancedElement.getTime() <= 0) {
            return new QueueChangeEvent(mQueue.remove().getProcess());
        }
        else {
            return null;
        }
    }
}
