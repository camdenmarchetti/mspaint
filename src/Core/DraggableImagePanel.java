package Core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class DraggableImagePanel extends JPanel {

    private BufferedImage _image;
    private Point _anchor;

    private Graphics2D _imageGraphics;

    private Dimension _emptySize;
    private Dimension _panelSize;

    private Border _emptyBorder;
    private Border _panelBorder;

    private Shape _region;

    public DraggableImagePanel(BufferedImage source, Point location, Shape region) {
        super();

        int width = source.getWidth();
        int height = source.getHeight();
        this._emptySize = new Dimension(width, height);
        this._panelSize = new Dimension(width + 1, height + 1);

        setSize(this._panelSize);

        this._emptyBorder = BorderFactory.createEmptyBorder();
        this._panelBorder = BorderFactory.createDashedBorder(Color.BLACK, 1, 3, 5, false);

        setBorder(this._panelBorder);

        location.translate(-this.getInsets().left, -this.getInsets().top);
        setLocation(location);

        //setBackground(new Color(0, 0, 0, 1));
        setOpaque(false);
        setDoubleBuffered(true);

        this._image = source;
        this._region = _MoveRegion(region, -region.getBounds().x, -region.getBounds().y);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                DraggableImagePanel.this._anchor = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                DraggableImagePanel.this._anchor = null;
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point location = e.getComponent().getLocation();
                int deltaX = location.x - DraggableImagePanel.this._anchor.x;
                int deltaY = location.y - DraggableImagePanel.this._anchor.y;

                e.translatePoint(deltaX, deltaY);
                DraggableImagePanel.this.setLocation(e.getPoint());
            }
        });
    }

    public BufferedImage getImage() {
        return this._image;
    }

    public void setClip(int x, int y, int width, int height) {
        if (this._imageGraphics != null) {
            this._imageGraphics.setClip(x, y, width, height);
        }
    }

    public Rectangle getCustomBounds() {
        // Default the size and border to get proper bounds
//        setBorder(this._emptyBorder);
//        setSize(this._emptySize);

        Rectangle bounds = getBounds();
        bounds.translate(this.getInsets().left + 1, this.getInsets().top + 1);

        // Reset the size and border
//        setSize(this._panelSize);
//        setBorder(this._panelBorder);

        return bounds;
    }

    private Shape _MoveRegion(Shape region, int dx, int dy) {
        Polygon translation = new Polygon();

        AffineTransform translate = AffineTransform.getTranslateInstance(dx, dy);
        PathIterator regionSegments = region.getPathIterator(translate);

        SEGMENT_LOOP:
        while (!regionSegments.isDone()) {
            float[] coords = new float[6];
            switch (regionSegments.currentSegment(coords)) {
                case PathIterator.SEG_LINETO:
                case PathIterator.SEG_MOVETO:
                    translation.addPoint((int)coords[0], (int)coords[1]);

                    regionSegments.next();
                    break;
                case PathIterator.SEG_CLOSE:
                    break SEGMENT_LOOP;
                default:
                    break;
            }
        }

        if (translation.npoints <= 2) {
            return region;
        }

        int x0 = translation.xpoints[0];
        int y0 = translation.xpoints[0];

        int xN = translation.xpoints[translation.npoints - 1];
        int yN = translation.xpoints[translation.npoints - 1];

        if (x0 != xN || y0 != yN) {
            translation.addPoint(x0, y0);
        }

        return translation;
    }

    public Shape GetRegion() {
        Rectangle bounds = getCustomBounds();
        return _MoveRegion(this._region, bounds.x, bounds.y);
    }

    public BufferedImage GetImage() {
        return this._image;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (this._image == null) {
            return;
        }

        Shape clip = null;
        if (this._imageGraphics != null) {
            clip = this._imageGraphics.getClip();
            this._imageGraphics.dispose();
        }

        this._imageGraphics = (Graphics2D)graphics.create();
        if (this._imageGraphics == null) {
            return;
        }

        Insets padding = getInsets();
        int width = this._image.getWidth();
        int height = this._image.getHeight();

        if (clip != null) {
            this._imageGraphics.setClip(clip);
        }

        this._imageGraphics.drawImage(this._image, padding.left, padding.top, width, height, null);
    }
}
