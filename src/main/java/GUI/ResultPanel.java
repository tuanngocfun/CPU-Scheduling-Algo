
package GUI;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.data.gantt.*;
import org.jfree.data.time.*;
import org.jfree.data.category.IntervalCategoryDataset;
import java.math.*;
import java.text.SimpleDateFormat;
import Util.*;
import Core.*;
import Core.Process;
import java.awt.CardLayout;
import java.util.*;

/**
 *
 * @version 1.0
 */
public class ResultPanel extends javax.swing.JPanel {
    // Custom Variables
    private static final String PROGRESS_PANEL_CARDNAME = "progressPanel";
    private static final String CHART_PANEL_CARDNAME = "chartPanel";
    
    private static final String CHART_TITLE = "SCHEDULING GANTT CHART";
    private static final String CATEGORY_TITLE = "COMPONENT";
    private static final String TIME_UNIT = "ms";
    private static final String TIMELINE_TITLE = "TIME" + " (" + TIME_UNIT + ")";
    private static final String RANGE_AXIS_PATTERN = "S";
    private static final int ROUNDED_PLACE = 5;
    
    private MainFrame mMainFrame;
    private ChartPanel mChartPanel;
    private int mMinimumTime;
    private int mMaximumTime;

    /**
     * Creates new form ResultBodyPanel
     * @param mainFrame: the main frame
     */
    public ResultPanel(MainFrame mainFrame) {
        initComponents();
        initCustomVars(mainFrame);
        preset();
    }

    /**
     * Getter for mChartPanel to allow access in tests and other parts of GUI code.
     * @return The current ChartPanel instance.
     */
    public ChartPanel getChartPanel() {
        return this.mChartPanel;
    }

    private void initCustomVars(MainFrame mainFrame) {
        mMainFrame = mainFrame;
        mChartPanel = null;
        mMinimumTime = 0;
        mMaximumTime = 0;
    }
    
    private void preset() {
        showProgressPanel();
    }
    
    private void showProgressPanel() {
        CardLayout layout = (CardLayout) resultBodyChartPanel.getLayout();
        layout.show(resultBodyChartPanel, PROGRESS_PANEL_CARDNAME);
    }
    
    private void showChartPanel() {
        CardLayout layout = (CardLayout) resultBodyChartPanel.getLayout();
        layout.show(resultBodyChartPanel, CHART_PANEL_CARDNAME);
    }
    
    private IntervalCategoryDataset createDataset(GUIProcessedData input) {
        TaskSeriesCollection dataset = new TaskSeriesCollection(){
            @Override
            public void add(TaskSeries series) {
                super.add(series);
                java.util.Collections.sort(getColumnKeys(), new CycleComponentNameComparator());
            }
        };
        
        SortedSet<Integer> startTimes = new TreeSet<>();
        SortedSet<Integer> endTimes = new TreeSet<>();
        Process[] processes = input.getProcesses();
        
        resultBodyProgressBar.setMaximum(processes.length);
        resultBodyProgressBar.setIndeterminate(false);
        resultBodyProgressLabel.setText("Processing...");
        
        for(Process process : processes) {
            TaskSeries taskSeries = new TaskSeries(process.toString());
            RunningInterval runningInterval = input.getRunningInterval(process);
            
            for(CycleComponent component : runningInterval.getComponents()) {
                Interval[] intervals = runningInterval.getIntervals(component);
                if(intervals.length == 0) {
                    continue;
                }
                String currentComponentName = component.getName();
                Interval firstInterval = intervals[0];
                Interval lastInterval = intervals[intervals.length - 1];
                
                startTimes.add(firstInterval.getStart());
                endTimes.add(lastInterval.getEnd());
                
                TimePeriod overallPeriod = new SimpleTimePeriod(firstInterval.getStart(), lastInterval.getEnd());
                Task overallTask = new Task(currentComponentName, overallPeriod);
                
                for(var interval : intervals) {
                    TimePeriod timePeriod = new SimpleTimePeriod(interval.getStart(), interval.getEnd());
                    Task task = new Task(currentComponentName, timePeriod);
                    overallTask.addSubtask(task);
                }
                
                taskSeries.add(overallTask);
            }
            dataset.add(taskSeries);
            resultBodyProgressBar.setValue(resultBodyProgressBar.getValue() + 1);
        }
        
        mMinimumTime = startTimes.first();
        mMaximumTime = endTimes.last() + 1;
        
        return dataset;
    }

