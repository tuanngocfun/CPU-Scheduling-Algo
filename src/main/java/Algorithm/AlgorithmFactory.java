
package Algorithm;

import Core.ScheduleTable;
import Core.CycleComponent;
import Queue.*;

/**
 *
 * @version 1.0
 */
public final class AlgorithmFactory {
    private AlgorithmFactory() {}
    
    public static SchedulingAlgorithm getAlgorithm(String algorithm, ScheduleTable table, CycleComponent component) {
        if(algorithm == null || component == null || table == null)
            return null;
        
        if(algorithm.matches(FCFS.FIFO_REGEX)) {
            return new FCFS(table, component, new StandardQueue());
        }
        else if(algorithm.matches(SJF.SJF_REGEX)) {
            return new SJF(table, component, new BlockingSortedQueue());
        }
        else if(algorithm.matches(SRTF.SRTF_REGEX)) {
            return new SRTF(table, component, new SortedQueue());
        }
        else if(algorithm.matches(RR.RR_REGEX)) {
            String charRegex = "[^\\d]+";
            String[] numberTokens = algorithm.replaceAll(charRegex, " ").trim().split(" ");
            int quantum = Integer.parseInt(numberTokens[0]);
            
            RRQueue queue = new RRQueue();
            RR rr = new RR(table, component, queue, quantum);
            queue.setOwner(rr);
            return rr;
        }
        else {
            return null;
        }
    }
}
