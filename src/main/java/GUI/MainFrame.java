
package GUI;

import GUI.Component.*;
import Handler.GUIDataHandler;
import Util.GUIProcessedData;
import Algorithm.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @version 1.0
 */
public class MainFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1;
    
    // Custom Variables
    public static final String TITLE = "CPU Scheduling Computing Program";
    private static final String TABLECONFIG_PANEL_CARDNAME = "tableConfigPanel";
    private static final String TABLEINPUT_PANEL_CARDNAME = "tableInputPanel";
    private static final String RESULT_PANEL_CARDNAME = "resultPanel";
    
    private GUIDataHandler mGUIDataHandler;
    private Map<String, String> mComboBoxAlgorithmMap;
    private ResultPanel mResultPanel;
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        initCustomVars();
        preset();
    }

    // Public or package-private getters for testing
    public Map<String, String> getComboBoxAlgorithmMap() {
        return mComboBoxAlgorithmMap;
    }

    public JComboBox<String> getTableConfigCPUAlgoComboBox() {
        return tableConfigCPUAlgoComboBox;
    }

    public JSpinner getTableConfigCPUAlgoRRSpinner() {
        return tableConfigCPUAlgoRRSpinner;
    }

    // Public or package-private setters for testing
    public void setComboBoxAlgorithmMap(Map<String, String> comboBoxAlgorithmMap) {
        this.mComboBoxAlgorithmMap = comboBoxAlgorithmMap;
    }

    public void setTableConfigCPUAlgoComboBox(JComboBox<String> comboBox) {
        this.tableConfigCPUAlgoComboBox = comboBox;
    }

    public void setTableConfigCPUAlgoRRSpinner(JSpinner spinner) {
        this.tableConfigCPUAlgoRRSpinner = spinner;
    }

    private void initCustomVars() {
        mGUIDataHandler = new GUIDataHandler();
        mComboBoxAlgorithmMap = new LinkedHashMap<>();
        
        // Create entries for the combo box - algorithm map and put them to the combo boxes
        mComboBoxAlgorithmMap.put("FCFS (FIFO)", FCFS.class.getSimpleName());
        mComboBoxAlgorithmMap.put("SJF (non-preemptive SJF)", SJF.class.getSimpleName());
        mComboBoxAlgorithmMap.put("SRTF (preemptive SJF)", SRTF.class.getSimpleName());
        mComboBoxAlgorithmMap.put("Round Robin (RR)", RR.class.getSimpleName());
        mComboBoxAlgorithmMap.entrySet().stream().map(entry -> entry.getKey()).map(comboBoxItem -> {
            tableConfigCPUAlgoComboBox.addItem(comboBoxItem);
            return comboBoxItem;
        }).forEachOrdered(comboBoxItem -> {
            tableConfigResourceAlgoComboBox.addItem(comboBoxItem);
        });
        
        mResultPanel = new ResultPanel(this);
        backgroundPanel.add(mResultPanel, RESULT_PANEL_CARDNAME);
    }
    
    private void preset() {
        // Set title
        this.setTitle(TITLE);
        
        // Center Frame
        this.setLocationRelativeTo(null);
        
        // Center table headers
        ((DefaultTableCellRenderer)tableInputBodyTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        // Hide input components for RR algo since the initial chosen algorithm is FCFS
        hideCPURRAlgoInputComponents();
        hideResourceRRAlgoInputComponents();
    }
    
    private void closing() {
        WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
    }
    
    private void hideCPURRAlgoInputComponents() {
        tableConfigCPUAlgoRRLabel.setVisible(false);
        tableConfigCPUAlgoRRSpinner.setVisible(false);
    }
    
    private void hideResourceRRAlgoInputComponents() {
        tableConfigResourceAlgoRRLabel.setVisible(false);
        tableConfigResourceAlgoRRSpinner.setVisible(false);
    }
    
    private void showCPURRAlgoInputComponents() {
        tableConfigCPUAlgoRRLabel.setVisible(true);
        tableConfigCPUAlgoRRSpinner.setVisible(true);
    }
    
    private void showResourceRRAlgoInputComponents() {
        tableConfigResourceAlgoRRLabel.setVisible(true);
        tableConfigResourceAlgoRRSpinner.setVisible(true);
    }
    
    private void setTableRowCount(int rowCount) {
        ScheduleTableModel tableModel = (ScheduleTableModel) tableInputBodyTable.getModel();
        
        int oldRowCount = tableModel.getRowCount();
        tableModel.setRowCount(rowCount);
        
        if(oldRowCount < rowCount) {
            for(int i = oldRowCount; i < rowCount; i++) {
                tableModel.setValueAt("P" + (i + 1), i, tableModel.getProcessColumnIndex());
                tableModel.setValueAt("0", i, tableModel.getSystemArrivalTimeColumnIndex());
                tableModel.setValueAt("0", i, tableModel.getRQArrivalTimeColumnIndex());
                // Set initial value for CPU Burst
                for(int j = 1; j <= tableModel.getCycleCount(); j++) {
                    tableModel.setValueAt("0", i, tableModel.getCPUBurstColumnIndex(j));
                }
            }
        }
        
        resizeRowHeight();
    }
    
    private void setTableCycleCount(int cycleCount) {
        ScheduleTableModel tableModel = (ScheduleTableModel) tableInputBodyTable.getModel();
        
        // For each cycle, we add 2 new columns, one for CPU Burst and one for IO Burst
        int oldCycleCount = tableModel.getCycleCount();
        
        if(oldCycleCount < cycleCount) {
            for(int i = oldCycleCount; i < cycleCount; i++) {
                tableModel.addColumn("CPU Burst " + (i + 1));
                tableModel.addColumn("IO Burst " + (i + 1));
                // Set initial value for CPU Burst
                for(int j = 0; j < tableModel.getRowCount(); j++) {
                    tableModel.setValueAt("0", j, tableModel.getCPUBurstColumnIndex(i + 1));
                }
            }
        }
        else if(oldCycleCount > cycleCount) {
            tableModel.setColumnCount(2 * cycleCount + tableModel.getFixedColumnCount());
        }
        
        attachCellEditor();
        resizeColumnWidth();
    }
    
    private void attachCellEditor() {
        ScheduleTableModel tableModel = (ScheduleTableModel) tableInputBodyTable.getModel();
        TableColumnModel columnModel = tableInputBodyTable.getColumnModel();
        TableColumn systemArrivalTimeColumn = columnModel.getColumn(tableModel.getSystemArrivalTimeColumnIndex());
        TableColumn RQArrivalTimeColumn = columnModel.getColumn(tableModel.getRQArrivalTimeColumnIndex());
        systemArrivalTimeColumn.setCellEditor(new ScheduleTableCellEditor(new PositiveIntegerVerifier()));
        RQArrivalTimeColumn.setCellEditor(new ScheduleTableCellEditor(new PositiveIntegerVerifier()));
        
        for(int i = 1; i <= tableModel.getCycleCount(); i++) {
            TableColumn cpuBurstColumn = columnModel.getColumn(tableModel.getCPUBurstColumnIndex(i));
            TableColumn ioBurstColumn = columnModel.getColumn(tableModel.getIOBurstColumnIndex(i));
            cpuBurstColumn.setCellEditor(new ScheduleTableCellEditor(new PositiveIntegerVerifier()));
            ioBurstColumn.setCellEditor(new ScheduleTableCellEditor(new IOBurstVerifier()));
        }
    }
    
    private void resizeColumnWidth() {
        final int COLUMN_WIDTH = 150;
        final int FIRST_COLUMN_WIDTH = 70;
        TableColumnModel columnModel = tableInputBodyTable.getColumnModel();
        
        for(int column = 0; column < tableInputBodyTable.getColumnCount(); column++) {
            if(column == 0) {
                columnModel.getColumn(column).setPreferredWidth(FIRST_COLUMN_WIDTH);
            }
            else {
               columnModel.getColumn(column).setPreferredWidth(COLUMN_WIDTH); 
            }
        }
    }
    
    private void resizeRowHeight() {
        final int ROW_HEIGHT = 50;
        tableInputBodyTable.setRowHeight(ROW_HEIGHT);
    }
    
    private void selectCellAndScrollTableToCell(int row, int column) {
        tableInputBodyTable.setRowSelectionInterval(row, row);
        tableInputBodyTable.setColumnSelectionInterval(column, column);
        tableInputBodyTable.scrollRectToVisible(tableInputBodyTable.getCellRect(row, column, true));
    }
    
    private boolean tryStopTableEditing() {
        int editedColumn = tableInputBodyTable.getEditingColumn();
        var cellEditor = (ScheduleTableCellEditor) tableInputBodyTable.getColumnModel().getColumn(editedColumn).getCellEditor();
        return cellEditor.stopCellEditing();
    }
    
    private String getInputCPUAlgo() {
        String cpuAlgo = mComboBoxAlgorithmMap.get((String) tableConfigCPUAlgoComboBox.getSelectedItem());
        if("RR".equals(cpuAlgo)) {
            int q = (Integer) tableConfigCPUAlgoRRSpinner.getValue();
            cpuAlgo = cpuAlgo + " (q = " + q + ")";
        }
        
        return cpuAlgo;
    }
    
    private String getInputResourceAlgo() {
        String resourceAlgo = mComboBoxAlgorithmMap.get((String) tableConfigResourceAlgoComboBox.getSelectedItem());
        if("RR".equals(resourceAlgo)) {
            int q = (Integer) tableConfigResourceAlgoRRSpinner.getValue();
            resourceAlgo = resourceAlgo + " (q = " + q + ")";
        }
        
        return resourceAlgo;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backgroundPanel = new javax.swing.JPanel();
        tableConfigPanel = new javax.swing.JPanel();
        tableConfigFooterPanel = new javax.swing.JPanel();
        tableConfigBackButton = new javax.swing.JButton();
        tableConfigNextButton = new javax.swing.JButton();
        tableConfigBodyPanel = new javax.swing.JPanel();
        tableConfigNoOfProcessesLabel = new javax.swing.JLabel();
        tableConfigNoOfCyclesLabel = new javax.swing.JLabel();
        tableConfigCPUAlgoLabel = new javax.swing.JLabel();
        tableConfigResourceAlgoLabel = new javax.swing.JLabel();
        tableConfigNoOfProcessesSpinner = new javax.swing.JSpinner();
        tableConfigNoOfCyclesSpinner = new javax.swing.JSpinner();
        tableConfigCPUAlgoComboBox = new javax.swing.JComboBox<>();
        tableConfigResourceAlgoComboBox = new javax.swing.JComboBox<>();
        tableConfigResourceAlgoRRLabel = new javax.swing.JLabel();
        tableConfigResourceAlgoRRSpinner = new javax.swing.JSpinner();
        tableConfigCPUAlgoRRLabel = new javax.swing.JLabel();
        tableConfigCPUAlgoRRSpinner = new javax.swing.JSpinner();
        tableInputPanel = new javax.swing.JPanel();
        tableInputFooterPanel = new javax.swing.JPanel();
        tableInputBackButton = new javax.swing.JButton();
        tableInputProcessButton = new javax.swing.JButton();
        tableInputBodyPanel = new javax.swing.JPanel();
        tableInputBodyScrollPane = new javax.swing.JScrollPane();
        tableInputBodyTable = new javax.swing.JTable();
        tableInputBodyIOBurstFormatExampleLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        backgroundPanel.setLayout(new java.awt.CardLayout());

        tableConfigBackButton.setText("Exit");
        tableConfigBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableConfigBackButtonActionPerformed(evt);
            }
        });

        tableConfigNextButton.setText("Next");
        tableConfigNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableConfigNextButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tableConfigFooterPanelLayout = new javax.swing.GroupLayout(tableConfigFooterPanel);
        tableConfigFooterPanel.setLayout(tableConfigFooterPanelLayout);
        tableConfigFooterPanelLayout.setHorizontalGroup(
            tableConfigFooterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableConfigFooterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableConfigBackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tableConfigNextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tableConfigFooterPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {tableConfigBackButton, tableConfigNextButton});

        tableConfigFooterPanelLayout.setVerticalGroup(
            tableConfigFooterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableConfigFooterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tableConfigFooterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableConfigBackButton)
                    .addComponent(tableConfigNextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tableConfigFooterPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {tableConfigBackButton, tableConfigNextButton});

        tableConfigNoOfProcessesLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        tableConfigNoOfProcessesLabel.setText("Number of processes:");

        tableConfigNoOfCyclesLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        tableConfigNoOfCyclesLabel.setText("Number of cycles:");

        tableConfigCPUAlgoLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        tableConfigCPUAlgoLabel.setText("CPU Scheduling Algorithm:");

        tableConfigResourceAlgoLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        tableConfigResourceAlgoLabel.setText("Resource Scheduling Algorithm:");

        tableConfigNoOfProcessesSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        tableConfigNoOfCyclesSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        tableConfigCPUAlgoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableConfigCPUAlgoComboBoxActionPerformed(evt);
            }
        });

        tableConfigResourceAlgoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableConfigResourceAlgoComboBoxActionPerformed(evt);
            }
        });

        tableConfigResourceAlgoRRLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        tableConfigResourceAlgoRRLabel.setText("q =");

        tableConfigResourceAlgoRRSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        tableConfigCPUAlgoRRLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        tableConfigCPUAlgoRRLabel.setText("q =");

        tableConfigCPUAlgoRRSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        javax.swing.GroupLayout tableConfigBodyPanelLayout = new javax.swing.GroupLayout(tableConfigBodyPanel);
        tableConfigBodyPanel.setLayout(tableConfigBodyPanelLayout);
        tableConfigBodyPanelLayout.setHorizontalGroup(
            tableConfigBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableConfigBodyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tableConfigBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tableConfigCPUAlgoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                    .addComponent(tableConfigNoOfProcessesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                    .addComponent(tableConfigNoOfCyclesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tableConfigResourceAlgoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tableConfigBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tableConfigBodyPanelLayout.createSequentialGroup()
                        .addGroup(tableConfigBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tableConfigCPUAlgoComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tableConfigResourceAlgoComboBox, 0, 180, Short.MAX_VALUE))
                        .addGap(36, 36, 36)
                        .addGroup(tableConfigBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tableConfigResourceAlgoRRLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tableConfigCPUAlgoRRLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tableConfigBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tableConfigCPUAlgoRRSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tableConfigResourceAlgoRRSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(tableConfigNoOfCyclesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tableConfigNoOfProcessesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(570, Short.MAX_VALUE))
        );

        tableConfigBodyPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {tableConfigCPUAlgoLabel, tableConfigNoOfCyclesLabel, tableConfigNoOfProcessesLabel, tableConfigResourceAlgoLabel});

        tableConfigBodyPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {tableConfigNoOfCyclesSpinner, tableConfigNoOfProcessesSpinner});

        tableConfigBodyPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {tableConfigCPUAlgoRRSpinner, tableConfigResourceAlgoRRSpinner});

        tableConfigBodyPanelLayout.setVerticalGroup(
            tableConfigBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableConfigBodyPanelLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(tableConfigBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableConfigNoOfProcessesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tableConfigNoOfProcessesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53)
                .addGroup(tableConfigBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableConfigNoOfCyclesLabel)
                    .addComponent(tableConfigNoOfCyclesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(55, 55, 55)
                .addGroup(tableConfigBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableConfigCPUAlgoLabel)
                    .addComponent(tableConfigCPUAlgoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tableConfigCPUAlgoRRLabel)
                    .addComponent(tableConfigCPUAlgoRRSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addGroup(tableConfigBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableConfigResourceAlgoLabel)
                    .addComponent(tableConfigResourceAlgoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tableConfigResourceAlgoRRLabel)
                    .addComponent(tableConfigResourceAlgoRRSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(401, Short.MAX_VALUE))
        );

        tableConfigBodyPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {tableConfigCPUAlgoLabel, tableConfigNoOfCyclesLabel, tableConfigNoOfProcessesLabel, tableConfigResourceAlgoLabel});

        tableConfigBodyPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {tableConfigNoOfCyclesSpinner, tableConfigNoOfProcessesSpinner});

        tableConfigBodyPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {tableConfigCPUAlgoComboBox, tableConfigCPUAlgoRRSpinner, tableConfigResourceAlgoComboBox, tableConfigResourceAlgoRRSpinner});

        javax.swing.GroupLayout tableConfigPanelLayout = new javax.swing.GroupLayout(tableConfigPanel);
        tableConfigPanel.setLayout(tableConfigPanelLayout);
        tableConfigPanelLayout.setHorizontalGroup(
            tableConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tableConfigFooterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tableConfigBodyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tableConfigPanelLayout.setVerticalGroup(
            tableConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tableConfigPanelLayout.createSequentialGroup()
                .addComponent(tableConfigBodyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableConfigFooterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        backgroundPanel.add(tableConfigPanel, "tableConfigPanel");

        tableInputBackButton.setText("Back");
        tableInputBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableInputBackButtonActionPerformed(evt);
            }
        });

        tableInputProcessButton.setText("Process");
        tableInputProcessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tableInputProcessButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tableInputFooterPanelLayout = new javax.swing.GroupLayout(tableInputFooterPanel);
        tableInputFooterPanel.setLayout(tableInputFooterPanelLayout);
        tableInputFooterPanelLayout.setHorizontalGroup(
            tableInputFooterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableInputFooterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableInputBackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 863, Short.MAX_VALUE)
                .addComponent(tableInputProcessButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tableInputFooterPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {tableInputBackButton, tableInputProcessButton});

        tableInputFooterPanelLayout.setVerticalGroup(
            tableInputFooterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableInputFooterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tableInputFooterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tableInputBackButton)
                    .addComponent(tableInputProcessButton, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tableInputFooterPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {tableInputBackButton, tableInputProcessButton});

        tableInputBodyTable.setModel(new ScheduleTableModel());
        tableInputBodyTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableInputBodyTable.setCellSelectionEnabled(true);
        tableInputBodyTable.getTableHeader().setReorderingAllowed(false);
        tableInputBodyScrollPane.setViewportView(tableInputBodyTable);

        tableInputBodyIOBurstFormatExampleLabel.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        tableInputBodyIOBurstFormatExampleLabel.setText("*IO Burst's format: Rn (t). For example: R1 (2), R2 (4)");

        javax.swing.GroupLayout tableInputBodyPanelLayout = new javax.swing.GroupLayout(tableInputBodyPanel);
        tableInputBodyPanel.setLayout(tableInputBodyPanelLayout);
        tableInputBodyPanelLayout.setHorizontalGroup(
            tableInputBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableInputBodyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tableInputBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableInputBodyScrollPane)
                    .addGroup(tableInputBodyPanelLayout.createSequentialGroup()
                        .addComponent(tableInputBodyIOBurstFormatExampleLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        tableInputBodyPanelLayout.setVerticalGroup(
            tableInputBodyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tableInputBodyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableInputBodyScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 666, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableInputBodyIOBurstFormatExampleLabel)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tableInputPanelLayout = new javax.swing.GroupLayout(tableInputPanel);
        tableInputPanel.setLayout(tableInputPanelLayout);
        tableInputPanelLayout.setHorizontalGroup(
            tableInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tableInputFooterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tableInputBodyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tableInputPanelLayout.setVerticalGroup(
            tableInputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tableInputPanelLayout.createSequentialGroup()
                .addComponent(tableInputBodyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableInputFooterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        backgroundPanel.add(tableInputPanel, "tableInputPanel");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableConfigNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableConfigNextButtonActionPerformed
        setTableRowCount((Integer) tableConfigNoOfProcessesSpinner.getValue());
        setTableCycleCount((Integer) tableConfigNoOfCyclesSpinner.getValue());
        showTableInputPanel();
    }//GEN-LAST:event_tableConfigNextButtonActionPerformed

    private void tableInputBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableInputBackButtonActionPerformed
        if(tableInputBodyTable.isEditing() && tryStopTableEditing() == false) {
            return;
        }
        
        showTableConfigPanel();
    }//GEN-LAST:event_tableInputBackButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        close();
    }//GEN-LAST:event_formWindowClosing

    private void tableInputProcessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableInputProcessButtonActionPerformed
        if(tableInputBodyTable.isEditing() && tryStopTableEditing() == false) {
            return;
        }
        
        boolean isValidInput = true;
        int invalidRow = -1;
        ScheduleTableModel tableModel = (ScheduleTableModel) tableInputBodyTable.getModel();
        // Check the table to ensure all system arrival times are lower than their corresponding RQ arrival times
        for(int i = 0; i < tableModel.getRowCount(); i++) {
            String input1 = (String) tableModel.getValueAt(i, tableModel.getSystemArrivalTimeColumnIndex());
            String input2 = (String) tableModel.getValueAt(i, tableModel.getRQArrivalTimeColumnIndex());
            int systemArrivalTime = Integer.parseInt(input1);
            int RQArrivalTime = Integer.parseInt(input2);
            if(systemArrivalTime > RQArrivalTime) {
                isValidInput = false;
                invalidRow = i;
                break;
            }
        }
        
        if(!isValidInput) {
            makeBeepSound();
            
            String message = "Error at Process " + (invalidRow + 1) + ":\n" +
                             "System Arrival Time must not be after RQ Arrival Time";
            String title = "Invalid Input";
            int choice = JOptionPane.showOptionDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION, 
                                                    JOptionPane.ERROR_MESSAGE, null, null, null);
            if(choice == JOptionPane.OK_OPTION) {
                selectCellAndScrollTableToCell(invalidRow, tableModel.getSystemArrivalTimeColumnIndex());
            }
        }
        else {
            GUIInputData input = new GUIInputData(tableModel, getInputCPUAlgo(), getInputResourceAlgo());
            GUIProcessedData processedInput = mGUIDataHandler.handleGUIInput(input);
            if(processedInput != null) {
                showResultPanel();
                mResultPanel.generateGanttChart(processedInput);
            }
            else {
                makeBeepSound();
                String message = "Unexpected Error" + '\n' +
                                 "Something wrong happened!";
                String title = "Error";
                JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_tableInputProcessButtonActionPerformed

    private void tableConfigCPUAlgoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableConfigCPUAlgoComboBoxActionPerformed
        String selectedItem = (String) tableConfigCPUAlgoComboBox.getSelectedItem();
        if("RR".equals(mComboBoxAlgorithmMap.get(selectedItem))) {
            showCPURRAlgoInputComponents();
        }
        else {
            hideCPURRAlgoInputComponents();
        }
    }//GEN-LAST:event_tableConfigCPUAlgoComboBoxActionPerformed

    private void tableConfigResourceAlgoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableConfigResourceAlgoComboBoxActionPerformed
        String selectedItem = (String) tableConfigResourceAlgoComboBox.getSelectedItem();
        if("RR".equals(mComboBoxAlgorithmMap.get(selectedItem))) {
            showResourceRRAlgoInputComponents();
        }
        else {
            hideResourceRRAlgoInputComponents();
        }
    }//GEN-LAST:event_tableConfigResourceAlgoComboBoxActionPerformed

    private void tableConfigBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tableConfigBackButtonActionPerformed
        close();
    }//GEN-LAST:event_tableConfigBackButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JButton tableConfigBackButton;
    private javax.swing.JPanel tableConfigBodyPanel;
    private javax.swing.JComboBox<String> tableConfigCPUAlgoComboBox;
    private javax.swing.JLabel tableConfigCPUAlgoLabel;
    private javax.swing.JLabel tableConfigCPUAlgoRRLabel;
    private javax.swing.JSpinner tableConfigCPUAlgoRRSpinner;
    private javax.swing.JPanel tableConfigFooterPanel;
    private javax.swing.JButton tableConfigNextButton;
    private javax.swing.JLabel tableConfigNoOfCyclesLabel;
    private javax.swing.JSpinner tableConfigNoOfCyclesSpinner;
    private javax.swing.JLabel tableConfigNoOfProcessesLabel;
    private javax.swing.JSpinner tableConfigNoOfProcessesSpinner;
    private javax.swing.JPanel tableConfigPanel;
    private javax.swing.JComboBox<String> tableConfigResourceAlgoComboBox;
    private javax.swing.JLabel tableConfigResourceAlgoLabel;
    private javax.swing.JLabel tableConfigResourceAlgoRRLabel;
    private javax.swing.JSpinner tableConfigResourceAlgoRRSpinner;
    private javax.swing.JButton tableInputBackButton;
    private javax.swing.JLabel tableInputBodyIOBurstFormatExampleLabel;
    private javax.swing.JPanel tableInputBodyPanel;
    private javax.swing.JScrollPane tableInputBodyScrollPane;
    private javax.swing.JTable tableInputBodyTable;
    private javax.swing.JPanel tableInputFooterPanel;
    private javax.swing.JPanel tableInputPanel;
    private javax.swing.JButton tableInputProcessButton;
    // End of variables declaration//GEN-END:variables

    public GUIDataHandler getGUIDataHandler() {
        return mGUIDataHandler;
    }
    
    public ResultPanel getResultPanel() {
        return mResultPanel;
    }
    
    public void showTableConfigPanel() {
        CardLayout layout = (CardLayout) backgroundPanel.getLayout();
        layout.show(backgroundPanel, TABLECONFIG_PANEL_CARDNAME);
    }
    
    public void showTableInputPanel() {
        CardLayout layout = (CardLayout) backgroundPanel.getLayout();
        layout.show(backgroundPanel, TABLEINPUT_PANEL_CARDNAME);
    }
    
    public void showResultPanel() {
        CardLayout layout = (CardLayout) backgroundPanel.getLayout();
        layout.show(backgroundPanel, RESULT_PANEL_CARDNAME);
    }
    
    public void close() {
        closing();
        System.exit(0);
    }
    
    public static void makeBeepSound() {
        Toolkit.getDefaultToolkit().beep();
    }
}
