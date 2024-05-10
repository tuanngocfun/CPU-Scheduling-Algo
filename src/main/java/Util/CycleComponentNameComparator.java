
package Util;

import java.util.Comparator;

/**
 *
 * @version 1.0
 */
public class CycleComponentNameComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        boolean isString1CPU = o1.contains("CPU");
        boolean isString2CPU = o2.contains("CPU");
        if(isString1CPU && isString2CPU) {
            return o1.compareTo(o2);
        }
        else if(isString1CPU && !isString2CPU) {
            return -1;
        }
        else if(!isString1CPU && isString2CPU) {
            return 1;
        }
        else {
            try {
                int ID1 = Integer.parseInt(o1.substring(1));
                int ID2 = Integer.parseInt(o2.substring(1));
                return Integer.valueOf(ID1).compareTo(ID2);
            }
            catch(NumberFormatException e) {
                return o1.compareTo(o2);
            }
        }
    }
}
