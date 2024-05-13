
package Core;

/**
 *
 * @version 1.0
 */
public class IOBurst extends CycleComponentBurst<IODevice> {
    public IOBurst() {
        super();
    }
    
    public IOBurst(IODevice ioDevice, int burstTime) {
        super(ioDevice, burstTime);
    }
}
