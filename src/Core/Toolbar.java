package Core;

import Popups.FontDialog;
import Tools.*;
import Utilities.CursorUtils;
import Utilities.SpriteSheet;
import Valids.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;

// For selecting a tool to use
public class Toolbar extends JPanel {

    private JToggleButton _activeButton;
    private ToolSettings _linkedToolSettings;

    public static Cursor GetCurrentCursor() { return CursorUtils.getCurrentCursor(); }

    Toolbar(PaintCanvas linkedCanvas, ToolSettings settings) {
        super();
        this._linkedToolSettings = settings;

        setBackground(Color.LIGHT_GRAY);

        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        JPanel toolList = new JPanel();
        toolList.setLayout(new GridBagLayout());

        GridBagConstraints toolGrid = new GridBagConstraints();
        toolGrid.insets = new Insets(0, 0, 1, 1);

        JToggleButton polySelect = _createToolButton(0, 3, linkedCanvas, FreeSelect.class);
        JToggleButton rectSelect = _createToolButton(2, 3, linkedCanvas, Tools.RectSelect.class);
        JToggleButton eraser = _createToolButton(0, 2, linkedCanvas, Tools.Eraser.class);
        JToggleButton bucket = _createToolButton(0, 0, linkedCanvas, Tools.PaintBucket.class);
        JToggleButton picker = _createToolButton(1, 4, linkedCanvas, Tools.ColorPicker.class);
        JToggleButton magnifier = _createToolButton(1, 0, linkedCanvas, Tools.MagnifyingGlass.class);
        JToggleButton pencil = _createToolButton(1, 3, linkedCanvas, Tools.Pencil.class);
        JToggleButton brush = _createToolButton(1, 2, linkedCanvas, Tools.Paintbrush.class);
        JToggleButton sprayCan = _createToolButton(1, 5, linkedCanvas, Tools.SprayCan.class);
        JToggleButton text = _createToolButton(0, 5, linkedCanvas, Tools.Text.class);
        JToggleButton line = _createToolButton(0, 4, linkedCanvas, Tools.Line.class);
        JToggleButton curvedLine = _createToolButton(0, 1, linkedCanvas, Tools.CurvedLine.class);
        JToggleButton rectangle = _createToolButton(2, 1, linkedCanvas, Tools.Rectangle.class);
        JToggleButton polygon = _createToolButton(2, 0, linkedCanvas, Tools.Polygon.class);
        JToggleButton oval = _createToolButton(1, 1, linkedCanvas, Tools.Oval.class);
        JToggleButton roundedRect = _createToolButton(2, 2, linkedCanvas, Tools.RoundedRectangle.class);

        toolGrid.gridx = 0;
        toolGrid.gridy = 0;
        toolList.add(polySelect, toolGrid);

        toolGrid.gridy = 1;
        toolList.add(eraser, toolGrid);

        toolGrid.gridy = 2;
        toolList.add(picker, toolGrid);

        toolGrid.gridy = 3;
        toolList.add(pencil, toolGrid);

        toolGrid.gridy = 4;
        toolList.add(sprayCan, toolGrid);

        toolGrid.gridy = 5;
        toolList.add(line, toolGrid);

        toolGrid.gridy = 6;
        toolList.add(rectangle, toolGrid);

        toolGrid.gridy = 7;
        toolList.add(oval, toolGrid);

        toolGrid.gridx = 1;
        toolList.add(roundedRect, toolGrid);

        toolGrid.gridy = 6;
        toolList.add(polygon, toolGrid);

        toolGrid.gridy = 5;
        toolList.add(curvedLine, toolGrid);

        toolGrid.gridy = 4;
        toolList.add(text, toolGrid);

        toolGrid.gridy = 3;
        toolList.add(brush, toolGrid);

        toolGrid.gridy = 2;
        toolList.add(magnifier, toolGrid);

        toolGrid.gridy = 1;
        toolList.add(bucket, toolGrid);

        toolGrid.gridy = 0;
        toolList.add(rectSelect, toolGrid);

        polySelect.setSelected(false);
        rectSelect.setSelected(false);
        eraser.setSelected(false);
        bucket.setSelected(false);
        picker.setSelected(false);
        magnifier.setSelected(false);
        pencil.setSelected(false);
        brush.setSelected(false);
        sprayCan.setSelected(false);
        text.setSelected(false);
        //text.setEnabled(false);
        line.setSelected(false);
        curvedLine.setSelected(false);
        rectangle.setSelected(false);
        polygon.setSelected(false);
        oval.setSelected(false);
        roundedRect.setSelected(false);

        this._activeButton = pencil;
        this._activeButton.setSelected(true);
        this._activeButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.BLUE, Color.BLUE));

        toolList.setAlignmentY(Component.TOP_ALIGNMENT);

        Dimension panelSize = new Dimension(70, 280);
        toolList.setMaximumSize(panelSize);
        toolList.setMinimumSize(panelSize);
        toolList.setPreferredSize(panelSize);
        toolList.setSize(panelSize);

        Dimension fullSizeMin = new Dimension(70, 500);
        setMinimumSize(fullSizeMin);
        setPreferredSize(fullSizeMin);
        setSize(fullSizeMin);

        add(toolList);
        add(settings);
        add(Box.createVerticalGlue());
    }

    private JToggleButton _createToolButton(int spriteRow, int spriteCol, PaintCanvas canvas, Class toolClass) {
        JToggleButton button = new JToggleButton();
        Dimension buttonSize = new Dimension(34, 34);

        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setSize(buttonSize);

        button.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.WHITE, Color.GRAY));

        button.setIcon(SpriteSheet.GetIcon(spriteRow, spriteCol, 1.0));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                if (canvas.getTool().getClass() != toolClass) {
                    try {
                        if (toolClass.getSimpleName().trim().equalsIgnoreCase("ColorPicker")) {
                            canvas.setTool(new ColorPicker(Toolbar.this));
                        } else {
                            canvas.setTool((PaintTool) toolClass.newInstance());
                        }
                        Toolbar.this._linkedToolSettings.ClearSettings();

                        if (FontDialog.isCreated()) {
                            FontDialog.disposeCurrent();
                        }

                        // Setup the ToolSettings window
                        switch (toolClass.getSimpleName().trim().toUpperCase()) {
                            case "RECTANGLE":
                            case "ROUNDEDRECTANGLE":
                            case "OVAL":
                            case "POLYGON":
                                CursorUtils.setCurrentCursor(CursorType.Crosshair);
                                Toolbar.this._linkedToolSettings.setupFillMode();
                                break;
                            case "LINE":
                            case "CURVEDLINE":
                                CursorUtils.setCurrentCursor(CursorType.Crosshair);
                                Toolbar.this._linkedToolSettings.setupLine(0);
                                break;
                            case "SPRAYCAN":
                                CursorUtils.setCurrentCursor(CursorType.SprayCan);
                                //Toolbar._cursor = _loadCursor("/res/spraycan.gif", new Point(0, 0));
                                Toolbar.this._linkedToolSettings.setupSprayCan();
                                break;
                            case "ERASER":
                                CursorUtils.setCurrentCursor(CursorType.Box);
                                Toolbar.this._linkedToolSettings.setupEraser();
                                break;
                            case "PAINTBRUSH":
                                CursorUtils.setCurrentCursor(CursorType.Crosshair);
                                Toolbar.this._linkedToolSettings.setupPaintBrush();
                                break;
                            case "RECTSELECT":
                            case "FREESELECT":
                                CursorUtils.setCurrentCursor(CursorType.Crosshair);
                                Toolbar.this._linkedToolSettings.setupSelectMode();
                                break;
                            case "TEXT":
                                CursorUtils.setCurrentCursor(CursorType.Crosshair);
                                ApplicationWindow.ShowFontDialog();
                                //Toolbar.this._linkedToolSettings.setupSelectMode();
                                break;
                            case "MAGNIFYINGGLASS":
                                CursorUtils.setCurrentCursor(CursorType.Magnify);
                                Toolbar.this._linkedToolSettings.setupZoomMode();
                                break;
                            case "PAINTBUCKET":
                                CursorUtils.setCurrentCursor(CursorType.Bucket);
                                break;
                            case "PENCIL":
                                CursorUtils.setCurrentCursor(CursorType.Pencil);
                                break;
                            case "COLORPICKER":
                                CursorUtils.setCurrentCursor(CursorType.Crosshair);
                                break;
                        }

                        button.setSelected(true);
                        button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.BLUE, Color.BLUE));

                        Toolbar.this._activeButton.setSelected(false);
                        Toolbar.this._activeButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.WHITE, Color.GRAY));

                        Toolbar.this._activeButton = button;
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return button;
    }

    public void FillBox(Color fillColor) {
        this._linkedToolSettings.setBackground(fillColor);
    }

}
