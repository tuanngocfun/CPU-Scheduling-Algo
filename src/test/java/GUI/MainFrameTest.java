package GUI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import java.lang.reflect.Method;
import java.util.HashMap;

public class MainFrameTest {

    private MainFrame mainFrame;

    @BeforeEach
    void setUp() {
        mainFrame = new MainFrame();

        // Set up the algorithm map with test data
        HashMap<String, String> testMap = new HashMap<>();
        testMap.put("Round Robin (RR)", "RR");
        mainFrame.setComboBoxAlgorithmMap(testMap);

        // Mock the JComboBox and JSpinner if necessary or just configure them
        JComboBox<String> mockComboBox = new JComboBox<>();
        mockComboBox.addItem("Round Robin (RR)");
        mainFrame.setTableConfigCPUAlgoComboBox(mockComboBox);

        JSpinner mockSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        mainFrame.setTableConfigCPUAlgoRRSpinner(mockSpinner);
    }

    @Test
    public void testGetInputCPUAlgo() throws Exception {
        MainFrame mainFrame = new MainFrame();
        
        // Set up the environment, and selecting an item in the comboBox:
        Method setItemMethod = JComboBox.class.getDeclaredMethod("setSelectedItem", Object.class);
        setItemMethod.setAccessible(true);
        setItemMethod.invoke(mainFrame.getTableConfigCPUAlgoComboBox(), "Round Robin (RR)");

        // Set spinner value if needed
        JSpinner spinner = mainFrame.getTableConfigCPUAlgoRRSpinner();
        spinner.setValue(5);  // This assumes to have public access to get the spinner

        // Access the private method
        Method getInputCPUAlgoMethod = MainFrame.class.getDeclaredMethod("getInputCPUAlgo");
        getInputCPUAlgoMethod.setAccessible(true);
        String output = (String) getInputCPUAlgoMethod.invoke(mainFrame);

        // Assert the expected output
        assertEquals("RR (q = 5)", output, "The algorithm output should match the expected value.");
    }
}