    private void clearChart() {
        resultBodyChartPanel.remove(mChartPanel);
        mChartPanel = null;
        mMinimumTime = 0;
        mMaximumTime = 0;
        resultBodyRunResultWTLabel.setText("");
        resultBodyRunResultTALabel.setText("");
    }
    
    private void clearProgress() {
        resultBodyProgressLabel.setText("");
        resultBodyProgressBar.setValue(0);
        resultBodyProgressBar.setMaximum(0);
    }
    
    private void restartProgressBar() {
        resultBodyProgressLabel.setText("Calculating amount of tasks...");
        resultBodyProgressBar.setIndeterminate(true);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        resultFooterPanel = new javax.swing.JPanel();
        resultRestartButton = new javax.swing.JButton();
        resultFinishButton = new javax.swing.JButton();
        resultBodyPanel = new javax.swing.JPanel();
        resultBodyRunResultPanel = new javax.swing.JPanel();
        resultBodyRunResultWTLabel = new javax.swing.JLabel();
        resultBodyRunResultTALabel = new javax.swing.JLabel();
        resultBodyChartPanel = new javax.swing.JPanel();
        resultBodyProgressPanel = new javax.swing.JPanel();
        resultBodyProgressBar = new javax.swing.JProgressBar();
        resultBodyProgressLabel = new javax.swing.JLabel();

        resultRestartButton.setText("Restart");
        resultRestartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultRestartButtonActionPerformed(evt);
            }
        });

        resultFinishButton.setText("Finish");
        resultFinishButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultFinishButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout resultFooterPanelLayout = new javax.swing.GroupLayout(resultFooterPanel);
        resultFooterPanel.setLayout(resultFooterPanelLayout);
        resultFooterPanelLayout.setHorizontalGroup(
            resultFooterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultFooterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resultRestartButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(resultFinishButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        resultFooterPanelLayout.setVerticalGroup(
            resultFooterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultFooterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultFooterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resultRestartButton, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                    .addComponent(resultFinishButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        resultBodyRunResultWTLabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        resultBodyRunResultTALabel.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        javax.swing.GroupLayout resultBodyRunResultPanelLayout = new javax.swing.GroupLayout(resultBodyRunResultPanel);
        resultBodyRunResultPanel.setLayout(resultBodyRunResultPanelLayout);
        resultBodyRunResultPanelLayout.setHorizontalGroup(
            resultBodyRunResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultBodyRunResultPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resultBodyRunResultWTLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(resultBodyRunResultTALabel)
                .addGap(365, 365, 365))
        );

        resultBodyRunResultPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {resultBodyRunResultTALabel, resultBodyRunResultWTLabel});

        resultBodyRunResultPanelLayout.setVerticalGroup(
            resultBodyRunResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultBodyRunResultPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(resultBodyRunResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resultBodyRunResultWTLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resultBodyRunResultTALabel))
                .addContainerGap())
        );

        resultBodyRunResultPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {resultBodyRunResultTALabel, resultBodyRunResultWTLabel});

        resultBodyChartPanel.setLayout(new java.awt.CardLayout());

        resultBodyProgressBar.setStringPainted(true);

        resultBodyProgressLabel.setText("Calculating amount of Tasks...");

        javax.swing.GroupLayout resultBodyProgressPanelLayout = new javax.swing.GroupLayout(resultBodyProgressPanel);
        resultBodyProgressPanel.setLayout(resultBodyProgressPanelLayout);
        resultBodyProgressPanelLayout.setHorizontalGroup(
            resultBodyProgressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resultBodyProgressPanelLayout.createSequentialGroup()
                .addContainerGap(270, Short.MAX_VALUE)
                .addGroup(resultBodyProgressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resultBodyProgressLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resultBodyProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(230, 230, 230))
        );
        resultBodyProgressPanelLayout.setVerticalGroup(
            resultBodyProgressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultBodyProgressPanelLayout.createSequentialGroup()
                .addGap(275, 275, 275)
                .addComponent(resultBodyProgressLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultBodyProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(347, Short.MAX_VALUE))
        );

        resultBodyChartPanel.add(resultBodyProgressPanel, "progressPanel");

        javax.swing.GroupLayout resultBodyPanelLayout = new javax.swing.GroupLayout(resultBodyPanel);
        resultBodyPanel.setLayout(resultBodyPanelLayout);
        resultBodyPanelLayout.setHorizontalGroup(
            resultBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(resultBodyRunResultPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(resultBodyChartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        resultBodyPanelLayout.setVerticalGroup(
            resultBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultBodyPanelLayout.createSequentialGroup()
                .addComponent(resultBodyChartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultBodyRunResultPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(resultFooterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(resultBodyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(resultBodyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultFooterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void resultRestartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultRestartButtonActionPerformed
        mMainFrame.showTableConfigPanel();
        clearChart();
        clearProgress();
        this.showProgressPanel();
    }//GEN-LAST:event_resultRestartButtonActionPerformed

    private void resultFinishButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultFinishButtonActionPerformed
        mMainFrame.close();
    }//GEN-LAST:event_resultFinishButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel resultBodyChartPanel;
    private javax.swing.JPanel resultBodyPanel;
    private javax.swing.JProgressBar resultBodyProgressBar;
    private javax.swing.JLabel resultBodyProgressLabel;
    private javax.swing.JPanel resultBodyProgressPanel;
    private javax.swing.JPanel resultBodyRunResultPanel;
    private javax.swing.JLabel resultBodyRunResultTALabel;
    private javax.swing.JLabel resultBodyRunResultWTLabel;
    private javax.swing.JButton resultFinishButton;
    private javax.swing.JPanel resultFooterPanel;
    private javax.swing.JButton resultRestartButton;
    // End of variables declaration//GEN-END:variables

    private static double round(double value, int places) throws IllegalArgumentException {
        if(places < 0) 
            throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        
        return bd.doubleValue();
    }
    
    public void generateGanttChart(GUIProcessedData input) {
        if(input == null)
            return;
        
        restartProgressBar();
        showProgressPanel();
        
        IntervalCategoryDataset dataset = createDataset(input);
        JFreeChart chart = ChartFactory.createGanttChart(CHART_TITLE, CATEGORY_TITLE, TIMELINE_TITLE, dataset, true, false, false);
        DateAxis axis = (DateAxis) chart.getCategoryPlot().getRangeAxis();
        axis.setDateFormatOverride(new SimpleDateFormat(RANGE_AXIS_PATTERN));
        axis.setMinimumDate(new Date(mMinimumTime));
        axis.setMaximumDate(new Date(mMaximumTime));
        
        mChartPanel = new ChartPanel(chart);
        mChartPanel.setSize(mMainFrame.getWidth() - 16, mMainFrame.getHeight() - 170);
        resultBodyRunResultWTLabel.setText("Average Wating Time: " + 
                                round(input.getRunResult().getWaitingTime(), ROUNDED_PLACE) + " " + TIME_UNIT);
        resultBodyRunResultTALabel.setText("Average Turnaround Time: " + 
                                round(input.getRunResult().getTurnaroundTime(), ROUNDED_PLACE) + " " + TIME_UNIT);
        
        resultBodyChartPanel.add(mChartPanel, CHART_PANEL_CARDNAME);
        showChartPanel();
    }
}
