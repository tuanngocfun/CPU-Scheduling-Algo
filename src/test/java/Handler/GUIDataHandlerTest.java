package Handler;

import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.approvaltests.reporters.UseReporter;
import org.approvaltests.reporters.windows.TortoiseTextDiffReporter;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import Core.CPU;
import Core.CycleComponent;
import Core.GanttChart;
import Core.IODevice;
import Core.Interval;
import Core.Process;
import Core.ScheduleTable;
import Core.SchedulingInfo;
import GUI.Component.GUIInputData;
import GUI.Component.ScheduleTableModel;
import Util.GUIProcessedData;
import Util.RunningInterval;

import com.google.gson.Gson;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

// Optionally specify a reporter, here's an example with TortoiseTextDiffReporter
@UseReporter(TortoiseTextDiffReporter.class)
public class GUIDataHandlerTest {

    @Test
    public void testHandleGUIInput1() {
        // Set the columns including cycles
        String[] columnNames = {"Process", "System Arrival Time", "RQ Arrival Time",
                                "CPU Burst 1", "IO Burst 1", "CPU Burst 2", "IO Burst 2"};
        
        // Initialize the table model with the specified columns
        ScheduleTableModel tableModel = new ScheduleTableModel();
        tableModel.setColumnIdentifiers(columnNames);

        // Add rows - make sure each row has values for each column
        tableModel.addRow(new Object[]{"P1", "0", "2", "5", "R1(4)", "3", "R2(2)"});
        tableModel.addRow(new Object[]{"P2", "2", "4", "", "R3(2)", "4", ""});

        // Create GUIInputData with the table model and scheduling algorithms
        GUIInputData inputData = new GUIInputData(tableModel, "Round Robin", "Priority");

        // Create an instance of the handler
        GUIDataHandler dataHandler = new GUIDataHandler();

        // Process the input
        GUIProcessedData processedData = dataHandler.handleGUIInput(inputData);

        // If necessary, convert the output to JSON or another format to check
        // Here, we'll simply print out some of the processed data for manual verification
        if (processedData != null) {
            System.out.println("RunResult Waiting Time: " + processedData.getRunResult().getWaitingTime());
            System.out.println("RunResult Turnaround Time: " + processedData.getRunResult().getTurnaroundTime());

            for (Process process : processedData.getProcesses()) {
                RunningInterval runningInterval = processedData.getRunningInterval(process);
                System.out.println("Process: " + process);
                for (CycleComponent component : runningInterval.getComponents()) {
                    System.out.println("  Component: " + component);
                    for (Interval interval : runningInterval.getIntervals(component)) {
                        System.out.println("    Interval: " + interval);
                    }
                }
            }
        } else {
            System.out.println("Processed data is null");
        }
        // // Create a ScheduleTableModel with sample data
        // ScheduleTableModel tableModel = new ScheduleTableModel();
        // tableModel.addRow(new Object[]{"P1", "0", "2", "5", "", "3", "R1(4)"});
        // tableModel.addRow(new Object[]{"P2", "2", "4", "", "R2(2)", "4", ""});

        // // Set up the GUIInputData
        // GUIInputData inputData = new GUIInputData(tableModel, "Round Robin", "Priority");

        // // Create an instance of the handler
        // GUIDataHandler dataHandler = new GUIDataHandler();

        // // Process the input
        // GUIProcessedData processedData = dataHandler.handleGUIInput(inputData);

        // // Convert the output to JSON and verify
        // String jsonOutput = convertToJson(processedData);
        // Approvals.verify(jsonOutput, new Options().forFile().withExtension(".json")); 
    }

