
package Handler;

import Algorithm.*;
import Core.Process;
import Core.*;
import Util.*;
import Queue.*;
import java.util.*;

/**
 *
 * @version 1.0
 */
public final class AlgorithmHandler {
    private final Scheduler mScheduler;
    private SchedulingAlgorithm mCPUAlgorithm;
    private Map<CycleComponent, SchedulingAlgorithm> mComponentAlgorithmMap;
    private Map<Process, Double> mProcessCycleStateMap;
    private SortedSet<Integer> mRQArrivalTimes;
    private Map<Process, Integer> mProcessWaitingTimes;
    private Map<Process, Integer> mProcessTurnaroundTimes;
    
    
    AlgorithmHandler(Scheduler scheduler) {
        mScheduler = scheduler;
        initializeCache();
    }
    
    private void initializeCache() {
        mCPUAlgorithm = null;
        mComponentAlgorithmMap = new HashMap<>();
        mProcessCycleStateMap = new HashMap<>();
        mRQArrivalTimes = new TreeSet<>();
        mProcessWaitingTimes = new HashMap<>();
        mProcessTurnaroundTimes = new HashMap<>();
    }
    
    private void clearCache() {
        mCPUAlgorithm = null;
        mComponentAlgorithmMap.clear();
        mProcessCycleStateMap.clear();
        mRQArrivalTimes.clear();
        mProcessWaitingTimes.clear();
        mProcessTurnaroundTimes.clear();
    }
    
    private ReadyQueue getComponentAssociatedQueue(CycleComponent component) {
        var associatedAlgo = mComponentAlgorithmMap.get(component);
        return (associatedAlgo != null) ? associatedAlgo.getQueue() : null;
    }
    
    private boolean isWholeNumber(Double number) {
        if(number == null) {
            return false;
        }
        else {
            return number == number.intValue();
        }
    }
    
    private CycleComponentBurst toNextBurst(Process process) {
        Double previousState = mProcessCycleStateMap.get(process);
        if(previousState == null)
            return null;
        
        Double currentState = previousState;
        ScheduleTable table = mScheduler.getScheduleTable();
        SchedulingInfo processSchedule = table.getSchedule(process);
        
        int cycleNo = previousState.intValue();
        Integer lastCycle = processSchedule.lastCycleNo();
        if(lastCycle == null)
            return null;
        
        CycleComponentBurst result = null;
        while(true) {
            // If the current cycle exceeds the last cycle in the table, it means that the process has reached its end of the cycle
            if(cycleNo > lastCycle) {
                mProcessCycleStateMap.remove(process);
                break;
            }
            
            // If current state is a whole number, it's supposed next Burst is the IOBurst of the current cycle
            if(isWholeNumber(currentState)) {
                Cycle cycle = processSchedule.getCycle(cycleNo);
                if(cycle != null) {
                    result = cycle.getIOBurst();
                    // If the cycle really has an IOBurst, this is the next burst
                    if(result != null)  {
                        mProcessCycleStateMap.put(process, currentState + 0.5);
                        break;
                    }
                }
            }
            // Otherwise, it's supposed next Burst is the CPUBurst of the next cycle
            else {
                cycleNo++;
                Cycle cycle = processSchedule.getCycle(cycleNo);
                if(cycle != null) {
                    result = cycle.getCPUBurst();
                    // If the cycle really has an CPUBurst, this is the next burst
                    if(result != null)  {
                        mProcessCycleStateMap.put(process, currentState + 0.5);
                        break;
                    }
                }
            }
            
            currentState += 0.5;
        }
        
        return result;
    }
    
    public boolean setup(CPU cpu, Collection<IODevice> ioDevices) {
        String cpuAlgoString = mScheduler.getCPUAlgorithm();
        String resourceAlgoString = mScheduler.getResourceAlgorithm();
        ScheduleTable table = mScheduler.getScheduleTable();
        
        SchedulingAlgorithm cpuAlgorithm = AlgorithmFactory.getAlgorithm(cpuAlgoString, table, cpu);
        if(cpuAlgorithm == null || cpu.getGanttChart() == null) {
            return false;
        }
        else {
            mComponentAlgorithmMap.put(cpu, cpuAlgorithm);
            mCPUAlgorithm = cpuAlgorithm;
        }
        
        for(var ioDevice : ioDevices) {
            SchedulingAlgorithm resourceAlgorithm = AlgorithmFactory.getAlgorithm(resourceAlgoString, table, ioDevice);
            if(resourceAlgorithm == null || ioDevice.getGanttChart() == null) {
                mComponentAlgorithmMap.clear();
                return false;
            }
            else {
                mComponentAlgorithmMap.put(ioDevice, resourceAlgorithm);
            }
        }
        
        for(var process : table.getProcesses()) {
            mProcessCycleStateMap.put(process, 1.0);
            mRQArrivalTimes.add(table.getSchedule(process).getRQArrivalTime());
            mProcessWaitingTimes.put(process, 0);
            //mProcessWaitingTimes.put(process, 0);
            //mProcessTurnaroundTimes.put(process, 0);
        }
        
        return true;
    }
    
