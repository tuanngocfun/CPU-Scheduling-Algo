
package Queue;

import Core.Process;

/**
 * Represents an element in the scheduling queue. This element associates a {@link Process}
 * with its required time in the queue.
 *
 * <p>Two {@code QueueElement} objects are considered equal if they have the same {@link Process}
 * and the same time. This class provides overridden {@code equals} and {@code hashCode} methods
 * to support use in collections that rely on these methods, such as {@link java.util.HashSet} and
 * {@link java.util.HashMap}.</p>
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
    
    /**
     * Compares this object to the specified object. The result is {@code true} if and only if
     * the argument is not {@code null}, is a {@code QueueElement} object, and has the same
     * {@link Process} and time as this object.
     * 
     * @param obj The object to compare this {@code QueueElement} against.
     * @return {@code true} if the given object represents a {@code QueueElement} equivalent to this
     *         element, {@code false} otherwise.
     */
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


    /**
     * Returns a hash code value for this {@code QueueElement}.
     *
     * <p>This method is supported for the benefit of hash tables such as those provided by
     * {@link java.util.HashMap}.</p>
     * 
     * <p>The hash code is computed as:</p>
     * <pre>{@code
     * int hash = 5;
     * hash = 67 * hash + mProcess.hashCode();
     * hash = 67 * hash + mTime;
     * return hash;
     * }</pre>
     * 
     * @return A hash code value for this object.
     */
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
