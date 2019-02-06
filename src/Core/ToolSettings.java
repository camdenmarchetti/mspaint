package Core;

import Utilities.CursorUtils;
import Utilities.SpriteSheet;

import Valids.BrushShape;
import Valids.FillMode;
import Valids.SelectMode;
import Valids.ZoomLevel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

// For selecting things like brush shape or size
public class ToolSettings extends JPanel {
    /* Possible settings */
    private static int _brushSize = 1;
    public static int getBrushSize() { return _brushSize; }
    public static void setBrushSize(int size) {
        int old = getBrushSize();

        _brushSize = size;

        _propertyFirer.firePropertyChange("brushSize", old, size);
    }

    private static BrushShape _brushShape = BrushShape.Circle;
    public static BrushShape getBrushShape() { return _brushShape; }
    public static void setBrushShape(BrushShape shape) {
        BrushShape old = getBrushShape();

        _brushShape = shape;

        _propertyFirer.firePropertyChange("brushShape", old, shape);
    }

    private static SelectMode _selectMode = SelectMode.IgnoreBackground;
    public static SelectMode getSelectMode() { return _selectMode; }
    public static void setSelectMode(SelectMode mode) {
        SelectMode old = getSelectMode();

        _selectMode = mode;

        _propertyFirer.firePropertyChange("selectMode", old, mode);
    }

    private static FillMode _fillMode = FillMode.BorderNoFill;
    public static FillMode getFillMode() { return _fillMode; }
    public static void setFillMode(FillMode mode) {
        FillMode old = getFillMode();

        _fillMode = mode;

        _propertyFirer.firePropertyChange("fillMode", old, mode);
    }

    private static ZoomLevel _zoomLevel = ZoomLevel.x1;
    public static int getZoomLevel() { return _zoomLevel.getValue(); }
    public static void setZoomLevel(ZoomLevel level) {
        int old = getZoomLevel();

        _zoomLevel = level;

        _propertyFirer.firePropertyChange("zoomLevel", old, level.getValue());
    }

    private static ToolSettings _propertyFirer;

    private static JButton[] _selectedButtons = new JButton[2];
    private static Color _selectionColor = new Color(22, 129, 251);

    public ToolSettings() {
        super();
        setBackground(Color.LIGHT_GRAY);
        setBorder(new EtchedBorder());

        Dimension settingSize = new Dimension(70, 104);
        setSize(settingSize);
        setMinimumSize(settingSize);
        setMaximumSize(settingSize);
        setPreferredSize(settingSize);

        ToolSettings._propertyFirer = this;
    }

    public void ClearSettings() {
        removeAll();

        _brushSize = 1;
        _selectMode = SelectMode.IgnoreBackground;
        _fillMode = FillMode.BorderNoFill;

        EventQueue.invokeLater(() -> { revalidate(); repaint(); });
    }

    // NOTE: The below is probably the worst way to handle this (lol I should fix it)
    public void setupPaintBrush() {
        setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();

        JButton[] circles = _makePaintBrushRow(BrushShape.Circle, new int[] { 14, 6, 7 }, new int[] { 2, 4, 7 });
        JButton[] squares = _makePaintBrushRow(BrushShape.Square, new int[] { 31, 15, 23 }, new int[] { 2, 5, 8 });
        JButton[] forwards = _makePaintBrushRow(BrushShape.ForeSlash, new int[] { 27, 26, 25 }, new int[] { 3, 6, 9 });
        JButton[] backwards = _makePaintBrushRow(BrushShape.BackSlash, new int[] { 28, 29, 30 }, new int[] { 3, 6, 9 });

        grid.insets = new Insets(3, 3, 3, 3);

        for (int index = 0; index < 3; index++) {
            grid.gridx = index;

            grid.gridy = 0;
            add(circles[index], grid);

            grid.gridy = 1;
            add(squares[index], grid);

            grid.gridy = 2;
            add(forwards[index], grid);

            grid.gridy = 3;
            add(backwards[index], grid);
        }

        setBrushSize(4);
        setBrushShape(BrushShape.Circle);
        _colorButton(circles[1], 0);


        // Revalidate/repaint the window
        EventQueue.invokeLater(() -> { revalidate(); repaint(); });
    }

