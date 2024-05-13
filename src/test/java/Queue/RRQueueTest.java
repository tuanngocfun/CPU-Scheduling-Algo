package Queue;

import org.junit.*;
import static org.junit.Assert.*;

public class RRQueueTest extends QueueTests {
    private RRQueue queue;

    @Before
    public void setUpQueue() {
        super.setUp(); // Call to setup processes from QueueTests
        queue = new RRQueue(new Algorithm.RR(null, null, null, 4)); // Assuming quantum is 4
        queue.add(new QueueElement(p1, 10));
        queue.add(new QueueElement(p2, 5));
        queue.add(new QueueElement(p3, 8));
    }

    @Test
    public void testAddAndSize() {
        assertEquals(3, queue.size());
    }

    @Test
    public void testRemove() {
        QueueElement removed = queue.remove();
        assertEquals(p1, removed.getProcess());
        assertEquals(2, queue.size());
    }

    @Test
    public void testNextElement() {
        QueueElement next = queue.nextElement();
        assertNotNull(next);
        assertEquals(p1, next.getProcess());
        assertEquals(10, next.getTime());
    }

    @Test
    public void testAdvanceWithinQuantum() {
        QueueElement advanced = queue.advance(3);
        assertEquals(p1, advanced.getProcess());
        assertEquals(7, advanced.getTime());
    }

    @Test
    public void testAdvanceOverQuantum() {
        QueueElement advanced = queue.advance(5);
        assertEquals(p1, advanced.getProcess());
        assertEquals(5, advanced.getTime());

        QueueElement next = queue.nextElement();
        assertEquals(p1, next.getProcess()); 
    }

    @Test
    public void testTimeTillNextProcessFinish() {
        Integer time = queue.timeTillNextProcessFinish();
        assertEquals(Integer.valueOf(17), time); 
    }
}
