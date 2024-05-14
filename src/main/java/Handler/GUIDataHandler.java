package Handler;

import Core.Cycle;
import Core.IOBurst;
import Core.CPUBurst;
import Core.SchedulingInfo;
import Core.ScheduleTable;
import java.util.*;
import GUI.Component.*;
import Core.*;
import Util.*;
import Core.Process;

/**
 *
 * @version 1.0
 */
public class GUIDataHandler {
    public GUIProcessedData handleGUIInput(GUIInputData input) {
        if(input == null)
            return null;
        
        ScheduleTableModel tableModel = input.getTableModel();
        Scheduler scheduler = new Scheduler();
        ScheduleTable table = scheduler.getScheduleTable();
        
        CPU cpu = new CPU(new GanttChart());
        Set<CycleComponent> components = new HashSet<>();
        components.add(cpu);
        
        try {
            // Populate the schedule table
            for(int i = 0; i < tableModel.getProcessCount(); i++) {
                SchedulingInfo info = new SchedulingInfo();
                Process process = new Process(i + 1);
                table.setSchedule(process, info);

                // Fill in arrival times
                int RQArrivalTime = Integer.parseInt((String) tableModel.getValueAt(i, tableModel.getRQArrivalTimeColumnIndex()));
                int systemArrivalTime = Integer.parseInt((String) tableModel.getValueAt(i, tableModel.getSystemArrivalTimeColumnIndex()));
                info.setRQArrivalTime(RQArrivalTime);
                info.setSystemArrivalTime(systemArrivalTime);
                
                // Fill in cycles' information
                for(int currentCycle = 1; currentCycle <= tableModel.getCycleCount(); currentCycle++) {
                    int CPUBurstColumnIndex = tableModel.getCPUBurstColumnIndex(currentCycle);
                    int IOBurstColumnIndex = tableModel.getIOBurstColumnIndex(currentCycle);

                    String cpuBurstInput = (String) tableModel.getValueAt(i, CPUBurstColumnIndex);
                    String ioBurstInput = (String) tableModel.getValueAt(i, IOBurstColumnIndex);

                    CPUBurst cpuBurst = (cpuBurstInput != null && !cpuBurstInput.isEmpty()) 
                                    ? new CPUBurst(cpu, Integer.parseInt(cpuBurstInput)) : null;
                    IOBurst ioBurst = (ioBurstInput != null && !ioBurstInput.isEmpty()) 
                                    ? IOBurstVerifier.parseIOBurst(ioBurstInput) : null;

                    // Add the IO Device to the set of component (if any)
                    if(ioBurst != null) {
                        IODevice ioDevice = ioBurst.getComponent();
                        ioDevice.setGanttChart(new GanttChart());
                        components.add(ioDevice);
                    }

                    info.addCycle(currentCycle, new Cycle(cpuBurst, ioBurst));
                }
            }

            scheduler.setCPUAlgorithm(input.getCPUSchedulingAlgorithm());
            scheduler.setResourceAlgorithm(input.getResourceSchedulingAlgorithm());

            RunResult runResult = scheduler.run();
            Map<Process, RunningInterval> processRunningIntervalMap = new LinkedHashMap<>();
            for(var process : table.getProcesses()) {
                RunningInterval runningInterval = new RunningInterval();
                components.forEach(component -> {
                    GanttChart chart = component.getGanttChart();
                    Interval[] intervals = chart.getIntervals(process);
                    if(intervals != null) {
                        for(var interval : intervals) {
                            runningInterval.add(component, interval);
                        }
                    }
                });
                processRunningIntervalMap.put(process, runningInterval);
            }

            return new GUIProcessedData(runResult, processRunningIntervalMap);
        }
        catch(IllegalArgumentException e) {
            return null;
        }
    }
}


// package Handler;

// import java.util.*;
// import java.util.stream.IntStream;

// import GUI.Component.*;
// import Core.CPU;
// import Core.CPUBurst;
// import Core.IOBurst;
// import Core.Process;
// import Core.CycleComponent;
// import Core.Cycle;
// import Core.IODevice; 
// import Core.SchedulingInfo;
// import Core.GanttChart;
// import Core.Interval;
// import Core.ScheduleTable;
// import Util.*;
// /**
//  * Handles the GUI input data and processes it to produce the scheduling results.
//  *
//  * @version 2.0
//  */
// public class GUIDataHandler {
//     private CPU sharedCPU;  // Shared CPU for all CPU bursts
    
//     /**
//      * Processes the GUI input data and returns the processed data.
//      *
//      * @param input The GUI input data.
//      * @return The processed GUI data.
//      */
//     public GUIProcessedData handleGUIInput(GUIInputData input) {
//         if (input == null) {
//             return null;
//         }