    private JButton[] _makePaintBrushRow(BrushShape shape, int[] indexes, int[] sizes) {

        Dimension buttonSize = new Dimension((getWidth() - 18) / 3, (getHeight() - 24) / 4);

        JButton large = new JButton();

        large.setBorder(BorderFactory.createEmptyBorder());
        large.setBackground(null);
        large.setOpaque(true);

        large.setIcon(SpriteSheet.GetIcon(indexes[2]));

        large.setSize(buttonSize);
        large.setMinimumSize(buttonSize);
        large.setMaximumSize(buttonSize);
        large.setPreferredSize(buttonSize);

        large.addActionListener(e -> {
            ToolSettings.setBrushSize(sizes[2]);
            ToolSettings.setBrushShape(shape);

            _colorButton(large, 0);
        });


        JButton medium = new JButton();

        medium.setBorder(BorderFactory.createEmptyBorder());
        medium.setBackground(null);
        medium.setOpaque(true);

        medium.setIcon(SpriteSheet.GetIcon(indexes[1]));

        medium.setSize(buttonSize);
        medium.setMinimumSize(buttonSize);
        medium.setMaximumSize(buttonSize);
        medium.setPreferredSize(buttonSize);

        medium.addActionListener(e -> {
            ToolSettings.setBrushSize(sizes[1]);
            ToolSettings.setBrushShape(shape);

            _colorButton(medium, 0);
        });


        JButton small = new JButton();

        small.setBorder(BorderFactory.createEmptyBorder());
        small.setBackground(null);
        small.setOpaque(true);

        small.setIcon(SpriteSheet.GetIcon(indexes[0]));

        small.setSize(buttonSize);
        small.setMinimumSize(buttonSize);
        small.setMaximumSize(buttonSize);
        small.setPreferredSize(buttonSize);

        small.addActionListener(e -> {
            ToolSettings.setBrushSize(sizes[0]);
            ToolSettings.setBrushShape(shape);

            _colorButton(small, 0);
        });

        return new JButton[]{ large, medium, small };
    }

    public void setupLine(int settingIndex) {
        setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        Dimension buttonSize = new Dimension(getWidth() - 6, (getHeight() - 36) / 5);

        grid.insets = new Insets(3, 3, 3, 3);
        grid.gridx = 0;
        grid.gridy = 0;
        grid.fill = GridBagConstraints.HORIZONTAL;
        grid.weightx = 1;
        grid.weighty = 1.0/5;

        int[] indexes = { 32, 33, 34, 35, 36 };
        for (int index = 0; index < 5; index++) {
            JButton btn = new JButton();

            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setBackground(null);
            btn.setOpaque(true);

            btn.setIcon(SpriteSheet.GetIcon(indexes[index]));

            btn.setSize(buttonSize);
            btn.setMinimumSize(buttonSize);
            btn.setMaximumSize(buttonSize);
            btn.setPreferredSize(buttonSize);

            final int brushSize = index + 1;
            btn.addActionListener(e -> {
                ToolSettings.setBrushSize(brushSize);
                _colorButton(btn, settingIndex);
            });

            if (index == 0) {
                ToolSettings._selectedButtons[settingIndex] = btn;
            }

            grid.gridy = index;
            add(btn, grid);
        }

        setBrushSize(1);
        _colorButton(ToolSettings._selectedButtons[settingIndex], settingIndex);

        // Revalidate/repaint the window
        EventQueue.invokeLater(() -> { revalidate(); repaint(); });
    }

    public void setupEraser() {
        setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        Dimension buttonSize = new Dimension(getWidth() - 6, (getHeight() - 30) / 4);

        grid.insets = new Insets(3, 3, 3, 3);
        grid.gridx = 0;
        grid.gridy = 0;
        grid.fill = GridBagConstraints.HORIZONTAL;
        grid.weightx = 1;
        grid.weighty = 1.0/4;

        int[] indexes = { 31, 15, 24, 23 };
        for (int index = 0; index < 4; index++) {
            JButton btn = new JButton();

            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setBackground(null);
            btn.setOpaque(true);

            btn.setIcon(SpriteSheet.GetIcon(indexes[index]));

            btn.setSize(buttonSize);
            btn.setMinimumSize(buttonSize);
            btn.setMaximumSize(buttonSize);
            btn.setPreferredSize(buttonSize);

            final int eraserSize = 4 + index * 2;
            btn.addActionListener(e -> {
                ToolSettings.setBrushSize(eraserSize);
                CursorUtils.setBoxCursorSize(eraserSize * ToolSettings.getZoomLevel());
                _colorButton(btn, 0);
            });

            if (index == 2) {
                ToolSettings._selectedButtons[0] = btn;
            }

            grid.gridy = index;
            add(btn, grid);
        }

        setBrushSize(8);
        CursorUtils.setBoxCursorSize(8 * ToolSettings.getZoomLevel());

        _colorButton(ToolSettings._selectedButtons[0], 0);

        // Revalidate/repaint the window
        EventQueue.invokeLater(() -> { revalidate(); repaint(); });
    }

