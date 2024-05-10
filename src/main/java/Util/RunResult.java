
package Util;

/**
 *
 * @version 1.0
 */
public class RunResult {
    private final double mWaitingTime;
    private final double mTurnaroundTime;
    
    public RunResult(double waitingTime, double turnaroundTime) throws IllegalArgumentException {
        if(waitingTime < 0 || turnaroundTime < 0)
            throw new IllegalArgumentException();
        
        mWaitingTime = waitingTime;
        mTurnaroundTime = turnaroundTime;
    }
    
    public double getWaitingTime() {
        return mWaitingTime;
    }
    
    public double getTurnaroundTime() {
        return mTurnaroundTime;
    }
}
