package Core;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JLayeredPane;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

// For selecting the colors to use
public class ColorBar extends JPanel {

    private static final String _PAL_HEADER_ = "52494646 80000000 50414C20 64617461 74000000 00031C00";
    private static Color[][] _DEFAULT_COLORS_ = {
        {   // Row one colors (darker)
            new Color(0, 0, 0),
            new Color(123, 123, 123),
            new Color(123, 3, 7),
            new Color(123, 122, 22),
            new Color(15, 121, 17),
            new Color(16, 122, 122),
            new Color(2, 12, 122),
            new Color(123, 14, 122),
            new Color(123, 123, 61),
            new Color(4, 56, 58),
            new Color(21, 126, 251),
            new Color(5, 59, 122),
            new Color(59, 36, 251),
            new Color(122, 57, 11)
        },
        {   // Row two colors (brighter)
            new Color(255, 255, 255),
            new Color(189, 189, 189),
            new Color(251, 13, 27),
            new Color(255, 253, 56),
            new Color(41, 253, 47),
            new Color(45, 255, 254),
            new Color(11, 36, 250),
            new Color(252, 40, 252),
            new Color(255, 253, 132),
            new Color(42, 253, 129),
            new Color(130, 255, 254),
            new Color(124, 127, 251),
            new Color(252, 21, 125),
            new Color(252, 123, 67)
        }
    };

    private JButton[][] _colorButtons = {
            { null, null, null, null, null, null, null, null, null, null, null, null, null, null },
            { null, null, null, null, null, null, null, null, null, null, null, null, null, null }
    };

    private JPanel _colorSet;
    private JLayeredPane _swatchPane;
    private JLabel _primarySwatch, _secondarySwatch;

    private int _primaryIndex = 0;

    private static ColorBar _instance;
    public static void RepaintNewColors(Color newColor, int index) {
        if (index == 0) {
            ColorBar._instance._primarySwatch.setBackground(newColor);

            List<Color> colorListA = Arrays.asList(ColorBar._DEFAULT_COLORS_[0]);
            List<Color> colorListB = Arrays.asList(ColorBar._DEFAULT_COLORS_[1]);

            if (colorListA.contains(newColor)) {
                ColorBar._instance._primaryIndex = colorListA.indexOf(newColor);
            } else if (colorListB.contains(newColor)) {
                ColorBar._instance._primaryIndex = colorListB.indexOf(newColor) + 14;
            } else {
                if (ColorBar._instance._primaryIndex < 14) {
                    ColorBar._DEFAULT_COLORS_[0][ColorBar._instance._primaryIndex] = newColor;
                } else {
                    ColorBar._DEFAULT_COLORS_[1][ColorBar._instance._primaryIndex - 14] = newColor;
                }
            }
        } else if (index == 1) {
            ColorBar._instance._secondarySwatch.setBackground(newColor);
        }

        ColorBar._instance._swatchPane.setLayer(ColorBar._instance._primarySwatch, 1);
        ColorBar._instance._swatchPane.setLayer(ColorBar._instance._secondarySwatch, 0);
    }

    private static void UpdateCurrentColorIndex(Color newColor) {
        List<Color> colorListA = Arrays.asList(ColorBar._DEFAULT_COLORS_[0]);
        List<Color> colorListB = Arrays.asList(ColorBar._DEFAULT_COLORS_[1]);

        if (colorListA.contains(newColor)) {
            ColorBar._instance._primaryIndex = colorListA.indexOf(newColor);
        } else if (colorListB.contains(newColor)) {
            ColorBar._instance._primaryIndex = colorListB.indexOf(newColor) + 14;
        }
    }