    public void setupFillMode() {
        setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        Dimension buttonSize = new Dimension(getWidth() - 6, (getHeight() - 24) / 3);

        grid.insets = new Insets(3, 3, 3, 3);
        grid.gridx = 0;
        grid.gridy = 0;
        grid.fill = GridBagConstraints.HORIZONTAL;
        grid.weightx = 1;
        grid.weighty = 1.0/3;

        int[] indexes = { 37, 39, 38 };
        FillMode[] fillModes = { FillMode.BorderNoFill, FillMode.Both, FillMode.FillNoBorder };
        for (int index = 0; index < 3; index++) {
            JButton btn = new JButton();

            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setBackground(null);
            btn.setOpaque(true);

            btn.setIcon(SpriteSheet.GetIcon(indexes[index]));

            btn.setSize(buttonSize);
            btn.setMinimumSize(buttonSize);
            btn.setMaximumSize(buttonSize);
            btn.setPreferredSize(buttonSize);

            FillMode mode = fillModes[index];
            btn.addActionListener(e -> {
                ToolSettings.setFillMode(mode);
                _colorButton(btn, 0);
            });

            if (index == 0) {
                ToolSettings._selectedButtons[0] = btn;
            }

            grid.gridy = index;
            add(btn, grid);
        }
        setFillMode(fillModes[0]);
        _colorButton(ToolSettings._selectedButtons[0], 0);

        // Revalidate/repaint the window
        EventQueue.invokeLater(() -> { revalidate(); repaint(); });
    }

    public void setupSprayCan() {
        setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        grid.insets = new Insets(4, 4, 4, 4);

        Dimension buttonSize = new Dimension((getWidth() - 16) / 2, (getHeight() - 16) / 2);

        JButton small = new JButton();

        small.setBorder(BorderFactory.createEmptyBorder());
        small.setBackground(null);
        small.setOpaque(true);

        small.setIcon(SpriteSheet.GetIcon(2, 4, 1));

        small.setSize(buttonSize);
        small.setMinimumSize(buttonSize);
        small.setMaximumSize(buttonSize);
        small.setPreferredSize(buttonSize);

        small.addActionListener(e -> {
            ToolSettings.setBrushSize(5);
            _colorButton(small, 0);
        });


        JButton medium = new JButton();

        medium.setBorder(BorderFactory.createEmptyBorder());
        medium.setBackground(null);
        medium.setOpaque(true);

        medium.setIcon(SpriteSheet.GetIcon(2, 6, 1));
        medium.setSize(buttonSize);
        medium.setMinimumSize(buttonSize);
        medium.setMaximumSize(buttonSize);
        medium.setPreferredSize(buttonSize);

        medium.addActionListener(e -> {
            ToolSettings.setBrushSize(9);
            _colorButton(medium, 0);
        });


        JButton large = new JButton();

        large.setBorder(BorderFactory.createEmptyBorder());
        large.setBackground(null);
        large.setOpaque(true);

        large.setIcon(SpriteSheet.GetIcon(2, 5,1));

        large.setSize(buttonSize);
        large.setMinimumSize(buttonSize);
        large.setMaximumSize(buttonSize);
        large.setPreferredSize(buttonSize);

        large.addActionListener(e -> {
            ToolSettings.setBrushSize(13);
            _colorButton(large, 0);
        });


        grid.gridx = 0;
        grid.gridy = 0;
        add(small, grid);

        grid.gridx = 1;
        add(medium, grid);

        grid.gridx = 0;
        grid.gridy = 1;
        grid.weightx = 1;
        grid.gridwidth = 2;
        add(large, grid);


        setBrushSize(5);

        ToolSettings._selectedButtons[0] = small;
        _colorButton(ToolSettings._selectedButtons[0], 0);

        // Revalidate/repaint the window
        EventQueue.invokeLater(() -> { revalidate(); repaint(); });
    }

