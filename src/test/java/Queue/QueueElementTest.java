package Queue;

import static org.junit.Assert.*;
import org.junit.Test;
import Core.Process;

public class QueueElementTest {
    @Test
    public void testQueueElementCreation() {
        Process p = new Process(1);
        QueueElement element = new QueueElement(p, 10);

        assertNotNull("QueueElement should be created", element);
        assertEquals("Process ID should match", 1, element.getProcess().getID());
        assertEquals("Time should match", 10, element.getTime());
    }

    @Test
    public void testQueueElementEquality() {
        Process p1 = new Process(1);
        QueueElement element1 = new QueueElement(p1, 10);
        QueueElement element2 = new QueueElement(p1, 10);

        assertTrue("Elements should be equal", element1.equals(element2));
        assertEquals("Hash codes should be equal", element1.hashCode(), element2.hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testQueueElementWithNullProcess() {
        new QueueElement(null, 10);
    }
}
