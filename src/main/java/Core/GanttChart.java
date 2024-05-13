
package Core;

import java.util.*;

/**
 *
 * Represents a Gantt chart, mapping intervals to processes.
 */
public final class GanttChart {
    private final TreeMap<Interval, Process> intervalMap;
    private final Map<Process, Set<Interval>> processMap;
    
    public GanttChart() {
        this.intervalMap = new TreeMap<>();
        this.processMap = new HashMap<>();
    }
    
    public Interval getFirstInterval() {
        return this.intervalMap.firstKey();
    }
    
    public Interval getLastInterval() {
        return this.intervalMap.lastKey();
    }
    
    /**
     * Adds or merges intervals for the given process.
     *
     * @param process The process.
     * @param interval The interval to add.
     */
    public void add(Process process, Interval interval) throws IllegalArgumentException {
        if (process == null || interval == null) {
            throw new IllegalArgumentException("Process and interval must not be null.");
        }

        // Check if we can merge intervals
        Map.Entry<Interval, Process> lastEntry = intervalMap.lastEntry();
        if (lastEntry != null) {
            Interval lastInterval = lastEntry.getKey();
            Process lastProcess = lastEntry.getValue();
            if (process.equals(lastProcess) && lastInterval.getEnd() == interval.getStart()) {
                Interval mergedInterval = new Interval(lastInterval.getStart(), interval.getEnd());
                intervalMap.remove(lastInterval);
                intervalMap.put(mergedInterval, process);

                Set<Interval> intervalSet = processMap.get(process);
                intervalSet.remove(lastInterval);
                intervalSet.add(mergedInterval);
                return;
            }
        }

        intervalMap.put(interval, process);

        Set<Interval> intervalSet = processMap.computeIfAbsent(process, k -> new TreeSet<>());
        intervalSet.add(interval);
    }
    
    // Remove a process and its corresponding interval from the chart
    // Auto unmerge interval if the start of the removed interval is also the end of the next interval (same process)
    public void remove(Process process, Interval interval) throws IllegalArgumentException {
        if(process == null || interval == null)
            throw new IllegalArgumentException();
        
        Map.Entry<Interval, Process> lastEntry = intervalMap.lastEntry();
        if(lastEntry != null) {
            Interval lastInterval = lastEntry.getKey();
            Process lastProcess = lastEntry.getValue();
            if(process.equals(lastProcess) && lastInterval.covers(interval) && lastInterval.getEnd() == interval.getEnd()) {
                Interval newInterval = new Interval(lastInterval.getStart(), interval.getStart());
            
                intervalMap.remove(lastInterval);
                intervalMap.put(newInterval, process);

                Set<Interval> intervalSet = processMap.get(process);
                intervalSet.remove(lastInterval);
                intervalSet.add(newInterval);
                return;
            }
        }
        
        intervalMap.remove(interval);
        
        updateProcessMap(process, interval, null);
    }

    /**
     * Update the process map after changes to intervals
     * @param process the process whose intervals are updated
     * @param oldInterval the interval to remove from the process's set
     * @param newInterval the new interval to add, or null if no new interval
     */
    private void updateProcessMap(Process process, Interval oldInterval, Interval newInterval) {
        Set<Interval> intervals = processMap.get(process);
        if (intervals != null) {
            intervals.remove(oldInterval);

            if (newInterval != null) {
                intervals.add(newInterval);
            }

            if (intervals.isEmpty()) {
                processMap.remove(process);
            }
        }
    }
    
    public Process getProcess(Interval interval) {
        return intervalMap.get(interval);
    }
    
    public Process[] getProcesses() {
        return processMap.keySet().toArray(Process[]::new);
    }
    
    public Interval[] getIntervals(Process process) {
        Set<Interval> intervalSet = processMap.get(process);
        if(intervalSet == null) {
            return null;
        }
        else {
            return intervalSet.toArray(Interval[]::new);
        }
    }
    
    public Interval[] getIntervals() {
        return intervalMap.keySet().toArray(Interval[]::new);
    }
    
    public void draw() {
        intervalMap.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        });
    }
}
