
package Core;

import java.util.*;

/**
 *
 * @version 1.0
 */
public class SchedulingInfo {
    protected int mSystemArrivalTime;
    protected int mRQArrivalTime;
    protected final SortedMap<Integer, Cycle> mCycleMap;
    
    public SchedulingInfo() {
        mSystemArrivalTime = mRQArrivalTime = 0;
        mCycleMap = new TreeMap<>();
    }
    
    public SchedulingInfo(int arrivalTime) {
        mSystemArrivalTime = mRQArrivalTime = (arrivalTime >= 0) ? arrivalTime : 0;
        mCycleMap = new TreeMap<>();
    }
    
    public SchedulingInfo(int systemArrivalTime, int RQArrivalTime) {
        mSystemArrivalTime = (systemArrivalTime >= 0 && systemArrivalTime <= RQArrivalTime) ? systemArrivalTime : 0;
        mRQArrivalTime = (RQArrivalTime >= 0) ? RQArrivalTime : 0;
        mCycleMap = new TreeMap<>();
    }
    
    public void setSystemArrivalTime(int systemArrivalTime) {
        if(systemArrivalTime >= 0 && systemArrivalTime <= mRQArrivalTime) {
            mSystemArrivalTime = systemArrivalTime;
        }
    }
    
    public int getSystemArrivalTime() {
        return mSystemArrivalTime;
    }
    
    public void setRQArrivalTime(int RQArrivalTime) {
        if(RQArrivalTime >= 0 && RQArrivalTime >= mSystemArrivalTime) {
            mRQArrivalTime = RQArrivalTime;
        }
    }
    
    public int getRQArrivalTime() {
        return mRQArrivalTime;
    }
    
    public void addCycle(int cycleNo, Cycle cycle) throws IllegalArgumentException {
        if(cycle == null)
            throw new IllegalArgumentException("cycle == null");
        
        mCycleMap.put(cycleNo, cycle);
    }
    
    public void removeCycle(int cycleNo) {
        mCycleMap.put(cycleNo, new Cycle());
    }
    
    public Cycle getCycle(int cycleNo) {
        return mCycleMap.get(cycleNo);
    }
    
    public Integer lastCycleNo() {
        return mCycleMap.lastKey();
    }
    
    public Cycle[] getAllCycles() {
        return mCycleMap.values().toArray(Cycle[]::new);
    }
    
    public boolean isSystemArrived(int timePoint) {
        return mSystemArrivalTime <= timePoint;
    }
    
    public boolean isRQArrived(int timePoint) {
        return mRQArrivalTime <= timePoint;
    }
    
    public CycleComponent firstArrivedComponent() {
        Cycle firstCycle = mCycleMap.get(1);
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


