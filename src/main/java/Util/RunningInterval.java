
package Util;

import Core.*;
import java.util.*;

/**
 *
 * @version 1.0
 */
public class RunningInterval {
    private final Map<CycleComponent, Set<Interval>> mDataMap;

    public RunningInterval() {
        mDataMap = new HashMap<>();
    }
    
    public void add(CycleComponent component, int startTime, int endTime) {
        add(component, new Interval(startTime, endTime));
    }
    
    public void add(CycleComponent component, Interval interval) throws IllegalArgumentException {
        if(component == null || interval == null)
            throw new IllegalArgumentException();
        
        Set<Interval> intervalList = mDataMap.get(component);
        if(intervalList == null) {
            intervalList = new TreeSet<>();
            intervalList.add(interval);
            mDataMap.put(component, intervalList);
        }
        else {
            intervalList.add(interval);
        }
    }
    
    public void remove(CycleComponent component, Interval interval) {
        if(component == null || interval == null)
            return;
        
        Set<Interval> intervalList = mDataMap.get(component);
        if(intervalList != null) {
            intervalList.remove(interval);
            if(intervalList.isEmpty()) {
                mDataMap.remove(component);
            }
        }
    }
    
    public CycleComponent[] getComponents() {
        return mDataMap.keySet().toArray(CycleComponent[]::new);
    }
    
    public Interval[] getIntervals() {
        List<Interval> resultList = new ArrayList<>();
        mDataMap.values().forEach(set -> resultList.addAll(set));
        return resultList.toArray(Interval[]::new);
    }
    
    public Interval[] getIntervals(CycleComponent component) {
        Set<Interval> intervalList = mDataMap.get(component);
        if(intervalList != null) {
            return intervalList.toArray(Interval[]::new);
        }
        else {
            return null;
        }
    }
}
