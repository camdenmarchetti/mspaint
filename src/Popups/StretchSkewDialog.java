package Popups;

import Utilities.ImageUtils;
import Utilities.NumberUtils;
import Utilities.SpriteSheet;
import Valids.Direction;
import Valids.RotateAmount;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class StretchSkewDialog extends JDialog {

    private final String _title = "Stretch and Skew";
    private final ModalityType _modalityType = ModalityType.APPLICATION_MODAL;

    private Direction _skewDirection = Direction.Horizontal;
    private double _skewAmount = 0;

    private Direction _stretchDirection = Direction.Horizontal;
    private double _stretchAmount = 100;

    private JTextField _skewAmountH, _skewAmountV;
    private JTextField _stretchAmountH, _stretchAmountV;

    public StretchSkewDialog(Window owner) {
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
            internal.add(_createStretchSkew(), internalLayout);

            internalLayout.gridx = 7;
            internalLayout.gridwidth = 2;
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

    private JPanel _createStretchSkew() throws NullPointerException {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(_createTitledPanel("Stretch", "%", 5, 5, false));
        panel.add(_createTitledPanel("Skew", "Degrees", 6, 0, true));
        panel.add(Box.createGlue());

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
            System.out.println("Clicked OK");
            StretchSkewDialog.this.firePropertyChange("result", "", StretchSkewDialog.this._getResult());
            StretchSkewDialog.this.dispose();
        });

        JButton buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(e -> {
            System.out.println("Clicked Cancel");
            StretchSkewDialog.this.firePropertyChange("result", "", StretchSkewDialog.this._getResult());
            StretchSkewDialog.this.dispose();
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

    private JPanel _createTitledPanel(String title, String label, int spriteRow, int spriteCol, boolean skewPanel) {
        JPanel panel = new JPanel();
        Border basicEtch = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        panel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
        panel.setLayout(new GridLayout(2, 4, 1, 1));
        panel.setBorder(BorderFactory.createTitledBorder(basicEtch, title));

        BufferedImage image = (BufferedImage)((ImageIcon)SpriteSheet.GetIcon(spriteRow, spriteCol, 1.5)).getImage();
        BufferedImage scaledImage = ImageUtils.ScaleImage(image, 2.0, 2.0);

        // Horizontal components
        JPanel horizPanel = new JPanel();
        horizPanel.setLayout(new GridBagLayout());

        GridBagConstraints horizLayout = new GridBagConstraints();
        horizLayout.anchor = GridBagConstraints.CENTER;
        horizLayout.fill = GridBagConstraints.HORIZONTAL;
        horizLayout.weightx = 2;

        JTextField horizField = new JTextField();
        horizField.setEnabled(true);

        ImageIcon horizIcon = new ImageIcon(scaledImage);
        JLabel horizLabel = new JLabel(label);

        if (skewPanel) {
            this._skewAmountH = horizField;
        } else {
            this._stretchAmountH = horizField;
        }

        horizField.getDocument().addDocumentListener(new DocumentListener() {
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
                String text = horizField.getText();
                if (text.isEmpty()) {
                    return;
                }

                double value = NumberUtils.Convert(text, Double.class).doubleValue();
                if (skewPanel) {
                    StretchSkewDialog.this._skewAmount = value;
                } else {
                    StretchSkewDialog.this._stretchAmount = value;
                }
            }
        });

        JRadioButton horizRadio = new JRadioButton("Horizontal:", true);
        horizRadio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                horizField.setEnabled(true);

                if (skewPanel) {
                    StretchSkewDialog.this._skewDirection = Direction.Horizontal;
                } else {
                    StretchSkewDialog.this._stretchDirection = Direction.Horizontal;
                }
            } else {
                horizField.setEnabled(false);
            }
        });

        // Vertical components
        JPanel vertPanel = new JPanel();
        vertPanel.setLayout(new GridBagLayout());

        GridBagConstraints vertLayout = new GridBagConstraints();
        vertLayout.anchor = GridBagConstraints.CENTER;
        vertLayout.fill = GridBagConstraints.HORIZONTAL;
        vertLayout.weightx = 2;

        JTextField vertField = new JTextField();
        vertField.setEnabled(false);

        if (skewPanel) {
            this._skewAmountV = vertField;
        } else {
            this._stretchAmountV = vertField;
        }

        BufferedImage vertImage = ImageUtils.RotateImage(scaledImage, RotateAmount.R90);
        if (skewPanel) {
            vertImage = ImageUtils.FlipImage(vertImage, Direction.Vertical);
        }
        ImageIcon vertIcon = new ImageIcon(vertImage);
        JLabel vertLabel = new JLabel(label);

        vertField.getDocument().addDocumentListener(new DocumentListener() {
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
                String text = vertField.getText();
                if (text.isEmpty()) {
                    return;
                }

                double value = NumberUtils.Convert(text, Double.class).doubleValue();
                if (skewPanel) {
                    StretchSkewDialog.this._skewAmount = value;
                } else {
                    StretchSkewDialog.this._stretchAmount = value;
                }
            }
        });

        JRadioButton vertRadio = new JRadioButton("Vertical:", false);
        vertRadio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                vertField.setEnabled(true);

                if (skewPanel) {
                    StretchSkewDialog.this._skewDirection = Direction.Vertical;
                } else {
                    StretchSkewDialog.this._stretchDirection = Direction.Vertical;
                }
            } else {
                vertField.setEnabled(false);
            }
        });

        // Group radio buttons
        ButtonGroup stretchGroup = new ButtonGroup();
        stretchGroup.add(horizRadio);
        stretchGroup.add(vertRadio);

        horizPanel.add(horizField, horizLayout);
        vertPanel.add(vertField, vertLayout);

        // Add all components
        panel.add(new JLabel(horizIcon));
        panel.add(horizRadio);
        panel.add(horizPanel);
        panel.add(horizLabel);

        panel.add(new JLabel(vertIcon));
        panel.add(vertRadio);
        panel.add(vertPanel);
        panel.add(vertLabel);

        return panel;
    }

    public void setTextValues(Dimension stretch, Dimension skew) {
        this._skewAmountH.setText("" + skew.width);
        this._skewAmountV.setText("" + skew.height);

        this._stretchAmountH.setText("" + stretch.width);
        this._stretchAmountV.setText("" + stretch.height);

        revalidate();
        repaint();
    }

    private String _getResult() {
        String stretch = (this._stretchDirection == Direction.Horizontal) ? "H" : "V";
        String skew = (this._skewDirection == Direction.Horizontal) ? "H" : "V";

        return String.format("O|%s|%f|%s|%f", stretch, this._stretchAmount, skew, this._skewAmount);
    }
}
