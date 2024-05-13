
package Core;

/**
 *
 * @version 1.0
 */
public class CPU extends CycleComponent {
    private static final String DEFAULT_NAME = "CPU";
    
    public CPU() {
        super(DEFAULT_NAME);
    }
    
    public CPU(GanttChart chart) {
        super(DEFAULT_NAME, chart);
    }
    
    public CPU(String name, GanttChart chart) {
        super(name, chart);
    }
}
