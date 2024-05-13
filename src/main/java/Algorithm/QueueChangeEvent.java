
package Algorithm;

import Core.Process;

/**
 *
 * @version 1.0
 */
public class QueueChangeEvent {
    private final Process mProcess;
    
    public QueueChangeEvent(Process process) {
        mProcess = process;
    }
    
    public Process getProcess() {
        return mProcess;
    }
}
