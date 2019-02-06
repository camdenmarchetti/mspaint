package Popups;

import Valids.SizeUnit;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ItemEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AttributesDialog extends JDialog {

    private final String _title = "Attributes";
    private final ModalityType _modalityType = ModalityType.APPLICATION_MODAL;

    private int _width = 640;
    private int _height = 480;
    private SizeUnit _units = SizeUnit.Pels;

    private JTextField _heightField, _widthField;

    public AttributesDialog(Window owner) {
        super(owner);

        this.setTitle(this._title);
        this.setModalityType(this._modalityType);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        this.setLayout(new GridBagLayout());
        GridBagConstraints layout = new GridBagConstraints();
        layout.insets = new Insets(12, 12, 11, 5);
        layout.anchor = GridBagConstraints.NORTH;


        JPanel internal = new JPanel();
        internal.setLayout(new GridBagLayout());

        GridBagConstraints internalLayout = new GridBagConstraints();
        internalLayout.insets = new Insets(0, 0, 0, 2);
        internalLayout.anchor = GridBagConstraints.NORTH;

        try {
            internalLayout.gridx = 0;
            internalLayout.gridy = 0;
            internalLayout.gridwidth = 7;
            internalLayout.gridheight = 1;
            internal.add(_createSizeRow(), internalLayout);

            internalLayout.gridx = 7;
            internalLayout.gridwidth = 2;
            internalLayout.gridheight = 2;
            internal.add(_createButtons(), internalLayout);

            internalLayout.gridx = 0;
            internalLayout.gridy = 1;
            internalLayout.gridwidth = 7;
            internalLayout.gridheight = 1;
            internalLayout.fill = GridBagConstraints.BOTH;
            internal.add(_createUnits(), internalLayout);
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }

        layout.gridx = 0;
        layout.gridy = 0;
        this.add(internal, layout);

        this.pack();
        this.setMinimumSize(this.getSize());
        this.setMaximumSize(this.getSize());
    }

    private JPanel _createSizeRow() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 4));

        JLabel widthLabel = new JLabel("Width: ");
        widthLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JTextField widthField = new JTextField();
        widthField.setHorizontalAlignment(SwingConstants.LEFT);
        widthField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (widthField.getText().isEmpty()) {
                    return;
                }

                double value;
                try {
                    value = Double.valueOf(widthField.getText());
                } catch (Exception ex) { return; }
                AttributesDialog.this._width = (int)value;
            }
        });

        JPanel widthPanel = new JPanel();
        widthPanel.setLayout(new GridBagLayout());

        GridBagConstraints widthLayout = new GridBagConstraints();
        widthLayout.anchor = GridBagConstraints.CENTER;
        widthLayout.fill = GridBagConstraints.HORIZONTAL;
        widthLayout.weightx = 2;

        widthPanel.add(widthField, widthLayout);


        JLabel heightLabel = new JLabel("Height: ");
        heightLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JTextField heightField = new JTextField();
        heightField.setHorizontalAlignment(SwingConstants.LEFT);
        heightField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (heightField.getText().isEmpty()) {
                    return;
                }

                double value;
                try {
                    value = Double.valueOf(heightField.getText());
                } catch (Exception ex) { return; }
                AttributesDialog.this._height = (int)value;
            }
        });

        JPanel heightPanel = new JPanel();
        heightPanel.setLayout(new GridBagLayout());

        GridBagConstraints heightLayout = new GridBagConstraints();
        heightLayout.anchor = GridBagConstraints.CENTER;
        heightLayout.fill = GridBagConstraints.HORIZONTAL;
        heightLayout.weightx = 2;

        heightPanel.add(heightField, heightLayout);

        panel.add(widthLabel);
        panel.add(widthPanel);
        panel.add(heightLabel);
        panel.add(heightPanel);

        this._widthField = widthField;
        this._heightField = heightField;

        return panel;
    }

    private JPanel _createButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridBagLayout());

        GridBagConstraints layout = new GridBagConstraints();
        layout.insets = new Insets(3, 3, 2, 2);
        layout.anchor = GridBagConstraints.NORTH;
        layout.fill = GridBagConstraints.HORIZONTAL;
        layout.gridwidth = 1;

        JButton buttonOK = new JButton("OK");
        buttonOK.addActionListener(e -> {
            AttributesDialog.this.firePropertyChange("result", "", AttributesDialog.this._getResult("O"));
            AttributesDialog.this.dispose();
        });

        JButton buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(e -> {
            AttributesDialog.this.firePropertyChange("result", "", AttributesDialog.this._getResult("C"));
            AttributesDialog.this.dispose();
        });

        JButton buttonDefault = new JButton("Default");
        buttonDefault.addActionListener(e -> {
            AttributesDialog.this.firePropertyChange("result", "", AttributesDialog.this._getResult("D"));
            AttributesDialog.this.dispose();
        });

        layout.gridx = 0;
        layout.gridy = 0;
        buttons.add(buttonOK, layout);

        layout.gridy = 1;
        buttons.add(buttonCancel, layout);

        layout.gridy = 2;
        buttons.add(buttonDefault, layout);

        layout.gridy = 3;
        layout.weighty = 2;
        layout.fill = GridBagConstraints.BOTH;
        buttons.add(Box.createGlue(), layout);

        panel.add(buttons);
        return panel;
    }

    private JPanel _createUnits() {
        JPanel panel = new JPanel();
        Border basicEtch = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        panel.setLayout(new GridLayout(1, 3));
        panel.setBorder(BorderFactory.createTitledBorder(basicEtch, "Units"));

        JRadioButton inchesRadio = new JRadioButton("Inches", false);
        inchesRadio.setHorizontalTextPosition(JRadioButton.TRAILING);
        inchesRadio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                AttributesDialog.this._units = SizeUnit.Inches;
            }
        });

        JRadioButton cmRadio = new JRadioButton("Cm", false);
        cmRadio.setHorizontalTextPosition(JRadioButton.TRAILING);
        cmRadio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                AttributesDialog.this._units = SizeUnit.Cm;
            }
        });

        JRadioButton pelsRadio = new JRadioButton("Pels", true);
        pelsRadio.setHorizontalTextPosition(JRadioButton.TRAILING);
        pelsRadio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                AttributesDialog.this._units = SizeUnit.Pels;
            }
        });

        // Group radio buttons
        ButtonGroup stretchGroup = new ButtonGroup();
        stretchGroup.add(inchesRadio);
        stretchGroup.add(cmRadio);
        stretchGroup.add(pelsRadio);

        panel.add(inchesRadio, 0, 0);
        panel.add(cmRadio, 0, 1);
        panel.add(pelsRadio, 0, 2);

        return panel;
    }

    private String _getResult(String code) {
        if (code.equalsIgnoreCase("D")) {
            return "O|640|480|P";
        } else {
            String units = "I";
            if (this._units == SizeUnit.Cm) {
                units = "C";
            } else if (this._units == SizeUnit.Pels) {
                units = "P";
            }

            return String.format("%s|%d|%d|%s", code, this._width, this._height, units);
        }
    }

    public void setSizeValues(Dimension size) {
        this._width = size.width;
        this._widthField.setText("" + size.width);
        this._height = size.height;
        this._heightField.setText("" + size.height);
    }
}
