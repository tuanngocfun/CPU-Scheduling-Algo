
package Core;

/**
 *
 * @version 1.0
 * @param <T>: A CycleComponent
 */
public class CycleComponentBurst<T extends CycleComponent> {
    protected T mComponent;
    protected Integer mBurstTime;
    
    public CycleComponentBurst() {
        mComponent = null;
        mBurstTime = null;
    }
    
    public CycleComponentBurst(T component, int burstTime) {
        mComponent = component;
        mBurstTime = burstTime;
    }
    
    public void setComponent(T component) {
        mComponent = component;
    }
    
    public T getComponent() {
        return mComponent;
    }
    
    public void setBurstTime(Integer burstTime) {
        mBurstTime = burstTime;
    }
    
    public Integer getBurstTime() {
        return mBurstTime;
    }
}
