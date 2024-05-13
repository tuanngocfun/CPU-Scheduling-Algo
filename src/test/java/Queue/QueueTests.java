package Queue;

import org.junit.*;
import Core.Process;

public abstract class QueueTests {
    protected Process p1, p2, p3;

    @Before
    public void setUp() {
        p1 = new Process(1);
        p2 = new Process(2);
        p3 = new Process(3);
    }

}
