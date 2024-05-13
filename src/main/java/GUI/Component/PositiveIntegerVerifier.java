
package GUI.Component;

import java.awt.Color;
import javax.swing.*;

/**
 *
 * @version 1.0
 */
public class PositiveIntegerVerifier extends InputVerifier {
    @Override
    public boolean verify(JComponent input) {
        String text = ((JTextField) input).getText().trim();
        if(text.isEmpty()) {
            return true;
        }
        
        try {
            int value = Integer.parseInt(text);
            return value >= 0;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }
    
    @Override
    public boolean shouldYieldFocus(JComponent source, JComponent target) {
        boolean valid = verify(source) && verifyTarget(target);
        if(!valid) {
            source.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
        else {
            source.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            JTextField sourceTextField = (JTextField) source;
            String text = sourceTextField.getText().trim();
            if(text.isEmpty()) {
                sourceTextField.setText("0");
            }
            else {
                int value = Integer.parseInt(text);
                sourceTextField.setText(Integer.toString(value));
            }
        }
        return valid;
    }
}
