package Util;

/**
 * Represents the result of a process execution, holding the waiting time and turnaround time.
 * @version 1.0
 */
public class RunResult {
    private final double waitingTime;
    private final double turnaroundTime;

    /**
     * Initializes a new instance of the RunResult class.
     *
     * @param waitingTime The waiting time of the process.
     * @param turnaroundTime The turnaround time of the process.
     * @throws IllegalArgumentException if either waitingTime or turnaroundTime is negative.
     */
    public RunResult(double waitingTime, double turnaroundTime) {
        if (waitingTime < 0 || turnaroundTime < 0)
            throw new IllegalArgumentException("Waiting time and Turnaround time must be non-negative.");

        this.waitingTime = waitingTime;
        this.turnaroundTime = turnaroundTime;
    }

    /**
     * Returns the waiting time.
     *
     * @return The waiting time.
     */
    public double getWaitingTime() {
        return waitingTime;
    }

    /**
     * Returns the turnaround time.
     *
     * @return The turnaround time.
     */
    public double getTurnaroundTime() {
        return turnaroundTime;
    }
}
