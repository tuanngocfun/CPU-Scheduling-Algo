package Util;

import Core.CycleComponent;
import Core.Interval;
import java.util.*;

/**
 * Manages intervals during which various cycle components are running.
 */
public class RunningInterval {
    private final Map<CycleComponent, Set<Interval>> dataMap;

    public RunningInterval() {
        this.dataMap = new HashMap<>();
    }

    /**
     * Adds a new interval for the given component.
     *
     * @param component The cycle component.
     * @param startTime The start time of the interval.
     * @param endTime The end time of the interval.
     */
    public void add(CycleComponent component, int startTime, int endTime) {
        add(component, new Interval(startTime, endTime));
    }

    /**
     * Adds the interval to the component's set of intervals.
     *
     * @param component The cycle component.
     * @param interval The interval to add.
     */
    public void add(CycleComponent component, Interval interval) {
        if (component == null || interval == null) {
            throw new IllegalArgumentException("Component and interval must not be null.");
        }

        Set<Interval> intervals = dataMap.computeIfAbsent(component, k -> new TreeSet<>());
        intervals.add(interval);
    }

    /**
     * Removes the specified interval from the component's set of intervals.
     *
     * @param component The cycle component.
     * @param interval The interval to remove.
     */
    public void remove(CycleComponent component, Interval interval) {
        if (component == null || interval == null) {
            return;
        }

        Set<Interval> intervals = dataMap.get(component);
        if (intervals != null) {
            intervals.remove(interval);
            if (intervals.isEmpty()) {
                dataMap.remove(component);
            }
        }
    }

    /**
     * Returns all components.
     *
     * @return An array of all components.
     */
    public CycleComponent[] getComponents() {
        return dataMap.keySet().toArray(new CycleComponent[0]);
    }

    /**
     * Returns all intervals for all components.
     *
     * @return An array of all intervals.
     */
    public Interval[] getIntervals() {
        List<Interval> resultList = new ArrayList<>();
        dataMap.values().forEach(resultList::addAll);
        return resultList.toArray(new Interval[0]);
    }

    /**
     * Returns intervals for the specified component.
     *
     * @param component The cycle component.
     * @return An array of intervals for the component.
     */
    public Interval[] getIntervals(CycleComponent component) {
        Set<Interval> intervals = dataMap.get(component);
        return intervals != null ? intervals.toArray(new Interval[0]) : new Interval[0];
    }
}
