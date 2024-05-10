package Core;

import java.util.UnknownFormatConversionException;

/**
 * Represents a process with a unique ID.
 *
 * @version 1.0
 */
public class Process {
    // Matches 'P' or 'p' followed by one or more digits, optionally surrounded by spaces
    private static final String PROCESS_REGEX = "^(?i)P ?\\d+$";
    protected final int mID;

    public Process(int ID) {
        mID = ID;
    }

    public int getID() {
        return mID;
    }

    /**
     * Parses a string to create a Process object.
     * The string should be in the format 'P' followed by an integer (e.g., P1, p 23).
     *
     * @param toParse The string to parse.
     * @return A new Process object with the parsed ID.
     * @throws UnknownFormatConversionException If the string format is invalid.
     */
    public static Process parseProcess(String toParse) throws UnknownFormatConversionException {
        if (toParse.matches(PROCESS_REGEX)) {
            // Extract the part after 'P' or 'p', trim any spaces, and parse it as an integer
            String numericPart = toParse.substring(1).trim();
            int processId;
            try {
                processId = Integer.parseInt(numericPart);
            } catch (NumberFormatException e) {
                throw new UnknownFormatConversionException("Invalid process ID format: " + toParse);
            }
            return new Process(processId);
        } else {
            throw new UnknownFormatConversionException("Invalid format: " + toParse);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
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
