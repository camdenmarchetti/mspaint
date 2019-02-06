package Core;

import Popups.FontDialog;
import Tools.PaintTool;
import Tools.Pencil;

import Utilities.CursorUtils;
import Utilities.ImageUtils;
import Valids.CursorType;
import Valids.Direction;
import Valids.RotateAmount;
import Valids.SizeUnit;
import Valids.ZoomLevel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import sun.awt.image.MultiResolutionCachedImage;

public class PaintCanvas extends JComponent {

    private static final Dimension _DEFAULT_SIZE_ = new Dimension(648, 488);
    private static final int _PADDING_ = 8;

    // _floatSpace is used to handle floating images (image pastes or temporary lines)
    // _newImage is used when opening a file
    private BufferedImage _paintSpace, _floatSpace, _newImage;

    private Graphics2D _graphics;

    private Color[] _colors = { Color.BLACK, Color.WHITE };
    private final Color _TRANSPARENT = new Color(0, 0, 0, 1);

    private PaintTool _tool;
    private Stack<BufferedImage> _undo, _redo;

    private Easel _easel;

    private final JFileChooser _fileChooser = new JFileChooser();
    private File _saveFile = null;

    private int _prevZoomCheck = 1;
    private boolean _gridEnabled = false;
    private boolean _drawOpaque = false;
    private boolean _canDraw = true;
    private JTextArea _editor;

    public PaintCanvas(ToolSettings settings) {
        this(PaintCanvas._DEFAULT_SIZE_, settings);
    }

    public PaintCanvas(Dimension canvasSize, ToolSettings settings) {
        super();

        setDoubleBuffered(false);
        this.setSize(canvasSize);
        this.setBorder(new ResizableCanvasBorder(this));

        this._tool = new Pencil();
        this._undo = new Stack<>();
        this._redo = new Stack<>();

        CursorUtils.setCurrentCursor(CursorType.Pencil);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!PaintCanvas.this._canDraw) {
                    return;
                }

                if (!PaintCanvas.this._easel.CommitEasel()) {
                    PaintCanvas.this.getTool().MouseClick(PaintCanvas.this, e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!PaintCanvas.this._canDraw) {
                    return;
                }

                if (!PaintCanvas.this._easel.CommitEasel()) {
                    PaintCanvas.this.getTool().MousePress(PaintCanvas.this, e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!PaintCanvas.this._canDraw) {
                    return;
                }

                PaintCanvas.this.getTool().MouseRelease(PaintCanvas.this, e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!PaintCanvas.this._canDraw) {
                    return;
                }

                CursorUtils.applyCursor(getRootPane());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!PaintCanvas.this._canDraw) {
                    return;
                }

                CursorUtils.clearCursor(getRootPane());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!PaintCanvas.this._canDraw) {
                    return;
                }

                PaintCanvas.this.getTool().MouseDrag(PaintCanvas.this, e);
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

        settings.addPropertyChangeListener("zoomLevel", e -> {
            Dimension newSize = PaintCanvas.this.getSize();
            newSize.setSize(newSize.width - _PADDING_, newSize.height - _PADDING_);

            int oldValue = (Integer)e.getOldValue();
            int newValue = (Integer)e.getNewValue();

            if (newValue == PaintCanvas.this._prevZoomCheck) {
                return;
            }

            PaintCanvas.this._prevZoomCheck = newValue;
            double scale = (double)newValue / (double)oldValue;

            newSize.setSize((int)(newSize.width * scale) + _PADDING_, (int)(newSize.height * scale) + _PADDING_);

            // Don't zoom with a floating layer
            if (PaintCanvas.this._easel.HasFloatingImage()) {
                PaintCanvas.this._easel.CommitEasel();
            }

            PaintCanvas.this.setSize(newSize);
            PaintCanvas.this.setMaximumSize(newSize);
            PaintCanvas.this.setMinimumSize(newSize);
            PaintCanvas.this.setPreferredSize(newSize);


            EventQueue.invokeLater(() -> {
                PaintCanvas.this.revalidate();
                PaintCanvas.this.repaint();
            });
        });