//         this.sharedCPU = new CPU(new GanttChart()); // Initialize shared CPU instance

//         try {
//             Scheduler scheduler = setupScheduler(input);
//             Set<CycleComponent> components = setupComponents(scheduler);

//             populateScheduleTable(input.getTableModel(), scheduler.getScheduleTable(), components);

//             scheduler.setCPUAlgorithm(input.getCPUSchedulingAlgorithm());
//             scheduler.setResourceAlgorithm(input.getResourceSchedulingAlgorithm());

//             return generateProcessedData(scheduler, components);
//         } catch (IllegalArgumentException e) {
//             // Log the error or handle it as needed
//             System.err.println("Error processing input: " + e.getMessage());
//             return null;
//         }
//     }

//     private Scheduler setupScheduler(GUIInputData input) {
//         return new Scheduler();
//     }

//     private Set<CycleComponent> setupComponents(Scheduler scheduler) {
//         HashSet<CycleComponent> components = new HashSet<>();
//         components.add(this.sharedCPU);  // Add shared CPU instance
//         return components;
//     }

//     private void populateScheduleTable(ScheduleTableModel tableModel, ScheduleTable scheduleTable, Set<CycleComponent> components) {
//         IntStream.range(0, tableModel.getProcessCount()).forEach(i -> {
//             Process process = new Process(i + 1);
//             SchedulingInfo info = new SchedulingInfo();

//             info.setRQArrivalTime(parseInt((String) tableModel.getValueAt(i, tableModel.getRQArrivalTimeColumnIndex())));
//             info.setSystemArrivalTime(parseInt((String) tableModel.getValueAt(i, tableModel.getSystemArrivalTimeColumnIndex())));

//             scheduleTable.setSchedule(process, info);
//             populateCycles(tableModel, i, info, components);
//         });
//     }

//     private void populateCycles(ScheduleTableModel tableModel, int processIndex, SchedulingInfo info, Set<CycleComponent> components) {
//         IntStream.range(1, tableModel.getCycleCount() + 1).forEach(cycleIndex -> {
//             String cpuBurstInput = (String) tableModel.getValueAt(processIndex, tableModel.getCPUBurstColumnIndex(cycleIndex));
//             String ioBurstInput = (String) tableModel.getValueAt(processIndex, tableModel.getIOBurstColumnIndex(cycleIndex));

//             CPUBurst cpuBurst = createCPUBurst(cpuBurstInput, components);
//             IOBurst ioBurst = createIOBurst(ioBurstInput, components);

//             info.addCycle(cycleIndex, new Cycle(cpuBurst, ioBurst));
//         });
//     }

//     private CPUBurst createCPUBurst(String input, Set<CycleComponent> components) {
//         if (input != null && !input.isEmpty()) {
//             int burstTime = parseInt(input);
//             return new CPUBurst(this.sharedCPU, burstTime);
//         }
//         return null;
//     }

//     private IOBurst createIOBurst(String input, Set<CycleComponent> components) {
//         if (input != null && !input.isEmpty()) {
//             try {
//                 IOBurst ioBurst = IOBurstVerifier.parseIOBurst(input);
//                 IODevice ioDevice = ioBurst.getComponent();
//                 ioDevice.setGanttChart(new GanttChart());  // Ensure IODevice has a GanttChart
//                 components.add(ioDevice);
//                 return ioBurst;
//             } catch (UnknownFormatConversionException e) {
//                 throw new IllegalArgumentException("Invalid IO Burst: " + input);
//             }
//         }
//         return null;
//     }

//     private GUIProcessedData generateProcessedData(Scheduler scheduler, Set<CycleComponent> components) {
//         RunResult runResult = scheduler.run();
//         Map<Process, RunningInterval> processRunningIntervalMap = new LinkedHashMap<>();

//         for (Process process : scheduler.getScheduleTable().getProcesses()) {
//             RunningInterval runningInterval = new RunningInterval();
//             for (CycleComponent component : components) {
//                 Interval[] intervals = component.getGanttChart().getIntervals(process);
//                 if (intervals != null) {
//                     Arrays.stream(intervals).forEach(interval -> runningInterval.add(component, interval));
//                 }
//             }
//             processRunningIntervalMap.put(process, runningInterval);
//         }

//         return new GUIProcessedData(runResult, processRunningIntervalMap);
//     }

//     private int parseInt(String value) {
//         try {
//             return Integer.parseInt(value);
//         } catch (NumberFormatException e) {
//             throw new IllegalArgumentException("Invalid number format: " + value);
//         }
//     }
// }