package Queue;

import org.junit.*;
import static org.junit.Assert.*;
import Core.Process;

public class SortedQueueTest extends QueueTests {
    private SortedQueue queue;

    @Before
    public void setUpQueue() {
        super.setUp(); // Call to setup processes from QueueTests
        queue = new SortedQueue();
        queue.add(new QueueElement(p1, 10));
        queue.add(new QueueElement(p3, 5));
        queue.add(new QueueElement(p2, 8));
    }

    @Test
    public void testOrderingByTime() {
        QueueElement first = queue.remove();
        assertEquals(p3, first.getProcess());
        assertEquals(5, first.getTime());

        QueueElement second = queue.remove();
        assertEquals(p2, second.getProcess());
        assertEquals(8, second.getTime());

        QueueElement third = queue.remove();
        assertEquals(p1, third.getProcess());
        assertEquals(10, third.getTime());
    }

    @Test
    public void testOrderingByProcessIDWhenTimesEqual() {
        queue.add(new QueueElement(new Process(4), 5));
        queue.add(new QueueElement(new Process(0), 5));

        QueueElement first = queue.remove();
        assertEquals(0, first.getProcess().getID());

        QueueElement second = queue.remove();
        assertEquals(p3, second.getProcess());

        QueueElement third = queue.remove();
        assertEquals(4, third.getProcess().getID());
    }

    @Test
    public void testAdvance() {
        QueueElement advanced = queue.advance(3);
        assertEquals(p3, advanced.getProcess());
        assertEquals(2, advanced.getTime());

        advanced = queue.advance(2);
        assertNull(queue.tryAdvance(1)); // p3 should be removed now
    }

    @Test
    public void testTimeTillNextProcessFinish() {
        Integer time = queue.timeTillNextProcessFinish();
        assertEquals(Integer.valueOf(5), time);
    }
}
