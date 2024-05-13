package Algorithm;

import Core.CPU;
import Core.CPUBurst;
import Core.IOBurst;
import Core.Process;
import Core.Cycle;
import Core.IODevice;
import Core.SchedulingInfo;
import Core.GanttChart;
import Core.ScheduleTable;
import Handler.Scheduler;
import Util.RunResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RRAndFIFOTest {

    private Scheduler scheduler;
    private ScheduleTable scheduleTable;
    private CPU cpu;
    private IODevice r1, r2;

    @BeforeEach
    public void setUp() {
        cpu = new CPU("CPU", new GanttChart());
        r1 = new IODevice("R1", 1, new GanttChart());
        r2 = new IODevice("R2", 2, new GanttChart());
        scheduler = new Scheduler("RR(q=3)", "FCFS"); // Set the quantum value to 3 for the RR algorithm
        scheduleTable = new ScheduleTable(scheduler);

        Process p1 = new Process(1);
        Process p2 = new Process(2);
        Process p3 = new Process(3);

        SchedulingInfo s1 = new SchedulingInfo(0);
        s1.addCycle(1, new Cycle(new CPUBurst(cpu, 5), new IOBurst(r1, 4)));
        s1.addCycle(2, new Cycle(new CPUBurst(cpu, 3), null));

        SchedulingInfo s2 = new SchedulingInfo(3);
        s2.addCycle(1, new Cycle(new CPUBurst(cpu, 2), new IOBurst(r1, 2)));
        s2.addCycle(2, new Cycle(new CPUBurst(cpu, 2), null));

        SchedulingInfo s3 = new SchedulingInfo(3);
        s3.addCycle(1, new Cycle(new CPUBurst(cpu, 3), new IOBurst(r2, 1)));
        s3.addCycle(2, new Cycle(new CPUBurst(cpu, 2), null));

        scheduleTable.setSchedule(p1, s1);
        scheduleTable.setSchedule(p2, s2);
        scheduleTable.setSchedule(p3, s3);

        // Properly set up the scheduler with the CPU and I/O devices
        scheduler.getScheduleTable().setSchedule(p1, s1);
        scheduler.getScheduleTable().setSchedule(p2, s2);
        scheduler.getScheduleTable().setSchedule(p3, s3);

        // Collection<IODevice> ioDevices = Arrays.asList(r1, r2);
        // for (IODevice ioDevice : ioDevices) {
        //     scheduler.getScheduleTable().getScheduler().setup(cpu, ioDevices);  // Try this simulation
        // }
    }

    @Test
    public void testRRandFIFOScheduling() {
        RunResult result = scheduler.run();
        assertNotNull(result);

        System.out.println("Gantt Chart for CPU:");
        cpu.getGanttChart().draw();

        System.out.println("Gantt Chart for R1:");
        r1.getGanttChart().draw();

        System.out.println("Gantt Chart for R2:");
        r2.getGanttChart().draw();

        System.out.println("Average Waiting Time: " + result.getWaitingTime());
        System.out.println("Average Turnaround Time: " + result.getTurnaroundTime());

        // Example Assertions
        assertTrue(result.getWaitingTime() >= 0);
        assertTrue(result.getTurnaroundTime() >= 0);

        // Assert the average waiting time and average turnaround time
        assertEquals(4.333333333333333, result.getWaitingTime(), 0.0001, "Average Waiting Time mismatch");
        assertEquals(13.0, result.getTurnaroundTime(), 0.0001, "Average Turnaround Time mismatch");
    }
}