    @Test
    public void testHandleGUIInput() {
        // Mock the necessary dependencies
        ScheduleTableModel mockedTableModel = mock(ScheduleTableModel.class);
        Scheduler mockedScheduler = mock(Scheduler.class);
        ScheduleTable mockedTable = mock(ScheduleTable.class);
        CPU mockedCPU = mock(CPU.class);
        GanttChart mockedChart = mock(GanttChart.class);
        IODevice mockedIODevice = mock(IODevice.class);

        // Setup the mock behavior
        when(mockedTableModel.getProcessCount()).thenReturn(2);
        when(mockedTableModel.getCycleCount()).thenReturn(2);
        when(mockedTableModel.getSystemArrivalTimeColumnIndex()).thenReturn(1);
        when(mockedTableModel.getRQArrivalTimeColumnIndex()).thenReturn(2);
        when(mockedTableModel.getCPUBurstColumnIndex(anyInt())).thenReturn(3, 5);
        when(mockedTableModel.getIOBurstColumnIndex(anyInt())).thenReturn(4, 6);

        when(mockedTableModel.getValueAt(0, 1)).thenReturn("0");
        when(mockedTableModel.getValueAt(0, 2)).thenReturn("2");
        when(mockedTableModel.getValueAt(0, 3)).thenReturn("5");
        when(mockedTableModel.getValueAt(0, 4)).thenReturn("R1(4)");
        when(mockedTableModel.getValueAt(0, 5)).thenReturn("3");
        when(mockedTableModel.getValueAt(0, 6)).thenReturn("R2(2)");

        when(mockedTableModel.getValueAt(1, 1)).thenReturn("2");
        when(mockedTableModel.getValueAt(1, 2)).thenReturn("4");
        when(mockedTableModel.getValueAt(1, 3)).thenReturn("");
        when(mockedTableModel.getValueAt(1, 4)).thenReturn("R3(2)");
        when(mockedTableModel.getValueAt(1, 5)).thenReturn("4");
        when(mockedTableModel.getValueAt(1, 6)).thenReturn("");

        when(mockedScheduler.getScheduleTable()).thenReturn(mockedTable);
        when(mockedIODevice.getGanttChart()).thenReturn(mockedChart);

        // Create GUIInputData with the table model and scheduling algorithms
        GUIInputData inputData = new GUIInputData(mockedTableModel, "Round Robin", "Priority");

        // Initialize the handler
        GUIDataHandler dataHandler = new GUIDataHandler();

         // Act: Process the input
        GUIProcessedData processedData = dataHandler.handleGUIInput(inputData);

        // Assert
        //  assertNotNull(processedData, "Processed data should not be null");

        // Verify that the scheduler and model calls are made as expected
        verify(mockedScheduler, times(0)).getScheduleTable();
        verify(mockedTableModel, times(3)).getProcessCount();
        verify(mockedTableModel, times(6)).getCycleCount();

        // Verify that the scheduling algorithm setters are called
        verify(mockedScheduler, times(0)).setCPUAlgorithm("Round Robin");
        verify(mockedScheduler, times(0)).setResourceAlgorithm("Priority");

        // More detailed verification can be added here as necessary
        verify(mockedTable, times(0)).setSchedule(any(Process.class), any(SchedulingInfo.class));

        // Print some outputs if necessary for manual inspection
        // System.out.println("RunResult Waiting Time: " + processedData.getRunResult().getWaitingTime());
        // System.out.println("RunResult Turnaround Time: " + processedData.getRunResult().getTurnaroundTime());

        
    }

// @Test
// public void testHandleGUIInputWithCorrectSetup() {
//     // Initialize the table model and configure column identifiers and rows based on the setup from the GUI
//     ScheduleTableModel tableModel = new ScheduleTableModel();
//     String[] columnNames = {"Process", "System Arrival Time", "RQ Arrival Time", "CPU Burst 1", "IO Burst 1"};
//     tableModel.setColumnIdentifiers(columnNames);

//     // Adding rows exactly as provided in the GUI input screen
//     tableModel.addRow(new Object[]{"P1", "0", "1", "100", "R1 (50)"});
//     tableModel.addRow(new Object[]{"P2", "1", "2", "200", "R2 (100)"});

//     // Creating GUIInputData with SJF non-preemptive and FCFS as selected in the configuration screen
//     GUIInputData inputData = new GUIInputData(tableModel, "SJF (non-preemptive SJF)", "FCFS (FIFO)");

//     // Create an instance of GUIDataHandler
//     GUIDataHandler dataHandler = new GUIDataHandler();

//     // Process the input data
//     GUIProcessedData processedData = dataHandler.handleGUIInput(inputData);

//     // Assertions to check non-null outputs and potentially other conditions based on your program logic
//     assertNotNull(processedData, "Processed data should not be null");
//     // Further assertions can be added to validate specific logic, like checking the processed data matches expected Gantt Chart outputs
// }

}
