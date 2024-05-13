
package GUI.Component;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @version 1.0
 */
public class ScheduleTableModel extends DefaultTableModel {
    private final String[] fixedColumnNames;
    
    public ScheduleTableModel() {
        super(new Object[][]{}, new String[]{"Process", "System Arrival Time", "RQ Arrival Time"});
        this.fixedColumnNames = new String[]{"Process", "System Arrival Time", "RQ Arrival Time"};
    }
    
    public int getProcessColumnIndex() {
        return 0;
    }
    
    public int getSystemArrivalTimeColumnIndex() {
        return 1;
    }
    
    public int getRQArrivalTimeColumnIndex() {
        return 2;
    }
    
    public int getFixedColumnCount() {
        return fixedColumnNames.length;
    }
    
    public int getProcessCount() {
        return this.getRowCount();
    }
    
    public int getCycleCount() {
        return (this.getColumnCount() - getFixedColumnCount()) / 2;
    }
    
    public int getCPUBurstColumnIndex(int cycleNo) {
        return (cycleNo > 0) ? 2 * (cycleNo - 1) + getFixedColumnCount() : -1;
    }
    
    public int getIOBurstColumnIndex(int cycleNo) {
        return (cycleNo > 0) ? 2 * (cycleNo - 1) + getFixedColumnCount() + 1 : -1;
    }
    
    public boolean isCPUBurstColumn(int columnIndex) {
        int columnCount = this.getColumnCount();
        if(columnIndex < getFixedColumnCount() || columnIndex >= columnCount) {
            return false;
        }
        else {
            return (columnIndex - getFixedColumnCount()) % 2 == 0;
        }
    }
    
    public boolean isIOBurstColumn(int columnIndex) {
        int columnCount = this.getColumnCount();
        if(columnIndex < getFixedColumnCount() || columnIndex >= columnCount) {
            return false;
        }
        else {
            return (columnIndex - getFixedColumnCount()) % 2 == 1;
        }
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }
}
