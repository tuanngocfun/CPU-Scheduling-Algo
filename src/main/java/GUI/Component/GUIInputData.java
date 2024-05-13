
package GUI.Component;

/**
 *
 * @version 1.0
 */
public class GUIInputData {
    private ScheduleTableModel mTableModel;
    private String mCPUAlgorithm;
    private String mResourceAlgorithm;
    
    public GUIInputData(ScheduleTableModel tableModel) {
        mTableModel = tableModel;
        mCPUAlgorithm = mResourceAlgorithm = null;
    }
    
    public GUIInputData(ScheduleTableModel tableModel, String cpuAlgo, String resourceAlgo) {
        mTableModel = tableModel;
        mCPUAlgorithm = cpuAlgo;
        mResourceAlgorithm = resourceAlgo;
    }
    
    public void setCPUSchedulingAlgorithm(String cpuAlgo) {
        mCPUAlgorithm = cpuAlgo;
    }
    
    public String getCPUSchedulingAlgorithm() {
        return mCPUAlgorithm;
    }
    
    public void setResourceSchedulingAlgorithm(String resourceAlgo) {
        mResourceAlgorithm = resourceAlgo;
    }
    
    public String getResourceSchedulingAlgorithm() {
        return mResourceAlgorithm;
    }
    
    public void setTableModel(ScheduleTableModel tableModel) {
        mTableModel = tableModel;
    }
    
    public ScheduleTableModel getTableModel() {
        return mTableModel;
    }
}
