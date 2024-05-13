package GUI;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ResultPanelTest {
    private MainFrame frame;
    private ResultPanel resultPanel;

    @Before
    public void setUp() {
        frame = new MainFrame();
        resultPanel = frame.getResultPanel();
    }

    @Test
    public void testInitialChartState() {
        // Assuming getResultPanel() provides access to the chart panel or similar methods are available
        assertNull("Initial chart panel should be null", resultPanel.getChartPanel());
    }

    // Add more tests as needed...
}
