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

public class SJFandFIFOTest {

    private Scheduler scheduler;
    private ScheduleTable scheduleTable;
    private CPU cpu;
    private IODevice r1, r2;

    @BeforeEach
    public void setUp() {
        cpu = new CPU("CPU", new GanttChart());
        r1 = new IODevice("R1", 1, new GanttChart());
        r2 = new IODevice("R2", 2, new GanttChart());
        scheduler = new Scheduler("SJF", "FCFS");
        scheduleTable = new ScheduleTable(scheduler);

        Process p1 = new Process(1);
        Process p2 = new Process(2);
        Process p3 = new Process(3);
        Process p4 = new Process(4);

        SchedulingInfo s1 = new SchedulingInfo(0,0);
        s1.addCycle(1, new Cycle(new CPUBurst(cpu, 3), new IOBurst(r1, 2)));
        s1.addCycle(2, new Cycle(new CPUBurst(cpu, 4), null));

        SchedulingInfo s2 = new SchedulingInfo(1,2);
        s2.addCycle(1, new Cycle(new CPUBurst(cpu, 4), new IOBurst(r2, 1)));
        s2.addCycle(2, new Cycle(new CPUBurst(cpu, 1), null));

        SchedulingInfo s3 = new SchedulingInfo(2,4);
        s3.addCycle(1, new Cycle(new CPUBurst(cpu, 2), new IOBurst(r1, 1)));
        s3.addCycle(2, new Cycle(new CPUBurst(cpu, 2), null));

        SchedulingInfo s4 = new SchedulingInfo(3,4);
        s4.addCycle(1, new Cycle(new CPUBurst(cpu, 1), new IOBurst(r2, 2)));
        s4.addCycle(2, new Cycle(new CPUBurst(cpu, 1), null));

        scheduleTable.setSchedule(p1, s1);
        scheduleTable.setSchedule(p2, s2);
        scheduleTable.setSchedule(p3, s3);
        scheduleTable.setSchedule(p4, s4);

        // Properly set up the scheduler with the CPU and I/O devices
        scheduler.getScheduleTable().setSchedule(p1, s1);
        scheduler.getScheduleTable().setSchedule(p2, s2);
        scheduler.getScheduleTable().setSchedule(p3, s3);
        scheduler.getScheduleTable().setSchedule(p4, s4);
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
        assertTrue(result.getWaitingTime() == 4.75);
        assertTrue(result.getTurnaroundTime() == 11.75);
    }
}