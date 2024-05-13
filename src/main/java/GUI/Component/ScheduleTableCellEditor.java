
package GUI.Component;

import GUI.MainFrame;
import javax.swing.*;

/**
 *
 * @version 1.0
 */
public class ScheduleTableCellEditor extends DefaultCellEditor {
    InputVerifier verifier = null;

    public ScheduleTableCellEditor(InputVerifier verifier) {
        super(new JTextField());
        this.verifier = verifier;
    }
    
    public InputVerifier getInputVerifier() {
        return verifier;
    }

    @Override
    public boolean stopCellEditing() {
        boolean valid = verifier.shouldYieldFocus(editorComponent, null) && super.stopCellEditing();
        if(!valid) {
            MainFrame.makeBeepSound();
        }
        return valid;
    }
}