    public RunResult run() {
        ScheduleTable table = mScheduler.getScheduleTable();
        Collection<SchedulingAlgorithm> algorithms = mComponentAlgorithmMap.values();
        Set<Process> arrivedProcesses = new TreeSet<>(new ProcessComparator());
        int processCount = mProcessCycleStateMap.keySet().size();
        
        int currentTime = 0;
        int advancedTime;
        while(!mProcessCycleStateMap.isEmpty()) {
            advancedTime = Integer.MAX_VALUE;
            
            // Add processes to corresponding queue at their first RQ arrival
            if(arrivedProcesses.size() < processCount) {
                // For every process
                for(Process process : mProcessCycleStateMap.keySet()) {
                    // Get the schedule associating with the process
                    SchedulingInfo processSchedule = table.getSchedule(process);
                    
                    // If the process has not been added to the list of arrived processes AND the process has already arrived at the RQ
                    if(!arrivedProcesses.contains(process) && processSchedule.isRQArrived(currentTime) == true) {
                        // Get the first cycle of the process in its schedule
                        Cycle firstCycle = processSchedule.getCycle(1);
                        // Get the first component whose associating RQ the process arrived at
                        CycleComponent firstArrivedComponent = processSchedule.firstArrivedComponent();

                        // If the component is a CPU
                        if(firstArrivedComponent instanceof CPU) {
                            // Add the process to the associating RQ of this CPU with a time equals to the burst time of the first cycle's cpu burst
                            getComponentAssociatedQueue(firstArrivedComponent).add(process, firstCycle.getCPUBurst().getBurstTime());
                        }
                        else {
                            // Add the process to the associating RQ of this CPU with a time equals to the burst time of the first cycle's IO burst
                            getComponentAssociatedQueue(firstArrivedComponent).add(process, firstCycle.getIOBurst().getBurstTime());
                            // Increase the process's associating cycle state by 0.5 to indicate that it is now in an IO's RQ
                            mProcessCycleStateMap.put(process, mProcessCycleStateMap.get(process) + 0.5);
                        }
                        
                        mRQArrivalTimes.remove(processSchedule.getRQArrivalTime());
                        arrivedProcesses.add(process);
                    }
                }
            }
            
            // Set 'advancedTime' so that the next 'currentTime' is the lowest RQ Arrival Times (if any)
            if(!mRQArrivalTimes.isEmpty()) {
                advancedTime = mRQArrivalTimes.first() - currentTime;
            }
            
            // At this time, 'avancedTime' has been set so that it accommodates with RQ Arrival Times
            
            // Get the remaining working times of all the processes in the queue
            // Then set 'advancedTime' to either the shortest time or 'advancedTime', whichever is lower
            SortedSet<Integer> remainingTimes = new TreeSet<>();
            algorithms.stream().map(algorithm -> algorithm.getQueue()).filter(queue -> (!queue.isEmpty())).forEach(queue -> {
                remainingTimes.add(queue.timeTillNextProcessFinish());
            });
            
            if(!remainingTimes.isEmpty()) {
                int shortestTime = remainingTimes.first();
                advancedTime = (shortestTime < advancedTime) ? shortestTime : advancedTime;
            }
            
            // At this time, 'advancedTime' has been set to be the possible lowest time
            
            // Execute the algorithms to advanced all the queues and store all received events
            List<QueueChangeEvent> events = new ArrayList<>();
            for(SchedulingAlgorithm algorithm : algorithms) {
                QueueChangeEvent event = (algorithm.equals(mCPUAlgorithm)) ? algorithm.execute(currentTime, advancedTime, mProcessWaitingTimes) :  
                                                                             algorithm.execute(currentTime, advancedTime, null);
                if(event != null) {
                    events.add(event);
                }
            }
            
            // Handle received events
            for(var event : events) {
                // Get the process whose queue to be changed
                Process processToChange = event.getProcess();
                // Get the next burst of the process
                CycleComponentBurst nextBurst = toNextBurst(processToChange);
                
                // If there is no next burst, the process is about to leave the system
                if(nextBurst == null) {
                    // Add the process's turnaround time to the map
                    // turnaround time = system leaving time - system arrival time = current time + advanced time - system arrival time
                    mProcessTurnaroundTimes.put(processToChange, currentTime + advancedTime - table.getSchedule(processToChange).getSystemArrivalTime());
                }
                // Otherwise, the process is about to change its queue
                else {
                    // Get the next queue of the process
                    ReadyQueue nextQueue = mComponentAlgorithmMap.get(nextBurst.getComponent()).getQueue();
                    // Add the process and its corresponding burst time to the next queue
                    nextQueue.add(processToChange, nextBurst.getBurstTime());
                }
            }
            
            // Increase current time by advanced time since 'advanced time' has elapsed
            currentTime += advancedTime;
        }
        
        // Compute WT and TA
        RunResult result = computeWTTA();
        
        // Clear cache to release memory and return the result
        clearCache();
        return result;
    }
    
    private RunResult computeWTTA() {
        if(mProcessWaitingTimes.isEmpty() || mProcessTurnaroundTimes.isEmpty()) {
            return null;
        }
        
        // Compute WT
        double WT = (double) mProcessWaitingTimes.entrySet().stream().map(entry -> entry.getValue()).reduce(0, Integer::sum) / mProcessWaitingTimes.size();
        // Compute TA
        double TA = (double) mProcessTurnaroundTimes.entrySet().stream().map(entry -> entry.getValue()).reduce(0, Integer::sum) / mProcessTurnaroundTimes.size();

        return new RunResult(WT, TA);
    }
}