        settings.addPropertyChangeListener("zoomLevel", e -> {
            Dimension newSize = PaintCanvas.this.getSize();
            newSize.setSize(newSize.width - _PADDING_, newSize.height - _PADDING_);

            int oldValue = (Integer)e.getOldValue();
            int newValue = (Integer)e.getNewValue();

            if (newValue == PaintCanvas.this._prevZoomCheck) {
                return;
            }

            PaintCanvas.this._prevZoomCheck = newValue;
            double scale = (double)newValue / (double)oldValue;

            newSize.setSize((int)(newSize.width * scale) + _PADDING_, (int)(newSize.height * scale) + _PADDING_);

            // Don't zoom with a floating layer
            if (PaintCanvas.this._easel.HasFloatingImage()) {
                PaintCanvas.this._easel.CommitEasel();
            }

            PaintCanvas.this.setSize(newSize);
            PaintCanvas.this.setMaximumSize(newSize);
            PaintCanvas.this.setMinimumSize(newSize);
            PaintCanvas.this.setPreferredSize(newSize);


            EventQueue.invokeLater(() -> {
                PaintCanvas.this.revalidate();
                PaintCanvas.this.repaint();
            });
        });
    }

    void LinkEasel(Easel easel) {
        this._easel = easel;
    }

    protected void paintComponent(Graphics g) {
        if (this._paintSpace == null || this._newImage != null) {

            int width, height;

            if (this._newImage != null) {
                width = this._newImage.getWidth();
                height = this._newImage.getHeight();
            } else {
                width = getSize().width - _PADDING_;
                height = getSize().height - _PADDING_;
            }

            this._paintSpace = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            this._graphics = (Graphics2D)this._paintSpace.getGraphics();

            // Setup rendering hints (anti-aliasing)
            this._graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            this._graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            this._graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            this._graphics.setClip(4, 4, width, height);

            // Sterilize the surface
            this._graphics.setPaint(Color.WHITE);
            this._graphics.fillRect(0, 0, width, height);

            if (this._newImage != null) {
                this._graphics.drawImage(this._newImage, 0, 0, width, height, null);
                this._newImage = null;
            }

            ClearTempPaint();
        }

        // TODO: Make this more efficient by not scaling the image every draw?
        int zoomLevel = ToolSettings.getZoomLevel();
        if (zoomLevel != 1) {

            // Get the scale size
            int scaleWidth = getSize().width - _PADDING_;
            int scaleHeight = getSize().height - _PADDING_;
            int scaleShift = 8 - 4 * zoomLevel;

            // Set the scale rendering hint
            this._graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            this._graphics.setClip(4, 4, scaleWidth, scaleHeight);

            g.drawImage(this._paintSpace, scaleShift, scaleShift, scaleWidth, scaleHeight, null);

            this._graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        } else {
            // We can paint the image normally
            int width = getSize().width - _PADDING_;
            int height = getSize().height - _PADDING_;

            this._graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            this._graphics.setClip(4, 4, width, height);

            g.drawImage(this._paintSpace, 4, 4, width, height, null);
        }

        if (this._gridEnabled && zoomLevel > 2) {
            g.setColor(Color.BLACK);

            int width = getSize().width - _PADDING_;
            int height = getSize().height - _PADDING_;

            g.setClip(4, 4, width, height);
            int scaleShift = 8 - 4 * zoomLevel;

            for (int col = scaleShift; col < width; col += zoomLevel) {
                g.drawLine(col, scaleShift, col, height);
            }

            for (int row = scaleShift; row < height; row += zoomLevel) {
                g.drawLine(scaleShift, row, width, row);
            }
        }
    }

    PaintTool getTool()
    {
        return this._tool;
    }

    void setTool(PaintTool tool)
    {
        this._tool = tool;
    }

    public Image getImage()
    {
        return this._paintSpace;
    }

    public void setColor(int colorIndex, Color color)
    {
        if (colorIndex < -1) {
            this._colors[(-colorIndex) % 2] = color;
        } else if (colorIndex == -1) {
            this._colors[1] = color;
        } else if (colorIndex > 1) {
            this._colors[colorIndex % 2] = color;
        } else {
            this._colors[colorIndex] = color;
        }
    }

    public BufferedImage setSelection(Shape region, Color ignoreColor) {
        ResetTempPaint();

        if (region == null) {
            return null;
        }

        Rectangle regionBounds = region.getBounds();
        int x = Math.max(4, Math.min(getWidth() - _PADDING_, (int)regionBounds.getX()));
        int y = Math.max(4, Math.min(getHeight() - _PADDING_, (int)regionBounds.getY()));
        int width = (int)regionBounds.getWidth();
        int height = (int)regionBounds.getHeight();

        if (x + width > getWidth() - _PADDING_) {
            width = getWidth() - x - _PADDING_;
        }

        if (y + height > getHeight() - _PADDING_) {
            height = getHeight() - y - _PADDING_;
        }

        if (width <= 0 || height <= 0) {
            return null;
        }

        BufferedImage extraction = this._paintSpace.getSubimage(x, y, width, height);

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        BufferedImage layer = device.getDefaultConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        Graphics2D layerGraphics = layer.createGraphics();

        // Translate the shape (clip region) to be from position (0, 0)
        Shape translation = new Polygon();
        AffineTransform translate = AffineTransform.getTranslateInstance(-regionBounds.x, -regionBounds.y);
        PathIterator regionSegments = region.getPathIterator(translate);

        SEGMENT_LOOP:
        while (!regionSegments.isDone()) {
            float[] coords = new float[6];
            switch (regionSegments.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    ((Polygon) translation).addPoint((int)coords[0], (int)coords[1]);
                    regionSegments.next();
                    break;
                case PathIterator.SEG_CLOSE:
                    break SEGMENT_LOOP;
                default:
                    break;
            }
        }

        // Setup the translated clip area to only draw our selection
        Area userClip = new Area(translation);

        // Set the clip and draw the image
        layerGraphics.setClip(userClip);
        layerGraphics.drawImage(extraction, 0, 0, null);

        // Invert the clip and update the graphics clip region
        userClip.subtract(new Area(regionBounds));
        layerGraphics.setClip(userClip);

        // Remove surrounding color
        layerGraphics.setColor(this._TRANSPARENT);
        layerGraphics.fillRect(0, 0, width, height);

        layerGraphics.setClip(null);

        layerGraphics.dispose();


        // Remove ignoreColor
        if (ignoreColor != null) {
            // Get the data from the image
            WritableRaster layerData = layer.getRaster();
            int red = ignoreColor.getRed();
            int green = ignoreColor.getGreen();
            int blue = ignoreColor.getBlue();

            float[] transparent = this._TRANSPARENT.getComponents(null);

            // Iterate the image pixels (ugh)
            for (int row = 0; row < layer.getHeight(); row++) {
                for (int col = 0; col < layer.getWidth(); col++) {
                    int[] pixel = layerData.getPixel(col, row, (int[])null);

                    // Only work with matches
                    if (pixel[0] == red && pixel[1] == green || pixel[2] == blue) {
                        // Replace the pixel with a transparent one
                        layerData.setPixel(col, row, transparent);
                    }
                }
            }
        }

        return layer;
    }

    public Graphics2D getGraphics2D() { return this._graphics; }

    public Color getColor(int color) {
        Color returnColor;
        if (color < -1) {
            returnColor = this._colors[(-color) % 2];
        } else if (color == -1) {
            returnColor = this._colors[1];
        } else if (color > 1) {
            returnColor = this._colors[color % 2];
        } else {
            returnColor = this._colors[color];
        }

        if (this._drawOpaque && returnColor.getAlpha() != 255) {
            return new Color(returnColor.getRGB());
        }

        return returnColor;
    }

    void Undo()
    {
        if (this._undo.empty()) {
            return;
        }

        BufferedImage newPaintSpace = this._undo.pop();

        this._redo.add(_copyPaintSpace());
        this._paintSpace.setData(newPaintSpace.getData());

        repaint();
    }

    void Open() {
        if (this._undo.size() != 0) {
            if (!_WarnSave()) {
                return;
            }
        }

        this._fileChooser.resetChoosableFileFilters();
        this._fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image", "jpg", "jpeg", "png", "bmp", "gif"));
        this._fileChooser.removeChoosableFileFilter(this._fileChooser.getAcceptAllFileFilter());

        if (this._fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File newFile = this._fileChooser.getSelectedFile();
        if (newFile == null) {
            return;
        }

        try {
            this._newImage = ImageIO.read(newFile);
            this._saveFile = newFile;

            this._undo.clear();
            this._redo.clear();
            ToolSettings.setZoomLevel(ZoomLevel.x1);

            int width = this._newImage.getWidth();
            int height = this._newImage.getHeight();

            setSize(width, height);

            revalidate();
            repaint();
        } catch (IOException ex) {
            // Do nothing
        }
    }

    boolean Save(File saveFile, BufferedImage image, boolean copyTo) {
        if (saveFile != null) {

            if (!saveFile.getName().contains(".")) {
                boolean renamed = saveFile.renameTo(new File(saveFile.getAbsoluteFile() + ".bmp"));
                if (!renamed) {
                    return false;
                }
            }

            String extension = saveFile.getName().substring(saveFile.getName().lastIndexOf('.') + 1).trim().toLowerCase();

            try {
                ImageIO.write(image, extension, saveFile);
                return true;
            } catch (IOException ioError) {
                System.out.println("Uh oh... What should I do?");
            } catch (Exception generic) {
                System.out.println("Something else went wrong.");
            }
        } else {
            if (!copyTo) {
                return SaveAs();
            }
        }

        return false;
    }

    boolean SaveAs() {
        this._fileChooser.showSaveDialog(null);
        this._saveFile = this._fileChooser.getSelectedFile();

        if (this._saveFile != null) {
            return Save(this._saveFile, this._paintSpace, false);
        }

        return false;
    }

    boolean SaveDefault() {
        return Save(this._saveFile, this._paintSpace, false);
    }

    void Redo()
    {
        if (this._redo.empty()) {
            return;
        }

        BufferedImage newPaintSpace = this._redo.pop();

        this._undo.add(_copyPaintSpace());
        this._paintSpace.setData(newPaintSpace.getData());

        repaint();
    }

    public void StorePrePaint() {
        this._undo.add(_copyPaintSpace());
    }

    public void SetupTempPaint() {
        if (this._floatSpace != null) {
            this._floatSpace.setData(_copyPaintSpace().getData());
        } else {
            BufferedImage paintSpaceCopy = _copyPaintSpace();
            this._floatSpace = new BufferedImage(paintSpaceCopy.getWidth(), paintSpaceCopy.getHeight(), BufferedImage.TYPE_INT_ARGB);
            this._floatSpace.setData(paintSpaceCopy.getData());
        }
    }

    public void ResetTempPaint() {
        if (this._floatSpace != null) {
            this._paintSpace.setData(_copyFloatSpace().getData());
            this.repaint();
        }
    }

    public void ClearTempPaint() {
        this._floatSpace = null;
    }

    public void RequestPaint(boolean drawSuccessful) {
        if (drawSuccessful && _canDraw) {
            repaint();

            while (this._undo.size() > 30) {
                this._undo.remove(0);
            }

            while (this._redo.size() > 30) {
                this._redo.remove(0);
            }
        }
    }

    private BufferedImage _copyPaintSpace() {
        ColorModel colorModel = this._paintSpace.getColorModel();
        WritableRaster copiedData = this._paintSpace.copyData(null);
        return new BufferedImage(colorModel, copiedData, colorModel.isAlphaPremultiplied(), null);
    }

    private BufferedImage _copyFloatSpace() {
        ColorModel colorModel = this._floatSpace.getColorModel();
        WritableRaster copiedData = this._floatSpace.copyData(null);
        return new BufferedImage(colorModel, copiedData, colorModel.isAlphaPremultiplied(), null);
    }

    public void setFloatingLayer(BufferedImage floatingLayer, Point location, Shape selection, boolean clear) {
        this._easel.AddFloatingImage(floatingLayer, location, selection);

        if (this._graphics != null) {
            Color startColor = this._graphics.getColor();

            if (clear) {
                this._graphics.setColor(this.getColor(1));
                this._graphics.fill(selection);
                this._graphics.setColor(startColor);
            }
        }
    }

    public boolean commitImage(BufferedImage image, Rectangle posSize) {
        if (this._graphics == null) {
            return false;
        }

        this._graphics.drawImage(image, posSize.x - 5, posSize.y - 5, posSize.width, posSize.height, null);
        return true;
    }

    public void Reset(boolean clearOnly) {
        if (this._undo.size() > 0) {
            if (!_WarnSave()) {
                return;
            }
        }

        this._undo.clear();
        this._redo.clear();
        ToolSettings.setZoomLevel(ZoomLevel.x1);

        if (!clearOnly) {
            this.setSize(PaintCanvas._DEFAULT_SIZE_);
            this.setMaximumSize(PaintCanvas._DEFAULT_SIZE_);
            this.setMinimumSize(PaintCanvas._DEFAULT_SIZE_);
            this.setPreferredSize(PaintCanvas._DEFAULT_SIZE_);

            this._saveFile = null;

            this._colors[0] = Color.BLACK;
            this._colors[1] = Color.WHITE;

            ColorBar.RepaintNewColors(this._colors[0], 0);
            ColorBar.RepaintNewColors(this._colors[1], 1);
        }

        this._newImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);

        revalidate();
        repaint();
    }

    public void Paste() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable == null || !transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            return;
        }

        try
        {
            try {
                _paste((BufferedImage)transferable.getTransferData(DataFlavor.imageFlavor));
            } catch (Exception innerEx) {
                MultiResolutionCachedImage cache = (MultiResolutionCachedImage)transferable.getTransferData(DataFlavor.imageFlavor);
                Image img = cache.getResolutionVariants().get(0);
                _paste((BufferedImage)img);
            }
        }
        catch (Exception e)
        { /* Do nothing */ }
    }

    public void PasteFrom() {
        this._fileChooser.resetChoosableFileFilters();
        this._fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image", "jpg", "jpeg", "png", "bmp", "gif"));
        this._fileChooser.removeChoosableFileFilter(this._fileChooser.getAcceptAllFileFilter());

        if (this._fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File newFile = this._fileChooser.getSelectedFile();
        if (newFile == null) {
            return;
        }

        try {
            _paste(ImageIO.read(newFile));
        } catch (IOException ex) { /* Do nothing */ }
    }

    public void _paste(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();

        if (this._easel.HasFloatingImage()) {
            this._easel.CommitEasel();
        }

        if (width > this.getWidth() - _PADDING_ || height > this.getHeight() - _PADDING_) {
            int resize = _PromptResize();
            if (resize == JOptionPane.CANCEL_OPTION) {
                return;
            }

            if (resize == JOptionPane.YES_OPTION) {
                Dimension newSize = new Dimension(width, height);

                this.setSize(newSize);
                this.setMaximumSize(newSize);
                this.setMinimumSize(newSize);
                this.setPreferredSize(newSize);
            }
        }

        setFloatingLayer(source, new Point(4, 4), new Rectangle2D.Double(0, 0, width, height), false);
    }

    public void CopySelection() {
        if (this._easel.HasFloatingImage()) {
            try {
                new ClipboardInstance(this._easel.GetFloatingImage());
            } catch (Exception e) { /* Do nothing */ }
        }
    }

    public void CopySelectionTo() {
        if (this._easel.HasFloatingImage()) {
            this._fileChooser.showSaveDialog(this);
            this.Save(this._fileChooser.getSelectedFile(), this._easel.GetFloatingImage(), true);
        }
    }

    public void CutSelection() {
        if (this._easel.HasFloatingImage()) {
            CopySelection();
            ClearSelection();
        }
    }

    public void ClearSelection() {
        if (this._easel.HasFloatingImage()) {
            if (this._graphics != null) {
                Shape fillRegion = this._easel.GetFloatingRegion();
                this._easel.ResetFloatingLayer();

                if (this._graphics != null) {
                    Color graphicsColor = this._graphics.getColor();
                    this._graphics.setColor(getColor(1));
                    this._graphics.fill(fillRegion);
                    this._graphics.setColor(graphicsColor);
                }

                repaint();
                revalidate();
            }
        }
    }

    public void SelectAll() {
        Rectangle2D region = new Rectangle2D.Double(4, 4, this.getWidth() - _PADDING_, this.getHeight() - _PADDING_);

        if (this._easel.HasFloatingImage()) {
            this._easel.CommitEasel();
        }

        setFloatingLayer(setSelection(region, null), new Point(4, 4), region, true);
    }


    private boolean _WarnSave() {
        String text = "Save changes to " + (this._saveFile != null ? this._saveFile.getName() : "untitled") + "?";
        JOptionPane warnSave = new JOptionPane(text, JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);

        JDialog dialog = warnSave.createDialog(this, "Paint");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                warnSave.setValue(JOptionPane.CANCEL_OPTION);
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
        Object userDecision = warnSave.getValue();

        if (userDecision != null) {
            int decisionIndex = (Integer)userDecision;
            if (decisionIndex == JOptionPane.YES_OPTION) {
                return Save(this._saveFile, this._paintSpace, false);
            }

            if (decisionIndex == JOptionPane.CANCEL_OPTION) {
                return false;
            }

            return true;
        }

        return false;
    }

    private int _PromptResize() {
        String text = "The image in the clipboard is larger than the bitmap.\nWould you like the bitmap enlarged?";
        JOptionPane warnSave = new JOptionPane(text, JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);

        JDialog dialog = warnSave.createDialog(this, "Paint");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                warnSave.setValue(JOptionPane.CANCEL_OPTION);
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
        Object userDecision = warnSave.getValue();

        if (userDecision != null) {
            return (Integer)userDecision;
        }

        return JOptionPane.CANCEL_OPTION;
    }

    public void ToggleGridLines() {
        this._gridEnabled = !this._gridEnabled;

        repaint();
    }

    public void ToggleOpaque() {
        this._drawOpaque = !this._drawOpaque;
    }

    public void StretchAndSkew(Direction stretchDir, double stretchAmt, Direction skewDir, double skewAmt) {
        BufferedImage newImage = null;

        if (((int)stretchAmt) != 100) {
            double sx = stretchDir == Direction.Horizontal ? stretchAmt / 100.0 : 1;
            double sy = stretchDir == Direction.Vertical ? stretchAmt / 100.0 : 1;

            newImage = ImageUtils.ScaleImage(this._paintSpace, sx, sy);
        }

        if (((int)skewAmt) != 0 && (int)Math.abs(skewAmt) != 90) {
            if (newImage != null) {
                newImage = ImageUtils.SkewImage(newImage, skewDir, skewAmt);
            } else {
                newImage = ImageUtils.SkewImage(this._paintSpace, skewDir, skewAmt);
            }
        }

        if (newImage != null) {
            this._newImage = newImage;
            int width = this._newImage.getWidth();
            int height = this._newImage.getHeight();

            setSize(width + _PADDING_, height + _PADDING_);
            revalidate();
            repaint();
        }
    }

    public void Resize(SizeUnit units, int width, int height) {
        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();

        if (units != SizeUnit.Pels && defaultToolkit != null) {
            int dpi = defaultToolkit.getScreenResolution();

            if (units == SizeUnit.Inches) {
                // Convert from inches to pixels
                width *= dpi;
                height *= dpi;
            } else if (units == SizeUnit.Cm) {
                // Convert from centimeters to pixels (2.54 cm/inch)
                width = (int)((width * dpi) / 2.54);
                height = (int)((height * dpi) / 2.54);
            }
        }

        if (width < 1) {
            width = 1;
        }

        if (height < 1) {
            height = 1;
        }

        // Resize the canvas
        int currentWidth = this._paintSpace.getWidth();
        int currentHeight = this._paintSpace.getHeight();

        int copyWidth = width > currentWidth ? currentWidth : width;
        int copyHeight = height > currentHeight ? currentHeight : height;

        Raster currentData = this._paintSpace.getData(new Rectangle(copyWidth, copyHeight));

        this._newImage = new BufferedImage(width, height, this._paintSpace.getType());
        this._newImage.setData(currentData);

        this.setSize(width + _PADDING_, height + _PADDING_);
        revalidate();
        repaint();
    }

    public Dimension getImageSize() {
        return new Dimension(this._paintSpace.getWidth(), this._paintSpace.getHeight());
    }

    public void Flip(Direction flipDir) {
        this._newImage = ImageUtils.FlipImage(this._paintSpace, flipDir);
        revalidate();
        repaint();
    }

    public void Rotate(RotateAmount rotAmount) {
        this._newImage = ImageUtils.RotateImage(this._paintSpace, rotAmount);
        revalidate();
        repaint();
    }

    public void AllowDraw() {
        _canDraw = true;
    }

    public void ForbidDraw() {
        _canDraw = false;
    }

    public void drawText() {
        if (this._editor == null) {
            return;
        }

        Rectangle bounds = this._editor.getBounds();
        Font drawFont = this._editor.getFont();
        String text = this._editor.getText();

        List<String> lines = new ArrayList<>();

        int totalLines = this._editor.getLineCount();
        for (int line = 0; line < totalLines; line++) {
            try {
                int start = this._editor.getLineStartOffset(line);
                int end = this._editor.getLineEndOffset(line);

                lines.add(text.substring(start, end));

            } catch (Exception e) { }
        }

        _commitText(bounds, drawFont, lines);

        this._editor.setVisible(false);
        remove(this._editor);
        this._editor = null;

    }

    private void _commitText(Rectangle bounds, Font drawFont, List<String> lines) {
        if (this._graphics == null || bounds.width <= 0 || bounds.height <= 0 || lines.isEmpty()) {
            return;
        }

        this._graphics.setFont(drawFont);
        //FontRenderContext renderContext = this._graphics.getFontRenderContext();

        int prevFontHeights = 0;
        for (int row = 0; row < lines.size(); row++) {


            String line = lines.get(row);
            //GlyphVector fontBounds = drawFont.createGlyphVector(renderContext, line);

            prevFontHeights += this._graphics.getFontMetrics(drawFont).getLineMetrics(line, this._graphics).getHeight();
            this._graphics.drawString(line, bounds.x, bounds.y + prevFontHeights);
        }

        EventQueue.invokeLater(() -> {
            this.revalidate();
            this.repaint();
        });
    }

    public void setupEditor(Rectangle2D shape) {
        this._editor = new JTextArea();

        shape.setRect(shape.getX() + 4, shape.getY() + 4, shape.getWidth(), shape.getHeight());

        this._editor.setBounds(shape.getBounds());
        this._editor.setBackground(new Color(0, 0, 0, 1));
        this._editor.setOpaque(false);
        this._editor.setLineWrap(true);
        this._editor.setWrapStyleWord(true);
        this._editor.setFont(FontDialog.GetFont());

        this._editor.setBorder(BorderFactory.createDashedBorder(Color.BLUE));

        this._editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                _editor.setFont(FontDialog.GetFont());
                super.keyTyped(e);
            }
        });

        add(this._editor);
        this._editor.setVisible(true);
    }
}
