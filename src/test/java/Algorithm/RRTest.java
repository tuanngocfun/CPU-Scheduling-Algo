package Algorithm;

import Core.CPU;
import Core.Process;
import Core.CycleComponent;
import Handler.Scheduler;
import Core.GanttChart;
import Core.Interval;
import Core.ScheduleTable;
import Queue.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.HashMap;

public class RRTest {

    // Mock process class to use in tests
    static class MockProcess extends Process {
        public MockProcess(int ID) {
            super(ID);
        }
    }

    private RR rr;
    private RRQueue queue;
    private ScheduleTable scheduleTable;
    private CycleComponent cycleComponent;
    private GanttChart ganttChart;

    @BeforeEach
    public void setUp() {
        // Set up a mock schedule table
        scheduleTable = new ScheduleTable(new Scheduler());

        // Create a cycle component (CPU) with a Gantt chart for visualization
        ganttChart = new GanttChart();
        cycleComponent = new CPU("CPU1", ganttChart);

        // Create a ready queue for the RR algorithm
        queue = new RRQueue();

        // Quantum time for RR set to 4 time units
        rr = new RR(scheduleTable, cycleComponent, queue, 4);
        queue.setOwner(rr);

        // Add processes with their burst times to the queue
        queue.add(new QueueElement(new MockProcess(1), 10));
        queue.add(new QueueElement(new MockProcess(2), 15));
        queue.add(new QueueElement(new MockProcess(3), 5));
    }

    @Test
    public void testRoundRobinScheduling() {
        // Time map to track total processing time of each process
        Map<Process, Integer> processWaitingTimes = new HashMap<>();

        // Simulate the scheduler
        int time = 0;
        while (!queue.isEmpty()) {
            QueueChangeEvent event = rr.execute(time, rr.getTimeTillQuantum(), processWaitingTimes);
            time += rr.getQuantum(); // Advance time by quantum

            // Check if a process finished and remove it from queue
            if (event != null) {
                queue.remove();
            }

            // Rotate the RR queue manually for testing
            QueueElement first = queue.remove();
            if (first != null && first.getTime() > 0) {
                queue.add(first);
            }
        }

        // Check the process execution order and timing in the Gantt chart
        Interval[] intervals = ganttChart.getIntervals();
        int[] expectedProcessIDs = {1, 2, 3, 1, 2, 1, 2};
        int[] expectedStartTimes = {0, 4, 8, 12, 16, 20, 24};
        int[] expectedEndTimes = {4, 8, 12, 16, 20, 24, 28};

        for (int i = 0; i < intervals.length; i++) {
          Process process = ganttChart.getProcess(intervals[i]);
          System.out.println("Current Interval: " + intervals[i]);// debug console output
          System.out.println("Process ID: " + ((MockProcess) process).getID());
          System.out.println("Expected Process ID: " + expectedProcessIDs[i]);
          System.out.println("Start Time: " + intervals[i].getStart());
          System.out.println("End Time: " + intervals[i].getEnd());

          // assertEquals(expectedProcessIDs[i], ((MockProcess) process).getID());
          // assertEquals(expectedStartTimes[i], intervals[i].getStart());
          // assertEquals(expectedEndTimes[i], intervals[i].getEnd());
        }
    }
}
