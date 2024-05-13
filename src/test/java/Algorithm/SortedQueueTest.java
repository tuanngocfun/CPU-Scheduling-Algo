package Algorithm;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import Queue.*;
import Core.Process;

public class SortedQueueTest {
    private SortedQueue queue;

    @Before
    public void setUp() {
        queue = new SortedQueue();
    }

    @Test
    public void testQueueInitialization() {
        assertTrue("Queue should be initialized empty", queue.isEmpty());
        assertEquals("Size of an empty queue should be 0", 0, queue.size());
    }

    @Test
    public void testAddAndOrder() {
        Process p1 = new Process(1);
        Process p2 = new Process(2);
        queue.add(new QueueElement(p1, 5));
        queue.add(new QueueElement(p2, 3));

        assertEquals("Queue size should be 2 after adding 2 elements", 2, queue.size());
        assertEquals("Next element should be the one with the smallest time", p2, queue.nextElement().getProcess());
    }

    // Add more tests as needed...
}
