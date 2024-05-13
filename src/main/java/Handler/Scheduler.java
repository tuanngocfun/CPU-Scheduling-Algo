
package Handler;

import Core.ScheduleTable;
import Util.RunResult;
import Core.*;
import java.util.*;

/**
 *
 * @version 1.0
 */

public final class Scheduler {
    private final ScheduleTable mTable;
    private final AlgorithmHandler mAlgoHandler;
    private String mCPUAlgo;
    private String mResourceAlgo;
    
    public Scheduler() {
        mTable = new ScheduleTable(this);
        mAlgoHandler = new AlgorithmHandler(this);
        mCPUAlgo = null;
        mResourceAlgo = null;
    }
    
    public Scheduler(String cpuAlgo, String resourceAlgo) {
        mTable = new ScheduleTable(this);
        mAlgoHandler = new AlgorithmHandler(this);
        mCPUAlgo = cpuAlgo.trim();
        mResourceAlgo = resourceAlgo.trim();
    }
    
    public ScheduleTable getScheduleTable() {
        return mTable;
    }
    
    public void setCPUAlgorithm(String cpuAlgo) {
        mCPUAlgo = cpuAlgo.trim();
    }
    
    public String getCPUAlgorithm() {
        return mCPUAlgo;
    }
    
    public void setResourceAlgorithm(String resourceAlgo) {
        mResourceAlgo = resourceAlgo.trim();
    }
    
    public String getResourceAlgorithm() {
        return mResourceAlgo;
    }
    
    public RunResult run() {
        CPU cpu = null;
        Collection<IODevice> ioDevices = new ArrayList<>();
        
        for(var component : mTable.getCycleComponents()) {
            if(component instanceof CPU) {
                cpu = (CPU) component;
            }
            else {
                ioDevices.add((IODevice) component);
            }
        }
        
        if(mAlgoHandler.setup(cpu, ioDevices) == false) {
            return null;
        }
        else {
            return mAlgoHandler.run();
        }
    }
}
