
package Core;

import java.util.UnknownFormatConversionException;

/**
 *
 * @version 1.0
 */
public class Process {
    private static final String PROCESS_REGEX = "^(?i)P ?\\d$";
    protected final int mID;
    
    public Process(int ID) {
        mID = ID;
    }
    
    public int getID() {
        return mID;
    }
    
    // This method looks buggy
    public static Process parseProcess(String toParse) throws UnknownFormatConversionException {
        if(toParse.matches(PROCESS_REGEX)) {
            return new Process(toParse.charAt(toParse.length() - 1));
        }
        else {
            throw new UnknownFormatConversionException(toParse);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        else if(obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        else {
            Process other = (Process) obj;
            return this.mID == other.mID;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + this.mID;
        return hash;
    }
    
    @Override
    public String toString() {
        return "P" + mID;
    }
}
