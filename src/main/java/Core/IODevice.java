
package Core;

/**
 *
 * @version 1.0
 */
public class IODevice extends CycleComponent {
    private final int mID;
    
    public IODevice(String name, int ID) {
        super(name);
        mID = ID;
    }
    
    public IODevice(String name, int ID, GanttChart chart) {
        super(name, chart);
        mID = ID;
    }
    
    public int getID() {
        return mID;
    }
}
