
package Queue;

import Core.Process;

/**
 *
 * Represents an element in the scheduling queue, associating a process with its required time.
 */
public final class QueueElement {
    private Process mProcess;
    private int mTime;
    
    public QueueElement(Process process, int time) throws IllegalArgumentException {
        if(process == null) throw new IllegalArgumentException("Process cannot be null");
        
        this.mProcess = process;
        this.mTime = time;
    }
    
    public Process getProcess() {
        return mProcess;
    }
    
    public int getTime() {
        return mTime;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        else if(obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        else {
            QueueElement other = (QueueElement) obj;
            return mProcess.equals(other.mProcess) && mTime == other.mTime;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + mProcess.hashCode();
        hash = 67 * hash + this.mTime;
        return hash;
    }
    
    @Override
    public String toString() {
        return mProcess + "(" + mTime + ")";
    }
}
