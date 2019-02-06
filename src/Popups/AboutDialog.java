package Popups;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AboutDialog extends JDialog {

    private final String _title = "About Paint";
    private final ModalityType _modalityType = ModalityType.DOCUMENT_MODAL;

    public AboutDialog(Window owner) {
        super(owner);

        this.setTitle(this._title);
        this.setModalityType(this._modalityType);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTextArea aboutText = new JTextArea();
        aboutText.setEnabled(false);
        aboutText.setLineWrap(true);
        aboutText.setWrapStyleWord(true);
        aboutText.setText("Mega early version of a Java AWT/Swing remake of Microsoft Paint (Windows 95 edition).\n\nAuthor: Camden Marchetti\nComputer Science\nDrexel University 2019");

        Container dialogContainer = this.getContentPane();
        dialogContainer.setLayout(new BorderLayout());
        dialogContainer.add(new JScrollPane(aboutText), BorderLayout.CENTER);

        this.setMinimumSize(new Dimension(300, 180));
        this.pack();
        this.setVisible(true);
    }
}
