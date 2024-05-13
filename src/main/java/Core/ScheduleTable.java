
package Core;

import Handler.Scheduler;
import java.util.*;

/**
 *
 * @version 1.0
 */
public class ScheduleTable {
    private final Scheduler mScheduler;
    protected final Map<Process, SchedulingInfo> mProcessScheduleMap;
    
    public ScheduleTable(Scheduler scheduler) {
        mScheduler = scheduler;
        mProcessScheduleMap = new HashMap<>();
    }
    
    public Scheduler getScheduler() {
        return mScheduler;
    }
    
    public void setSchedule(Process process, SchedulingInfo schedule) {
        if(schedule != null) {
            mProcessScheduleMap.put(process, schedule);
        }
    }
    
    public SchedulingInfo getSchedule(Process process) {
        return mProcessScheduleMap.get(process);
    }
    
    public boolean hasSchedule(Process process) {
        return mProcessScheduleMap.containsKey(process);
    }
    
    public CycleComponent[] getCycleComponents(Process process) {
        SchedulingInfo processSchedule = mProcessScheduleMap.get(process);
        if(processSchedule == null)
            return null;
        
        HashSet<CycleComponent> components = new HashSet<>();
        
        for(var cycle : processSchedule.getAllCycles()) {
            CPUBurst cpuBurst = cycle.getCPUBurst();
            if(cpuBurst != null) {
                components.add(cpuBurst.getComponent());
            }

            IOBurst ioBurst = cycle.getIOBurst();
            if(ioBurst != null) {
                components.add(ioBurst.getComponent());
            }
        }
        
        return components.toArray(CycleComponent[]::new);
    }
    
    public CycleComponent[] getCycleComponents() {
        HashSet<CycleComponent> components = new HashSet<>();
        
        mProcessScheduleMap.values().forEach(processSchedule -> {
            for(var cycle : processSchedule.getAllCycles()) {
                CPUBurst cpuBurst = cycle.getCPUBurst();
                if(cpuBurst != null) {
                    components.add(cpuBurst.getComponent());
                }
                
                IOBurst ioBurst = cycle.getIOBurst();
                if(ioBurst != null) {
                    components.add(ioBurst.getComponent());
                }
            }
        });
        
        return components.toArray(CycleComponent[]::new);
    }
    
    public Process[] getProcesses() {
        return mProcessScheduleMap.keySet().toArray(Process[]::new);
    }
    
    public CycleComponent getProcessFirstArrivedComponent(Process process) {
        SchedulingInfo processSchedule = mProcessScheduleMap.get(process);
        if(processSchedule == null)
            return null;
        
        Cycle firstCycle = processSchedule.getCycle(1);
        if(firstCycle == null)
            return null;
        
        CycleComponentBurst firstBurst = firstCycle.getCPUBurst();
        if(firstBurst != null) {
            return firstBurst.getComponent();
        }
        else {
            firstBurst = firstCycle.getIOBurst();
            return firstBurst.getComponent();
        }
    }
}
