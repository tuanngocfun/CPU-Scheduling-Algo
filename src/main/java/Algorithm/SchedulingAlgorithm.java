
package Algorithm;

import Queue.*;
import Core.*;
import Core.Process;
import java.util.Map;

/**
 *
 * @version 1.0
 */
public abstract class SchedulingAlgorithm {
    protected ScheduleTable mTable;
    protected CycleComponent mComponent;
    protected ReadyQueue mQueue;
    
    protected SchedulingAlgorithm(ScheduleTable table, CycleComponent component, ReadyQueue queue) {
        mTable = table;
        mComponent = component;
        mQueue = queue;
    }
        
    public CycleComponent getComponent() {
        return mComponent;
    }
    
    public ReadyQueue getQueue() {
        return mQueue;
    }
    
    /**
     * Execute the scheduling algorithm.
     *
     * @param timePoint The current time point in the scheduling.
     * @param time The time to advance in this execution.
     * @param processWaitingTimes A map from each process to its total waiting time.
     * @return A QueueChangeEvent if a process finishes its work, otherwise null.
     */
    public abstract QueueChangeEvent execute(int timePoint, int time, Map<Process, Integer> processWaitingTimes);
}
