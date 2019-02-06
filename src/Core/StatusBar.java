package Core;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JTextField;

public class StatusBar extends JComponent {

    private JTextField _textField;

    public StatusBar() {
        super();

        this._textField = new JTextField("For Help, click Help Topics on the Help Menu.");
        this._textField.setEnabled(false);

        this.add(this._textField, BorderLayout.CENTER);
    }

    public void UpdateText(String statusText) {
        this._textField.setText(statusText);
    }
}
