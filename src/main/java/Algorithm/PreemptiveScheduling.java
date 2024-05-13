
package Algorithm;

import Core.ScheduleTable;
import Core.CycleComponent;
import Queue.*;

/**
 *
 * @version 1.0
 */
public abstract class PreemptiveScheduling extends SchedulingAlgorithm {
    protected PreemptiveScheduling(ScheduleTable table, CycleComponent component, ReadyQueue queue) {
        super(table, component, queue);
    }
}
