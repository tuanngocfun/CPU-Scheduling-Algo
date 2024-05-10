
package Core;

/**
 *
 * @version 1.0
 */
public class Interval implements Comparable<Interval> {
    protected int mStart;
    protected int mEnd;
    
    public Interval() {
        mStart = 0;
        mEnd = 0;
    }
    
    public Interval(int start, int end) throws IllegalArgumentException {
        if(start <= end) {
            mStart = start;
            mEnd = end;
        }
        else {
            throw new IllegalArgumentException(start + " > " + end);
        }
    }
    
    public int getStart() {
        return mStart;
    }
    
    public int getEnd() {
        return mEnd;
    }
    
    public boolean covers(Interval other) {
        return (other != null) ? mStart <= other.mStart && mEnd >= other.mEnd : false;
    }
    
    public boolean overlaps(Interval other) {
        if(other == null)
            return false;
        
        if(mStart < other.mStart) {
            return mEnd > other.mStart;
        }
        else {
            return other.mEnd > mStart;
        }
    }
    
    @Override
    public String toString() {
        return mStart + " - " + mEnd;
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
            Interval other = (Interval) obj;
            return this.mStart == other.mStart && this.mEnd == other.mEnd;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.mStart;
        hash = 97 * hash + this.mEnd;
        return hash;
    }
    
    @Override
    public int compareTo(Interval other) {
        if(mStart == other.mStart) {
            return (mEnd > other.mEnd) ? 1 :
                   (mEnd < other.mEnd) ? -1 : 0;
        }
        else {
            return (mStart > other.mStart) ? 1 : -1;
        }
    }
}
