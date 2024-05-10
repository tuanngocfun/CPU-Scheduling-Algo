package Util;

import Core.Process;
import java.util.Comparator;

/**
 * Compares two Process objects based on their IDs.
 */
public class ProcessComparator implements Comparator<Process> {
    @Override
    public int compare(Process p1, Process p2) {
        return Integer.compare(p1.getID(), p2.getID());
    }
}
