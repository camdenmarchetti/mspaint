package Core;

import Popups.AboutDialog;
import Popups.AttributesDialog;
import Popups.FlipRotateDialog;
import Popups.FontDialog;
import Popups.StretchSkewDialog;
import Utilities.CursorUtils;
import Valids.Direction;
import Valids.RotateAmount;
import Valids.SizeUnit;
import Valids.ZoomLevel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.ScrollPane;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

public class ApplicationWindow extends JFrame {

    private Easel _easel;
    private PaintCanvas _canvas;
    private Toolbar _toolbar;
    private ColorBar _colorBar;
    private ToolSettings _toolSettings;
    private static StatusBar _statusBarInst;

    public ApplicationWindow()
    {
        this._toolSettings = new ToolSettings();
        this._canvas = new PaintCanvas(_toolSettings);

        this._colorBar = new ColorBar(this._canvas);
        this._toolbar = new Toolbar(this._canvas, _toolSettings);

        this._easel = new Easel(this._canvas);
        this._canvas.LinkEasel(this._easel);

        if (_statusBarInst == null) {
            _statusBarInst = new StatusBar();
        }

        _setupWindow();
        _addMenu();

        addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                super.windowActivated(e);
                System.out.println("Activated!");
                CursorUtils.applyCursor(ApplicationWindow.this.getRootPane());
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                super.windowDeiconified(e);
                CursorUtils.applyCursor(ApplicationWindow.this.getRootPane());
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
                super.windowGainedFocus(e);
                System.out.println("Focus gained!");
                CursorUtils.applyCursor(ApplicationWindow.this.getRootPane());
            }

            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                CursorUtils.applyCursor(ApplicationWindow.this.getRootPane());
            }

            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                CursorUtils.clearCursor(ApplicationWindow.this.getRootPane());
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                super.windowDeactivated(e);
                CursorUtils.clearCursor(ApplicationWindow.this.getRootPane());
            }

            @Override
            public void windowIconified(WindowEvent e) {
                super.windowIconified(e);
                CursorUtils.clearCursor(ApplicationWindow.this.getRootPane());
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                super.windowLostFocus(e);
                CursorUtils.clearCursor(ApplicationWindow.this.getRootPane());
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                CursorUtils.applyCursor(getRootPane());
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                CursorUtils.clearCursor(getRootPane());
            }
        });
    }

    public static void UpdateStatus(String statusText) {
        _statusBarInst.UpdateText(statusText);
    }

    private void _setupWindow() {
        setSize(750, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 6, 5, 5);

        JPanel mainPanel = new JPanel();
        BorderLayout layout = new BorderLayout();
        mainPanel.setLayout(layout);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 2;
        constraints.weighty = 2;

        setBackground(Color.GRAY);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.out.println("Unsupported look and feel :(");
        }


        this._colorBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(this._colorBar, BorderLayout.SOUTH);

        this._toolbar.setAlignmentY(Component.TOP_ALIGNMENT);
        mainPanel.add(this._toolbar, BorderLayout.WEST);


        ScrollPane easelScroll = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        easelScroll.add(this._easel);
        mainPanel.add(easelScroll, BorderLayout.CENTER);

        add(mainPanel, constraints);
    }

    private void _addMenu() {
        JMenuBar menuBar = new JMenuBar();

        // TODO: Fix these descriptions lol
        JMenu fileMenu = _createFileMenu();
        JMenu editMenu = _createEditMenu();
        JMenu viewMenu = _createViewMenu();
        JMenu imageMenu = _createImageMenu();
        JMenu optionsMenu = _createOptionsMenu();
        JMenu helpMenu = _createHelpMenu();

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(imageMenu);
        menuBar.add(optionsMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    // Create the base JMenu object
    private JMenu _createMenu(String title, String help, int mnemonic) {
        JMenu menu = new JMenu(title);
        menu.setMnemonic(mnemonic);
        menu.getAccessibleContext().setAccessibleDescription(help);

        return menu;
    }

    // Create the JMenu and JMenu items for the Edit menu
    private JMenu _createEditMenu() {
        JMenu menu = _createMenu("Edit", "For Help, click Help Topics on the Help Menu.", KeyEvent.VK_E);

        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.addActionListener(ae -> ApplicationWindow.this._canvas.Undo());
        undoItem.setMnemonic(KeyEvent.VK_U);
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));

        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.addActionListener(ae -> ApplicationWindow.this._canvas.Redo());
        redoItem.setMnemonic(KeyEvent.VK_R);
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));

        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.addActionListener(ae -> ApplicationWindow.this._canvas.CutSelection());
        cutItem.setMnemonic(KeyEvent.VK_T);
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));

        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(ae -> ApplicationWindow.this._canvas.CopySelection());
        copyItem.setMnemonic(KeyEvent.VK_C);
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));

        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.addActionListener(ae -> ApplicationWindow.this._canvas.Paste());
        pasteItem.setMnemonic(KeyEvent.VK_P);
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));

        JMenuItem clearItem = new JMenuItem("Clear Selection");
        clearItem.addActionListener(ae -> ApplicationWindow.this._canvas.ClearSelection());
        clearItem.setMnemonic(KeyEvent.VK_L);
        clearItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));

        JMenuItem selectAllItem = new JMenuItem("Select All");
        selectAllItem.addActionListener(ae -> ApplicationWindow.this._canvas.SelectAll());
        selectAllItem.setMnemonic(KeyEvent.VK_A);
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));

        JMenuItem copyToItem = new JMenuItem("Copy To...");
        copyToItem.addActionListener(ae -> ApplicationWindow.this._canvas.CopySelectionTo());
        copyToItem.setMnemonic(KeyEvent.VK_O);

        JMenuItem pasteFromItem = new JMenuItem("Paste From...");
        pasteFromItem.addActionListener(ae -> ApplicationWindow.this._canvas.PasteFrom());
        pasteFromItem.setMnemonic(KeyEvent.VK_F);

        menu.add(undoItem);
        menu.add(redoItem);
        menu.addSeparator();
        menu.add(cutItem);
        menu.add(copyItem);
        menu.add(pasteItem);
        menu.add(clearItem);
        menu.add(selectAllItem);
        menu.addSeparator();
        menu.add(copyToItem);
        menu.add(pasteFromItem);

        return menu;
    }

    // Create the JMenu and JMenu items for the File menu
    private JMenu _createFileMenu() {
        JMenu menu = _createMenu("File", "For Help, click Help Topics on the Help Menu.", KeyEvent.VK_F);

        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(ae -> ApplicationWindow.this._canvas.Reset(false));
        newItem.setMnemonic(KeyEvent.VK_N);
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(ae -> ApplicationWindow.this._canvas.Open());
        openItem.setMnemonic(KeyEvent.VK_O);
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(ae -> ApplicationWindow.this._canvas.SaveDefault());
        saveItem.setMnemonic(KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

        JMenuItem saveAsItem = new JMenuItem("Save As...");
        saveAsItem.setMnemonic(KeyEvent.VK_A);
        saveAsItem.addActionListener(ae -> ApplicationWindow.this._canvas.SaveAs());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(ae -> System.exit(0));
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));

        menu.add(newItem);
        menu.add(openItem);
        menu.add(saveItem);
        menu.add(saveAsItem);
        menu.addSeparator();
        menu.add(exitItem);

        return menu;
    }

    // Create the JMenu and JMenu items for the Help menu
    private JMenu _createHelpMenu() {
        JMenu menu = _createMenu("Help", "For Help, click Help Topics on the Help Menu.", KeyEvent.VK_H);

        JMenuItem aboutItem = new JMenuItem("About...");
        aboutItem.addActionListener(ae -> ApplicationWindow.this._showAbout());
        aboutItem.setMnemonic(KeyEvent.VK_A);

        menu.add(aboutItem);

        return menu;
    }

    // Create the JMenu and JMenu items for the Image menu
    private JMenu _createImageMenu() {
        JMenu menu = _createMenu("Image", "For Help, click Help Topics on the Help Menu.", KeyEvent.VK_I);

        JMenuItem flipRotateItem = new JMenuItem("Flip/Rotate...");
        flipRotateItem.addActionListener(ae -> ApplicationWindow.this._showFlipRotate());
        flipRotateItem.setMnemonic(KeyEvent.VK_F);
        flipRotateItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));

        JMenuItem stretchSkewItem = new JMenuItem("Stretch/Skew...");
        stretchSkewItem.addActionListener(ae -> ApplicationWindow.this._showStretchSkew());
        stretchSkewItem.setMnemonic(KeyEvent.VK_S);
        stretchSkewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));

        JMenuItem invertItem = new JMenuItem("Invert Colors");
        //invertItem.addActionListener(ae -> ApplicationWindow.this._canvas.Invert());
        invertItem.setMnemonic(KeyEvent.VK_I);
        invertItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));

        JMenuItem attributeItem = new JMenuItem("Attributes...");
        attributeItem.addActionListener(ae -> ApplicationWindow.this._showAttributes());
        attributeItem.setMnemonic(KeyEvent.VK_A);
        attributeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));

        JMenuItem clearItem = new JMenuItem("Clear Image");
        clearItem.addActionListener(ae -> ApplicationWindow.this._canvas.Reset(true));
        clearItem.setMnemonic(KeyEvent.VK_N);
        clearItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));

        menu.add(flipRotateItem);
        menu.add(stretchSkewItem);
        //menu.add(invertItem);
        menu.add(attributeItem);
        menu.add(clearItem);

        return menu;
    }

    // Create the JMenu and JMenu items for the Options menu
    private JMenu _createOptionsMenu() {
        JMenu menu = _createMenu("Options", "For Help, click Help Topics on the Help Menu.", KeyEvent.VK_O);

        JMenuItem editColorItem = new JMenuItem("Edit Colors...");
        editColorItem.addActionListener(ae -> ApplicationWindow.this._colorBar.Edit());
        editColorItem.setMnemonic(KeyEvent.VK_E);

        JMenuItem getColorItem = new JMenuItem("Get Colors...");
        getColorItem.addActionListener(ae -> ApplicationWindow.this._colorBar.Import());
        getColorItem.setMnemonic(KeyEvent.VK_G);

        JMenuItem saveColorItem = new JMenuItem("Save Colors...");
        saveColorItem.addActionListener(ae -> ApplicationWindow.this._colorBar.Export());
        saveColorItem.setMnemonic(KeyEvent.VK_S);

        JCheckBoxMenuItem drawOpaqueItem = new JCheckBoxMenuItem("Draw Opaque", false);
        drawOpaqueItem.addActionListener(ae -> ApplicationWindow.this._canvas.ToggleOpaque());
        drawOpaqueItem.setMnemonic(KeyEvent.VK_D);

        menu.add(editColorItem);
        menu.add(getColorItem);
        menu.add(saveColorItem);
        menu.add(drawOpaqueItem);

        return menu;
    }

    // Create the JMenu and JMenu items for the View menu
    private JMenu _createViewMenu() {
        JMenu menu = _createMenu("View", "For Help, click Help Topics on the Help Menu.", KeyEvent.VK_V);

        JCheckBoxMenuItem hideToolItem = new JCheckBoxMenuItem("Tool Box", true);
        hideToolItem.addActionListener(ae -> ApplicationWindow.this._toolbar.setVisible(!ApplicationWindow.this._toolbar.isVisible()));
        hideToolItem.setMnemonic(KeyEvent.VK_T);

        JCheckBoxMenuItem hideColorItem = new JCheckBoxMenuItem("Color Box", true);
        hideColorItem.addActionListener(ae -> ApplicationWindow.this._colorBar.setVisible(!ApplicationWindow.this._colorBar.isVisible()));
        hideColorItem.setMnemonic(KeyEvent.VK_C);

        JCheckBoxMenuItem hideStatusItem = new JCheckBoxMenuItem("Status Bar", true);
        //hideColorItem.addActionListener(ae -> ApplicationWindow.this._colorBar.setVisible(!ApplicationWindow.this._colorBar.isVisible()));
        hideStatusItem.setMnemonic(KeyEvent.VK_S);
        hideStatusItem.setEnabled(false);

        JMenu zoomMenu = new JMenu("Zoom");
        zoomMenu.setMnemonic(KeyEvent.VK_S);

        JMenuItem zoomNormalItem = new JMenuItem("Normal Size");
        zoomNormalItem.addActionListener(ae -> ToolSettings.setZoomLevel(ZoomLevel.x1));
        zoomNormalItem.setMnemonic(KeyEvent.VK_N);
        zoomNormalItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.CTRL_MASK));

        JMenuItem zoomLargerItem = new JMenuItem("Large Size");
        zoomLargerItem.addActionListener(ae -> ToolSettings.setZoomLevel(ZoomLevel.x4));
        zoomLargerItem.setMnemonic(KeyEvent.VK_L);
        zoomLargerItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.CTRL_MASK));

        JMenuItem customZoomItem = new JMenuItem("Custom...");
        //customZoomItem.addActionListener(ae -> ToolSettings.setZoomLevel(new CustomZoomWindow().getUserSelection()));
        customZoomItem.setMnemonic(KeyEvent.VK_U);
        customZoomItem.setEnabled(false);

        JCheckBoxMenuItem gridItem = new JCheckBoxMenuItem("Show Grid", false);
        gridItem.addActionListener(ae -> ApplicationWindow.this._canvas.ToggleGridLines());
        gridItem.setMnemonic(KeyEvent.VK_G);
        gridItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));

        JMenuItem thumbnailItem = new JMenuItem("Show Thumbnail");
        //thumbnailItem.addActionListener(ae -> new ThumbnailWindow(ApplicationWindow.this._canvas));
        thumbnailItem.setMnemonic(KeyEvent.VK_H);
        thumbnailItem.setEnabled(false);

        JMenuItem viewBitmapItem = new JMenuItem("View Bitmap");
        //viewBitmapItem.addActionListener(ae -> null);
        viewBitmapItem.setMnemonic(KeyEvent.VK_V);
        viewBitmapItem.setEnabled(false);

        JMenuItem hideTextItem = new JMenuItem("Text Toolbar");
        hideTextItem.addActionListener(ae -> ShowFontDialog());
        hideTextItem.setMnemonic(KeyEvent.VK_E);
        hideTextItem.setEnabled(true);

        zoomMenu.add(zoomNormalItem);
        zoomMenu.add(zoomLargerItem);
        zoomMenu.add(customZoomItem);
        zoomMenu.addSeparator();
        zoomMenu.add(gridItem);
        zoomMenu.add(thumbnailItem);

        menu.add(hideToolItem);
        menu.add(hideColorItem);
        menu.add(hideStatusItem);
        menu.addSeparator();
        menu.add(zoomMenu);
        menu.add(viewBitmapItem);
        menu.add(hideTextItem);

        return menu;
    }

    private void _showAbout() {
        new AboutDialog(this);
    }

    private void _showStretchSkew() {
        StretchSkewDialog dialog = new StretchSkewDialog(this);
        dialog.addPropertyChangeListener("result", evt -> {
            String value = evt.getNewValue().toString();
            if (value.startsWith("C")) {
                return;
            }

            String[] split = value.split("\\|");

            Direction stretchDir;
            if (split[1].equalsIgnoreCase("H")) {
                stretchDir = Direction.Horizontal;
            } else {
                stretchDir = Direction.Vertical;
            }

            Direction skewDir;
            if (split[3].equalsIgnoreCase("H")) {
                skewDir = Direction.Horizontal;
            } else {
                skewDir = Direction.Vertical;
            }

            this._canvas.StretchAndSkew(stretchDir, Double.parseDouble(split[2]), skewDir, Double.parseDouble(split[4]));
        });

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                dialog.setTextValues(new Dimension(100, 100), new Dimension(0, 0));
            }
        });

        dialog.setVisible(true);
    }

    private void _showAttributes() {
        AttributesDialog dialog = new AttributesDialog(this);
        dialog.setSizeValues(this._canvas.getImageSize());
        dialog.addPropertyChangeListener("result", evt -> {
            String value = evt.getNewValue().toString();
            if (value.startsWith("C")) {
                return;
            }

            String[] split = value.split("\\|");

            int width = Integer.parseInt(split[1]);
            int height = Integer.parseInt(split[2]);
            String unit = split[3];

            if (unit.equalsIgnoreCase("I")) {
                this._canvas.Resize(SizeUnit.Inches, width, height);
            } else if (unit.equalsIgnoreCase("C")) {
                this._canvas.Resize(SizeUnit.Cm, width, height);
            } else {
                this._canvas.Resize(SizeUnit.Pels, width, height);
            }
        });

        dialog.setVisible(true);
    }

    private void _showFlipRotate() {
        FlipRotateDialog dialog = new FlipRotateDialog(this);
        dialog.addPropertyChangeListener("result", evt -> {
            String value = evt.getNewValue().toString();
            if (value.startsWith("C")) {
                return;
            }

            String[] split = value.split("\\|");

            int code = Integer.parseInt(split[1]);

            if (code == FlipRotateDialog.FLIP_HORIZONTAL) {
                this._canvas.Flip(Direction.Horizontal);
            } else if (code == FlipRotateDialog.FLIP_VERTICAL) {
                this._canvas.Flip(Direction.Vertical);
            } else if (code == FlipRotateDialog.ROTATE_90) {
                this._canvas.Rotate(RotateAmount.R90);
            } else if (code == FlipRotateDialog.ROTATE_180) {
                this._canvas.Rotate(RotateAmount.R180);
            } else if (code == FlipRotateDialog.ROTATE_270) {
                this._canvas.Rotate(RotateAmount.R270);
            }
        });

        dialog.setVisible(true);
    }

    public static void ShowFontDialog() {
        if (FontDialog.isCreated()) {
            FontDialog.getCurrent().setVisible(true);
            FontDialog.getCurrent().setLocationRelativeTo(null);
        } else {
            new FontDialog().setVisible(true);
        }
    }
}