    public void setupSelectMode() {
        setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        Dimension buttonSize = new Dimension(getWidth() - 6, (getHeight() - 24) / 2);

        grid.insets = new Insets(3, 3, 3, 3);
        grid.gridx = 0;
        grid.gridy = 0;
        grid.fill = GridBagConstraints.HORIZONTAL;
        grid.weightx = 1;
        grid.weighty = 1.0/2;

        int[] indexes = { 40, 42 };
        SelectMode[] selectModes = { SelectMode.TakeBackground, SelectMode.IgnoreBackground };
        for (int index = 0; index < 2; index++) {
            JButton btn = new JButton();

            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setBackground(null);
            btn.setOpaque(true);

            ImageIcon startingIcon = (ImageIcon)SpriteSheet.GetIcon(indexes[index], 1.5);

            if (startingIcon != null) {
                Image startingImage = startingIcon.getImage();
                if (startingImage != null) {
                    double ratio = startingImage.getWidth(null) / (double)startingImage.getHeight(null);
                    int w = buttonSize.width - 6;
                    int h = buttonSize.height - 6;

                    if (h > w / ratio) {
                        h = (int)(w / ratio);
                    } else {
                        w = (int)(h * ratio);
                    }

                    ImageIcon expanded = new ImageIcon(startingImage.getScaledInstance(w, h, Image.SCALE_DEFAULT));

                    btn.setIcon(expanded);
                }
            }

            btn.setSize(buttonSize);
            btn.setMinimumSize(buttonSize);
            btn.setMaximumSize(buttonSize);
            btn.setPreferredSize(buttonSize);

            SelectMode mode = selectModes[index];
            btn.addActionListener(e -> {
                ToolSettings.setSelectMode(mode);
                _colorButton(btn, 0);
            });

            if (index == 0) {
                ToolSettings._selectedButtons[0] = btn;
            }

            grid.gridy = index;
            add(btn, grid);
        }
        setSelectMode(selectModes[0]);
        _colorButton(ToolSettings._selectedButtons[0], 0);

        // Revalidate/repaint the window
        EventQueue.invokeLater(() -> { revalidate(); repaint(); });
    }

    public void setupZoomMode() {
        setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        Dimension buttonSize = new Dimension(getWidth() - 6, (getHeight() - 24) / 4);

        grid.insets = new Insets(3, 3, 3, 3);
        grid.gridx = 0;
        grid.gridy = 0;
        grid.fill = GridBagConstraints.HORIZONTAL;
        grid.weightx = 1;
        grid.weighty = 1.0/4;

        // Odd ordering due to reuse of eraser sprites
        int[] indexes = { 44, 31, 24, 23 };
        ZoomLevel[] zoomLevels = { ZoomLevel.x1, ZoomLevel.x2, ZoomLevel.x6, ZoomLevel.x8 };
        String[] zoomLevelStrings = {"1x", "2x", "6x", "8x" };
        for (int index = 0; index < 4; index++) {
            JButton btn = new JButton(zoomLevelStrings[index]);

            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setBackground(null);
            btn.setOpaque(true);

            btn.setIcon(SpriteSheet.GetIcon(indexes[index]));
            btn.setHorizontalAlignment(SwingConstants.CENTER);
            btn.setHorizontalTextPosition(SwingConstants.LEFT);

            btn.setSize(buttonSize);
            btn.setMinimumSize(buttonSize);
            btn.setMaximumSize(buttonSize);
            btn.setPreferredSize(buttonSize);

            ZoomLevel level = zoomLevels[index];
            btn.addActionListener(e -> {
                int oldZoom = ToolSettings.getZoomLevel();
                if (oldZoom != level.getValue()) {
                    _colorButton(btn, 0);
                    ToolSettings.setZoomLevel(level);
                }
            });

            int zoomLevel = getZoomLevel();
            if (zoomLevel == 1 && index == 0 ||
                    zoomLevel == 2 && index == 1 ||
                    zoomLevel == 6 && index == 2 ||
                    zoomLevel == 8 && index == 3)
            {
                ToolSettings._selectedButtons[0] = btn;
            }

            grid.gridy = index;
            add(btn, grid);
        }

        _colorButton(ToolSettings._selectedButtons[0], 0);

        // Revalidate/repaint the window
        EventQueue.invokeLater(() -> { revalidate(); repaint(); });
    }

    private void _colorButton(JButton button, int index) {
        if (_selectedButtons[index] != null) {
            _selectedButtons[index].setBackground(null);
        }

        _selectedButtons[index] = button;
        _selectedButtons[index].setBackground(_selectionColor);
    }
}

