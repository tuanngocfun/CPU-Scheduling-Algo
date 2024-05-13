
package Util;

import java.util.*;
import Core.Process;

/**
 *
 * @version 1.0
 */
public class GUIProcessedData {
    private final RunResult mRunResult;
    private Map<Process, RunningInterval> mProcessRunningIntervalMap; 
    
    public GUIProcessedData(RunResult runResult, Map<Process, RunningInterval> processRunningIntervalMap)
            throws IllegalArgumentException {
        
        if(runResult == null || processRunningIntervalMap == null)
            throw new IllegalArgumentException();
        
        mRunResult = runResult;
        mProcessRunningIntervalMap = processRunningIntervalMap;
    }
    
    public RunResult getRunResult() {
        return mRunResult;
    }
    
    public Process[] getProcesses() {
        return mProcessRunningIntervalMap.keySet().toArray(Process[]::new);
    }
    
    public RunningInterval getRunningInterval(Process process) {
        return mProcessRunningIntervalMap.get(process);
    }
}
