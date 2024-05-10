
package Util;

import Core.Process;
import java.util.Comparator;

/**
 *
 * @version 1.0
 */
public class ProcessComparator implements Comparator<Process> {
    @Override
    public int compare(Process p1, Process p2) {
        int ID1 = p1.getID();
        int ID2 = p2.getID();
        return (ID1 > ID2) ? 1 :
               (ID1 < ID2) ? -1 : 0;
    }
}
