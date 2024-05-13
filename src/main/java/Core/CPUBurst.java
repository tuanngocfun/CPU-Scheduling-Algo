
package Core;

/**
 *
 * @version 1.0
 */
public class CPUBurst extends CycleComponentBurst<CPU> {
    public CPUBurst() {
        super();
    }
    
    public CPUBurst(CPU cpu, int burstTime) {
        super(cpu, burstTime);
    }
}