    public ColorBar(PaintCanvas linkedCanvas) {
        super();

        ColorBar._instance = this;

        setSize(150, 90);
        setBackground(null);

        BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
        setLayout(layout);

        Dimension swatch = new Dimension(17, 17);
        Color background = getBackground();

        this._colorSet = new JPanel();
        this._colorSet.setBackground(background);

        GridBagLayout colorGrid = new GridBagLayout();
        GridBagConstraints colorConstraints = new GridBagConstraints();
        this._colorSet.setLayout(colorGrid);

        colorConstraints.insets = new Insets(1, 1, 0, 0);

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 14; col++) {
                JButton button = new JButton();

                button.setSize(swatch);
                button.setPreferredSize(swatch);
                button.setMinimumSize(swatch);
                button.setMaximumSize(swatch);

                button.setBackground(ColorBar._DEFAULT_COLORS_[row][col]);
                button.setOpaque(true);
                button.setBorderPainted(true);
                button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, ColorBar._DEFAULT_COLORS_[row][col], Color.BLACK));

                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Color newColor = button.getBackground();

                        if (SwingUtilities.isLeftMouseButton(e)) {
                            linkedCanvas.setColor(0, newColor);

                            ColorBar.this._primarySwatch.setBackground(newColor);
                        } else if(SwingUtilities.isRightMouseButton(e)) {
                            linkedCanvas.setColor(1, newColor);

                            ColorBar.this._secondarySwatch.setBackground(newColor);
                        }

                        ColorBar.this._swatchPane.setLayer(ColorBar.this._primarySwatch, 1);
                        ColorBar.this._swatchPane.setLayer(ColorBar.this._secondarySwatch, 0);
                        ColorBar.UpdateCurrentColorIndex(newColor);
                    }
                });

                colorConstraints.gridx = col + 2;
                colorConstraints.gridy = row;
                this._colorSet.add(button, colorConstraints);
                this._colorButtons[row][col] = button;
            }
        }

        Dimension doubleSwatch = new Dimension(swatch.width * 2, swatch.height * 2);

        this._swatchPane = new JLayeredPane();
        this._swatchPane.setSize(doubleSwatch);
        this._swatchPane.setPreferredSize(doubleSwatch);
        this._swatchPane.setMinimumSize(doubleSwatch);
        this._swatchPane.setMaximumSize(doubleSwatch);
        this._swatchPane.setBackground(Color.WHITE);

        this._swatchPane.setBounds(swatch.width / 4, swatch.height / 4, swatch.width, swatch.height);

        this._primarySwatch = new JLabel();
        this._secondarySwatch = new JLabel();

        this._primarySwatch.setBackground(linkedCanvas.getColor(0));
        this._primarySwatch.setSize(swatch);
        this._primarySwatch.setPreferredSize(swatch);
        this._primarySwatch.setMinimumSize(swatch);
        this._primarySwatch.setMaximumSize(swatch);
        this._primarySwatch.setLocation(swatch.width / 4, swatch.height / 4);
        this._primarySwatch.setOpaque(true);
        this._primarySwatch.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        this._secondarySwatch.setBackground(linkedCanvas.getColor(1));
        this._secondarySwatch.setSize(swatch);
        this._secondarySwatch.setPreferredSize(swatch);
        this._secondarySwatch.setMinimumSize(swatch);
        this._secondarySwatch.setMaximumSize(swatch);
        this._secondarySwatch.setLocation(swatch.width * 3 / 4, swatch.height * 3 / 4);
        this._secondarySwatch.setOpaque(true);
        this._secondarySwatch.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        this._swatchPane.add(this._primarySwatch, 0);
        this._swatchPane.add(this._secondarySwatch, 1);

        colorConstraints.gridx = 0;
        colorConstraints.gridy = 0;
        colorConstraints.gridwidth = 2;
        colorConstraints.gridheight = 2;
        colorConstraints.weightx = 1;
        colorConstraints.weighty = 1;
        this._swatchPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.BLACK, new Color(0,0, 0, 0)));
        this._colorSet.add(this._swatchPane, colorConstraints);

        add(this._colorSet);
        add(Box.createHorizontalGlue());

        this._colorSet.setMaximumSize(this._colorSet.getPreferredSize());
    }

    public void Edit() {
        Color newColor = JColorChooser.showDialog(this, "Choose Color", Color.WHITE);

        if (newColor == null) {
            return;
        }

        int index0 = this._primaryIndex < 14 ? 0 : 1;
        int index1 = this._primaryIndex < 14 ? this._primaryIndex : this._primaryIndex - 14;

        ColorBar._DEFAULT_COLORS_[index0][index1] = newColor;
        this._colorButtons[index0][index1].setBackground(newColor);
        this._colorButtons[index0][index1].setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, newColor, Color.BLACK));

        this._colorSet.revalidate();
        this._colorSet.repaint();
    }

    public void Import() {
        JFileChooser inputSelection = new JFileChooser();
        inputSelection.addChoosableFileFilter(new FileNameExtensionFilter("Palette", "pal"));
        inputSelection.removeChoosableFileFilter(inputSelection.getAcceptAllFileFilter());

        if (inputSelection.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File path = inputSelection.getSelectedFile();
        if (path == null) {
            return;
        }

        byte[] fileContents;
        try {
            fileContents =  Files.readAllBytes(Paths.get(path.getAbsolutePath()));
        } catch (Exception ex) {
            return;
        }

        int colorStep = 0;
        int[] components = { 0, 0, 0 };

        int colorIndex = 0;
        for (int index = 24; index < fileContents.length; index++) {
            if (colorStep < 3) {
                components[colorStep++] = (fileContents[index] & 0xFF);
            } else {
                Color newColor = new Color(components[0], components[1], components[2], (fileContents[index] & 0xFF));

                if (colorIndex < 14) {
                    ColorBar._DEFAULT_COLORS_[0][colorIndex] = newColor;
                    this._colorButtons[0][colorIndex].setBackground(newColor);
                    this._colorButtons[0][colorIndex].setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, newColor, Color.BLACK));
                } else {
                    ColorBar._DEFAULT_COLORS_[1][colorIndex - 14] = newColor;
                    this._colorButtons[1][colorIndex - 14].setBackground(newColor);
                    this._colorButtons[1][colorIndex - 14].setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, newColor, Color.BLACK));
                }

                colorIndex++;
                colorStep = 0;
            }
        }

        this._colorSet.revalidate();
        this._colorSet.repaint();
    }

    public void Export() {
        List<Byte> output = new ArrayList<>();

        for (String chunk : ColorBar._PAL_HEADER_.split(" ")) {
            for (int bit = 0; bit < (chunk.length() / 2); bit++) {
                output.add((byte)Integer.parseInt(chunk.substring(2 * bit, 2 * bit + 2),16));
            }
        }

        for (Color swatch : ColorBar._DEFAULT_COLORS_[0]) {
            _loadColorToList(output, swatch);
        }

        for (Color swatch : ColorBar._DEFAULT_COLORS_[1]) {
            _loadColorToList(output, swatch);
        }

        JFileChooser outputSelection = new JFileChooser();
        outputSelection.showSaveDialog(null);

        File path = outputSelection.getSelectedFile();
        if (path == null) {
            return;
        }

        FileOutputStream stream;
        try {
            stream = new FileOutputStream(path.getAbsolutePath());

            byte[] outputArray = new byte[output.size()];
            for (int index = 0; index < output.size(); index++) {
                outputArray[index] = output.get(index);
            }

            stream.write(outputArray);
            stream.close();
        } catch (Exception ex) {
            // Uh oh...
        }
    }

    private void _loadColorToList(List<Byte> list, Color swatch) {
        int color = swatch.getRGB();

        list.add((byte)(color >> 16 & 0xFF));
        list.add((byte)(color >> 8 & 0xFF));
        list.add((byte)(color & 0xFF));
        list.add((byte)(swatch.getAlpha() & 0xFF));
    }
}
