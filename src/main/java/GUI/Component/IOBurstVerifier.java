
package GUI.Component;

import Core.IODevice;
import Core.IOBurst;
import java.util.UnknownFormatConversionException;
import java.util.regex.*;
import java.awt.Color;
import javax.swing.*;

/**
 *
 * @version 1.0
 */
public class IOBurstVerifier extends InputVerifier {
    public static final String IOBURST_REGEX = "^(?i)(R\\d+) *\\((\\d+)\\)$";
    
    @Override
    public boolean verify(JComponent input) {
        String text = ((JTextField) input).getText().trim();
        return text.isEmpty() || text.matches(IOBURST_REGEX);
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
            String text = sourceTextField.getText().trim().toUpperCase();
            if (!text.isEmpty()) {
                // Extract the ID from the name of the IO device and append it to the end of the name in parentheses
                String[] strings = IOBurstVerifier.parseIOBurstString(text);
                text = strings[0] + " (" + strings[1] + ")";
                sourceTextField.setText(text);
            }
        }
        return valid;
    }
    
    public static IOBurst parseIOBurst(String toParse) throws UnknownFormatConversionException {
        Pattern pattern = Pattern.compile(IOBurstVerifier.IOBURST_REGEX);
        Matcher matcher = pattern.matcher(toParse);
        if(matcher.matches()) {
            String name = matcher.group(1);
            int ID = Integer.parseInt(name.substring(1));
            int burstTime = Integer.parseInt(matcher.group(2));
            return new IOBurst(new IODevice(name, ID), burstTime);
        }
        else {
            throw new UnknownFormatConversionException(toParse);
        }
    }
    
    public static String[] parseIOBurstString(String toParse) throws UnknownFormatConversionException {
        Pattern pattern = Pattern.compile(GUI.Component.IOBurstVerifier.IOBURST_REGEX);
        Matcher matcher = pattern.matcher(toParse);
        if(matcher.matches()) {
            String[] result = {matcher.group(1), matcher.group(2)};
            return result;
        }
        else {
            throw new UnknownFormatConversionException(toParse);
        }
    }
}
