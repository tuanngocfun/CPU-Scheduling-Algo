
package Core;

import java.util.Objects;

/**
 *
 * Represents a component of a cycle in a process.
 */
public abstract class CycleComponent {
    private final String name;
    protected GanttChart chart;
    
    public CycleComponent(String name) {
        this.name = name;
        this.chart = null;
    }
    
    public CycleComponent(String name, GanttChart chart) {
        this.name = name;
        this.chart = chart;
    }
    
    public String getName() {
        return name;
    }
    
    public void setGanttChart(GanttChart chart) {
        this.chart = chart;
    }
    
    public GanttChart getGanttChart() {
        return chart;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CycleComponent other = (CycleComponent) obj;
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.name);
        return hash;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
