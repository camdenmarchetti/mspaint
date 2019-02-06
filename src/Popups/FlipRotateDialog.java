package Popups;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ItemEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class FlipRotateDialog extends JDialog {

    public static final int FLIP_HORIZONTAL = 1;
    public static final int FLIP_VERTICAL = 2;
    public static final int ROTATE_90 = 3;
    public static final int ROTATE_180 = 4;
    public static final int ROTATE_270 = 5;

    private final String _title = "Flip and Rotate";
    private final ModalityType _modalityType = ModalityType.APPLICATION_MODAL;

    private int _mode = FLIP_HORIZONTAL;

    public FlipRotateDialog(Window owner) {
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
            internalLayout.gridheight = 3;
            internalLayout.fill = GridBagConstraints.BOTH;
            internal.add(_createFlipRotate(), internalLayout);

            internalLayout.gridx = 7;
            internalLayout.gridwidth = 2;
            internalLayout.gridheight = 1;
            internal.add(_createButtons(), internalLayout);
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
            FlipRotateDialog.this.firePropertyChange("result", "", FlipRotateDialog.this._getResult("O"));
            FlipRotateDialog.this.dispose();
        });

        JButton buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(e -> {
            FlipRotateDialog.this.firePropertyChange("result", "", FlipRotateDialog.this._getResult("C"));
            FlipRotateDialog.this.dispose();
        });

        layout.gridx = 0;
        layout.gridy = 0;
        buttons.add(buttonOK, layout);

        layout.gridy = 1;
        buttons.add(buttonCancel, layout);

        layout.gridy = 2;
        layout.weighty = 2;
        layout.fill = GridBagConstraints.BOTH;
        buttons.add(Box.createGlue(), layout);

        panel.add(buttons);
        return panel;
    }

    private JPanel _createFlipRotate() {
        JPanel panel = new JPanel();
        Border basicEtch = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(basicEtch, "Flip or rotate"));

        GridBagConstraints layout = new GridBagConstraints();

        JRadioButton horizFlipRadio = new JRadioButton("Flip horizontal", true);
        horizFlipRadio.setHorizontalTextPosition(JRadioButton.TRAILING);
        horizFlipRadio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                FlipRotateDialog.this._mode = FlipRotateDialog.FLIP_HORIZONTAL;
            }
        });

        JRadioButton vertFlipRadio = new JRadioButton("Flip vertical", false);
        vertFlipRadio.setHorizontalTextPosition(JRadioButton.TRAILING);
        vertFlipRadio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                FlipRotateDialog.this._mode = FlipRotateDialog.FLIP_VERTICAL;
            }
        });

        JRadioButton rotate90Radio = new JRadioButton("90°", true);
        rotate90Radio.setHorizontalTextPosition(JRadioButton.TRAILING);
        rotate90Radio.setEnabled(false);
        rotate90Radio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                FlipRotateDialog.this._mode = FlipRotateDialog.ROTATE_90;
            }
        });

        JRadioButton rotate180Radio = new JRadioButton("180°", false);
        rotate180Radio.setHorizontalTextPosition(JRadioButton.TRAILING);
        rotate180Radio.setEnabled(false);
        rotate180Radio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                FlipRotateDialog.this._mode = FlipRotateDialog.ROTATE_180;
            }
        });

        JRadioButton rotate270Radio = new JRadioButton("270°", false);
        rotate270Radio.setHorizontalTextPosition(JRadioButton.TRAILING);
        rotate270Radio.setEnabled(false);
        rotate270Radio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                FlipRotateDialog.this._mode = FlipRotateDialog.ROTATE_270;
            }
        });

        ButtonGroup rotateGroup = new ButtonGroup();
        rotateGroup.add(rotate90Radio);
        rotateGroup.add(rotate180Radio);
        rotateGroup.add(rotate270Radio);

        JRadioButton rotateRadio = new JRadioButton("Rotate by angle", false);
        rotateRadio.setHorizontalTextPosition(JRadioButton.TRAILING);
        rotateRadio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (rotate90Radio.isSelected()) {
                    FlipRotateDialog.this._mode = FlipRotateDialog.ROTATE_90;
                } else if (rotate180Radio.isSelected()) {
                    FlipRotateDialog.this._mode = FlipRotateDialog.ROTATE_180;
                } else {
                    FlipRotateDialog.this._mode = FlipRotateDialog.ROTATE_270;
                }

                rotate90Radio.setEnabled(true);
                rotate180Radio.setEnabled(true);
                rotate270Radio.setEnabled(true);
            } else {
                rotate90Radio.setEnabled(false);
                rotate180Radio.setEnabled(false);
                rotate270Radio.setEnabled(false);
            }
        });

        // Group radio buttons
        ButtonGroup mainGroup = new ButtonGroup();
        mainGroup.add(horizFlipRadio);
        mainGroup.add(vertFlipRadio);
        mainGroup.add(rotateRadio);

        layout.fill = GridBagConstraints.HORIZONTAL;
        layout.anchor = GridBagConstraints.WEST;
        layout.weightx = 3;

        layout.gridx = 0;
        layout.gridy = 0;
        layout.gridwidth = 2;
        panel.add(horizFlipRadio, layout);

        layout.gridy = 1;
        panel.add(vertFlipRadio, layout);

        layout.gridy = 2;
        panel.add(rotateRadio, layout);

        layout.gridy = 0;
        layout.gridheight = 3;
        layout.gridwidth = 1;
        panel.add(Box.createVerticalBox(), layout);

        layout.gridx = 1;
        layout.gridy = 3;
        layout.gridheight = 1;
        panel.add(rotate90Radio, layout);

        layout.gridy = 4;
        panel.add(rotate180Radio, layout);

        layout.gridy = 5;
        panel.add(rotate270Radio, layout);

        layout.gridx = 2;
        layout.gridy = 0;
        layout.gridheight = 5;
        layout.gridwidth = 2;
        layout.weightx = 3;
        panel.add(Box.createVerticalBox(), layout);

        return panel;
    }

    private String _getResult(String code) {
        return code + "|" + this._mode;
    }
}
