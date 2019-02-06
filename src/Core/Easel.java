package Core;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;

public class Easel extends JLayeredPane {
    private DraggableImagePanel _floatingLayer;
    private PaintCanvas _canvas;

    private int _lastX = -1;
    private int _lastY = -1;

    public Easel(PaintCanvas canvas) {
        Dimension canvasSize = canvas.getSize();
        setSize(canvasSize);
        setMinimumSize(canvasSize);
        setMaximumSize(canvasSize);
        setPreferredSize(canvasSize);

        setDoubleBuffered(true);

        setBorder(BorderFactory.createEtchedBorder());

        this._canvas = canvas;
        add(canvas, JLayeredPane.FRAME_CONTENT_LAYER);

        canvas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);

                Dimension canvasSize = e.getComponent().getSize();

                Easel.this.setSize(canvasSize);
                Easel.this.setMinimumSize(canvasSize);
                Easel.this.setMaximumSize(canvasSize);
                Easel.this.setPreferredSize(canvasSize);

                Easel.this.revalidate();
                Easel.this.repaint();

                canvas.ClearTempPaint();
            }
        });
    }

    public boolean CommitEasel() {
        if (this._floatingLayer == null) {
            return false;
        }

        if (!this._canvas.commitImage(this._floatingLayer.getImage(), this._floatingLayer.getCustomBounds())) {
            return false;
        }

        ResetFloatingLayer();
        this._canvas.revalidate();
        this._canvas.repaint();

        return true;
    }

    protected void paintComponent(Graphics g) {
        if (this._floatingLayer == null) {
            return;
        }

        int x = this._floatingLayer.getX();
        int y = this._floatingLayer.getY();

        if (this._lastX < 0 || this._lastX != x || this._lastY < 0 || this._lastY != y) {
            int drawW = Math.min(Math.max(this._canvas.getWidth() - x, 0), this._floatingLayer.getWidth());
            int drawH = Math.min(Math.max(this._canvas.getHeight() - y, 0), this._floatingLayer.getHeight());

            this._floatingLayer.setClip(0, 0, drawW, drawH);
        }

        this._floatingLayer.repaint();
    }

    public boolean HasFloatingImage() {
        return this._floatingLayer != null;
    }

    public Shape GetFloatingRegion() {
        return this._floatingLayer.GetRegion();
    }

    public BufferedImage GetFloatingImage() {
        return this._floatingLayer.GetImage();
    }

    public void AddFloatingImage(BufferedImage newImage, Point location, Shape region) {
        if (this._floatingLayer != null) {
            remove(this._floatingLayer);
            this._floatingLayer = null;
        }

        this._floatingLayer = new DraggableImagePanel(newImage, location, region);

        add(this._floatingLayer, JLayeredPane.DRAG_LAYER);
    }

    public void ResetFloatingLayer() {
        if (this._floatingLayer != null) {
            remove(this._floatingLayer);
            this._floatingLayer = null;
            revalidate();
            repaint();
        }
    }
}
