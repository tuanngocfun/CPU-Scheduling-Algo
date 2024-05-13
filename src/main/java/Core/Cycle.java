
package Core;

/**
 *
 * @version 1.0
 */
public class Cycle {
    private CPUBurst mCPUBurst;
    private IOBurst mIOBurst;
    
    public Cycle() {
        mCPUBurst = null;
        mIOBurst = null;
    }
    
    public Cycle(CPUBurst cpuBurst, IOBurst ioBurst) {
        mCPUBurst = cpuBurst;
        mIOBurst = ioBurst;
    }
    
    public void setCPUBurst(CPUBurst cpuBurst) {
        mCPUBurst = cpuBurst;
    }
    
    public CPUBurst getCPUBurst() {
        return mCPUBurst;
    }
    
    public void setIOBurst(IOBurst ioBurst) {
        mIOBurst = ioBurst;
    }
    
    public IOBurst getIOBurst() {
        return mIOBurst;
    }
}
